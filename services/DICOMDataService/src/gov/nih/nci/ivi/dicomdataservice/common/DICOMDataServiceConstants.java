package gov.nih.nci.ivi.dicomdataservice.common;

import javax.xml.namespace.QName;


public interface DICOMDataServiceConstants {
	public static final String SERVICE_NS = "http://dicomdataservice.ivi.nci.nih.gov/DICOMDataService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "DICOMDataServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "DICOMDataServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	public static final QName DOMAINMODEL = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata.dataservice", "DomainModel");
	
}
