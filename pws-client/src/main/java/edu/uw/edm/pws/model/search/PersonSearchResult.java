package edu.uw.edm.pws.model.search;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

import edu.uw.edm.pws.model.Person;
import lombok.Data;

/**
 * @author Maxime Deravet Date: 2019-02-15
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PersonSearchResult {
    @JsonProperty("Persons")
    private List<Person> persons;

    @JsonProperty("PageSize")
    private int pageSize;

    @JsonProperty("PageStart")
    private int pageStart;

    @JsonProperty("TotalCount")
    private int totalCount;

}
