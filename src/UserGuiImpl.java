package meetingScheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

import jade.gui.TimeChooser;

/*
 * Implementation class for User GUI called by userinterface Agent
 */
public class UserGuiImpl extends JFrame implements UserGui{

	UserInterfaceAgent myAgent;

	AdministratorAgent administratorAgent;

	private JTextField empIdTF,empGroupTF,meetingStartTimeTF, meetingEndTimeTF, customMeetingTitleTF, customMeetingStartTF, 
	customMeetingEndTF, locationTF,customlocationTF, meetingTitleTF;
	private JButton setMeetingStartB, setMeetingEndB, selectMeetingB, addUserB, proposeMeetB, resetB, checkboxB, 
	setAgendaB, setStartTimeB, setEndTimeB, addToAgendaB, chexkboxRemoveB, seeUsersB, locationB;//, exitB;
	private JTextArea logTA, agendaTA;
	private HashMap userData;
	private Date customST, customET, startTime, endTime;
	private Date meetingTime;

	String[] selectedRoom = { "ROOM 1","ROOM 2", "ROOM 3","ROOM 4","ROOM 5","ROOM 6"};
	public UserGuiImpl() {
		super();

		addWindowListener(new   WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.deleteAgent();
			}
		} );

		HashSet groupNames = new HashSet<>();
	

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new GridBagLayout());
		rootPanel.setMinimumSize(new Dimension(430, 175));
		rootPanel.setPreferredSize(new Dimension(530, 175));
		GridBagConstraints gridBagConstraints = new GridBagConstraints();


		// ********	Employee Group *************************

		JLabel l = new JLabel("Selected Users:");

		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		seeUsersB = new JButton("See selected users!");
		seeUsersB.setMinimumSize(new Dimension(250, 20));
		seeUsersB.setPreferredSize(new Dimension(250, 20));
		seeUsersB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(UserGuiImpl.this, groupNames, "Selected Users Are", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(seeUsersB, gridBagConstraints);

		selectMeetingB = new JButton("Choose");
		selectMeetingB.setMinimumSize(new Dimension(100, 20));
		selectMeetingB.setPreferredSize(new Dimension(100, 20));
		selectMeetingB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				userData = myAgent.askUser();
				System.out.println("userdata ini gui is "+userData.keySet());
				CustomDialog customDialog = new CustomDialog();
				if(null!=userData || !userData.isEmpty()) {
					Set<String> keys = userData.keySet();
					customDialog.addMessageText("Select Users:");

					for(String key:keys) {
						if(key.equals(myAgent.getLocalName())) {
							continue;
						}
						JPanel p3 = new JPanel();
						GridBagConstraints gridBagConstraints = new GridBagConstraints();

						checkboxB = new JButton(key);
						checkboxB.setMinimumSize(new Dimension(150, 20));
						checkboxB.setPreferredSize(new Dimension(150, 20));
						checkboxB.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent ae) {
								groupNames.add(key);

							}
						});
						gridBagConstraints = new GridBagConstraints();
						gridBagConstraints.gridx = 0;
						gridBagConstraints.gridy = 0;
						gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
						gridBagConstraints.insets = new Insets(5, 3, 0, 3);
						p3.add(checkboxB, gridBagConstraints);   
						p3.setBorder(new BevelBorder(BevelBorder.LOWERED));
						getContentPane().add(p3, BorderLayout.NORTH);


						chexkboxRemoveB = new JButton("remove "+key);
						chexkboxRemoveB.setMinimumSize(new Dimension(1500, 20));
						chexkboxRemoveB.setPreferredSize(new Dimension(150, 20));
						chexkboxRemoveB.addActionListener(new ActionListener() {
							@Override
							public void actionPerformed(ActionEvent ae) {
								groupNames.remove(key);

							}
						});
						gridBagConstraints = new GridBagConstraints();
						gridBagConstraints.gridx = 0;
						gridBagConstraints.gridy = 1;
						gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
						gridBagConstraints.insets = new Insets(5, 3, 0, 3);
						p3.add(chexkboxRemoveB, gridBagConstraints);   
						p3.setBorder(new BevelBorder(BevelBorder.LOWERED));
						getContentPane().add(p3, BorderLayout.NORTH);
						customDialog.addComponent(p3);
						JLabel label = new JLabel("\n");
						customDialog.addComponent(label);
					}

					customDialog.show();
				} else {
					JOptionPane.showMessageDialog(UserGuiImpl.this, "NO Users to display", "WARNING", JOptionPane.WARNING_MESSAGE);
				}
			}
		} );
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(selectMeetingB, gridBagConstraints);   
		rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(rootPanel, BorderLayout.NORTH);

		// ********	Time Meeting *************************

		l = new JLabel("Start Time:");
		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		meetingStartTimeTF = new JTextField(64);
		meetingStartTimeTF.setMinimumSize(new Dimension(246, 20));
		meetingStartTimeTF.setPreferredSize(new Dimension(246, 20));
		meetingStartTimeTF.setEnabled(false);
		meetingStartTimeTF.setDisabledTextColor(Color.DARK_GRAY);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(meetingStartTimeTF, gridBagConstraints);

		setMeetingStartB = new JButton("Set");
		setMeetingStartB.setMinimumSize(new Dimension(100, 20));
		setMeetingStartB.setPreferredSize(new Dimension(100, 20));
		setMeetingStartB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

				Date m = meetingTime;
				if (m == null) {
					m = new Date();
				}
				TimeChooser tc = new TimeChooser(m);
				if (tc.showEditTimeDlg(UserGuiImpl.this) == TimeChooser.OK) {
					startTime = tc.getDate();
					if(null!=startTime) {
						meetingStartTimeTF.setText(startTime.toString());
					}
					else {
						meetingStartTimeTF.setText("");
					}

				}
			}
		} );
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(setMeetingStartB, gridBagConstraints);   
		rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(rootPanel, BorderLayout.NORTH);

		l = new JLabel("End Time:");
		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		meetingEndTimeTF = new JTextField(64);
		meetingEndTimeTF.setMinimumSize(new Dimension(246, 20));
		meetingEndTimeTF.setPreferredSize(new Dimension(246, 20));
		meetingEndTimeTF.setEnabled(false);
		meetingEndTimeTF.setDisabledTextColor(Color.DARK_GRAY);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(meetingEndTimeTF, gridBagConstraints);

		setMeetingEndB = new JButton("Set");
		setMeetingEndB.setMinimumSize(new Dimension(100, 20));
		setMeetingEndB.setPreferredSize(new Dimension(100, 20));
		setMeetingEndB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				Date m = meetingTime;
				if (m == null) {
					m = new Date();
				}
				TimeChooser tc = new TimeChooser(m);
				if (tc.showEditTimeDlg(UserGuiImpl.this) == TimeChooser.OK) {
					endTime = tc.getDate();
					if(null!=endTime) {
						meetingEndTimeTF.setText(endTime.toString());
					}
					else {
						meetingEndTimeTF.setText("");
					}

				}
			}
		} );
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 3;
		gridBagConstraints.gridy = 4;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(setMeetingEndB, gridBagConstraints);   
		rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(rootPanel, BorderLayout.NORTH);

		l = new JLabel("location:");
		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		final JComboBox<String> cb = new JComboBox<String>(selectedRoom);
		cb.setMinimumSize(new Dimension(246, 20));
		cb.setPreferredSize(new Dimension(246, 20));
		cb.setVisible(true);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 5;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(cb, gridBagConstraints);

		l = new JLabel("Description:");
		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		meetingTitleTF = new JTextField(64);
		meetingTitleTF.setMinimumSize(new Dimension(246, 20));
		meetingTitleTF.setPreferredSize(new Dimension(246, 20));
		meetingTitleTF.setEnabled(true);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 6;
		gridBagConstraints.gridwidth = 2;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(meetingTitleTF, gridBagConstraints);

		setAgendaB = new JButton("Set Agenda for Today!");
		setAgendaB.setMinimumSize(new Dimension(250, 20));
		setAgendaB.setPreferredSize(new Dimension(250, 20));
		setAgendaB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				CustomDialog customDialog = new CustomDialog();
				JPanel p = new JPanel();
				JLabel label = new JLabel("Meeting Title:");
				label.setHorizontalAlignment(SwingConstants.LEFT);
				GridBagConstraints gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
				p.add(label, gridBagConstraints);
				customMeetingTitleTF = new JTextField(64);
				customMeetingTitleTF.setMinimumSize(new Dimension(150, 20));
				customMeetingTitleTF.setPreferredSize(new Dimension(150, 20));
				customMeetingTitleTF.setEnabled(true);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(customMeetingTitleTF, gridBagConstraints);
				customDialog.addComponent(p);

				p = new JPanel();
				label = new JLabel("Meeting Location:");
				label.setHorizontalAlignment(SwingConstants.LEFT);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
				p.add(label, gridBagConstraints);

				final JComboBox<String> cb1 = new JComboBox<String>(selectedRoom);
				cb1.setMinimumSize(new Dimension(150, 20));
				cb1.setPreferredSize(new Dimension(150, 20));
				cb1.setVisible(true);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 2;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(cb1, gridBagConstraints);

				customDialog.addComponent(p);

				p = new JPanel();
				label = new JLabel("Start Time:");
				label.setHorizontalAlignment(SwingConstants.LEFT);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
				p.add(label, gridBagConstraints);
				customMeetingStartTF = new JTextField(64);
				customMeetingStartTF.setMinimumSize(new Dimension(146, 20));
				customMeetingStartTF.setPreferredSize(new Dimension(146, 20));
				customMeetingStartTF.setEnabled(false);
				customMeetingStartTF.setDisabledTextColor(Color.DARK_GRAY);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(customMeetingStartTF, gridBagConstraints);
				setStartTimeB = new JButton("Set");
				setStartTimeB.setMinimumSize(new Dimension(70, 20));
				setStartTimeB.setPreferredSize(new Dimension(70, 20));
				setStartTimeB.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						Date m = meetingTime;
						if (m == null) {
							m = new Date();
						}
						TimeChooser tc = new TimeChooser(m);
						if (tc.showEditTimeDlg(UserGuiImpl.this) == TimeChooser.OK) {
							customST = tc.getDate();
							if(null!=customST) {
								customMeetingStartTF.setText(customST.toString());

							}
							else {
								customMeetingStartTF.setText("");
							}

						}
					}
				} );
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(setStartTimeB, gridBagConstraints);   
				p.setBorder(new BevelBorder(BevelBorder.LOWERED));
				getContentPane().add(p, BorderLayout.NORTH);
				customDialog.addComponent(p);

				p = new JPanel();
				label = new JLabel("End Time:");
				label.setHorizontalAlignment(SwingConstants.LEFT);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 0;
				gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new java.awt.Insets(5, 3, 0, 3);
				p.add(label, gridBagConstraints);
				customMeetingEndTF = new JTextField(64);
				customMeetingEndTF.setMinimumSize(new Dimension(146, 20));
				customMeetingEndTF.setPreferredSize(new Dimension(146, 20));
				customMeetingEndTF.setEnabled(false);
				customMeetingEndTF.setDisabledTextColor(Color.DARK_GRAY);
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 1;
				gridBagConstraints.gridwidth = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(customMeetingEndTF, gridBagConstraints);

				setEndTimeB = new JButton("Set");
				setEndTimeB.setMinimumSize(new Dimension(70, 20));
				setEndTimeB.setPreferredSize(new Dimension(70, 20));
				setEndTimeB.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {
						Date m = meetingTime;
						if (m == null) {
							m = new Date();
						}
						TimeChooser tc = new TimeChooser(m);
						if (tc.showEditTimeDlg(UserGuiImpl.this) == TimeChooser.OK) {
							customET = tc.getDate();
							if(null!=customET) {
								customMeetingEndTF.setText(customET.toString());
							}
							else {
								customMeetingEndTF.setText("");
							}

						}
					}
				} );
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(setEndTimeB, gridBagConstraints);   
				p.setBorder(new BevelBorder(BevelBorder.LOWERED));
				getContentPane().add(p, BorderLayout.NORTH);
				customDialog.addComponent(p);

				p = new JPanel();
				addToAgendaB = new JButton("Add to Agenda!");
				addToAgendaB.setMinimumSize(new Dimension(250, 20));
				addToAgendaB.setPreferredSize(new Dimension(250, 20));
				addToAgendaB.addActionListener(new ActionListener(){
					public void actionPerformed(ActionEvent e) {

						if(customMeetingStartTF.getText().isEmpty()|| customMeetingEndTF.getText().isEmpty()||customMeetingTitleTF.getText().isEmpty()) {
							JOptionPane.showMessageDialog(UserGuiImpl.this, "fill all details!", "WARNING", JOptionPane.WARNING_MESSAGE);

						}else {
							if(customET.before(customST)) {
								JOptionPane.showMessageDialog(UserGuiImpl.this, "End time is before Start time!", "WARNING", JOptionPane.WARNING_MESSAGE);						
							}else {
								String CustomoLocation = (String) cb1.getSelectedItem();
								myAgent.addAgenda(customMeetingTitleTF.getText(), CustomoLocation, customST, customET);
							}
						}
					}
				} );
				gridBagConstraints = new GridBagConstraints();
				gridBagConstraints.gridx = 0;
				gridBagConstraints.gridy = 2;
				gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
				gridBagConstraints.insets = new Insets(5, 3, 0, 3);
				p.add(addToAgendaB, gridBagConstraints);   
				p.setBorder(new BevelBorder(BevelBorder.LOWERED));
				getContentPane().add(p, BorderLayout.NORTH);
				customDialog.addComponent(p);
				customDialog.show();
			}
		} );
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 7;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(setAgendaB, gridBagConstraints);   


		// **********to appear on panel *************************
		rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(rootPanel, BorderLayout.NORTH);

		JPanel p1 = new JPanel(); //new GridLayout(1,2)
		// ********	Text Area *************************
		logTA = new JTextArea();
		logTA.setEnabled(false);
		logTA.setDisabledTextColor(Color.DARK_GRAY);
		logTA.setSize(250, 180);
		logTA.append("********************Logger********************");
		JScrollPane jsp = new JScrollPane(logTA);
		jsp.setMinimumSize(new Dimension(250, 180));
		jsp.setPreferredSize(new Dimension(250, 180));
		jsp.setMaximumSize(new Dimension(250, 180));

		p1.setBorder(new BevelBorder(BevelBorder.LOWERED));
		p1.add(jsp);
		getContentPane().add(p1, BorderLayout.CENTER);

		agendaTA = new JTextArea();
		agendaTA.setEnabled(false);
		agendaTA.setDisabledTextColor(Color.DARK_GRAY);
		agendaTA.setSize(250, 180);
		agendaTA.append("*****************Today's Agenda****************");
		JScrollPane jsp1 = new JScrollPane(agendaTA);
		jsp1.setMinimumSize(new Dimension(250, 180));
		jsp1.setPreferredSize(new Dimension(250, 180));
		jsp1.setMaximumSize(new Dimension(250, 180));
		p1.setBorder(new BevelBorder(BevelBorder.RAISED));
		p1.add(jsp1);
		p1.setSize(new Dimension(500, 180));
		getContentPane().add(p1, BorderLayout.CENTER);	

		JPanel p2 = new JPanel();

		// ********	Propose Button *************************

		proposeMeetB = new JButton("Propose");
		proposeMeetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

				if(meetingTitleTF.getText().isEmpty() || meetingStartTimeTF.getText().isEmpty() ||
						meetingEndTimeTF.getText().isEmpty() ) { //locationTF.getText().isEmpty()
					JOptionPane.showMessageDialog(UserGuiImpl.this, "Fill all details!", "WARNING", JOptionPane.WARNING_MESSAGE);
				}else {
					if(endTime.before(startTime)) {
						JOptionPane.showMessageDialog(UserGuiImpl.this, "End time should be after the start time", "WARNING", JOptionPane.WARNING_MESSAGE);

					} else {
						String selected = (String)cb.getSelectedItem();
						System.out.println("selected value is "+selected);
						myAgent.proposeMeeting(groupNames, meetingTitleTF.getText(),selected ,startTime, endTime); //locationTF.getText()
					}
				}
			}
		} );

		// ********	Reset Button *************************
		resetB = new JButton("Reset");
		resetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				//				locationTF.setText("");
				meetingStartTimeTF.setText("");
				meetingEndTimeTF.setText("");
				meetingTitleTF.setText("");
				groupNames.clear();
				cb.setSelectedIndex(0);
			}
		} );
		p2.add(proposeMeetB);
		p2.add(resetB);
		p2.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(p2, BorderLayout.SOUTH);		
		pack();
		setResizable(false);
	}

	public void setAgent(UserInterfaceAgent userInterfaceAgent) {
		myAgent = userInterfaceAgent;
		this.setTitle("Welcome "+myAgent.getLocalName());
	}

	public void notifyUser(String message) {
		logTA.append("\n"+message);
	}
	public void notifyUserWithDialogueBox(String message) {
		JOptionPane.showMessageDialog(UserGuiImpl.this, message, "WARNING", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void updateAgenda(Meeting meeting) {
		agendaTA.append("\nMeeting on "+meeting.getMeetingTitle()+ " at location " + meeting.getLocation() +" is scheduled from "+meeting.getMeetingStartTime()+" to "
				+ meeting.getMeetingEndTime());
	}
	@Override
	public void updateHalfAgenda(String user, Meeting meeting) {
		agendaTA.append("\n"+ user +" set meeting on "+meeting.getMeetingTitle()+ " from "+meeting.getMeetingStartTime()+" to "
				+ meeting.getMeetingEndTime()+" in "+ meeting.getLocation());
	}
}
