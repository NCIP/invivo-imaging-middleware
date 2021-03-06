package gov.nih.nci.ivi.pathologydataservice.service.globus;

import gov.nih.nci.ivi.pathologydataservice.service.PathologyDataServiceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the PathologyDataServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class PathologyDataServiceProviderImpl{
	
	PathologyDataServiceImpl impl;
	
	public PathologyDataServiceProviderImpl() throws RemoteException {
		impl = new PathologyDataServiceImpl();
	}
	

    public gov.nih.nci.ivi.pathologydataservice.stubs.ViewResponse view(gov.nih.nci.ivi.pathologydataservice.stubs.ViewRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.ViewResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.ViewResponse();
    boxedResult.setResponse(impl.view(params.getViewParams().getViewParams()));
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveResponse retrieve(gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveResponse();
    boxedResult.setTransferServiceContextReference(impl.retrieve(params.getCQLQuery().getCQLQuery()));
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.SubmitResponse submit(gov.nih.nci.ivi.pathologydataservice.stubs.SubmitRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.SubmitResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.SubmitResponse();
    boxedResult.setTransferServiceContextReference(impl.submit());
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.DeleteResponse delete(gov.nih.nci.ivi.pathologydataservice.stubs.DeleteRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.DeleteResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.DeleteResponse();
    impl.delete(params.getFileInfo().getFileInfo());
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.ExecuteResponse execute(gov.nih.nci.ivi.pathologydataservice.stubs.ExecuteRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.ExecuteResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.ExecuteResponse();
    impl.execute(params.getExecutionParameters().getExecutionParameters());
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveThumbnailResponse retrieveThumbnail(gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveThumbnailRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveThumbnailResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.RetrieveThumbnailResponse();
    boxedResult.setTransferServiceContextReference(impl.retrieveThumbnail(params.getCQLQuery().getCQLQuery()));
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.GetWidthResponse getWidth(gov.nih.nci.ivi.pathologydataservice.stubs.GetWidthRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.GetWidthResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.GetWidthResponse();
    boxedResult.setResponse(impl.getWidth(params.getCQLQuery().getCQLQuery()));
    return boxedResult;
  }

    public gov.nih.nci.ivi.pathologydataservice.stubs.GetHeightResponse getHeight(gov.nih.nci.ivi.pathologydataservice.stubs.GetHeightRequest params) throws RemoteException {
    gov.nih.nci.ivi.pathologydataservice.stubs.GetHeightResponse boxedResult = new gov.nih.nci.ivi.pathologydataservice.stubs.GetHeightResponse();
    boxedResult.setResponse(impl.getHeight(params.getCQLQuery().getCQLQuery()));
    return boxedResult;
  }

}
