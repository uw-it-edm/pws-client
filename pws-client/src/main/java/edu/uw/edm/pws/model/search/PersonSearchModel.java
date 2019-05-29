package edu.uw.edm.pws.model.search;

import lombok.Builder;
import lombok.Getter;

/**
 * @author Maxime Deravet Date: 2019-02-15
 */
@Builder
@Getter
public class PersonSearchModel {
    private String address;
    private String changedSinceDate;
    private String department;
    private String developmentID;
    private boolean eduPersonAffiliationAffiliate;
    private boolean eduPersonAffiliationAlum;
    private boolean eduPersonAffiliationEmployee;
    private boolean eduPersonAffiliationFaculty;
    private boolean eduPersonAffiliationMember;
    private boolean eduPersonAffiliationStaff;
    private boolean eduPersonAffiliationStudent;
    private EmployeeAffiliationStateFilter employeeAffiliationStateFilter;
    private boolean includeOnlyActiveEmployee;
    private String email;
    private String employeeID;
    private String firstName;
    private String homeDepartment;
    private String lastName;
    private String mailStop;
    private String phoneNumber;
    private String studentNumber;
    private String studentSystemKey;
    private String title;
    private String uWNetID;
    private String uWRegID;


    @Builder.Default
    private int pageStart = 1;
    @Builder.Default
    private int pageSize = 10;
}
