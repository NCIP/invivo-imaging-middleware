package gov.nih.nci.ivi.helper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Properties;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import edu.emory.cci.aim.client.AIMTCGADataServiceClient;
import edu.northwestern.radiology.aim.ImageAnnotation;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;

public class AIMTCGADataServiceHelperTestCase extends TestCase {

	AIMTCGADataServiceClient aimDataService = null;
	private static final String AIMDATASERVICE_PROPERTIES = "test/resources/aimTCGADataServiceClientTest.properties";
	Properties serviceURLs = null;

	protected void setUp() throws Exception {
		super.setUp();

		FileInputStream in = new FileInputStream(AIMDATASERVICE_PROPERTIES);
		serviceURLs = new Properties();
		serviceURLs.load(in);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testDeserialization() {
		fail("this test needs to be updated");
		String localAimFile = "test/resources/AIMObjects/0023FollowupB.xml";
		FileReader reader = null;
		try {
			reader = new FileReader(localAimFile);
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		Object o = null;
		try {
			o = ObjectDeserializer.deserialize(new InputSource(reader), ImageAnnotation.class);
		} catch (DeserializationException e) {
			e.printStackTrace();
			fail("should not be here");
		}

		assertNotNull(o);
	}
	
	public void testAIMQuery() {
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("aimDataserviceMain") };

		AIMTCGADataServiceHelper helper = new AIMTCGADataServiceHelper();
//		CQLQuery cqlq = AIMTCGADataServiceHelper.generateImageAnnotationQuery("1.3.6.1.4.1.9328.50.46.7758667636001999163091261310647023809", 
//				"1.3.6.1.4.1.9328.50.46.116744760614299221617250951944115808458", "Adam Flanders");
		CQLQuery cqlq = AIMTCGADataServiceHelper.generateImageAnnotationQuery("1.3.6.1.4.1.9328.50.46.130563880911723253267280582465817207504", 
		"1.3.6.1.4.1.9328.50.46.236326650903196607542589296789154905463", null);
	
		try {
			System.out.println(ObjectSerializer.toString(cqlq, new QName(
					"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery",
					"CQLQuery")));
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			fail("exception thrown: " + e.getMessage());
		}

		try {
			Iterator iter2 = helper.queryAnnotations(cqlq, urls[0]);
			int ii = 0;
			while (iter2.hasNext()) {
				String xml = (String)iter2.next();
				
				System.out.println("xml: " + xml);
				
//				this will fail.  deserialization using AXIS is not working right.
//				java.lang.Object obj = iter2.next();
//				if (obj == null) {
//					System.out.println("something not right.  obj is null");
//					continue;
//				}
				System.out.println("Result " + ++ii + ". ");
				/*
				 * try { System.out.println(ObjectSerializer.toString(obj, new
				 * QName( "gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", obj
				 * .getClass().getName()))); } catch (SerializationException e) {
				 * fail("Error when serializing results"); }
				 */	
			}
			
		} catch (MalformedURIException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	}

	public void testRetrieveAnnotations() {
		double t1 = System.currentTimeMillis() / 1000.0;
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("aimDataserviceMain") };

//		final CQLQuery fcqlq = makeQuery("test/resources/queryAllAIMObjects.xml");
//		final CQLQuery fcqlq = makeQuery("test/resources/queryAllAIMForSingleSeries.xml");

		AIMTCGADataServiceHelper helper = new AIMTCGADataServiceHelper();
//		final CQLQuery fcqlq = AIMTCGADataServiceHelper.generateImageAnnotationQuery("1.3.6.1.4.1.9328.50.46.7758667636001999163091261310647023809", 
//		"1.3.6.1.4.1.9328.50.46.116744760614299221617250951944115808458", "Adam Flanders");
		final CQLQuery fcqlq = AIMTCGADataServiceHelper.generateImageAnnotationQuery("1.3.6.1.4.1.9328.50.46.130563880911723253267280582465817207504", 
		"1.3.6.1.4.1.9328.50.46.236326650903196607542589296789154905463", null);
		
		File localDownloadLocation = new File("tmp/TestAimDownloadDir");
		if (!localDownloadLocation.exists())
			localDownloadLocation.mkdirs();

		try {
			helper.retrieveAnnotations(fcqlq, urls[0], "tmp/TestAimDownloadDir");
		} catch (Exception e) {
			e.printStackTrace();
			fail("exception thrown: " + e.getMessage());
		}
		double t2 = System.currentTimeMillis() / 1000.0;

		System.out.println("Time Taken = " + (t2 - t1));
	}

	public void testSubmitAnnotations() {
		double t1 = System.currentTimeMillis() / 1000.0;
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("aimDataserviceMain") };

		AIMTCGADataServiceHelper helper = new AIMTCGADataServiceHelper();
		// this is the wrong file.  do not test.
		fail("this test needs to be udpated");
		
//		String localAimFile = "test/resources/AIMObjects/0023FollowupB.xml";
//
//		try {
//			helper.submitAnnotations(localAimFile, urls[0]);
//		} catch (Exception e) {
//			e.printStackTrace();
//			fail("exception thrown: " + e.getMessage());
//		}
//
//		double t2 = System.currentTimeMillis() / 1000.0;
//		System.out.println("Time Taken = " + (t2 - t1));
	}

	private CQLQuery makeQuery(String filename) {
		CQLQuery newQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader(filename));
			newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput,
					CQLQuery.class);
		} catch (FileNotFoundException e) {
			fail("test Query XML file not found");
		} catch (DeserializationException e) {
			fail("test Query XML file could not be deserialized");
		}
		assertNotNull(newQuery);
		return newQuery;
	}

}
