/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle or the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */ 

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

/* 1.4 example used by DialogDemo.java. */
public class GridOptionsDiag extends JDialog
                   implements ActionListener,
                              PropertyChangeListener {
    private JSpinner rowChooser;
    private JSpinner colChooser;
    private boolean isValid = false;
    private int row = 0;
    private int col = 0;
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
    public GridOptionsDiag(Frame aFrame, String aWord) {
        super(aFrame, true);

        setTitle("Grid");

        
        SpinnerNumberModel rowModel = new SpinnerNumberModel(
                new Integer(1), // value
                new Integer(1), // min
                new Integer(30), // max
                new Integer(1) // step
        );
        
        SpinnerNumberModel colModel = new SpinnerNumberModel(
                new Integer(1), // value
                new Integer(1), // min
                new Integer(30), // max
                new Integer(1) // step
        );
        
        rowChooser = new JSpinner(rowModel);
        colChooser = new JSpinner(colModel);
        
        Box rBox = Box.createHorizontalBox();
        JLabel rLabel = new JLabel("Row Size: ");
        rLabel.setPreferredSize(new Dimension(70, 20));
        rBox.add(rLabel);
        rBox.add(rowChooser);
        
        Box cBox = Box.createHorizontalBox();
        JLabel cLabel = new JLabel("Col Size: ");
        cLabel.setPreferredSize(new Dimension(70, 20));
        cBox.add(cLabel);
        cBox.add(colChooser);
        
        Box vTextFields = Box.createVerticalBox();
        vTextFields.add(rBox);
        vTextFields.add(cBox);
        
        //Create an array of the text and components to be displayed.
        String msgString1 = "Determine the row and column size of the grid";
        Object[] array = {msgString1, vTextFields};

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
            	rowChooser.requestFocusInWindow();
            	colChooser.requestFocusInWindow();
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
				row  = ((Integer)rowChooser.getValue()).intValue();
				col  = ((Integer)colChooser.getValue()).intValue();
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

	public int getRow() {
		// TODO Auto-generated method stub
		return row;
	}

	public int getCol() {
		// TODO Auto-generated method stub
		return col;
	}
}
