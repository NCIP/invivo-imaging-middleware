package edu.emory.cci.aim.service.globus;

import edu.emory.cci.aim.service.AIM3DataServiceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the AIM3DataServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class AIM3DataServiceProviderImpl{
	
	AIM3DataServiceImpl impl;
	
	public AIM3DataServiceProviderImpl() throws RemoteException {
		impl = new AIM3DataServiceImpl();
	}
	

    public edu.emory.cci.aim.stubs.SubmitResponse submit(edu.emory.cci.aim.stubs.SubmitRequest params) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
    edu.emory.cci.aim.stubs.SubmitResponse boxedResult = new edu.emory.cci.aim.stubs.SubmitResponse();
    impl.submit(params.getXmls());
    return boxedResult;
  }

    public edu.emory.cci.aim.stubs.QueryByTransferResponse queryByTransfer(edu.emory.cci.aim.stubs.QueryByTransferRequest params) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
    edu.emory.cci.aim.stubs.QueryByTransferResponse boxedResult = new edu.emory.cci.aim.stubs.QueryByTransferResponse();
    boxedResult.setTransferServiceContextReference(impl.queryByTransfer(params.getCqlQuery().getCQLQuery()));
    return boxedResult;
  }

    public edu.emory.cci.aim.stubs.SubmitByTransferResponse submitByTransfer(edu.emory.cci.aim.stubs.SubmitByTransferRequest params) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType {
    edu.emory.cci.aim.stubs.SubmitByTransferResponse boxedResult = new edu.emory.cci.aim.stubs.SubmitByTransferResponse();
    boxedResult.setTransferServiceContextReference(impl.submitByTransfer());
    return boxedResult;
  }

}
