package gov.nih.nci.ivi.test.story.steps;

import com.atomicobject.haste.framework.Step;

import gov.nih.nci.ivi.test.story.util.GlobusHelper;


public class CleanupGlobusStep extends Step {
	private GlobusHelper helper;


	public CleanupGlobusStep(GlobusHelper helper) throws Exception {
		super();
		this.helper = helper;
	}


	public void runStep() throws Throwable {
		System.out.println("removing temporary globus");

		helper.cleanupTempGlobus();
	}

}
