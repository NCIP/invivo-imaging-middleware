package edu.emory.cci.aim.common;

import javax.xml.namespace.QName;


public interface AIMv1DataServiceConstants {
	public static final String SERVICE_NS = "http://aim.cci.emory.edu/AIMv1DataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "AIMv1DataServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "AIMv1DataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
