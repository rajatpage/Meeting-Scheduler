package meetingScheduler;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jade.content.ContentElementList;
import jade.content.ContentManager;
import jade.content.lang.Codec;
import jade.content.lang.Codec.CodecException;
import jade.content.lang.sl.SLCodec;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetInitiator;
import jade.proto.ContractNetResponder;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

/*
 * Agent communicating with MeetingManager and MatchMaker and passing values to set up a meeting with selected users
 */
public class UserInterfaceAgent extends Agent{

	private Codec codec = new SLCodec();
	private Ontology ontology = UserOntology.getInstance();
	private UserGui userGUI;

	private Vector attendeeAgents = new Vector();
	private boolean userFlag;
	private HashMap userData = new HashMap();
	Set<String> keys = new HashSet<String>();

	Set<String> attendeeList = new HashSet<>();

	public void setup() {
		UserInterfaceAgent agent = this;
		addBehaviour(new User(this));

		addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action(){
				ACLMessage msg = receive();

				if(msg!=null){
					if("addingUsers".equals(msg.getConversationId())) {
						try {
							
							userData = ((HashMap)msg.getContentObject());
							if(userData!= null) {
							keys = userData.keySet();
							} else {
								System.out.println("userdata is null");
							}
						} catch (UnreadableException e) {
												e.printStackTrace();
						}
					}
					if(msg.getPerformative() == ACLMessage.ACCEPT_PROPOSAL ) {
						System.out.println("in accepting proposal");
						if("proposalOutput".equals(msg.getConversationId())) {
			System.out.println("in proposalOutput");
							userGUI.notifyUser("Meeting has been accepted");
							Meeting mee = new Meeting();
							try {
								mee = (Meeting)msg.getContentObject();
							} catch (UnreadableException e) {
								e.printStackTrace();
							}
							
							userGUI.updateAgenda(mee);
							
						}
						if("proposeMeeting".equals(msg.getConversationId())) {
							System.out.println("in proposeMeeting");
							HashMap<String, Meeting> mapToInform = new HashMap<String, Meeting>();
							try {
								mapToInform = (HashMap<String, Meeting>) msg.getContentObject();
							} catch (UnreadableException e) {
								e.printStackTrace();
							}
							Set<String> keys = (Set<String>) mapToInform.keySet();
							for (String key: keys) {
							userGUI.updateHalfAgenda(key, mapToInform.get(key));
							userGUI.notifyUser("New meeting has been set by "+key);
							}
						
						}

					}
					if(msg.getPerformative() == ACLMessage.REJECT_PROPOSAL ) {
						if(	"proposalOutput".equals(msg.getConversationId())) {
							userGUI.notifyUser("Sorry! Meeting can't be set today!:(");							
						}
					} 
					if(msg.getPerformative() == ACLMessage.AGREE) {
						if(msg.getConversationId().equals("addAgendaResult")) {
							Meeting meeting = new Meeting();
							try {
								meeting = (Meeting)msg.getContentObject();
							} catch (UnreadableException e) {
								e.printStackTrace();
							}
							userGUI.updateAgenda(meeting);
						}
					}
					if(msg.getPerformative() == ACLMessage.CANCEL) {
						userGUI.notifyUserWithDialogueBox("Already meeting set at this time");
					}
				}
			}       
		});

		addBehaviour(new TickerBehaviour(this, 60000) {
			protected void onTick() {
				// Update the list of seller agents
				DFAgentDescription template = new DFAgentDescription();
				ServiceDescription sd = new ServiceDescription();
				sd.setType("user-interface");
				template.addServices(sd);
				try {
					DFAgentDescription[] result = DFService.search(myAgent,
							template);
				}
				catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		} );

		addBehaviour(new TickerBehaviour(this, 3000) {
			protected void onTick() {
				boolean flag = false;
				if(keys!=null && !keys.isEmpty()) {
					for(String key:keys) {
						if(getLocalName().equals(key)) {
							// Create and show the GUI 
							userGUI = new UserGuiImpl();
							userGUI.setAgent(agent);
							userGUI.show();
							flag= true;
							userFlag = true;
						}
					}
					removeBehaviour(this);
				}
				if(!flag) {
					System.out.println("User not in Database "+getLocalName());
					System.out.println("Current users are "+keys);
				}
			}
		} );

	}

	public void addAgenda(String title, String location, Date meetingStartTime, Date meetingEndTime) {
		Meeting meeting = new Meeting();
		meeting.setMeetingTitle(title);
		meeting.setLocation(location);
		meeting.setMeetingStartTime(meetingStartTime);
		meeting.setMeetingEndTime(meetingEndTime);
		ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
		aclmsg.addReceiver(new AID("MeetingManager", AID.ISLOCALNAME));
		try {
			aclmsg.setPerformative(ACLMessage.REQUEST);
			aclmsg.setConversationId("addAgenda");
			aclmsg.setContentObject(meeting);
			send(aclmsg);

		}  catch (IOException e) {
			e.printStackTrace();
			userGUI.notifyUserWithDialogueBox("Not able to connect to server!");
		}
	}

	public void deleteAgent() {
		ACLMessage aclmsg = new ACLMessage(ACLMessage.INFORM);
		aclmsg.addReceiver(new AID("MatchMaker", AID.ISLOCALNAME));
		aclmsg.setContent("delete");
		send(aclmsg);

		doDelete();
	}

	public HashMap askUser() {

		return userData;
	}

	public void proposeMeeting(HashSet<String> attendees,String title, String loc, Date meetingST, Date meetingET) {
		System.out.println("proposing meeting"+attendees);
		Meeting meeting = new Meeting();
		MeetingInfo meetingInfo = new MeetingInfo();
		meeting.setMeetingTitle(title);
		meeting.setLocation(loc);
		meeting.setMeetingStartTime(meetingST);
		meeting.setMeetingEndTime(meetingET);
		attendeeList = attendees;
		meetingInfo.setAttendees(attendees);
		meetingInfo.setMeeting(meeting);
		proposeMeeting(meetingInfo);

	}
	public void proposeMeeting(MeetingInfo meetingInfo) {


		ACLMessage aclmsg = new ACLMessage(ACLMessage.PROPOSE);
		aclmsg.addReceiver(new AID("MeetingManager", AID.ISLOCALNAME));
		try {
			aclmsg.setContentObject(meetingInfo);
		} catch (IOException e) {
			e.printStackTrace();
		}
		aclmsg.setPerformative(ACLMessage.PROPOSE);
		aclmsg.setConversationId("proposeMeeting");
		send(aclmsg);
	}
	public class User extends CyclicBehaviour {

		public User(UserInterfaceAgent userInterfaceAgent) {

		}

		@Override
		public void action() {
			ACLMessage aclmsg = new ACLMessage(ACLMessage.REQUEST);
			aclmsg.addReceiver(new AID("MatchMaker", AID.ISLOCALNAME));
			aclmsg.setContent("askForUsers");
			send(aclmsg);
			if (userFlag) {
				System.out.println("removing behaviour");
				removeBehaviour(this);
			}
		}
	}

	private class ManagerClass extends TickerBehaviour {

		private String empId, location;
		private Date meetingStartTime, meetingEndTime;
		private long deadline, initTime, deltaT;

		private ManagerClass(UserInterfaceAgent a, String eId, String loc, Date meetingST, Date meetingET) {
			super(a, 3000); // tick every 1/2 minute
			System.out.println("calling manager class");
			empId = eId;
			location = loc;
			meetingStartTime = meetingST;
			meetingEndTime = meetingET;
		}

		public void onTick() {
			long currentTime = System.currentTimeMillis();
			long elapsedTime = currentTime - initTime;
			System.out.println(empId+"in else 1 "+meetingStartTime);
			myAgent.addBehaviour(new ProposeMeetingInitiator(getLocalName(), meetingStartTime,meetingEndTime, this));
		}
	}

	public ACLMessage cfp = new ACLMessage(ACLMessage.CFP);

	public class ProposeMeetingInitiator extends ContractNetInitiator {

		private String empId;

		private Date meetingST, meetingET;
		private ManagerClass manager;

		public ProposeMeetingInitiator(String eId, Date meetST, Date meetET, ManagerClass m) {
			super(UserInterfaceAgent.this, cfp);
			empId = eId;
			meetingST = meetST;
			meetingET = meetET;
			manager = m;
			System.out.println("in ProposeMeetingInitiator "+empId);
			Meeting meeting = new Meeting();
			meeting.setEmpId(empId);
			meeting.setMeetingStartTime(meetingST);
			meeting.setMeetingEndTime(meetingET);
			Meet meetAction = new Meet();
			meetAction.setItem(meeting);
			Action act = new Action(UserInterfaceAgent.this.getAID(), meetAction);
			try {
				cfp.setLanguage(codec.getName());
				cfp.setOntology(ontology.getName());
				UserInterfaceAgent.this.getContentManager().fillContent(cfp, act);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		protected Vector prepareCfps(ACLMessage cfp) {
			cfp.clearAllReceiver();
			System.out.println("preparingcfs"+attendeeAgents.size());
			for (int i = 0; i < attendeeAgents.size(); ++i) {
				cfp.addReceiver((AID) attendeeAgents.get(i));
			}

			Vector v = new Vector();
			v.add(cfp);
			if (attendeeAgents.size() > 0)
				userGUI.notifyUser("Sent Call for Proposal to "+attendeeAgents.size()+" users.");
			return v;
		}

		protected void handleAllResponses(Vector responses, Vector acceptances) {
			ACLMessage bestOffer = null;
			int bestPrice = -1;
			Date startTime = new Date();
			Date endTime  = new Date();

			for (int i = 0; i < responses.size(); i++) {
				ACLMessage rsp = (ACLMessage) responses.get(i);
				if (rsp.getPerformative() == ACLMessage.PROPOSE) {

					try {
						ContentElementList cel = (ContentElementList)myAgent.getContentManager().extractContent(rsp);
						startTime = ((ConnectedUser) cel.get(1)).getMeetingStartTime();
						endTime = ((ConnectedUser) cel.get(1)).getMeetingEndTime();
						if(bestOffer == null) {
							bestOffer = rsp;
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

			for (int i = 0; i < responses.size(); i++) {
				ACLMessage rsp = (ACLMessage) responses.get(i);
				ACLMessage accept = rsp.createReply();
				if (rsp == bestOffer) {
					boolean acceptedProposal = false;
					if((meetingST.after(startTime) && meetingST.after(endTime)) || (meetingST.before(startTime) && meetingET.before(startTime) )) {
						acceptedProposal = true;
					}
					accept.setPerformative(acceptedProposal ? ACLMessage.ACCEPT_PROPOSAL : ACLMessage.REJECT_PROPOSAL);
					accept.setContent(empId);
					userGUI.notifyUser(acceptedProposal ? "sent Accept Proposal" : "sent Reject Proposal");
				} else {
					accept.setPerformative(ACLMessage.REJECT_PROPOSAL);  
				}
				acceptances.add(accept);
			}
		}

		protected void handleInform(ACLMessage inform) {

			int price = Integer.parseInt(inform.getContent());
			userGUI.notifyUser("Book "+empId+" successfully purchased. Price =" + price);
			manager.stop();
		}
	}
}
