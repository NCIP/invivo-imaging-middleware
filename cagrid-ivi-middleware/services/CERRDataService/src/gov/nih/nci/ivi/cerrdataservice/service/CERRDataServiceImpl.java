package gov.nih.nci.ivi.cerrdataservice.service;

import gov.nih.nci.cagrid.data.DataServiceConstants;
import gov.nih.nci.cagrid.data.InitializationException; 
import edu.osu.bmi.utils.io.zip.ZipEntryOutputStream;
import edu.osu.bmi.xml.service.delegates.transfer.SubmitByTransferDelegate; 

import edu.osu.bmi.xml.service.delegates.transfer.QueryByTransferDelegate; 

import gov.nih.nci.cagrid.data.InitializationException; 
import edu.osu.bmi.xml.service.delegates.SubmitDelegate; 

import gov.nih.nci.cagrid.common.FaultHelper; 
import gov.nih.nci.cagrid.data.QueryProcessingException; 
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor; 
import edu.osu.bmi.xml.db.XMLDBConnector; 
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType; 
import gov.nih.nci.cagrid.data.service.ServiceConfigUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException; 
import java.util.Date;
import java.util.Enumeration;
import java.util.Properties; 
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import edu.osu.bmi.xml.service.delegates.DataServiceUtils; 

import java.rmi.RemoteException;

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
public class CERRDataServiceImpl extends CERRDataServiceImplBase {


	private Properties cqlQueryProcessorConfig = null;
	private CQLQueryProcessor queryProcessorInstance = null;
	private XMLDBConnector connector = null;

	

	
	public CERRDataServiceImpl() throws RemoteException {
		super();
	}
	
  public void submit(java.lang.String[] xmls) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
	 if (cqlQueryProcessorConfig == null) { 
		 try { 
			 cqlQueryProcessorConfig = DataServiceUtils.getCqlQueryProcessorConfig(); 
		 } catch (QueryProcessingException e2) { 
			 FaultHelper helper = new FaultHelper( 
				 new QueryProcessingExceptionType()); 
			 helper.addFaultCause(e2); 
				 throw (QueryProcessingExceptionType) helper.getFault(); 
		 } 
	 } 
	 if (connector == null) { 
		 try { 
			 connector = XMLDBConnector.newInstance(cqlQueryProcessorConfig); 
		 } catch (InitializationException e2) { 
			 FaultHelper helper = new FaultHelper( 
				 new QueryProcessingExceptionType()); 
			 helper.addFaultCause(e2); 
			 throw (QueryProcessingExceptionType) helper.getFault(); 
		 } 
	 } 

