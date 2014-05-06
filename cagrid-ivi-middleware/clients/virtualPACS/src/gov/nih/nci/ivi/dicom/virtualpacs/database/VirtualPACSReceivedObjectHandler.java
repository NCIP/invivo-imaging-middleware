package gov.nih.nci.ivi.dicom.virtualpacs.database;

import gov.nih.nci.ivi.dicomdataservice.client.DICOMDataServiceClient;
import gov.nih.nci.ivi.utils.Zipper;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.io.IOException;
import java.rmi.RemoteException;

import javax.swing.Timer;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.descriptor.Status;

import submission.SubmissionInformation;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;

public class VirtualPACSReceivedObjectHandler extends ReceivedObjectHandler
implements ActionListener {

	private String savedImagesFolder;
	private String service;
	private Timer timer = null;
	private static final int SUBMIT_DELAY = 10000;
	private static final boolean INCREMENTAL = false;
	public boolean debug = false;

	public VirtualPACSReceivedObjectHandler(String imagesFolder, String url) {
		savedImagesFolder = imagesFolder;
		File dir = new File(savedImagesFolder);
		dir.deleteOnExit();

		service = url;

		System.out.println("created handler " + this);

		timer = new Timer(SUBMIT_DELAY, this);
		timer.setRepeats(false);
	}

	@Override
	public void sendReceivedObjectIndication(String dicomFileName,
			String transferSyntax, String callingAETitle)
	throws DicomNetworkException, DicomException, IOException {
		if (dicomFileName != null) {
			if (debug == true)
				System.out.println("Received: " + dicomFileName + " from "
						+ callingAETitle + " in " + transferSyntax);
			try {

				if (INCREMENTAL == false) {

					// timer behavior:
					// first received object switches on the timer. each
					// successive input resets the timer.
					// when the transfer is complete, the timer is
					if (timer.isRunning()) {
						timer.restart();
					} else {
						timer.start();
					}
				}
				// submit, but only after some timeout.

			} catch (Exception e) {
				e.printStackTrace(System.err);
			}
		}
	}

	public void actionPerformed(ActionEvent arg0) {
		if (this.service == null) {
			System.out.println("no service supporting submit has been selected. can't submit");
			return;
		}
		
		
		//Step 1: List the files.
		File dir = new File(this.savedImagesFolder);
		File[] files = dir.listFiles(new FilenameFilter() {
			public boolean accept(File arg0, String arg1) {
				if (arg1.contains("mainfest") || arg1.endsWith("zip")) {
					return false;
				}
				return true;
			}
		});
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++) {
			filenames[i] = files[i].getAbsolutePath();
		}

		// Step 2: Zip up the files
		String localFileName = this.savedImagesFolder + File.separator + "submitPacket.zip";
		try {
			Zipper.zip(localFileName, filenames, false);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// Step 3: Send the Zipped up file
		DICOMDataServiceClient dicomClientSubmit = null;
		//	try {
		try {
			dicomClientSubmit = new DICOMDataServiceClient(this.service);
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		SubmissionInformation subInfo = new SubmissionInformation();
		subInfo.setFileName("submitPacket.zip");
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
			File dataSource = new File(localFileName);
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

		//Step 4: Process the upload on server
		try {
			tclient.setStatus(Status.Staged);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		
		//Step 5: local Cleanup
		DICOMcaGridRRG.deleteDir(dir);
		dir.mkdirs();

		timer.stop();
	}

}
