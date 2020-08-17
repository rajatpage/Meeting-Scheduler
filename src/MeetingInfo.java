package meetingScheduler;

import java.util.HashSet;

import jade.content.Concept;

/*
 * After changing the architecture, it is used to send user information between UserInterfaceAgent and MeetingManager
 */
public class MeetingInfo implements Concept{

	private Meeting meeting;
	
	private HashSet<String> attendees;
	
	public Meeting getMeeting() {
		return meeting;
	}

	public void setMeeting(Meeting meeting) {
		this.meeting = meeting;
	}

	public HashSet<String> getAttendees() {
		return attendees;
	}

	public void setAttendees(HashSet<String> attendees) {
		this.attendees = attendees;
	}
}
