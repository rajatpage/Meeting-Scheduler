package meetingScheduler;

public interface AdministratorGui {
	void setAgent(AdministratorAgent administratorAgent);

	void show();

	void hide();

	void notifyUser(String message);
	
	void notifyUserWithDialogueBox(String message);

	void dispose();
	
	void reset();
	
	void removeLastEntry();

}
