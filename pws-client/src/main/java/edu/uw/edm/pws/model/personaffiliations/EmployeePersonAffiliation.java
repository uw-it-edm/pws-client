package edu.uw.edm.pws.model.personaffiliations;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * @author Maxime Deravet Date: 2019-02-14
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EmployeePersonAffiliation {

    @JsonProperty("EmployeeID")
    private String employeeId;

    @JsonProperty("HomeDepartment")
    private String homeDepartment;

    @JsonProperty("MailStop")
    private String mailStop;

    @JsonProperty("EmployeeWhitePages")
    private EmployeeWhitePages employeeWhitePages;
}
