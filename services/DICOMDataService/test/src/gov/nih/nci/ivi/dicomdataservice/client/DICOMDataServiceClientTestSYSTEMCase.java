package gov.nih.nci.ivi.dicomdataservice.client;

import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationResponseHelper;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;

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
import java.io.Reader;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.apache.axis.types.Duration;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.Status;
import org.globus.gsi.GlobusCredential;
import org.globus.ws.enumeration.ClientEnumIterator;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import submission.SubmissionInformation;

////////////////////////////////////////////////////////////////////////////
//                                                                        //
// This client side test will not work with the version of xerces (2.7.1) //
// used in the server environment. It requires 2.9 or later.              //
//                                                                        //
////////////////////////////////////////////////////////////////////////////

public class DICOMDataServiceClientTestSYSTEMCase extends DICOMDataServiceAbstractSecureTest {
    ModelMap ncia_map = null;
    DICOMDataServiceClient dicomDataService = null;
    HashmapToCQLQuery h2cql = null;

    private static final String DICOM_QUERY_CORRECT_FILENAME = "test/resources/queryVirtualPacsSample.dcm";
    private static final String DICOM_QUERY_WRONG_FILENAME = "test/resources/queryVirtualPacsSampleWrong.dcm";

    // Credentials to use for this test
    private static GlobusCredential bogus1Credential;
//    private static GlobusCredential bogus2Credential;
//    private static GlobusCredential bogus3Credential;

    String localDownloadLocation = System.getProperty("java.io.tmpdir") + File.separator
            + "DicomDataServiceClientDownload";
    /**
     * If the credentials needed for secure tested have not yet been obtained,
     * then obtain them.
     */
    private static void ensureCredentialsAreReady() throws Exception {
        if (bogus1Credential == null) {
            // System.setProperty("javax.xml.parsers.DocumentBuilderFactory",
            // "javax.xml.parsers.DocumentBuilderFactory");
            System.out.println("Getting Credentials");
            bogus1Credential = getBogus1Credential();
            //bogus2Credential = login(IDP_URL, IFS_URL, "bogus2", "pswd123456X$");
            //bogus3Credential = login(IDP_URL, IFS_URL, "bogus3", "pswd123456X$");
        } else {
            System.out.println("Using existing credentials");
        }
    }

    public void setUp() throws Exception {
        super.setUp();
        System.out.println("Beginning setUp");
        ncia_map = new ModelMap();
        h2cql = new HashmapToCQLQuery(ncia_map);

        ensureCredentialsAreReady();
        System.out.println("Ending setUp");
    }

    protected void tearDown() throws Exception {
        super.tearDown();
    }

    public void testSubmitOneSeries() throws Exception {
        enterTest("testSubmitOneSeries.");
    
        assertNotNull(ncia_map);
        assertNotNull(h2cql);
    
        SubmissionInformation subInfo = new SubmissionInformation();
        subInfo.setFileName("Test.zip");
        subInfo.setFileType("zip");
        String submitFileName = getSampleUploadData(subInfo);
    
        doSubmit(bogus1Credential, subInfo, submitFileName);
    
        exitTest("testSubmitOneSeries.");
    }
    
    public void testSubmitMore() throws Exception {
        enterTest("testSubmitMore.");

        SubmissionInformation subInfo = new SubmissionInformation();
        subInfo.setFileName("data.zip");
        subInfo.setFileType("zip");
        String[] fileNames = {
                "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2359/1.3.6.1.4.1.9328.50.1.2358.dcm",
                "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2359/1.3.6.1.4.1.9328.50.1.2360.dcm",
                "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2359/1.3.6.1.4.1.9328.50.1.2361.dcm",
                "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2439/1.3.6.1.4.1.9328.50.1.2438.dcm",
                "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2439/1.3.6.1.4.1.9328.50.1.2440.dcm"
        };
        String submitFileName = getUploadData(subInfo, fileNames);
    
        doSubmit(bogus1Credential, subInfo, submitFileName);
        
        exitTest("testSubmitMore.");
    }

