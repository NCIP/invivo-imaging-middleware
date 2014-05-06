package edu.emory.cci.aim.common;

import javax.xml.namespace.QName;


public interface AIMTCGADataServiceConstants {
	public static final String SERVICE_NS = "http://aim.cci.emory.edu/AIMTCGADataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "AIMTCGADataServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "AIMTCGADataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
