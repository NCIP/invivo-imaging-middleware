package gov.nih.nci.ivi.imagedataservice.service;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.filesystem.CQL2FS;
import gov.nih.nci.ivi.filesystem.FSHelper;

import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;
import java.util.Vector;

class ImageRetrieve {

	private boolean debug;
	private Properties queryProcessorProperties;

	public ImageRetrieve(Properties parentQueryProcessorProperties, boolean debugLevel) {
		this.queryProcessorProperties = parentQueryProcessorProperties;
		debug = debugLevel;
	}

	public Vector<String> performRetrieve(CQLQuery query) {
		String rootDir = null;
		try {
			rootDir = this.queryProcessorProperties.getProperty("rootDir");
			//rootDir = ImageDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
		} catch (Exception e1) {
			System.err.println("Error in getting query processor configuration");
			if(debug)
				e1.printStackTrace();
		}
		FSHelper helper = null;
		try {
			helper = new FSHelper(rootDir);
		} catch (IOException e) {
			System.err.println("Error instantiating a FileSystemHelper");
			if(debug)
				e.printStackTrace();
		}

		String path = null;
		try {
			path = CQL2FS.cqlToPath(query);
		} catch (MalformedQueryException e) {
			System.err.println(e.getMessage());
			if(debug)
				e.printStackTrace();
		}

		// query the files
		String[] filelist = null;
		try {
			filelist = helper.queryFSByWildCard(path);
		} catch (IOException e) {
			System.out.println("FSCQLQueryProcessor: " + e.getMessage());
			if(debug)
				e.printStackTrace();
		}
		
		if(debug)
		{
			for (int i = 0; i < filelist.length; i++)
				System.out.println(filelist[i]);
			System.out.println("Number of files Retrieved: " + filelist.length);
		}		

		Vector <String> retrievedFileNames = new Vector<String>(Arrays.asList(filelist));
		return retrievedFileNames;
	}
	
}