/*L
* Copyright The Ohio State University
* Copyright Emory University
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.io/invivo-imaging-middleware/LICENSE.txt for details.
*/

package edu.emory.cci.sts.loginmodule;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.authentication.stubs.types.AuthenticationProviderFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InsufficientAttributeFault;
import gov.nih.nci.cagrid.authentication.stubs.types.InvalidCredentialFault;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.List;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.GlobusCredential;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.AbstractServerLoginModule;


public class DorianLoginModule extends AbstractServerLoginModule {

	private Principal identity;
	private Map<String, List<Group>> roles;
	private String defaultRole = "STSClient";

	public Map<String, List<Group>> getRoles() {
		return roles;
	}

	public DorianLoginModule() {
		super();
		log.info("Creating a new instance of DorianLoginModule");
	}

	public void setRoles(Map<String, List<Group>> roles) {
		this.roles = roles;
	}



	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setIdentity(Principal identity) {
		this.identity = identity;
	}

	private String username;
	private Log log = LogFactory.getLog(getClass());
	private String idP;
	private Group defaultRoleGroup;
	private String authenticationService;



	public void initRoles() throws Exception {
		log.info("Initializing Default Role");
		Principal roleIdentity = createIdentity(defaultRole);
		defaultRoleGroup = new SimpleGroup("Roles");
		defaultRoleGroup.addMember(roleIdentity);

	}





	@Override
	protected Principal getIdentity() {

		return identity;
	}

	@Override
	protected Group[] getRoleSets() throws LoginException {
		log.info("Request for getting Groups [" + username + "]");
		return new Group[]{defaultRoleGroup};

	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandlers,
			Map<String, ?> sharedState, Map<String, ?> options) {

		super.initialize(subject, callbackHandlers, options, sharedState);

		try {
			idP = (String) options.get("identityProviderUrl");

			authenticationService = (String) options.get("authenticationService") ;

			initRoles();
			log.info("DorianLoginModuleInitialized");

		} catch (Exception e) {

			log.error("Failed to initialize roles");
		}

	}

	@Override
	public boolean login() throws LoginException {
		try {
			log.info("Starting the login process");
			if (idP == null)
				throw new LoginException("Identity Provider not set");

			String[] userPass = getUsernameAndPassword();
			SAMLAssertion retVal = loginCaBIG(userPass[0], userPass[1]);
			GlobusCredential credential = getCredential(retVal);

			if (retVal != null) {
				username = userPass[0];
				Principal principal  = createIdentity(username);
				identity = new PrincipalWithAttributes(principal);
				log.info("Login successful with user identity [" + identity
						+ "]");
				loginOk = true;

				((PrincipalWithAttributes)identity).setAttribute("globusCredential", credential);
				((PrincipalWithAttributes)identity).setAttribute("targetGrid", idP);


				return true;
			}
			else
			{
				return false;
			}


		} catch (IOException e) {
			log.error(e);
			throw new LoginException(e.getMessage());
		} catch (UnsupportedCallbackException e) {
			log.error(e);
			throw new LoginException(e.getMessage());

		} catch (Exception e) {
			log.error(e);
			throw new LoginException(e.getMessage());
		}

	}

	public SAMLAssertion loginCaBIG(String userId, String passwd) throws Exception {
		Credential credential = new Credential();
		BasicAuthenticationCredential cred = new BasicAuthenticationCredential();
		cred.setUserId(userId);
		cred.setPassword(passwd);
		credential.setBasicAuthenticationCredential(cred);




		AuthenticationClient client;
		SAMLAssertion saml = null;
		boolean authSuccessUsingAuthenticationService = false;
		if(authenticationService!=null)
		{
			try {
				log.info("Creating authentication client: URL=" + authenticationService + "; userId="
						+ userId );
				client = new AuthenticationClient(authenticationService, credential);
				log.info("Authenticating the credentials .....");
				saml = client.authenticate();
				authSuccessUsingAuthenticationService = true;
			} catch (Throwable e) {
				log.warn("Authentication using [" + authenticationService + "] failed. Trying with [" + idP + "]");
				authSuccessUsingAuthenticationService = false;

			}
		}

		if(authSuccessUsingAuthenticationService == false)
		{
			try {
				log.info("Creating authentication client: URL=" + idP + "; userId="
						+ userId );
				client = new AuthenticationClient(idP, credential);
				log.info("Authenticating the credentials .....");
				saml = client.authenticate();
				} catch (Throwable e) {
				log.error("Cannot initialize the Dorian client", e);
				throw new Exception("Cannot initialize the Dorian client");
			}
		}


		log.info("Dorian credential obtained");
		return saml;
		}

	private GlobusCredential getCredential(gov.nih.nci.cagrid.opensaml.SAMLAssertion saml) throws MalformedURIException, RemoteException {
		int certificateLifeTime = 12;
		gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime lifetime = new gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime();
        lifetime.setHours(certificateLifeTime);
        int delegationLifetime = 0;
        log.info("Connecting to Dorian to get proxy certificate with lifetime set to [" + certificateLifeTime + "] hrs");
        IFSUserClient dorian = new IFSUserClient(idP);
        log.info("Creating proxy certificate");
        GlobusCredential proxy = dorian.createProxy(saml, lifetime, delegationLifetime);
        log.info("Acquired proxy certificate");
        return proxy;

	}


	public String[] getUsernameAndPassword() throws IOException,
			UnsupportedCallbackException {

		NameCallback nameCallback = new NameCallback("Username:");
		PasswordCallback passCallback = new PasswordCallback("Password", false);

		callbackHandler.handle(new Callback[] { nameCallback, passCallback });
		String[] retVal = new String[2];
		retVal[0] = nameCallback.getName();
		retVal[1] = new String(passCallback.getPassword());
		return retVal;
	}

}
