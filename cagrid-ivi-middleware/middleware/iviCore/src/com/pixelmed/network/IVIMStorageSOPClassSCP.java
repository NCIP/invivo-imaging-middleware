/* Copyright (c) 2001-2005, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.network;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.LinkedList;
import java.util.ListIterator;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.DicomOutputStream;
import com.pixelmed.dicom.FileMetaInformation;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.TransferSyntax;
import com.pixelmed.query.QueryResponseGenerator;
import com.pixelmed.query.QueryResponseGeneratorFactory;
import com.pixelmed.query.RetrieveResponseGenerator;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;
import com.pixelmed.utils.ByteArray;
import com.pixelmed.utils.CopyStream;
import com.pixelmed.utils.FileUtilities;
import com.pixelmed.utils.HexDump;

/**
 * <p>
 * This class implements the SCP role of SOP Classes of the Storage Service
 * Class, the Study Root Query Retrieve Information Model Find, Get and Move SOP
 * Classes, and the Verification SOP Class.
 * </p>
 * 
 * <p>
 * The class has a constructor and a <code>run()</code> method. The constructor
 * is passed a socket on which has been received a transport connection open
 * indication. The <code>run()</code> method waits for an association to be
 * initiated (i.e. acts as an association acceptor), then waits for storage or
 * verification commands, storing data sets in Part 10 files in the specified
 * folder.
 * </p>
 * 
 * <p>
 * Debugging messages with a varying degree of verbosity can be activated.
 * </p>
 * 
 * <p>
 * This class is not normally used directly, but rather is instantiated by the
 * {@link com.pixelmed.network.StorageSOPClassSCPDispatcher
 * StorageSOPClassSCPDispatcher}, which takes care of listening for transport
 * connection open indications, and creates new threads and starts them to
 * handle each incoming association request.
 * </p>
 * 
 * @see com.pixelmed.network.StorageSOPClassSCPDispatcher
 * 
 * @author dclunie
 */
public class IVIMStorageSOPClassSCP extends SOPClass implements Runnable {
    private static final Logger myLogger = LogManager.getLogger(IVIMStorageSOPClassSCP.class);

    /***/
    @SuppressWarnings("unused")
    private static final String identString = "@(#) $Header: /cvsshare/content/gforge/middleware/middleware/projects/iviCore/src/com/pixelmed/network/IVIMStorageSOPClassSCP.java,v 1.2 2007/01/22 20:01:34 tpan Exp $";

    /***/
    private class CompositeCommandReceivedPDUHandler extends ReceivedDataHandler {
        /***/
        private int command;
        /***/
        private byte[] commandReceived;
        /***/
        private AttributeList commandList;
        /***/
        private byte[] dataReceived;
        /***/
        private AttributeList dataList;
        /***/
        private OutputStream out;
        /***/
        private CStoreRequestCommandMessage csrq;
        /***/
        private CEchoRequestCommandMessage cerq;
        /***/
        private CFindRequestCommandMessage cfrq;
        /***/
        private CMoveRequestCommandMessage cmrq;
        /***/
        private CGetRequestCommandMessage cgrq;
        /***/
        private byte[] response;
        /***/
        private byte presentationContextIDUsed;
        // private Association association;
        /***/
        private File receivedFile;
        /***/
        private File temporaryReceivedFile;
        /***/
        private File savedImagesFolder;
        /***/
        private QueryResponseGeneratorFactory queryResponseGeneratorFactory;
        /***/
        private RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory;

        /**
         * @exception IOException
         * @exception DicomException
         */
        private void buildCEchoResponse() throws DicomException, IOException {
            response = new CEchoResponseCommandMessage(cerq.getAffectedSOPClassUID(), cerq.getMessageID(), 0x0000)
                    .getBytes();
        }

        /**
         * @exception IOException
         * @exception DicomException
         */
        private void buildCStoreResponse() throws DicomException, IOException {
            response = new CStoreResponseCommandMessage(csrq.getAffectedSOPClassUID(),
                    csrq.getAffectedSOPInstanceUID(), csrq.getMessageID(), 0x0000 // success
            // status
            ).getBytes();
        }

        /**
         * @param savedImagesFolder
         *            null if we do not want to actually save received data
         *            (i.e., we want to discard it for testing)
         * @param queryResponseGeneratorFactory
         *            a factory to make handlers to generate query responses
         *            from a supplied query message
         * @param retrieveResponseGeneratorFactory
         *            a factory to make handlers to generate retrieve responses
         *            from a supplied retrieve message
         * @param debugLevel
         */
        public CompositeCommandReceivedPDUHandler(File savedImagesFolder,
                QueryResponseGeneratorFactory queryResponseGeneratorFactory,
                RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory, int debugLevel) {
            super(debugLevel);
            command = MessageServiceElementCommand.NOCOMMAND;
            commandReceived = null;
            commandList = null;
            dataReceived = null;
            dataList = null;
            out = null;
            csrq = null;
            receivedFile = null;
            this.savedImagesFolder = savedImagesFolder;
            this.queryResponseGeneratorFactory = queryResponseGeneratorFactory;
            this.retrieveResponseGeneratorFactory = retrieveResponseGeneratorFactory;
        }

