package meetingScheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Set;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;

// this class is responsible to handle tasks related to admin Users like adding removing and grouping users
public class AdministratorAgent extends Agent {

	AdministratorGui administratorGui;

	private Hashtable<String, String> nameCatalogue, groupCatalouge;
	private HashMap userData = new HashMap();

	public void setup() {
		System.out.println("Hello. My name is "+getLocalName());

		String matchMakerName = "MatchMaker" ;
		String meetingManagerName = "MeetingManager";
		AgentContainer c = getContainerController();
		try {
			AgentController a = ((ContainerController) c).createNewAgent( matchMakerName, "meetingScheduler.MatchMaker", null );
			a.start();
			AgentController b = ((ContainerController) c).createNewAgent( meetingManagerName, "meetingScheduler.MeetingManager", null );
			b.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		nameCatalogue = new Hashtable<String, String>();
		groupCatalouge = new Hashtable<String, String>();

		// Create and show the GUI 
		administratorGui = new AdministratorGuiImpl();
		administratorGui.setAgent(this);
		administratorGui.show();

		//Registering Admin agent to yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Register-User");
		sd.setName("JADE-Register-User");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}

	}

	public boolean addUers(String empId, String empName, String empGroup) {
		if(userData!=null && userData.containsKey(empId)) {
			return false;
		}
		else {
			addBehaviour(new SystemManager(this, empId, empName, empGroup, "addUser"));			
			return true;			
		}
	}

	public void removeUers(String empId, String empName, String empGroup) {
		addBehaviour(new SystemManager(this, empId, empName, empGroup, "removeUser"));	
	}

	protected void takeDown() {
		// Dispose the GUI if it is there
		if (administratorGui != null) {
			administratorGui.dispose();
		}
		// Printout a dismissal message
		System.out.println("Admin-agent "+getAID().getName()+"terminated.");
	}
	public void deleteAgent() {
		ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
		System.out.println("before ttry in userInterface");
		aclmsg.addReceiver(new AID("MatchMaker", AID.ISLOCALNAME));
		aclmsg.setContent("deleteAgent");
		send(aclmsg);

		doDelete();
	}

	public Hashtable<String, String> getNameCatalouge(){
		return nameCatalogue;
	}

	public Hashtable<String, String> getGroupCatalouge(){
		return groupCatalouge;
	}

	// ********	SystemManager Class to add/delete users to database***************************************************************************

	private class SystemManager extends OneShotBehaviour {

		private String empId, empName, empGroup, action;

		public SystemManager(Agent a, String eId, String eName, String eGroup, String act) {	
			empId = eId;
			empName = eName;
			empGroup = eGroup;
			action = act;
		}

		@Override
		public void action() {

			String[] groupNames = new String[25];
			userData.put(empId, this);
			ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
			try {
				aclmsg.addReceiver(new AID("MatchMaker", AID.ISLOCALNAME));
				aclmsg.setConversationId(action);
				if(action.equals("removeUser")) {
					aclmsg.setContentObject(empId);
				}else {
					aclmsg.setContentObject(userData);
					//					System.out.println("adding to meeting manager");
					ACLMessage ac = new ACLMessage(ACLMessage.INFORM);
					ac.addReceiver(new AID("MeetingManager", AID.ISLOCALNAME));
					ac.setConversationId("addingUsersToDB");
					ac.setContent(empId);
					send(ac);
				}

			} catch (IOException e) {
				System.out.println("in IOException");
				userData.remove(empId);
				administratorGui.removeLastEntry();
				administratorGui.notifyUserWithDialogueBox(" Not connected to the server!");
			} catch (NullPointerException e){
				System.out.println("in null pointer");
				userData.remove(empId);
				administratorGui.removeLastEntry();
				administratorGui.notifyUserWithDialogueBox(" Not connected to the server!");
				e.printStackTrace();
			}	catch (Exception e) {
				System.out.println("in exception");
				userData.remove(empId);
				administratorGui.removeLastEntry();
				administratorGui.notifyUserWithDialogueBox(" Not connected to the server!");
				e.printStackTrace();
			}
			send(aclmsg);
			if(action.equals("removeUser")) {
				userData.remove(empId);
			}
		}


	}

	// ********	UserManager Class to makes changes to users ***************************************************************************

	private class UserManager extends TickerBehaviour {

		public UserManager(Agent a, long period) {
			super(a, period);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub

		}

	}

}
