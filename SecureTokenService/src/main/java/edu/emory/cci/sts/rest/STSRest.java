package edu.emory.cci.sts.rest;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.ws.WebServiceException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.picketlink.identity.federation.core.ErrorCodes;
import org.picketlink.identity.federation.core.config.STSType;
import org.picketlink.identity.federation.core.exceptions.ConfigurationException;
import org.picketlink.identity.federation.core.parsers.sts.STSConfigParser;
import org.picketlink.identity.federation.core.saml.v2.util.DocumentUtil;
import org.picketlink.identity.federation.core.wstrust.PicketLinkSTS;
import org.picketlink.identity.federation.core.wstrust.PicketLinkSTSConfiguration;
import org.picketlink.identity.federation.core.wstrust.STSConfiguration;
import org.picketlink.identity.federation.core.wstrust.SamlCredential;
import org.picketlink.identity.federation.core.wstrust.WSTrustConstants;
import org.picketlink.identity.federation.core.wstrust.WSTrustException;
import org.picketlink.identity.federation.core.wstrust.WSTrustRequestHandler;
import org.picketlink.identity.federation.core.wstrust.WSTrustUtil;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityToken;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityTokenResponse;
import org.picketlink.identity.federation.core.wstrust.wrappers.RequestSecurityTokenResponseCollection;
import org.picketlink.identity.federation.core.wstrust.writers.WSTrustRequestWriter;
import org.picketlink.identity.federation.core.wstrust.writers.WSTrustResponseWriter;
import org.picketlink.identity.federation.ws.trust.StatusType;
import org.picketlink.identity.federation.ws.trust.ValidateTargetType;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

@Path("/STS")
public class STSRest {

	private Log log = LogFactory.getLog(getClass());

	@Context
	private SecurityContext securityContext;
	private static final String SEPARATOR = AccessController.doPrivileged(new PrivilegedAction<String>()
			   {
			      public String run()
			      {
			         return System.getProperty("file.separator");
			      }
			   });
	
	private static final String STS_CONFIG_FILE = "picketlink-sts.xml";

	private static final String STS_CONFIG_DIR = "picketlink-store" + SEPARATOR + "sts" + SEPARATOR;


	  protected STSConfiguration config;

	@GET
	public String info() {
		return "RESTful API for the Security Token Service";
	}

	@GET
	@Path("issueToken")
	@Produces("text/xml")
	public Response issueToken(
			@QueryParam("targetService") String targetServiceEndpoint,
			@QueryParam("tokenType") String tokenType) {
		RequestSecurityToken requestSecurityToken = new RequestSecurityToken();PicketLinkSTS s;
		if (targetServiceEndpoint != null)
			requestSecurityToken.setAppliesTo(WSTrustUtil
					.createAppliesTo(targetServiceEndpoint));

		else if (tokenType != null)
			requestSecurityToken.setTokenType(URI.create(tokenType));

		else {
			return createErrorResponse("<error>Either targetService or tokenType must be specified</error>");
		}

		requestSecurityToken.setRequestType(URI
				.create(WSTrustConstants.ISSUE_REQUEST));
		requestSecurityToken.setContext("default-context");

		Source source = handleTokenRequest(requestSecurityToken);
		try {
			Document responseDoc = (Document) DocumentUtil
					.getNodeFromSource(source);
			NodeList nodes = responseDoc.getElementsByTagNameNS(
					WSTrustConstants.BASE_NAMESPACE, "RequestedSecurityToken");
			if (nodes == null || nodes.getLength() == 0)
				nodes = responseDoc
						.getElementsByTagName("RequestedSecurityToken");
			if (nodes == null)
				throw new WSTrustException(ErrorCodes.NULL_VALUE + "NodeList");

			Node rstr = nodes.item(0);
			if (rstr == null)
				throw new WSTrustException(ErrorCodes.NULL_VALUE
						+ "RSTR in the payload");

			Element token = (Element) rstr.getFirstChild();

			Document theResponseDoc = DocumentUtil.getDocument(DocumentUtil
					.getDOMElementAsString(token));
			theResponseDoc.importNode(token, true);
			return Response.ok().entity(theResponseDoc).build();
		} catch (Exception e) {
			return createErrorResponse("<error>" + e + "</error>");
		}

	}

