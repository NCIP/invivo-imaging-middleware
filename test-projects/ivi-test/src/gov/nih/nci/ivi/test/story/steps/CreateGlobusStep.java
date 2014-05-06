package gov.nih.nci.ivi.test.story.steps;

import com.atomicobject.haste.framework.Step;

import gov.nih.nci.ivi.test.story.util.GlobusHelper;


public class CreateGlobusStep extends Step {
	private GlobusHelper helper;

	public CreateGlobusStep(GlobusHelper helper) throws Exception {
		super();
		this.helper = helper;
	}


	public void runStep() throws Throwable {
		System.out.println("Creating temporary globus");

		helper.createTempGlobus();
		
	}

}
