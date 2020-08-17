package meetingScheduler;

import jade.content.Concept;

/*
 * User concept for UserOntology
 */
public class Users implements Concept {

private String empId;

private String empName;

private String empGroup;

public String getEmpGroup() {
	return empGroup;
}

public void setEmpGroup(String empGroup) {
	this.empGroup = empGroup;
}

public String getempId() {
  return empId;
}

public void setempId(String empId) {
  this.empId = empId;
}

public String getempName() {
  return empName;
}

public void setempName(String empName) {
  this.empName = empName;
}

}
