/*
 * **************************************************************
 * DICOMDataServiceTestCasesForAllClasses.java
 * Author : Dhananjaya Somanna
 * Version 1.0
 * **************************************************************
 */

package gov.nih.nci.ivi.dicomdataservice.client;

import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.utils.Zipper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.Properties;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

import submission.SubmissionInformation;

public class DICOMDataServiceTestCasesForAllClasses extends TestCase {

	static Logger logger = Logger
			.getLogger(DICOMDataServiceClientTestSYSTEMCase.class);

	ModelMap ncia_map = null;
	DICOMDataServiceClient dicomDataService = null;
	HashmapToCQLQuery h2cql = null;

	private static final String DICOMDATASERVICE_PROPERTIES = "test/resources/dicomDataServiceClientTest.properties";
	private static final String DICOM_QUERY_CORRECT_FILENAME = "test/resources/queryVirtualPacsSample.dcm";
	private static final String DICOM_QUERY_WRONG_FILENAME = "test/resources/queryVirtualPacsSampleWrong.dcm";

	String localDownloadLocation = System.getProperty("java.io.tmpdir")
			+ File.separator + "DicomDataServiceClientDownload";
	String localUploadLocation = System.getProperty("java.io.tmpdir")
			+ File.separator + "DicomDataServiceClientUpload";
	Properties serviceURLs = null;

	/**
	 * 
	 */
	protected void setUp() throws Exception {
		super.setUp();
		try {
			ncia_map = new ModelMap();
			h2cql = new HashmapToCQLQuery(ncia_map);
			DOMConfigurator.configure("xmllog4jconfig.xml");
		} catch (FileNotFoundException e1) {
			fail("Modelmap file not found");
		} catch (ModelMapException e1) {
			fail("Modelmap Exception");
		} catch (IOException e1) {
			fail("Modelmap IO Exception");
		} catch (ClassNotFoundException e1) {
			fail("Modelmap ClassNotFound Exception");
		}

		FileInputStream in = new FileInputStream(DICOMDATASERVICE_PROPERTIES);
		logger.info("Loading Service Properties");
		serviceURLs = new Properties();
		serviceURLs.load(in);
	}

