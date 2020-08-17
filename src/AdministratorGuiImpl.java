package meetingScheduler;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.BevelBorder;

/*
 * this class is responsible for handling the GUI of Admin user
 */
public class AdministratorGuiImpl extends JFrame implements AdministratorGui{
	
	private AdministratorAgent myAgent;
	private JTextField empIdTF,empNameTF,empGroupTF;
	private JButton addUserB, removeUserB, resetB;//, exitB;
	private JTextArea logTA;
	private int counterTF;

	public AdministratorGuiImpl() {
		super();
		
		addWindowListener(new   WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				myAgent.doDelete();
			}

		} );

		JPanel rootPanel = new JPanel();
		rootPanel.setLayout(new GridBagLayout());
		rootPanel.setMinimumSize(new Dimension(330, 125));
		rootPanel.setPreferredSize(new Dimension(330, 125));
		JLabel l = new JLabel("Employee ID:");

		l.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		empIdTF = new JTextField(64);
		empIdTF.setMinimumSize(new Dimension(222, 20));
		empIdTF.setPreferredSize(new Dimension(222, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 0;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(empIdTF, gridBagConstraints);

		// ********	Employee Name ***********************
		l = new JLabel("Employee Name:");

		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);		

		empNameTF = new JTextField(64);
		empNameTF.setMinimumSize(new Dimension(222, 20));
		empNameTF.setPreferredSize(new Dimension(222, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(empNameTF, gridBagConstraints);


		// ********	Employee Group *************************

		l = new JLabel("Employee Group:");

		l.setHorizontalAlignment(SwingConstants.LEFT);
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 0;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(l, gridBagConstraints);

		empGroupTF = new JTextField(64);
		empGroupTF.setMinimumSize(new Dimension(222, 20));
		empGroupTF.setPreferredSize(new Dimension(222, 20));
		gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.gridx = 1;
		gridBagConstraints.gridy = 2;
		gridBagConstraints.gridwidth = 3;
		gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
		gridBagConstraints.insets = new Insets(5, 3, 0, 3);
		rootPanel.add(empGroupTF, gridBagConstraints);



		// **********to appear on panel *************************
		rootPanel.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(rootPanel, BorderLayout.NORTH);

		// ********	Text Area *************************
		logTA = new JTextArea();
		logTA.setEnabled(false);
		logTA.setDisabledTextColor(Color.RED);
		JScrollPane jsp = new JScrollPane(logTA);
		jsp.setMinimumSize(new Dimension(300, 180));
		jsp.setPreferredSize(new Dimension(300, 180));
		JPanel p = new JPanel();
		p.setBorder(new BevelBorder(BevelBorder.LOWERED));
		p.add(jsp);
		getContentPane().add(p, BorderLayout.CENTER);

		p = new JPanel();

		// ********	add Button *************************

		addUserB = new JButton("ADD");
		addUserB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {

				String empId = empIdTF.getText();
				String empName = empNameTF.getText();
				String empGroup = empGroupTF.getText();
				try {
					if(myAgent.addUers(empId, empName, empGroup)) {
						notifyUser("User with employeeId: "+empId+", name: "+empName+" is added in group: "+empGroup);
					}
					else {
						JOptionPane.showMessageDialog(AdministratorGuiImpl.this, "Employee ID already exists", "WARNING", JOptionPane.WARNING_MESSAGE);
					}
				}
				catch(Exception ex) {
					JOptionPane.showMessageDialog(AdministratorGuiImpl.this, "Errors", "WARNING", JOptionPane.WARNING_MESSAGE);
				}
			}
		} );

		// ********	Remove Button *************************

		removeUserB = new JButton("Remove");
		removeUserB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				String empId = empIdTF.getText();
				String empName = empNameTF.getText();
				String empGroup = empGroupTF.getText();

				myAgent.removeUers(empId, empName, empGroup);
				notifyUser("Removed User employeeId: "+empId+", name: "+empName+" is also removed from group: "+empGroup);

			}
		} );

		// ********	Reset Button *************************
		resetB = new JButton("Reset");
		resetB.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				empIdTF.setText("");
				empNameTF.setText("");
				empGroupTF.setText("");
			}
		} );
		p.add(addUserB);	
		p.add(removeUserB);
		p.add(resetB);
		p.setBorder(new BevelBorder(BevelBorder.LOWERED));
		getContentPane().add(p, BorderLayout.SOUTH);
		pack();
		setResizable(false);
	}

	public void setAgent(AdministratorAgent administratorAgent) {
		myAgent = administratorAgent;
		this.setTitle("Admin logged in as: "+myAgent.getLocalName());
	}

	public void notifyUser(String message) {
		logTA.append(message+"\n");
		counterTF++;
	}
	public void notifyUserWithDialogueBox(String message) {
		JOptionPane.showMessageDialog(AdministratorGuiImpl.this, message, "WARNING", JOptionPane.WARNING_MESSAGE);
	}

	@Override
	public void reset() {
	}

	@Override
	public void removeLastEntry() {
		logTA.remove(counterTF);
		counterTF--;
	}

}