    /**
     * @param cred
     * @param subInfo
     * @param submitFileName
     * @throws MalformedURIException
     * @throws RemoteException
     * @throws Exception
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void doSubmit(GlobusCredential cred, SubmissionInformation subInfo, String submitFileName)
            throws MalformedURIException, RemoteException, Exception, FileNotFoundException, IOException {
        boolean secure = isSecure(serviceUrl);
        dicomDataService = new DICOMDataServiceClient(serviceUrl);
        assertNotNull(dicomDataService);
    
        TransferServiceContextClient tclient = null;
        if (secure) {
            ProxyUtil.saveProxyAsDefault(cred);
        }
        System.out.println("Sending submit request to DICOM service.");
        tclient = new TransferServiceContextClient(dicomDataService.submitDICOMData(subInfo).getEndpointReference());
        assertNotNull(tclient);
    
        File transferDoc = new File(submitFileName);
        System.out.println("Submiting file to transfer service: " + transferDoc);
        BufferedInputStream dicomIn = new BufferedInputStream(new FileInputStream(transferDoc));
        if (secure) {
            TransferClientHelper.putData(dicomIn, transferDoc.length(), tclient.getDataTransferDescriptor(), cred);
        } else {
            TransferClientHelper.putData(dicomIn, transferDoc.length(), tclient.getDataTransferDescriptor());
        }
        dicomIn.close();
    
        tclient.setStatus(Status.Staged);
    }

    // get metadata only
    public void testImageQuery() throws Exception {
        enterTest("Entering testImageQuery.");
        String queryString  = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:CQLQuery xmlns:ns1=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"
            + " <ns1:Target name=\"gov.nih.nci.ncia.domain.Image\" xsi:type=\"ns1:Object\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "  <ns1:Group logicRelation=\"AND\" xsi:type=\"ns1:Group\">\n"
            + "   <ns1:Association name=\"gov.nih.nci.ncia.domain.Series\" roleName=\"series\" xsi:type=\"ns1:Association\">\n"
            + "    <ns1:Group logicRelation=\"AND\" xsi:type=\"ns1:Group\">\n"
            + "     <ns1:Association name=\"gov.nih.nci.ncia.domain.Study\" roleName=\"study\" xsi:type=\"ns1:Association\">\n"
            + "      <ns1:Group logicRelation=\"AND\" xsi:type=\"ns1:Group\">\n"
            + "       <ns1:Association name=\"gov.nih.nci.ncia.domain.Patient\" roleName=\"patient\" xsi:type=\"ns1:Association\">\n"
            + "        <ns1:Attribute name=\"patientId\" predicate=\"EQUAL_TO\" value=\"1.3.6.1.4.1.9328.50.1.0004\" xsi:type=\"ns1:Attribute\"/>\n"
            + "       </ns1:Association>\n"
            + "       <ns1:Attribute name=\"studyInstanceUID\" predicate=\"EQUAL_TO\" value=\"1.3.6.1.4.1.9328.50.1.2354\" xsi:type=\"ns1:Attribute\"/>\n"
            + "      </ns1:Group>"
            + "     </ns1:Association>"
            + "     <ns1:Attribute name=\"seriesInstanceUID\" predicate=\"EQUAL_TO\" value=\"1.3.6.1.4.1.9328.50.1.2359\" xsi:type=\"ns1:Attribute\"/>\n"
            + "    </ns1:Group>\n"
            + "   </ns1:Association>"
            + "  </ns1:Group>\n"
            + " </ns1:Target>\n"
            + "</ns1:CQLQuery>";
        testQuery(queryString, 3);
        exitTest("testImageQuery");
    }

    public void testSeriesQuery() throws Exception {
        enterTest("Entering testImageQuery.");
        String queryString  = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:CQLQuery xmlns:ns1=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"
            + " <ns1:Target name=\"gov.nih.nci.ncia.domain.Series\" xsi:type=\"ns1:Object\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + "  <ns1:Group logicRelation=\"AND\" xsi:type=\"ns1:Group\">\n"
            + "   <ns1:Association name=\"gov.nih.nci.ncia.domain.Study\" roleName=\"study\" xsi:type=\"ns1:Association\">\n"
            + "    <ns1:Group logicRelation=\"AND\" xsi:type=\"ns1:Group\">\n"
            + "     <ns1:Association name=\"gov.nih.nci.ncia.domain.Patient\" roleName=\"patient\" xsi:type=\"ns1:Association\">\n"
            + "      <ns1:Attribute name=\"patientId\" predicate=\"EQUAL_TO\" value=\"1.3.6.1.4.1.9328.50.1.0004\" xsi:type=\"ns1:Attribute\"/>\n"
            + "     </ns1:Association>\n"
            + "     <ns1:Attribute name=\"studyInstanceUID\" predicate=\"EQUAL_TO\" value=\"1.3.6.1.4.1.9328.50.1.2354\" xsi:type=\"ns1:Attribute\"/>\n"
            + "    </ns1:Group>"
            + "   </ns1:Association>"
            + "  </ns1:Group>\n"
            + " </ns1:Target>\n"
            + "</ns1:CQLQuery>";
        testQuery(queryString, 2);
        exitTest("testImageQuery");
    }

    /**
     * @param queryString
     * @throws Exception
     */
    private void testQuery(String queryString, int expectedCount) throws Exception {
        try {
            DICOMDataServiceClient dataService = null;
            // dataService = new DataServiceClient(serviceUrl);
            dataService = new DICOMDataServiceClient(serviceUrl);
            assertNotNull(dataService);

            final CQLQuery fcqlq = makeQuery(queryString);

            CQLQueryResults result = null;
            ProxyUtil.saveProxyAsDefault(bogus1Credential);
            
            System.out.println("Submitting query.");
            result = dataService.query(fcqlq);
            System.out.println("Received result.");

            assertNotNull(result);
            CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result);
            int ii = 0;
            while (iter2.hasNext()) {
                java.lang.Object obj = iter2.next();
                if (obj == null) {
                    System.out.println("something not right.  obj is null");
                    continue;
                }
                ii += 1;
                System.out.println("Result " + ii + " is " + obj);
            }
            System.out.println("Result contained " + ii + " objects.");
            assertEquals("Received wrong number of results.", expectedCount, ii);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public void testRetrieveOneSeries() throws Exception {
        enterTest("testRetrieveOneSeries");

        assertNotNull(ncia_map);
        assertNotNull(h2cql);

        dicomDataService = new DICOMDataServiceClient(serviceUrl);
        assertNotNull(dicomDataService);

//        if (isUnsecure(serviceUrl)) {
//            testUnsecureRetrieveOneSeries(serviceUrl);
//        } else if (isSecure(serviceUrl)) {
            testSecureRetrieveOneSeries(serviceUrl);
//        } else {
//            fail("should be testing with http or https url");
//        }
        exitTest("testRetrieveOneSeries");
    }

    private void testUnsecureRetrieveOneSeries(String url) throws Exception {
        final CQLQuery fcqlq = makeQuery(new File("test/resources/useCase4.xml"));

        InputStream istream = null;
        TransferServiceContextClient tclient = null;
        tclient = new TransferServiceContextClient(dicomDataService.retrieveDICOMData(fcqlq).getEndpointReference());
        istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());

        assertNotNull("Input stream recieved from transfer service is null", istream);
        ZipInputStream zis = new ZipInputStream(istream);
        ZipEntryInputStream zeis = null;
        BufferedInputStream bis = null;
        while (true) {
            try {
                zeis = new ZipEntryInputStream(zis);
            } catch (EOFException e) {
                break;
            }

            // System.out.println(zeis.getName());

            File localLocation = new File(localDownloadLocation);
            if (!localLocation.exists())
                localLocation.mkdirs();

            String unzzipedFile = localDownloadLocation + File.separator + zeis.getName();
            bis = new BufferedInputStream(zeis);
            // do something with the content of the inputStream

            byte[] data = new byte[8192];
            int bytesRead = 0;
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile));
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

