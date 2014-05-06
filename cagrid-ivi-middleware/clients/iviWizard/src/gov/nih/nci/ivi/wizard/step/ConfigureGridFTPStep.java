package gov.nih.nci.ivi.wizard.step;

import java.util.Map;

@Deprecated
public class ConfigureGridFTPStep extends BasePropretyConfigureStep {

	/**
	 * This method initializes 
	 * 
	 */
	public ConfigureGridFTPStep(Map globalMap) {
		super(globalMap);
		this.addOption("gridFTPHost","localhost", "Grid FTP Host");
		this.addOption("gridFTPPort","2811", "Grid FTP Port");
		this.setComplete(true);
	}
	
	public String getName(){
		return "GridFTP";
	}
	
	public String getSummary(){
		return "Configure GridFTP Options";
	}

}  //  @jve:decl-index=0:visual-constraint="10,10"
