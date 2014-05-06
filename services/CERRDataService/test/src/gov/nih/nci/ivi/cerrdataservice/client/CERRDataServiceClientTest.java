package gov.nih.nci.ivi.cerrdataservice.client;

import gov.nih.nci.ivi.cerrdataservice.client.CERRDataServiceClient;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;

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

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import junit.framework.TestCase;

public class CERRDataServiceClientTest extends TestCase {

	private CERRDataServiceClient cerrDataService;
	private static final String CERRDATASERVICE_PROPERTIES = "test/resources/cerrDataServiceClientTest.properties";
	Properties serviceURLs = null;
	String localDownloadLocation = System.getProperty("java.io.tmpdir")
			+ File.separator + "CerrDataServiceClientDownload";

	protected void setUp() throws Exception {
		super.setUp();

		FileInputStream in = new FileInputStream(CERRDATASERVICE_PROPERTIES);
		serviceURLs = new Properties();
		serviceURLs.load(in);
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testQueryAllCerrObjects() {
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("cerrdataserviceMain") };
		try {
			cerrDataService = new CERRDataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(cerrDataService);

		final CQLQuery fcqlq = makeQuery("test/resources/queries/queryAllCerrObjects.xml");

		CQLQueryResults result = null;
		try {
			result = cerrDataService.query(fcqlq);
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
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result);
		int ii = 1;
		while (iter2.hasNext()) {
			java.lang.Object obj = iter2.next();
			if (obj == null) {
				System.out.println("something not right.  obj is null");
				continue;
			}
			System.out.println("Result " + ii++ + ". ");

			try {
				System.out.println(ObjectSerializer.toString(obj, new QName(
						"gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", obj
								.getClass().getName())));
			} catch (SerializationException e) {

				fail("Error when serializing results");
			}
		}
		assertEquals(--ii, 1);
	}

	public void testRetrieveAllCerrObjects() {
		assertNotNull(serviceURLs);

		String[] urls = new String[] { serviceURLs
				.getProperty("cerrdataserviceMain") };
		try {
			cerrDataService = new CERRDataServiceClient(urls[0]);
		} catch (MalformedURIException e) {
			fail("Invalid Dataservice URL");
		} catch (RemoteException e) {
			fail("Unknown Remote Error");
		}
		assertNotNull(cerrDataService);

		final CQLQuery fcqlq = makeQuery("test/resources/queries/queryAllCerrObjects.xml");

		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			tclient = new TransferServiceContextClient(cerrDataService
					.retrieveCERRObjects(fcqlq).getEndpointReference());
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
		System.out.println(localLocation.listFiles().length);
		// assertEquals(localLocation.listFiles().length, 79);

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
