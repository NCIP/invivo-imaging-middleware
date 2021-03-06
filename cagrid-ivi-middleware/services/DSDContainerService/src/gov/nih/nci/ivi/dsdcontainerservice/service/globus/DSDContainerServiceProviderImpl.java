package gov.nih.nci.ivi.dsdcontainerservice.service.globus;

import gov.nih.nci.ivi.dsdcontainerservice.service.DSDContainerServiceImpl;

import java.rmi.RemoteException;

/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * This class implements each method in the portType of the service.  Each method call represented
 * in the port type will be then mapped into the unwrapped implementation which the user provides
 * in the DSDContainerServiceImpl class.  This class handles the boxing and unboxing of each method call
 * so that it can be correclty mapped in the unboxed interface that the developer has designed and 
 * has implemented.  Authorization callbacks are automatically made for each method based
 * on each methods authorization requirements.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class DSDContainerServiceProviderImpl{
	
	DSDContainerServiceImpl impl;
	
	public DSDContainerServiceProviderImpl() throws RemoteException {
		impl = new DSDContainerServiceImpl();
	}
	

    public gov.nih.nci.ivi.dsdcontainerservice.stubs.DeployResponse deploy(gov.nih.nci.ivi.dsdcontainerservice.stubs.DeployRequest params) throws RemoteException {
    gov.nih.nci.ivi.dsdcontainerservice.stubs.DeployResponse boxedResult = new gov.nih.nci.ivi.dsdcontainerservice.stubs.DeployResponse();
    boxedResult.setURL(impl.deploy(params.getPackageInfo().get_package(),params.getTransferServiceURL().getTransferServiceContextReference()));
    return boxedResult;
  }

    public gov.nih.nci.ivi.dsdcontainerservice.stubs.UndeployResponse undeploy(gov.nih.nci.ivi.dsdcontainerservice.stubs.UndeployRequest params) throws RemoteException {
    gov.nih.nci.ivi.dsdcontainerservice.stubs.UndeployResponse boxedResult = new gov.nih.nci.ivi.dsdcontainerservice.stubs.UndeployResponse();
    boxedResult.setURL(impl.undeploy(params.getPackageInfo().get_package()));
    return boxedResult;
  }

}
