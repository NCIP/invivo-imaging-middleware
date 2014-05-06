package gov.nih.nci.ivi.dsdrepository;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.dsd.URL;
import gov.nih.nci.ivi.dsd._package;

import java.io.File;
import java.util.Properties;

import junit.framework.TestCase;

public class DSDRepositoryCQLQueryProcessorTestCase extends TestCase {

	String ROOT_DIR = "/tmp/DSDR";
	Properties params;
	DSDRepositoryCQLQueryProcessor processor;
	CQLQuery query;
	gov.nih.nci.cagrid.cqlquery.Object tar;
	gov.nih.nci.cagrid.cqlquery.Attribute attr;
	CQLQueryResults results;
	CQLQueryResultsIterator iter;
	String packageNames[] = {"DICOMDataService.gar", "ImageDataService.gar", 
			                 "DSDRepositoryService.gar", "DSDContainerService.gar"};
	
	public DSDRepositoryCQLQueryProcessorTestCase(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		params = DSDRepositoryHelper.getParameters();
		params.put(DSDRepositoryHelper.ROOT_DIR, ROOT_DIR);
		processor = new DSDRepositoryCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			e.printStackTrace();
		}
		// create the package dir
		File packageDir = new File(ROOT_DIR + File.separator + "packages");
		if (!packageDir.exists())
			assertTrue(packageDir.mkdirs());
		// put some packages
		for (int i = 0; i < packageNames.length; i++) {
			File packageFile = new File(ROOT_DIR + File.separator + "packages" + File.separator + packageNames[i]);
			if (!packageFile.exists())
				assertTrue(packageFile.createNewFile());
		}
		// create the container service dir
		File containerServiceFile = new File(ROOT_DIR + File.separator + "http:--140.254.80.191:8080-wsrf-services-cagrid-DSDContainerService");
		if (!containerServiceFile.exists())
			assertTrue(containerServiceFile.mkdirs());
		// put a package under the container service dir
		File containerServicePackageFile = new File(ROOT_DIR + File.separator + "http:--140.254.80.191:8080-wsrf-services-cagrid-DSDContainerService" + File.separator + "DICOMDataService.gar");
		if (!containerServicePackageFile.exists())
			assertTrue(containerServicePackageFile.createNewFile());
		
		query = null;
		tar = null;
		attr = null;
		results = null;
		iter = null;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testProcessQueryCQLQueryPackageNames() {
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.dsd._package");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("availablePackages");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("");
		tar.setAttribute(attr);
		query.setTarget(tar);

		results = null;
		try {
			results = processor.processQuery(query);
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
		assertNotNull(results);
		iter = new CQLQueryResultsIterator(results);
		if (iter == null || iter.hasNext() == false)
			fail("no available packages found");
		while (iter.hasNext()) {
			_package packageInfo = (_package)iter.next();
			System.out.println(packageInfo.getName());
		}
		System.out.println("Successfully passed the test.");
	}

	public final void testProcessQueryCQLQueryContainerServices() {
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.dsd.URL");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("DSDContainerServices");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("http://cagrid01.bmi.ohio-state.edu:8080/wsrf/services/DefaultIndexService");
		tar.setAttribute(attr);
		query.setTarget(tar);

		results = null;
		try {
			results = processor.processQuery(query);
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
		iter = new CQLQueryResultsIterator(results);
		if (iter == null || iter.hasNext() == false)
			fail("no container services found");
		while (iter.hasNext()) {
			URL containerServiceURL = (URL)iter.next();
			System.out.println(containerServiceURL.getValue());
		}
		System.out.println("Successfully passed the test.");
	}
	
	public final void testProcessQueryCQLQueryDeployedPackages() {
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.dsd._package");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("deployedPackages");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("http://140.254.80.191:8080/wsrf/services/cagrid/DSDContainerService");
		tar.setAttribute(attr);
		query.setTarget(tar);

		results = null;
		try {
			results = processor.processQuery(query);
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
		iter = new CQLQueryResultsIterator(results);
		if (iter == null || iter.hasNext() == false)
			fail("no deployed packages found for this service");
		while (iter.hasNext()) {
			_package packageInfo = (_package)iter.next();
			System.out.println(packageInfo.getName());
		}
		System.out.println("Successfully passed the test.");
	}
	
}
