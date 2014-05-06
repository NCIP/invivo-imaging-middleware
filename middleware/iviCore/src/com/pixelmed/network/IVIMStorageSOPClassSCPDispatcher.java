/* Copyright (c) 2001-2005, David A. Clunie DBA Pixelmed Publishing. All rights reserved. */

package com.pixelmed.network;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.StoredFilePathStrategy;
import com.pixelmed.query.QueryResponseGeneratorFactory;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;

/**
 * This class is a modification of the
 * com.pixelmed.network.StorageSOPClassSCPDispatcher. The modifications are so
 * that the serversockets are closed appropriately after the thread terminates
 * The reason it's in the com.pixelmed.network pacakge rather than
 * gov.nih.nci.ivi... is because Association class has 2 methods that are
 * private and are used here, so we need to be in the same package as the
 * original StorageSOPClassSCPDispatcher.
 * 
 * C-STORE operation is performed on a single image at a time. has to handle
 * upload piece by piece.
 * 
 * <p>
 * This class waits for incoming connections and association requests for the
 * SCP role of SOP Classes of the Storage Service Class, the Study Root Query
 * Retrieve Information Model Find, Get and Move SOP Classes, and the
 * Verification SOP Class.
 * </p>
 * 
 * <p>
 * The class has a constructor and a <code>run()</code> method. The
 * constructor is passed a socket on which to listen for transport connection
 * open indications. The <code>run()</code> method waits for transport
 * connection open indications, then instantiates
 * {@link com.pixelmed.network.IVIMStorageSOPClassSCP StorageSOPClassSCP} to
 * accept an association and wait for storage or verification commands, storing
 * data sets in Part 10 files in the specified folder.
 * </p>
 * 
 * <p>
 * An instance of
 * {@link com.pixelmed.network.ReceivedObjectHandler ReceivedObjectHandler} can
 * be supplied in the constructor to process the received data set stored in the
 * file when it has been completely received.
 * </p>
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <pre>
 * try {
 * 	new Thread(new StorageSOPClassSCPDispatcher(&quot;104&quot;, &quot;STORESCP&quot;, &quot;/tmp&quot;,
 * 			new OurReceivedObjectHandler(), 0)).start();
 * } catch (IOException e) {
 * 	e.printStackTrace(System.err);
 * }
 * </pre>
 * 
 * <p>
 * Debugging messages with a varying degree of verbosity can be activated.
 * </p>
 * 
 * <p>
 * The main method is also useful in its own right as a command-line Storage SCP
 * utility, which will store incoming files in a specified directory.
 * </p>
 * 
 * <p>
 * For example:
 * </p>
 * 
 * <pre>
 *  % java -cp ./pixelmed.jar com.pixelmed.network.StorageSOPClassSCPDispatcher &quot;104&quot; &quot;STORESCP&quot; &quot;/tmp&quot; 0
 * </pre>
 * 
 * @see com.pixelmed.network.IVIMStorageSOPClassSCP
 * @see com.pixelmed.network.ReceivedObjectHandler
 * 
 * @author dclunie
 */
public class IVIMStorageSOPClassSCPDispatcher implements Runnable {
	/***/
	private static final String identString = "@(#) $Header: /cvsshare/content/gforge/middleware/middleware/projects/iviCore/src/com/pixelmed/network/IVIMStorageSOPClassSCPDispatcher.java,v 1.9 2007/02/08 19:25:27 ashishof77 Exp $";

	// TCP
	private volatile ServerSocket serverSocket = null;
	private volatile boolean stopped = false;

	// EmbeddedPACS usage thread
	private boolean useForEmbeddedPACS = false;

