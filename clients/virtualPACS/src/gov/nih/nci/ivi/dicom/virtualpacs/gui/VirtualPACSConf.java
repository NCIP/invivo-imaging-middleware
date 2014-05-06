package gov.nih.nci.ivi.dicom.virtualpacs.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.HashSet;


import org.apache.log4j.Logger;
import org.cagrid.grape.GridApplication;
import org.cagrid.grape.configuration.GeneralConfiguration;
import org.cagrid.grape.configuration.Properties;
import org.cagrid.grape.configuration.Property;
import org.cagrid.grape.configuration.Values;


/**
 * @author <A href="mailto:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A href="mailto:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A href="mailto:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ArgumentManagerTable.java,v 1.2 2004/10/15 16:35:16 langella
 *          Exp $
 */
public class VirtualPACSConf {

	private static Logger log = Logger.getLogger(VirtualPACSConf.class);;
	
    public static String UI_CONF = "VirtualPACS";
	public static String APP_NAME = "AppName";	
	public static final String VPACS_PROPERTIES = "VPACSProperties";
    private static String DATA_SERVICES_ELEMENT = "data-services";

    
    private VirtualPACSConf() {

    }


    public static Set<String> getDataServices() {
		return new HashSet<String>(getValues(VirtualPACSConf.DATA_SERVICES_ELEMENT));
    }
    
    public static String getAppName() {
		return getValues(VirtualPACSConf.APP_NAME).get(0);
    }
    
    public static String getVirtualPacsProperties() {
    	return getValues(VirtualPACSConf.VPACS_PROPERTIES).get(0);
    }



	public static List<String> getValues(String property) {
		List<String> values = new ArrayList<String>();
		try {
			GeneralConfiguration conf = (GeneralConfiguration) GridApplication.getContext().getConfigurationManager()
				.getConfigurationObject(VirtualPACSConf.UI_CONF);

			Properties props = conf.getProperties();
			if (props != null) {
				Property[] prop = props.getProperty();
				if (prop != null) {
					for (int i = 0; i < prop.length; i++) {
						if (prop[i].getName().equals(property)) {
							Values vals = prop[i].getValues();
							if (vals != null) {
								String[] val = vals.getValue();
								if (val != null) {
									for (int j = 0; j < val.length; j++) {
										values.add(val[j]);
									}
								}
							}
						}
					}
				}
			}

		} catch (Exception e) {
			log.error("Error loading the property " + property + " from the configuration.");
			log.error(e);
		}
		return values;
	}


}
