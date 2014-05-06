package gov.nih.nci.ivi.wizard;

import org.pietschy.wizard.Wizard;

public class IVIServiceDeploymentWizard {

	public static void main(String[] args) {
		IVIInstallPath paths = new IVIInstallPath();
		IVIWizardModel model = new IVIWizardModel(paths);
		Wizard wizard = new Wizard(model);
		wizard.setOverviewVisible(true);
		wizard.showInFrame("IVI Service Deployment");
	}
}
