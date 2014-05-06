package gov.nih.nci.ivi.wizard.step;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JRadioButton;

import org.pietschy.wizard.PanelWizardStep;

public class ServiceTypeChoiceStep extends PanelWizardStep {

	private JRadioButton dicomServiceButton = null;
	private JRadioButton imageServiceButton = null;

	/**
	 * This method initializes 
	 * 
	 */
	public ServiceTypeChoiceStep() {
		super();
		initialize();
		this.setComplete(true);
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridy = 1;
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        this.setLayout(new GridBagLayout());
        this.setSize(new Dimension(263, 161));
        this.add(getDicomServiceButton(), gridBagConstraints1);
        this.add(getImageServiceButton(), gridBagConstraints2);
			
	}

	/**
	 * This method initializes dicomServiceButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getDicomServiceButton() {
		if (dicomServiceButton == null) {
			dicomServiceButton = new JRadioButton();
			dicomServiceButton.setText("DICOM Data Service");
			dicomServiceButton.setSelected(true);
			dicomServiceButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(dicomServiceButton.isSelected()){
						imageServiceButton.setSelected(false);
					}
				}
			});
		}
		return dicomServiceButton;
	}
	
	public boolean isDICOMType(){
		return this.getDicomServiceButton().isSelected();
	}

	/**
	 * This method initializes imageServiceButton	
	 * 	
	 * @return javax.swing.JRadioButton	
	 */
	private JRadioButton getImageServiceButton() {
		if (imageServiceButton == null) {
			imageServiceButton = new JRadioButton();
			imageServiceButton.setText("Image Data Service");
			imageServiceButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(imageServiceButton.isSelected()){
						dicomServiceButton.setSelected(false);
					}
				}
			});
		}
		return imageServiceButton;
	}
	
	public boolean isImageType(){
		return getImageServiceButton().isSelected();
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
