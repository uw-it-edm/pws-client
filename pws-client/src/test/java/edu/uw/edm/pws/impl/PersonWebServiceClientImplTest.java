package edu.uw.edm.pws.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.jayway.jsonpath.Configuration;
import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import com.jayway.jsonpath.Option;
import edu.uw.edm.pws.exception.BadPersonRequestException;
import edu.uw.edm.pws.exception.NoSuchPersonException;
import edu.uw.edm.pws.exception.PWSException;
import edu.uw.edm.pws.exception.UnknownPersonRequestException;
import edu.uw.edm.pws.model.Person;
import edu.uw.edm.pws.model.search.PersonSearchModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import tools.jackson.databind.json.JsonMapper;

/**
 * @author Maxime Deravet Date: 10/25/17
 */
public class PersonWebServiceClientImplTest {

    private MockRestServiceServer server;

    private PersonWebServiceClientImpl pws;

    @BeforeEach
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        List<HttpMessageConverter<?>> messageConverters = new ArrayList<>();
        JacksonJsonHttpMessageConverter converter =
                new JacksonJsonHttpMessageConverter(JsonMapper.builder().build());
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        messageConverters.add(converter);
        restTemplate.setMessageConverters(messageConverters);
        server = MockRestServiceServer.bindTo(restTemplate).build();

