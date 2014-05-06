package gov.nih.nci.ivi.utils;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.common.security.ProxyUtil;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import org.cagrid.gaards.ui.common.CredentialManager;
import gov.nih.nci.cagrid.introduce.security.client.ServiceSecurityClient;
import gov.nih.nci.cagrid.metadata.security.CommunicationMechanism;
import gov.nih.nci.cagrid.metadata.security.ProtectionLevelType;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;

import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.GlobusGSSCredentialImpl;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;

public class ClientSecurityUtil {
	public static String ANONYMOUS_USER = "<anonymous>";

	// this block of code checks to see if the credential is valid and has enough lifetime.
	// thie code is tied to the "initialized" method and should only be executed once for all tests
	// related to this test
	public static synchronized GlobusCredential getDefaultCredential(long minimumLifetime,
			String idpURL, String ifsURL, String userId, String passwd) throws GlobusCredentialException {
		GlobusCredential cred = null;
		try {
			cred = GlobusCredential.getDefaultCredential();
			// relogin if the credential is going to expire within 20 minutes.
			if (cred.getTimeLeft() <= minimumLifetime) {
				login(idpURL, ifsURL, userId, passwd);
			}
		} catch (GlobusCredentialException e) {
			// something failed.  try  login
			login(idpURL, ifsURL, userId, passwd);
		}

		return GlobusCredential.getDefaultCredential();

	}

	private static synchronized void login(String idpURL, String ifsURL, String userId, String passwd) {
		// need to log in...
		Credential credential = new Credential();
		BasicAuthenticationCredential cred = new BasicAuthenticationCredential();
		cred.setUserId(userId);
		cred.setPassword(passwd);
		credential.setBasicAuthenticationCredential(cred);
		try {
			AuthenticationClient client = new AuthenticationClient(idpURL,credential);
			SAMLAssertion saml = client.authenticate();
			IFSUserClient c2 = new IFSUserClient(ifsURL);
			ProxyLifetime lifetime = new ProxyLifetime();
			lifetime.setHours(12);
			lifetime.setMinutes(0);
			lifetime.setSeconds(0);
			int delegation = 0;
			System.out.println(saml.toString());
			GlobusCredential proxycred = c2.createProxy(saml, lifetime, delegation);
			CredentialManager.getInstance().addCredential(proxycred);
			System.out.println(proxycred.toString());
			ProxyUtil.saveProxyAsDefault(proxycred);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static boolean isContainerSecure(ServiceSecurityClient client) {
		return client.getEndpointReference().getAddress().getScheme().equalsIgnoreCase("https");
	}

	public static boolean isServiceSecure(CommunicationMechanism mechanism) {
		if (mechanism != null) {
			if (mechanism.getGSITransport()!= null) {
				return true;
			} else if (mechanism.getGSISecureConversation() != null) {
				return true;
			} else if (mechanism.getGSISecureMessage() != null) {
				return true;
			} // else if the transport is null, or if transport is none
		} // if there is no mechanism, then there is no protection level.
		return false;
	}

	/**
	 * get the user credential.  If null, then return default. if default is null, return null.
	 * @param client
	 * @return
	 * @throws GlobusCredentialException
	 */
	public static GlobusCredential getUserCredential(ServiceSecurityClient client) {
		GlobusCredential gcred = client.getProxy();

		if (gcred == null) {
			System.out.println("WARNING: Client does not have proxy credential.  Using Default");
			try {
				gcred = GlobusCredential.getDefaultCredential();
			} catch (GlobusCredentialException e) {
				// if this fails, then no default credential
				gcred = null;
			}
		}
		return gcred;
	}

	/**
	 * Get the user credential as string - if client does not have a proxy, then user default.
	 *
	 * if credential exists, then use it.  else use anonymous
	 * this has to match to the service's method of submitting credentials
	 */
	public static String getUserIdentity(ServiceSecurityClient client) {
		GlobusCredential gcred = ClientSecurityUtil.getUserCredential(client);
		if (gcred != null) {
			System.out.println("client proxy = {" + gcred.getIdentity() + "}");
			return gcred.getIdentity();
		} else {
			System.out.println("WARNING: no credential. Returning anonymous");
			return ANONYMOUS_USER;
		}
	}

	/**
	 * Get the user credential - if client does not have a proxy, then user default.
	 * @param mechanism
	 * @return
	 * @throws GSSException
	 */
	public static GSSCredential getUserGSSCredential(ServiceSecurityClient client) throws GSSException {
		GlobusCredential gcred = ClientSecurityUtil.getUserCredential(client);
		System.out.println("client proxy = {" + gcred.getIdentity() + "}");
		return new GlobusGSSCredentialImpl(gcred, GSSCredential.INITIATE_AND_ACCEPT);


	}


	/**
	 * get the default protection level
	 */
	public static ProtectionLevelType getProtectionLevel(CommunicationMechanism mechanism) {
		ProtectionLevelType level = null;
		if (mechanism != null) {
			if (mechanism.getGSITransport()!= null) {
				level = mechanism.getGSITransport().getProtectionLevel();
			} else if (mechanism.getGSISecureConversation() != null) {
				level = mechanism.getGSISecureConversation().getProtectionLevel();
			} else if (mechanism.getGSISecureMessage() != null) {
				level = mechanism.getGSISecureMessage().getProtectionLevel();
			} // else if the transport is null, or if transport is none
				// leave it alone.
		} // if there is no mechanism, then there is no protection level.
		return level;
	}

	public static boolean getEncryption(CommunicationMechanism mechanism) {
		ProtectionLevelType level = ClientSecurityUtil.getProtectionLevel(mechanism);
		if (level != null) {
			if ((level.equals(ProtectionLevelType.privacy))
					|| (level.equals(ProtectionLevelType.either))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean getSignature(CommunicationMechanism mechanism) {
		ProtectionLevelType level = ClientSecurityUtil.getProtectionLevel(mechanism);
		if (level != null) {
			if ((level.equals(ProtectionLevelType.integrity))
					|| (level.equals(ProtectionLevelType.either))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isAnonymousAllowed(CommunicationMechanism mechanism) {
		return mechanism.isAnonymousPermitted();
	}

}
