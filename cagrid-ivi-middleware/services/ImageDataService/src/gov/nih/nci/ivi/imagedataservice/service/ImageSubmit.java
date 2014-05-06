package gov.nih.nci.ivi.imagedataservice.service;

import gov.nih.nci.ivi.utils.Zipper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import submission.SubmissionInformation;

class ImageSubmit {

	private SubmissionInformation submissionInformation;
	private String rootDir = null;
	private boolean debug = false;

	public ImageSubmit(SubmissionInformation subInfo, boolean debugLevel) {

		debug = debugLevel;
		try {
			rootDir = ImageDataServiceConfiguration.getConfiguration()
					.getCqlQueryProcessorConfig_rootDir();
		} catch (Exception e1) {
			System.err
					.println("Error in getting query processor configuration");
			if (debug)
				e1.printStackTrace();
		}
		subInfo = submissionInformation;
	}

	public void processImageUpload(String localLocation) {
		if (debug)
			System.out.println("Data has been uploaded to" + localLocation);

		java.util.Random r = new java.util.Random();
		r.setSeed(System.currentTimeMillis());
		int val = r.nextInt();
		String unzipUploadDirPath = rootDir + File.separator + "Image-Upload"
				+ File.separator + val;
		File unzipUploadDir = new File(unzipUploadDirPath);
		if (!unzipUploadDir.exists())
			unzipUploadDir.mkdirs();
		System.out.println("Upload Dir = " + unzipUploadDirPath);

		String[] outs = null;
		try {
			outs = Zipper.unzip(localLocation, unzipUploadDirPath);
			File uploadedFile = new File(localLocation);
			uploadedFile.delete();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		for (int index = 0; index < outs.length; index++)
			System.out.println(outs[index]);
		try {
			Thread.sleep(7000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		uploadToImageDataServiceDirectory(outs);

		for (int index = 0; index < outs.length; index++) {
			File tmpDICOMFile = new File(outs[index]);
			tmpDICOMFile.delete();
		}
		unzipUploadDir.delete();
	}

	private void uploadToImageDataServiceDirectory(String[] outs) {
		try {
			for (int i = 0; i < outs.length; i++) {
				System.out.println(outs[i]);
				File inputFile = new File(outs[i]);
				FileInputStream fis = new FileInputStream(inputFile);
				byte[] content = new byte[fis.available()];
				fis.read(content);
				FileOutputStream fos = new FileOutputStream(rootDir
						+ File.separator + inputFile.getName());
				fos.write(content);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}