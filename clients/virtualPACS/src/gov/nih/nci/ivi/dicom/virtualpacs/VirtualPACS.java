/**
 * 
 */
package gov.nih.nci.ivi.dicom.virtualpacs;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.dicom.virtualpacs.database.DICOMcaGridQRGFactory;
import gov.nih.nci.ivi.dicom.virtualpacs.database.DICOMcaGridRRGFactory;
import gov.nih.nci.ivi.dicom.virtualpacs.database.VirtualPACSReceivedObjectHandlerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import com.pixelmed.database.DatabaseApplicationProperties;
import com.pixelmed.network.IVIMStorageSOPClassSCPDispatcher;
import com.pixelmed.network.NetworkApplicationInformationFederated;
import com.pixelmed.network.NetworkApplicationProperties;
import com.pixelmed.network.NetworkConfigurationSource;
import com.pixelmed.network.ReceivedObjectHandler;
/**
 * @author ashish@bmi.osu.edu
 * @author tpan@bmi.osu.edu
 *
 */
public class VirtualPACS {
	public static final String MODELMAP_PROPERTIES_FILE = "ModelMapPropertiesFile"; 
	
	
//	private DatabaseInformationModel databaseInformationModel;
	private Thread pacsThread = null;
	private IVIMStorageSOPClassSCPDispatcher dispatcher = null;
//	private StorageSOPClassSCPDispatcher dispatcher = null;
	
	private String[] dataServiceURLs;
	
	public VirtualPACS(Properties properties, String[] dataServiceURLs) throws java.io.IOException, com.pixelmed.dicom.DicomException, com.pixelmed.network.DicomNetworkException {
		this.dataServiceURLs = dataServiceURLs;
		if (dataServiceURLs == null || dataServiceURLs.length == 0) {
			System.err.println("ERROR: no data services specified.  Exiting");
			return;
		}
		
		DatabaseApplicationProperties databaseApplicationProperties = new DatabaseApplicationProperties(properties);
		
		//TODO: The virtualPACS temp folder should NOT be created in the user's home directory
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
			int port = networkApplicationProperties.getListeningPort();
			String calledAETitle = networkApplicationProperties.getCalledAETitle();
			int storageSCPDebugLevel = networkApplicationProperties.getStorageSCPDebugLevel();
			int queryDebugLevel = networkApplicationProperties.getQueryDebugLevel();
			int storageSCUDebugLevel = networkApplicationProperties.getStorageSCUDebugLevel();

			// TODO later, get this from properties file, or through discovery
			// don't need this
			// String modelMapPropertiesFile = properties.getProperty(VirtualPACS.MODELMAP_PROPERTIES_FILE);
			ModelMap tmap = null;
			try {
				tmap = new ModelMap();
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			final ModelMap map = tmap;
/*			
			this.dispatcher = new StorageSOPClassSCPDispatcher(port, calledAETitle, savedImagesFolder, 
					null, new DICOMcaGridQRGFactory(dataServiceURLs, map),
					new DICOMcaGridRRGFactory(dataServiceURLs, map),
					federatedNetworkApplicationInformation, false, storageSCUDebugLevel);
*/			
			this.dispatcher = new IVIMStorageSOPClassSCPDispatcher(port, calledAETitle, savedImagesFolder, 
					(ReceivedObjectHandler)null,
					new VirtualPACSReceivedObjectHandlerFactory(dataServiceURLs, null),
					new DICOMcaGridQRGFactory(dataServiceURLs, map),
					new DICOMcaGridRRGFactory(dataServiceURLs, map),
					federatedNetworkApplicationInformation, false, storageSCUDebugLevel);
			
			this.pacsThread = new Thread(this.dispatcher);
		}
	}

	public void start() {
		if (dataServiceURLs == null || dataServiceURLs.length == 0) {
			System.err.println("ERROR: no data services specified.  Exiting");
			return;
		}
		
		
		this.pacsThread.setDaemon(true);
		if (!this.pacsThread.isAlive() && !this.pacsThread.isInterrupted()) {
			this.pacsThread.start();
			System.out.println("PACS started");
		} else {
			System.err.println("WARNING: this thread is not alive or is interrupted");
		}
	}
	public void stop() {
//		if (dataServiceURLs == null || dataServiceURLs.length == 0) {
//			System.err.println("ERROR: no data services specified.  Exiting");
//			return;
//		}
		
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
/*		if (arg.length <= 0) {
			System.err.println("please supply the property filename");
			return;
		}
		String propertiesFileName = arg[0];

		File logFile = new File("resources/logfile.log");
		if(!logFile.exists())
			try {
				logFile.createNewFile();
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		BufferedOutputStream beos;
		try {
			beos = new BufferedOutputStream(new FileOutputStream(logFile));
			System.setErr(new PrintStream(beos, true));
			System.setOut(new PrintStream(beos, true));
		} catch (FileNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
*/
		try {
			FileInputStream in = new FileInputStream("resources/virtualPACS.properties");
			Properties properties = new Properties(/*defaultProperties*/);
			properties.load(in);
			in.close();
			System.err.println("properties="+properties);
			String[] services = new String[] { "http://127.0.0.1:8080/wsrf/services/cagrid/DICOMDataService"};
			new VirtualPACS(properties, services).start();	
		}
		catch (Exception e) {
			System.err.println(e);
		}
	}
}



