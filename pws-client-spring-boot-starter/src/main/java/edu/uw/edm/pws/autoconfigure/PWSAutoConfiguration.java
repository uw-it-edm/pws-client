package edu.uw.edm.pws.autoconfigure;

import edu.uw.edm.pws.PersonWebServiceClient;
import edu.uw.edm.pws.autoconfigure.security.KeyManagerCabinet;
import edu.uw.edm.pws.impl.PersonWebServiceClientImpl;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
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

/**
 * @author Maxime Deravet Date: 10/24/17
 */
@Configuration
@ConditionalOnClass(
    value = {
      PersonWebServiceClient.class,
      RestTemplateBuilder.class,
    })
@AutoConfigureAfter(RestTemplateAutoConfiguration.class)
@EnableConfigurationProperties(PWSProperties.class)
public class PWSAutoConfiguration {
  private final PWSProperties pwsProperties;

  public PWSAutoConfiguration(PWSProperties pwsProperties) {
    this.pwsProperties = pwsProperties;
  }

  @Bean
  @ConditionalOnMissingBean
  public PersonWebServiceClient personWebServiceClient(
      @Qualifier("pws-client") RestTemplate restTemplate) {

    return new PersonWebServiceClientImpl(restTemplate, pwsProperties.getUrl());
  }

  private CloseableHttpClient httpClient(final KeyManagerCabinet cabinet)
      throws KeyManagementException, NoSuchAlgorithmException {
    TrustManager[] trustManagers = cabinet.getTrustManagers();

    SSLContext context = SSLContext.getInstance("TLS");
    context.init(cabinet.getKeyManagers(), trustManagers, new SecureRandom());

    SSLConnectionSocketFactory sslSocketFactory =
        new SSLConnectionSocketFactory(context, new DefaultHostnameVerifier());

    return HttpClients.custom()
        .setConnectionManager(
            PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslSocketFactory)
                .build())
        .build();
  }

  @Bean
  @Qualifier("pws-client")
  public KeyManagerCabinet keyManagerCabinet(PWSProperties PWSProperties) throws Exception {
    return new KeyManagerCabinet.Builder(
            PWSProperties.getKeystoreLocation(), PWSProperties.getKeystorePassword())
        .build();
  }

  @Bean
  @Qualifier("pws-client")
  public HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory(
      @Qualifier("pws-client") KeyManagerCabinet keyManagerCabinet)
      throws NoSuchAlgorithmException, KeyManagementException {

    final CloseableHttpClient httpClient = httpClient(keyManagerCabinet);
    return new HttpComponentsClientHttpRequestFactory(httpClient);
  }

  @Bean
  @Qualifier("pws-client")
  public RestTemplate restTemplate(
      RestTemplateBuilder restTemplateBuilder,
      @Qualifier("pws-client")
          HttpComponentsClientHttpRequestFactory httpComponentsClientHttpRequestFactory) {
    return restTemplateBuilder.requestFactory(() -> httpComponentsClientHttpRequestFactory).build();
  }
}