        zis.close();
        tclient.destroy();

        File localLocation = new File(localDownloadLocation);
        if (!localLocation.exists()) {
            localLocation.mkdirs();
        }
        System.out.println(localLocation.listFiles().length);
        // assertEquals(localLocation.listFiles().length, 79);
    }

    private void testSecureRetrieveOneSeries(String url) throws Exception {
        enterTest("testSecureRetrieveOneSeries");
        GlobusCredential cred = bogus1Credential;

        final CQLQuery fcqlq = makeQuery(new File("test/resources/useCase4.xml"));

        InputStream istream = null;
        ProxyUtil.saveProxyAsDefault(cred);
        TransferServiceContextClient tclient = new TransferServiceContextClient(dicomDataService.retrieveDICOMData(
                fcqlq).getEndpointReference());
        istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor(), cred);

        assertNotNull("Input stream recieved from transfer service is null", istream);
        ZipInputStream zis = new ZipInputStream(istream);
        ZipEntryInputStream zeis = null;
        BufferedInputStream bis = null;
        while (true) {
            try {
                zeis = new ZipEntryInputStream(zis);
            } catch (EOFException e) {
                break;
            }

            // System.out.println(zeis.getName());

            File localLocation = new File(localDownloadLocation);
            if (!localLocation.exists())
                localLocation.mkdirs();

            String unzzipedFile = localDownloadLocation + File.separator + zeis.getName();
            bis = new BufferedInputStream(zeis);
            // do something with the content of the inputStream

            byte[] data = new byte[8192];
            int bytesRead = 0;

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile));
            while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
                bos.write(data, 0, bytesRead);
                // System.out.println(new String(data));
                // System.out.println("caGrid transferred at " + new
                // Date().getTime());
            }
            bos.flush();
            bos.close();
        }

        zis.close();
        tclient.destroy();

        File localLocation = new File(localDownloadLocation);
        if (!localLocation.exists()) {
            localLocation.mkdirs();
        }
        File[] localFiles = localLocation.listFiles();
        System.out.println(localLocation.getCanonicalPath() + " contains "
                + (localFiles == null ? 0 : localFiles.length) + " files.");
        // assertEquals(localLocation.listFiles().length, 79);
        exitTest("testSecureRetrieveOneSeries");
    }

    public void testUnsecureProgressiveRetrieveOneSeries() throws Exception {
        enterTest("testUnsecureProgressiveRetrieveOneSeries");
        assertNotNull(h2cql);

        try {
            dicomDataService = new DICOMDataServiceClient(serviceUrl);
        } catch (MalformedURIException e) {
            fail("Invalid Dataservice URL");
        } catch (RemoteException e) {
            fail("Unknown Remote Error");
        }
        assertNotNull(dicomDataService);

        final CQLQuery fcqlq = makeQuery(new File("test/resources/useCase4.xml"));

        ClientEnumIterator iterator = null;
        EnumerationResponseContainer enumerateResponse = dicomDataService.retrieveDICOMDataProgressively(fcqlq);
        // EnumerationContextType enumerationContext =
        // enumerateResponse.getContext();
        // ClientEnumIterator iterator = new ClientEnumIterator(epr,
        // enumerationContext);
        //			
        iterator = EnumerationResponseHelper.createClientIterator(enumerateResponse, DICOMDataServiceClient.class
                .getResourceAsStream("client-config.wsdd"));
        assertNotNull(iterator);

        // EnumerateResponse iterator = epr.createEnumeration();
        Duration dur = new Duration();
        dur.setHours(1);
        // IterationConstraints constraints = new IterationConstraints(10, -1,
        // dur);
        // iterator.setIterationConstraints(constraints);
        // QName qname = new
        // QName("http://transfer.cagrid.org/TransferService/Context/types",
        // "TransferServiceContextReference");
        int threadCount = 0;
        boolean thereAreItems = true;
        Thread[] fetchThreads = null;
        boolean isFetchThreaded = false;
        if (isFetchThreaded) {
            fetchThreads = new Thread[1000];
        }

        while (thereAreItems) {
            try {
                while (iterator.hasNext()) {
                    SOAPElement element = (SOAPElement) iterator.next();
                    TransferServiceContextReference fileURI = (TransferServiceContextReference) ObjectDeserializer
                            .toObject(element, TransferServiceContextReference.class);
                    TransferServiceContextClient tclient = new TransferServiceContextClient(fileURI
                            .getEndpointReference());

                    if (true) {
                        if (isFetchThreaded) {

                            fetchThreads[threadCount] = this.startFetchThread(fileURI);
                            threadCount++;
                            // workerPool.execute(createFetchThread(fileURI,
                            // user, encryption, signature));
                        } else
                            myGridFTPRetrieval(fileURI);
                    }
                }
                thereAreItems = false;
            } catch (NoSuchElementException e) {
                e.printStackTrace();
                thereAreItems = false;
            } catch (DeserializationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        // iterator.release();
        exitTest("testUnsecureProgressiveRetrieveOneSeries");
    }

    private Thread startFetchThread(TransferServiceContextReference fileURI) {
        Thread t = new Thread(new FetchThread(fileURI));
        t.start();
        return t;
    }

    public void testSimpleQueryAllPatients() throws Exception {
        enterTest("testSimpleQueryAllPatients");
        assertNotNull(ncia_map);
        assertNotNull(h2cql);

        dicomDataService = new DICOMDataServiceClient(serviceUrl);

        assertNotNull(dicomDataService);

        String queryString  = 
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
            + "<ns1:CQLQuery xmlns:ns1=\"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery\">\n"
            + " <ns1:Target name=\"gov.nih.nci.ncia.domain.Series\" xsi:type=\"ns1:Object\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\">\n"
            + " </ns1:Target>\n"
            + "</ns1:CQLQuery>";
        System.out.println("Executing query: " + queryString);
        final CQLQuery fcqlq = makeQuery(queryString);

        CQLQueryResults result = null;
        result = dicomDataService.query(fcqlq);

        assertNotNull(result);
        CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result);
        int ii = 0;
        while (iter2.hasNext()) {
            ii += 1;
            Object obj = iter2.next();
            if (obj == null) {
                System.out.println("something not right.  obj is null");
                continue;
            }
            System.out.println("Result " + ii + ". ");
            try {
                System.out.println(ObjectSerializer.toString(obj, new QName(
                        "gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", obj.getClass().getName())));
            } catch (SerializationException e) {
                fail("Error when serializing results");
            }
        }
        assertEquals(10, ii);
        exitTest("testSimpleQueryAllPatients");
    }

    public class FetchThread implements Runnable {

        TransferServiceContextReference tscr;

        FetchThread(TransferServiceContextReference fileURI) {
            tscr = fileURI;
        }

        public void run() {
            System.out.println("Downloading file ");
            myGridFTPRetrieval(tscr);
        }

    }

    private void myGridFTPRetrieval(TransferServiceContextReference tscr) {

        TransferServiceContextClient tclient = null;
        InputStream istream = null;

        try {
            tclient = new TransferServiceContextClient(tscr.getEndpointReference());
            istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
        } catch (RemoteException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        assertNotNull("Input stream recieved from transfer service is null", istream);

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

            String unzzipedFile = localDownloadLocation + File.separator + zeis.getName();
            bis = new BufferedInputStream(zeis);
            // do something with the content of the inputStream

            byte[] data = new byte[8192];
            int bytesRead = 0;
            try {
                BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile));
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
        if (!localLocation.exists()) {
            localLocation.mkdirs();
        }
        System.out.println(localLocation.listFiles().length);

    }

    private CQLQuery makeQuery(File filename) throws Exception {
        System.out.println("Making query from contents of " + filename.getAbsolutePath() + ":");
        writeFileToStandardOutput(filename);
        System.out.println();
        Reader reader = new FileReader(filename);
        return makeQuery(reader);
    }

    private CQLQuery makeQuery(String queryString) throws Exception {
        System.out.println("Making query:");
        System.out.println(queryString);
        return makeQuery(new StringReader(queryString));
    }

    /**
     * @param reader
     * @return
     * @throws DeserializationException
     */
    private CQLQuery makeQuery(Reader reader) throws DeserializationException {
        InputSource queryInput = new InputSource(reader);
        CQLQuery newQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
        assertNotNull(newQuery);
        return newQuery;
    }
    
    private void writeFileToStandardOutput(File filename) throws Exception {
        InputStream reader = new BufferedInputStream(new FileInputStream(filename), 10000);
        StringBuilder builder = new StringBuilder(20000);
        while (true) {
            int nextChar = reader.read();
            if (nextChar < 0) {
                break;
            }
            builder.append((char)nextChar);
        }
        System.out.println(builder.toString());
        reader.close();
    }

    private String getSampleUploadData(SubmissionInformation subInfo) throws Exception {
        String[] sampleFileNames = new String[2]; // DICOM files to be submitted
        sampleFileNames[0] = DICOM_QUERY_CORRECT_FILENAME;
        sampleFileNames[1] = DICOM_QUERY_WRONG_FILENAME;
        return getUploadData(subInfo, sampleFileNames);
    }

    /**
     * @param subInfo
     * @param fileNames
     * @return
     * @throws FileNotFoundException
     * @throws IOException
     */
    private String getUploadData(SubmissionInformation subInfo, String[] fileNames) throws FileNotFoundException,
            IOException {
        File localLocation = new File(localUploadLocation);
        if (!localLocation.exists()) {
            localLocation.mkdirs();
        }
        String localFileName = localUploadLocation + File.separator + subInfo.getFileName();
        System.out.println("Creating .zip file for upload: " + localFileName);
        zip(fileNames, localFileName);
        return localFileName;
    }

}
