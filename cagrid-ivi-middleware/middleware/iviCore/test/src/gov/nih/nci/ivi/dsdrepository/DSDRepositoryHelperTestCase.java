package gov.nih.nci.ivi.dsdrepository;

import gov.nih.nci.ivi.dsd._package;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

public class DSDRepositoryHelperTestCase extends TestCase {

	Properties params;
	DSDRepositoryHelper helper;
	String rootDir;
	String packageNames[] = {"DICOMDataService.gar", "ImageDataService.gar", 
			                 "DSDRepositoryService.gar", "DSDContainerService.gar"};

	public DSDRepositoryHelperTestCase(String arg0) {
		super(arg0);
		rootDir = System.getProperty("java.io.tmpdir");
	}

	protected void setUp() throws Exception {
		super.setUp();
		params = DSDRepositoryHelper.getParameters();
		params.put(DSDRepositoryHelper.ROOT_DIR, rootDir);
		helper = null;
		try {
			helper = new DSDRepositoryHelper(params);
		} catch (IOException e) {
			e.printStackTrace();
		}

		// create the package dir
		File packageDir = new File(rootDir + File.separator + "packages");
		if (!packageDir.exists())
			assertTrue(packageDir.mkdirs());
		// put some packages
		for (int i = 0; i < packageNames.length; i++) {
			File packageFile = new File(rootDir + File.separator + "packages" + File.separator + packageNames[i]);
			if (!packageFile.exists())
				assertTrue(packageFile.createNewFile());
		}
		// create the container service dir
		File containerServiceFile = new File(rootDir + File.separator + "http..--140.254.80.191..8080-wsrf-services-cagrid-DSDContainerService");
		if (!containerServiceFile.exists())
			assertTrue(containerServiceFile.mkdirs());
		// put a package under the container service dir
		File containerServicePackageFile = new File(rootDir + File.separator + "http..--140.254.80.191..8080-wsrf-services-cagrid-DSDContainerService" + File.separator + "DICOMDataService.gar");
		if (!containerServicePackageFile.exists())
			assertTrue(containerServicePackageFile.createNewFile());
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testQueryAvailablePackages() {
		_package []availablePackages = null;
		try {
			availablePackages = helper.queryAvailablePackages();
		} catch (IOException e) {
			fail("Should not have thrown exception:" + e.getMessage());
		}
		if (availablePackages == null)
			fail("no packages available");
		else
			for (int i = 0; i < availablePackages.length; i++)
				System.out.println(availablePackages[i].getIdentifier());
		System.out.println("Successfully passed the test.");
	}

	public final void testQueryDSDContainerServices() {
		String []containerServiceURLs = null;
		try {
			containerServiceURLs = helper.queryDSDContainerServices("http://cagrid01.bmi.ohio-state.edu:8080/wsrf/services/DefaultIndexService");
		} catch (IOException e) {
			fail("Should not have thrown exception:" + e.getMessage());
		}
		if (containerServiceURLs == null)
			fail("no container services available");
		for (int i = 0; i < containerServiceURLs.length; i++)
			System.out.println(containerServiceURLs[i]);
		System.out.println("Successfully passed the test.");
	}
/*
	public final void testQueryDeployedPackages() {
		String []deployedPackages = null;
		try {
			deployedPackages = helper.queryDeployedPackages("http://140.254.80.191:8080/wsrf/services/cagrid/DSDContainerService");
		} catch (IOException e) {
			fail("Should not have thrown exception:" + e.getMessage());
		}
		if (deployedPackages == null)
			fail("no deployed packages for this service");
		else
			for (int i = 0; i < deployedPackages.length; i++)
				System.out.println(deployedPackages[i]);
		System.out.println("Successfully passed the test.");
	}
*/
}
