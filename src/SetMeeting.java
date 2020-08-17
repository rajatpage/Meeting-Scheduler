package meetingScheduler;

import jade.content.AgentAction;

/*
 * Action class on meeting.java for MeetingOntology
 */
public class SetMeeting implements AgentAction {

	private Users item;

	public Users getItem() {
		return item;
	}

	public void setItem(Users item) {
		this.item = item;
	}
}
