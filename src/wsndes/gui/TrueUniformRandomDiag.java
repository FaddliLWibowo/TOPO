package wsndes.gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;

public class TrueUniformRandomDiag extends JDialog
implements ActionListener,
PropertyChangeListener {
	
	int num, h, w;
	private JSpinner widthChooser,heightChooser,numChooser;
	private String btnString1 = "Generate";
    private String btnString2 = "Cancel";
    private JOptionPane optionPane;
    private boolean isValid = false;
	
	public TrueUniformRandomDiag(Frame aFrame, String aWord) {
        super(aFrame, true);
        setTitle("Uniform Random");

        
        SpinnerNumberModel widthModel = new SpinnerNumberModel(
                new Integer(200), // value
                new Integer(1), // min
                new Integer(1000), // max
                new Integer(1) // step
        );
        
        SpinnerNumberModel heightModel = new SpinnerNumberModel(
                new Integer(200), // value
                new Integer(1), // min
                new Integer(1000), // max
                new Integer(1) // step
        );
        
        SpinnerNumberModel numModel = new SpinnerNumberModel(
                new Integer(30), // value
                new Integer(1), // min
                new Integer(200), // max
                new Integer(1) // step
        );
        
        widthChooser = new JSpinner(widthModel);
        heightChooser = new JSpinner(heightModel);
        numChooser = new JSpinner(numModel);
        
        
        Box wBox = Box.createHorizontalBox();
        JLabel wLabel = new JLabel("Area Width: ");
        wLabel.setPreferredSize(new Dimension(70, 20));
        wBox.add(wLabel);
        wBox.add(widthChooser);
        
        Box hBox = Box.createHorizontalBox();
        JLabel hLabel = new JLabel("Area Height: ");
        hLabel.setPreferredSize(new Dimension(70, 20));
        hBox.add(hLabel);
        hBox.add(heightChooser);
        
        Box nBox = Box.createHorizontalBox();
        JLabel nLabel = new JLabel("Motes: ");
        nLabel.setPreferredSize(new Dimension(70, 20));
        nBox.add(nLabel);
        nBox.add(numChooser);
        
        Object[] options = {btnString1, btnString2};
        Box vTextFields = Box.createVerticalBox();
        vTextFields.add(wBox);
        vTextFields.add(hBox);
        vTextFields.add(nBox);
        
        //Create an array of the text and components to be displayed.
        String msgString1 = "Determine the width and heigh of the region and the number of motes";
        Object[] array = {msgString1, vTextFields};
        //Create the JOptionPane.
        optionPane = new JOptionPane(array,
                                    JOptionPane.PLAIN_MESSAGE,
                                    JOptionPane.YES_NO_OPTION,
                                    null,
                                    options,
                                    options[0]);
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
            	widthChooser.requestFocusInWindow();
            	heightChooser.requestFocusInWindow();
            	numChooser.requestFocusInWindow();
            }
        });

        //Register an event handler that puts the text into the option pane.

        //Register an event handler that reacts to option pane state changes.
        optionPane.addPropertyChangeListener(this);
	}

	@Override
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
				w  = ((Integer)widthChooser.getValue()).intValue();
				h  = ((Integer)heightChooser.getValue()).intValue();
				num  = ((Integer)numChooser.getValue()).intValue();
				isValid = true;
				clearAndHide();
            } else { //user closed dialog or clicked cancel
            	isValid = false;
                clearAndHide();
            }
        }
	}
	
    public void clearAndHide() {
        setVisible(false);
    }
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		optionPane.setValue(btnString1);
	}

	public int getRegionWidth() {
		return w;
	}

	public int getRegionHeight() {
		return h;
	}

	public int getNum() {
		return num;
	}
	
	public boolean isValid() {
        return isValid;
    }
}
