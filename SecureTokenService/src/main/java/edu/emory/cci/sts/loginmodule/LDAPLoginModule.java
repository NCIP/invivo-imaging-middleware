/*L
* Copyright The Ohio State University
* Copyright Emory University
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.io/invivo-imaging-middleware/LICENSE.txt for details.
*/

package edu.emory.cci.sts.loginmodule;

import java.io.IOException;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Map;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.security.SimpleGroup;
import org.jboss.security.auth.spi.AbstractServerLoginModule;



public class LDAPLoginModule extends AbstractServerLoginModule {

	private String ldapServer;
	private Principal identity;
	private String defaultRole = "STSClient";
	private String username;
	private Log log = LogFactory.getLog(getClass());
	private Group defaultRoleGroup;
	private String dnPrefix = "";
	private String dnSuffix = "";

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
		return new Group[] { defaultRoleGroup };

	}

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandlers,
			Map sharedState, Map  options) {

		super.initialize(subject, callbackHandlers, options, sharedState);

		try {
			if(options.get("ldapServer")!=null)
			ldapServer = (String) options.get("ldapServer");
			else
				throw new Exception("The property [ldapServer] not set");

			if(options.get("dnPrefix")!=null)
				dnPrefix = (String) options.get("dnPrefix");
				else
					log.debug("The property [dnPrefix] not set. Defaulting to empty string");

			if(options.get("dnSuffix")!=null)
				dnSuffix = (String) options.get("dnSuffix");
				else
					log.debug("The property [dnSuffix] not set. Defaulting to empty string");

			if(options.get("defaultRole")!=null)
			defaultRole = (String) options.get("defaultRole");
				else
					log.debug("The property [defaultRole] not set. Defaulting to " + defaultRole);



			initRoles();
			log.info("LDAPLoginModule initialized");

		} catch (Exception e) {

			log.error("Failed to initialize roles",e);
		}

	}

	@Override
	public boolean login() throws LoginException {
		try {
			log.info("Starting the login process");
			if (ldapServer == null)
				throw new LoginException("LDAP server not specified");

			String[] userPass = getUsernameAndPassword();
			boolean authStatus = authLDAP(userPass[0], userPass[1]);

			if (authStatus == true) {
				username = userPass[0];
				Principal principal = createIdentity(username);
				identity = new PrincipalWithAttributes(principal);
				log.info("Login successful with user identity [" + identity
						+ "]");
				loginOk = true;

				return true;
			} else {
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

	public boolean authLDAP(String username, String password) {
		String dn =  dnPrefix + username + dnSuffix;
		Properties env = new Properties();
		env.put(Context.INITIAL_CONTEXT_FACTORY,
				"com.sun.jndi.ldap.LdapCtxFactory");
		env.put(Context.PROVIDER_URL, ldapServer);
		env.put(Context.SECURITY_PRINCIPAL, dn);
		env.put(Context.SECURITY_CREDENTIALS, password);

		try {
			DirContext ctx = new InitialDirContext(env);
			log.info("LDAP Auth succeeded for [" + dn + "]");
		} catch (NamingException e) {
			log.error("Failed LDAP Auth using DN [" + dn  +"]",e);
			return false;
		}

		return true;
	}

}
