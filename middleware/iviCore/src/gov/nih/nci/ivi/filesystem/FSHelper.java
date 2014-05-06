/**
 * 
 */
package gov.nih.nci.ivi.filesystem;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Properties;

/**
 * @author tpan
 *
 */
public class FSHelper {

	public static final String ROOT_DIR = "rootDir";
	File rootDir;

	public static Properties getParameters() {
		Properties params =  new Properties();
		params.put(FSHelper.ROOT_DIR, "CHANGE_ME:   tmp" );

		return params;
	}

	public FSHelper(Properties configuration) throws IOException {
		if (configuration == null)
			throw new IOException("FSHelper: configuration is null");
		this.initialize((String)configuration.get(FSHelper.ROOT_DIR));
	}

	public FSHelper(String dirName) throws IOException {
		this.initialize(dirName);
	}

	private void initialize(String dirName) throws IOException {
		// just validate it
		if (dirName == null || dirName == "")
			throw new IOException("FSHelper: null or empty dirName");
		rootDir = new File(dirName);
		if (!rootDir.exists())
			rootDir.mkdirs();		
		if (!rootDir.isDirectory())
			throw new IOException("FSHelper: rootDir " + rootDir.getAbsolutePath() + " is not a directory");
		if (!rootDir.exists() || !rootDir.canRead() || !rootDir.canWrite())
			throw new IOException("FSHelper: rootDir " + rootDir.getAbsolutePath() + " is not ready");
	}

	public String[] queryFSByWildCard(String filePattern) throws IOException {
		if (filePattern == null || filePattern == "")
			throw new IOException("FSHelper: Null or empty file pattern");
		String[] fullpaths = null;
		
		  // Return all  non dot files in current directory
		if(filePattern.equals("*"))
			fullpaths = retrieveAllNonDotFiles(filePattern);
		
		 // No WildCards and is likely an exact filename
		else if(!filePattern.contains("*") && !filePattern.contains("?"))
			fullpaths = retriveExactFileName(filePattern);
		
		// There is a wildcard
		else
			fullpaths = retrieveWildCardFileName(filePattern);
		
		return fullpaths;
	}
	
	/*
	 * Modified from Wildcardsearch.java which was authored by kmportner
	 * http://forum.java.sun.com/thread.jspa?threadID=480592&start=0&tstart=0
	 */
	private String[] retrieveWildCardFileName(String filePattern) throws IOException {
		String[] fullpaths = null;	
		final String modifiedFilePattern = replaceWildcards(filePattern);
		File parentDir = rootDir;
		if (!parentDir.exists() || !parentDir.canRead())
			throw new IOException("FSHelper: Unable to read directory: " + parentDir.getAbsolutePath());

		String[] files = parentDir.list(new FilenameFilter()
		{
			public boolean accept(File dir, String name)
			{
				return (name.toLowerCase().matches(modifiedFilePattern));
			}
		});
		fullpaths = new String[files.length];
		for (int i = 0; i < fullpaths.length; i++) {
			fullpaths[i] = parentDir.getAbsolutePath() + File.separator + files[i];
			System.out.println("returned by FSHelper: " + fullpaths[i]);
		}
		return fullpaths;		
	}

	/*
	 * Modified from Wildcardsearch.java which was authored by kmportner
	 * http://forum.java.sun.com/thread.jspa?threadID=480592&start=0&tstart=0
	 */
	private String replaceWildcards(String wild)
	{
		StringBuffer buffer = new StringBuffer();
		
		char [] chars = wild.toCharArray();
		
		for (int i = 0; i < chars.length; ++i)
		{
			if (chars[i] == '*')
				buffer.append(".*");
			else if (chars[i] == '?')
				buffer.append(".{1}");
			else
				buffer.append(chars[i]);
		}	
		return buffer.toString();
	}	// end replaceWildcards method


