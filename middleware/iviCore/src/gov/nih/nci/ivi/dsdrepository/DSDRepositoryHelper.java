/**
 * 
 */
package gov.nih.nci.ivi.dsdrepository;

import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;
import gov.nih.nci.ivi.dsd._package;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.Vector;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;

/**
 * @author tpan
 *
 */
public class DSDRepositoryHelper {

	static final String ROOT_DIR = "rootDir";
	static final String []staticContainerServiceURLs =
	{"http://192.168.1.103:8080/wsrf/services/cagrid/DSDContainerService",
	"http://140.254.80.174:8080/wsrf/services/cagrid/DSDContainerService",
	"http://164.107.181.187:8080/wsrf/services/cagrid/DSDContainerService"};
	String []containerServiceURLs = null;
	File packageDir;
	File rootDir;

	public static Properties getParameters() {
		Properties params =  new Properties();
		params.put(DSDRepositoryHelper.ROOT_DIR, "CHANGE_ME:   tmp" );

		return params;
	}

	public DSDRepositoryHelper(Properties configuration) throws IOException {
		if (configuration == null)
			throw new IOException("DSDRepositoryHelper: configuration is null");
		this.initialize((String)configuration.get(DSDRepositoryHelper.ROOT_DIR));
	}

	public DSDRepositoryHelper(String dirName) throws IOException {
		this.initialize(dirName);
	}

	private void initialize(String dirName) throws IOException {
		// just validate it
		if (dirName == null || dirName == "")
			throw new IOException("DSDRepositoryHelper: null or empty dirName");
		rootDir = new File(dirName);
		if (!rootDir.exists())
			rootDir.mkdirs();
		if (!rootDir.isDirectory())
			throw new IOException("DSDRepositoryHelper: rootDir " + rootDir.getAbsolutePath() + " is not a directory");
		if (!rootDir.exists() || !rootDir.canRead() || !rootDir.canWrite())
			throw new IOException("DSDRepositoryHelper: rootDir " + rootDir.getAbsolutePath() + " is not ready");
	}

	public _package[] queryAvailablePackages() throws IOException {
		if (!rootDir.exists() || !rootDir.canRead())
			throw new IOException("DSDRepositoryHelper: Unable to read directory: " + rootDir.getAbsolutePath());
		File packageDir = new File(rootDir.getAbsolutePath() + File.separator + "packages");
		_package []availablePackages = null;
		if (!packageDir.exists()) {
			System.out.println("Package dir " + packageDir.getAbsolutePath() + " does not exist");
			throw new IOException("Package dir " + packageDir.getAbsolutePath() + " does not exist");
		}

		String []availablePackageNames = null;
		availablePackageNames = packageDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".gar"))
					return true;
				return false;
			}
		});
		if (availablePackageNames != null)
			availablePackages = new _package[availablePackageNames.length];
		for (int i = 0; i < availablePackageNames.length; i++) {
			availablePackages[i] = null;
			File availablePackageFile = new File(packageDir.getAbsoluteFile() + File.separator + removeGarExtension(availablePackageNames[i]) + ".dat");
			if (availablePackageFile.exists() && availablePackageFile.canRead()) {
				availablePackages[i] = PackageManager.readPackageInfo(availablePackageFile.getAbsolutePath());
//				PackageManager.displayPackageInfo(availablePackages[i]);
			}
			else
				availablePackages[i] = new _package();
			availablePackages[i].setIdentifier(availablePackageNames[i]);
		}

		return availablePackages;
	}

	public String[] queryDSDContainerServices(String indexServiceURL) throws IOException {
		// add static URLs to a hashmap and a vector
        System.out.println("Adding static URLs");
		HashMap<String, String> staticURLHashMap = new HashMap<String, String>();
		Vector<String> URLVector = new Vector<String>();
		if (staticContainerServiceURLs != null)
			for (int i = 0; i < staticContainerServiceURLs.length; i++) {
				staticURLHashMap.put(staticContainerServiceURLs[i], "");
				URLVector.addElement(staticContainerServiceURLs[i]);
			}
		// discover registered dsd repository services
		EndpointReferenceType[] results = null;
        try {
        	DiscoveryClient discClient = new DiscoveryClient(indexServiceURL);
        	results = discClient.discoverServicesByName("DSDContainerService");
        } catch (MalformedURIException e) {
        	e.printStackTrace();
        	throw new IOException(e.getMessage());
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new IOException(e.getMessage());
        }
		// add discovered URLs to the vector
        System.out.println("Adding discovered URLs");
		if (results != null)
			for (int i = 0; i < results.length; i++) {
        		System.out.println("discovered " + results[i].getAddress().toString());
        		if (!staticURLHashMap.containsKey(results[i].getAddress().toString()))
        			URLVector.addElement(results[i].getAddress().toString());
			}
		// convert vector to an array
        containerServiceURLs = new String[URLVector.size()];
        for (int i = 0; i < URLVector.size(); i++)
        	containerServiceURLs[i] = URLVector.elementAt(i);
		return containerServiceURLs;
	}

	private String modifyURL(String str) {
		return str.replaceAll("/", "-");
	}

	public String[] queryDeployedPackages(String containerServiceURL) throws IOException {
		String[] deployedPackages = null;

		if (!rootDir.exists() || !rootDir.canRead())
			throw new IOException("DSDRepositoryHelper: Unable to read directory: " + rootDir.getAbsolutePath());
		File serviceDir = new File(rootDir.getAbsolutePath() + File.separator + modifyURL(containerServiceURL));
		if (!serviceDir.exists())
			return deployedPackages;
		deployedPackages = serviceDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (name.endsWith(".gar"))
					return true;
				return false;
			}
		});

		return deployedPackages;
	}

	private String removeGarExtension(String str) {
		return str.substring(0, str.lastIndexOf(".gar"));
	}


	public File getRootDir() {
		return rootDir;
	}

	public static void main(String[] args) {
		Properties params = DSDRepositoryHelper.getParameters();
		params.put(DSDRepositoryHelper.ROOT_DIR, "/tmp/DSDR/");
		DSDRepositoryHelper helper = null;
		try {
			helper = new DSDRepositoryHelper(params);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// query available package names
		_package []availablePackages = null;
		try {
			availablePackages = helper.queryAvailablePackages();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (availablePackages == null)
			System.out.println("no packages available");
		else
			for (int i = 0; i < availablePackages.length; i++)
				System.out.println(availablePackages[i].getIdentifier());

		// query DSD container services
		String []containerServiceURLs = null;
		try {
			containerServiceURLs = helper.queryDSDContainerServices("http://cagrid01.bmi.ohio-state.edu:8080/wsrf/services/DefaultIndexService");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (containerServiceURLs == null)
			System.out.println("no container services available");
		for (int i = 0; i < containerServiceURLs.length; i++)
			System.out.println(containerServiceURLs[i]);

		// query deployed packages for a service
		String[] deployedPackageIdentifiers = null;
		try {
			deployedPackageIdentifiers = helper.queryDeployedPackages("http://140.254.80.191:8080/wsrf/services/cagrid/DSDContainerService");
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (deployedPackageIdentifiers == null)
			System.out.println("no deployed packages for this service");
		else
			for (int i = 0; i < deployedPackageIdentifiers.length; i++)
				System.out.println(deployedPackageIdentifiers[i]);
	}

}