	@POST
	@Path("validateToken")
	public Response validateToken(@FormParam("token") String token) {
		RequestSecurityToken requestSecurityToken = new RequestSecurityToken();

		requestSecurityToken.setRequestType(URI
				.create(WSTrustConstants.VALIDATE_REQUEST));
		requestSecurityToken.setContext("default-context");
		try {

			SamlCredential cred = new SamlCredential(token);
			requestSecurityToken.setTokenType(URI
					.create(WSTrustConstants.STATUS_TYPE));
			ValidateTargetType validateTarget = new ValidateTargetType();
			validateTarget.add(cred.getAssertionAsElement());
			requestSecurityToken.setValidateTarget(validateTarget);

			if (this.config == null)
				try {
					if (log.isInfoEnabled())
						log.info("Loading STS configuration");
					this.config = this.getConfiguration();
				} catch (ConfigurationException e) {
					throw new WebServiceException(
							ErrorCodes.STS_CONFIGURATION_EXCEPTION, e);
				}

			WSTrustRequestHandler handler = this.config.getRequestHandler();

			DOMResult result = new DOMResult(DocumentUtil.createDocument());
			WSTrustRequestWriter writer = new WSTrustRequestWriter(result);
			writer.write(requestSecurityToken);

			requestSecurityToken.setRSTDocument((Document) result.getNode());
			RequestSecurityTokenResponse response = handler.validate(
					requestSecurityToken, securityContext.getUserPrincipal());

			StatusType status = response.getStatus();
			return createSuccessResponse(status.getCode());
		} catch (Exception e) {
			return createErrorResponse("<error>" + e + "</error>");
		}

	}

	@POST
	@Path("renew")
	public Response renewToken() {
		return null;
	}

	public Response createSuccessResponse(String content) {
		return Response.ok().entity(content).build();
	}

	public Response createErrorResponse(String message) {
		log.error(message);
		return Response.serverError().entity(message).build();
	}

