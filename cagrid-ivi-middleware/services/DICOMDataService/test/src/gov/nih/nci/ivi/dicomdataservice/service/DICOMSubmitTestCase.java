package gov.nih.nci.ivi.dicomdataservice.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.Vector;

import junit.framework.TestCase;

public class DICOMSubmitTestCase extends TestCase {

	private static final String QUERYPROCESSOR_PROPERTIES = "test/resources/queryProcessor.properties";
	private static final String PIXELMED_PROPERTIES = "test/resources/dicom/pixelmed.properties";
	Properties serviceProps = null;
	Vector <String> submittedDicomFiles = null;

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
		super.tearDown();
	}
	
/*	public void testSubmitOneSeries()
	{
		fail("To be implemented");
	}

	public void testSubmitOneStudy()
	{
		fail("To be implemented");
	}

	public void testSubmitOnePatient()
	{
		fail("To be implemented");
	}
*/
}
