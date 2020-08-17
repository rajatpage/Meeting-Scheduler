package meetingScheduler;

import jade.content.onto.*;
import jade.content.schema.*;

/*
 * UserOntology class
 */
public class UserOntology extends Ontology{
	// The name identifying this ontology

	public static final String USERS = "Users";

	public static final String MEETING_SCHEDULE = "meetingSchedule";

	public static final String EMPLOYEE_ID = "empId";

	public static final String EMPLOYEE_NAME= "empName";

	public static final String EMPLOYEE_GROUP = "empGroup";

	public static final String COSTS_ITEM = "item";

	public static final String COSTS_PRICE = "price";

	public static final String SET_MEETING = "setMeeting";

	public static final String SELL_ITEM = "item";

	public static final String ONTOLOGY_NAME = "User-ontology";
	private static Ontology theInstance = new UserOntology();

	public static Ontology getInstance() {

		return theInstance;
	}

	private UserOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());
		try {
			add(new ConceptSchema(USERS), Users.class);
			add(new PredicateSchema(MEETING_SCHEDULE), ConnectedUser.class);
			add(new AgentActionSchema(SET_MEETING), SetMeeting.class);



			// Structure of the schema for the Users concept
			ConceptSchema cs = (ConceptSchema) getSchema(USERS);
			cs.add(EMPLOYEE_ID, (PrimitiveSchema) getSchema(BasicOntology.STRING));
			cs.add(EMPLOYEE_NAME, (PrimitiveSchema) getSchema(BasicOntology.STRING), 0,
					ObjectSchema.UNLIMITED);
			cs.add(EMPLOYEE_GROUP, (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);



			// Structure of the schema for the Costs predicate

			PredicateSchema ps = (PredicateSchema) getSchema(MEETING_SCHEDULE);

			ps.add(COSTS_ITEM, (ConceptSchema) cs);

			ps.add(COSTS_PRICE, (PrimitiveSchema) getSchema(BasicOntology.INTEGER));



			// Structure of the schema for the Sell agent action

			AgentActionSchema as = (AgentActionSchema) getSchema(SET_MEETING);

			as.add(SELL_ITEM, (ConceptSchema) getSchema(USERS));

		}

		catch (OntologyException oe) {

			oe.printStackTrace();

		}

	}


}
