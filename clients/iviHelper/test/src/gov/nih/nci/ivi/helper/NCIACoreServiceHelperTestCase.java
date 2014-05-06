package gov.nih.nci.ivi.helper;


import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Iterator;
import java.util.Properties;

import junit.framework.TestCase;

import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

public class NCIACoreServiceHelperTestCase extends TestCase {

	private static final String DICOMDATASERVICE_PROPERTIES = "test/resources/nciaCoreServiceClientTest.properties";
	Properties serviceURLs = null;

	protected void setUp() throws Exception {
		super.setUp();

		FileInputStream in = new FileInputStream(DICOMDATASERVICE_PROPERTIES);
		serviceURLs = new Properties();
		serviceURLs.load(in);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testRetrieveDICOMDataOneSeries() {
		double t1 = System.currentTimeMillis()/1000.0;
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs.getProperty("dicomdataserviceMain") };

		final CQLQuery fcqlq = makeQuery("test/resources/useCase4.xml");
		
		NCIADataServiceHelper helper = new NCIADataServiceHelper();
		helper.retrieveDICOMData(fcqlq, urls[0], "tmp/TestDownloadDir");
		double t2 = System.currentTimeMillis()/1000.0;
		
		System.out.println("Time Taken = "+ (t2-t1));
		System.out.println(new File("tmp/TestDownloadDir").listFiles().length);
		//assertEquals(localLocation.listFiles().length, 79);

	}
	
	public void testRetrieveDICOMDataOneStudy() {
		double t1 = System.currentTimeMillis()/1000.0;
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs.getProperty("dicomdataserviceMain") };

		final CQLQuery fcqlq = makeQuery("test/resources/useCase3.xml");
		
		NCIADataServiceHelper helper = new NCIADataServiceHelper();
		helper.retrieveDICOMData(fcqlq, urls[0], "tmp/TestDownloadDir");
		double t2 = System.currentTimeMillis()/1000.0;
		
		System.out.println("Time Taken = "+ (t2-t1));
		System.out.println(new File("tmp/TestDownloadDir").listFiles().length);
		//assertEquals(localLocation.listFiles().length, 79);

	}
	public void testQueryDICOMDataOneSeries() {
		double t1 = System.currentTimeMillis()/1000.0;
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs.getProperty("dicomdataserviceMain") };

		final CQLQuery fcqlq = makeQuery("test/resources/useCase4.xml");
		
		NCIADataServiceHelper helper = new NCIADataServiceHelper();
		Iterator iter = helper.queryDICOMData(fcqlq, urls[0]);
		double t2 = System.currentTimeMillis()/1000.0;
		
		while (iter.hasNext()) {
			System.out.println((String) iter.next());
		}
		
		System.out.println("Time Taken = "+ (t2-t1));

	}
	
	public void testQueryDICOMDataOneStudy() {
		double t1 = System.currentTimeMillis()/1000.0;
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs.getProperty("dicomdataserviceMain") };

		final CQLQuery fcqlq = makeQuery("test/resources/useCase3.xml");
		
		NCIADataServiceHelper helper = new NCIADataServiceHelper();
		Iterator iter = helper.queryDICOMData(fcqlq, urls[0]);
		double t2 = System.currentTimeMillis()/1000.0;

		while (iter.hasNext()) {
			System.out.println((String) iter.next());
		}
		
		
		System.out.println("Time Taken = "+ (t2-t1));

	}

	private CQLQuery makeQuery(String filename) {
		CQLQuery newQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader(filename));
			newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
		} catch (FileNotFoundException e) {
			fail("test Query XML file not found");
		} catch (DeserializationException e) {
			fail("test Query XML file could not be deserialized");
		}
		assertNotNull(newQuery);
		return newQuery;
	}

	
/*
	public void testDICOMDataServiceHelperString() {
		fail("Not yet implemented"); // TODO
	}

	public void testMakeCQLQueryFromDICOMAttributes() {
		fail("Not yet implemented"); // TODO
	}

	public void testRetrieveDICOMDataAttributeListStringString() {
		fail("Not yet implemented"); // TODO
	}

	public void testRetrieveDICOMDataHashMapOfStringStringStringString() {
		fail("Not yet implemented"); // TODO
	}

	public void testRetrieveDICOMDataProgressively() {
		fail("Not yet implemented"); // TODO
	}

	public void testRetrieveDICOMDataCQLQueryStringString() {
		fail("Not yet implemented"); // TODO
	}

	public void testSubmitDICOMData() {
		fail("Not yet implemented"); // TODO
	}
*/
}
