package edu.uw.edm.pws.model.personaffiliations;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * @author Maxime Deravet Date: 2019-02-14
 */
public class StudentWhitePages {

    @JsonProperty("Class")
    /**
     * Class in PWS
     */
    private String classValue;

    @JsonProperty("Departments")
    private List<String> departments;

    @JsonProperty("Email")
    private String email;

    @JsonProperty("Name")
    private String name;

    @JsonProperty("Phone")
    private String phone;

    @JsonProperty("PublishInDirectory")
    private String publishInDirectory;

}
