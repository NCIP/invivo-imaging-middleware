package gov.nih.nci.ivi.imagedataservice.common;

import javax.xml.namespace.QName;


public interface ImageDataServiceConstants {
	public static final String SERVICE_NS = "http://imagedataservice.ivi.nci.nih.gov/ImageDataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "ImageDataServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "ImageDataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
}
