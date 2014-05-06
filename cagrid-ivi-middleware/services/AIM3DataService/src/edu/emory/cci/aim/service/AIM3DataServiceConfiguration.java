package edu.emory.cci.aim.service;

import gov.nih.nci.cagrid.introduce.servicetools.ServiceConfiguration;

import org.globus.wsrf.config.ContainerConfig;
import java.io.File;
import javax.naming.InitialContext;

import org.apache.axis.MessageContext;
import org.globus.wsrf.Constants;


/** 
 * DO NOT EDIT:  This class is autogenerated!
 * 
 * This class holds all service properties which were defined for the service to have
 * access to.
 * 
 * @created by Introduce Toolkit version 1.3
 * 
 */
public class AIM3DataServiceConfiguration implements ServiceConfiguration {

	public static AIM3DataServiceConfiguration  configuration = null;
    public String etcDirectoryPath;
    	
	public static AIM3DataServiceConfiguration getConfiguration() throws Exception {
		if (AIM3DataServiceConfiguration.configuration != null) {
			return AIM3DataServiceConfiguration.configuration;
		}
		MessageContext ctx = MessageContext.getCurrentContext();

		String servicePath = ctx.getTargetService();

		String jndiName = Constants.JNDI_SERVICES_BASE_NAME + servicePath + "/serviceconfiguration";
		try {
			javax.naming.Context initialContext = new InitialContext();
			AIM3DataServiceConfiguration.configuration = (AIM3DataServiceConfiguration) initialContext.lookup(jndiName);
		} catch (Exception e) {
			throw new Exception("Unable to instantiate service configuration.", e);
		}

		return AIM3DataServiceConfiguration.configuration;
	}
	

	
	private String cqlQueryProcessorConfig_xmlDomainModel;
	
	private String cqlQueryProcessorConfig_classToQname;
	
	private String cqlQueryProcessorConfig_xmlNamespaceResolverClass;
	
	private String queryProcessorClass;
	
	private String cqlQueryProcessorConfig_xmlCollectionName;
	
	private String cqlQueryProcessorConfig_xmldbURI;
	
	private String cqlQueryProcessorConfig_xmldbPath;
	
	private String cqlQueryProcessorConfig_xmldbAttachmentPath;
	
	private String cqlQueryProcessorConfig_xmldbConnectorClass;
	
	private String cqlQueryProcessorConfig_xmldbConfigFile;
	
	private String serverConfigLocation;
	
	private String dataService_cqlValidatorClass;
	
	private String dataService_domainModelValidatorClass;
	
	private String dataService_validateCqlFlag;
	
	private String dataService_validateDomainModelFlag;
	
	private String dataService_classMappingsFilename;
	
	private String caGridWsEnumeration_iterImplType;
	
	private String cqlQueryProcessorConfig_dataSecurityEnabled;
	
	private String cqlQueryProcessorConfig_dataSecurityGrouperRootStem;
	
	private String cqlQueryProcessorConfig_CQLPreprocessor_Classname;
	
	private String cqlQueryProcessorConfig_dataSecurityInstanceUIDXPath;
	
	private String cqlQueryProcessorConfig_dataSecurityLocalGrouper;
	
	private String cqlQueryProcessorConfig_submitPreprocessorClass;
	
	
    public String getEtcDirectoryPath() {
		return ContainerConfig.getBaseDirectory() + File.separator + etcDirectoryPath;
	}
	
