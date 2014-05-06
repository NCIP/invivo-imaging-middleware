package gov.nih.nci.ivi.test.story.steps;

import com.atomicobject.haste.framework.Step;

import gov.nih.nci.ivi.test.story.util.GlobusHelper;


public class StopGlobusStep extends Step {
	private GlobusHelper helper;


	public StopGlobusStep(GlobusHelper helper) throws Exception {
		super();
		this.helper = helper;
	}


	public void runStep() throws Throwable {
		System.out.println("stopping temporary globus");

		helper.stopGlobus();

		assertFalse(helper.isGlobusRunning());
	}

}
