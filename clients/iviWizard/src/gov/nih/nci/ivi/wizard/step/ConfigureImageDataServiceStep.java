package gov.nih.nci.ivi.wizard.step;

import java.util.HashMap;
import java.util.Map;


public class ConfigureImageDataServiceStep extends BasePropretyConfigureStep {

	/**
	 * This method initializes 
	 * 
	 */
	public ConfigureImageDataServiceStep(Map globalMap) {
		super(globalMap);
		this.addOption("cqlQueryProcessorConfig_rootDir","/tmp/GIdata", "Image Data Service Root Directory");
		this.setComplete(true);
	}
	
	public String getName(){
		return "Image Grid Service";
	}
	
	public String getSummary(){
		return "Configure Image Grid Service Options";
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