	public void stop() {
		try {
			this.stopped = true;
			this.serverSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	// TCP

	/***/
	private class DefaultReceivedObjectHandler extends ReceivedObjectHandler {
		/**
		 * @param fileName
		 * @param transferSyntax
		 *            the transfer syntax in which the data set was received and
		 *            is stored
		 * @param callingAETitle
		 *            the AE title of the caller who sent the data set
		 * @exception IOException
		 * @exception DicomException
		 * @exception DicomNetworkException
		 */
		public void sendReceivedObjectIndication(String fileName,
				String transferSyntax, String callingAETitle)
				throws DicomNetworkException, DicomException, IOException {
			System.err
					.println("StorageSOPClassSCPDispatcher.DefaultReceivedObjectHandler.sendReceivedObjectIndication() fileName: "
							+ fileName
							+ " from "
							+ callingAETitle
							+ " in "
							+ transferSyntax);
		}
	}

	/***/
	private int port;
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
	private ReceivedObjectHandlerFactory receivedObjectHandlerFactory;
	/***/
	private QueryResponseGeneratorFactory queryResponseGeneratorFactory;
	/***/
	private RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory;
	/***/
	private NetworkApplicationInformation networkApplicationInformation;
	/***/
	private boolean secureTransport;
	/***/
	private int debugLevel;

	/**
	 * <p>
	 * Construct an instance of dispatcher that will wait for transport
	 * connection open indications, and handle associations and commands.
	 * </p>
	 * 
	 * @param port
	 *            the port on which to listen for connections
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
	 */
	public IVIMStorageSOPClassSCPDispatcher(int port, String calledAETitle,
			File savedImagesFolder,
			ReceivedObjectHandler receivedObjectHandler, int debugLevel)
			throws IOException {
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.ourMaximumLengthReceived = AssociationFactory
				.getDefaultMaximumLengthReceived();
		this.socketReceiveBufferSize = AssociationFactory
				.getDefaultReceiveBufferSize();
		this.socketSendBufferSize = AssociationFactory
				.getDefaultSendBufferSize();
		this.savedImagesFolder = savedImagesFolder;
		this.receivedObjectHandler = receivedObjectHandler;
		this.receivedObjectHandlerFactory = null;
		this.queryResponseGeneratorFactory = null;
		this.retrieveResponseGeneratorFactory = null;
		this.networkApplicationInformation = null;
		this.debugLevel = debugLevel;
		this.secureTransport = false;
	}

	/**
	 * <p>
	 * Construct an instance of dispatcher that will wait for transport
	 * connection open indications, and handle associations and commands.
	 * </p>
	 * 
	 * @param port
	 *            the port on which to listen for connections
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
	 *            stored, or null for the default that prints the file name
	 * @param queryResponseGeneratorFactory
	 *            the factory to make handlers to generate query responses from
	 *            a supplied query message
	 * @param retrieveResponseGeneratorFactory
	 *            the factory to make handlers to generate retrieve responses
	 *            from a supplied retrieve message
	 * @param networkApplicationInformation
	 *            from which to obtain a map of application entity titles to
	 *            presentation addresses
	 * @param secureTransport
	 *            true if to use secure transport protocol
	 * @param debugLevel
	 *            zero for no debugging messages, higher values more verbose
	 *            messages
	 * @exception IOException
	 */
	public IVIMStorageSOPClassSCPDispatcher(int port, String calledAETitle,
			int ourMaximumLengthReceived, int socketReceiveBufferSize,
			int socketSendBufferSize, File savedImagesFolder,
			ReceivedObjectHandler receivedObjectHandler,
			QueryResponseGeneratorFactory queryResponseGeneratorFactory,
			RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory,
			NetworkApplicationInformation networkApplicationInformation,
			boolean secureTransport, int debugLevel) throws IOException {
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.ourMaximumLengthReceived = ourMaximumLengthReceived;
		this.socketReceiveBufferSize = socketReceiveBufferSize;
		this.socketSendBufferSize = socketSendBufferSize;
		this.savedImagesFolder = savedImagesFolder;
		this.receivedObjectHandler = receivedObjectHandler == null ? new DefaultReceivedObjectHandler()
				: receivedObjectHandler;
		this.receivedObjectHandlerFactory = null;
		this.queryResponseGeneratorFactory = queryResponseGeneratorFactory;
		this.retrieveResponseGeneratorFactory = retrieveResponseGeneratorFactory;
		this.networkApplicationInformation = networkApplicationInformation;
		this.debugLevel = debugLevel;
		this.secureTransport = secureTransport;
	}

	/**
	 * <p>
	 * Construct an instance of dispatcher that will wait for transport
	 * connection open indications, and handle associations and commands.
	 * </p>
	 * 
	 * @param port
	 *            the port on which to listen for connections
	 * @param calledAETitle
	 *            our AE Title
	 * @param savedImagesFolder
	 *            the folder in which to store received data sets (may be null,
	 *            to ignore received data for testing)
	 * @param receivedObjectHandler
	 *            the handler to call after each data set has been received and
	 *            stored, or null for the default that prints the file name
	 * @param queryResponseGeneratorFactory
	 *            the factory to make handlers to generate query responses from
	 *            a supplied query message
	 * @param retrieveResponseGeneratorFactory
	 *            the factory to make handlers to generate retrieve responses
	 *            from a supplied retrieve message
	 * @param networkApplicationInformation
	 *            from which to obtain a map of application entity titles to
	 *            presentation addresses
	 * @param secureTransport
	 *            true if to use secure transport protocol
	 * @param debugLevel
	 *            zero for no debugging messages, higher values more verbose
	 *            messages
	 * @exception IOException
	 */
	public IVIMStorageSOPClassSCPDispatcher(int port, String calledAETitle,
			File savedImagesFolder,
			ReceivedObjectHandler receivedObjectHandler,
			QueryResponseGeneratorFactory queryResponseGeneratorFactory,
			RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory,
			NetworkApplicationInformation networkApplicationInformation,
			boolean secureTransport, int debugLevel) throws IOException {
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.ourMaximumLengthReceived = AssociationFactory
				.getDefaultMaximumLengthReceived();
		this.socketReceiveBufferSize = AssociationFactory
				.getDefaultReceiveBufferSize();
		this.socketSendBufferSize = AssociationFactory
				.getDefaultSendBufferSize();
		this.savedImagesFolder = savedImagesFolder;
		this.receivedObjectHandler = receivedObjectHandler == null ? new DefaultReceivedObjectHandler()
				: receivedObjectHandler;
		this.receivedObjectHandlerFactory = null;
		this.queryResponseGeneratorFactory = queryResponseGeneratorFactory;
		this.retrieveResponseGeneratorFactory = retrieveResponseGeneratorFactory;
		this.networkApplicationInformation = networkApplicationInformation;
		this.debugLevel = debugLevel;
		this.secureTransport = secureTransport;
	}

	/**
	 * <p>
	 * Construct an instance of dispatcher that will wait for transport
	 * connection open indications, and handle associations and commands.
	 * </p>
	 * 
	 * @param port
	 *            the port on which to listen for connections
	 * @param calledAETitle
	 *            our AE Title
	 * @param savedImagesFolder
	 *            the folder in which to store received data sets (may be null,
	 *            to ignore received data for testing)
	 * @param receivedObjectHandler
	 *            the handler to call after each data set has been received and
	 *            stored, or null for the default that prints the file name
	 * @param queryResponseGeneratorFactory
	 *            the factory to make handlers to generate query responses from
	 *            a supplied query message
	 * @param retrieveResponseGeneratorFactory
	 *            the factory to make handlers to generate retrieve responses
	 *            from a supplied retrieve message
	 * @param networkApplicationInformation
	 *            from which to obtain a map of application entity titles to
	 *            presentation addresses
	 * @param secureTransport
	 *            true if to use secure transport protocol
	 * @param debugLevel
	 *            zero for no debugging messages, higher values more verbose
	 *            messages
	 * @exception IOException
	 */
	public IVIMStorageSOPClassSCPDispatcher(int port, String calledAETitle,
			File savedImagesFolder,
			ReceivedObjectHandler receivedObjectHandler,
			ReceivedObjectHandlerFactory receivedObjectHandlerFactory,
			QueryResponseGeneratorFactory queryResponseGeneratorFactory,
			RetrieveResponseGeneratorFactory retrieveResponseGeneratorFactory,
			NetworkApplicationInformation networkApplicationInformation,
			boolean secureTransport, int debugLevel) throws IOException {
		this.port = port;
		this.calledAETitle = calledAETitle;
		this.ourMaximumLengthReceived = AssociationFactory
				.getDefaultMaximumLengthReceived();
		this.socketReceiveBufferSize = AssociationFactory
				.getDefaultReceiveBufferSize();
		this.socketSendBufferSize = AssociationFactory
				.getDefaultSendBufferSize();
		this.savedImagesFolder = savedImagesFolder;
		this.receivedObjectHandler = new DefaultReceivedObjectHandler();
		this.receivedObjectHandlerFactory = receivedObjectHandlerFactory;
		this.queryResponseGeneratorFactory = queryResponseGeneratorFactory;
		this.retrieveResponseGeneratorFactory = retrieveResponseGeneratorFactory;
		this.networkApplicationInformation = networkApplicationInformation;
		this.debugLevel = debugLevel;
		this.secureTransport = secureTransport;
	}

	public void useAsEmbeddedPACS(boolean flag) {
		if (flag)
			this.useForEmbeddedPACS = true;
		else
			this.useForEmbeddedPACS = false;
	}

	/**
	 * <p>
	 * Waits for a transport connection indications, then spawns new threads to
	 * act as association acceptors, which then wait for storage or verification
	 * commands, storing data sets in Part 10 files in the specified folder,
	 * until the associations are released or the transport connections are
	 * closed.
	 * </p>
	 */
	public void run() {
		// System.err.println("StorageSOPClassSCPDispatcher.run():");
		try {
			if (debugLevel > 1)
				System.err
						.println("StorageSOPClassSCPDispatcher.run(): Trying to bind to port "
								+ port);
			if (secureTransport) {
				SSLServerSocketFactory sslserversocketfactory = (SSLServerSocketFactory) SSLServerSocketFactory
						.getDefault();
				SSLServerSocket sslserversocket = (SSLServerSocket) sslserversocketfactory
						.createServerSocket(port);
				String[] suites = Association
						.getCipherSuitesToEnable(sslserversocket
								.getSupportedCipherSuites());
				if (suites != null) {
					sslserversocket.setEnabledCipherSuites(suites);
				}
				String[] protocols = Association
						.getProtocolsToEnable(sslserversocket
								.getEnabledProtocols());
				if (protocols != null) {
					sslserversocket.setEnabledProtocols(protocols);
				}
				// sslserversocket.setNeedClientAuth(true);
				serverSocket = sslserversocket;
			} else {
				serverSocket = new ServerSocket(port);
			}

			while (!this.serverSocket.isClosed()) {
				Socket socket = serverSocket.accept();
				System.out.println("TCP: SCPDispatcher socket = "
						+ socket.toString());
				// System.err.println("StorageSOPClassSCPDispatcher.run():
				// returned from accept");
				// setSocketOptions(socket,ourMaximumLengthReceived,socketReceiveBufferSize,socketSendBufferSize,debugLevel);
				// defer loading applicationEntityMap until each incoming
				// connection, since may have been updated
				ApplicationEntityMap applicationEntityMap = null;
				if (networkApplicationInformation != null) {
					applicationEntityMap = networkApplicationInformation
							.getApplicationEntityMap();
				}
				if (applicationEntityMap == null) {
					applicationEntityMap = new ApplicationEntityMap();
				}
				{
					// add ourselves to AET map, if not already there, in case
					// we want to C-MOVE to ourselves
					InetAddress ourAddress = serverSocket.getInetAddress();
					if (ourAddress != null
							&& applicationEntityMap.get(calledAETitle) == null) {
						applicationEntityMap
								.put(
										calledAETitle,
										new PresentationAddress(ourAddress
												.getHostAddress(), port),
										NetworkApplicationProperties.StudyRootQueryModel/* hmm... :( */,
										null/* primaryDeviceType */);
					}
					if (debugLevel > 1)
						System.err
								.println("StorageSOPClassSCPDispatcher:run(): applicationEntityMap = "
										+ applicationEntityMap);
				}

				try {
					if (this.useForEmbeddedPACS) {
						new Thread(new StorageSOPClassSCP(socket,
								calledAETitle, ourMaximumLengthReceived,
								socketReceiveBufferSize, socketSendBufferSize,
								savedImagesFolder, StoredFilePathStrategy
										.getDefaultStrategy(),
								receivedObjectHandler,
								queryResponseGeneratorFactory,
								retrieveResponseGeneratorFactory,
								applicationEntityMap, debugLevel)).start();
					} else {
						// TCP: for handling storage requests, need separate
						// folders (and clean up), and separate received object
						// handler
						File imageFolder = File.createTempFile("store", "tmp",
								this.savedImagesFolder);
						if (imageFolder.exists() && !imageFolder.isDirectory()) {
							if (!imageFolder.delete()) {
								System.err
										.println("temp file is not deleted properly");
							}
						}
						if (!imageFolder.mkdirs()) {
							System.err
									.println("temp directory is not created properly");
						}

						new Thread(new IVIMStorageSOPClassSCP(socket,
								calledAETitle, ourMaximumLengthReceived,
								socketReceiveBufferSize, socketSendBufferSize,
								imageFolder, this.receivedObjectHandlerFactory,
								queryResponseGeneratorFactory,
								retrieveResponseGeneratorFactory,
								applicationEntityMap, debugLevel)).start();
					}

				} catch (Exception e) {
					e.printStackTrace(System.err);
				}
			}

		} catch (IOException e) {
			if (this.stopped) {
				System.out.println(" PACS listener stopped ");
			} else {
				e.printStackTrace(System.err);
			}
		}
	}

	/**
	 * <p>
	 * For testing.
	 * </p>
	 * 
	 * <p>
	 * Wait for connections, accept associations and store received files in the
	 * specified folder.
	 * </p>
	 * 
	 * @param arg
	 *            array of five or eight strings - our port, our AE Title,
	 *            optionally the max PDU size, socket receive and send buffer
	 *            sizes, the folder in which to stored received files (zero
	 *            length if want to ignore received data), a string flag valued
	 *            SECURE or other, and the debugging level
	 */
	public static void main(String arg[]) {
		try {
			IVIMStorageSOPClassSCPDispatcher dispatcher = null;
			File savedImagesFolder = null;
			if (arg.length == 5) {
				String savedImagesFolderName = arg[2];
				if (savedImagesFolderName != null
						&& savedImagesFolderName.length() > 0) {
					savedImagesFolder = new File(savedImagesFolderName);
				}
				dispatcher = new IVIMStorageSOPClassSCPDispatcher(Integer
						.parseInt(arg[0]), arg[1], savedImagesFolder, null,
						null, null, null,
						arg[3].toUpperCase().equals("SECURE"), Integer
								.parseInt(arg[4])/* debugLevel */);
			} else if (arg.length == 8) {
				String savedImagesFolderName = arg[5];
				if (savedImagesFolderName != null
						&& savedImagesFolderName.length() > 0) {
					savedImagesFolder = new File(savedImagesFolderName);
				}
				dispatcher = new IVIMStorageSOPClassSCPDispatcher(Integer
						.parseInt(arg[0]), arg[1], Integer.parseInt(arg[2]),
						Integer.parseInt(arg[3]), Integer.parseInt(arg[4]),
						savedImagesFolder, null, null, null, null, arg[6]
								.toUpperCase().equals("SECURE"), Integer
								.parseInt(arg[7])/* debugLevel */);
			}
			new Thread(dispatcher).start();
		} catch (Exception e) {
			e.printStackTrace(System.err);
			System.exit(0);
		}
	}
}
