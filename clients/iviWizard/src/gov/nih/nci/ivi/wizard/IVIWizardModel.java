package gov.nih.nci.ivi.wizard;

import javax.swing.JComponent;

import org.pietschy.wizard.OverviewProvider;
import org.pietschy.wizard.models.MultiPathModel;
import org.pietschy.wizard.models.Path;

public class IVIWizardModel extends MultiPathModel implements OverviewProvider {

	public IVIWizardModel(Path arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public JComponent getOverviewComponent() {
		return new IVIOverviewPanel();
	}

}
