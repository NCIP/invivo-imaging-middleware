package edu.emory.cci.sts.attributeprovider;


import edu.internet2.middleware.grouper.GrouperRuntimeException;
import edu.internet2.middleware.grouper.InsufficientPrivilegeException;
import gov.nih.nci.cagrid.gridgrouper.client.GridGrouper;
import gov.nih.nci.cagrid.gridgrouper.grouper.GroupI;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.security.auth.Subject;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.globus.gsi.GlobusCredential;
import org.jboss.security.SecurityContextAssociation;
import org.picketlink.identity.federation.core.util.Base64;
import org.picketlink.identity.federation.core.wstrust.plugins.saml.SAML20TokenAttributeProvider;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeStatementType;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeStatementType.ASTChoiceType;
import org.picketlink.identity.federation.saml.v2.assertion.AttributeType;
import edu.emory.cci.sts.loginmodule.*;

public class GridGrouperSAMLAttributeProvider implements SAML20TokenAttributeProvider {

	private Log log = LogFactory.getLog(getClass());
	private GridGrouper grouper;
	private static String GRID_GROUPER_PROPERTY_NAME="gridGrouperUrl";
	private static String GROUP_MEMBERSHIP_PROPERTY_NAME="groupMembership";
	

	
	private AttributeType getGlobusCredentialSAMLAttribute(GlobusCredential credential) throws IOException
	{
		AttributeType credentialAttr = new AttributeType("GlobusCredential");
	
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		credential.save(os);
		os.close();
		byte[] rawBytes = os.toByteArray();
		String base64encoded = Base64.encodeBytes(rawBytes);
		credentialAttr.addAttributeValue(base64encoded);
		return credentialAttr;
	}
	
	


	public AttributeStatementType createErrorAttributeStatement(String message)
	{
		AttributeStatementType attributeStatement = new AttributeStatementType();
        AttributeType errorAttribute = new AttributeType("error");
        errorAttribute.addAttributeValue(message);
        attributeStatement.addAttribute(new ASTChoiceType(errorAttribute));
        return attributeStatement;
	}

	public  List<String> getGroupMembershipInfo(GlobusCredential credential ) throws GrouperRuntimeException, InsufficientPrivilegeException
	{
		List<String> list = new ArrayList<String>();
		Set<GroupI> set = grouper.getMembersGroups(credential.getIdentity());
		for(GroupI group : set)
		{
			list.add(group.getName().replaceAll(":", "/"));
		}
		
		return list;
	}





	public AttributeStatementType getAttributeStatement() {
		log.info("Injecting Attributes in SAML assertion from GridGrouper");
		Subject subject = SecurityContextAssociation.getSecurityContext().getSubjectInfo().getAuthenticatedSubject();
	      if( subject == null )
	      {
	             log.warn("No authentication Subject found, cannot provide any group information");
	             return createErrorAttributeStatement("Error retrieving group information");
	      }
	      else
	      {
	          AttributeStatementType attributeStatement = new AttributeStatementType();
	          
	          AttributeType targetGrid = new AttributeType("targetGrid");
	          
	          
	          
	          AttributeType gridGrp = new AttributeType("gridGrouper");
	 
	          gridGrp.addAttributeValue(grouper == null ? "not set" : grouper.getName());
	          
	          
	          
	          
	          AttributeType groupMembershipAttribute = new AttributeType(GROUP_MEMBERSHIP_PROPERTY_NAME);
	          
	         
	          attributeStatement.addAttribute(new ASTChoiceType(groupMembershipAttribute));
	          attributeStatement.addAttribute(new ASTChoiceType(targetGrid));
	          attributeStatement.addAttribute(new ASTChoiceType(gridGrp));
	          
	         
	          
	          try {
	        	  
	        	  Principal identity = subject.getPrincipals().iterator().next();
	        		        	
	        	  targetGrid.addAttributeValue(((PrincipalWithAttributes) identity).getAttribute("targetGrid"));
	        	  GlobusCredential credential =  ((PrincipalWithAttributes) identity).getAttribute("globusCredential");
		          List<String> groupMembers = getGroupMembershipInfo(credential) ;
				
				for(String groupMember : groupMembers)
				{
					groupMembershipAttribute.addAttributeValue(groupMember);
				}
				
				attributeStatement.addAttribute( new ASTChoiceType(this.getGlobusCredentialSAMLAttribute(credential)));
				
			} catch (GrouperRuntimeException e) {
				log.error(e);
				return createErrorAttributeStatement("Error retrieving group information");
			} catch (InsufficientPrivilegeException e) {
				log.error(e);
				return createErrorAttributeStatement("Error retrieving group information");
			} catch (MalformedURIException e) {
				log.error(e);
				return createErrorAttributeStatement("Error retrieving group information");
			} catch (RemoteException e) {
				log.error(e);
				return createErrorAttributeStatement("Error retrieving group information");
			}catch( Exception e)
			{
				log.error(e);
				e.printStackTrace();
				return createErrorAttributeStatement("Error retrieving group information");
			}finally{
				
			}
	     
	          return attributeStatement;
	      }
	}




	public void setProperties(Map<String, String> properties) {
		String gridGrouperUrl = properties.get(GRID_GROUPER_PROPERTY_NAME);
		if(gridGrouperUrl!=null)
		{
			grouper = new GridGrouper(gridGrouperUrl);
		}
		
				
	}

}
