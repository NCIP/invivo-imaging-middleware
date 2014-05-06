package gov.nih.nci.ivi.dicom.virtualpacs.gui;

/*
 * this window is the simplified version for RSNA
 */

import gov.nih.nci.cagrid.common.portal.PortalLookAndFeel;
import gov.nih.nci.ivi.dicom.virtualpacs.VirtualPACS;
import gov.nih.nci.ivi.guicomponents.ServiceDiscoveryPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;

import org.cagrid.grape.ApplicationComponent;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.DicomNetworkException;


/**
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @version $Id: VirtualPACSWindow.java,v 1.6 2007/06/13 18:20:53 tpan Exp $
 */
public class VirtualPACSWindow extends ApplicationComponent {

    private static final String NAMED = "named";
	private static final String BROWSER = "Browser";

	public static final String DICOM_DATA_SERVICE = "DICOMDataService";
	public static final String NCIA_DATA_SERVICE = "NCIACoreService";

	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private javax.swing.JPanel jContentPane = null;
    private JPanel contentPanel = null;
    private JScrollPane jScrollPane = null;
    private JPanel queryPanel = null;
    private JButton stopVPACSButton = null;
    private JButton startVPACSButton = null;
    private JPanel progressPanel = null;
    private JProgressBar progress = null;
    private JCheckBox allServices = null;
// barla
    private boolean started;
// barla

    private JPanel mainPanel;
	private HashMap<String, VirtualPACS> virtualPACSList;
	private ServiceDiscoveryPanel servicesPanel;

    /**
     * This is the default constructor
     */
    public VirtualPACSWindow() {
        super();

        
        initialize();
//        this.setFrameIcon(WorkbenchLookAndFeel.getDataBrowserIcon());
    }


    /**
     * This method initializes this
     * 
     * @return void
     */
    private void initialize() {
        System.out.println(this.getSize().toString());
        this.setContentPane(getJContentPane());
        this.setTitle(VirtualPACSConf.getAppName() + ": " + VirtualPACSWindow.BROWSER);
        this.virtualPACSList = new HashMap<String, VirtualPACS>();
    }

    /**
     * This method initializes jContentPane
     * 
     * @return javax.swing.JPanel
     */
    private javax.swing.JPanel getJContentPane() {
        if (jContentPane == null) {
            jContentPane = new javax.swing.JPanel();
            jContentPane.setLayout(new java.awt.BorderLayout());
            jContentPane.add(getMainPanel(), java.awt.BorderLayout.CENTER);
        }
        return jContentPane;
    }



    private JPanel getMainPanel() {
        if (mainPanel == null) {
            
            // map window  - for now, put the list of series on there
            GridBagConstraints statusConstraints = new GridBagConstraints();
            statusConstraints.anchor = GridBagConstraints.SOUTH;
            statusConstraints.gridheight = GridBagConstraints.REMAINDER;
            statusConstraints.gridwidth = 1;
            statusConstraints.gridx = 0;
            statusConstraints.gridy = GridBagConstraints.RELATIVE;
            statusConstraints.weightx = 1.0D;
            statusConstraints.weighty = 0.0D;
            statusConstraints.fill = GridBagConstraints.HORIZONTAL;

            
            GridBagConstraints serviceConstraints = new GridBagConstraints();
            serviceConstraints.gridx = 0;
            serviceConstraints.gridy = 1;
            serviceConstraints.ipadx = 2;
            serviceConstraints.ipady = 2;
//            serviceConstraints.gridwidth = 1;
//            serviceConstraints.gridheight = 1;
            serviceConstraints.weightx = 1.0D;
            serviceConstraints.weighty = 0.0D;
            //serviceConstraints.insets = new java.awt.Insets(2, 3, 2, 3);
            serviceConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            serviceConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
            // with querying filters
            // with "query" button
            


            // series list panel - scrolling table
            GridBagConstraints listConstraints = new GridBagConstraints();
            listConstraints.gridx = 0;
            listConstraints.gridy = 0;
            listConstraints.ipadx = 2;
            listConstraints.ipady = 2;
//            listConstraints.gridwidth = 1;
//            listConstraints.gridheight = 1;
            listConstraints.weightx = 1.0D;
            listConstraints.weighty = 1.0D;
            //listConstraints.insets = new java.awt.Insets(2, 3, 2, 3);
            listConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            listConstraints.fill = java.awt.GridBagConstraints.BOTH;

            
            mainPanel = new JPanel();
            mainPanel.setLayout(new GridBagLayout());
            // the panel with list of services and start/stop button
            mainPanel.add(getServicesPanel(), listConstraints);
            mainPanel.add(getVPACSControlPanel(), serviceConstraints);
            mainPanel.add(getProgressPanel(), statusConstraints);
            
            mainPanel.revalidate();
        }
        return mainPanel;
    }

