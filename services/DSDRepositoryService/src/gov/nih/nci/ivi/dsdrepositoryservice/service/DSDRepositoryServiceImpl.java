package gov.nih.nci.ivi.dsdrepositoryservice.service;

import gov.nih.nci.ivi.dsd.ContainerInfo;
import gov.nih.nci.ivi.dsd.PackageContainerRequisiteInfo;
import gov.nih.nci.ivi.dsd.PackageRepositoryURL;
import gov.nih.nci.ivi.dsd.URL;
import gov.nih.nci.ivi.dsdcontainerservice.client.DSDContainerServiceClient;
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
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

/** 
 * The DSD repository service implements mechanisms for deploying/undeploying a service package (i.e., a .GAR file) on
 * a remote DSD container service. During the deployment, the package is transfered by the DSD container service from the
 * DSD repository service using gridFTP.
 *
 * @created by Introduce Toolkit version 1.2
 * @see DSDContainerServiceImpl
 */
public class DSDRepositoryServiceImpl extends DSDRepositoryServiceImplBase {


	public DSDRepositoryServiceImpl() throws RemoteException {
		super();
	}

	private String modifyURL(String str) {
		return str.replaceAll("/", "-");
	}

	private void validatePackage(gov.nih.nci.ivi.dsd._package packageInfo) throws RemoteException {
		if (packageInfo == null)
			throw new RemoteException("PackageInfo is null.");
		if (packageInfo.getContainerURL() == null || packageInfo.getContainerURL().getURL() == null)
			throw new RemoteException("The container URL provided is null.");
		if (packageInfo.getIdentifier() == null)
			throw new RemoteException("The package identifier provided is null.");
	}

	/** 
	 * This method starts deployment of a package in the repository on a remote container service. Basically, some information
	 * about the package and the URL at which the package is stored is passed to the container service. The container service
	 * than transfers the package from the repository using the Transfer Service.
	 * 
	 * @param packageInfo Some information about the deployment environment and package properties.
	 * @return 
	 * @return On successful deployment, the URL where the package deployed is returned. On failure, either
	 * an exception is thrown or a null URL is returned.
	 *
	 */

