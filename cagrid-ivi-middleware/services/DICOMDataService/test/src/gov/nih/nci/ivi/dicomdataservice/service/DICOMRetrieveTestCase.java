package gov.nih.nci.ivi.dicomdataservice.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

public class DICOMRetrieveTestCase extends TestCase {

    private static final String QUERYPROCESSOR_PROPERTIES = "test/resources/queryProcessor.properties";
    // private static final String PIXELMED_PROPERTIES =
    // "test/resources/dicom/pixelmed.properties";
    Properties serviceProps = null;
    List<String> retrievedDicomFiles = null;

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
        if (retrievedDicomFiles != null && !retrievedDicomFiles.isEmpty()) {
            String parentDir1 = null;
            String parentDir2 = null;
            for (int index = 0; index < retrievedDicomFiles.size(); index++) {
                String fileName = retrievedDicomFiles.get(index);
                File dicomFileName = new File(fileName);
                parentDir1 = dicomFileName.getParent();
                dicomFileName.delete();
            }

            // TODO Check if files are downloaded in two nested directories or
            // just one
            File parentDir = new File(parentDir1);
            parentDir2 = parentDir.getParent();
            parentDir.delete();
            parentDir = new File(parentDir2);
            parentDir.delete();
        }
        super.tearDown();
    }

    public void testUnsecureRetrieveAllStudiesOnePatient() throws Exception {
        assertNotNull(serviceProps);

		String filename = "test/resources/useCase1.xml";
		CQLQuery newQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader(filename));
			newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
			System.out.println(ObjectSerializer.toString(newQuery,
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

        DICOMRetrieve dicomRet = new DICOMRetrieve(serviceProps, false, null, 0);
        retrievedDicomFiles = dicomRet.performRetrieve(fcqlq);

		System.out.println("No. files retrieved: "+retrievedDicomFiles.size());
		//assertEquals(retrievedDicomFiles.size(), 1);
	}

    public void testUnsecureRetrieveOneStudyOnePatient() throws Exception {
        assertNotNull(serviceProps);

        String filename = "test/resources/useCase2.xml";
        CQLQuery newQuery = null;
        try {
            InputSource queryInput = new InputSource(new FileReader(filename));
            newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
            System.err.println(ObjectSerializer.toString(newQuery, new QName(
                    "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
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

        DICOMRetrieve dicomRet = new DICOMRetrieve(serviceProps, false, null, 0);
        retrievedDicomFiles = dicomRet.performRetrieve(fcqlq);

        assertEquals(retrievedDicomFiles.size(), 1);
    }

    public void testUnsecureRetrieveAllSeriesOneStudy() throws Exception {
        assertNotNull(serviceProps);

        String filename = "test/resources/useCase4.xml";
        CQLQuery newQuery = null;
        try {
            InputSource queryInput = new InputSource(new FileReader(filename));
            newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
            System.err.println(ObjectSerializer.toString(newQuery, new QName(
                    "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
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

        DICOMRetrieve dicomRet = new DICOMRetrieve(serviceProps, false, null, 0);
        retrievedDicomFiles = dicomRet.performRetrieve(fcqlq);

        assertEquals(retrievedDicomFiles.size(), 1);
    }

    public void testUnsecureRetrieveOneSeries() throws Exception {
        assertNotNull(serviceProps);

        String filename = "test/resources/useCase4.xml";
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
        final CQLQuery fcqlq = newQuery;

        DICOMRetrieve dicomRet = new DICOMRetrieve(serviceProps, false, null, 0);
        retrievedDicomFiles = dicomRet.performRetrieve(fcqlq);

        assertEquals(retrievedDicomFiles.size(), 1);
    }

    /*
     * public void testSecurePatientRetrieve() { fail("To be Implemented"); }
     * 
     * public void testSecureStudyRetrieve() { fail("To be Implemented"); }
     * 
     * public void testSecureSeriesRetrieve() { fail("To be Implemented"); }
     * 
     * public void testSecureImageRetrieve() { fail("To be Implemented"); }
     */
}
