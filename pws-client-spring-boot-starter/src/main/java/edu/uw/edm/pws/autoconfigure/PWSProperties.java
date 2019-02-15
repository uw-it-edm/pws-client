package edu.uw.edm.pws.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Maxime Deravet Date: 10/24/17
 */
@ConfigurationProperties(prefix = "pws")
@Getter
@Setter
public class PWSProperties {
    /**
     * Group webservices URL
     */
    private String url = "https://ws.admin.washington.edu/";
    private String keystoreLocation;
    private String keystorePassword;

}
