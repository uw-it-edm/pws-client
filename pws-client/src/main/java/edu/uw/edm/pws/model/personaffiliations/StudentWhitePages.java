package edu.uw.edm.pws.model.personaffiliations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import lombok.Data;

/**
 * @author Maxime Deravet Date: 2019-02-14
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY,
        getterVisibility = JsonAutoDetect.Visibility.NONE,
        setterVisibility = JsonAutoDetect.Visibility.NONE,
        isGetterVisibility = JsonAutoDetect.Visibility.NONE)
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