	public gov.nih.nci.ivi.dsd.URL deploy(gov.nih.nci.ivi.dsd._package packageInfo) throws RemoteException {

		validatePackage(packageInfo);
		DSDContainerServiceClient client = null;

		String rootDirectory = null;
		try {
			rootDirectory = DSDRepositoryServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}

		final String rootDir = rootDirectory;		

		String packageLocation = rootDir + File.separator + "packages" + File.separator + packageInfo.getIdentifier();
		if (!(new File(packageLocation).exists()))
			throw new RemoteException("Package " + packageInfo.getIdentifier() + " does not exist.");
		File packageFile = new File(packageLocation);
		if (!packageFile.exists() || !packageFile.canRead())
			throw new RemoteException("Package " + packageInfo.getIdentifier() + " does not exist.");

		System.out.println("DSDRS: Trying to deploy " + packageInfo.getIdentifier() + " on " + packageInfo.getContainerURL().getURL().getValue());
		File containerServicePackageFlag = new File(rootDir + File.separator + modifyURL(packageInfo.getContainerURL().getURL().getValue()) + File.separator + packageInfo.getIdentifier());
		URL resultURL = null;
		if (containerServicePackageFlag.exists())
			throw new RemoteException("Package " + packageInfo.getIdentifier() + " is already deployed.");
		else {
			try {
				client = new DSDContainerServiceClient(packageInfo.getContainerURL().getURL().getValue());
			} catch (MalformedURIException e) {
				throw new RemoteException(e.getMessage());
			}
			// prepare the package
			// set the repository URL
			org.apache.axis.MessageContext ctx = org.apache.axis.MessageContext.getCurrentContext();
			String transportURL = (String) ctx.getProperty(org.apache.axis.MessageContext.TRANS_URL);
			System.out.println("DSDRS: Repository service URL: " + transportURL);
			PackageRepositoryURL packageRepositoryURL = new PackageRepositoryURL();
			URL[] repositoryURLs = new URL[1];
			repositoryURLs[0] = new URL();
			repositoryURLs[0].setValue(transportURL);
			packageRepositoryURL.setURL(repositoryURLs);
			packageInfo.setRepositoryURL(packageRepositoryURL);

			PackageContainerRequisiteInfo containerRequisiteInfo = new PackageContainerRequisiteInfo();
			ContainerInfo containerInfo = new ContainerInfo();
			containerInfo.setId(0);
			containerRequisiteInfo.setContainerInfo(containerInfo);
			packageInfo.setContainerRequisiteInfo(containerRequisiteInfo);			



			// Get Package to Transfer Service
			// set up the piped streams
			PipedOutputStream pos = new PipedOutputStream();
			PipedInputStream pis = new PipedInputStream();
			try {
				pis.connect(pos);
			} catch (IOException e) {
				throw new RemoteException("Unable to make a pipe", e);
			}

//			The part below needs to be threaded, since the transfer service creation reads from the stream completely.
			final PipedOutputStream fpos = pos;
			final String packageIdentifier = packageInfo.getIdentifier();
			Thread t = new Thread() {

				@Override
				public void run() {
					// now write to the output stream.  for this test, use a zip stream.
					// this is really to deal with the fact that we don't have a good way to delimit the files.
					ZipEntryOutputStream zeos = null;
					ZipOutputStream zos = new ZipOutputStream(new BufferedOutputStream(fpos));

					String transferDoc = rootDir + File.separator + "packages" + File.separator + packageIdentifier;
					System.out.println("transferDoc is " + transferDoc);
					try {
						zeos = new ZipEntryOutputStream(zos, new File(transferDoc).getName(), ZipEntry.STORED);
						BufferedInputStream dicomIn = new BufferedInputStream(new FileInputStream(transferDoc));
						byte[] data = new byte[dicomIn.available()];
						int bytesRead = 0;
						while ((bytesRead = (dicomIn.read(data, 0, data.length))) > 0)  {
							zeos.write(data, 0, bytesRead);
							//System.out.println("Finished reading some part of DICOM file" + transferDoc);
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

			resultURL = client.deploy(packageInfo, tscr);
	    	containerServicePackageFlag.getParentFile().mkdirs();
		    try {
				containerServicePackageFlag.createNewFile();
			} catch (IOException e) {
				throw new RemoteException(e.getMessage());
			}
		}
		return resultURL;
	}

	/** 
	 * This method starts undeployment of a service running on a remote container. Basically, some information
	 * about the service to be undeployed is passed to the container service by this method. The container service
	 * than tries to undeploy the package from the container.
	 * 
	 * @param packageInfo Some information about the environment and service properties.
	 * @return On successful undeployment, null is returned. On failure, either
	 * an exception is thrown or the URL of the still-running service is returned.
	 *
	 */
	public gov.nih.nci.ivi.dsd.URL undeploy(gov.nih.nci.ivi.dsd._package packageInfo) throws RemoteException {
		DSDContainerServiceClient client = null;
		try {
			client = new DSDContainerServiceClient(packageInfo.getContainerURL().getURL().getValue());
		} catch (MalformedURIException e) {
			throw new RemoteException(e.getMessage());
		}
		String rootDir = null;
		try {
			rootDir = DSDRepositoryServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
		URL resultURL = null;
		File flag = new File(rootDir + File.separator + modifyURL(packageInfo.getContainerURL().getURL().getValue()) + File.separator + packageInfo.getIdentifier());
	    if (flag.exists()) {
			System.out.println("DSDRS: Trying to undeploy " + packageInfo.getIdentifier() + " on " + packageInfo.getContainerURL().getURL().getValue());
			resultURL = client.undeploy(packageInfo);
	    	if (resultURL == null)
	    		flag.delete();
	    }
	    else {
	    	throw new RemoteException("Package is not previously deployed");
	    }

	    return resultURL;
	}

}

