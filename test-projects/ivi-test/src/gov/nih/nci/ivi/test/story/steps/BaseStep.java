package gov.nih.nci.ivi.test.story.steps;



import com.atomicobject.haste.framework.Step;


public abstract class BaseStep extends Step {

	private String baseDir;
	private boolean build;


	public BaseStep() throws Exception {
		baseDir = System.getProperty("basedir");
		if (baseDir == null) {
			System.err.println("basedir system property not set");
			throw new Exception("basedir system property not set");
		}
		this.build = build;
	}


	public String getBaseDir() {
		return baseDir;
	}

}
