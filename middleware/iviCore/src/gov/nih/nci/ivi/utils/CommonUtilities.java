package gov.nih.nci.ivi.utils;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.axis.types.URI;

public class CommonUtilities {

	public static void gridFTPDataRetrieval(String dataSourceURL, String localDataDestination) {
		File inputDir = null;
		int nfiles = -1;

		inputDir = new File(localDataDestination);
		inputDir.mkdirs();
		String completedFlag =
			(inputDir.getAbsolutePath() + File.separator + "completed.zip");


		GridFTPFetcher gftpfetcher = new GridFTPFetcher();
		try {
			gftpfetcher.fetch(dataSourceURL, completedFlag+"-tmp.zip", true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!new File(completedFlag+"-tmp.zip").exists())
		{
			System.err.println("Could not retrieve file: " + dataSourceURL );
			return;
		}

		String[] outs;
		try {
			outs = Zipper.unzip(completedFlag+"-tmp.zip",
					inputDir.getAbsolutePath());
			nfiles=outs.length-2;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File manifestFile = new File(inputDir.getAbsolutePath() + File.separator + "manifest");
		if(manifestFile.exists())	manifestFile.delete();
		File zipFile = new File(completedFlag+"-tmp.zip");
		if(zipFile.exists())	zipFile.delete();

		System.out.println("Retrieved " + nfiles + "files to" + localDataDestination);
		return;
	}

	public static void gridFTPSecureDataRetrieval(URI dataSourceURL, String localDataDestination, String user, boolean encryption, boolean signature) {
		File inputDir = null;
		int nfiles = -1;

		inputDir = new File(localDataDestination);
		inputDir.mkdirs();
		String completedFlag =
			(inputDir.getAbsolutePath() + File.separator + "completed.zip");


		GridFTPFetcher gftpfetcher = new GridFTPFetcher();
		try {
			gftpfetcher.secureFetch(dataSourceURL, completedFlag+"-tmp.zip", user, encryption, signature);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(!new File(completedFlag+"-tmp.zip").exists())
		{
			System.err.println("Could not retrieve file: " + dataSourceURL );
			return;
		}

		String[] outs;
		try {
			outs = Zipper.unzip(completedFlag+"-tmp.zip",
					inputDir.getAbsolutePath());
			nfiles=outs.length-2;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		File manifestFile = new File(inputDir.getAbsolutePath() + File.separator + "manifest");
		if(manifestFile.exists())	manifestFile.delete();
		File zipFile = new File(completedFlag+"-tmp.zip");
		if(zipFile.exists())	zipFile.delete();
		File zipFile2 = new File(completedFlag);
		if(zipFile2.exists())	zipFile2.delete();

		System.out.println("Retrieved " + nfiles + "files to" + localDataDestination);
		return;
	}


	public static void gridFTPDataSubmission(String dataDestinationURL, String [] localDataFiles, boolean compressionFlag) throws IOException
	{
		// zip up the files
		String localFileName = System.getProperty("java.io.tmpdir") + File.separator + "submitPacket.zip";
		try {
			Zipper.zip(localFileName, localDataFiles, compressionFlag);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		GridFTPFetcher gftpPutter = new GridFTPFetcher();
		gftpPutter.upload(dataDestinationURL, localFileName, compressionFlag);

		File zipFile = new File(localFileName);
		if(zipFile.exists())
			zipFile.delete();

	}

}