    private JPanel getServicesPanel() {
    	
    	if (servicesPanel == null) {

    		String[] models = new String[] {"NCIA_MODEL"};
    		String[] serviceNames = new String[] {DICOM_DATA_SERVICE, NCIA_DATA_SERVICE};
    		servicesPanel = new ServiceDiscoveryPanel(serviceNames, models);
    		    		
    		// set the presets.
    		servicesPanel.setPresetServices(VirtualPACSConf.getDataServices());
    		
    	}
    	return servicesPanel;
    }
    
    /**
     * This method initializes progressPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getProgressPanel() {
        if (progressPanel == null) {
        	
            GridBagConstraints gridBagConstraints = new GridBagConstraints();
            gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
            gridBagConstraints.gridx = -1;
            gridBagConstraints.gridy = -1;
            gridBagConstraints.weightx = 1.0D;
            gridBagConstraints.insets = new Insets(2, 2, 2, 2);
            progressPanel = new JPanel();
            progressPanel.setLayout(new GridBagLayout());
            progressPanel.add(getProgress(), gridBagConstraints);
        }
        return progressPanel;
    }


    /* (non-Javadoc)
	 * @see edu.osu.bmi.gridcad.workbench.GridCADWindowI#getProgress()
	 */
    public JProgressBar getProgress() {
        if (progress == null) {
            progress = new JProgressBar();
            progress.setForeground(getOhioStateRed());
            progress.setString("");
            progress.setStringPainted(true);
        }
        return progress;
    }

    
    
