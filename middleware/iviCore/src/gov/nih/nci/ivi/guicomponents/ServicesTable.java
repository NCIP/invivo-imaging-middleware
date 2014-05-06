package gov.nih.nci.ivi.guicomponents;


import java.util.HashMap;
import java.util.Random;
import java.util.Vector;

import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.apache.axis.message.addressing.EndpointReferenceType;
import org.projectmobius.portal.PortalTable;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: ServicesTable.java,v 1.2 2007/06/13 18:20:19 tpan Exp $
 */
public class ServicesTable extends PortalTable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6029734844846173027L;
	public static String SERVICE = "Service";
	public static String TYPE = "Location"; 
	public static String URL = "URL";
	
	private static String[] FIRST_NAMES = {
	        "Albert", "Brenda", "Charles", "Deana", "Eric", "Francesca", "George", "Hermione", "Ivan", "Jennifer",
	        "Kevin", "Laurie", "Michael", "Nancy", "Octavius", "Penelope", "Quentin", "Rachel", "Steven", "Tara",
	        "Ulysses", "Valerie", "Walter", "Xena", "Yanis", "Zoe"  
	};
	private static String[] LAST_NAMES = {
	        "Aardvark", "Badger", "Cheeta", "Dolphin", "Elephant", "Ferret", "Gecko", "Hawk", "Iquana", "Jellyfish",
	        "Kangaroo", "Lemur", "Mouse", "Nightingale", "Otter", "Porcupine", "Quail", "Rabbit", "Snake", "Turtle",
	        "Unicorn", "Viper", "Wallaby", "X", "Yak", "Zebra"  
	};
	
    private static String ipList[] = {
    	// OSC
    	"http://192.232.26.250:8080/wsrf/services/cagrid/DICOMDataService",
    	// BMI
    	"http://140.254.80.34:8080/wsrf/services/cagrid/DICOMDataService",
    	// NCIA
    	"http://137.187.67.108:8080/wsrf/services/cagrid/DICOMDataService",
    	// L.A.
    	"http://128.125.124.240:8080/wsrf/services/cagrid/DICOMDataService",
    	// QARC
    	"http://204.17.95.243:8080/wsrf/services/cagrid/DICOMDataService",
    	// Argonne
    	"http://128.135.125.146:8080/wsrf/services/cagrid/DICOMDataService",
    	// Brazil
    	"http://150.164.254.225:8080/wsrf/services/cagrid/DICOMDataService",
    	// Turkey
    	"http://139.179.21.147:8080/wsrf/services/cagrid/DICOMDataService",
    	// RSNA1
    	"http://167.165.30.101:8080/wsrf/services/cagrid/DICOMDataService",
    	// RSNA2
    	"http://167.165.30.103:8080/wsrf/services/cagrid/DICOMDataService",
    	};

    private static final String[] labels = {
    	"OSC",
    	"BMI",
    	"NCIA",
    	"L.A.",
    	"QARC",
    	"Argonne",
    	"Brazil",
    	"Turkey",
    	"RSNA1",
    	"RSNA2"};

    private  HashMap <String,String> siteLabelMap = null;

	private static Random rand = new Random(0);
	private static int firstNameId;
	private static int lastNameId;
	
	
	public ServicesTable() {

		super(createTableModel());
		this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		//this.addMouseListener(new MultipleRowSelectionMouseListener());
				
		TableColumn c = this.getColumn(SERVICE);
		c.setMaxWidth(0);
		c.setMinWidth(0);
		c.setPreferredWidth(0);
		c.setResizable(false);

		c = this.getColumn(TYPE);
		c.setWidth(40);
		c.setPreferredWidth(40);
		
		c = this.getColumn(URL);
		c.setWidth(40);
		c.setPreferredWidth(40);
		
        siteLabelMap = new HashMap<String, String>();
        for (int i = 0; i < ipList.length; i++)
        	siteLabelMap.put(ipList[i], labels[i]);
		
		this.clearTable();
	}


    protected static DefaultTableModel createTableModel() {
		DefaultTableModel model = new DefaultTableModel();
		model.addColumn(SERVICE);
		model.addColumn(TYPE);
		model.addColumn(URL);
		return model;
	}

	static protected synchronized String getRandomName() {
	    ServicesTable.firstNameId++;
	    ServicesTable.firstNameId = ServicesTable.firstNameId % 25;
	    ServicesTable.lastNameId++;
	    ServicesTable.lastNameId = ServicesTable.lastNameId % 25;
	    
	    return ServicesTable.FIRST_NAMES[ServicesTable.firstNameId] + " " + ServicesTable.LAST_NAMES[ServicesTable.lastNameId];
	}
	
	
	protected synchronized void addServices(EndpointReferenceType[] services) {
		for (EndpointReferenceType s : services) {
			if (s == null) {
				continue;
			}
			
			Vector<Object> v = new Vector<Object>();
			v.add(s);
			if (s.getAddress() == null || 
					siteLabelMap.get(s.getAddress().toString()) == null)
				v.add("Unknown");
			else
				v.add(siteLabelMap.get(s.getAddress().toString()));
			v.add(s.getAddress());
			addRow(v);
		}
	}

	protected synchronized EndpointReferenceType[] getSelectedServices() {
		int[] ids = this.getSelectedRows();
		EndpointReferenceType[] services = new EndpointReferenceType[ids.length];
		for (int i = 0; i < ids.length; i++) {
			services[i] = EndpointReferenceType.class.cast(this.getModel().getValueAt(ids[i], 0));
		}
		return services;
	}

}