	 SubmitDelegate.submit(connector, xmls); 
}

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference queryByTransfer(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
	 if (cqlQueryProcessorConfig == null) { 
		 try { 
			 cqlQueryProcessorConfig = DataServiceUtils.getCqlQueryProcessorConfig(); 
		 } catch (QueryProcessingException e2) { 
			 FaultHelper helper = new FaultHelper( 
				 new QueryProcessingExceptionType()); 
			 helper.addFaultCause(e2); 
				 throw (QueryProcessingExceptionType) helper.getFault(); 
		 } 
	 } 
	 if (queryProcessorInstance == null) { 
		 try { 
			 queryProcessorInstance = DataServiceUtils.getCqlQueryProcessorInstance(cqlQueryProcessorConfig); 
		 } catch (QueryProcessingException e2) { 
			 FaultHelper helper = new FaultHelper( 
				 new QueryProcessingExceptionType()); 
			 helper.addFaultCause(e2); 
			 throw (QueryProcessingExceptionType) helper.getFault(); 
		 } 
	 } 

	 return QueryByTransferDelegate.queryByTransfer( 
		 (LazyCQLQueryProcessor) queryProcessorInstance, cqlQuery); 
}

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference submitByTransfer() throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
	 if (cqlQueryProcessorConfig == null) { 
		 try { 
			 cqlQueryProcessorConfig = DataServiceUtils.getCqlQueryProcessorConfig(); 
		 } catch (QueryProcessingException e2) { 
			 FaultHelper helper = new FaultHelper( 
				 new QueryProcessingExceptionType()); 
			 helper.addFaultCause(e2); 
				 throw (QueryProcessingExceptionType) helper.getFault(); 
		 } 
	 } 
	 if (connector == null) { 
		 try { 
			 connector = XMLDBConnector.newInstance(cqlQueryProcessorConfig); 
		 } catch (InitializationException e2) { 
			 FaultHelper helper = new FaultHelper( 
				 new QueryProcessingExceptionType()); 
			 helper.addFaultCause(e2); 
			 throw (QueryProcessingExceptionType) helper.getFault(); 
		 } 
	 } 

	 return SubmitByTransferDelegate.submitByTransfer( 
		 connector); 
}

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieveCERRObjects(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException {
		CQLQueryProcessor processor;
		try {
			processor = getCqlQueryProcessorInstance();
		} catch (QueryProcessingException e2) {
			FaultHelper helper = new FaultHelper(new QueryProcessingExceptionType());
			helper.addFaultCause(e2);
			throw (QueryProcessingExceptionType) helper.getFault();
		}

		// Step 1: Get filenames of Matlab objects
		CERRRetrieve cerrRet = new CERRRetrieve(processor, 0);
		final Vector<String> retrievedFiles = cerrRet.performRetrieve(cQLQuery);
		if(retrievedFiles.size() == 0)
		{
			System.out.println("no files were retrieved ÑÊreturning a null TrfContClient");
			return null;
		}
		// Step 2: We transfer the data


		// set up the piped streams
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		try {
			pis.connect(pos);
		} catch (IOException e) {
			throw new RemoteException("Unable to make a pipe", e);
		}

		// The part below needs to be threaded, since the transfer service
		// creation reads from the stream completely.
		final PipedOutputStream fpos = pos;
		Thread t = new Thread() {

			@Override
			public void run() {
				// now write to the output stream. for this test, use a zip
				// stream.
				// this is really to deal with the fact that we don't have a
				// good way to delimit the files.
				double t1 = System.currentTimeMillis() / 1000.0;

				ZipEntryOutputStream zeos = null;
				ZipOutputStream zos = new ZipOutputStream(
						new BufferedOutputStream(fpos, 524288));
				for (int index = 0; index < retrievedFiles.size(); index++) {
					String transferDoc = retrievedFiles.get(index);
					System.out.println("transferDoc is " + transferDoc);
					try {
						zeos = new ZipEntryOutputStream(zos, new File(
								transferDoc).getName(), ZipEntry.DEFLATED);
						BufferedInputStream cerrIn = new BufferedInputStream(
								new FileInputStream(transferDoc));
						byte[] data = new byte[cerrIn.available()];
						int bytesRead = 0;
						while ((bytesRead = (cerrIn.read(data, 0, data.length))) > 0) {
							zeos.write(data);
							// System.out.println("Finished reading some part of
							// DICOM file" + transferDoc);
						}
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						System.err.println("ERROR writing to zip entry "
								+ e1.getMessage());
						e1.printStackTrace();
					} finally {
						try {
							zeos.flush();
							zeos.close();
							System.out.println("caGrid transferred at "
									+ new Date().getTime());

						} catch (IOException e) {
							// TODO Auto-generated catch block
							System.err.println("ERROR closing zip entry "
									+ e.getMessage());
							e.printStackTrace();
						}
					}
				}
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					System.err.println("ERROR closing zip stream "
							+ e.getMessage());
					e.printStackTrace();
				}
				try {
					fpos.flush();
					fpos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				double t2 = System.currentTimeMillis() / 1000.0;

				System.out.println("Time Taken = " + (t2 - t1));

			}
		};
		t.start();

		// set up the transfer context
		TransferServiceContextReference tscr = TransferServiceHelper
				.createTransferContext(pis, null);
		System.out.println("TransferServiceContextReference " + tscr);
		// set up the transfer context
		return tscr;
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference submitCERRObject(edu.osu.bmi.ivi.cerr.CERRObject cERRObject) throws RemoteException {
	  Properties queryProcProps = null;
		final edu.osu.bmi.ivi.cerr.CERRObject fCerrObj = cERRObject;
		try {
			queryProcProps = gov.nih.nci.cagrid.data.service.ServiceConfigUtil
			.getQueryProcessorConfigurationParameters();
		} catch (Exception e2) {
			e2.printStackTrace();
		}
		final Properties queryProps = queryProcProps;

		DataStagedCallback callback = new DataStagedCallback() {

			public void dataStaged(TransferServiceContextResource resource) {
				CERRSubmit dicomSub = new CERRSubmit(queryProps, fCerrObj);
				dicomSub.processCERRUpload(resource.getDataStorageDescriptor().getLocation());
			}
		};
		return TransferServiceHelper.createTransferContext(null, callback);
  }


	protected Properties getCqlQueryProcessorConfig() throws QueryProcessingException {
		if (cqlQueryProcessorConfig == null) {
			try {
				cqlQueryProcessorConfig = ServiceConfigUtil.getQueryProcessorConfigurationParameters();
			} catch (Exception ex) {
				throw new QueryProcessingException(
					"Error getting query processor configuration parameters: " + ex.getMessage(), ex);
			}
		}
		// clone the query processor config instance
		// (in case they get modified by the Query Processor implementation)
		Properties clone = new Properties();
		Enumeration keyEnumeration = cqlQueryProcessorConfig.keys();
		while (keyEnumeration.hasMoreElements()) {
			String key = (String) keyEnumeration.nextElement();
			String value = cqlQueryProcessorConfig.getProperty(key);
			clone.setProperty(key, value);
		}
		return clone;
	}

  
  public CQLQueryProcessor getCqlQueryProcessorInstance() throws QueryProcessingException {
		if (queryProcessorInstance == null) {
			// get the query processor's class
			String qpClassName = null;
			try {
				qpClassName = ServiceConfigUtil.getCqlQueryProcessorClassName();
			} catch (Exception ex) {
				throw new QueryProcessingException(
					"Error determining query processor class name: " + ex.getMessage(), ex);
			}
			Class cqlQueryProcessorClass = null;
			try {
				cqlQueryProcessorClass = Class.forName(qpClassName);
			} catch (ClassNotFoundException ex) {
				throw new QueryProcessingException(
					"Error loading query processor class: " + ex.getMessage(), ex);
			}
			// create a new instance of the query processor
			try {
				queryProcessorInstance = (gov.nih.nci.cagrid.data.cql.CQLQueryProcessor) cqlQueryProcessorClass.newInstance();
			} catch (Exception ex) {
				throw new QueryProcessingException(
					"Error creating query processor instance: " + ex.getMessage(), ex);
			}
			// configure the instance
			try {
				String serverConfigLocation = ServiceConfigUtil.getConfigProperty(
					DataServiceConstants.SERVER_CONFIG_LOCATION);
				InputStream configStream = new FileInputStream(serverConfigLocation);
				Properties configuredProperties = getCqlQueryProcessorConfig();
				Properties defaultProperties = queryProcessorInstance.getRequiredParameters();
				Properties unionProperties = new Properties();
				Enumeration defaultKeys = defaultProperties.keys();
				while (defaultKeys.hasMoreElements()) {
					String key = (String) defaultKeys.nextElement();
					String value = null;
					if (configuredProperties.keySet().contains(key)) {
						value = configuredProperties.getProperty(key);
					} else {
						value = defaultProperties.getProperty(key);
					}
					unionProperties.setProperty(key, value);
				}
				queryProcessorInstance.initialize(unionProperties, configStream);
			} catch (Exception ex) {
				throw new QueryProcessingException("Error initializing query processor: " + ex.getMessage(), ex);
			}
		}
		return queryProcessorInstance;
	}

  
}

