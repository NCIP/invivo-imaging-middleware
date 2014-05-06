package gov.nih.nci.ivi.wizard.step;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JTextPane;

import org.pietschy.wizard.PanelWizardStep;

public class IntroductionStep extends PanelWizardStep {

	private JTextPane IntroTextPane = null;

	/**
	 * This method initializes 
	 * 
	 */
	public IntroductionStep() {
		super();
		initialize();
		this.setComplete(true);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(2, 2, 2, 2);
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(263, 161));
        this.add(getIntroTextPane(), gridBagConstraints);
			
	}

	/**
	 * This method initializes IntroTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getIntroTextPane() {
		if (IntroTextPane == null) {
			IntroTextPane = new JTextPane();
			IntroTextPane.setText("" +
					"Welcome to the IVI Service Creation Wizard.  The next few steps will walk" +
					"\nyou through creating and dpeloying and caGrid Imaging Data Service");
		}
		return IntroTextPane;
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
