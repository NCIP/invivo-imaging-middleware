package gov.nih.nci.ivi.pathologydataservice.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.pathology.PathologyDataServiceHelper;
import gov.nih.nci.ivi.utils.Zipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

class PathologyRetrieve {

	private CQLQuery query;
	private Vector<String> retrievedZipFileNames = null;
	private Vector<String> allFileNames;
	private String rootDir;
    private Thread t;

	public PathologyRetrieve(String rootDirFromServiceProperty, boolean dataLevelAuthorizationOn, String user, int debugLevel){
		this.rootDir = rootDirFromServiceProperty;
		
	}

	public Vector<String> performRetrieveThumbnail(CQLQuery cqlQuery) {
		System.out.println("rootDir is " + rootDir);
		System.out.println("Started thumbnail retrieval at: " + System.currentTimeMillis());
		this.query = cqlQuery;

		// get FSHelper
		PathologyDataServiceHelper helper = null;
		System.out.println("before helper");
		try {
			helper = new PathologyDataServiceHelper(rootDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("after helper");
		// convert cql to file system path
		String path = rootDir;

		System.out.println("before query test line");
		if (!query.getTarget().getAttribute().getName().equals("fileName")) {
			System.out.println("Attribute name must be fileName in the query.");
    		retrievedZipFileNames = null;
		}
		System.out.println("after query test line");

		try {
			System.out.println("Executing query: ");
			System.out.println(ObjectSerializer.toString(query, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String value = query.getTarget().getAttribute().getValue();

		path += File.separator + "thumbnails" + File.separator + value;

		if(retrievedZipFileNames == null)
			retrievedZipFileNames = new Vector<String>();
		retrievedZipFileNames.add(path);

		return retrievedZipFileNames;
	}

	
	public Vector<String> performRetrieve(CQLQuery cqlQuery) {
		this.query = cqlQuery;
		System.out.println("rootDir is " + rootDir);
//		t = new Thread(new FileRetrieve());
		System.out.println("Started retrieval at: " + System.currentTimeMillis());

		// get FSHelper
		PathologyDataServiceHelper helper = null;
		try {
			helper = new PathologyDataServiceHelper(rootDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		// convert cql to file system path
		String path = rootDir;
		if (!query.getTarget().getAttribute().getName().equals("fileName")) {
			System.out.println("Attribute name must be fileName in the query.");
    		retrievedZipFileNames = null;
		}
		try {
			System.out.println("Executing query: ");
			System.out.println(ObjectSerializer.toString(query, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
		} catch (SerializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		String value = query.getTarget().getAttribute().getValue();
		if (value.endsWith(".svs") || value.endsWith(".SVS"))
			path += File.separator + "images" + File.separator + value;
		else if (value.endsWith(".jpg") || value.endsWith(".JPG"))
			path += File.separator + "cache" + File.separator + value;
		else
			path += File.separator + "codes" + File.separator + value;
		/*			String[] filelist = {path};

	// create the output file name
		File inputDir = null;
		try {
			inputDir = File.createTempFile("IDS-server-retrieve", null, new File(System.getProperty("java.io.tmpdir")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		path = inputDir.getAbsolutePath();
		inputDir.delete();
		inputDir = new File(path);
		inputDir.mkdirs();
		String zipFileName = inputDir.getAbsolutePath() + File.separator + "completed.zip";

		// if no files to be zipped
		if (filelist.length == 0) {
			try {
				new File(zipFileName).createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			// zip the files
    		try {
				Zipper.zip(zipFileName, filelist, false);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
*/		if(retrievedZipFileNames == null)
			retrievedZipFileNames = new Vector<String>();
		retrievedZipFileNames.add(path);

		
		//		t.start();

		
		
		return retrievedZipFileNames;
	}
	
	
/*	
    class FileRetrieve implements Runnable {

    	public FileRetrieve() {
    	}
    	
    	public void run() {
    		// get FSHelper
    		PathologyDataServiceHelper helper = null;
    		try {
				helper = new PathologyDataServiceHelper(rootDir);
			} catch (IOException e) {
				e.printStackTrace();
			}
			// convert cql to file system path
			String path = rootDir;
			if (!query.getTarget().getAttribute().getName().equals("fileName")) {
				System.out.println("Attribute name must be fileName in the query.");
	    		retrievedZipFileNames = null;
			}
			try {
				System.out.println("Executing query: ");
				System.out.println(ObjectSerializer.toString(query, 
						new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery")));
			} catch (SerializationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			
			String value = query.getTarget().getAttribute().getValue();
			if (value.endsWith(".svs") || value.endsWith(".SVS"))
				path += File.separator + "images" + File.separator + value;
			else if (value.endsWith(".jpg") || value.endsWith(".JPG"))
				path += File.separator + "cache" + File.separator + value;
			else
				path += File.separator + "codes" + File.separator + value;
			String[] filelist = {path};

			// create the output file name
			File inputDir = null;
			try {
				inputDir = File.createTempFile("IDS-server-retrieve", null, new File(System.getProperty("java.io.tmpdir")));
			} catch (IOException e) {
				e.printStackTrace();
			}
			path = inputDir.getAbsolutePath();
			inputDir.delete();
			inputDir = new File(path);
			inputDir.mkdirs();
			String zipFileName = inputDir.getAbsolutePath() + File.separator + "completed.zip";

			// if no files to be zipped
			if (filelist.length == 0) {
				try {
					new File(zipFileName).createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			else {
				// zip the files
	    		try {
					Zipper.zip(zipFileName, filelist, false);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
    		if(retrievedZipFileNames == null)
    			retrievedZipFileNames = new Vector<String>();
    		retrievedZipFileNames.add(zipFileName);
    	}
    }

*/

}
