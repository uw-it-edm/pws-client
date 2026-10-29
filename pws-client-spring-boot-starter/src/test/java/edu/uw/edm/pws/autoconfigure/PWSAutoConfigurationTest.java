package edu.uw.edm.pws.autoconfigure;

import static org.assertj.core.api.Assertions.assertThat;

import edu.uw.edm.pws.PersonWebServiceClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.restclient.autoconfigure.RestTemplateAutoConfiguration;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

public class PWSAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner =
            new ApplicationContextRunner()
                    .withConfiguration(
                            AutoConfigurations.of(
                                    RestTemplateAutoConfiguration.class, PWSAutoConfiguration.class));

    @Test
    public void registersPersonWebServiceClient() {
        contextRunner.run(context -> assertThat(context).hasSingleBean(PersonWebServiceClient.class));
    }
}