	protected Source handleTokenRequest(RequestSecurityToken request) {
		if (this.config == null)
			try {
				if (log.isInfoEnabled())
					log.info("Loading STS configuration");
				this.config = this.getConfiguration();
			} catch (ConfigurationException e) {
				throw new WebServiceException(
						ErrorCodes.STS_CONFIGURATION_EXCEPTION, e);
			}

		WSTrustRequestHandler handler = this.config.getRequestHandler();
		String requestType = request.getRequestType().toString();
		if (log.isDebugEnabled())
			log.debug("STS received request of type " + requestType);

		try {
			if (requestType.equals(WSTrustConstants.ISSUE_REQUEST)) {
				Source source = this.marshallResponse(handler.issue(request,
						securityContext.getUserPrincipal()));
				Document doc = handler.postProcess(
						(Document) ((DOMSource) source).getNode(), request);
				return new DOMSource(doc);
			} else if (requestType.equals(WSTrustConstants.RENEW_REQUEST)) {
				Source source = this.marshallResponse(handler.renew(request,
						securityContext.getUserPrincipal()));
				// we need to sign/encrypt renewed tokens.
				Document document = handler.postProcess(
						(Document) ((DOMSource) source).getNode(), request);
				return new DOMSource(document);
			} else if (requestType.equals(WSTrustConstants.CANCEL_REQUEST))
				return this.marshallResponse(handler.cancel(request,
						securityContext.getUserPrincipal()));
			else if (requestType.equals(WSTrustConstants.VALIDATE_REQUEST))
				return this.marshallResponse(handler.validate(request,
						securityContext.getUserPrincipal()));
			else
				throw new WSTrustException(ErrorCodes.STS_INVALID_REQUEST_TYPE
						+ requestType);
		} catch (WSTrustException we) {
			throw new WebServiceException(
					ErrorCodes.STS_EXCEPTION_HANDLING_TOKEN_REQ
							+ we.getMessage(), we);
		}
	}
	protected Source marshallResponse(RequestSecurityTokenResponse response)
	   {
	      // add the single response to a RequestSecurityTokenResponse collection, as per the specification.
	      RequestSecurityTokenResponseCollection responseCollection = new RequestSecurityTokenResponseCollection();
	      responseCollection.addRequestSecurityTokenResponse(response);

	      try
	      {
	         DOMResult result = new DOMResult(DocumentUtil.createDocument());
	         WSTrustResponseWriter writer = new WSTrustResponseWriter(result);
	         writer.write(responseCollection);
	         return new DOMSource(result.getNode());
	      }
	      catch (Exception e)
	      {
	         throw new WebServiceException(ErrorCodes.STS_RESPONSE_WRITING_ERROR + e.getMessage(), e);
	      }
	   }
	 protected STSConfiguration getConfiguration() throws ConfigurationException
	   {
	      URL configurationFileURL = null;

	      try
	      {
	         // check the user home for a configuration file generated by the picketlink console.
	         String configurationFilePath = System.getProperty("user.home") + SEPARATOR + STS_CONFIG_DIR + STS_CONFIG_FILE;
	         File configurationFile = new File(configurationFilePath);
	         if (configurationFile.exists())
	            configurationFileURL = configurationFile.toURI().toURL();
	         else
	            // if not configuration file was found in the user home, check the context classloader.
	            configurationFileURL = SecurityActions.loadResource(getClass(), STS_CONFIG_FILE);

	         // if no configuration file was found, log a warn message and use default configuration values.
	         if (configurationFileURL == null)
	         {
	            log.warn(STS_CONFIG_FILE + " configuration file not found. Using default configuration values");
	            return new PicketLinkSTSConfiguration();
	         }

	         InputStream stream = configurationFileURL.openStream();
	         STSType stsConfig = (STSType) new STSConfigParser().parse(stream);
	         STSConfiguration configuration = new PicketLinkSTSConfiguration(stsConfig);
	         if (log.isInfoEnabled())
	            log.info(STS_CONFIG_FILE + " configuration file loaded");
	         return configuration;
	      }
	      catch (Exception e)
	      {
	         throw new ConfigurationException(ErrorCodes.STS_CONFIGURATION_FILE_PARSING_ERROR + configurationFileURL + "]",
	               e);
	      }
	   }
	 
	 
	 private static class SecurityActions
	 {
		   static Class<?> loadClass(final Class<?> theClass, final String fqn)
		   {
		      return AccessController.doPrivileged(new PrivilegedAction<Class<?>>()
		      {
		         public Class<?> run()
		         {
		            ClassLoader classLoader = theClass.getClassLoader();

		            Class<?> clazz = loadClass(classLoader, fqn);
		            if (clazz == null)
		            {
		               classLoader = Thread.currentThread().getContextClassLoader();
		               clazz = loadClass(classLoader, fqn);
		            }
		            return clazz;
		         }
		      });
		   }

		   static Class<?> loadClass(final ClassLoader cl, final String fqn)
		   {
		      return AccessController.doPrivileged(new PrivilegedAction<Class<?>>()
		      {
		         public Class<?> run()
		         {
		            try
		            {
		               return cl.loadClass(fqn);
		            }
		            catch (ClassNotFoundException e)
		            {
		            }
		            return null;
		         }
		      });
		   }

		   /**
		    * Load a resource based on the passed {@link Class} classloader.
		    * Failing which try with the Thread Context CL
		    * @param clazz
		    * @param resourceName
		    * @return
		    */
		   static URL loadResource(final Class<?> clazz, final String resourceName)
		   {
		      return AccessController.doPrivileged(new PrivilegedAction<URL>()
		      {
		         public URL run()
		         {
		            URL url = null;
		            ClassLoader clazzLoader = clazz.getClassLoader();
		            url = clazzLoader.getResource(resourceName);

		            if (url == null)
		            {
		               clazzLoader = Thread.currentThread().getContextClassLoader();
		               url = clazzLoader.getResource(resourceName);
		            }

		            return url;
		         }
		      });
		   }
		}
}
