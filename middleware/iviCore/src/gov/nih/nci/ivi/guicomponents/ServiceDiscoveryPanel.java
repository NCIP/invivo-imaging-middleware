/*
 * Created on Nov 25, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Generation - Code and Comments
 */
package gov.nih.nci.ivi.guicomponents;

import gov.nih.nci.cagrid.discovery.client.DiscoveryClient;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.border.LineBorder;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.types.URI.MalformedURIException;
import org.apache.commons.collections.set.ListOrderedSet;
import org.cagrid.grape.utils.ErrorDialog;
/**
 * @author tpan
 *

 */
public class ServiceDiscoveryPanel extends JPanel {

	public static final String SEARCH_TYPE_MODEL = "model";
	public static final String SEARCH_TYPE_SERVICE_NAME = "service name";
	
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
    protected HashMap<String, String> services;
    protected HashMap<String, String> servicesLocations;

	private JComboBox searchTypeCombo = null;
	private JComboBox searchValueCombo = null;
	private JList serviceListBox = null;
	private JPanel serviceSearchPanel = null;

    private static final String[] SEARCH_TYPES = new String[] {
    	SEARCH_TYPE_MODEL, SEARCH_TYPE_SERVICE_NAME
    };
    private String[] serviceNames;
    private String[] serviceModels;
    private JToggleButton dataServiceOnlyButton;

	private ServicesTable servicesTable;
	private Set<String> presetServices = new HashSet<String>();
	private Set<String> userServices = new HashSet<String>();

	private String indexServiceURL = null;

	public ServiceDiscoveryPanel(String indexServiceURL, String[] serviceNames, String[] serviceModels) {
		this(serviceNames, serviceModels);
		this.indexServiceURL = indexServiceURL;
	}

	public ServiceDiscoveryPanel(String[] serviceNames, String[] serviceModels) {
        if (serviceNames == null) {
        	this.serviceNames = new String[] {};
        } else {
        	this.serviceNames = serviceNames;
        }
        if (serviceModels == null) {
        	this.serviceModels = new String[] {};
        } else {
        	this.serviceModels = serviceModels;
        }
    	initialize();
    }


    public void initialize() {

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.anchor = GridBagConstraints.NORTH;
        gridBagConstraints1.weightx = 1.0;
        gridBagConstraints1.weighty = 0.0;


        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.fill = GridBagConstraints.BOTH;
        gridBagConstraints2.gridy = GridBagConstraints.RELATIVE;
        gridBagConstraints2.gridx = 0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridheight = GridBagConstraints.REMAINDER;
        gridBagConstraints2.anchor = GridBagConstraints.NORTH;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;


        this.setLayout(new GridBagLayout());
        this.add(getServiceSearchPanel(), gridBagConstraints1);
        this.add(getServiceListBox(), gridBagConstraints2);

    }

    /**
	 * This method initializes serviceListBox
	 *
	 * @return javax.swing.JList
	 */
	private JScrollPane getServiceListBox() {
		if (serviceListBox == null) {
			JScrollPane jScrollPane = new JScrollPane();
	        jScrollPane.setViewportView(getServicesTable());
	        return jScrollPane;
		}
		return null;
	}

	private ServicesTable getServicesTable() {
		if (servicesTable == null) {
			servicesTable = new ServicesTable();
		}
		return servicesTable;
	}


