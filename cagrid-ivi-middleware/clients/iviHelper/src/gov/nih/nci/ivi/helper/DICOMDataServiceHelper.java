package gov.nih.nci.ivi.helper;

import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.dicom.DICOM2CQL;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.dicomdataservice.client.DICOMDataServiceClient;
import gov.nih.nci.ncia.domain.Series;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipInputStream;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.descriptor.Status;

import submission.SubmissionInformation;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;

public class DICOMDataServiceHelper {
	boolean debug = false;
	ModelMap mmap = null;

	public DICOMDataServiceHelper() {

		try {
			mmap = new ModelMap();
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public CQLQuery makeCQLQueryFromDICOMAttributes(AttributeList filter) {
		return DICOM2CQL.convert2CQL(mmap, filter);
	}
	public Iterator queryDICOMData(AttributeList filter, String dataSourceURL) {
		final CQLQuery cqlq = makeCQLQueryFromDICOMAttributes(filter);
		return queryDICOMData(cqlq, dataSourceURL);
	}
	public Iterator queryDICOMData(HashMap<String, String> queryHashMap,
			String dataSourceURL) {
		CQLQuery newQuery = null;
		HashmapToCQLQuery makeCQL = new HashmapToCQLQuery(mmap);
		try {
			newQuery = makeCQL.makeCQLQuery(queryHashMap);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		return queryDICOMData(newQuery, dataSourceURL);
	}
	public Iterator queryDICOMData(CQLQuery cqlQuery, String dataSourceURL) {
		DICOMDataServiceClient dicomclient = null;
		CQLQueryResults results = null;

		try {
				dicomclient = new DICOMDataServiceClient(dataSourceURL);
				results = dicomclient.query(cqlQuery);
				
			} catch (MalformedURIException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
	
			}
			
			if (results != null) {
				return new CQLQueryResultsIterator(results, true);

			} else {
				return null;
			}
	}
	
	public void retrieveDICOMData(AttributeList filter, String dataSourceURL,
			String dataDestination) {
		final CQLQuery cqlq = makeCQLQueryFromDICOMAttributes(filter);
		retrieveDICOMData(cqlq, dataSourceURL, dataDestination);
	}

	public void retrieveDICOMData(HashMap<String, String> queryHashMap,
			String dataSourceURL, String dataDestination) {
		CQLQuery newQuery = null;
		try {
			HashmapToCQLQuery makeCQL = new HashmapToCQLQuery(new ModelMap());
			newQuery = makeCQL.makeCQLQuery(queryHashMap);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		retrieveDICOMData(newQuery, dataSourceURL, dataDestination);
	}

	public void retrieveDICOMDataProgressively(CQLQuery cqlQuery,
			String dataSourceURL, String dataDestination, String userID,
			boolean encrypt, boolean signtur) {
		
		retrieveDICOMData(cqlQuery, dataSourceURL, dataDestination);

	}


	public void retrieveDICOMData(CQLQuery cqlQuery, String dataSourceURL,
			String dataDestination) {
		DICOMDataServiceClient dicomclient = null;
			try {
				dicomclient = new DICOMDataServiceClient(dataSourceURL);
			} catch (MalformedURIException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (RemoteException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			InputStream istream = null;
			TransferServiceContextClient tclient = null;
			try {
				tclient = new TransferServiceContextClient(dicomclient.retrieveDICOMData(cqlQuery).getEndpointReference());
				istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
			} catch (MalformedURIException e) {
				System.err.println("Malformed URI Exception Thrown");
				e.printStackTrace();
			} catch (RemoteException e) {
				System.err.println("Remote Exception Thrown");
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			/*
	            try {
	   			 ZipInputStream in2 = new ZipInputStream(new BufferedInputStream(istream));
				 ZipEntry ze;
					while ((ze = in2.getNextEntry()) != null) {
					      System.out.println("Extracting file "+ze);
					      int x;
					      while ((x = in2.read()) != -1)
					            System.out.write(x);
					      System.out.println();
					}
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			try {
				JarInputStream jis = new JarInputStream(istream);
				Manifest manifest = jis.getManifest();
				
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	*/		
			/////////////
			ZipInputStream zis = new ZipInputStream(istream);
			ZipEntryInputStream zeis = null;
			BufferedInputStream bis = null;
			while(true) {
				try {
					zeis = new ZipEntryInputStream(zis);
				} catch (EOFException e) {
					break;
				} catch (IOException e) {
					e.printStackTrace();
					System.err.println("IOException thrown when recieving the zip stream");
				}

				//System.out.println(zeis.getName());

				File localLocation = new File(dataDestination);
				if(!localLocation.exists())
					localLocation.mkdirs();

				String unzzipedFile = dataDestination + File.separator + zeis.getName();
				bis = new BufferedInputStream(zeis);
				// do something with the content of the inputStream

				byte[] data = new byte[524288];
				int bytesRead = 0;
				try {
					BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile), 524288);
					while ((bytesRead = (bis.read(data, 0, data.length))) > 0)  {
						bos.write(data, 0, bytesRead);
						//System.out.println(new String(data));
						//System.out.println("caGrid transferred at " + new Date().getTime());
					}
					bos.flush();
					bos.close();
				} catch (IOException e) {
					System.err.println("IOException thrown when reading the zip stream");
					e.printStackTrace();
				}
			}

			try {
				zis.close();
			} catch (IOException e) {
				System.err.println("IOException thrown when closing the zip stream");
				e.printStackTrace();
			}

			try {
				tclient.destroy();
			} catch (RemoteException e) {
				System.err.println("Remote exception thrown when closing the transer context");
				e.printStackTrace();
			}
			
			File localLocation = new File(dataDestination);
			//System.out.println(localLocation.listFiles().length);
			//assertEquals(localLocation.listFiles().length, 79);
	}

	public void submitDICOMData(File dataSource, String[] dataDestinationURL) {
		for (int i = 0; i < dataDestinationURL.length; i++) {
				DICOMDataServiceClient dicomClientSubmit = null;
				try {
					dicomClientSubmit = new DICOMDataServiceClient(
							dataDestinationURL[i]);
				} catch (MalformedURIException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (RemoteException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				SubmissionInformation subInfo = new SubmissionInformation();
				//subInfo.setFileName("Test.zip");
				subInfo.setFileType("zip");

				
				TransferServiceContextClient tclient = null;
				try {
					tclient = new TransferServiceContextClient(dicomClientSubmit.submitDICOMData(subInfo).getEndpointReference());
				} catch (MalformedURIException e) {
					e.printStackTrace();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
				
				try {
					//File transferDoc = new File(retrievedFile);
						System.out.println("transferDoc is " + dataSource);
						BufferedInputStream dicomIn = new BufferedInputStream(new FileInputStream(dataSource));
						TransferClientHelper.putData(dicomIn, dataSource.length(), tclient.getDataTransferDescriptor());
						dicomIn.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				try {
					tclient.setStatus(Status.Staged);
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
	}

	/*
	 * private Thread startFetchThread(URI fileURI, String localDataDestination,
	 * String user, boolean encryption, boolean signature) { Thread t = new
	 * Thread(new FetchThread(fileURI, localDataDestination, user, encryption,
	 * signature)); t.start(); return t; }
	 * 
	 * public class FetchThread implements Runnable {
	 * 
	 * URI fileURI; String user; boolean encryption; boolean signature; String
	 * localDataDestination;
	 * 
	 * FetchThread(URI fileURI, String localDestination, String user, boolean
	 * encryption, boolean signature) { this.fileURI = fileURI; this.user =
	 * user; this.encryption = encryption; this.signature = signature;
	 * this.localDataDestination = localDestination; }
	 * 
	 * public void run() { System.out.println("Downloading file " +
	 * fileURI.toString()); CommonUtilities.gridFTPSecureDataRetrieval(fileURI,
	 * localDataDestination, user, encryption, signature); }
	 *  }
	 */
	public static void main(String[] args) {

		DICOMDataServiceHelper helper = new DICOMDataServiceHelper();
		HashMap<String, String> query = new HashMap<String, String>();
		query.put(HashmapToCQLQuery.TARGET_NAME_KEY, Series.class
				.getCanonicalName() );
		query.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID",
		"1.3.6.1.4.1.9328.50.1.1632");
		query.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID",
		"1.3.6.1.4.1.9328.50.1.1972");

		helper
		.retrieveDICOMData(
				query,
				"http://140.254.80.209:8080/wsrf/services/cagrid/DICOMDataService",
		"/tmp/DICOMHelperDownload");
	}

}
