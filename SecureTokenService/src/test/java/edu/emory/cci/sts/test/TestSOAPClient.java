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

public class TestSOAPClient  {

	// Testcase to check the issue token request

	public String stsUrl = "http://localhost:8080/SecurityTokenServicePF/PicketlinkSTS";
	public String requesterUsername = "UserA";
	public String requesterPassword = "PassA";
	public String validatorUsername = "UserB";
	public String validatorPassword = "PassB";

	@Test
	public void testIssue() {

		// Specify username and password for the training account


		try {
			// create a WSTrustClient instance.
			WSTrustClient client = new WSTrustClient(
					"PicketLinkSTS",
					"PicketLinkSTSPort",
					stsUrl,
					new SecurityInfo(requesterUsername, requesterPassword));

			// issue a SAML assertion using the client API.
			Element assertion = null;
			System.out
					.println("\nInvoking token service to get SAML assertion for "
							+ requesterUsername);

			// specify the type of token you want to use. In this case its SAML2
			assertion = client.issueTokenForEndpoint("http://services.testcorp.org/provider1");
			System.out.println("SAML assertion for " + requesterUsername
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


		// create a WSTrustClient instance for the client
		WSTrustClient client = new WSTrustClient(
				"PicketLinkSTS",
				"PicketLinkSTSPort",
				stsUrl,
				new SecurityInfo(requesterUsername, requesterPassword));

		// create a WSTrustClient instance for the service that wants to
		// validate the token
		WSTrustClient service = new WSTrustClient(
				"PicketLinkSTS",
				"PicketLinkSTSPort",
				stsUrl,
				new SecurityInfo(validatorUsername,validatorPassword));

		// issue a SAML assertion using the client API.
		Element assertion = null;
		try {
			System.out
					.println("\nInvoking token service to get SAML assertion for "
							+ requesterUsername);
			assertion = client.issueTokenForEndpoint("http://services.testcorp.org/provider1");
			System.out.println("SAML assertion for " + requesterUsername
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
