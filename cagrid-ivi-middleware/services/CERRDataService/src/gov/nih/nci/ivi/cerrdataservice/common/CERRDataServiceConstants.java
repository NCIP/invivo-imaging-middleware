package gov.nih.nci.ivi.cerrdataservice.common;

import javax.xml.namespace.QName;


public interface CERRDataServiceConstants {
	public static final String SERVICE_NS = "http://cerrdataservice.ivi.nci.nih.gov/CERRDataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "CERRDataServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "CERRDataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
