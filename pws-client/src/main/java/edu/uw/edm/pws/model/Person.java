package edu.uw.edm.pws.model;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Date;
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

public class Person {

    @JsonProperty("DisplayName")
    protected String displayName;

    @JsonProperty("EduPersonAffiliations")
    protected List<String> eduPersonAffiliations;

    @JsonProperty("IsTestEntity")
    protected Boolean isTestEntity;

    @JsonProperty("PreferredFirstName")
    protected String preferredFirstName;

    @JsonProperty("PreferredMiddleName")
    protected String preferredMiddleName;

    @JsonProperty("PreferredSurname")
    protected String preferredSurname;

    @JsonProperty("PriorUWNetIDs")
    protected List<String> priorUWNetIDs;

    @JsonProperty("PriorUWRegIDs")
    protected List<String> priorUWRegIDs;

    @JsonProperty("RegisteredFirstMiddleName")
    protected String registeredFirstMiddleName;

    @JsonProperty("RegisteredName")
    protected String registeredName;

    @JsonProperty("RegisteredSurname")
    protected String registeredSurname;

    @JsonProperty("RepositoryTimeStamp")
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "dd/MM/yyyy hh:mm:ss a")
    protected Date repositoryTimeStamp;

    @JsonProperty("UIDNumber")
    protected String uIDNumber;

    @JsonProperty("UWNetID")
    protected String uWNetID;

    @JsonProperty("UWRegID")
    protected String uWRegID;

    @JsonProperty("WhitepagesPublish")
    protected Boolean whitepagesPublish;

    @JsonProperty("PersonAffiliations")
    private PersonAffiliations personAffiliations;
}
