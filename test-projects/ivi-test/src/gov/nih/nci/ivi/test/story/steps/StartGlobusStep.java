package gov.nih.nci.ivi.test.story.steps;

import com.atomicobject.haste.framework.Step;

import gov.nih.nci.ivi.test.story.util.GlobusHelper;


public class StartGlobusStep extends Step {
	private GlobusHelper helper;

	public StartGlobusStep(GlobusHelper helper) throws Exception {
		super();
		this.helper = helper;
	}


	public void runStep() throws Throwable {
		System.out.println("Starting temporary globus");
		
		helper.startGlobus();
		Thread.sleep(10000);
	}

}
