package gov.nih.nci.ivi.dicomdataservice.service;

import edu.osu.bmi.utils.io.zip.ZipEntryOutputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;
import gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer;
import gov.nih.nci.cagrid.wsenum.utils.EnumerateResponseFactory;
import gov.nih.nci.cagrid.wsenum.utils.EnumerationCreationException;
import gov.nih.nci.ivi.dicom.DICOMCQLQueryProcessor;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.util.List;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/**
 * TODO:I am the service side implementation class. IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class DICOMDataServiceImpl extends DICOMDataServiceImplBase {
    private static final Logger myLogger = LogManager.getLogger(DICOMDataServiceImpl.class);

    /**
     * The base name to use for threads that send .zip files containing
     * retrieved images to the client.
     */
    private static final String ZIP_FILE_SENDER = "zip-sender-";

    /**
     * Used to put a serial number on thread names for writing .zip files from
     * the transfer service.
     */
    private static int zipOutputWriterCount = 0;

    private boolean authOn;

    private Properties dicomQueryProcProps;

    public DICOMDataServiceImpl() throws RemoteException {
        super();
        // auditSrvHelper = new
        // gov.nih.nci.ivi.auditdataservice.context.service.helper.AuditServiceHelper();
        // localDicomAuditor = new DICOMAuditor();
        // boolean useCMOVE = false;
        try {
            String useCMOVEs = DICOMDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_useCMOVE();
            if (useCMOVEs.equals("true")) {
                // useCMOVE = true;
            } else {
                // useCMOVE = false;
            }
        } catch (Exception e) {
            String msg = "Property useCMOVE was not property configured";
            myLogger.fatal(msg, e);
            throw new RemoteException(msg, e);
        }
        dicomQueryProcProps = fetchQueryProcProps();
        authOn = isDataLevelAuthorization(dicomQueryProcProps);
        myLogger.info("Use Data level authorization is " + authOn);
    }

    /*
     * private boolean checkEmbeddedPacs() { boolean isDown = true; try { new
     * com.pixelmed.network.VerificationSOPClassSCU(null, 0, null, null, false,
     * 0); } catch (com.pixelmed.network.DicomNetworkException e) { isDown =
     * false; } catch (com.pixelmed.dicom.DicomException e) {
     * e.printStackTrace(); } catch (IOException e) { e.printStackTrace(); }
     * return isDown; }
     */
    // private void startEmbeddedPACS() throws RemoteException {
    // String username = System.getenv("LOGNAME");
    // if (username == null) {
    // username = "";
    // } else {
    // username = "-" + username;
    // }
    // String tmpdir = System.getProperty("java.io.tmpdir");
    // myLogger.debug("tmpdir is " + tmpdir);
    // String dumpdir = (tmpdir + java.io.File.separator +
    // "DICOMCQLQueryProcessor-embedded" + username);
    // myLogger.debug("dumpdir " + dumpdir);
    // if (!new java.io.File(dumpdir).exists()) {
    // myLogger.debug("making dump dir " + dumpdir);
    // java.io.File f;
    //
    // f = new java.io.File(dumpdir);
    // f.mkdir();
    // assert (f.exists());
    //
    // f = new java.io.File((dumpdir + java.io.File.separator + "db"));
    // f.mkdir();
    // assert (f.exists());
    // }
    //
    // try {
    // myLogger.debug("starting embedded pacs...");
    // Properties prop = gov.nih.nci.cagrid.data.service.ServiceConfigUtil
    // .getQueryProcessorConfigurationParameters();
    // this.epacs = new EmbeddedPACS(dumpdir, prop);
    // this.epacs.start();
    // } catch (Exception ex) {
    // String msg = "Error starting embedded PACS";
    // myLogger.error(msg, ex);
    // this.epacs.stop();// xTODO get rid of the call to stop
    // throw new RemoteException("starting embedded pacs", ex);
    // }
    //
    // }
    public TransferServiceContextReference retrieveDICOMData(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery)
            throws RemoteException {
        final String user = authOn ? UserUtil.getUserId() : null;
        DICOMRetrieve dicomRet = new DICOMRetrieve(dicomQueryProcProps, authOn, user, 0);
        final List<String> retrievedFiles = dicomRet.performRetrieve(cQLQuery);
        if (myLogger.isInfoEnabled()) {
            myLogger.info("Retrieved " + retrievedFiles.size() + " files.");
        }
        // Step 2: We transfer the data

        // set up the piped streams
        final PipedOutputStream pos = new PipedOutputStream();
        PipedInputStream pis = new PipedInputStream();
        try {
            pis.connect(pos);
        } catch (Exception e) {
            String msg = "Unable to make a pipe";
            myLogger.error(msg, e);
            throw new RemoteException(msg, e);
        }
        myLogger.debug("Pipe successfully created.");

        // The part below needs to be threaded, since the transfer service
        // creation reads from the stream completely.
        Thread t = new Thread(computeZipFileSenderThreadName()) {

            @Override
            public void run() {
                // write to the output stream using a zip output stream.
                long t1 = System.currentTimeMillis() / 1000;

                int entryCount = 0; // The number of entries that have been
                                    // written to the .zip file.

                ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(pos, 8192));
                myLogger.debug("ZipOutputStream created.");
                try {
                    for (int index = 0; index < retrievedFiles.size(); index++) {
                        String transferDoc = retrievedFiles.get(index);
                        if (myLogger.isDebugEnabled()) {
                            myLogger.debug("transferDoc is " + transferDoc);
                        }
                        if (transferDoc == null) {
                            myLogger.warn("For retrieved file @index " + index + " transferDoc is null!");
                            continue;
                        }
                        ZipEntryOutputStream zeos = null;
                        File dcmFile = null;
                        try {
                            dcmFile = new File(transferDoc);
                            zeos = new ZipEntryOutputStream(zos, dcmFile.getName(), ZipEntry.STORED);
                            myLogger.debug(".zip entry created");
                            entryCount++;
                            FileInputStream fin = new FileInputStream(transferDoc);
                            BufferedInputStream dicomIn = new BufferedInputStream(fin);
                            byte[] data = new byte[8192];
                            int bytesRead = 0;
                            while ((bytesRead = (dicomIn.read(data, 0, data.length))) > 0) {
                                zeos.write(data, 0, bytesRead);
                            }
                        } catch (Exception e1) {
                            myLogger.error("ERROR writing to zip entry ", e1);
                        } finally {
                            try {
                                if (zeos != null) {
                                    zeos.close();
                                }
                                myLogger.info("Transfer of file complete: " + transferDoc);
                            } catch (Exception e) {
                                myLogger.error("ERROR closing zip entry ", e);
                            }
                            if (dcmFile != null) {
                                deleteFile(dcmFile);
                            }
                        }
                    }
                } finally {
                    try {
                        if (entryCount > 0) {
                            zos.close();
                        } else {
                            pos.close();
                        }
                        myLogger.debug("OutputStream closed.");
                    } catch (Exception e) {
                        myLogger.error("ERROR closing output stream ", e);
                    }
                }
                if (myLogger.isDebugEnabled()) {
                    long t2 = System.currentTimeMillis() / 1000;
                    myLogger.debug("Time Taken = " + (t2 - t1));
                }

            }
        };
        t.start();

        // set up the transfer context
        TransferServiceContextReference tscr = TransferServiceHelper.createTransferContext(pis, null);
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("TransferServiceContextReference " + tscr);
        }
        // set up the transfer context
        return tscr;
    }

    /**
     * Compute a name for a new thread for sending a .zip file to a transfer
     * service client that consists of a fixed base name and a serial number.
     * 
     * @return the computed name.
     */
    private static String computeZipFileSenderThreadName() {
        zipOutputWriterCount++;
        return ZIP_FILE_SENDER + zipOutputWriterCount;
    }

    /**
     * Close the given file, logging any problems.
     * 
     * @param dcmFile
     *            The file to be closed.
     */
    private static void deleteFile(File dcmFile) {
        try {
            if (!dcmFile.delete()) {
                myLogger.warn("Failed to delete file: " + dcmFile.getAbsolutePath());
            }
        } catch (Exception e) {
            String msg = "Exception thrown while trying to delete file: " + dcmFile.getAbsolutePath();
            myLogger.error(msg, e);
        }
    }

    /**
     * Return true if the configuration file says that data level authorization
     * should be on.
     * 
     * @param dicomQueryProcProps
     *            The properties that will contain the configuration
     *            information.
     */
    private boolean isDataLevelAuthorization(Properties dicomQueryProcProps) {
        String dataLevelAuthString = dicomQueryProcProps.getProperty(DICOMCQLQueryProcessor.DATA_LEVEL_AUTH);
        myLogger.debug("Value of service property " + DICOMCQLQueryProcessor.DATA_LEVEL_AUTH + " is "
                + dataLevelAuthString);
        if (dataLevelAuthString == null) {
            dataLevelAuthString = dicomQueryProcProps.getProperty(DICOMCQLQueryProcessor.CQL_QUERY_DATA_LEVEL_AUTH);
            myLogger.debug("Value of service property " + DICOMCQLQueryProcessor.CQL_QUERY_DATA_LEVEL_AUTH + " is "
                    + dataLevelAuthString);
        }
        return Boolean.parseBoolean(dataLevelAuthString);
    }

    /**
     * @return
     */
    private Properties fetchQueryProcProps() throws RemoteException {
        Properties dicomQueryProcProps;
        try {
            dicomQueryProcProps = ServiceConfigUtil.getQueryProcessorConfigurationParameters();
            if (dicomQueryProcProps == null) {
                String msg = "Query Processor Configuration Parameters could not be retrieved";
                myLogger.error(msg);
                throw new RemoteException(msg);
            }
        } catch (Exception e2) {
            String msg = "Query Processor Configuration Parameters could not be retrieved";
            myLogger.error(msg, e2);
            throw new RemoteException(msg, e2);
        }
        return dicomQueryProcProps;
    }

    public TransferServiceContextReference submitDICOMData(submission.SubmissionInformation submissionInformation)
            throws RemoteException {
        if (myLogger.isDebugEnabled()) {
            myLogger.debug(submissionInformation.getFileType() + "...." + submissionInformation.getFileName());
        }
        if (!submissionInformation.getFileType().equalsIgnoreCase("zip")) {
            String msg = "Uploaded File Information is not of type zip";
            myLogger.warn(msg);
            throw new RemoteException(msg);
        }
        final submission.SubmissionInformation subInfo = submissionInformation;

        Properties dicomQueryProcProps = null;
        try {
            dicomQueryProcProps = ServiceConfigUtil.getQueryProcessorConfigurationParameters();
        } catch (Exception e2) {
            myLogger.error("Failed to get query processor parameters", e2);
        }
        final Properties queryProps = dicomQueryProcProps;

        // It is necessary to set the user here rather than during the callback,
        // because the user can only be found in the same thread that this
        // operation was called from.
        final String user = authOn ? UserUtil.getUserId() : null;
        DataStagedCallback callback = new DataStagedCallback() {

            public void dataStaged(TransferServiceContextResource resource) {
                boolean authOn = isDataLevelAuthorization(queryProps);

                DICOMSubmit dicomSub = new DICOMSubmit(subInfo, queryProps, authOn, user);
                try {
                    dicomSub.processDicomUpload(resource.getDataStorageDescriptor().getLocation());
                } catch (Exception e) {
                    myLogger.error("Upload Failed", e);
                }
            }
        };
        return TransferServiceHelper.createTransferContext(null, callback);
    }

    /*
     * This method should ideally be in a ServiceImplBase and should never be
     * edited Also, every method should have their own unique pre- &
     * post-operation auditors
     * 
     * private void preRetrieveDICOMDataAudit(final CQLQuery cqlQuery) { Thread
     * t = new Thread() { @Override public void run() { AuditMessageType
     * auditMessage = new AuditMessageType(); // Sec 5.1
     * auditMessage.setEventIdentification
     * (localDicomAuditor.preExecutionAudit(cqlQuery.toString(),
     * AuditCodes.EventActionCode_Read, null)); // Sec 5.2 & 5.3
     * auditMessage.setActiveParticipant
     * (localDicomAuditor.getActiveParticipant(org
     * .apache.axis.MessageContext.getCurrentContext(),
     * org.globus.wsrf.security.SecurityManager.getManager())); // Sec 5.4
     * auditMessage
     * .setAuditSourceIdentification(localDicomAuditor.getAuditSourceIdentification
     * (null)); // Sec 5.5 Participant Object Identification // Don't know if
     * this is needed; Probably should create the interfaces for this.
     * 
     * auditSrvHelper.preOpEvent(auditMessage); } }; t.start(); }
     */

    /*
     * private void postRetrieveDICOMDataAudit(final boolean status, final
     * String outcome) { Thread t = new Thread() { @Override public void run() {
     * EventIdentificationType postExeEventID =
     * localDicomAuditor.postOperation(status, outcome);
     * auditSrvHelper.postOpEvent(postExeEventID); } }; t.start(); }
     */

    public EnumerationResponseContainer retrieveDICOMDataProgressively(CQLQuery cQLQuery) throws RemoteException {
        Properties dicomQueryProcProps = null;
        try {
            dicomQueryProcProps = ServiceConfigUtil.getQueryProcessorConfigurationParameters();
        } catch (Exception e2) {
            myLogger.error("Failed to get query processor parameters", e2);
        }

        if (dicomQueryProcProps == null) {
            String msg = "Query Processor Configuration Parameters could not be retrieved";
            myLogger.error(msg);
            throw new RemoteException(msg);
        }
        gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer response = null;
        try {
            response = EnumerateResponseFactory.createEnumerationResponse(new DICOMServiceEnumIterator(cQLQuery,
                    dicomQueryProcProps));
        } catch (EnumerationCreationException e) {
            myLogger.error("Error creating enumeration response", e);
        }
        return response;
    }

}
