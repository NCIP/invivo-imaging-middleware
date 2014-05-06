package edu.emory.cci.aim.service;

import edu.emory.cci.aim.service.globus.resource.AIMTCGADataServiceResource;
import  edu.emory.cci.aim.service.AIMTCGADataServiceConfiguration;

import java.rmi.RemoteException;

import javax.naming.InitialContext;
import javax.xml.namespace.QName;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;
import org.globus.wsrf.ResourceContext;
import org.globus.wsrf.ResourceContextException;
import org.globus.wsrf.ResourceException;
import org.globus.wsrf.ResourceHome;
import org.globus.wsrf.ResourceProperty;
import org.globus.wsrf.ResourcePropertySet;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 *
 * Provides some simple accessors for the Impl.
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public abstract class AIMTCGADataServiceImplBase {
	
	public AIMTCGADataServiceImplBase() throws RemoteException {
	
	}
	
	public AIMTCGADataServiceConfiguration getConfiguration() throws Exception {
		return AIMTCGADataServiceConfiguration.getConfiguration();
	}
	
	
	public edu.emory.cci.aim.service.globus.resource.AIMTCGADataServiceResourceHome getResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("home");
		return (edu.emory.cci.aim.service.globus.resource.AIMTCGADataServiceResourceHome)resource;
	}

	
	
	
	public ResourceHome getCaGridEnumerationResourceHome() throws Exception {
		ResourceHome resource = getResourceHome("caGridEnumerationHome");
		return resource;
	}
	
	
	protected ResourceHome getResourceHome(String resourceKey) throws Exception {
		MessageContext ctx = MessageContext.getCurrentContext();

		ResourceHome resourceHome = null;
		
		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/" + resourceKey;
		try {
			javax.naming.Context initialContext = new InitialContext();
			resourceHome = (ResourceHome) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate resource home. : " + resourceKey, e);
		}

		return resourceHome;
	}


}

