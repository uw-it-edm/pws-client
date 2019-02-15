package edu.uw.edm.pws.autoconfigure;

import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;

import edu.uw.edm.pws.PersonWebServiceClient;
import edu.uw.edm.pws.autoconfigure.security.KeyManagerCabinet;
import edu.uw.edm.pws.impl.PersonWebServiceClientImpl;

/**
 * @author Maxime Deravet Date: 10/24/17
 */
@Configuration
@ConditionalOnClass(value = {PersonWebServiceClient.class, RestTemplateBuilder.class,})
@AutoConfigureAfter(RestTemplateAutoConfiguration.class)
@EnableConfigurationProperties(PWSProperties.class)
public class PWSAutoConfiguration {
    private final PWSProperties pwsProperties;

    public PWSAutoConfiguration(PWSProperties pwsProperties) {
        this.pwsProperties = pwsProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public PersonWebServiceClient personWebServiceClient(@Qualifier("pws-client") RestTemplate restTemplate) {

        return new PersonWebServiceClientImpl(restTemplate, pwsProperties.getUrl());
    }

    private CloseableHttpClient httpClient(final PoolingHttpClientConnectionManager connectionManager) {

        RequestConfig.Builder requestConfigBuilder = RequestConfig.custom().setExpectContinueEnabled(true);

        return HttpClients.custom().setConnectionManager(connectionManager)
                .setDefaultRequestConfig(requestConfigBuilder.build()).build();
    }

    private PoolingHttpClientConnectionManager connectionManager(final KeyManagerCabinet cabinet) throws KeyManagementException, NoSuchAlgorithmException {
        TrustManager[] trustManagers = cabinet.getTrustManagers();


        SSLContext context = SSLContext.getInstance(SSLConnectionSocketFactory.TLS);
        context.init(cabinet.getKeyManagers(), trustManagers, new SecureRandom());

        HostnameVerifier hostnameVerifier = new DefaultHostnameVerifier();

        ConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(context, hostnameVerifier);

        final Registry<ConnectionSocketFactory> sfr = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.getSocketFactory())
                .register("https", socketFactory)
                .build();

        return new PoolingHttpClientConnectionManager(sfr);
    }

    @Bean
    @Qualifier("pws-client")
    public KeyManagerCabinet keyManagerCabinet(PWSProperties PWSProperties) throws Exception {
        return new KeyManagerCabinet.Builder(PWSProperties.getKeystoreLocation(), PWSProperties.getKeystorePassword()).build();
    }


    @Bean
    @Qualifier("pws-client")
    public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(@Qualifier("pws-client") KeyManagerCabinet keyManagerCabinet) throws NoSuchAlgorithmException, KeyManagementException {

        final PoolingHttpClientConnectionManager connectionManager = connectionManager(keyManagerCabinet);

        final HttpClient httpClient = httpClient(connectionManager);
        return new HttpComponentsClientHttpRequestFactory(httpClient);
    }

    @Bean
    @Qualifier("pws-client")
    public RestTemplate restTemplate(RestTemplateBuilder restTemplateBuilder, @Qualifier("pws-client") HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
        return restTemplateBuilder.requestFactory(() -> httpComponentsClientHttpRequestFactory).build();
    }
}
