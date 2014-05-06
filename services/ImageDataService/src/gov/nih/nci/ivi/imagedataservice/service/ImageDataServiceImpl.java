package gov.nih.nci.ivi.imagedataservice.service;

import edu.osu.bmi.utils.io.zip.ZipEntryOutputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.cagrid.transfer.context.service.globus.resource.TransferServiceContextResource;
import org.cagrid.transfer.context.service.helper.DataStagedCallback;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class ImageDataServiceImpl extends ImageDataServiceImplBase {


	public ImageDataServiceImpl() throws RemoteException {
		super();
	}

	public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieveImageData(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException {

		Properties imageQueryProcProps = null;
		try {
			imageQueryProcProps = gov.nih.nci.cagrid.data.service.ServiceConfigUtil
			.getQueryProcessorConfigurationParameters();
		} catch (Exception e2) {
			e2.printStackTrace();
		}

		ImageRetrieve imageRet = new ImageRetrieve(imageQueryProcProps, true);
		final Vector <String> retrievedFiles = imageRet.performRetrieve(cQLQuery);
		// Step 2: We transfer the data

		// set up the piped streams
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		try {
			pis.connect(pos);
		} catch (IOException e) {
			throw new RemoteException("Unable to make a pipe", e);
		}

//		The part below needs to be threaded, since the transfer service creation reads from the stream completely.
		final PipedOutputStream fpos = pos;
		Thread t = new Thread() {

			@Override
			public void run() {
				// now write to the output stream.  for this test, use a zip stream.
				// this is really to deal with the fact that we don't have a good way to delimit the files.

				ZipEntryOutputStream zeos = null;
				ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fpos));
				for(int index = 0; index < retrievedFiles.size(); index++)
				{
					String transferDoc = retrievedFiles.get(index);
					System.out.println("transferDoc is " + transferDoc);
					try {
						zeos = new ZipEntryOutputStream(zos, new File(transferDoc).getName(), ZipEntry.STORED);
						BufferedInputStream imageIn = new BufferedInputStream(new FileInputStream(transferDoc));
						byte[] data = new byte[imageIn.available()];
						int bytesRead = 0;
						while ((bytesRead = (imageIn.read(data, 0, data.length))) > 0)  {
							zeos.write(data, 0, bytesRead);
							//System.out.println("Finished reading some part of Image file" + transferDoc);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.err.println("ERROR writing to zip entry " + e1.getMessage());
						e1.printStackTrace();
					} finally {
						try {
							zeos.flush();
							zeos.close();
							System.out.println("caGrid transferred at " + new Date().getTime());

						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println("ERROR closing zip entry " + e.getMessage());
							e.printStackTrace();
						}
					}
				}
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("ERROR closing zip stream " + e.getMessage());
					e.printStackTrace();
				}
				try {
					fpos.flush();
					fpos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		t.start();

		// set up the transfer context
		TransferServiceContextReference tscr = TransferServiceHelper.createTransferContext(pis, null);
		System.out.println("TransferServiceContextReference " + tscr);
		// set up the transfer context
		return tscr;
	}

	public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference submitImageData(submission.SubmissionInformation submissionInformation) throws RemoteException {
		System.out.println(submissionInformation.getFileType() + "...." + submissionInformation.getFileName());

		if(!submissionInformation.getFileType().equalsIgnoreCase("zip"))
			throw new RemoteException("Uploaded File Information is not of type zip");
		final submission.SubmissionInformation subInfo = submissionInformation;

		Properties imageQueryProcProps = null;
		try {
			imageQueryProcProps = gov.nih.nci.cagrid.data.service.ServiceConfigUtil
			.getQueryProcessorConfigurationParameters();
		} catch (Exception e2) {
			e2.printStackTrace();
		}


		DataStagedCallback callback = new DataStagedCallback() {

			public void dataStaged(TransferServiceContextResource resource) {
				ImageSubmit imgSub = new ImageSubmit(subInfo, true);
				imgSub.processImageUpload(resource.getDataStorageDescriptor().getLocation());				
			}
		};
		return TransferServiceHelper.createTransferContext(null, callback);
	}

}