	/**
	 * This method initializes serviceSearchPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getServiceSearchPanel() {

		if (serviceSearchPanel == null) {
			serviceSearchPanel = new JPanel();
		}

		// all service checkbox
        serviceSearchPanel.setLayout(new GridBagLayout());

        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 0.0;
        gridBagConstraints1.weighty = 1.0;


        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 2;
        gridBagConstraints2.gridheight = 1;
        gridBagConstraints2.anchor = GridBagConstraints.EAST;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.fill = WIDTH;
        gridBagConstraints2.weighty = 1.0;

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 3;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.gridwidth = 1;
        gridBagConstraints3.gridheight = 1;
        gridBagConstraints3.anchor = GridBagConstraints.EAST;
        gridBagConstraints3.weightx = 0.0;
        gridBagConstraints3.weighty = 1.0;

//        serviceSearchPanel.add(comp)
		serviceSearchPanel.add(getDataServiceOnlyPanel(), gridBagConstraints1);
		serviceSearchPanel.add(getServiceFilterPanel(), gridBagConstraints2);

		// add the search button
		serviceSearchPanel.add(getServiceSearchButton(), gridBagConstraints3);

		
		return serviceSearchPanel;
	}

	private JPanel getDataServiceOnlyPanel() {
		JPanel dataServiceOnlyPanel = new JPanel();
		dataServiceOnlyPanel.setBorder(new LineBorder(Color.BLACK));

		if (dataServiceOnlyButton == null) {
			dataServiceOnlyButton = new JToggleButton();
		}
		dataServiceOnlyButton.setSelected(true);
		dataServiceOnlyPanel.add(dataServiceOnlyButton);
		JLabel dataServiceOnlyLabel = new JLabel("Data Service Only");
		dataServiceOnlyPanel.add(dataServiceOnlyLabel);

		return dataServiceOnlyPanel;
	}
	
	private JPanel getServiceFilterPanel() {
		JPanel serviceFilterPanel = new JPanel();
		serviceFilterPanel.setBorder(new LineBorder(Color.BLACK));

		serviceFilterPanel.setLayout(new GridBagLayout());


		
        GridBagConstraints gridBagConstraints1 = new GridBagConstraints();
        gridBagConstraints1.gridx = 0;
        gridBagConstraints1.gridy = 0;
        gridBagConstraints1.gridwidth = 1;
        gridBagConstraints1.gridheight = 1;
        gridBagConstraints1.anchor = GridBagConstraints.WEST;
        gridBagConstraints1.weightx = 0.0;
        gridBagConstraints1.weighty = 1.0;


        GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
        gridBagConstraints2.gridx = 1;
        gridBagConstraints2.gridy = 0;
        gridBagConstraints2.gridwidth = 1;
        gridBagConstraints2.gridheight = 1;
        gridBagConstraints2.anchor = GridBagConstraints.WEST;
        gridBagConstraints2.weightx = 1.0;
        gridBagConstraints2.weighty = 1.0;

        GridBagConstraints gridBagConstraints3 = new GridBagConstraints();
        gridBagConstraints3.gridx = 2;
        gridBagConstraints3.gridy = 0;
        gridBagConstraints3.gridwidth = 2;
        gridBagConstraints3.gridheight = 1;
        gridBagConstraints3.anchor = GridBagConstraints.WEST;
        gridBagConstraints3.weightx = 1.0;
        gridBagConstraints3.weighty = 1.0;
        gridBagConstraints3.fill = WIDTH;

		
		JLabel serviceFilterLabel = new JLabel("Search By: ");
		serviceFilterPanel.add(serviceFilterLabel, gridBagConstraints1);

		// search dropdown
		if (searchTypeCombo == null) {
			searchTypeCombo = new JComboBox();
		}
		for (int i = 0; i < SEARCH_TYPES.length; i++) {
			searchTypeCombo.addItem(SEARCH_TYPES[i]);
		}
		searchTypeCombo.setSelectedIndex(0);
		searchTypeCombo.setEditable(false);
		serviceFilterPanel.add(searchTypeCombo, gridBagConstraints2);
		searchTypeCombo.addItemListener(new ItemListener() {

			public void itemStateChanged(ItemEvent arg0) {
		        JComboBox cb = (JComboBox)arg0.getSource();
		        String selectedStr = (String)cb.getSelectedItem();
		        setFilterContent(selectedStr);
			}
		});

		//	
		if (searchValueCombo == null) {
			searchValueCombo = new JComboBox();
		}
		String selectedStr = SEARCH_TYPES[searchTypeCombo.getSelectedIndex()];
        setFilterContent(selectedStr);
		searchValueCombo.setSelectedIndex(-1);
		searchValueCombo.setEditable(true);
		serviceFilterPanel.add(searchValueCombo, gridBagConstraints3);
		
		return serviceFilterPanel;
	}

	/**
	 * @param selectedStr
	 */
	private void setFilterContent(String selectedStr) {
		searchValueCombo.removeAllItems();
		if (selectedStr.equals(SEARCH_TYPE_MODEL)) {
    		for (int i = 0; i < this.serviceModels.length; i++) {
    			searchValueCombo.addItem(this.serviceModels[i]);
    		}
        } else if (selectedStr.equals(SEARCH_TYPE_SERVICE_NAME)) {
    		for (int i = 0; i < this.serviceNames.length; i++) {
    			searchValueCombo.addItem(this.serviceNames[i]);
    		}        	
        }
	}


