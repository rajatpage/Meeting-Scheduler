package meetingScheduler;

import javax.swing.Action;

import jade.content.AgentAction;

/*
 * This is an action on Users.java for UserOntology
 */
public class Meet implements AgentAction{

	private Meeting item;

	public Meeting getItem() {
		return item;
	}

	public void setItem(Meeting item) {
		this.item = item;
	}
	
}
