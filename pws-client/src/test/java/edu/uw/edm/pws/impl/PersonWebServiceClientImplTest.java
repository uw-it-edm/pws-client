package edu.uw.edm.pws.impl;

import org.hamcrest.core.SubstringMatcher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

import edu.uw.edm.pws.exception.BadSearchRequestException;
import edu.uw.edm.pws.exception.NoSuchPersonException;
import edu.uw.edm.pws.exception.PWSException;
import edu.uw.edm.pws.model.Person;
import edu.uw.edm.pws.model.search.PersonSearchModel;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.startsWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

/**
 * @author Maxime Deravet Date: 10/25/17
 */
@RunWith(SpringRunner.class)
public class PersonWebServiceClientImplTest {

    private MockRestServiceServer server;


    private PersonWebServiceClientImpl pws;


    @Before
    public void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.bindTo(restTemplate).build();

        pws = new PersonWebServiceClientImpl(restTemplate, "http://gws.com/");

    }

    @Test(expected = PWSException.class)
    public void getPersonExceptionTest() throws PWSException {
        this.server.expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full")).andRespond(withStatus(HttpStatus.BAD_REQUEST));
        pws.getPersonByRegId("my-reg-id");

    }

    @Test(expected = NoSuchPersonException.class)
    public void noSuchPersonPersonExceptionTest() throws PWSException {
        this.server.expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full")).andRespond(withStatus(HttpStatus.NOT_FOUND));
        pws.getPersonByRegId("my-reg-id");

    }


    @Test
    public void getPersonTest() throws PWSException {
        this.server.expect(requestTo("http://gws.com/identity/v2/person/my-reg-id/full")).andRespond(withSuccess(TEST_RESPONSE_GET, MediaType.APPLICATION_JSON));
        final Person personByRegId = pws.getPersonByRegId("my-reg-id");

        assertThat(personByRegId.getDisplayName(), is(equalTo("BART SIMPSON")));
        assertThat(personByRegId.getPersonAffiliations().getAlumPersonAffiliations().getDevelopmentId(), is(equalTo("9999980050")));
        assertThat(personByRegId.getPersonAffiliations().getEmployeePersonAffiliation().getEmployeeId(), is(equalTo("000210480")));
        assertThat(personByRegId.getPersonAffiliations().getStudentPersonAffiliation().getStudentNumber(), is(equalTo("3090370")));

    }

    @Test(expected = BadSearchRequestException.class)
    public void badSearchRequestExceptionTest() throws PWSException {
        this.server.expect(requestTo(startsWith("http://gws.com/identity/v2/person")))
                .andRespond(withStatus(HttpStatus.BAD_REQUEST));
        pws.searchPerson(PersonSearchModel.builder().build());

    }


    @Test
    public void searchPersonTest() throws PWSException {

        final PersonSearchModel searchModel = PersonSearchModel.builder()
                .lastName("SIMPS*")
                .pageSize(20)
                .build();

        this.server.expect(requestTo(startsWith("http://gws.com/identity/v2/person")))
                .andExpect(queryParam("verbose", "true"))
                .andExpect(queryParam("last_name", "SIMPS*"))
                .andExpect(queryParam("page_size", "20"))
                .andExpect(queryParam("page_start", "1"))

                .andRespond(withSuccess(TEST_SEARCHRESPONSE_GET, MediaType.APPLICATION_JSON));
        pws.searchPerson(searchModel);

    }

    private static final String TEST_SEARCHRESPONSE_GET = "{\n" +
            "    \"Current\": {\n" +
            "        \"Address\": null,\n" +
            "        \"ChangedSinceDate\": null,\n" +
            "        \"Department\": null,\n" +
            "        \"DevelopmentID\": null,\n" +
            "        \"EduPersonAffiliationAffiliate\": false,\n" +
            "        \"EduPersonAffiliationAlum\": false,\n" +
            "        \"EduPersonAffiliationEmployee\": false,\n" +
            "        \"EduPersonAffiliationFaculty\": false,\n" +
            "        \"EduPersonAffiliationMember\": false,\n" +
            "        \"EduPersonAffiliationStaff\": false,\n" +
            "        \"EduPersonAffiliationStudent\": false,\n" +
            "        \"Email\": null,\n" +
            "        \"EmployeeID\": null,\n" +
            "        \"FirstName\": null,\n" +
            "        \"HomeDepartment\": null,\n" +
            "        \"Href\": \"/identity/v2/person.xhtml?uwregid=&uwnetid=&employee_id=&student_number=&student_system_key=&development_id=&last_name=SIMPS*&first_name=&edupersonaffiliation_student=&edupersonaffiliation_staff=&edupersonaffiliation_faculty=&edupersonaffiliation_employee=false&edupersonaffiliation_member=&edupersonaffiliation_alum=&edupersonaffiliation_affiliate=&changed_since_date=&phone_number=&mail_stop=&home_dept=&department=&address=&title=&email=&verbose=true&page_size=10&page_start=1\",\n" +
            "        \"LastName\": \"SIMPS*\",\n" +
            "        \"MailStop\": null,\n" +
            "        \"PageSize\": \"10\",\n" +
            "        \"PageStart\": \"1\",\n" +
            "        \"PhoneNumber\": null,\n" +
            "        \"StudentNumber\": null,\n" +
            "        \"StudentSystemKey\": null,\n" +
            "        \"Title\": null,\n" +
            "        \"UWNetID\": null,\n" +
            "        \"UWRegID\": null,\n" +
            "        \"Verbose\": true\n" +
            "    },\n" +
            "    \"Next\": null,\n" +
            "    \"PageSize\": \"10\",\n" +
            "    \"PageStart\": \"1\",\n" +
            "    \"Persons\": [\n" +
            "        {\n" +
            "            \"DisplayName\": \"BART SIMPSON\",\n" +
            "            \"EduPersonAffiliations\": [\n" +
            "                \"member\",\n" +
            "                \"student\",\n" +
            "                \"alum\",\n" +
            "                \"staff\",\n" +
            "                \"employee\"\n" +
            "            ],\n" +
            "            \"IsTestEntity\": true,\n" +
            "            \"PreferredFirstName\": null,\n" +
            "            \"PreferredMiddleName\": null,\n" +
            "            \"PreferredSurname\": null,\n" +
            "            \"PriorUWNetIDs\": [],\n" +
            "            \"PriorUWRegIDs\": [\n" +
            "                \"C18452A204E54F8E8F1DAE1E72B679B2\"\n" +
            "            ],\n" +
            "            \"RegisteredFirstMiddleName\": \"BART\",\n" +
            "            \"RegisteredName\": \"BART SIMPSON\",\n" +
            "            \"RegisteredSurname\": \"SIMPSON\",\n" +
            "            \"RepositoryTimeStamp\": \"1/3/2017 9:29:12 AM\",\n" +
            "            \"UIDNumber\": null,\n" +
            "            \"UWNetID\": null,\n" +
            "            \"UWRegID\": \"ADA12DA10F7649B2A8861B14633F0A0A\",\n" +
            "            \"WhitepagesPublish\": false,\n" +
            "            \"PersonAffiliations\": {\n" +
            "                \"AlumPersonAffiliation\": {\n" +
            "                    \"DevelopmentID\": \"9999980050\"\n" +
            "                },\n" +
            "                \"EmployeePersonAffiliation\": {\n" +
            "                    \"EmployeeID\": \"000210480\",\n" +
            "                    \"EmployeeWhitePages\": {\n" +
            "                        \"Addresses\": [],\n" +
            "                        \"EmailAddresses\": [],\n" +
            "                        \"Faxes\": [],\n" +
            "                        \"Name\": null,\n" +
            "                        \"Phones\": [],\n" +
            "                        \"Positions\": [],\n" +
            "                        \"PublishInDirectory\": null,\n" +
            "                        \"TouchDials\": [],\n" +
            "                        \"VoiceMails\": []\n" +
            "                    },\n" +
            "                    \"HomeDepartment\": \"C&C TEST BUDGET\",\n" +
            "                    \"MailStop\": null\n" +
            "                },\n" +
            "                \"StudentPersonAffiliation\": {\n" +
            "                    \"StudentNumber\": \"3090370\",\n" +
            "                    \"StudentSystemKey\": \"990100370\",\n" +
            "                    \"StudentWhitePages\": {\n" +
            "                        \"Class\": null,\n" +
            "                        \"Departments\": [],\n" +
            "                        \"Email\": null,\n" +
            "                        \"Name\": null,\n" +
            "                        \"Phone\": null,\n" +
            "                        \"PublishInDirectory\": null\n" +
            "                    }\n" +
            "                }\n" +
            "            }\n" +
            "        }\n" +
            "    ],\n" +
            "    \"Previous\": null,\n" +
            "    \"TotalCount\": 1\n" +
            "}";

    private static final String TEST_RESPONSE_GET = "{\n" +
            "    \"DisplayName\": \"BART SIMPSON\",\n" +
            "    \"EduPersonAffiliations\": [\"member\",\"student\",\"alum\",\"staff\",\"employee\"],\n" +
            "    \"IsTestEntity\": true,\n" +
            "    \"PreferredFirstName\": null,\n" +
            "    \"PreferredMiddleName\": null,\n" +
            "    \"PreferredSurname\": null,\n" +
            "    \"PriorUWNetIDs\": [ ],\n" +
            "    \"PriorUWRegIDs\": [\"C18452A204E54F8E8F1DAE1E72B679B2\"],\n" +
            "    \"RegisteredFirstMiddleName\": \"BART\",\n" +
            "    \"RegisteredName\": \"BART SIMPSON\",\n" +
            "    \"RegisteredSurname\": \"SIMPSON\",\n" +
            "    \"RepositoryTimeStamp\": \"1/3/2017 9:29:12 AM\",\n" +
            "    \"UIDNumber\": null,\n" +
            "    \"UWNetID\": null,\n" +
            "    \"UWRegID\": \"ADA12DA10F7649B2A8861B14633F0A0A\",\n" +
            "    \"WhitepagesPublish\": false,\n" +
            "    \"PersonAffiliations\": {\n" +
            "        \"AlumPersonAffiliation\": {\n" +
            "            \"DevelopmentID\": \"9999980050\"\n" +
            "        },\n" +
            "        \"EmployeePersonAffiliation\": {\n" +
            "            \"EmployeeID\": \"000210480\",\n" +
            "            \"EmployeeWhitePages\": {\n" +
            "                \"Addresses\": [ ],\n" +
            "                \"EmailAddresses\": [ ],\n" +
            "                \"Faxes\": [ ],\n" +
            "                \"Name\": null,\n" +
            "                \"Phones\": [ ],\n" +
            "                \"Positions\": [ ],\n" +
            "                \"PublishInDirectory\": null,\n" +
            "                \"TouchDials\": [ ],\n" +
            "                \"VoiceMails\": [ ]\n" +
            "            },\n" +
            "            \"HomeDepartment\": \"C&C TEST BUDGET\",\n" +
            "            \"MailStop\": null\n" +
            "        },\n" +
            "        \"StudentPersonAffiliation\": {\n" +
            "            \"StudentNumber\": \"3090370\",\n" +
            "            \"StudentSystemKey\": \"990100370\",\n" +
            "            \"StudentWhitePages\": {\n" +
            "                \"Class\": null,\n" +
            "                \"Departments\": [ ],\n" +
            "                \"Email\": null,\n" +
            "                \"Name\": null,\n" +
            "                \"Phone\": null,\n" +
            "                \"PublishInDirectory\": null\n" +
            "            }\n" +
            "        }\n" +
            "    }\n" +
            "}";
}