        pws = new PersonWebServiceClientImpl(restTemplate, "http://gws.com/");
    }

    @Test
    public void getPersonExceptionTest() {
        this.server
                .expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full.json"))
                .andRespond(
                        withStatus(HttpStatus.SERVICE_UNAVAILABLE)
                                .body(
                                        """
                                        {
                                            "InnerExceptions": null,
                                            "StackTrace": null,
                                            "StatusCode": "503",
                                            "StatusDescription": "'bla'"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON));
        PWSException ex = assertThrows(PWSException.class, () -> pws.getPersonByRegId("my-reg-id"));
        assertThat(ex.getPwsError().getStatusCode(), is(equalTo("503")));
    }

    @Test
    public void noSuchPersonPersonExceptionTest() {
        this.server
                .expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full.json"))
                .andRespond(
                        withStatus(HttpStatus.NOT_FOUND)
                                .body(
                                        """
                                        {
                                            "InnerExceptions": null,
                                            "StackTrace": null,
                                            "StatusCode": "404",
                                            "StatusDescription": "No person found."
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON));
        NoSuchPersonException ex =
                assertThrows(NoSuchPersonException.class, () -> pws.getPersonByRegId("my-reg-id"));
        assertThat(ex.getPwsError().getStatusCode(), is(equalTo("404")));
    }

    @Test
    public void fieldsArentDuplicatedTest() throws PWSException {
        this.server
                .expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full.json"))
                .andRespond(withSuccess(TEST_RESPONSE_GET, MediaType.APPLICATION_JSON));
        final Person personByRegId = pws.getPersonByRegId("my-reg-id");

        final String json = JsonMapper.builder().build().writeValueAsString(personByRegId);
        Configuration jsonPathConf =
                Configuration.defaultConfiguration().addOptions(Option.DEFAULT_PATH_LEAF_TO_NULL);

        final DocumentContext parse = JsonPath.using(jsonPathConf).parse(json);
        assertThat(parse.read("$.uwnetID"), nullValue());
        assertThat(parse.read("$.UWNetID"), is(equalTo("bsimps")));
    }

    @Test
    public void getPersonTest() throws PWSException {
        this.server
                .expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full.json"))
                .andRespond(withSuccess(TEST_RESPONSE_GET, MediaType.APPLICATION_JSON));
        final Person personByRegId = pws.getPersonByRegId("my-reg-id");

        assertThat(personByRegId.getDisplayName(), is(equalTo("BART SIMPSON")));
        assertThat(
                personByRegId.getPersonAffiliations().getAlumPersonAffiliations().getDevelopmentId(),
                is(equalTo("9999980050")));
        assertThat(
                personByRegId.getPersonAffiliations().getEmployeePersonAffiliation().getEmployeeId(),
                is(equalTo("000210480")));
        assertThat(
                personByRegId.getPersonAffiliations().getStudentPersonAffiliation().getStudentNumber(),
                is(equalTo("3090370")));
    }

    @Test
    public void badSearchRequestExceptionTest() {
        this.server
                .expect(requestTo(startsWith("http://gws.com/identity/v2/person")))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                                .body(
                                        """
                                        {
                                            "InnerExceptions": null,
                                            "StackTrace": null,
                                            "StatusCode": "400",
                                            "StatusDescription": "Invalid studentNumber"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON));

        BadPersonRequestException ex =
                assertThrows(BadPersonRequestException.class, () -> pws.getPersonByRegId("my-reg-id"));
        assertThat(ex.getPwsError().getStatusCode(), is(equalTo("400")));
        assertThat(ex.getPwsError().getStatusDescription(), is(equalTo("Invalid studentNumber")));
    }

    @Test
    public void searchPersonTest() throws PWSException {
        final PersonSearchModel searchModel =
                PersonSearchModel.builder().lastName("SIMPS*").pageSize(20).build();

        this.server
                .expect(requestTo(startsWith("http://gws.com/identity/v2/person")))
                .andExpect(queryParam("verbose", "true"))
                .andExpect(queryParam("last_name", "SIMPS*"))
                .andExpect(queryParam("page_size", "20"))
                .andExpect(queryParam("page_start", "1"))
                .andRespond(withSuccess(TEST_SEARCHRESPONSE_GET, MediaType.APPLICATION_JSON));
        pws.searchPerson(searchModel);
    }

    @Test
    public void searchPersonUriAndHeadersTest() throws PWSException {
        final PersonSearchModel searchModel =
                PersonSearchModel.builder()
                        .firstName("Bart Jr")
                        .lastName("SIMPS*")
                        .email("bsimps@uw.edu")
                        .pageSize(20)
                        .build();

        this.server
                .expect(
                        requestTo(
                                "http://gws.com/identity/v2/person.json?verbose=true&page_size=20&page_start=1"
                                        + "&email=bsimps@uw.edu&first_name=Bart%20Jr&last_name=SIMPS*"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE))
                .andRespond(withSuccess(TEST_SEARCHRESPONSE_GET, MediaType.APPLICATION_JSON));
        pws.searchPerson(searchModel);
        this.server.verify();
    }

    @Test
    public void searchPersonBadRequestTest() {
        this.server
                .expect(requestTo(startsWith("http://gws.com/identity/v2/person.json")))
                .andExpect(queryParam("student_number", "3090370"))
                .andRespond(
                        withStatus(HttpStatus.BAD_REQUEST)
                                .body(
                                        """
                                        {
                                            "InnerExceptions": null,
                                            "StackTrace": null,
                                            "StatusCode": "400",
                                            "StatusDescription": "Invalid studentNumber"
                                        }
                                        """)
                                .contentType(MediaType.APPLICATION_JSON));

        BadPersonRequestException ex =
                assertThrows(
                        BadPersonRequestException.class,
                        () ->
                                pws.searchPerson(
                                        PersonSearchModel.builder().studentNumber("3090370").build()));
        assertThat(ex.getPwsError().getStatusCode(), is(equalTo("400")));
        assertThat(ex.getPwsError().getStatusDescription(), is(equalTo("Invalid studentNumber")));
    }

    // Proxies/gateways in front of PWS may return HTML or an empty body instead of PWS error JSON.
    // The client must still throw UnknownPersonRequestException (despite the name, it signals an
    // unparseable error response, not a missing person) rather than leak a JacksonException.
    @ParameterizedTest
    @ValueSource(strings = {"<html>Bad Gateway</html>", ""})
    public void unparseableErrorBodyTest(String body) {
        this.server
                .expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full.json"))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY).body(body));

        UnknownPersonRequestException ex =
                assertThrows(
                        UnknownPersonRequestException.class, () -> pws.getPersonByRegId("my-reg-id"));
        assertThat(ex.getMessage(), is(equalTo("502 - " + body)));
        assertThat(ex.getCause(), instanceOf(HttpStatusCodeException.class));
        assertThat(
                ((HttpStatusCodeException) ex.getCause()).getStatusCode().value(), is(equalTo(502)));
    }

    private static final String TEST_SEARCHRESPONSE_GET =
            """
            {
                "Current": {
                    "Address": null,
                    "ChangedSinceDate": null,
                    "Department": null,
                    "DevelopmentID": null,
                    "EduPersonAffiliationAffiliate": false,
                    "EduPersonAffiliationAlum": false,
                    "EduPersonAffiliationEmployee": false,
                    "EduPersonAffiliationFaculty": false,
                    "EduPersonAffiliationMember": false,
                    "EduPersonAffiliationStaff": false,
                    "EduPersonAffiliationStudent": false,
                    "Email": null,
                    "EmployeeID": null,
                    "FirstName": null,
                    "HomeDepartment": null,
                    "Href": "/identity/v2/person.xhtml?uwregid=&uwnetid=&employee_id=&student_number=&student_system_key=&development_id=&last_name=SIMPS*&first_name=&edupersonaffiliation_student=&edupersonaffiliation_staff=&edupersonaffiliation_faculty=&edupersonaffiliation_employee=false&edupersonaffiliation_member=&edupersonaffiliation_alum=&edupersonaffiliation_affiliate=&changed_since_date=&phone_number=&mail_stop=&home_dept=&department=&address=&title=&email=&verbose=true&page_size=10&page_start=1",
                    "LastName": "SIMPS*",
                    "MailStop": null,
                    "PageSize": "10",
                    "PageStart": "1",
                    "PhoneNumber": null,
                    "StudentNumber": null,
                    "StudentSystemKey": null,
                    "Title": null,
                    "UWNetID": null,
                    "UWRegID": null,
                    "Verbose": true
                },
                "Next": null,
                "PageSize": "10",
                "PageStart": "1",
                "Persons": [
                    {
                        "DisplayName": "BART SIMPSON",
                        "EduPersonAffiliations": [
                            "member",
                            "student",
                            "alum",
                            "staff",
                            "employee"
                        ],
                        "IsTestEntity": true,
                        "PreferredFirstName": null,
                        "PreferredMiddleName": null,
                        "PreferredSurname": null,
                        "PriorUWNetIDs": [],
                        "PriorUWRegIDs": [
                            "C18452A204E54F8E8F1DAE1E72B679B2"
                        ],
                        "RegisteredFirstMiddleName": "BART",
                        "RegisteredName": "BART SIMPSON",
                        "RegisteredSurname": "SIMPSON",
                        "RepositoryTimeStamp": "1/3/2017 9:29:12 AM",
                        "UIDNumber": null,
                        "UWNetID": null,
                        "UWRegID": "ADA12DA10F7649B2A8861B14633F0A0A",
                        "WhitepagesPublish": false,
                        "PersonAffiliations": {
                            "AlumPersonAffiliation": {
                                "DevelopmentID": "9999980050"
                            },
                            "EmployeePersonAffiliation": {
                                "EmployeeID": "000210480",
                                "EmployeeWhitePages": {
                                    "Addresses": [],
                                    "EmailAddresses": [],
                                    "Faxes": [],
                                    "Name": null,
                                    "Phones": [],
                                    "Positions": [],
                                    "PublishInDirectory": null,
                                    "TouchDials": [],
                                    "VoiceMails": []
                                },
                                "HomeDepartment": "C&C TEST BUDGET",
                                "MailStop": null
                            },
                            "StudentPersonAffiliation": {
                                "StudentNumber": "3090370",
                                "StudentSystemKey": "990100370",
                                "StudentWhitePages": {
                                    "Class": null,
                                    "Departments": [],
                                    "Email": null,
                                    "Name": null,
                                    "Phone": null,
                                    "PublishInDirectory": null
                                }
                            }
                        }
                    }
                ],
                "Previous": null,
                "TotalCount": 1
            }
            """;

    private static final String TEST_RESPONSE_GET =
            """
            {
                "DisplayName": "BART SIMPSON",
                "EduPersonAffiliations": ["member","student","alum","staff","employee"],
                "IsTestEntity": true,
                "PreferredFirstName": null,
                "PreferredMiddleName": null,
                "PreferredSurname": null,
                "PriorUWNetIDs": [ ],
                "PriorUWRegIDs": ["C18452A204E54F8E8F1DAE1E72B679B2"],
                "RegisteredFirstMiddleName": "BART",
                "RegisteredName": "BART SIMPSON",
                "RegisteredSurname": "SIMPSON",
                "RepositoryTimeStamp": "1/3/2017 9:29:12 AM",
                "UIDNumber": null,
                "UWNetID": "bsimps",
                "UWRegID": "ADA12DA10F7649B2A8861B14633F0A0A",
                "WhitepagesPublish": false,
                "PersonAffiliations": {
                    "AlumPersonAffiliation": {
                        "DevelopmentID": "9999980050"
                    },
                    "EmployeePersonAffiliation": {
                        "EmployeeID": "000210480",
                        "EmployeeWhitePages": {
                            "Addresses": [ ],
                            "EmailAddresses": [ ],
                            "Faxes": [ ],
                            "Name": null,
                            "Phones": [ ],
                            "Positions": [ ],
                            "PublishInDirectory": null,
                            "TouchDials": [ ],
                            "VoiceMails": [ ]
                        },
                        "HomeDepartment": "C&C TEST BUDGET",
                        "MailStop": null
                    },
                    "StudentPersonAffiliation": {
                        "StudentNumber": "3090370",
                        "StudentSystemKey": "990100370",
                        "StudentWhitePages": {
                            "Class": null,
                            "Departments": [ ],
                            "Email": null,
                            "Name": null,
                            "Phone": null,
                            "PublishInDirectory": null
                        }
                    }
                }
            }
            """;
}
