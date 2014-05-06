package gov.nih.nci.ivi.wizard;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class IVIOverviewPanel extends JPanel {

	private JLabel jLabel = null;

	public IVIOverviewPanel() {
		super();
		initialize();
	}

	/**
	 * This method initializes this
	 * 
	 */
	private void initialize() {
        jLabel = new JLabel(new ImageIcon("./resources/image.jpg"));
        this.add(jLabel, null);
			
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
