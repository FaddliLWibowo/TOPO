package wsndes.gui;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import java.beans.*; //property change stuff
import java.awt.*;
import java.awt.event.*;


public class NaiveOptionsDiag  extends JDialog
								implements ActionListener,
								PropertyChangeListener {
	private JSpinner numChooser;
	private boolean isValid = false;
	private int num = 0;
	private JOptionPane optionPane;

	private String btnString1 = "Generate";
	private String btnString2 = "Cancel";
	
	/**
	* Returns null if the typed string was invalid;
	* otherwise, returns the string as the user entered it.
	*/
	public boolean isValid() {
		return isValid;
	}
	
	/** Creates the reusable dialog. */
	public NaiveOptionsDiag(Frame aFrame, String aWord) {
		super(aFrame, true);
		
		setTitle("Naive");
		
		
		SpinnerNumberModel numModel = new SpinnerNumberModel(
				new Integer(1), // value
				new Integer(1), // min
				new Integer(900), // max
				new Integer(1) // step
				);
		
		numChooser = new JSpinner(numModel);
		
		Box rBox = Box.createHorizontalBox();
		JLabel rLabel = new JLabel("Row Size: ");
		rLabel.setPreferredSize(new Dimension(70, 20));
		rBox.add(rLabel);
		rBox.add(numChooser);
		
		//Create an array of the text and components to be displayed.
		String msgString1 = "Determine the number of motes";
		Object[] array = {msgString1, rBox};
		
		//Create an array specifying the number of dialog buttons
		//and their text.
		Object[] options = {btnString1, btnString2};
		
		//Create the JOptionPane.
		optionPane = new JOptionPane(array,
		      JOptionPane.PLAIN_MESSAGE,
		      JOptionPane.YES_NO_OPTION,
		      null,
		      options,
		      options[0]);
		
		//Make this dialog display it.
		setContentPane(optionPane);
		
		//Handle window closing correctly.
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				/*
				* Instead of directly closing the window,
				* we're going to change the JOptionPane's
				* value property.
				*/
				optionPane.setValue(new Integer(
				          JOptionPane.CLOSED_OPTION));
			}
		});
		
		//Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
				numChooser.requestFocusInWindow();
			}
		});
		
		//Register an event handler that puts the text into the option pane.
		
		//Register an event handler that reacts to option pane state changes.
		optionPane.addPropertyChangeListener(this);
	}
	
	/** This method handles events for the text field. */
	public void actionPerformed(ActionEvent e) {
		optionPane.setValue(btnString1);
	}
	
	/** This method reacts to state changes in the option pane. */
	public void propertyChange(PropertyChangeEvent e) {
		String prop = e.getPropertyName();
		
		if (isVisible()
		&& (e.getSource() == optionPane)
		&& (JOptionPane.VALUE_PROPERTY.equals(prop) ||
		JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))) {
			Object value = optionPane.getValue();
			
			if (value == JOptionPane.UNINITIALIZED_VALUE) {
				//ignore reset
				return;
			}
			
			//Reset the JOptionPane's value.
			//If you don't do this, then if the user
			//presses the same button next time, no
			//property change event will be fired.
			optionPane.setValue(
			JOptionPane.UNINITIALIZED_VALUE);
			
			if (btnString1.equals(value)) {
				num  = ((Integer)numChooser.getValue()).intValue();
				isValid = true;
				clearAndHide();
			} else { //user closed dialog or clicked cancel
				isValid = false;
				clearAndHide();
			}
		}
	}
	
	/** This method clears the dialog and hides it. */
	public void clearAndHide() {
		setVisible(false);
	}
	
	public int getNum() {
		// TODO Auto-generated method stub
		return num;
	}
	
}

