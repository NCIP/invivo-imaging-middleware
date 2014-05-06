package gov.nih.nci.ivi.pathologydataservice.common;

import javax.xml.namespace.QName;


public interface PathologyDataServiceConstants {
	public static final String SERVICE_NS = "http://pathologydataservice.ivi.nci.nih.gov/PathologyDataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "PathologyDataServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "PathologyDataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
}
