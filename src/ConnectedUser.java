package meetingScheduler;

import java.util.Date;

import jade.content.Predicate;
/*
 * this is the predicate for user concept of UserOntology
 */
public class ConnectedUser implements Predicate {

	private Meeting item;
	
	private Date meetingStartTime;
	
	private Date meetingEndTime;

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

	public Meeting getItem() {
		return item;
	}

	public void setItem(Meeting item) {
		this.item = item;
	}
	
}
