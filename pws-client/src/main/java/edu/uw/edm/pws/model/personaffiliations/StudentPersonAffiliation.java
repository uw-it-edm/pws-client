package edu.uw.edm.pws.model.personaffiliations;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

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
public class StudentPersonAffiliation {


    @JsonProperty("StudentNumber")
    private String studentNumber;

    @JsonProperty("StudentSystemKey")
    private String studentSystemKey;

    @JsonProperty("StudentWhitePages")
    private StudentWhitePages studentWhitePages;

}
