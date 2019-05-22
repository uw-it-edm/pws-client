package edu.uw.edm.pws.model.search;

/**
 * @author Maxime Deravet Date: 2019-05-22
 */
public enum EmployeeFilter {
    activeOnly("true"), inactiveOnly("false"), all("true,false");

    private final String employeeIsActiveValue;

    EmployeeFilter(String employeeIsActiveValue) {
        this.employeeIsActiveValue = employeeIsActiveValue;
    }

    public String getEmployeeIsActiveValue() {
        return employeeIsActiveValue;
    }
}
