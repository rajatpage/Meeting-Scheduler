package meetingScheduler;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jade.content.Concept;

/*
 * This is the concept of MeetingOntology
 */
public class Meeting implements Concept{

	private String empId;

	private String meetingTitle;
	
	private String location;
	
	private Date meetingStartTime;
	
	private Date meetingEndTime;
	
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public Date getMeetingStartTime() {
		return meetingStartTime;
	}
	public void setMeetingStartTime(Date meetingStartTime) {
		this.meetingStartTime = meetingStartTime;
	}
	public Date getMeetingEndTime() {
		return meetingEndTime;
	}
	public void setMeetingEndTime(Date meetingEndTime) {
		this.meetingEndTime = meetingEndTime;
	}


//	private Set scheduledMeetings;
//	
//	public Set getScheduledMeetings() {
//		return scheduledMeetings;
//	}
//	public void setScheduledMeetings(Set meetingDate) {
//		scheduledMeetings = meetingDate;
//	}
//	private Set attendeeList;
	
//	private List<Map<String,Integer>> meetingList;
//	
//	public List<Map<String, Integer>> getMeetingList() {
//		return meetingList;
//	}
//	public void setMeetingList(List<Map<String, Integer>> meetingList) {
//		this.meetingList = meetingList;
//	}
//	public Set getAttendeeList() {
//		return attendeeList;
//	}
//	public void setAttendeeList(Set attendeeList) {
//		this.attendeeList = attendeeList;
//	}
	public String getMeetingTitle() {
		return meetingTitle;
	}
	public void setMeetingTitle(String meetingTitle) {
		this.meetingTitle = meetingTitle;
	}
	public String getEmpId() {
		return empId;
	}
	public void setEmpId(String empId) {
		this.empId = empId;
	}
	
}
