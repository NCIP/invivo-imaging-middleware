package gov.nih.nci.ivi.dicom.virtualpacs.database;

import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.ncia.client.NCIACoreServiceClient;
import gov.nih.nci.ivi.dicom.DICOM2CQL;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.dicomdataservice.client.DICOMDataServiceClient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.GetMethod;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.DataTransferDescriptor;
import org.globus.gsi.GSIConstants;
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.globus.net.GSIHttpURLConnection;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.ietf.jgss.GSSCredential;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.AttributeTagAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.query.RetrieveResponseGenerator;

/**
 * @author tpan Cache is not really enabled, and everything is going into the
 *         same directory. This is NOT scalable. because of this problem, we
 *         can't retrieve multiple datasets for the same query. because of this,
 *         we also cannot thread the query (retrieval probably can remain single
 *         threaded, since in theory the pipe is saturated.
 */
public class DICOMcaGridRRG implements RetrieveResponseGenerator {

	private String[] dataServiceUrls;
	private HashMap<String, String> dicomQueryHashMap = null;
	private File retrieveDir = null;
	private File tempDir = new File(System.getProperty("java.io.tmpdir"));
	private ModelMap mmap;

	public DICOMcaGridRRG(String[] dicomDataServiceUrls, ModelMap map) {
		System.err.println("DICOMcaGridRRG");
		this.dataServiceUrls = dicomDataServiceUrls;
		System.out.println("URL = " + this.dataServiceUrls);

		if (this.dicomQueryHashMap == null)
			this.dicomQueryHashMap = new HashMap<String, String>();

		// Create a temp directory for retrieval and unzipping of files. this is
		// active for just this session
		try {
			this.retrieveDir = this.createTempDir("VirtualPACS", "Retrieve",
					this.tempDir);
		} catch (IOException e) {
			System.err
			.println("ERROR:  unable to create input or output directories ");
			e.printStackTrace();
		}
		this.mmap = map;
	}

	private File createTempDir(String prefix, String suffix, File tempDir)
	throws IOException {
		File dir;
		dir = File.createTempFile(prefix, suffix, tempDir);
		String path = dir.getAbsolutePath();
		dir.delete();
		dir = new File(path);
		dir.mkdirs();
		return dir;
	}

