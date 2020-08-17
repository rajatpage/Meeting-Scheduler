package meetingScheduler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jade.content.ContentManager;
import jade.content.lang.Codec.CodecException;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.onto.basic.Action;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.FIPAAgentManagement.FailureException;
import jade.domain.FIPAAgentManagement.NotUnderstoodException;
import jade.domain.FIPAAgentManagement.RefuseException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.proto.ContractNetResponder;
/*
 * This class has the database and store data reated to employee ID, employee Name and employee Groups
 */
public class MatchMaker extends Agent{

	private HashMap userData = new HashMap();

	@Override
	protected void setup() {
		String localname = this.getLocalName();
		System.out.println("matchmaker" + localname);
		Agent a= this;



		addBehaviour(new CyclicBehaviour(this) {
			@Override
			public void action(){
				ACLMessage msg = receive();
				if(msg!=null){
					if("addUser".equals(msg.getConversationId())) {
						System.out.println("adding user");
						try {
							
							userData = ((HashMap)msg.getContentObject());
							System.out.println("in adduser of matchmaker"+userData);
							if(userData!=null) {
								Set<String> keys = userData.keySet();
								System.out.println("the keys in matchmaker are: "+ keys);
								addBehaviour(new UserInformer(a, userData));	

							}
						} catch (UnreadableException e) {
							e.printStackTrace();
						}

					}
					else if ("removeUser".equals(msg.getConversationId())) {
						try {
							String keyToRemove = (String)msg.getContentObject();
							userData.remove(keyToRemove);
							Set<String> keys = userData.keySet();
							System.out.println("the keys in removed matchmaker are: "+ keys);
						} catch (UnreadableException e) {
							e.printStackTrace();
						}
					}
					String msgContent = msg.getContent();

					if(msgContent!=null) {
						switch (msgContent) {
						case "deleteAgent":
							System.out.println("matchmaker is deleted");
							doDelete();
							break;

						case "askForUsers":
//					
							ACLMessage reply = new ACLMessage( ACLMessage.INFORM );
							try {
								reply.setContentObject(userData);
								reply.setConversationId("addingUsers");
							} catch (IOException e1) {
								e1.printStackTrace();
							};
							reply.addReceiver( msg.getSender() );
							send(reply);
							break;

						default:

						}
					}
				}
			}       
		});
	}
	/*
	 * class is responsible for getting all the user data by getUserData() method 
	 */
	private class UserInformer extends TickerBehaviour {

		public UserInformer(Agent a, Map user) {
			super(a, 3000);
		}

		@Override
		protected void onTick() {
			// TODO Auto-generated method stub

		}

		public Map getUserData() {
			return userData;
		}

		public void setUserData(Map userData) {
			userData = userData;
		}
	}


	public void deleteAgent() {
		this.doDelete();
	}
}
