package edu.uw.edm.pws.model.personaffiliations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * @author Maxime Deravet Date: 2019-02-14
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeeWhitePages {

    @JsonProperty("Addresses")
    private List<String> addresses;

    @JsonProperty("EmailAddresses")
    private List<String> emailAddresses;

    @JsonProperty("Faxes")
    private List<String> faxes;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Phones")
    private List<String> phones;

    @JsonProperty("Positions")
    private List<EmployeePosition> positions;

    @JsonProperty("PublishInDirectory")
    private Boolean publishInDirectory;

    @JsonProperty("TouchDials")
    private List<String> touchDials;

    @JsonProperty("VoiceMails")
    private List<String> voiceMails;

}
