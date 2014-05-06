package gov.nih.nci.ivi.dicomdataservice.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.utils.Zipper;

import java.io.File;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Properties;

import javax.xml.namespace.QName;
import javax.xml.soap.SOAPElement;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.ws.enumeration.EnumIterator;
import org.globus.ws.enumeration.IterationConstraints;
import org.globus.ws.enumeration.IterationResult;
import org.globus.ws.enumeration.TimeoutException;
import org.globus.wsrf.encoding.ObjectSerializer;

class DICOMServiceEnumIterator implements EnumIterator {
    private static final Logger myLogger = LogManager.getLogger(DICOMServiceEnumIterator.class);

    private DICOMRetrieve dicomRet;
    boolean isRetrievalRequest = true;

    /**
     * This is either null or the perform retrieve thread ended because an
     * exception was thrown and this is the exception.
     */
    private Throwable retrieveFailure = null;

    public DICOMServiceEnumIterator(CQLQuery query, Properties dicomQueryProcProps) {
        dicomRet = new DICOMRetrieve(dicomQueryProcProps, false, null, 0);
        dicomRet.setIsWSEnum(true);
        final CQLQuery fcqlq = query;
        Thread t = new Thread() {
            public void run() {
                try {
                    dicomRet.performRetrieve(fcqlq);
                } catch (RemoteException e) {
                    retrieveFailure = e;
                    myLogger.error("Ansynchronous performRetrieve thread ended because of a thrown exception.", e);
                }
                isRetrievalRequest = false;
            }

        };
        t.start();
    }

    public IterationResult next(IterationConstraints arg0) throws TimeoutException, NoSuchElementException {
        // System.out.println("next request issued");

        if (retrieveFailure != null) {
            String msg = "There is no next because the server's asynchronous performRetrieve thread failed with a thrown exception";
            myLogger.error(msg);
            NoSuchElementException excp = new NoSuchElementException(msg);
            excp.initCause(retrieveFailure);
            throw excp;
        }

        IterationResult iterationResult = null;
        List<String> allFileNames = null;
        boolean URIsAvailable = false;
        boolean iterationFlag = false;
        boolean finalResult = false;
        int numberOfAllFiles;
        int SLEEP_TIME = 50;

        // boolean contextSwitchFlag = false;

        do {
            try {
                Thread.sleep(SLEEP_TIME);
            } catch (InterruptedException e) {
                myLogger.warn("Thread interrupted", e);
            }

            if (dicomRet.isRetrievalRequest()) {
                myLogger.info("C_GET IS GOING ON");
                if (dicomRet != null) {
                    allFileNames = dicomRet.getRetrievedFileNames();
                    numberOfAllFiles = allFileNames.size();
                } else {
                    numberOfAllFiles = 0;
                }

                if (numberOfAllFiles > 0) {
                    if (myLogger.isInfoEnabled()) {
                        for (int i = 0; i < numberOfAllFiles; i++) {
                            myLogger.info("ENUMERATOR: " + allFileNames.get(i));
                        }
                    }
                    URIsAvailable = true;
                }
            } else {
                /*
                 * System.out.println("C_GET IS OVER will sleep now"); try {
                 * Thread.sleep(SLEEP_TIME); } catch (InterruptedException e) {
                 * e.printStackTrace(); }
                 */
                myLogger.info("C_GET IS OVER will sleep now");
                if (dicomRet != null) {
                    allFileNames = dicomRet.getRetrievedFileNames();
                    numberOfAllFiles = allFileNames.size();
                } else {
                    myLogger.info("Hmm DICOMRet == null????");
                    numberOfAllFiles = 0;
                }

                if (numberOfAllFiles == 0) {
                    // no new data after NO new data condition?
                    myLogger.info("There will not be any more data will destroy");
                    iterationResult = new IterationResult(new SOAPElement[0], true);
                    return iterationResult;
                } else // final chunk of data
                {
                    if (myLogger.isInfoEnabled()) {
                        for (int i = 0; i < allFileNames.size(); i++) {
                            myLogger.info("ENUMERATOR: " + allFileNames.get(i));
                        }
                    }
                    URIsAvailable = true;
                    finalResult = true;
                }
            }

        } while (!URIsAvailable);

        myLogger.info("started creating the results, " + numberOfAllFiles + " new files");
        // System.out.println("started creating the results, " +
        // (numberOfAllFiles-oldFileNames.size()) + " new files");
        QName qname = new QName("http://transfer.cagrid.org/TransferService/Context/types",
                "TransferServiceContextReference");
        int fileCount = 0;
        boolean zipFiles = true;
        SOAPElement[] elements = null;
        if (zipFiles) {
            // String fileNames[] = new
            // String[numberOfAllFiles-oldFileNames.size()];
            String fileNames[] = new String[numberOfAllFiles];
            for (int i = 0; i < numberOfAllFiles; i++) {
                String fileName = allFileNames.get(i);
                fileNames[fileCount++] = new String(fileName);
                /*
                 * if (!oldFileNames.contains(fileName)) { // this file is newly
                 * created, so add it to the results
                 * System.out.println("new file found. adding result " +
                 * fileName); fileNames[fileCount] = new String(fileName);
                 * fileCount++; oldFileNames.add(fileName); }
                 */}
            String username = System.getenv("LOGNAME");
            if (username == null)
                username = "";
            else
                username = "-" + username;
            File tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "DICOMDataService-WSEnum"
                    + username);
            tmpDir.mkdirs();
            String tmpFileName = null;
            try {
                tmpFileName = File.createTempFile("tmp", ".zip", tmpDir).getCanonicalPath();
                Zipper.zip(tmpFileName, fileNames, false);
            } catch (IOException e) {
                String msg = "next() failed because of an I/O error";
                myLogger.error(msg, e);
                NoSuchElementException excp = new NoSuchElementException(msg);
                excp.initCause(e);
                throw excp;
            }
            for (int i = 0; i < fileNames.length; i++) {
                File f = new File(fileNames[i]);
                if (!f.delete()) {
                    myLogger.warn("next(): Failed to delete " + f.getAbsolutePath());
                }
            }
            elements = new SOAPElement[1];

            try {
                TransferServiceContextReference tscr = TransferServiceHelper.createTransferContext(
                        new File(tmpFileName), null, false);
                elements[0] = ObjectSerializer.toSOAPElement(tscr, qname);
            } catch (Exception e) {
                String msg = "next() failed because of an Exception";
                myLogger.error(msg, e);
                NoSuchElementException excp = new NoSuchElementException(msg);
                excp.initCause(e);
                throw excp;
            }
            myLogger.info("returning the created zip file " + tmpFileName);
        } else {
            myLogger.info("returning " + fileCount + " individual files");
        }
        if (!finalResult)
            iterationResult = new IterationResult(elements, iterationFlag);
        else
            iterationResult = new IterationResult(elements, finalResult);
        return iterationResult;

    }

    public void release() {
        System.out.println("Cleaning up");
    }
}