/*L
* Copyright The Ohio State University
* Copyright Emory University
*
* Distributed under the OSI-approved BSD 3-Clause License.
* See http://ncip.github.io/invivo-imaging-middleware/LICENSE.txt for details.
*/

package edu.emory.cci.sts.tokenprovider;

import java.security.Principal;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.picketlink.identity.federation.core.ErrorCodes;
import org.picketlink.identity.federation.core.exceptions.ProcessingException;
import org.picketlink.identity.federation.core.interfaces.ProtocolContext;
import org.picketlink.identity.federation.core.saml.v2.common.IDGenerator;
import org.picketlink.identity.federation.core.saml.v2.constants.JBossSAMLURIConstants;
import org.picketlink.identity.federation.core.saml.v2.factories.SAMLAssertionFactory;
import org.picketlink.identity.federation.core.saml.v2.util.StatementUtil;
import org.picketlink.identity.federation.core.wstrust.SecurityToken;
import org.picketlink.identity.federation.core.wstrust.StandardSecurityToken;
import org.picketlink.identity.federation.core.wstrust.WSTrustConstants;
import org.picketlink.identity.federation.core.wstrust.WSTrustRequestContext;
import org.picketlink.identity.federation.core.wstrust.WSTrustUtil;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAML20TokenAttributeProvider;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAML20TokenProvider;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAMLUtil;
import org.picketlink.identity.federation.core.wstrust.wrappers.Lifetime;
import org.picketlink.identity.federation.saml.v2.assertion.AssertionType;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeStatementType;
import org.picketlink.identity.federation.saml.v2.assertion.AudienceRestrictionType;
import org.picketlink.identity.federation.saml.v2.assertion.AuthnStatementType;
import org.picketlink.identity.federation.saml.v2.assertion.ConditionsType;
import org.picketlink.identity.federation.saml.v2.assertion.KeyInfoConfirmationDataType;
import org.picketlink.identity.federation.saml.v2.assertion.NameIDType;
import org.picketlink.identity.federation.saml.v2.assertion.StatementAbstractType;
import org.picketlink.identity.federation.saml.v2.assertion.SubjectConfirmationType;
import org.picketlink.identity.federation.saml.v2.assertion.SubjectType;
import org.picketlink.identity.federation.ws.policy.AppliesTo;
import org.picketlink.identity.federation.ws.trust.RequestedReferenceType;
import org.picketlink.identity.federation.ws.wss.secext.KeyIdentifierType;
import org.w3c.dom.Element;

public class XUACompliantSAML2TokenProvider extends SAML20TokenProvider{

	private SAML20TokenAttributeProvider attributeProvider;
	public void initialize(Map<String, String> properties)
	   {
	      super.initialize(properties);

	      // Check if an attribute provider has been set.
	      String attributeProviderClassName = this.properties.get(ATTRIBUTE_PROVIDER);
	      if (attributeProviderClassName == null)
	      {
	         if (logger.isDebugEnabled())
	            logger.debug("No attribute provider set");
	      }
	      else
	      {
	         try
	         {
	            Class<?> clazz = Class.forName(attributeProviderClassName);
	            Object object = clazz.newInstance();
	            if (object instanceof SAML20TokenAttributeProvider)
	            {
	               this.attributeProvider = (SAML20TokenAttributeProvider) object;
	               this.attributeProvider.setProperties(this.properties);
	               logger.info(clazz.getName() + " configured to be used as Attribute Provider");
	            }
	            else
	               logger.warn("Attribute provider not installed: " + attributeProviderClassName
	                     + "is not an instance of SAML20TokenAttributeProvider");
	         }
	         catch (Exception pae)
	         {
	            logger.warn("Error instantiating attribute provider: " + pae.getMessage());
	            pae.printStackTrace();
	         }
	      }
	   }