    /**
     * This method initializes jPanel
     * holds the list of dicom data services, and buttons.
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getVPACSControlPanel() {
        if (contentPanel == null) {
            // list of services
            GridBagConstraints gridBagConstraints2 = new GridBagConstraints();
            gridBagConstraints2.anchor = GridBagConstraints.SOUTH;
            gridBagConstraints2.insets = new Insets(2, 3, 2, 3);
            gridBagConstraints2.gridheight = GridBagConstraints.RELATIVE;
            gridBagConstraints2.gridwidth = 1;
            gridBagConstraints2.gridx = -1;
            gridBagConstraints2.gridy = -1;
            gridBagConstraints2.ipadx = 2;
            gridBagConstraints2.ipady = 2;
            gridBagConstraints2.weightx = 1.0D;
            gridBagConstraints2.weighty = 1.0D;
            gridBagConstraints2.fill = GridBagConstraints.BOTH;
            GridBagConstraints serviceConstraints = new GridBagConstraints();
            serviceConstraints.gridx = 0;
            serviceConstraints.gridy = 0;
            serviceConstraints.weightx = 1.0D;
            serviceConstraints.weighty = 1.0D;
            serviceConstraints.anchor = java.awt.GridBagConstraints.NORTH;
            serviceConstraints.fill = java.awt.GridBagConstraints.BOTH;
            
            // button
            GridBagConstraints serviceButtonConstraints = new GridBagConstraints();
            serviceButtonConstraints.gridx = 0;
            serviceButtonConstraints.gridy = 1;
            serviceButtonConstraints.weighty = 0.0D;
            serviceButtonConstraints.weightx = 1.0D;
            serviceButtonConstraints.anchor = java.awt.GridBagConstraints.SOUTH;
            serviceButtonConstraints.gridheight = GridBagConstraints.REMAINDER;
            serviceButtonConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;

            contentPanel = new JPanel();
            contentPanel.setLayout(new GridBagLayout());
            contentPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Select Sites",
                    javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION,
                    javax.swing.border.TitledBorder.DEFAULT_POSITION, null, getOhioStateRed()));
            
            contentPanel.add(getServiceListPanel(), serviceConstraints);
            contentPanel.add(getButtonsPanel(), serviceButtonConstraints);
            
            
        }
        return contentPanel;
    }



    /**
     * This method initializes queryPanel
     * 
     * @return javax.swing.JPanel
     */
    private JPanel getButtonsPanel() {
        if (queryPanel == null) {


            GridBagConstraints allServiceConstraints = new GridBagConstraints();
            allServiceConstraints.insets = new java.awt.Insets(1, 1, 1, 1);
            allServiceConstraints.gridy = 0;
            allServiceConstraints.anchor = java.awt.GridBagConstraints.WEST;
            allServiceConstraints.gridx = 0;
            allServiceConstraints.weightx = 0.0D;
            allServiceConstraints.fill = GridBagConstraints.NONE;

            GridBagConstraints refreshButtonConstraints = new GridBagConstraints();
            refreshButtonConstraints.insets = new java.awt.Insets(5, 1, 5, 1);
            refreshButtonConstraints.gridy = 0;
            refreshButtonConstraints.anchor = java.awt.GridBagConstraints.WEST;
            refreshButtonConstraints.gridx = GridBagConstraints.RELATIVE;
            refreshButtonConstraints.weightx = 0.0D;
            refreshButtonConstraints.fill = GridBagConstraints.NONE;

            GridBagConstraints startButtonConstraints = new GridBagConstraints();
            startButtonConstraints.insets = new java.awt.Insets(5, 1, 5, 1);
            startButtonConstraints.gridy = 0;
            startButtonConstraints.anchor = java.awt.GridBagConstraints.WEST;
            startButtonConstraints.gridx = GridBagConstraints.RELATIVE;
            startButtonConstraints.weightx = 0.0D;
            startButtonConstraints.fill = GridBagConstraints.NONE;
            
            GridBagConstraints stopButtonConstraints = new GridBagConstraints();
            stopButtonConstraints.insets = new java.awt.Insets(5, 1, 5, 1);
            stopButtonConstraints.gridy = 0;
            stopButtonConstraints.gridx = GridBagConstraints.RELATIVE;
            stopButtonConstraints.weightx = 0.0D;
            stopButtonConstraints.anchor = GridBagConstraints.EAST;
            stopButtonConstraints.fill = java.awt.GridBagConstraints.NONE;
            
            queryPanel = new JPanel();
            queryPanel.setLayout(new GridBagLayout());


            queryPanel.add(getAllServices(), allServiceConstraints);
            queryPanel.add(getStartButton(), startButtonConstraints);
            queryPanel.add(getStopButton(), stopButtonConstraints);
        }
        return queryPanel;
    }

    /**
     * This method initializes allCheckBox
     * 
     * @return javax.swing.JCheckBox
     */
    private JPanel getAllServices() {
    	JPanel allServicesPanel = new JPanel();
    	
        if (allServices == null) {
            allServices = new JCheckBox();
            allServices.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                	if (allServices.isSelected()) {
                		servicesPanel.selectAll();
                	} else {
                		servicesPanel.clearAll();
                	}
                }
            });
        }
        
        allServicesPanel.add(allServices);

        JLabel allServicesLabel = new JLabel("All Services");
        allServicesPanel.add(allServicesLabel);
        
        return allServicesPanel;
    }
        
    
    /**
     * This method initializes formQuery button
     * 
     * @return javax.swing.JButton
     */
