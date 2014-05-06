package gov.nih.nci.ivi.dsdcontainerservice.common;

import javax.xml.namespace.QName;


public interface DSDContainerServiceConstants {
	public static final String SERVICE_NS = "http://dsdcontainerservice.ivi.nci.nih.gov/DSDContainerService";
	public static final QName RESOURCE_KEY = new QName(SERVICE_NS, "DSDContainerServiceKey");
	public static final QName RESOURCE_PROPERTY_SET = new QName(SERVICE_NS, "DSDContainerServiceResourceProperties");

	//Service level metadata (exposed as resouce properties)
	public static final QName SERVICEMETADATA = new QName("gme://caGrid.caBIG/1.0/gov.nih.nci.cagrid.metadata", "ServiceMetadata");
	
}
