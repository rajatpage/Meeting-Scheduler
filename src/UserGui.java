package meetingScheduler;

/*
 * Interface for user gui
 */
public interface UserGui {
	void setAgent(UserInterfaceAgent userInterfaceAgent);

	void show();

	void hide();

	void notifyUser(String message);
	
	public void notifyUserWithDialogueBox(String message);

	public void updateAgenda(Meeting meeting);
	
	public void updateHalfAgenda(String user, Meeting meeting);
	
	void dispose();
}
