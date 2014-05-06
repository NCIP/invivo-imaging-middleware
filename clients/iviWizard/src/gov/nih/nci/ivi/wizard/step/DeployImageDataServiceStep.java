package gov.nih.nci.ivi.wizard.step;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

public class DeployImageDataServiceStep extends BaseBusyStep {

	Map configuredProperties = null;

	/**
	 * This method initializes
	 *
	 */
	public DeployImageDataServiceStep(Map configuredProperties) {
		super("Deploy Image Data Service?");
		this.configuredProperties = configuredProperties;
	}

	public String getName() {
		return "Deployment";
	}

	public String getSummary() {
		return "Deploy Image Data Service";
	}

	protected void doWork() {
		Properties serviceProperties = new Properties();
		try {
			serviceProperties.load(new FileInputStream(new File(".."
					+ File.separator + ".." + File.separator + "services"
					+ File.separator + "ImageDataService" + File.separator
					+ "service.properties")));

			Iterator keys = configuredProperties.keySet().iterator();

			while (keys.hasNext()) {
				String key = (String) keys.next();
				serviceProperties.remove(key);
				serviceProperties.put(key, configuredProperties.get(key));
			}

			serviceProperties.store(new FileOutputStream(new File(".."
					+ File.separator + ".." + File.separator + "services"
					+ File.separator + "ImageDataService" + File.separator
					+ "service.properties")),
					"Introduce Generated Service Properties");

			try {
				String cmd = null;
				if (configuredProperties.get(
						ConfigureDeploymentStep.CONTAINER_TYPE).equals(
						ConfigureDeploymentStep.GLOBUS)) {
					cmd = CommonTools.getAntDeployGlobusCommand(new File(".."
							+ File.separator + ".." + File.separator + "services"
							+ File.separator + "ImageDataService")
							.getAbsolutePath());
				} else if (configuredProperties.get(
						ConfigureDeploymentStep.CONTAINER_TYPE).equals(
						ConfigureDeploymentStep.TOMCAT)) {
					cmd = CommonTools.getAntDeployTomcatCommand(new File(".."
							+ File.separator + ".." + File.separator + "services"
							+ File.separator + "ImageDataService")
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
