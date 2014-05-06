package gov.nih.nci.ivi.wizard.step;

import java.util.HashMap;
import java.util.Map;


public class ConfigurePACSStep extends BasePropretyConfigureStep {

	/**
	 * This method initializes 
	 * 
	 */
	public ConfigurePACSStep(Map globalMap) {
		super(globalMap);
		this.addOption("cqlQueryProcessorConfig_serverAE","RIDERPACS1", "DICOM PACS AETitle");
		this.addOption("cqlQueryProcessorConfig_serverip","localhost", "DICOM PACS IPAddress");
		this.addOption("cqlQueryProcessorConfig_serverport","4008", "DICOM PACS Port");
		this.addOption("cqlQueryProcessorConfig_sopClassForQRServerAE","Study", "SOPClass for Query & Retrieve");
		this.setComplete(true);
	}
	
	public String getName(){
		return "PACS";
	}
	
	public String getSummary(){
		return "Configure PACS Options";
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