	public void setEtcDirectoryPath(String etcDirectoryPath) {
		this.etcDirectoryPath = etcDirectoryPath;
	}


	
	public String getCqlQueryProcessorConfig_xmlDomainModel() {
		return ContainerConfig.getBaseDirectory() + File.separator + cqlQueryProcessorConfig_xmlDomainModel;
	}
	
	
	public void setCqlQueryProcessorConfig_xmlDomainModel(String cqlQueryProcessorConfig_xmlDomainModel) {
		this.cqlQueryProcessorConfig_xmlDomainModel = cqlQueryProcessorConfig_xmlDomainModel;
	}

	
	public String getCqlQueryProcessorConfig_classToQname() {
		return ContainerConfig.getBaseDirectory() + File.separator + cqlQueryProcessorConfig_classToQname;
	}
	
	
	public void setCqlQueryProcessorConfig_classToQname(String cqlQueryProcessorConfig_classToQname) {
		this.cqlQueryProcessorConfig_classToQname = cqlQueryProcessorConfig_classToQname;
	}

	
	public String getCqlQueryProcessorConfig_xmlNamespaceResolverClass() {
		return cqlQueryProcessorConfig_xmlNamespaceResolverClass;
	}
	
	
	public void setCqlQueryProcessorConfig_xmlNamespaceResolverClass(String cqlQueryProcessorConfig_xmlNamespaceResolverClass) {
		this.cqlQueryProcessorConfig_xmlNamespaceResolverClass = cqlQueryProcessorConfig_xmlNamespaceResolverClass;
	}

	
	public String getQueryProcessorClass() {
		return queryProcessorClass;
	}
	
	
	public void setQueryProcessorClass(String queryProcessorClass) {
		this.queryProcessorClass = queryProcessorClass;
	}

	
	public String getCqlQueryProcessorConfig_xmlCollectionName() {
		return cqlQueryProcessorConfig_xmlCollectionName;
	}
	
	
	public void setCqlQueryProcessorConfig_xmlCollectionName(String cqlQueryProcessorConfig_xmlCollectionName) {
		this.cqlQueryProcessorConfig_xmlCollectionName = cqlQueryProcessorConfig_xmlCollectionName;
	}

	
	public String getCqlQueryProcessorConfig_xmldbURI() {
		return cqlQueryProcessorConfig_xmldbURI;
	}
	
	
	public void setCqlQueryProcessorConfig_xmldbURI(String cqlQueryProcessorConfig_xmldbURI) {
		this.cqlQueryProcessorConfig_xmldbURI = cqlQueryProcessorConfig_xmldbURI;
	}

	
	public String getCqlQueryProcessorConfig_xmldbPath() {
		return cqlQueryProcessorConfig_xmldbPath;
	}
	
	
	public void setCqlQueryProcessorConfig_xmldbPath(String cqlQueryProcessorConfig_xmldbPath) {
		this.cqlQueryProcessorConfig_xmldbPath = cqlQueryProcessorConfig_xmldbPath;
	}

	
	public String getCqlQueryProcessorConfig_xmldbAttachmentPath() {
		return cqlQueryProcessorConfig_xmldbAttachmentPath;
	}
	
	
	public void setCqlQueryProcessorConfig_xmldbAttachmentPath(String cqlQueryProcessorConfig_xmldbAttachmentPath) {
		this.cqlQueryProcessorConfig_xmldbAttachmentPath = cqlQueryProcessorConfig_xmldbAttachmentPath;
	}

	
	public String getCqlQueryProcessorConfig_xmldbConnectorClass() {
		return cqlQueryProcessorConfig_xmldbConnectorClass;
	}
	
	
	public void setCqlQueryProcessorConfig_xmldbConnectorClass(String cqlQueryProcessorConfig_xmldbConnectorClass) {
		this.cqlQueryProcessorConfig_xmldbConnectorClass = cqlQueryProcessorConfig_xmldbConnectorClass;
	}

	
	public String getCqlQueryProcessorConfig_xmldbConfigFile() {
		return ContainerConfig.getBaseDirectory() + File.separator + cqlQueryProcessorConfig_xmldbConfigFile;
	}
	
	
	public void setCqlQueryProcessorConfig_xmldbConfigFile(String cqlQueryProcessorConfig_xmldbConfigFile) {
		this.cqlQueryProcessorConfig_xmldbConfigFile = cqlQueryProcessorConfig_xmldbConfigFile;
	}

	
	public String getServerConfigLocation() {
		return ContainerConfig.getBaseDirectory() + File.separator + serverConfigLocation;
	}
	
	
	public void setServerConfigLocation(String serverConfigLocation) {
		this.serverConfigLocation = serverConfigLocation;
	}

	
	public String getDataService_cqlValidatorClass() {
		return dataService_cqlValidatorClass;
	}
	
	
	public void setDataService_cqlValidatorClass(String dataService_cqlValidatorClass) {
		this.dataService_cqlValidatorClass = dataService_cqlValidatorClass;
	}

	
	public String getDataService_domainModelValidatorClass() {
		return dataService_domainModelValidatorClass;
	}
	
	
	public void setDataService_domainModelValidatorClass(String dataService_domainModelValidatorClass) {
		this.dataService_domainModelValidatorClass = dataService_domainModelValidatorClass;
	}

	
	public String getDataService_validateCqlFlag() {
		return dataService_validateCqlFlag;
	}
	
	
	public void setDataService_validateCqlFlag(String dataService_validateCqlFlag) {
		this.dataService_validateCqlFlag = dataService_validateCqlFlag;
	}

	
	public String getDataService_validateDomainModelFlag() {
		return dataService_validateDomainModelFlag;
	}
	
	
	public void setDataService_validateDomainModelFlag(String dataService_validateDomainModelFlag) {
		this.dataService_validateDomainModelFlag = dataService_validateDomainModelFlag;
	}

	
	public String getDataService_classMappingsFilename() {
		return ContainerConfig.getBaseDirectory() + File.separator + dataService_classMappingsFilename;
	}
	
	
	public void setDataService_classMappingsFilename(String dataService_classMappingsFilename) {
		this.dataService_classMappingsFilename = dataService_classMappingsFilename;
	}

	
	public String getCaGridWsEnumeration_iterImplType() {
		return caGridWsEnumeration_iterImplType;
	}
	
	
	public void setCaGridWsEnumeration_iterImplType(String caGridWsEnumeration_iterImplType) {
		this.caGridWsEnumeration_iterImplType = caGridWsEnumeration_iterImplType;
	}

	
	public String getCqlQueryProcessorConfig_dataSecurityEnabled() {
		return cqlQueryProcessorConfig_dataSecurityEnabled;
	}
	
	
	public void setCqlQueryProcessorConfig_dataSecurityEnabled(String cqlQueryProcessorConfig_dataSecurityEnabled) {
		this.cqlQueryProcessorConfig_dataSecurityEnabled = cqlQueryProcessorConfig_dataSecurityEnabled;
	}

	
	public String getCqlQueryProcessorConfig_dataSecurityGrouperRootStem() {
		return cqlQueryProcessorConfig_dataSecurityGrouperRootStem;
	}
	
	
	public void setCqlQueryProcessorConfig_dataSecurityGrouperRootStem(String cqlQueryProcessorConfig_dataSecurityGrouperRootStem) {
		this.cqlQueryProcessorConfig_dataSecurityGrouperRootStem = cqlQueryProcessorConfig_dataSecurityGrouperRootStem;
	}

	
	public String getCqlQueryProcessorConfig_CQLPreprocessor_Classname() {
		return cqlQueryProcessorConfig_CQLPreprocessor_Classname;
	}
	
	
	public void setCqlQueryProcessorConfig_CQLPreprocessor_Classname(String cqlQueryProcessorConfig_CQLPreprocessor_Classname) {
		this.cqlQueryProcessorConfig_CQLPreprocessor_Classname = cqlQueryProcessorConfig_CQLPreprocessor_Classname;
	}

	
	public String getCqlQueryProcessorConfig_dataSecurityInstanceUIDXPath() {
		return cqlQueryProcessorConfig_dataSecurityInstanceUIDXPath;
	}
	
	
	public void setCqlQueryProcessorConfig_dataSecurityInstanceUIDXPath(String cqlQueryProcessorConfig_dataSecurityInstanceUIDXPath) {
		this.cqlQueryProcessorConfig_dataSecurityInstanceUIDXPath = cqlQueryProcessorConfig_dataSecurityInstanceUIDXPath;
	}

	
	public String getCqlQueryProcessorConfig_dataSecurityLocalGrouper() {
		return cqlQueryProcessorConfig_dataSecurityLocalGrouper;
	}
	
	
	public void setCqlQueryProcessorConfig_dataSecurityLocalGrouper(String cqlQueryProcessorConfig_dataSecurityLocalGrouper) {
		this.cqlQueryProcessorConfig_dataSecurityLocalGrouper = cqlQueryProcessorConfig_dataSecurityLocalGrouper;
	}

	
	public String getCqlQueryProcessorConfig_submitPreprocessorClass() {
		return cqlQueryProcessorConfig_submitPreprocessorClass;
	}
	
	
	public void setCqlQueryProcessorConfig_submitPreprocessorClass(String cqlQueryProcessorConfig_submitPreprocessorClass) {
		this.cqlQueryProcessorConfig_submitPreprocessorClass = cqlQueryProcessorConfig_submitPreprocessorClass;
	}

	
}