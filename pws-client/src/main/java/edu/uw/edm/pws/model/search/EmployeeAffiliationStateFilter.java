package edu.uw.edm.pws.model.search;

/**
 * @author Maxime Deravet Date: 2019-05-22
 */
public enum EmployeeAffiliationStateFilter {
    current("current"), prior("prior"), all("prior,current");

    private final String employeeAffiliationStateValue;

    EmployeeAffiliationStateFilter(String employeeAffiliationStateValue) {
        this.employeeAffiliationStateValue = employeeAffiliationStateValue;
    }

    public String getEmployeeAffiliationStateValue() {
        return employeeAffiliationStateValue;
    }
}
