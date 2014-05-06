package gov.nih.nci.ivi.wizard.step;

import java.util.HashMap;
import java.util.Map;


public class ConfigureDicomDataServiceStep extends BasePropretyConfigureStep {

	/**
	 * This method initializes 
	 * 
	 */
	public ConfigureDicomDataServiceStep(Map globalMap) {
		super(globalMap);
		this.addOption("cqlQueryProcessorConfig_clientAE","DICOMDATASERVICE", "DataService AETitle");
		this.addBooleanOption("cqlQueryProcessorConfig_useCMOVE",false,"Use C_MOVE to retrieve images from PACS?");
		this.addOption("cqlQueryProcessorConfig_embeddedPacsAE","Ignore if using C_GET", "CMOVE destination AETitle");
		this.addOption("cqlQueryProcessorConfig_embeddedPacsPort","Ignore if using C_GET", "CMOVE destination Port");
		this.setComplete(true);
	}
	
	public String getName(){
		return "DICOM Grid Service";
	}
	
	public String getSummary(){
		return "Configure DICOM Grid Service Options";
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
