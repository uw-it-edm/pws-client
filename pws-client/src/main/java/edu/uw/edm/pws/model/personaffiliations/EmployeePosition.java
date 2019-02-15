package edu.uw.edm.pws.model.personaffiliations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Maxime Deravet Date: 2019-02-15
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeePosition {
    @JsonProperty("EWPDept")
    private String eWPDept;

    @JsonProperty("EWPTitle")
    private String eWPTitle;

    @JsonProperty("Primary")
    private Boolean primary;

}