	private String[] retriveExactFileName(String filePattern) throws IOException {
		String[] fullpaths = null;	
		String path = rootDir.getCanonicalPath() + File.separator + filePattern;
		final File queriedFile = new File(path).getCanonicalFile();
		if (!queriedFile.getCanonicalPath().startsWith(rootDir.getCanonicalPath()))
			throw new IOException("FSHelper: User trying to access a file outside the root dir");

		File parentDir = rootDir;
		if (!parentDir.exists() || !parentDir.canRead())
			throw new IOException("FSHelper: Unable to read directory: " + parentDir.getAbsolutePath());
		
		File[] allFiles = parentDir.listFiles();
		
		String[] files = parentDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// Is not a dot file nor a directory
				if (queriedFile.getName().equals(name))
					return true;
				else 
					return false;
			}
		});
		fullpaths = new String[files.length];
		for (int i = 0; i < fullpaths.length; i++) {
			fullpaths[i] = parentDir.getAbsolutePath() + File.separator + files[i];
			//System.out.println("returned by FSHelper: " + fullpaths[i]);
		}
		return fullpaths;		
	}

	private String[] retrieveAllNonDotFiles(String filePattern) throws IOException {
		String[] fullpaths = null;
		// Return all  non dot files in current directory
		File parentDir = rootDir;
		if (!parentDir.exists() || !parentDir.canRead())
			throw new IOException("FSHelper: Unable to read directory: " + parentDir.getAbsolutePath());
		
		File[] allFiles = parentDir.listFiles();
		
		String[] files = parentDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				// Is not a dot file nor a directory
				if (!name.startsWith(".") && !(new File(dir.getAbsolutePath()+File.separator+name)).isDirectory())
					return true;
				else 
					return false;
			}
		});
		fullpaths = new String[files.length];
		for (int i = 0; i < fullpaths.length; i++) {
			fullpaths[i] = parentDir.getAbsolutePath() + File.separator + files[i];
			//System.out.println("returned by FSHelper: " + fullpaths[i]);
		}
		return fullpaths;
	}

	/**
	* @deprecated Use {@link #queryFSByWildCard(String)}
	* which now supports Exact, All, and Wildcarding
	**/
	// for now, limited to all ("*"), or exact matches.
	public String[] queryFSByPath(String filePattern) throws IOException {
		if (filePattern == null || filePattern == "")
			throw new IOException("FSHelper: Null or empty file pattern");
		final boolean selectAll;
		File parentDir = null;
		final File queriedFile;
		if (filePattern.contains("*")) {
			selectAll = true;
			parentDir = rootDir;
			queriedFile = null;
		}
		else {
			String path = rootDir.getCanonicalPath() + File.separator + filePattern;
			queriedFile = new File(path).getCanonicalFile();
			if (!queriedFile.getCanonicalPath().startsWith(rootDir.getCanonicalPath()))
				throw new IOException("FSHelper: User trying to access a file outside the root dir");
			// if the canonical query path is equal to the root directory
			// consider it as a "select all" query
			if (queriedFile.getCanonicalPath().equals(rootDir.getCanonicalPath())) {
				selectAll = true;
				parentDir = rootDir;
			}
			else {
				selectAll = false;
				parentDir = queriedFile.getParentFile();
			}
		}
		if (!parentDir.exists() || !parentDir.canRead())
			throw new IOException("FSHelper: Unable to read directory: " + parentDir.getAbsolutePath());
		String[] files = parentDir.list(new FilenameFilter() {
			public boolean accept(File dir, String name) {
				if (selectAll)
					return true;
				else if (queriedFile.getName().equals(name))
					return true;
				return false;
			}
		});
		String[] fullpaths = new String[files.length];
		for (int i = 0; i < fullpaths.length; i++) {
//			if (new File(parentDir.getAbsolutePath() + File.separator + files[i]).isDirectory())
//				continue;
			fullpaths[i] = parentDir.getAbsolutePath() + File.separator + files[i];
			System.out.println("returned by FSHelper: " + fullpaths[i]);
		}
		
		return fullpaths;
	}
	
	public File getRootDir() {
		return rootDir;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties params = FSHelper.getParameters();
		params.put(FSHelper.ROOT_DIR, "/tmp/GIdata");
		FSHelper helper = null;
		try {
			helper = new FSHelper(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
		String path = "*";
		try {
			System.out.println(helper.queryFSByPath(path).length + " results" );
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
