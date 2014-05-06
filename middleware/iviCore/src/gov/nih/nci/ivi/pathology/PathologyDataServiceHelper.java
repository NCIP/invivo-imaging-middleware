package gov.nih.nci.ivi.pathology;

import gov.nih.nci.ivi.fileinfo.FileInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;
import java.util.Vector;

/**
 * @author tpan
 *
 */
public class PathologyDataServiceHelper {

	static final String ROOT_DIR = "rootDir";
	File rootDir;

	public static Properties getParameters() {
		Properties params =  new Properties();
		params.put(PathologyDataServiceHelper.ROOT_DIR, "CHANGE_ME:   tmp" );

		return params;
	}

	public PathologyDataServiceHelper(Properties configuration) throws IOException {
		if (configuration == null)
			throw new IOException("PathologyDataServiceHelper: configuration is null");
		this.initialize((String)configuration.get(PathologyDataServiceHelper.ROOT_DIR));
	}

	public PathologyDataServiceHelper(String dirName) throws IOException {
		this.initialize(dirName);
	}

	private void initialize(String dirName) throws IOException {
		// just validate it
		if (dirName == null || dirName == "")
			throw new IOException("PathologyDataServiceHelper: null or empty dirName");
		rootDir = new File(dirName);
		if (!rootDir.exists())
			rootDir.mkdirs();
		if (!rootDir.isDirectory())
			throw new IOException("PathologyDataServiceHelper: rootDir " + rootDir.getAbsolutePath() + " is not a directory");
		if (!rootDir.exists() || !rootDir.canRead() || !rootDir.canWrite())
			throw new IOException("PathologyDataServiceHelper: rootDir " + rootDir.getAbsolutePath() + " is not ready");
	}

	public File getRootDir() {
		return rootDir;
	}

	public static File[] listFilesAsArray(File directory, FilenameFilter filter, boolean recurse) {
		Collection<File> files = listFiles(directory, filter, recurse);
		File[] arr = new File[files.size()];
		return files.toArray(arr);
	}

	public static Collection<File> listFiles(File directory, FilenameFilter filter, boolean recurse) {
		Vector<File> files = new Vector<File>();
		File[] entries = directory.listFiles();
		for (File entry : entries) {
			if (filter == null || filter.accept(directory, entry.getName()))
				files.add(entry);
			if (recurse && entry.isDirectory())
				files.addAll(listFiles(entry, filter, recurse));
		}

		return files;		
	}
	
	public FileInfo[] queryFiles(String fileType) throws IOException {
		if (!rootDir.exists() || !rootDir.canRead())
			throw new IOException("PathologyDataServiceHelper: Unable to read directory: " + rootDir.getAbsolutePath());
		File fileDir = new File(rootDir.getAbsolutePath() + File.separator + fileType);
		if (!fileDir.exists()) {
			System.out.println("File dir " + fileDir.getAbsolutePath() + " does not exist");
			return null;
		}

		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String name) {
				return true;
			}
		};
		File []files = null;
		files = listFilesAsArray(fileDir, filter, true);

		FileInfo fileInfo[] = null;
		if (files != null)
			fileInfo = new FileInfo[files.length];
		for (int i = 0; i < files.length; i++) {
			fileInfo[i] = new FileInfo();
			String fileName = files[i].getAbsolutePath();
			fileInfo[i].setName(fileName.substring(fileDir.getAbsolutePath().length()+1, fileName.length()));
			if (files[i].isDirectory())
				fileInfo[i].setType("directory");
			else if (files[i].isFile())
				fileInfo[i].setType("file");
			else if (files[i].isHidden())
				fileInfo[i].setType("hidden");
			else
				fileInfo[i].setType("unknown");
		}

		return fileInfo;
	}

	public static void main(String[] args) {
		Properties params = PathologyDataServiceHelper.getParameters();
		params.put(PathologyDataServiceHelper.ROOT_DIR, "/tmp/pathology/");
		PathologyDataServiceHelper helper = null;
		try {
			helper = new PathologyDataServiceHelper(params);
		} catch (IOException e) {
			e.printStackTrace();
		}
		FileInfo []fileInfo = null;
		try {
			fileInfo = helper.queryFiles("images");
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < fileInfo.length; i++)
			System.out.println(fileInfo[i].getName() + " " + fileInfo[i].getType());
	}

}
