package meetingScheduler;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
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
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
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

/*
 * This class is resplonsible for managing personal agendas and setting meetings for all users
 */

public class MeetingManager extends Agent{

	private Codec codec = new SLCodec();
	HashMap<String, HashSet<Meeting>> storedData = new HashMap<String, HashSet<Meeting>>();
	
	MessageTemplate mt = 
			MessageTemplate.and(  
					MessageTemplate.MatchPerformative( ACLMessage.INFORM ),
					MessageTemplate.MatchPerformative( ACLMessage.PROPOSE )) ;

	protected void setup() {
		MeetingManager a = this;
		System.out.println("MeetingManager "+getAID().getName()+" is ready.");
		getContentManager().registerLanguage(codec);
	
		//Registering Meetingmanager to Yellow pages
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Meeting-Manager");
		sd.setName(getLocalName()+"-Meeting-Manager");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		}
		catch (FIPAException fe) {
			fe.printStackTrace();
		}

		addBehaviour(new CyclicBehaviour() {
			public void action() {
				ACLMessage msg = receive();
				if(msg!=null){
					if(msg.getPerformative()== ACLMessage.REQUEST ) {
						if(msg.getConversationId().equals("addAgenda")) {
							Meeting meeting = new Meeting();
							try {
								meeting = (Meeting) msg.getContentObject();
							} catch (UnreadableException e1) {
								e1.printStackTrace();
							}
							AID senderAid = msg.getSender();
							String sender = senderAid.getLocalName();
							ACLMessage reply = msg.createReply();
							reply.setConversationId("addAgendaResult");
							try {
								addBehaviour(new AgendaManager(a, meeting, sender));
								reply.setContentObject(meeting);
								reply.setPerformative(ACLMessage.AGREE);


							} catch(ArrayStoreException e) {
								reply.setPerformative(ACLMessage.CANCEL);

							} catch (IOException e) {
								e.printStackTrace();
							}
							send(reply);
						}
						System.out.println("in inform meetingmanger");
						String msgContent = msg.getContent();
					} 
					else if(msg.getPerformative() == ACLMessage.PROPOSE) {
						if(msg.getConversationId().equals("proposeMeeting")) {
								System.out.println("in propose of meeting");
							MeetingInfo meetingInfo = new MeetingInfo();
							try {
								meetingInfo = (MeetingInfo) msg.getContentObject();
							} catch (UnreadableException e) {
								e.printStackTrace();
							}
							AID senderAid = msg.getSender();
							String sender = senderAid.getLocalName();
							addBehaviour(new MeetingScheduler(a, meetingInfo, sender));
						}
					} else if(msg.getPerformative() == ACLMessage.INFORM) {
						if(msg.getConversationId().equals("addingUsersToDB")) {
							System.out.println("adding to");
							String empId = msg.getContent();
							HashSet<Meeting> empMeetings = new HashSet<Meeting>();
							storedData.put(empId, empMeetings);
						}
					}
					else {
					}
				}
			}
		});
	}

	/*
	 * Subclass to set meeting between different users and proposing new time if one is busy
	 */
	private class MeetingScheduler extends OneShotBehaviour {

		MeetingInfo meetingInfo = new MeetingInfo();
		String sender = new String();
		boolean flag =true;
		HashSet<String> attendees= new HashSet<String>();

		public  MeetingScheduler(Agent a, MeetingInfo mInfo, String s) {
			meetingInfo = mInfo;
			sender = s;
		}
		@Override
		public void action() {

			Meeting meeting = meetingInfo.getMeeting();
			attendees = meetingInfo.getAttendees();
			System.out.println("in action of meetiing scheduler "+meeting.getMeetingStartTime());
			proposeMeetingCheck(meeting);
			if(!flag) {	

				
				for(int i=0; i<30; i++) {
					flag = true;
					Date negotiatedST = meeting.getMeetingStartTime();
					negotiatedST.setMinutes(meeting.getMeetingStartTime().getMinutes()+10);
					Date negotiatedET = meeting.getMeetingEndTime();
					negotiatedET.setMinutes(meeting.getMeetingEndTime().getMinutes()+10);
					meeting.setMeetingStartTime(negotiatedST);
					meeting.setMeetingEndTime(negotiatedET);
					System.out.println("calculating new time with "+meeting.getMeetingStartTime());
					proposeMeetingCheck(meeting);
					System.out.println("output of propose meeting "+flag);
					if(flag) {
						sendAcceptance(meeting);
						break;
					}
				}if(!flag) {
					sendRejection();	
				}
			}
			else {
				System.out.println("in outer else");
				sendAcceptance(meeting);
			}

		}
		private void proposeMeetingCheck (Meeting meeting)  {
			outerloop:
				for(String key: attendees) {
					System.out.println("checking for user "+ key);
					HashSet<Meeting> meetings = storedData.get(key);
					for(Meeting storedMeeting: meetings) {
						System.out.println("attendee propose");
						flag = checkAvailableSlot(meeting, storedMeeting);
						if(!flag) {
							break outerloop;
						}
					}
					
				}
		if(flag) {
			HashSet<Meeting> senderMeetings = storedData.get(sender);
			for(Meeting senderMeeting : senderMeetings) {
				flag = checkAvailableSlot(meeting, senderMeeting);
				if(!flag) {
					break;
				}
			}
		}
		}
		private void sendAcceptance (Meeting meeting) {
			HashMap<String, Meeting> mapToInform = new HashMap<String, Meeting>();
			mapToInform.put(sender, meeting);
			ACLMessage acl = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
			acl.addReceiver(new AID(sender, AID.ISLOCALNAME));
			try {
				acl.setContentObject(meeting);
			} catch (IOException e) {
				e.printStackTrace();
			}
			acl.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
			acl.setConversationId("proposalOutput");
			send(acl);
			for(String attendee: attendees) {
				ACLMessage aclmsg = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				aclmsg.addReceiver(new AID(attendee, AID.ISLOCALNAME));
				try {
					aclmsg.setContentObject(mapToInform);
				} catch (IOException e) {
					e.printStackTrace();
				}
				aclmsg.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
				aclmsg.setConversationId("proposeMeeting");
				send(aclmsg);
				storedData.get(attendee).add(meeting);

			}
			storedData.get(sender).add(meeting);
		}
		private void sendRejection () {
			System.out.println("sending rejection");
			ACLMessage reply = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
			reply.setPerformative(ACLMessage.REJECT_PROPOSAL);
			reply.setConversationId("proposalOutput");
			reply.addReceiver(new AID(sender, AID.ISLOCALNAME));
			send(reply);
		}
	}
	
	// users' personal agendas are updated automatically and the UserInterface agents are notified of these changes by means of a FIPA-Query protocol
	private class AgendaManager extends OneShotBehaviour {

		boolean flag = true;

		public AgendaManager(Agent a, Meeting meeting, String sender) {
				HashSet<Meeting> scheduledMeetings = storedData.get(sender);
				for(Meeting scheduledMeeting: scheduledMeetings) {
					flag = checkAvailableSlot(meeting, scheduledMeeting);
					if(!flag) {
						break;
					}
				}
				if (flag) {
					storedData.get(sender).add(meeting);
				}
				else {
					throw new ArrayStoreException();
				}
		} 
		@Override
		public void action() {
		}

	}

	private boolean checkAvailableSlot (Meeting proposedMeeting, Meeting storedMeeting) {
		System.out.println(proposedMeeting.getMeetingStartTime()+" start time "+ storedMeeting.getMeetingStartTime());
		System.out.println(proposedMeeting.getMeetingEndTime()+" end time "+ storedMeeting.getMeetingEndTime());
		if((proposedMeeting.getMeetingStartTime().after(storedMeeting.getMeetingStartTime()))){  
			if(proposedMeeting.getMeetingStartTime().before(storedMeeting.getMeetingEndTime())) {
				//					System.out.println("making false for "+meetingDate.getMeetingStartTime());
				return false;
			}
		}
		if((proposedMeeting.getMeetingStartTime().before(storedMeeting.getMeetingStartTime()))) {
			if(	proposedMeeting.getMeetingEndTime().after(storedMeeting.getMeetingStartTime()) ) {
				//			System.out.println("making false for "+meetingDate.getMeetingStartTime());
				return false;
			}
		}

		if(proposedMeeting.getMeetingStartTime().equals(storedMeeting.getMeetingStartTime())|| proposedMeeting.getMeetingEndTime().equals(storedMeeting.getMeetingEndTime())) {
			return false;
		}

		return true;
	}
}