// barla
    private JButton getStartButton() {
        if (startVPACSButton == null) {
        	startVPACSButton = new JButton();
        	startVPACSButton.setEnabled(true);
        	startVPACSButton.setText("Start");
        	startVPACSButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        	startVPACSButton.setToolTipText("Start Virtual PACS");
        	startVPACSButton.setIcon(getQueryIcon());
        	final VirtualPACSWindow vPACSWindow = this;
        	startVPACSButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (started)
                    	System.err.println("Already started" + 
                                    "Open a new window to start a new vPACS instance");
                        //PortalUtils.showErrorMessage("Already started",
                        //    "Open a new window to start a new vPACS instance");
                    else {
                        started = true;
                        
                        // load the properties file
                        Properties prop = new Properties();
                        try {
							prop.load(new FileInputStream(VirtualPACSConf.getVirtualPacsProperties()));
						} catch (FileNotFoundException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                        try {
                        	// actually, there should not be any reuse.
                        	VirtualPACS vpacs = vPACSWindow.virtualPACSList.get(NAMED);
                        	if (vpacs == null) { 
                        		String[] services = servicesPanel.getSelectedDataServices();
                        		vpacs = new VirtualPACS(prop, services);
                        		vPACSWindow.virtualPACSList.put(NAMED, vpacs);
                        	}
							vpacs.start();
							stopVPACSButton.setEnabled(started);
							startVPACSButton.setEnabled(!started);
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DicomException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						} catch (DicomNetworkException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
                    }
                }
            });
        }
        return startVPACSButton;
    }

    /**
     * This method initializes runQuery button
     * 
     * @return javax.swing.JButton
     */
    private JButton getStopButton() {
        if (stopVPACSButton == null) {
        	stopVPACSButton = new JButton();
        	stopVPACSButton.setText("Stop");
        	stopVPACSButton.setEnabled(false);
        	stopVPACSButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        	stopVPACSButton.setToolTipText("Stop the current Virtual PACS");
        	stopVPACSButton.setIcon(getQueryIcon());
        	final VirtualPACSWindow VPACSWindow = this;
        	stopVPACSButton.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    if (!started)
                        System.err.println("Already stopped"+
                                        "press \"start\" to restart this vPACS instance");
                    //PortalUtils.showErrorMessage("Already stopped",
                    //        "press start to restart this vPACS instance");
                    else {
                        started = false;
  
	                	// stop the virtual pacs server
	                	VirtualPACS pacs = VPACSWindow.virtualPACSList.remove(NAMED);
	                	if (pacs != null) {
	                		pacs.stop();
	                		System.out.println("pacs stopped ");
	                	}
						startVPACSButton.setEnabled(!started);
						stopVPACSButton.setEnabled(started);
                    }
                }
            });
        }
        return stopVPACSButton;
    }    
    
    /**
     * This method initializes jScrollPane
     * 
     * @return javax.swing.JScrollPane
     */
    private JScrollPane getServiceListPanel() {
        if (jScrollPane == null) {
            jScrollPane = new JScrollPane();
            jScrollPane.setViewportView(getDataServices());
        }
        return jScrollPane;
    }
    
	

	
    private Component getDataServices() {
		// TODO Auto-generated method stub
		return null;
	}


        
    
    public final static Color getOhioStateRed(){
		 	float[] vals = new float[3];
			Color.RGBtoHSB(154,0,0,vals);
			return Color.getHSBColor(vals[0],vals[1],vals[2]);
		 }

	 public final static ImageIcon getQueryIcon(){
		 	return new javax.swing.ImageIcon(PortalLookAndFeel.class.getResource("/Magnify.gif"));
		 }

}
