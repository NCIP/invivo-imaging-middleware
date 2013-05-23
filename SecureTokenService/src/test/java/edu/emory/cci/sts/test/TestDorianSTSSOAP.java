/*L
* Copyright The Ohio State University
* Copyright Emory University
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.io/invivo-imaging-middleware/LICENSE.txt for details.
*/

package edu.emory.cci.sts.test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient;
import org.picketlink.identity.federation.api.wstrust.WSTrustClient.SecurityInfo;
import org.picketlink.identity.federation.core.wstrust.SamlCredential;
import org.picketlink.identity.federation.core.wstrust.WSTrustException;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAMLUtil;
import org.w3c.dom.Element;

public class TestDorianSTSSOAP {
	// Testcase to check the issue token request

	@Test
	public void testIssue() {

		// Specify username and password for the training account
		String username = "nadir";
		String password = "<yourpassword>";

		try {
			// create a WSTrustClient instance.
			WSTrustClient client = new WSTrustClient(
					"PicketLinkSTS",
					"PicketLinkSTSPort",
					"http://localhost:8080/SecurityTokenServiceDorian/PicketlinkSTS",
					new SecurityInfo(username, password));

			// issue a SAML assertion using the client API.
			Element assertion = null;
			System.out
					.println("\nInvoking token service to get SAML assertion for "
							+ username);

			// specify the type of token you want to use. In this case its SAML2
			assertion = client.issueTokenForEndpoint("http://services.testcorp.org/provider1");
			System.out.println("SAML assertion for " + username
					+ " successfully obtained!");
			SamlCredential credential = new SamlCredential(assertion);
			System.out.println("Token Issued : " + credential);
			assertNotNull(credential);




		} catch (WSTrustException wse) {
			System.out
					.println("Unable to issue assertion: " + wse.getMessage());
			fail(wse.toString());
		} catch (Exception e) {
			fail(e.toString());
		}

	}
@Test
public void testIssueValidate() throws Exception {
		String username = "nadir";
		String password = "<yourpassword>";

		String serviceUsername = "nadir";
		String servicePassword = "<yourpassword>";

		// create a WSTrustClient instance for the client
		WSTrustClient client = new WSTrustClient(
				"PicketLinkSTS",
				"PicketLinkSTSPort",
				"http://localhost:8080/SecurityTokenServiceDorian/PicketlinkSTS",
				new SecurityInfo(username, password));

		// create a WSTrustClient instance for the service that wants to
		// validate the token
		WSTrustClient service = new WSTrustClient(
				"PicketLinkSTS",
				"PicketLinkSTSPort",
				"http://localhost:8080/SecurityTokenServiceDorian/PicketlinkSTS",
				new SecurityInfo(serviceUsername, servicePassword));

		// issue a SAML assertion using the client API.
		Element assertion = null;
		try {
			System.out
					.println("\nInvoking token service to get SAML assertion for "
							+ username);
			assertion = client.issueToken(SAMLUtil.SAML2_TOKEN_TYPE);
			System.out.println("SAML assertion for " + username
					+ " successfully obtained!");
		} catch (WSTrustException wse) {
			System.out
					.println("Unable to issue assertion: " + wse.getMessage());
			fail(wse.toString());
		}

		SamlCredential credential = new SamlCredential(assertion);
		System.out.println(credential);

		// validate the token using the service side WSClient api
		boolean status = service.validateToken(assertion);
		assertTrue(status);
		System.out.println("Server Validates "
				+ status);


	}

}