        private class CMovePendingResponseSender extends MultipleInstanceTransferStatusHandler {

            private Association association;
            private CMoveRequestCommandMessage cmrq;

            int nRemaining;
            int nCompleted;
            int nFailed;
            int nWarning;

            CMovePendingResponseSender(Association association, CMoveRequestCommandMessage cmrq) {
                this.association = association;
                this.cmrq = cmrq;
                nRemaining = 0;
                nCompleted = 0;
                nFailed = 0;
                nWarning = 0;
            }

            public void updateStatus(int nRemaining, int nCompleted, int nFailed, int nWarning, String sopInstanceUID) {
                this.nRemaining = nRemaining;
                this.nCompleted = nCompleted;
                this.nFailed = nFailed;
                this.nWarning = nWarning;
                myLogger.debug("CompositeCommandReceivedPDUHandler.CMovePendingResponseSender.updateStatus(): "
                        + "Bulding C-MOVE pending response");
                if (nRemaining > 0) {
                    try {
                        byte cMovePendingResponseCommandMessage[] = new CMoveResponseCommandMessage(cmrq
                                .getAffectedSOPClassUID(), cmrq.getMessageID(), 0xFF00, // status
                                // is
                                // pending
                                false, // no dataset
                                nRemaining, nCompleted, nFailed, nWarning).getBytes();
                        if (myLogger.isDebugEnabled()) {
                            String listString = CompositeResponseHandler.dumpAttributeListFromCommandOrData(
                                    cMovePendingResponseCommandMessage, TransferSyntax.Default);
                            myLogger.debug("CompositeCommandReceivedPDUHandler.CMovePendingResponseSender."
                                    + "updateStatus(): C-MOVE pending response = " + listString);
                        }

                        byte presentationContextIDForResponse = association.getSuitablePresentationContextID(cmrq
                                .getAffectedSOPClassUID());
                        association.send(presentationContextIDForResponse, cMovePendingResponseCommandMessage, null);
                    } catch (Exception e) {
                        myLogger.error("updateStatus: Error updating status", e);
                    }
                }
                // else do not send pending message if nothing remaining; just
                // update counts
            }
        }

        private class CGetPendingResponseSender extends MultipleInstanceTransferStatusHandler {

            private Association association;
            private CGetRequestCommandMessage cgrq;

            int nRemaining;
            int nCompleted;
            int nFailed;
            int nWarning;

            CGetPendingResponseSender(Association association, CGetRequestCommandMessage cgrq) {
                this.association = association;
                this.cgrq = cgrq;
                nRemaining = 0;
                nCompleted = 0;
                nFailed = 0;
                nWarning = 0;
            }

