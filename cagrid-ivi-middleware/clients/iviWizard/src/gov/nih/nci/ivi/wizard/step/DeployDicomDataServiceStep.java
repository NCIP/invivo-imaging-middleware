package gov.nih.nci.ivi.wizard.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DeployDicomDataServiceStep extends BaseBusyStep {

	Map configuredProperties = null;

	/**
	 * This method initializes
	 *
	 */
	public DeployDicomDataServiceStep(Map configuredProperties) {
		super("Deploy DICOM Data Service?");
		this.configuredProperties = configuredProperties;
	}

	public String getName() {
		return "Deployment";
	}

	public String getSummary() {
		return "Deploy DICOM Data Service";
	}

	protected void doWork() {
		Properties serviceProperties = new Properties();
		try {
			serviceProperties.load(new FileInputStream(new File(".."
					+ File.separator + ".." + File.separator + "services"
					+ File.separator + "DICOMDataService" + File.separator
					+ "service.properties")));

			Iterator keys = configuredProperties.keySet().iterator();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				serviceProperties.remove(key);
				serviceProperties.put(key, configuredProperties.get(key));
			}

			serviceProperties.store(new FileOutputStream(new File(".."
					+ File.separator + ".." + File.separator + "services"
					+ File.separator + "DICOMDataService" + File.separator
					+ "service.properties")),
					"Introduce Generated Service Properties");

			try {
				String cmd = null;
				if (configuredProperties.get(
						ConfigureDeploymentStep.CONTAINER_TYPE).equals(
						ConfigureDeploymentStep.GLOBUS)) {
					cmd = CommonTools.getAntDeployGlobusCommand(new File(".."
							+ File.separator + ".." + File.separator + "services"
							+ File.separator + "DICOMDataService")
							.getAbsolutePath());
				} else if (configuredProperties.get(
						ConfigureDeploymentStep.CONTAINER_TYPE).equals(
						ConfigureDeploymentStep.TOMCAT)) {
					cmd = CommonTools.getAntDeployTomcatCommand(new File(".."
							+ File.separator + ".." + File.separator + "services"
							+ File.separator + "DICOMDataService")
							.getAbsolutePath());
				}
				Process p = CommonTools.createAndOutputProcess(cmd);
				p.waitFor();
			} catch (Exception e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

} // @jve:decl-index=0:visual-constraint="10,10"