	 public void issueToken(ProtocolContext protoContext) throws ProcessingException
	   {
	      if (!(protoContext instanceof WSTrustRequestContext))
	         return;

	      WSTrustRequestContext context = (WSTrustRequestContext) protoContext;
	      // generate an id for the new assertion.
	      String assertionID = IDGenerator.create("ID_");

	      // lifetime and audience restrictions.
	      Lifetime lifetime = context.getRequestSecurityToken().getLifetime();
	      AudienceRestrictionType restriction = null;
	      AppliesTo appliesTo = context.getRequestSecurityToken().getAppliesTo();
	      if (appliesTo != null)
	         restriction = SAMLAssertionFactory.createAudienceRestriction(WSTrustUtil.parseAppliesTo(appliesTo));
	      ConditionsType conditions = SAMLAssertionFactory.createConditions(lifetime.getCreated(), lifetime.getExpires(),
	            restriction);

	      // the assertion principal (default is caller principal)
	      Principal principal = context.getCallerPrincipal();

	      String confirmationMethod = null;
	      KeyInfoConfirmationDataType keyInfoDataType = null;
	      // if there is a on-behalf-of principal, we have the sender vouches confirmation method.
	      if (context.getOnBehalfOfPrincipal() != null)
	      {
	         principal = context.getOnBehalfOfPrincipal();
	         confirmationMethod = SAMLUtil.SAML2_SENDER_VOUCHES_URI;
	      }
	      // if there is a proof-of-possession token in the context, we have the holder of key confirmation method.
	      else if (context.getProofTokenInfo() != null)
	      {
	         confirmationMethod = SAMLUtil.SAML2_HOLDER_OF_KEY_URI;
	         keyInfoDataType = SAMLAssertionFactory.createKeyInfoConfirmation(context.getProofTokenInfo());
	      }
	      else
	         confirmationMethod = SAMLUtil.SAML2_BEARER_URI;

	      SubjectConfirmationType subjectConfirmation = SAMLAssertionFactory.createSubjectConfirmation(null,
	            confirmationMethod, keyInfoDataType);

	      // create a subject using the caller principal or on-behalf-of principal.
	      String subjectName = principal == null ? "ANONYMOUS" : principal.getName();
	      NameIDType nameID = SAMLAssertionFactory.createNameID(null, "urn:picketlink:identity-federation", subjectName);
	      SubjectType subject = SAMLAssertionFactory.createSubject(nameID, subjectConfirmation);

	      // create the attribute statements if necessary.
	      List<StatementAbstractType> statements = new ArrayList<StatementAbstractType>();
	      Map<String, Object> claimedAttributes = context.getClaimedAttributes();
	      if (claimedAttributes != null)
	      {

	         statements.add(StatementUtil.createAttributeStatement(claimedAttributes));
	      }

	      // Add AuthnStatement

	      try {
	    	  GregorianCalendar gc = new GregorianCalendar();
	    	  DatatypeFactory dtf = DatatypeFactory.newInstance();
	    	  XMLGregorianCalendar xgc = dtf.newXMLGregorianCalendar(gc);
			AuthnStatementType authnStatement = StatementUtil.createAuthnStatement(xgc , JBossSAMLURIConstants.AC_PASSWORD.get());
			statements.add(authnStatement);

		} catch (DatatypeConfigurationException e1) {
			logger.warn("Could not add AuthnStatment to the SAML assertion");
		}

	      // create the SAML assertion.
	      NameIDType issuerID = SAMLAssertionFactory.createNameID(null, null, context.getTokenIssuer());
	      AssertionType assertion = SAMLAssertionFactory.createAssertion(assertionID, issuerID, lifetime.getCreated(),
	            conditions, subject, statements);

	      if (this.attributeProvider != null)
	      {
	         AttributeStatementType attributeStatement = this.attributeProvider.getAttributeStatement();
	         if (attributeStatement != null)
	         {
	            assertion.addStatement(attributeStatement);
	         }
	      }

	      // convert the constructed assertion to element.
	      Element assertionElement = null;
	      try
	      {
	         assertionElement = SAMLUtil.toElement(assertion);
	      }
	      catch (Exception e)
	      {
	         throw new ProcessingException(ErrorCodes.PROCESSING_EXCEPTION + "Failed to marshall SAMLV2 assertion", e);
	      }

	      SecurityToken token = new StandardSecurityToken(context.getRequestSecurityToken().getTokenType().toString(),
	            assertionElement, assertionID);
	      context.setSecurityToken(token);

	      // set the SAML assertion attached reference.
	      KeyIdentifierType keyIdentifier = WSTrustUtil.createKeyIdentifier(SAMLUtil.SAML2_VALUE_TYPE, "#" + assertionID);
	      Map<QName, String> attributes = new HashMap<QName, String>();
	      attributes.put(new QName(WSTrustConstants.WSSE11_NS, "TokenType", WSTrustConstants.WSSE.PREFIX_11),
	            SAMLUtil.SAML2_TOKEN_TYPE);
	      RequestedReferenceType attachedReference = WSTrustUtil.createRequestedReference(keyIdentifier, attributes);
	      context.setAttachedReference(attachedReference);
	   }
}
