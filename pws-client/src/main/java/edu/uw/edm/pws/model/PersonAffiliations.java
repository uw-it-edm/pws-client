package edu.uw.edm.pws.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import edu.uw.edm.pws.model.personaffiliations.AlumPersonAffiliations;
import edu.uw.edm.pws.model.personaffiliations.EmployeePersonAffiliation;
import edu.uw.edm.pws.model.personaffiliations.StudentPersonAffiliation;
import lombok.Data;

/**
 * @author Maxime Deravet Date: 2019-02-14
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonAffiliations {

    @JsonProperty("AlumPersonAffiliation")
    private AlumPersonAffiliations alumPersonAffiliations;

    @JsonProperty("EmployeePersonAffiliation")
    private EmployeePersonAffiliation employeePersonAffiliation;

    @JsonProperty("StudentPersonAffiliation")
    private StudentPersonAffiliation studentPersonAffiliation;
}
