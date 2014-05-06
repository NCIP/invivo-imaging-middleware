package gov.nih.nci.ivi.wizard;

import java.util.HashMap;
import java.util.Map;

import org.pietschy.wizard.WizardModel;
import org.pietschy.wizard.models.BranchingPath;
import org.pietschy.wizard.models.Condition;
import org.pietschy.wizard.models.SimplePath;

import gov.nih.nci.ivi.wizard.step.ConfigureDeploymentStep;
import gov.nih.nci.ivi.wizard.step.ConfigureDicomDataServiceStep;
//import gov.nih.nci.ivi.wizard.step.ConfigureGridFTPStep;
import gov.nih.nci.ivi.wizard.step.ConfigureImageDataServiceStep;
import gov.nih.nci.ivi.wizard.step.ConfigurePACSStep;
import gov.nih.nci.ivi.wizard.step.DeployDicomDataServiceStep;
import gov.nih.nci.ivi.wizard.step.DeployImageDataServiceStep;
import gov.nih.nci.ivi.wizard.step.FinishedStep;
import gov.nih.nci.ivi.wizard.step.IntroductionStep;
import gov.nih.nci.ivi.wizard.step.ServiceTypeChoiceStep;

public class IVIInstallPath extends BranchingPath {

	public IVIInstallPath(){
		Map dicomPropertiesMap = new HashMap();
		Map imagePropertiesMap = new HashMap();
		SimplePath finishPath = new SimplePath();
		finishPath.addStep(new FinishedStep());
		
		SimplePath imageServicePath = new SimplePath();
		
		imageServicePath.addStep(new ConfigureImageDataServiceStep(imagePropertiesMap));
//		imageServicePath.addStep(new ConfigureGridFTPStep(imagePropertiesMap));
		imageServicePath.addStep(new ConfigureDeploymentStep(imagePropertiesMap));
		imageServicePath.addStep(new DeployImageDataServiceStep(imagePropertiesMap));
		imageServicePath.setNextPath(finishPath);
		
		SimplePath dicomServicePath = new SimplePath();
		dicomServicePath.addStep(new ConfigurePACSStep(dicomPropertiesMap));
		dicomServicePath.addStep(new ConfigureDicomDataServiceStep(dicomPropertiesMap));
//		dicomServicePath.addStep(new ConfigureGridFTPStep(dicomPropertiesMap));
		dicomServicePath.addStep(new ConfigureDeploymentStep(dicomPropertiesMap));
		dicomServicePath.addStep(new DeployDicomDataServiceStep(dicomPropertiesMap));
		dicomServicePath.setNextPath(finishPath);
		

		this.addStep(new IntroductionStep());
		final ServiceTypeChoiceStep serviceTypeSelector = new ServiceTypeChoiceStep();
		this.addStep(serviceTypeSelector);
		
		this.addBranch(dicomServicePath, new Condition() {
			public boolean evaluate(WizardModel arg0) {
				return serviceTypeSelector.isDICOMType();
			}
		});
		
		this.addBranch(imageServicePath, new Condition() {
			public boolean evaluate(WizardModel arg0) {
				return serviceTypeSelector.isImageType();
			}
		});
	}

}
