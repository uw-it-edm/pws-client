package edu.uw.edm.pws;

import edu.uw.edm.pws.exception.NoSuchPersonException;
import edu.uw.edm.pws.exception.PWSException;
import edu.uw.edm.pws.model.Person;
import edu.uw.edm.pws.model.search.PersonSearchModel;
import edu.uw.edm.pws.model.search.PersonSearchResult;

/**
 * @author Maxime Deravet
 */
public interface PersonWebServiceClient {


    Person getPersonByRegId(String regId) throws PWSException;

    PersonSearchResult searchPerson(PersonSearchModel personSearchModel) throws PWSException;

}