	/**
	 * 
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * 
	 */
	public void testAnnotationQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Annotation_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);

		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed Annotation Query()");
	}

	/**
	 * 
	 */
	public void testClinicalTrialProtocolQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Clinical_Trial_Protocol_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testClinicalTrialProtocolQuery()");
	}

	/**
	 * 
	 */
	public void testClinicalTrialSiteQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Clinical_Trial_Site_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testClinicalTrialSiteQuery()");
	}

	/**
	 * 
	 */
	public void testClinicalTrialSponsorQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Clinical_Trial_Sponsor_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testClinicaltrialSponsorQuery()");
	}

	/**
	 * 
	 */
	public void testClinicalTrialSubjectQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Clinical_Trial_Subject_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testClinicalTrialSubjectQuery()");
	}

	/**
	 * 
	 */
	public void testCurationDataQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Curation_Data_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testCurationDataQuery()");
	}

	/**
	 * 
	 */
	public void testEquipmentQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Equipment_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testEquipmentQuery()");
	}

	/**
	 * 
	 */
	public void testImageQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Image_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed Image Query()");
	}

	/**
	 * 
	 */
	public void testPatientQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Patient_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testPatientQuery()");
	}

	/**
	 * 
	 */
	public void testSeriesQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Series_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testSeriesQuery()");
	}

	/**
	 * 
	 */
	public void testStudyQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Study_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed Study Query()");
	}

	/**
	 * 
	 */
	public void testTrialDataProvenanceQuery() {

		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };
		DataServiceClient dataService = null;
		try {
			dataService = new DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Trial_Data_Provenance_Query.xml");

		CQLQueryResults result = null;
		try {
			result = dataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);
		if (!iter2.hasNext()) {
			logger.info("Handle returned but no results returned");
		} else {
			int ii = 1;
			while (iter2.hasNext()) {
				java.lang.Object obj = iter2.next();
				if (obj == null) {
					logger.info("something not right.  obj is null");
					continue;
				}
				logger.info("Result " + ii++ + ". ");
				System.out.println("Result " + ii++ + ". " + obj.toString());
			}
		}
		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Time Taken = " + (t2 - t1));
		logger.info("Successfully completed testTrialDataProvenanceQuery()");
	}

	public void testSeriesRetrieve() {
		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };

		logger.info(Arrays.toString(urls));

		try {
			dicomDataService = new DICOMDataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dicomDataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Series_Retrieve.xml");

		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			TransferServiceContextReference temp = dicomDataService
					.retrieveDICOMData(fcqlq);
			EndpointReferenceType temp2 = temp.getEndpointReference();
			tclient = new TransferServiceContextClient(temp2);
			istream = TransferClientHelper.getData(tclient
					.getDataTransferDescriptor());
		} catch (MalformedURIException e) {
			fail("Malformed URI Exception Thrown");
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote Exception Thrown");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull("Input stream recieved from transfer service is null",
				istream);
		ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {
				fail("IOException thrown when recieving the zip stream");
			}

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists())
				localLocation.mkdirs();

			logger.info("Files downloaded to: " + localDownloadLocation);
			String unzzipedFile = localDownloadLocation + File.separator
					+ zeis.getName();
			bis = new BufferedInputStream(zeis);

			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(unzzipedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
					// logger.info(new String(data));
					// logger.info("caGrid transferred at " + new
					// Date().getTime());
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
				fail("IOException thrown when reading the zip stream");
			}
		}

		try {
			zis.close();
		} catch (IOException e) {
			fail("IOException thrown when closing the zip stream");
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception thrown when closing the transer context");
		}

		File localLocation = new File(localDownloadLocation);
		if (localLocation.listFiles() == null) {
			logger.info("No files were downloaded");
		} else {
			logger.info(localLocation.listFiles().length);
		}
		// assertEquals(localLocation.listFiles().length, 79);

		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Transfer Time = " + (t2 - t1));
	}

	public void testPatientRetrieve() {
		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };

		logger.info(Arrays.toString(urls));

		try {
			dicomDataService = new DICOMDataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dicomDataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Patient_Retrieve.xml");

		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			TransferServiceContextReference temp = dicomDataService
					.retrieveDICOMData(fcqlq);
			EndpointReferenceType temp2 = temp.getEndpointReference();
			tclient = new TransferServiceContextClient(temp2);
			istream = TransferClientHelper.getData(tclient
					.getDataTransferDescriptor());
		} catch (MalformedURIException e) {
			fail("Malformed URI Exception Thrown");
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote Exception Thrown");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull("Input stream recieved from transfer service is null",
				istream);
		ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {
				fail("IOException thrown when recieving the zip stream");
			}

			// logger.info(zeis.getName());

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists())
				localLocation.mkdirs();

			logger.info("Files downloaded to: " + localDownloadLocation);
			String unzzipedFile = localDownloadLocation + File.separator
					+ zeis.getName();
			bis = new BufferedInputStream(zeis);

			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(unzzipedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
					// logger.info(new String(data));
					// logger.info("caGrid transferred at " + new
					// Date().getTime());
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
				fail("IOException thrown when reading the zip stream");
			}
		}

		try {
			zis.close();
		} catch (IOException e) {
			fail("IOException thrown when closing the zip stream");
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception thrown when closing the transer context");
		}

		File localLocation = new File(localDownloadLocation);
		if (localLocation.listFiles() == null) {
			logger.info("No files were downloaded");
		} else {
			logger.info(localLocation.listFiles().length);
		}
		// assertEquals(localLocation.listFiles().length, 79);

		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Transfer Time = " + (t2 - t1));
	}

	public void testStudyRetrieve() {
		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);
		assertNotNull(h2cql);

		String[] urls = new String[] { serviceURLs
				.getProperty("dicomdataserviceMain") };

		logger.info(Arrays.toString(urls));

		try {
			dicomDataService = new DICOMDataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(dicomDataService);

		final CQLQuery fcqlq = makeQuery("test/resources/Study_Retrieve.xml");

		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			TransferServiceContextReference temp = dicomDataService
					.retrieveDICOMData(fcqlq);
			EndpointReferenceType temp2 = temp.getEndpointReference();
			tclient = new TransferServiceContextClient(temp2);
			istream = TransferClientHelper.getData(tclient
					.getDataTransferDescriptor());
		} catch (MalformedURIException e) {
			fail("Malformed URI Exception Thrown");
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote Exception Thrown");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}

		assertNotNull("Input stream recieved from transfer service is null",
				istream);
		ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {
				fail("IOException thrown when recieving the zip stream");
			}

			// logger.info(zeis.getName());

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists())
				localLocation.mkdirs();

			logger.info("Files downloaded to: " + localDownloadLocation);
			String unzzipedFile = localDownloadLocation + File.separator
					+ zeis.getName();
			bis = new BufferedInputStream(zeis);

			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(unzzipedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
					// logger.info(new String(data));
					// logger.info("caGrid transferred at " + new
					// Date().getTime());
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
				fail("IOException thrown when reading the zip stream");
			}
		}

		try {
			zis.close();
		} catch (IOException e) {
			fail("IOException thrown when closing the zip stream");
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception thrown when closing the transer context");
		}

		File localLocation = new File(localDownloadLocation);
		if (localLocation.listFiles() == null) {
			logger.info("No files were downloaded");
		} else {
			logger.info(localLocation.listFiles().length);
		}
		// assertEquals(localLocation.listFiles().length, 79);

		double t2 = System.currentTimeMillis() / 1000.0;
		logger.info("Transfer Time = " + (t2 - t1));
	}

	/**
	 * 
	 * @author CCI
	 * 
	 */
	public class FetchThread implements Runnable {

		TransferServiceContextReference tscr;

		FetchThread(TransferServiceContextReference fileURI) {
			tscr = fileURI;
		}

		public void run() {
			logger.info("Downloading file ");
			myGridFTPRetrieval(tscr);
		}

	}

	/**
	 * 
	 * @param tscr
	 */
	private void myGridFTPRetrieval(TransferServiceContextReference tscr) {

		TransferServiceContextClient tclient = null;
		InputStream istream = null;

		try {
			tclient = new TransferServiceContextClient(tscr
					.getEndpointReference());
			istream = TransferClientHelper.getData(tclient
					.getDataTransferDescriptor());
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		assertNotNull("Input stream recieved from transfer service is null",
				istream);

		ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {
				fail("IOException thrown when recieving the zip stream");
			}

			// logger.info(zeis.getName());

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists())
				localLocation.mkdirs();

			String unzzipedFile = localDownloadLocation + File.separator
					+ zeis.getName();
			bis = new BufferedInputStream(zeis);
			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(unzzipedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
					// logger.info(new String(data));
					// logger.info("caGrid transferred at " + new
					// Date().getTime());
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				e.printStackTrace();
				fail("IOException thrown when reading the zip stream");
			}
		}

		try {
			zis.close();
		} catch (IOException e) {
			fail("IOException thrown when closing the zip stream");
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			fail("Remote exception thrown when closing the transer context");
		}

		File localLocation = new File(localDownloadLocation);
		logger.info(localLocation.listFiles().length);

	}

	/**
	 * 
	 * @param filename
	 * @return
	 */
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

	/**
	 * 
	 * @param subInfo
	 * @return
	 */
	private String getSampleUploadData(SubmissionInformation subInfo) {
		String[] sampleFileNames = new String[2];
		sampleFileNames[0] = DICOM_QUERY_CORRECT_FILENAME;
		sampleFileNames[1] = DICOM_QUERY_WRONG_FILENAME;
		String localFileName = localUploadLocation + File.separator
				+ subInfo.getFileName();
		try {
			Zipper.zip(localFileName, sampleFileNames, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return localFileName;
	}

}
