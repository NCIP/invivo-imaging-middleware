package gov.nih.nci.ivi.imagedataservice.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class ImageRetrieveTestCase extends TestCase {

	private static final String QUERYPROCESSOR_PROPERTIES = "test/resources/queryProcessor.properties";
	Properties serviceProps = null;
	Vector <String> retrievedImageFiles = null;

	protected void setUp() throws Exception {
		super.setUp();
		try {
			FileInputStream in = new FileInputStream(QUERYPROCESSOR_PROPERTIES);
			serviceProps = new Properties();
			serviceProps.load(in);
			in.close();
		} catch (FileNotFoundException e) {
			fail("queryProcessor properties file not found");
		} catch (IOException e) {
			fail("queryProcessor property file load failed");
			e.printStackTrace();
		}
	}

	protected void tearDown() throws Exception {
/*
		if(retrievedImageFiles != null && !retrievedImageFiles.isEmpty())
		{
			String parentDir1 = null;
			String parentDir2 = null;
			for(int index = 0; index < retrievedImageFiles.size(); index++) {
				String fileName = retrievedImageFiles.get(index);
				File dicomFileName = new File(fileName);
				parentDir1 = dicomFileName.getParent();
				dicomFileName.delete();
			}
			//TODO Check if files are downloaded in two nested directories or just one
			File parentDir = new File(parentDir1);
			parentDir2 = parentDir.getParent();
			parentDir.delete();
			parentDir = new File(parentDir2);
			parentDir.delete();
		}
		super.tearDown();
		*/
	}

	public void testPerformRetrieve() {
		String filename = "test/resources/genericImageFSCQL3.xml";
		CQLQuery newQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader(filename));
			newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
			System.err.println(ObjectSerializer.toString(newQuery,
			new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (FileNotFoundException e) {
			fail("test Query XML file not found");
		} catch (DeserializationException e) {
			fail("test Query XML file could not be deserialized");
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull(newQuery);
		final CQLQuery fcqlq = newQuery;
		
		ImageRetrieve imgRet = new ImageRetrieve(serviceProps, true);
		retrievedImageFiles = imgRet.performRetrieve(fcqlq);
	}

}
