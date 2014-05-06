package gov.nih.nci.ivi.wizard.step;

import java.util.Map;


public class ConfigureDeploymentStep extends BasePropretyConfigureStep {

	public static final String GLOBUS = "Globus";
	public static final String TOMCAT = "Tomcat";
	public static final String CONTAINER_TYPE = "containerType";
	
	/**
	 * This method initializes 
	 * 
	 */
	public ConfigureDeploymentStep(Map globalMap) {
		super(globalMap);
		this.addListOption(CONTAINER_TYPE, new String[]{GLOBUS, TOMCAT}, "Deployment Container");
		this.setComplete(true);
	}
	
	public String getName(){
		return "Deployment Container";
	}
	
	public String getSummary(){
		return "Configure Deployment Container";
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