	public static boolean deleteDir(File dir) {
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i = 0; i < children.length; i++) {
				boolean success = deleteDir(new File(dir, children[i]));
				if (!success) {
					return false;
				}
			}
		}
		// The directory is now empty so delete it
		return dir.delete();
	}
	
	
	public void close() {
		// TODO Auto-generated method stub
		
	}

	public SetOfDicomFiles getDicomFiles() {
		System.out.println("Called getDICOMFiles");

		SetOfDicomFiles dicomFiles = new SetOfDicomFiles();
		File[] dicomDirList = this.retrieveDir.listFiles(new FileFilter() {
			public boolean accept(File pathname) {
				return pathname.isDirectory();
			}
		});

		File[] dicomFileList = null;
		for (int i = 0; i < dicomDirList.length; i++) {
			System.out.println("dicom dir = " + dicomDirList[i].toString());
			dicomFileList = dicomDirList[i].listFiles(new FileFilter() {
				public boolean accept(File pathname) {
					String name = pathname.getName();
					if (name.equals("")) {
						return false;
					}
					return pathname.isFile()
					&& pathname.getName().endsWith(".dcm");
				}
			});

			for (int j = 0; j < dicomFileList.length; j++) {
				dicomFiles.add(dicomFileList[j].toString());
				System.out.println(dicomFileList[j].toString());
			}
		}

		return dicomFiles;
	}

	public String getErrorComment() {
		// TODO Auto-generated method stub
		return null;
	}

	public AttributeTagAttribute getOffendingElement() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getStatus() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void performRetrieve(String retrieveSOPClassUID,
			AttributeList requestIdentifier, boolean relational) {
		
		// Convert DICOM to CQL
		final CQLQuery dicomAsCqlRetrieve = DICOM2CQL.convert2CQL(mmap, requestIdentifier);

		// Check the CQL conversion
		try {
			String TEMP = ObjectSerializer.toString(dicomAsCqlRetrieve,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery",
					"CQLQuery"));
			System.out.println(TEMP);
		} catch (SerializationException e1) {
			e1.printStackTrace();
		}

		if(true)
			retrieveDICOMObjects(retrieveSOPClassUID, requestIdentifier, relational, dicomAsCqlRetrieve);
		//else 
		//	retrieveDICOMObjectsProgressively(retrieveSOPClassUID, requestIdentifier, relational, dicomAsCqlRetrieve);
	}
	
	private void retrieveDICOMObjects(String retrieveSOPClassUID,
			AttributeList requestIdentifier, boolean relational, CQLQuery cqlq) {
		System.out
		.println("Executing: DICOMcaGridRRG.performRetrieve()");
		
		String retDir = null;
		try {
			retDir = this.retrieveDir.getCanonicalPath();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		for (int i = 0; i < this.dataServiceUrls.length; i++) {
			java.util.Random r = new java.util.Random();
			r.setSeed(System.currentTimeMillis());
			int val = r.nextInt();
			String urlSpecificRetrieveDir = retDir + File.separator + "DownloadURL" + i;
			if (new File(urlSpecificRetrieveDir).mkdirs() != true) {
				System.err.println("Could not make a temp directory to download files: Check r/w permissions");
				break;
			}
			try {
				String url = this.dataServiceUrls[i];
				if (url.endsWith("DICOMDataService")) {
					DICOMDataServiceClient dicomClient = new DICOMDataServiceClient(url);
					retrieveViaTransferService(dicomClient.retrieveDICOMData(cqlq), urlSpecificRetrieveDir);
				} else if (url.endsWith("NCIACoreService")) {
					NCIACoreServiceClient dicomClient = new NCIACoreServiceClient(url);

					if(cqlq.getTarget().getName().equals("gov.nih.nci.ncia.domain.Study") &&
					   cqlq.getTarget().getAttribute().getName().equals("studyInstanceUID")) {
						   System.out.println("retrieving study: "+cqlq.getTarget().getAttribute().getValue());
						   retrieveViaTransferService(dicomClient.retrieveDicomDataByStudyUID(cqlq.getTarget().getAttribute().getValue()),
						                              urlSpecificRetrieveDir);
					} else if(cqlq.getTarget().getName().equals("gov.nih.nci.ncia.domain.Series") &&
								   cqlq.getTarget().getAttribute().getName().equals("seriesInstanceUID")) {
									   System.out.println("retrieving series: "+cqlq.getTarget().getAttribute().getValue());
									   retrieveViaTransferService(dicomClient.retrieveDicomDataBySeriesUID(cqlq.getTarget().getAttribute().getValue()),
									                              urlSpecificRetrieveDir);
					} else if(cqlq.getTarget().getName().equals("gov.nih.nci.ncia.domain.Patient") &&
							   cqlq.getTarget().getAttribute().getName().equals("patientID")) {
								   System.out.println("retrieving patient: "+cqlq.getTarget().getAttribute().getValue());
								   retrieveViaTransferService(dicomClient.retrieveDicomDataByPatientId(cqlq.getTarget().getAttribute().getValue()),
								                              urlSpecificRetrieveDir);
					} else {
					    retrieveViaTransferService(dicomClient.retrieveDicomData(cqlq), urlSpecificRetrieveDir);
				    }
				} else {
					System.err.println("Currently only supports DICOMDataService and NCIACoreService 4.2");					
				}
			} catch (MalformedURIException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void retrieveViaTransferService(TransferServiceContextReference tscr, String urlSpecificRetrieveDir) {

		TransferServiceContextClient tclient = null;
		InputStream istream = null;
		String localDownloadLocation = urlSpecificRetrieveDir;
		
		// figure out if this is secure
		boolean secure = false;
		if (tscr.getEndpointReference().getAddress().getScheme().equals("https")) {
			secure = true;
			System.out.println("SECURE connection: " + tscr.getEndpointReference().getAddress());
		}
		
		DataTransferDescriptor desc = null;
		try {
			if (secure) {
				tclient = new TransferServiceContextClient(tscr
						.getEndpointReference(), GlobusCredential.getDefaultCredential() );
			} else {
				tclient = new TransferServiceContextClient(tscr
						.getEndpointReference());
			}
			desc = tclient.getDataTransferDescriptor();

		} catch (RemoteException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GlobusCredentialException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		/* Ashish reported that there is some connection issue when using 
		 * the transfer client helper - URLConnection would throw a null
		 * pointer.  Direct connection using HTTPClient works okay.
		 * 
		 * However, Tony has not been able to find example of how to use HTTPClient
		 * with GSI credential.  We will revert to using the TransferClientHelper's
		 * URLConnection code.
		 */
		istream = null;
		try {
			if (secure) {
				istream = TransferClientHelper.getData(desc, GlobusCredential.getDefaultCredential());
			} else {
				URL url = null;
				try {
					url = new URL(desc.getUrl());
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				int statusCode = -1;
				HttpClient client = new HttpClient();
				System.out.println(url.toExternalForm());
				System.out.println(url.toString());
				GetMethod method = new GetMethod(url.toExternalForm());
				method.setFollowRedirects( false );
				try {
					statusCode = client.executeMethod( method );
				} catch (HttpException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				if(statusCode != -1)
				{
					try {
						istream = method.getResponseBodyAsStream();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			}
		} catch (Exception e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}
	
		
		
		if (istream != null) {
			/*
			 * Do something with your input stream here
			 */
			ZipInputStream zis = new ZipInputStream(istream);
			ZipEntryInputStream zeis = null;
			BufferedInputStream bis = null;
			while (true) {
				try {
					zeis = new ZipEntryInputStream(zis);
				} catch (EOFException e) {
					break;
				} catch (IOException e) {
					System.err.println("IOException thrown when recieving the zip stream");
					e.printStackTrace();
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
					System.err
					.println("IOException thrown when reading the zip stream");
				}
			}


			try {
				zis.close();
			} catch (IOException e) {
				System.err
				.println("IOException thrown when closing the zip stream");
			}

//			method.releaseConnection();

		}
		try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			System.err
			.println("Remote exception thrown when closing the transer context");
		}

		File localLocation = new File(localDownloadLocation);
		System.out.println(localLocation.listFiles().length);
	}

/*	public static void main(String arg[]) {
		final String VIRTUALPACS_PROPERTIES = "test/resources/virtualPacsTest.properties";
		final String DICOM_QUERY_CORRECT_FILENAME = "test/resources/1.3.6.1.4.1.9328.50.1.2894.dcm";
		final String DICOM_QUERY_WRONG_FILENAME = "test/resources/queryVirtualPacsSampleWrong.dcm";
		Properties serviceURLs = null;

		ModelMap ncia_map = null;
		try {
			ncia_map  = new ModelMap();
		} catch (FileNotFoundException e1) {
			System.err.println("Modelmap file not found");
		} catch (ModelMapException e1) {
			System.err.println("Modelmap Exception");
		} catch (IOException e1) {
			System.err.println("Modelmap IO Exception");
		} catch (ClassNotFoundException e1) {
			System.err.println("Modelmap ClassNotFound Exception");
		}

		FileInputStream in;
		try {
			in = new FileInputStream(VIRTUALPACS_PROPERTIES);
			serviceURLs = new Properties();
			serviceURLs.load(in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		AttributeList dicomQuery = makeDicomAttributeList("Series", DICOM_QUERY_CORRECT_FILENAME, ncia_map);

		String [] urls = new String[] {arg[1]};
		DICOMcaGridRRGFactory dicomRRGFactory = new DICOMcaGridRRGFactory(urls, ncia_map);
		RetrieveResponseGenerator retrieveResponseGenerator = dicomRRGFactory.newInstance();
		retrieveResponseGenerator.performRetrieve(null, dicomQuery, false);
		SetOfDicomFiles retrievedDicomFiles = retrieveResponseGenerator.getDicomFiles();

	}
	*/
	private static AttributeList makeDicomAttributeList(String level, String dicomFile, ModelMap ncia_map)
	{
		DicomInputStream i = null;
		AttributeList inputList = null;
		AttributeList dicomQuery = null;
		try {
			i = new DicomInputStream(new BufferedInputStream(new FileInputStream(dicomFile)));
		} catch (FileNotFoundException e) {
			System.err.println("DICOM Query file not found");
		} catch (IOException e) {
			System.err.println("DICOM Query file could not be parsed");
		}

		try {
			inputList = new AttributeList();
			inputList.read(i);
			dicomQuery = new AttributeList();
			dicomQuery.put(TagFromName.PatientID, inputList.get(TagFromName.PatientID));
		} catch (IOException e) {
			System.err.println("DICOM Query file could not be parsed");
		} catch (DicomException e) {
			System.err.println("DICOM Exception: Query file could not be parsed");
		}

		if(level.equals("Patient"))
		{
			try {
				AttributeTag t = TagFromName.QueryRetrieveLevel;
				Attribute a = AttributeFactory.newAttribute(t, ncia_map.getDicomDict().getValueRepresentationFromTag(t));
				a.addValue(ncia_map.getRetrieveLevel(InformationEntity.PATIENT));
				dicomQuery.put(t,a);
			} catch (DicomException e) {
				System.err.println("DICOM parsing exception");
			} catch (ModelMapException e) {
				System.err.println("ModelMap parsing exception");
			}

		}
		else if(level.equals("Study"))
		{
			try {
				dicomQuery.put(TagFromName.StudyInstanceUID, inputList.get(TagFromName.StudyInstanceUID));
				AttributeTag t = TagFromName.QueryRetrieveLevel;
				Attribute a = AttributeFactory.newAttribute(t, ncia_map.getDicomDict().getValueRepresentationFromTag(t));
				a.addValue(ncia_map.getRetrieveLevel(InformationEntity.STUDY));
				dicomQuery.put(t,a);
			} catch (DicomException e) {
				System.err.println("DICOM parsing exception");
			} catch (ModelMapException e) {
				System.err.println("ModelMap parsing exception");
			}
		}
		else if(level.equals("Series"))
		{
			try {
				dicomQuery.put(TagFromName.StudyInstanceUID, inputList.get(TagFromName.StudyInstanceUID));
				dicomQuery.put(TagFromName.SeriesInstanceUID, inputList.get(TagFromName.SeriesInstanceUID));
				AttributeTag t = TagFromName.QueryRetrieveLevel;
				Attribute a = AttributeFactory.newAttribute(t, ncia_map.getDicomDict().getValueRepresentationFromTag(t));
				a.addValue(ncia_map.getRetrieveLevel(InformationEntity.SERIES));
				dicomQuery.put(t,a);
			} catch (DicomException e) {
				System.err.println("DICOM parsing exception");
			} catch (ModelMapException e) {
				System.err.println("ModelMap parsing exception");
			}
		}
		else if(level.equals("Image"))
		{

		}
		return dicomQuery;
	}

    /**
	 * Copied from TransferClientHelper (caGrid 1.2 or at most 1.3)
     * 
     * Returns a handle to the input stream of the socket which is returning the
     * data referred to by the descriptor. This method can make an https
     * connection if desired using the credentials passed in.  If you wish to use this
     * method to connect to http it will not use the Crediential whether you pass 
     * them in or they are null,
     * 
     * @param desc              data transfer descriptor received from TransferServiceContextClient
     * @param creds             creator of the transfer resource credentials
     * @return
     * @throws Exception
     */
    public static InputStream getData(DataTransferDescriptor desc, GlobusCredential creds) throws Exception {
        URL url = new URL(desc.getUrl());
        if (url.getProtocol().equals("http")) {
            URLConnection conn = url.openConnection();
            conn.connect();
            return conn.getInputStream();
        } else if (url.getProtocol().equals("https")) {
            if(creds!=null){
            GlobusGSSCredentialImpl cred = new GlobusGSSCredentialImpl(creds, GSSCredential.INITIATE_AND_ACCEPT);
            GSIHttpURLConnection connection = new GSIHttpURLConnection(url);
            connection.setGSSMode(GSIConstants.MODE_SSL);
            connection.setCredentials(cred);
            return connection.getInputStream();
            } else {
                throw new Exception(
                "To use the https protocol to retrieve data from the Transfer Service you must have credentials");
            }
        }
        throw new Exception("Protocol " + url.getProtocol() + " not supported.");
    }


}
