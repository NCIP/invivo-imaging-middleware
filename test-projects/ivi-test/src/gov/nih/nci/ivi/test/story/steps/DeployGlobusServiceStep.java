package gov.nih.nci.ivi.test.story.steps;

import java.io.File;

import gov.nih.nci.ivi.test.story.CommonTools;
import gov.nih.nci.ivi.test.story.util.GlobusHelper;

public class DeployGlobusServiceStep extends BaseStep {
	private GlobusHelper helper;

	public DeployGlobusServiceStep(GlobusHelper helper) throws Exception {
		super();
		this.helper = helper;
	}

	public void runStep() throws Throwable {
		System.out.println("Deploying service ");

		String cmd = CommonTools.getAntDeployGlobusCommand(new File(
				getBaseDir()).getAbsolutePath());

		Process p = CommonTools.createAndOutputProcess(cmd);
		p.waitFor();
		assertEquals("Checking deploy status", 0, p.exitValue());

		helper.deployService(new File(getBaseDir()));

	}

}
