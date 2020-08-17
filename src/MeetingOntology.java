package meetingScheduler;

import jade.content.onto.BasicOntology;
import jade.content.onto.Ontology;
import jade.content.onto.OntologyException;
import jade.content.schema.AgentActionSchema;
import jade.content.schema.ConceptSchema;
import jade.content.schema.ObjectSchema;
import jade.content.schema.PredicateSchema;
import jade.content.schema.PrimitiveSchema;

/*
 * Meeting Ontology to set meetings and negotiate
 */
public class MeetingOntology extends Ontology  {

	public static final String ONTOLOGY_NAME = "Meeting-ontology";
	
	private static Ontology theInstance = new MeetingOntology();

	public static Ontology getTheInstance() {
		return theInstance;
	}

	public static void setTheInstance(Ontology theInstance) {
		MeetingOntology.theInstance = theInstance;
	}
	
	private MeetingOntology() {
		super(ONTOLOGY_NAME, BasicOntology.getInstance());
		try {
			 add(new ConceptSchema("Meeting"), Meeting.class);
			    add(new PredicateSchema("ConectedUser"), ConnectedUser.class);
			    add(new AgentActionSchema("Meet"), Meet.class);
			    
			    ConceptSchema cs = (ConceptSchema) getSchema("Meeting");
			    cs.add("empId", (PrimitiveSchema) getSchema(BasicOntology.STRING));
			    cs.add("meetingTitle", (PrimitiveSchema) getSchema(BasicOntology.STRING), 0, ObjectSchema.UNLIMITED);
			    cs.add("location", (PrimitiveSchema) getSchema(BasicOntology.STRING), ObjectSchema.OPTIONAL);
			    cs.add("meetingStartTime", (PrimitiveSchema) getSchema(BasicOntology.DATE), 0, ObjectSchema.UNLIMITED);
			    cs.add("meetingEndTime", (PrimitiveSchema) getSchema(BasicOntology.DATE), 0, ObjectSchema.UNLIMITED);
			    
			    PredicateSchema ps = (PredicateSchema) getSchema("ConnectedUser");
			    ps.add("item", (ConceptSchema) cs);
			    ps.add("meetingStartTime", (PrimitiveSchema) getSchema(BasicOntology.DATE));
			    ps.add("meetingEndTime", (PrimitiveSchema) getSchema(BasicOntology.DATE));
			    
			    AgentActionSchema as = (AgentActionSchema) getSchema("Meet");
			    as.add("item", (ConceptSchema) getSchema("Meeting"));
			   
		}  catch (OntologyException oe) {

		    oe.printStackTrace();

		  }
	}
}