            public void updateStatus(int nRemaining, int nCompleted, int nFailed, int nWarning, String sopInstanceUID) {
                this.nRemaining = nRemaining;
                this.nCompleted = nCompleted;
                this.nFailed = nFailed;
                this.nWarning = nWarning;
                myLogger.debug("CompositeCommandReceivedPDUHandler.CGetPendingResponseSender.updateStatus(): "
                        + "Bulding C-GET pending response");
                if (nRemaining > 0) {
                    try {
                        byte cGetPendingResponseCommandMessage[] = new CGetResponseCommandMessage(cgrq
                                .getAffectedSOPClassUID(), cgrq.getMessageID(), 0xFF00, // status
                                // is
                                // pending
                                false, // no dataset
                                nRemaining, nCompleted, nFailed, nWarning).getBytes();
                        if (myLogger.isDebugEnabled()) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.CGetPendingResponseSender."
                                    + "updateStatus(): C-GET pending response = "
                                    + CompositeResponseHandler.dumpAttributeListFromCommandOrData(
                                            cGetPendingResponseCommandMessage, TransferSyntax.Default));
                        }
                        byte presentationContextIDForResponse = association.getSuitablePresentationContextID(cgrq
                                .getAffectedSOPClassUID());
                        association.send(presentationContextIDForResponse, cGetPendingResponseCommandMessage, null);
                    } catch (Exception e) {
                        myLogger.error("updateStatus: ", e);
                    }
                }
                // else do not send pending message if nothing remaining; just
                // update counts
            }
        }

        /**
         * @param pdata
         * @param association
         * @exception IOException
         * @exception DicomException
         * @exception DicomNetworkException
         */
        public void sendPDataIndication(PDataPDU pdata, Association association) throws DicomNetworkException,
                DicomException, IOException {
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): sendPDataIndication()");
                super.dumpPDVList(pdata.getPDVList());
                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                        + "finished dumping PDV list from PDU");
            }
            // append to command ...
            LinkedList<?> pdvList = pdata.getPDVList();
            ListIterator<?> i = pdvList.listIterator();
            while (i.hasNext()) {
                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): have another fragment");
                PresentationDataValue pdv = (PresentationDataValue) i.next();
                presentationContextIDUsed = pdv.getPresentationContextID();
                if (pdv.isCommand()) {
                    receivedFile = null;
                    commandReceived = ByteArray.concatenate(commandReceived, pdv.getValue()); // handles
                    // null
                    // cases
                    if (pdv.isLastFragment()) {
                        if (myLogger.isDebugEnabled()) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                    + "last fragment of data seen\n" + HexDump.dump(commandReceived));
                        }
                        commandList = new AttributeList();
                        commandList.read(new DicomInputStream(new ByteArrayInputStream(commandReceived),
                                TransferSyntax.Default, false));
                        myLogger.debug(commandList);
                        command = Attribute.getSingleIntegerValueOrDefault(commandList, TagFromName.CommandField,
                                0xffff);
                        if (command == MessageServiceElementCommand.C_ECHO_RQ) { // C-ECHO-RQ
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): C-ECHO-RQ");
                            cerq = new CEchoRequestCommandMessage(commandList);
                            buildCEchoResponse();
                            setDone(true);
                            setRelease(false);
                        } else if (command == MessageServiceElementCommand.C_STORE_RQ) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): C-STORE-RQ");
                            csrq = new CStoreRequestCommandMessage(commandList);
                        } else if (command == MessageServiceElementCommand.C_FIND_RQ
                                && queryResponseGeneratorFactory != null) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): C-FIND-RQ");
                            cfrq = new CFindRequestCommandMessage(commandList);
                        } else if (command == MessageServiceElementCommand.C_MOVE_RQ
                                && retrieveResponseGeneratorFactory != null) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): C-MOVE-RQ");
                            cmrq = new CMoveRequestCommandMessage(commandList);
                        } else if (command == MessageServiceElementCommand.C_GET_RQ
                                && retrieveResponseGeneratorFactory != null) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): C-GET-RQ");
                            cgrq = new CGetRequestCommandMessage(commandList);
                        } else {
                            throw new DicomNetworkException("Unexpected command 0x" + Integer.toHexString(command)
                                    + " " + MessageServiceElementCommand.toString(command));
                        }
                        // 2004/06/08 DAC removed break that was here to resolve
                        // [bugs.mrmf] (000113) StorageSCP failing when data
                        // followed command in same PDU
                        if (myLogger.isDebugEnabled() && i.hasNext())
                            myLogger.debug("CompositeCommandReceivedPDUHandler: Data after command in same PDU");
                    }
                } else {
                    if (command == MessageServiceElementCommand.C_STORE_RQ) {
                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                + "Storing data fragment");
                        if (out == null && savedImagesFolder != null) { // lazy
                            // opening
                            FileMetaInformation fmi = new FileMetaInformation(csrq.getAffectedSOPClassUID(), csrq
                                    .getAffectedSOPInstanceUID(), association
                                    .getTransferSyntaxForPresentationContextID(presentationContextIDUsed), association
                                    .getCallingAETitle());
                            receivedFile = new File(savedImagesFolder, csrq.getAffectedSOPInstanceUID());
                            // temporaryReceivedFile=File.createTempFile("PMP",null);
                            temporaryReceivedFile = new File(savedImagesFolder, FileUtilities.makeTemporaryFileName());
                            if (myLogger.isDebugEnabled()) {
                                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                        + "Receiving and storing " + receivedFile);
                                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                        + "Receiving and storing into temporary " + temporaryReceivedFile);
                            }
                            out = new BufferedOutputStream(new FileOutputStream(temporaryReceivedFile));
                            DicomOutputStream dout = new DicomOutputStream(out, TransferSyntax.ExplicitVRLittleEndian,
                                    null);
                            fmi.getAttributeList().write(dout);
                            dout.flush();
                        }
                        if (out != null) {
                            out.write(pdv.getValue());
                        }
                        if (pdv.isLastFragment()) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                    + "Finished storing data");
                            if (out != null) {
                                out.close();
                                if (receivedFile.exists()) {
                                    myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                            + "Deleting pre-existing file for same SOPInstanceUID");
                                    receivedFile.delete(); // prior to rename of
                                    // temporary file, in
                                    // case might cause
                                    // renameTo() fail
                                }
                                if (!temporaryReceivedFile.renameTo(receivedFile)) {
                                    myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                            + "Could not move temporary file into place ... copying instead");
                                    CopyStream.copy(temporaryReceivedFile, receivedFile);
                                    temporaryReceivedFile.delete();
                                }
                                out = null;
                            }
                            buildCStoreResponse();
                            setDone(true);
                            setRelease(false);
                        }
                    } else if (command == MessageServiceElementCommand.C_FIND_RQ
                            && queryResponseGeneratorFactory != null) {
                        QueryResponseGenerator queryResponseGenerator = queryResponseGeneratorFactory.newInstance();
                        dataReceived = ByteArray.concatenate(dataReceived, pdv.getValue()); // handles
                        // null
                        // cases
                        if (pdv.isLastFragment()) {
                            if (myLogger.isDebugEnabled()) {
                                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                        + "last fragment of data seen\n" + HexDump.dump(dataReceived));
                            }
                            dataList = new AttributeList();
                            dataList.read(new DicomInputStream(new ByteArrayInputStream(dataReceived), association
                                    .getTransferSyntaxForPresentationContextID(presentationContextIDUsed), false));
                            myLogger.debug(dataList);
                            queryResponseGenerator
                                    .performQuery(cfrq.getAffectedSOPClassUID(), dataList, false/* relational */);
                            AttributeList responseIdentifierList;
                            while ((responseIdentifierList = queryResponseGenerator.next()) != null) {
                                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                        + "Building and sending pending response " + responseIdentifierList.toString());
                                byte presentationContextIDForResponse = association
                                        .getSuitablePresentationContextID(cfrq.getAffectedSOPClassUID());
                                if (myLogger.isDebugEnabled()) {
                                    myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                            + "Using context ID for response " + presentationContextIDForResponse);
                                }
                                byte cFindResponseCommandMessage[] = new CFindResponseCommandMessage(cfrq
                                        .getAffectedSOPClassUID(), cfrq.getMessageID(),
                                // (queryResponseGenerator.allOptionalKeysSuppliedWereSupported()
                                        // ? 0xff00 : 0xff01), // pending
                                        0xff00, // pending ... temporary
                                        // workaround for [bugs.mrmf]
                                        // (000213) K-PACS freaked out
                                        // by valid unsupported optional
                                        // keys pending response during
                                        // C-FIND
                                        true // dataset present
                                ).getBytes();
                                byte cFindIdentifier[] = new IdentifierMessage(responseIdentifierList, association
                                        .getTransferSyntaxForPresentationContextID(presentationContextIDForResponse))
                                        .getBytes();
                                // association.setReceivedDataHandler(new
                                // CXXXXResponseHandler(debugLevel));
                                association.send(presentationContextIDForResponse, cFindResponseCommandMessage, null);
                                association.send(presentationContextIDForResponse, null, cFindIdentifier);
                            }
                            queryResponseGenerator.close();
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                    + "Bulding final C-FIND success response");
                            response = new CFindResponseCommandMessage(cfrq.getAffectedSOPClassUID(), cfrq
                                    .getMessageID(), 0x0000, // success status
                                    // matching is
                                    // complete
                                    false // no dataset
                            ).getBytes();
                            setDone(true);
                            setRelease(false);
                        }
                    } else if (command == MessageServiceElementCommand.C_MOVE_RQ
                            && retrieveResponseGeneratorFactory != null && applicationEntityMap != null) {
                        RetrieveResponseGenerator retrieveResponseGenerator = retrieveResponseGeneratorFactory
                                .newInstance();
                        dataReceived = ByteArray.concatenate(dataReceived, pdv.getValue()); // handles
                        // null
                        // cases
                        if (pdv.isLastFragment()) {
                            if (myLogger.isDebugEnabled()) {
                                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                        + "last fragment of data seen" + HexDump.dump(dataReceived));
                            }
                            dataList = new AttributeList();
                            dataList.read(new DicomInputStream(new ByteArrayInputStream(dataReceived), association
                                    .getTransferSyntaxForPresentationContextID(presentationContextIDUsed), false));
                            myLogger.debug(dataList);
                            retrieveResponseGenerator
                                    .performRetrieve(cmrq.getAffectedSOPClassUID(), dataList, false/* relational */);
                            SetOfDicomFiles dicomFiles = retrieveResponseGenerator.getDicomFiles();
                            int status = retrieveResponseGenerator.getStatus();
                            retrieveResponseGenerator.close();
                            if (status != 0x0000 || dicomFiles == null) {
                                if (myLogger.isDebugEnabled()) {
                                    myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                            + "retrieve failed or contains nothing, status = 0x"
                                            + Integer.toHexString(status));
                                }
                                response = new CMoveResponseCommandMessage(cmrq.getAffectedSOPClassUID(), cmrq
                                        .getMessageID(), status, false, // no
                                        // dataset
                                        retrieveResponseGenerator.getOffendingElement(), null // no
                                // ErrorComment
                                ).getBytes();
                            } else {
                                CMovePendingResponseSender pendingResponseSender = new CMovePendingResponseSender(
                                        association, cmrq);
                                pendingResponseSender.nRemaining = dicomFiles.size(); // in
                                // case
                                // fails
                                // immediately
                                // with
                                // no
                                // status
                                // updates

                                String moveDestinationAETitle = cmrq.getMoveDestination();
                                PresentationAddress moveDestinationPresentationAddress = applicationEntityMap
                                        .getPresentationAddress(moveDestinationAETitle);
                                if (moveDestinationPresentationAddress != null) {
                                    String moveDestinationHostname = moveDestinationPresentationAddress.getHostname();
                                    int moveDestinationPort = moveDestinationPresentationAddress.getPort();
                                    if (myLogger.isDebugEnabled()) {
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "moveDestinationAETitle=" + moveDestinationAETitle);
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "moveDestinationHostname=" + moveDestinationHostname);
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "moveDestinationPort=" + moveDestinationPort);
                                    }
                                    {
                                        new StorageSOPClassSCU(moveDestinationHostname, moveDestinationPort,
                                                moveDestinationAETitle, // the
                                                // C-STORE
                                                // called
                                                // AET
                                                calledAETitle, // use ourselves
                                                // (the C-MOVE
                                                // called AET) as
                                                // the C-STORE
                                                // calling AET
                                                dicomFiles, 0, // compressionLevel
                                                pendingResponseSender, debugLevel);
                                    }
                                    if (myLogger.isDebugEnabled()) {
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "after all stored: nRemaining=" + pendingResponseSender.nRemaining
                                                + " nCompleted=" + pendingResponseSender.nCompleted + " nFailed="
                                                + pendingResponseSender.nFailed + " nWarning="
                                                + pendingResponseSender.nWarning);
                                    }
                                    if (pendingResponseSender.nRemaining > 0) {
                                        pendingResponseSender.nFailed += pendingResponseSender.nRemaining;
                                        pendingResponseSender.nRemaining = 0;
                                    }
                                    if (myLogger.isDebugEnabled()) {
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "after setting remaining to zero: nRemaining="
                                                + pendingResponseSender.nRemaining + " nCompleted="
                                                + pendingResponseSender.nCompleted + " nFailed="
                                                + pendingResponseSender.nFailed + " nWarning="
                                                + pendingResponseSender.nWarning);
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "Bulding final C-MOVE success response");
                                    }
                                    response = new CMoveResponseCommandMessage(
                                            cmrq.getAffectedSOPClassUID(),
                                            cmrq.getMessageID(),
                                            pendingResponseSender.nFailed > 0 ? 0xB000 : 0x0000, // status
                                            // is
                                            // warning
                                            // one
                                            // or
                                            // more
                                            // failure,
                                            // or
                                            // success
                                            // matching
                                            // is
                                            // complete
                                            false, // no dataset, unless there
                                            // was failure, then add
                                            // Failed SOP Instance UID
                                            // List (0008,0058)
                                            pendingResponseSender.nRemaining, pendingResponseSender.nCompleted,
                                            pendingResponseSender.nFailed, pendingResponseSender.nWarning).getBytes();
                                } else {
                                    status = 0xA801;
                                    if (myLogger.isDebugEnabled()) {
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "Unrecognized move destination " + moveDestinationAETitle
                                                + ", status = 0x" + Integer.toHexString(status));
                                    }
                                    response = new CMoveResponseCommandMessage(cmrq.getAffectedSOPClassUID(), cmrq
                                            .getMessageID(), status, false, // no
                                            // dataset
                                            null, // no OffendingElement
                                            moveDestinationAETitle // ErrorComment
                                    ).getBytes();
                                }
                            }
                            setDone(true);
                            setRelease(false);
                        }
                    } else if (command == MessageServiceElementCommand.C_GET_RQ
                            && retrieveResponseGeneratorFactory != null) {
                        RetrieveResponseGenerator retrieveResponseGenerator = retrieveResponseGeneratorFactory
                                .newInstance();
                        dataReceived = ByteArray.concatenate(dataReceived, pdv.getValue()); // handles
                        // null
                        // cases
                        if (pdv.isLastFragment()) {
                            if (myLogger.isDebugEnabled()) {
                                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                        + "last fragment of data seen" + HexDump.dump(dataReceived));
                            }
                            dataList = new AttributeList();
                            dataList.read(new DicomInputStream(new ByteArrayInputStream(dataReceived), association
                                    .getTransferSyntaxForPresentationContextID(presentationContextIDUsed), false));
                            myLogger.debug(dataList);
                            retrieveResponseGenerator
                                    .performRetrieve(cgrq.getAffectedSOPClassUID(), dataList, false/* relational */);
                            SetOfDicomFiles dicomFiles = retrieveResponseGenerator.getDicomFiles();
                            int status = retrieveResponseGenerator.getStatus();
                            retrieveResponseGenerator.close();
                            if (status != 0x0000 || dicomFiles == null) {
                                if (myLogger.isDebugEnabled()) {
                                    myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                            + "retrieve failed or contains nothing, status = 0x"
                                            + Integer.toHexString(status));
                                }
                                response = new CGetResponseCommandMessage(cgrq.getAffectedSOPClassUID(), cgrq
                                        .getMessageID(), status, false, // no
                                        // dataset
                                        retrieveResponseGenerator.getOffendingElement(), null // no
                                // ErrorComment
                                ).getBytes();
                            } else {
                                CGetPendingResponseSender pendingResponseSender = new CGetPendingResponseSender(
                                        association, cgrq);
                                pendingResponseSender.nRemaining = dicomFiles.size(); // in
                                // case
                                // fails
                                // immediately
                                // with
                                // no
                                // status
                                // updates
                                {
                                    // WARNING - StorageSOPClassSCU will
                                    // override the current ReceivedDataHandler
                                    // set on the association
                                    new StorageSOPClassSCU(association, dicomFiles, pendingResponseSender, debugLevel);
                                    association.setReceivedDataHandler(this); // re-establish
                                    // ourselves
                                    // as
                                    // the
                                    // handler
                                    // to
                                    // send
                                    // done
                                    // response
                                    if (myLogger.isDebugEnabled()) {
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "after all stored: nRemaining=" + pendingResponseSender.nRemaining
                                                + " nCompleted=" + pendingResponseSender.nCompleted + " nFailed="
                                                + pendingResponseSender.nFailed + " nWarning="
                                                + pendingResponseSender.nWarning);
                                    }
                                    if (pendingResponseSender.nRemaining > 0) {
                                        pendingResponseSender.nFailed += pendingResponseSender.nRemaining;
                                        pendingResponseSender.nRemaining = 0;
                                    }
                                    if (myLogger.isDebugEnabled()) {
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "after setting remaining to zero: nRemaining="
                                                + pendingResponseSender.nRemaining + " nCompleted="
                                                + pendingResponseSender.nCompleted + " nFailed="
                                                + pendingResponseSender.nFailed + " nWarning="
                                                + pendingResponseSender.nWarning);
                                        myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                                + "Bulding final C-GET success response");
                                    }
                                    response = new CGetResponseCommandMessage(
                                            cgrq.getAffectedSOPClassUID(),
                                            cgrq.getMessageID(),
                                            pendingResponseSender.nFailed > 0 ? 0xB000 : 0x0000, // status
                                            // is
                                            // warning
                                            // one
                                            // or
                                            // more
                                            // failure,
                                            // or
                                            // success
                                            // matching
                                            // is
                                            // complete
                                            false, // no dataset, unless there
                                            // was failure, then add
                                            // Failed SOP Instance UID
                                            // List (0008,0058)
                                            pendingResponseSender.nRemaining, pendingResponseSender.nCompleted,
                                            pendingResponseSender.nFailed, pendingResponseSender.nWarning).getBytes();
                                }
                            }
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                    + "Setting done flag for C-GET response");
                            setDone(true);
                            setRelease(false);
                        }
                    } else {
                        if (myLogger.isDebugEnabled()) {
                            myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): "
                                    + "Unexpected data fragment for command 0x" + Integer.toHexString(command) + " "
                                    + MessageServiceElementCommand.toString(command) + " - ignoring");
                        }
                    }
                }
            }
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("CompositeCommandReceivedPDUHandler.sendPDataIndication(): finished; isDone()="
                        + isDone());
            }
        }

        /***/
        public AttributeList getCommandList() {
            return commandList;
        }

        /***/
        public byte[] getResponse() {
            return response;
        }

        /***/
        public byte getPresentationContextIDUsed() {
            return presentationContextIDUsed;
        }

        /***/
        public File getReceivedFile() {
            return receivedFile;
        }

        /***/
        public String getReceivedFileName() {
            return receivedFile == null ? null : receivedFile.getPath();
        }
    }

    /**
     * @param association
     * @exception IOException
     * @exception AReleaseException
     * @exception DicomException
     * @exception DicomNetworkException
     */
    private boolean receiveAndProcessOneRequestMessage(Association association) throws AReleaseException,
            DicomNetworkException, DicomException, IOException {
        myLogger.debug("receiveAndProcessOneRequestMessage(): start");
        CompositeCommandReceivedPDUHandler receivedPDUHandler = new CompositeCommandReceivedPDUHandler(
                savedImagesFolder, queryResponseGeneratorFactory, retrieveResponseGeneratorFactory, debugLevel);
        association.setReceivedDataHandler(receivedPDUHandler);
        myLogger.debug("receiveAndProcessOneRequestMessage(): waitForPDataPDUsUntilHandlerReportsDone");
        association.waitForPDataPDUsUntilHandlerReportsDone(); // throws
        // AReleaseException
        // if release
        // request
        // instead
        myLogger.debug("receiveAndProcessOneRequestMessage(): back from waitForPDataPDUsUntilHandlerReportsDone");
        {
            String receivedFileName = receivedPDUHandler.getReceivedFileName(); // null
            // if
            // C-ECHO
            if (receivedFileName != null) {
                byte pcid = receivedPDUHandler.getPresentationContextIDUsed();
                String ts = association.getTransferSyntaxForPresentationContextID(pcid);
                String callingAE = association.getCallingAETitle();
                receivedObjectHandler.sendReceivedObjectIndication(receivedFileName, ts, callingAE);
            }
        }
        myLogger.debug("receiveAndProcessOneRequestMessage(): sending (final) response");
        byte[] response = receivedPDUHandler.getResponse();
        myLogger.debug("receiveAndProcessOneRequestMessage(): response = "
                + CompositeResponseHandler.dumpAttributeListFromCommandOrData(response, TransferSyntax.Default));
        association.send(receivedPDUHandler.getPresentationContextIDUsed(), response, null);
        myLogger.debug("receiveAndProcessOneRequestMessage(): end");
        boolean moreExpected;
        if (receivedPDUHandler.isToBeReleased()) {
            myLogger.debug("receiveAndProcessOneRequestMessage(): explicitly releasing association");
            association.release();
            moreExpected = false;
        } else {
            moreExpected = true;
        }
        return moreExpected;
    }

    /***/
    private Socket socket;
    /***/
    private String calledAETitle;
    /***/
    private int ourMaximumLengthReceived;
    /***/
    private int socketReceiveBufferSize;
    /***/
    private int socketSendBufferSize;
    /***/
    private File savedImagesFolder;
    /***/
    private ReceivedObjectHandler receivedObjectHandler;
    /***/
    private QueryResponseGeneratorFactory queryResponseGeneratorFactory;
    /***/
    private RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory;
    /***/
    private ApplicationEntityMap applicationEntityMap;
    /***/
    private int debugLevel;

    /**
     * <p>
     * Construct an instance of an association acceptor and storage, query,
     * retrieve and verification SCP to be passed to the constructor of a thread
     * that will be started.
     * </p>
     * 
     * @param socket
     *            the socket on which a transport connection open indication has
     *            been received
     * @param calledAETitle
     *            our AE Title
     * @param ourMaximumLengthReceived
     *            the maximum PDU length that we will offer to receive
     * @param socketReceiveBufferSize
     *            the TCP socket receive buffer size to set (if possible), 0
     *            means leave at the default
     * @param socketSendBufferSize
     *            the TCP socket send buffer size to set (if possible), 0 means
     *            leave at the default
     * @param savedImagesFolder
     *            the folder in which to store received data sets (may be null,
     *            to ignore received data for testing)
     * @param receivedObjectHandler
     *            the handler to call after each data set has been received and
     *            stored
     * @param queryResponseGeneratorFactory
     *            a factory to make handlers to generate query responses from a
     *            supplied query message
     * @param retrieveResponseGeneratorFactory
     *            a factory to make handlers to generate retrieve responses from
     *            a supplied retrieve message
     * @param applicationEntityMap
     *            a map of application entity titles to presentation addresses
     * @param debugLevel
     *            zero for no debugging messages, higher values more verbose
     *            messages
     * @exception IOException
     * @exception DicomException
     * @exception DicomNetworkException
     */
    public IVIMStorageSOPClassSCP(Socket socket, String calledAETitle, int ourMaximumLengthReceived,
            int socketReceiveBufferSize, int socketSendBufferSize, File savedImagesFolder,
            ReceivedObjectHandlerFactory receivedObjectHandlerFactory,
            QueryResponseGeneratorFactory queryResponseGeneratorFactory,
            RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory,
            ApplicationEntityMap applicationEntityMap, int debugLevel) throws DicomNetworkException, DicomException,
            IOException {
        // System.err.println("StorageSOPClassSCP()");
        this.socket = socket;
        this.calledAETitle = calledAETitle;
        this.ourMaximumLengthReceived = ourMaximumLengthReceived;
        this.socketReceiveBufferSize = socketReceiveBufferSize;
        this.socketSendBufferSize = socketSendBufferSize;
        this.savedImagesFolder = savedImagesFolder;
        this.receivedObjectHandler = receivedObjectHandlerFactory.newInstance(this.savedImagesFolder);
        this.queryResponseGeneratorFactory = queryResponseGeneratorFactory;
        this.retrieveResponseGeneratorFactory = retrieveResponseGeneratorFactory;
        this.applicationEntityMap = applicationEntityMap;
        this.debugLevel = debugLevel;
    }

    /**
     * <p>
     * Construct an instance of an association acceptor and storage and
     * verification SCP to be passed to the constructor of a thread that will be
     * started.
     * </p>
     * 
     * @param socket
     *            the socket on which a transport connection open indication has
     *            been received
     * @param calledAETitle
     *            our AE Title
     * @param ourMaximumLengthReceived
     *            the maximum PDU length that we will offer to receive
     * @param socketReceiveBufferSize
     *            the TCP socket receive buffer size to set (if possible), 0
     *            means leave at the default
     * @param socketSendBufferSize
     *            the TCP socket send buffer size to set (if possible), 0 means
     *            leave at the default
     * @param savedImagesFolder
     *            the folder in which to store received data sets (may be null,
     *            to ignore received data for testing)
     * @param receivedObjectHandler
     *            the handler to call after each data set has been received and
     *            stored
     * @param debugLevel
     *            zero for no debugging messages, higher values more verbose
     *            messages
     * @exception IOException
     * @exception DicomException
     * @exception DicomNetworkException
     */
    public IVIMStorageSOPClassSCP(Socket socket, String calledAETitle, int ourMaximumLengthReceived,
            int socketReceiveBufferSize, int socketSendBufferSize, File savedImagesFolder,
            ReceivedObjectHandlerFactory receivedObjectHandlerFactory, int debugLevel) throws DicomNetworkException,
            DicomException, IOException {
        // System.err.println("StorageSOPClassSCP()");
        this.socket = socket;
        this.calledAETitle = calledAETitle;
        this.ourMaximumLengthReceived = ourMaximumLengthReceived;
        this.socketReceiveBufferSize = socketReceiveBufferSize;
        this.socketSendBufferSize = socketSendBufferSize;
        this.savedImagesFolder = savedImagesFolder;
        this.receivedObjectHandler = receivedObjectHandlerFactory.newInstance(this.savedImagesFolder);
        this.queryResponseGeneratorFactory = null;
        this.retrieveResponseGeneratorFactory = null;
        this.applicationEntityMap = null;
        this.debugLevel = debugLevel;
    }

    /**
     * <p>
     * Construct an instance of an association acceptor and storage and
     * verification SCP to be passed to the constructor of a thread that will be
     * started.
     * </p>
     * 
     * @param socket
     *            the socket on which a transport connection open indication has
     *            been received
     * @param calledAETitle
     *            our AE Title
     * @param savedImagesFolder
     *            the folder in which to store received data sets (may be null,
     *            to ignore received data for testing)
     * @param receivedObjectHandler
     *            the handler to call after each data set has been received and
     *            stored
     * @param debugLevel
     *            zero for no debugging messages, higher values more verbose
     *            messages
     * @exception IOException
     * @exception DicomException
     * @exception DicomNetworkException
     */
    public IVIMStorageSOPClassSCP(Socket socket, String calledAETitle, File savedImagesFolder,
            ReceivedObjectHandlerFactory receivedObjectHandlerFactory, int debugLevel) throws DicomNetworkException,
            DicomException, IOException {
        // System.err.println("StorageSOPClassSCP()");
        this.socket = socket;
        this.calledAETitle = calledAETitle;
        this.ourMaximumLengthReceived = AssociationFactory.getDefaultMaximumLengthReceived();
        this.socketReceiveBufferSize = AssociationFactory.getDefaultReceiveBufferSize();
        this.socketSendBufferSize = AssociationFactory.getDefaultSendBufferSize();
        this.savedImagesFolder = savedImagesFolder;
        this.receivedObjectHandler = receivedObjectHandlerFactory.newInstance(this.savedImagesFolder);
        this.queryResponseGeneratorFactory = null;
        this.retrieveResponseGeneratorFactory = null;
        this.applicationEntityMap = null;
        this.debugLevel = debugLevel;
    }

    /**
     * <p>
     * Waits for an association to be initiated (acts as an association
     * acceptor), then waits for storage or verification commands, storing data
     * sets in Part 10 files in the specified folder, until the association is
     * released or the transport connection closes.
     * </p>
     */
    public void run() {
        // System.err.println("StorageSOPClassSCP.run()");
        try {
            myLogger.info("TCP SCP: socket is " + socket.toString());

            Association association = AssociationFactory.createNewAssociation(socket, calledAETitle,
                    ourMaximumLengthReceived, socketReceiveBufferSize, socketSendBufferSize, debugLevel);
            myLogger.debug(association);
            try {
                while (receiveAndProcessOneRequestMessage(association))
                    ;
            } catch (AReleaseException e) {
            }
        } catch (Exception e) {
            myLogger.error("Unexpected exception", e);
        }
    }
}
