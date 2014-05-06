package gov.nih.nci.ivi.dsdcontainerservice.service;

import gov.nih.nci.ivi.dsd.URL;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.zip.ZipInputStream;

import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.globus.wsrf.core.deploy.DeployFaultType;
import org.globus.wsrf.core.deploy.DeployOptions;
import org.globus.wsrf.core.deploy.DeployPortType;
import org.globus.wsrf.core.deploy.DeployRequest;
import org.globus.wsrf.core.deploy.OperationNotSupportedFaultType;
import org.globus.wsrf.core.deploy.TryAgainLaterFaultType;

/** 
 * TODO:I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class DSDContainerServiceImpl extends DSDContainerServiceImplBase {

	private Options options = null;
	private String customUsage = null;
	
    private static final Option DEPLOY_OPTION =
        OptionBuilder.withDescription("Perform GAR deploy only")
        .withLongOpt("deploy")
        .create("y");
    
    private static final Option TRANSFER_OPTION =
        OptionBuilder.withDescription("Perform GAR transfer only")
        .withLongOpt("transfer")
        .create("n");

    private static final Option CREATE_BACKUP =
        OptionBuilder.withDescription("Create backup of configuration files")
        .withLongOpt("backup")
        .create("b");

    private static final Option OVERWRITE_OPTION =
        OptionBuilder.withDescription("Overwrite existing deployment")
        .withLongOpt("overwrite")
        .create("o");

    private static final Option PROFILE_OPTION =
        OptionBuilder.withArgName( "name" )
        .hasArg()
        .withDescription("Specify configuration profile name")
        .withLongOpt("profile")
        .create("r");

    public void setCustomUsage(String customUsage) {
        this.customUsage = customUsage;
    }

	
    private void deployToContainer(DeployPortType port, DeployOptions options, String id) {
    	DeployRequest request = new DeployRequest();
		request.setID(id);
		request.setOptions(options);
		try {
			port.deploy(request);
		} catch (TryAgainLaterFaultType e) {
			e.printStackTrace();
		} catch (OperationNotSupportedFaultType e) {
			e.printStackTrace();
		} catch (DeployFaultType e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		System.out.println("GAR was successfully deployed.");
	}

	
	public DSDContainerServiceImpl() throws RemoteException {
		super();
		options = new Options();
        options.addOption(DEPLOY_OPTION);
        options.addOption(TRANSFER_OPTION);
        options.addOption(CREATE_BACKUP);
        options.addOption(OVERWRITE_OPTION);
        options.addOption(PROFILE_OPTION);
	}
	
  public gov.nih.nci.ivi.dsd.URL deploy(gov.nih.nci.ivi.dsd._package packageInfo,org.cagrid.transfer.context.stubs.types.TransferServiceContextReference transferServiceURL) throws RemoteException {
		
	  TransferServiceContextClient tclient = null;
	  InputStream istream = null;
	  try {
		tclient = new TransferServiceContextClient(transferServiceURL.getEndpointReference());
	    istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
	} catch (MalformedURIException e1) {
		// TODO Auto-generated catch block
		e1.printStackTrace();
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	  String localDownloadLocation = null;
	  ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while(true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {
				System.err.println("IOException thrown when recieving the zip stream");
				e.printStackTrace();
			}

			//System.out.println(zeis.getName());
			String cacheDir = null;
			try {
				cacheDir = DSDContainerServiceConfiguration.getConfiguration().getCacheDir();
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			localDownloadLocation =  cacheDir + File.separator + packageInfo.getIdentifier();
			File localLocation = new File(localDownloadLocation);
			if(!localLocation.exists())
				localLocation.mkdirs();

			String unzzipedFile = localDownloadLocation + File.separator + zeis.getName();
			bis = new BufferedInputStream(zeis);
			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(unzzipedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0)  {
					bos.write(data, 0, bytesRead);
					//System.out.println(new String(data));
					//System.out.println("caGrid transferred at " + new Date().getTime());
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				System.err.println("IOException thrown when reading the zip stream");
			}
		}

		try {
			zis.close();
		} catch (IOException e) {
			System.err.println("IOException thrown when closing the zip stream");
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			e.printStackTrace();
			System.err.println("Remote exception thrown when closing the transer context");
		}
	  
		System.out.println("DSDCS: Retrieved file to " + localDownloadLocation);

        // DEPLOY THE PACKAGE HERE WHEN THE GLOBUS TECHNOLOGY IS READY FOR THIS IN THE FUTURE
		// THE CODE COMMENTED BELOW CAN BE USED FOR THIS PURPOSE
/*
		Properties defaultOptions = new Properties();
// default service address
		defaultOptions.put(BaseClient.SERVICE_URL.getOpt(), "https://localhost:8080/wsrf/services/DeployService");
// GSI Secure Msg (signature)
		defaultOptions.put(BaseClient.PROTECTION.getOpt(), "sig");
// self authorization    
		defaultOptions.put(BaseClient.AUTHZ.getOpt(), "hostSelf");
		String garFile = null;
		CommandLine line = null;
		this.setCustomUsage("gar");
		try {
			List options = null;
			options.add(outputLocation);
			if (options == null || options.isEmpty()) {
				throw new ParseException("Expected gar argument");
			}
			garFile = (String)options.get(0);
			if (line.hasOption(DEPLOY_OPTION.getOpt()) && line.hasOption(TRANSFER_OPTION.getOpt())) {
				throw new ParseException(DEPLOY_OPTION.getOpt() + " and " + TRANSFER_OPTION.getOpt() + " arguments are exclusive");
			}
		} catch(ParseException e) {
			System.err.println("Error: " + e.getMessage());
		} catch (Exception e) {
			System.err.println("Error: " + e.getMessage());
		}
		DeployServiceAddressingLocator locator = new DeployServiceAddressingLocator();
		DeployOptions options = new DeployOptions();

		//options.setPerformValidation(Boolean.FALSE);

		// profile name
		if (line.hasOption(PROFILE_OPTION.getOpt())) {
			options.setProfile(line.getOptionValue(PROFILE_OPTION.getOpt()));
		}

		// overwrite option
		options.setOverwrite( (line.hasOption(OVERWRITE_OPTION.getOpt())) ? Boolean.TRUE : Boolean.FALSE );

		// create backup
		options.setCreateBackup( (line.hasOption(CREATE_BACKUP.getOpt())) ? Boolean.TRUE : Boolean.FALSE );

		// always deploy only (do not undeploy first)
		options.setUndeployFirst(Boolean.FALSE);
		try {
			DeployPortType port = locator.getDeployPortTypePort(client.getEndpointReference());
//            this.setOptions((Stub)port);

			deployToContainer(port, options, garFile);
		} catch(Exception e) {
			System.err.println("GAR deploy failed: " + FaultHelper.getMessage(e));
			System.exit(-1);
		}
		URL deployedURL = new URL();
		deployedURL.setValue("deployment URL");
*/
		URL deployedURL = new URL();
		deployedURL.setValue("some URL");

		return deployedURL;
  }

  public gov.nih.nci.ivi.dsd.URL undeploy(gov.nih.nci.ivi.dsd._package packageInfo) throws RemoteException {
    //TODO: Implement this autogenerated method
    throw new RemoteException("Not yet implemented");
  }

}