	   /**
     * This method initializes runQuery button
     *
     * @return javax.swing.JButton
     */
    private JButton getServiceSearchButton() {

    	JButton runQuery = new JButton();
        runQuery.setText("Locate");
        runQuery.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        runQuery.addActionListener(new java.awt.event.ActionListener() {
        	public void actionPerformed(java.awt.event.ActionEvent e) {

        		List<EndpointReferenceType> all = new ArrayList<EndpointReferenceType>();
    			// register the user added ones and the presets
    			for (String presetService : presetServices) {

    				EndpointReferenceType epr = new EndpointReferenceType();
    				AttributedURI tmp = null;
					try {
						tmp = new AttributedURI(presetService);
					} catch (MalformedURIException e1) {
						e1.printStackTrace();
					}
    				epr.setAddress(tmp);
    				all.add(epr);
    			}

    			for (String userService : userServices) {
    				EndpointReferenceType epr = new EndpointReferenceType();
    				AttributedURI tmp = null;
					try {
						tmp = new AttributedURI(userService);
					} catch (MalformedURIException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
    				epr.setAddress(tmp);
    				all.add(epr);
    			}
        		
        		String searchType = String.class.cast(searchTypeCombo.getSelectedItem());
        		String searchValue = String.class.cast(searchValueCombo.getSelectedItem());
        		boolean dataServiceOnly = dataServiceOnlyButton.isSelected();
        		EndpointReferenceType[] ds = null;
       			ds = discoverServices(searchType, searchValue, dataServiceOnly);
        			//if (ds == null) {
        			//	PortalUtils.showErrorMessage(" no services of type " + dataServiceName + " was found");
        			//}
    			if (ds == null || ds.length == 0) {
    				ErrorDialog.showError(" no services of type " + searchValue + " was discovered at index service ");
        		}

    			if (ds != null) {
		    		for (int i = 0; i < ds.length; i++) {
		    			all.add(ds[i]);
		    		}
        		}

    			registerServices(all.toArray(new EndpointReferenceType[] {}));

    			//setPreviouslySelected(selected);
            }
        });
        return runQuery;
    }






    public EndpointReferenceType[] discoverServices(String searchType, String searchValue, boolean dataServiceOnly) {
    	EndpointReferenceType[] result = null;
    	try {
    		DiscoveryClient discClient = null;
    		if (this.indexServiceURL == null || this.indexServiceURL.equals("")) {
    			discClient = new DiscoveryClient();
    		} else {
    			discClient = new DiscoveryClient(this.indexServiceURL);
    		}
    		
			if (searchType.equals(SEARCH_TYPE_MODEL)) {
				if (searchValue == null || searchValue.equals("") || searchValue.equals("*")) {
					if (dataServiceOnly) {
						result = discClient.getAllDataServices();
					} else {
						result = discClient.getAllServices(false);
					}
				} else {
					result = discClient.discoverDataServicesByDomainModel(searchValue);
				}
			} else if (searchType.equals(SEARCH_TYPE_SERVICE_NAME)) {
				if (searchValue == null || searchValue.equals("") || searchValue.equals("*")) {
					if (dataServiceOnly) {
						result = discClient.getAllDataServices();
					} else {
						result = discClient.getAllServices(false);
					}
				} else {
					result = discClient.discoverServicesByName(searchValue);
				}
			}
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
    }
    
    /**
     * This method initializes dataServicePanel
     *
     * @return javax.swing.JPanel
     */
    protected void registerServices(EndpointReferenceType[] ds) {
    	this.servicesTable.clearTable();

    	if (ds == null) {
    		return;
        }

        this.servicesTable.addServices(ds);
    }

    protected void unregisterServices(EndpointReferenceType[] ds) {

    }



    public String[] getSelectedDataServices() {
        EndpointReferenceType[] services = this.servicesTable.getSelectedServices();
        String[] l = new String[services.length];
        for (int i = 0; i < services.length; i++) {
            l[i] = services[i].getAddress().toString();
        }
        return l;
    }



	public static void main(String[] args) {
    	ServiceDiscoveryPanel panel = new ServiceDiscoveryPanel(null, null);
    	EndpointReferenceType[] ds = panel.discoverServices(SEARCH_TYPE_MODEL, "NCIA_MODEL", true);
    	if (ds != null)
    	System.out.println(ds.length);

    	JFrame frame = new JFrame("Test");
        // frame.getContentPane().setLayout(new GridLayout(1, 1));
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });


        frame.getContentPane().add(panel);
        frame.setSize(600, 600);
        frame.pack();

        frame.setVisible(true);
        frame.show();

    }

	public String getIndexServiceURL() {
		return indexServiceURL;
	}

	public void setIndexServiceURL(String indexServiceURL) {
		this.indexServiceURL = indexServiceURL;
	}

	public void selectAll() {
		servicesTable.selectAll();
	}

	public void clearAll() {
		servicesTable.clearSelection();
	}

	public void setPresetServices(Set<String> presetServices) {
		this.presetServices = presetServices;
	}
	public void addUserServices(String userService) {
		this.userServices.add(userService);
	}
	public void removeUserServices(String userService) {
		this.userServices.remove(userService);
	}
	public void clearUserServices() {
		this.userServices.clear();
	}

}  //  @jve:decl-index=0:visual-constraint="286,27"
