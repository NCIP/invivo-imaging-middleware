package edu.osu.bmi.ivi.aim.client;

import edu.emory.cci.aim.client.AIM3DataServiceClient;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

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
import java.util.Properties;
import java.util.zip.ZipInputStream;

import junit.framework.TestCase;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.descriptor.Status;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

public class AIMDataServiceClientTestCase extends TestCase {

	private AIM3DataServiceClient aimDataService;
	private static final String AIMDATASERVICE_PROPERTIES = "test/resources/aimDataServiceClientTest.properties";
	Properties serviceURLs = null;
	String localDownloadLocation = System.getProperty("java.io.tmpdir")
	+ File.separator + "AimDataServiceClientDownload";

	protected void setUp() throws Exception {
		super.setUp();

		FileInputStream in = new FileInputStream(AIMDATASERVICE_PROPERTIES);
		serviceURLs = new Properties();
		serviceURLs.load(in);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testQueryAllAimObjects() {
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("aimdataserviceMain") };
		try {
			aimDataService = new AIM3DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(aimDataService);

		final CQLQuery fcqlq = makeQuery("test/resources/RetrieveAll.xml");

		CQLQueryResults result = null;
		try {
			result = aimDataService.query(fcqlq);
		} catch (QueryProcessingExceptionType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MalformedQueryExceptionType e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		assertNotNull(result);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,false);
		int ii = 1;
		while (iter2.hasNext()) {
			java.lang.Object obj = iter2.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}
			System.out.println("Result " + ii++ + ". ");
			/*
			 * try { System.out.println(ObjectSerializer.toString(obj, new
			 * QName( "gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", obj
			 * .getClass().getName()))); } catch (SerializationException e) {
			 * fail("Error when serializing results"); }
			 * */
			 }
		//assertEquals(--ii, 10);
	}

	public void testSubmitOneAimObject() {
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("aimdataserviceMain") };
		try {
			aimDataService = new AIM3DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(aimDataService);
		String retrievedFile = "test/resources/Data.zip";

		TransferServiceContextClient tclient = null;
		try {
			tclient = new TransferServiceContextClient(aimDataService
					.submitByTransfer().getEndpointReference());
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		assertNotNull(tclient);

		try {
			File transferDoc = new File(retrievedFile);
			System.out.println("transferDoc is " + transferDoc);
			BufferedInputStream aimIn = new BufferedInputStream(
					new FileInputStream(transferDoc));
			TransferClientHelper.putData(aimIn, transferDoc.length(), tclient
					.getDataTransferDescriptor());
			aimIn.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			fail("Aim File not found");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} catch (Exception e) {
			fail();
		}

		try {
			tclient.setStatus(Status.Staged);
		} catch (RemoteException e) {
			e.printStackTrace();
			fail();
		}

	}

	public void testRetrieve() {
		double t1 = System.currentTimeMillis() / 1000.0;

		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("aimdataserviceMain") };
		try {
			aimDataService = new AIM3DataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(aimDataService);
		final CQLQuery fcqlq = makeQuery("test/resources/RetrieveAll.xml");

		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			tclient = new TransferServiceContextClient(aimDataService
					.queryByTransfer(fcqlq).getEndpointReference());
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

			// System.out.println(zeis.getName());

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists())
				localLocation.mkdirs();

			String unzzipedFile = localDownloadLocation + File.separator
					+ zeis.getName() + ".xml";
			bis = new BufferedInputStream(zeis);
			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(unzzipedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
					// System.out.println(new String(data));
					// System.out.println("caGrid transferred at " + new
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
		System.out.println(localLocation);
		System.out.println(localLocation.listFiles().length);
		// assertEquals(localLocation.listFiles().length, 79);

		double t2 = System.currentTimeMillis() / 1000.0;
		System.out.println("Transfer Time = " + (t2 - t1));
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

	/*
	 * private String getSampleUploadData() { String [] sampleFileNames = new
	 * String[1]; sampleFileNames[0] = AIM_QUERY_CORRECT_FILENAME; String
	 * localFileName = localUploadLocation + File.separator +
	 * subInfo.getFileName(); try { Zipper.zip(localFileName, sampleFileNames,
	 * false); } catch (FileNotFoundException e) { e.printStackTrace(); } catch
	 * (IOException e) { e.printStackTrace(); } return localFileName; }
	 */

}
