/**
 * 
 */
package gov.nih.nci.ivi.dicom.embeddedpacs;

import java.io.*;
import java.util.Properties;

import com.pixelmed.database.DatabaseApplicationProperties;
import com.pixelmed.network.IVIMStorageSOPClassSCPDispatcher;
import com.pixelmed.network.NetworkApplicationInformationFederated;
import com.pixelmed.network.NetworkApplicationProperties;
import com.pixelmed.network.NetworkConfigurationSource;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.ReceivedObjectHandlerFactory;

public class EmbeddedPACS {

//	private DatabaseInformationModel databaseInformationModel;
	private Thread pacsThread = null;
	private IVIMStorageSOPClassSCPDispatcher dispatcher = null;
	
	public EmbeddedPACS(String dumpDir, Properties queryProcessorProperty) throws java.io.IOException, com.pixelmed.dicom.DicomException, com.pixelmed.network.DicomNetworkException 
	{
		Properties properties = new Properties();
		String clientAE = queryProcessorProperty.getProperty("clientAE");
		String serverAE = queryProcessorProperty.getProperty("serverAE");

		// Properties of the embedded pacs
		properties.setProperty("Application.DatabaseFileName", dumpDir);
		properties.setProperty("Application.SavedImagesFolderName", dumpDir + File.separator + "db");

		properties.setProperty("Dicom.ListeningPort",  queryProcessorProperty.getProperty("embeddedPacsPort"));
		properties.setProperty("Dicom.CalledAETitle",  queryProcessorProperty.getProperty("embeddedPacsAE"));
		properties.setProperty("Dicom.CallingAETitle", queryProcessorProperty.getProperty("embeddedPacsAE"));

		// Two endpoints of the C_MOVE Operation
		properties.setProperty("Dicom.RemoteAEs", serverAE);

		//Their Properties
		properties.setProperty("Dicom.RemoteAEs."+serverAE+".CalledAETitle", queryProcessorProperty.getProperty("serverAE"));
		properties.setProperty("Dicom.RemoteAEs."+serverAE+".HostNameOrIPAddress", queryProcessorProperty.getProperty("serverip"));
		properties.setProperty("Dicom.RemoteAEs."+serverAE+".Port", queryProcessorProperty.getProperty("serverport"));
		properties.setProperty("Dicom.RemoteAEs."+serverAE+".QueryModel", "STUDYROOT");

//		properties.setProperty("PrimaryDeviceType", queryProcessorProperty.getProperty(""));
		DatabaseApplicationProperties databaseApplicationProperties = new DatabaseApplicationProperties(properties);
		File savedImagesFolder = databaseApplicationProperties.getSavedImagesFolderCreatingItIfNecessary();
		System.out.println(databaseApplicationProperties.getSavedImagesFolderName());
		System.out.println(savedImagesFolder.getPath());
//		databaseInformationModel = new PatientStudySeriesConcatenationInstanceModel(databaseApplicationProperties.getDatabaseFileName());
		NetworkApplicationProperties networkApplicationProperties = new NetworkApplicationProperties(properties);
		NetworkApplicationInformationFederated federatedNetworkApplicationInformation = new NetworkApplicationInformationFederated();

		try {
			NetworkConfigurationSource networkConfigurationFromProperties = networkApplicationProperties.getNetworkConfigurationSource();
			federatedNetworkApplicationInformation.addSource(networkConfigurationFromProperties);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}

		int port = networkApplicationProperties.getListeningPort();
		String calledAETitle = networkApplicationProperties.getCalledAETitle();
		int storageSCPDebugLevel = networkApplicationProperties.getStorageSCPDebugLevel();
		int queryDebugLevel = networkApplicationProperties.getQueryDebugLevel();
		int storageSCUDebugLevel = networkApplicationProperties.getStorageSCUDebugLevel();

/*		this.dispatcher = 
			new IVIMStorageSOPClassSCPDispatcher(
					port,calledAETitle,savedImagesFolder,
					new EmbeddedPACSReceivedObjectHandler(
							savedImagesFolder.toString()),
							null,
							null,
							federatedNetworkApplicationInformation,
							false,
							storageSCPDebugLevel);
*/
		this.dispatcher = new IVIMStorageSOPClassSCPDispatcher(
				port,calledAETitle,savedImagesFolder,
				new EmbeddedPACSReceivedObjectHandler(
						savedImagesFolder.toString()),
						null,
						null,
						federatedNetworkApplicationInformation,
						false/*secureTransport*/,
						0);
		this.dispatcher.useAsEmbeddedPACS(true);
		this.pacsThread = new Thread(this.dispatcher);	
	}
@Deprecated
	public EmbeddedPACS(String dumpdir, int port, String aetitle) throws java.io.IOException, com.pixelmed.dicom.DicomException, com.pixelmed.network.DicomNetworkException {
        System.out.println("dumpdir came in as " + dumpdir);
        assert(new File(dumpdir).exists());

        String propstring =
"# Test DicomAndWebStorageServer properties file\n" +
"#\n" +
"# Where to store the database support files\n" +
"#\n" +
"Application.DatabaseFileName=" + dumpdir + File.separator + "db\n" +
"#\n" +
"# Where to store the images stored in the database\n" +
"#\n" +
"Application.SavedImagesFolderName=" + dumpdir + "\n"+
"#\n" +
"# Dicom.CalledAETitle should be set to whatever this DicomImageViewer application is to\n" +
"# call itself when accepting an association.\n" +
"#\n" +
"Dicom.CalledAETitle=" + aetitle + "\n" +
"#\n" +
"# Dicom.CallingAETitle should be set to whatever this DicomImageViewer application is to\n" +
"# call itself when initiating an association.\n" +
"#\n" +
"Dicom.CallingAETitle=" + aetitle + "\n" +
"#\n" +
"# WebServer.ListeningPort should be set to whatever port the web server listens\n" +
"# on for incoming connections.\n" +
"#\n" +
"# WebServer.ListeningPort=7091\n" +
"#\n" +
"# Dicom.ListeningPort should be set to whatever port this DicomImageViewer application is to\n" +
"# listen on to accept incoming associations.\n" +
"#\n" +
"Dicom.ListeningPort="+port+"\n" +
"#\n" +
"# The root URL for the WebServer\n" +
"#\n" +
"WebServer.RootURL=\n" +
"#\n" +
"# The name of the syylesheet for the WebServer\n" +
"#\n" +
"WebServer.StylesheetPath=stylesheet.css\n" +
"#\n" +
"# WebServer.DebugLevel should be 0 for no debugging (silent), > 0 for more\n" +
"# verbose levels of debugging\n" +
"#\n" +
"WebServer.DebugLevel=0\n" +
"#\n" +
"# Dicom.StorageSCUCompressionLevel determines what types of compressed Transfer Syntaxes are\n" +
"# proposed:\n" +
"#	0 = none\n" +
"#	1 = propose deflate\n" +
"#	2 = propose deflate and bzip2 (if bzip2 codec is available)\n" +
"#\n" +
"Dicom.StorageSCUCompressionLevel=0\n" +
"#\n" +
"# Dicom.StorageSCUDebugLevel should be 0 for no debugging (silent), > 0 for more\n" +
"# verbose levels of debugging\n" +
"#\n" +
"Dicom.StorageSCUDebugLevel=0\n" +
"#\n" +
"# Dicom.StorageSCPDebugLevel should be 0 for no debugging (silent), > 0 for more\n" +
"# verbose levels of debugging\n" +
"#\n" +
"Dicom.StorageSCPDebugLevel=0\n" +
"#\n" +
"# Dicom.QueryDebugLevel should be 0 for no debugging (silent), > 0 for more\n" +
"# verbose levels of debugging\n" +
"#\n" +
"Dicom.QueryDebugLevel=0\n" +
"#\n" +
"# Dicom.RemoteAEs is a space or comma separated list of all the available remote AEs;\n" +
"# each AE may be named anything unique (in this file) without a space or comma; the name\n" +
"# does not need to be the same as the actual AE title.\n" +
"#\n" +
"Dicom.RemoteAEs=defiance vmstor01 vmstor02 localhost cabig1 dc02 gridvpacs\n" +
"#\n" +
"# Each remote AE (listed in Dicom.RemoteAEs) needs to be described by three\n" +
"# properties:\n" +
"# Dicom.RemoteAEs.XXXXX.CalledAETitle\n" +
"# Dicom.RemoteAEs.XXXXX.HostNameOrIPAddress\n" +
"# Dicom.RemoteAEs.XXXXX.Port\n" +
"#\n" +
"# where XXXXX is the name of the AE displayed to the user and used in this file\n" +
"#\n" +
"Dicom.RemoteAEs.defiance.CalledAETitle=DEFIANCE\n" +
"Dicom.RemoteAEs.defiance.HostNameOrIPAddress=140.254.80.41\n" +
"Dicom.RemoteAEs.defiance.Port=4051\n" +
"Dicom.RemoteAEs.defiance.QueryModel=STUDYROOT\n" +
"#\n" +
"Dicom.RemoteAEs.vmstor01.CalledAETitle=VMSTOR01\n" +
"Dicom.RemoteAEs.vmstor01.HostNameOrIPAddress=140.254.80.15\n" +
"Dicom.RemoteAEs.vmstor01.Port=4052\n" +
"Dicom.RemoteAEs.vmstor01.QueryModel=STUDYROOT\n" +
"#\n" +
"Dicom.RemoteAEs.vmstor02.CalledAETitle=VMSTOR02\n" +
"Dicom.RemoteAEs.vmstor02.HostNameOrIPAddress=140.254.80.16\n" +
"Dicom.RemoteAEs.vmstor02.Port=4053\n" +
"Dicom.RemoteAEs.vmstor02.QueryModel=STUDYROOT\n" +
"#\n" +
"Dicom.RemoteAEs.localhost.CalledAETitle=LOCALHOST\n" +
"Dicom.RemoteAEs.localhost.HostNameOrIPAddress=127.0.0.1\n" +
"Dicom.RemoteAEs.localhost.Port=4054\n" +
"Dicom.RemoteAEs.localhost.QueryModel=STUDYROOT\n" +
"#\n" +
"Dicom.RemoteAEs.cabig1.CalledAETitle=CABIG1\n" +
"Dicom.RemoteAEs.cabig1.HostNameOrIPAddress=140.254.80.136\n" +
"Dicom.RemoteAEs.cabig1.Port=4055\n" +
"Dicom.RemoteAEs.cabig1.QueryModel=STUDYROOT\n" +
"#\n" +
"Dicom.RemoteAEs.dc02.CalledAETitle=DC02\n" +
"Dicom.RemoteAEs.dc02.HostNameOrIPAddress=dc02.bmi.ohio-state.edu\n" +
"Dicom.RemoteAEs.dc02.Port=4008\n" +
"Dicom.RemoteAEs.dc02.QueryModel=STUDYROOT\n" +
"#\n" +
"Dicom.RemoteAEs.gridvpacs.CalledAETitle=GRIDVPACS\n" +
"Dicom.RemoteAEs.gridvpacs.HostNameOrIPAddress=140.254.80.217\n" +
"Dicom.RemoteAEs.gridvpacs.Port=4018\n" +
"Dicom.RemoteAEs.gridvpacs.QueryModel=STUDYROOT\n";

        //System.out.println("propstring is " + propstring);
        
        Properties properties = new Properties();
        
        String tempfn = File.createTempFile("tmp", "").getCanonicalPath();
        FileOutputStream f;
        f = new FileOutputStream(tempfn);
        f.write(propstring.getBytes());
        FileInputStream in = new FileInputStream(tempfn);
        properties = new Properties(/*defaultProperties*/);
        properties.load(in);
        in.close();
        new File(tempfn).delete();
        
		DatabaseApplicationProperties databaseApplicationProperties = new DatabaseApplicationProperties(properties);
		File savedImagesFolder = databaseApplicationProperties.getSavedImagesFolderCreatingItIfNecessary();
		System.out.println(databaseApplicationProperties.getSavedImagesFolderName());
		System.out.println(savedImagesFolder.getPath());
//		databaseInformationModel = new PatientStudySeriesConcatenationInstanceModel(databaseApplicationProperties.getDatabaseFileName());
		NetworkApplicationProperties networkApplicationProperties = new NetworkApplicationProperties(properties);
		NetworkApplicationInformationFederated federatedNetworkApplicationInformation = new NetworkApplicationInformationFederated();

		try {
			NetworkConfigurationSource networkConfigurationFromProperties = networkApplicationProperties.getNetworkConfigurationSource();
			federatedNetworkApplicationInformation.addSource(networkConfigurationFromProperties);
		}
		catch (Exception e) {
			e.printStackTrace(System.err);
		}

		// Start up DICOM association listener in background for receiving images and responding to echoes and queries and retrieves ...
//		System.err.println("Starting up DICOM association listener ...");
		{
			int port2 = networkApplicationProperties.getListeningPort();
			String calledAETitle = networkApplicationProperties.getCalledAETitle();
			int storageSCPDebugLevel = networkApplicationProperties.getStorageSCPDebugLevel();
			int queryDebugLevel = networkApplicationProperties.getQueryDebugLevel();
			int storageSCUDebugLevel = networkApplicationProperties.getStorageSCUDebugLevel();

			this.dispatcher = 
                new IVIMStorageSOPClassSCPDispatcher(
                    port2,calledAETitle,savedImagesFolder,
					new EmbeddedPACSReceivedObjectHandler(
                        savedImagesFolder.toString()),
                    null,
                    null,
					federatedNetworkApplicationInformation,
					false/*secureTransport*/,
					storageSCPDebugLevel);
			this.pacsThread = new Thread(this.dispatcher);
		}
	}
	
	public void start() {
		this.pacsThread.setDaemon(true);
		if (!this.pacsThread.isAlive() && !this.pacsThread.isInterrupted()) {
			this.pacsThread.start();
			System.out.println("PACS started");
		} else {
			System.err.println("WARNING: this thread is not alive or is interrupted");
		}
	}
	public void stop() {
		if (this.pacsThread.isAlive()) {
			this.dispatcher.stop();
			System.out.println("PACS stop requested");
		}
	}
	


	/**
	 * <p>Wait for connections and process requests, storing received files in a database.</p>
	 *
	 * @param	arg	a single file name that is the properties file
	 * @see com.pixelmed.database.DatabaseApplicationProperties
	 * @see com.pixelmed.network.NetworkApplicationProperties
	 * @see com.pixelmed.web.WebServerApplicationProperties
	 */
	public static void main(String arg[]) {
// 		String propertiesFileName = arg[0];
// 		try {
// 			new EmbeddedPACS(propertiesFileName);
// 		}
// 		catch (Exception e) {
// 			System.err.println(e);
// 		}
	}
}



