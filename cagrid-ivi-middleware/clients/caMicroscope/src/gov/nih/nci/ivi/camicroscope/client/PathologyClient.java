/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewJFrame.java
 *
 * Created on Feb 27, 2009, 7:28:17 PM
 */

package gov.nih.nci.ivi.camicroscope.client;

import edu.emory.cci.aim.client.AIMv1DataServiceClient;
import edu.northwestern.radiology.aim.ImageAnnotation;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.fileinfo.FileInfo;
import gov.nih.nci.ivi.fileinfo.ViewParameters;
import gov.nih.nci.ivi.pathologydataservice.client.PathologyDataServiceClient;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipInputStream;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;

import EDU.oswego.cs.dl.util.concurrent.misc.SwingWorker;

/**
 *
 * @author jim
 */
public class PathologyClient extends javax.swing.JFrame implements MouseListener, MouseMotionListener {

    /** Creates new form NewJFrame */
    public PathologyClient() {
        viewpoint = new Viewpoint();
        setTitle("caMicroscope");
	    cacheDirName = System.getProperty("java.io.tmpdir") + File.separator + "pathologyCache";
        initComponents();


        loadProperties();

        viewpoint.tileSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        System.out.println("tileSize = "+viewpoint.tileSize);

		queryPathologyDataServer("images");
        setJMenuBar(createMenuBar());
        thumbnailSlide.addMouseListener(this);
		thumbnailSlide.addMouseMotionListener(this);
        thumbnailSlide.setViewpoint(viewpoint);
        paintPanel1.setViewpoint(viewpoint);
        paintPanel1.addMouseListener(this);
        paintPanel1.addMouseMotionListener(this);      
        pathologyServerComboBox.setModel(new javax.swing.DefaultComboBoxModel(staticPathologyDataServices));
        annotationServerComboBox.setModel(new javax.swing.DefaultComboBoxModel(staticAnnotationDataServices));
        classCombo.setModel(new javax.swing.DefaultComboBoxModel(paintPanel1.annotationClass));


    }

    void loadProperties(){

        staticPathologyDataServices = new String[4];
        staticAnnotationDataServices = new String[4];

        try {
            Properties props = new Properties();
            props.load(new FileInputStream(new File("camicroscope.properties")));
            paintPanel1.setProperties(props);
            staticPathologyDataServices[0] = props.getProperty("pathologydataserver.0");
            staticPathologyDataServices[1] = props.getProperty("pathologydataserver.1");
            staticPathologyDataServices[2] = props.getProperty("pathologydataserver.2");
            staticPathologyDataServices[3] = props.getProperty("pathologydataserver.3");

            staticAnnotationDataServices[0] = props.getProperty("annotationdataserver.0");
            staticAnnotationDataServices[1] = props.getProperty("annotationdataserver.1");
            staticAnnotationDataServices[2] = props.getProperty("annotationdataserver.2");
            staticAnnotationDataServices[3] = props.getProperty("annotationdataserver.3");





        } catch (IOException ex) {
            Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        configDialog = new javax.swing.JDialog();
        pathologyServerComboBox = new javax.swing.JComboBox();
        tileSizeCombo = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        annotationServerComboBox = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        okButton = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        authorName = new java.awt.TextField();
        jLabel5 = new javax.swing.JLabel();
        jFileChooser1 = new javax.swing.JFileChooser();
        jSplitPane1 = new javax.swing.JSplitPane();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel3 = new javax.swing.JPanel();
        jButton2 = new javax.swing.JButton();
        annotationsScrollPane = new javax.swing.JScrollPane();
        annotationsList = new javax.swing.JList();
        jPanel4 = new javax.swing.JPanel();
        classCombo = new javax.swing.JComboBox();
        drawToggle = new javax.swing.JToggleButton();
        shapeCombo = new javax.swing.JComboBox();
        saveButton = new javax.swing.JButton();
        descriptionScrollPane = new javax.swing.JScrollPane();
        textMarkupPane = new javax.swing.JEditorPane();
        eraseToggle = new javax.swing.JToggleButton();
        zoomLevelCombo = new javax.swing.JComboBox();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        fileTree = new javax.swing.JTree();
        progressBar = new javax.swing.JProgressBar();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jLayeredPane2 = new javax.swing.JLayeredPane();
        thumbNailPanel = new javax.swing.JPanel();
        thumbnailSlide = new gov.nih.nci.ivi.camicroscope.client.ThumbnailSlide();
        paintPanel1 = new gov.nih.nci.ivi.camicroscope.client.PaintPanel();
        imageLabel = new javax.swing.JLabel();

        tileSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Fit Screen", "512x512", "1024x1024", "2048x2048" }));
        tileSizeCombo.setToolTipText("zoom");
        tileSizeCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileSizeComboActionPerformed(evt);
            }
        });

        jLabel1.setText("Configure caMicroscope");

        jLabel2.setText("Pathology Server");

        jLabel3.setText("Annotation Server");

        okButton.setText("ok");
        okButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                okButtonMouseClicked(evt);
            }
        });

        jLabel4.setText("Name");

        authorName.setText("Dr. Jane Doe");

        jLabel5.setText("Tile Size");

        org.jdesktop.layout.GroupLayout configDialogLayout = new org.jdesktop.layout.GroupLayout(configDialog.getContentPane());
        configDialog.getContentPane().setLayout(configDialogLayout);
        configDialogLayout.setHorizontalGroup(
            configDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configDialogLayout.createSequentialGroup()
                .add(configDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(configDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel3))
                    .add(configDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel1))
                    .add(configDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(configDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(configDialogLayout.createSequentialGroup()
                                .add(21, 21, 21)
                                .add(pathologyServerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 337, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(jLabel2)))
                    .add(configDialogLayout.createSequentialGroup()
                        .add(163, 163, 163)
                        .add(okButton))
                    .add(configDialogLayout.createSequentialGroup()
                        .add(41, 41, 41)
                        .add(configDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                            .add(configDialogLayout.createSequentialGroup()
                                .add(jLabel4)
                                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                                .add(authorName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 232, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                            .add(annotationServerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 337, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)))
                    .add(configDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(tileSizeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                    .add(configDialogLayout.createSequentialGroup()
                        .addContainerGap()
                        .add(jLabel5)))
                .addContainerGap(43, Short.MAX_VALUE))
        );
        configDialogLayout.setVerticalGroup(
            configDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(configDialogLayout.createSequentialGroup()
                .add(jLabel1)
                .add(18, 18, 18)
                .add(jLabel2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(pathologyServerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(20, 20, 20)
                .add(jLabel3)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(annotationServerComboBox, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(configDialogLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel4)
                    .add(authorName, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 43, Short.MAX_VALUE)
                .add(jLabel5)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(tileSizeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(46, 46, 46)
                .add(okButton)
                .addContainerGap())
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                formKeyPressed(evt);
            }
        });
        addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                formMouseWheelMoved(evt);
            }
        });

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setResizeWeight(0.2);
        jSplitPane1.setOneTouchExpandable(true);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jButton2.setFont(new java.awt.Font("Lucida Grande", 0, 8));
        jButton2.setText("Reload");
        jButton2.setToolTipText("Reload slides from server");
        jButton2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton2MouseReleased(evt);
            }
        });

        annotationsList.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotations"));
        annotationsList.setToolTipText("select an existing annotation for viewing.");
        annotationsList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                annotationsListValueChanged(evt);
            }
        });
        annotationsList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                annotationsListMouseClicked(evt);
            }
        });
        annotationsScrollPane.setViewportView(annotationsList);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Annotate"));

        classCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Red", "Blue", "Green" }));
        classCombo.setEnabled(false);
        classCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                classComboStateChange(evt);
            }
        });

        drawToggle.setFont(new java.awt.Font("Lucida Grande", 0, 8));
        drawToggle.setText("Draw");
        drawToggle.setToolTipText("turn on/off draw mode for annotation.");
        drawToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                drawToggleActionPerformed(evt);
            }
        });
        drawToggle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                drawToggleKeyTyped(evt);
            }
            public void keyPressed(java.awt.event.KeyEvent evt) {
                drawToggleKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                drawToggleKeyReleased(evt);
            }
        });
        drawToggle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                drawToggleMouseReleased(evt);
            }
        });

        shapeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Freehand", "Line", "Oval" }));
        shapeCombo.setToolTipText("Select type of drawing to do");
        shapeCombo.setEnabled(false);
        shapeCombo.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                shapeComboStateChange(evt);
            }
        });

        saveButton.setText("Save");
        saveButton.setToolTipText("save your annotations to the annotation server");
        saveButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                saveButtonMouseClicked(evt);
            }
        });

        textMarkupPane.setBorder(javax.swing.BorderFactory.createTitledBorder("Description"));
        descriptionScrollPane.setViewportView(textMarkupPane);

        eraseToggle.setFont(new java.awt.Font("Lucida Grande", 0, 8));
        eraseToggle.setText("Erase");
        eraseToggle.setToolTipText("erase exisiting shapes");
        eraseToggle.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                eraseToggleActionPerformed(evt);
            }
        });
        eraseToggle.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                eraseToggleKeyReleased(evt);
            }
        });
        eraseToggle.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                eraseToggleMouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel4Layout = new org.jdesktop.layout.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(3, 3, 3)
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(classCombo, 0, 116, Short.MAX_VALUE)
                    .add(shapeCombo, 0, 116, Short.MAX_VALUE))
                .addContainerGap())
            .add(descriptionScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .add(saveButton, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4Layout.createSequentialGroup()
                .add(drawToggle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 64, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(eraseToggle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 65, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel4Layout.createSequentialGroup()
                .add(jPanel4Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING, false)
                    .add(drawToggle, 0, 0, Short.MAX_VALUE)
                    .add(eraseToggle, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 16, Short.MAX_VALUE))
                .add(18, 18, 18)
                .add(shapeCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(classCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(18, 18, 18)
                .add(descriptionScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 109, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(saveButton, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 20, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE))
        );

        zoomLevelCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "1x", "2x", "4x", "5x", "10x", "20x", "40x", "80x", "100x" }));
        zoomLevelCombo.setToolTipText("zoom in or out.");
        zoomLevelCombo.setBorder(javax.swing.BorderFactory.createTitledBorder("Set Zoom"));
        zoomLevelCombo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomLevelComboActionPerformed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel3Layout = new org.jdesktop.layout.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(annotationsScrollPane, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, jPanel4, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, zoomLevelCombo, 0, 148, Short.MAX_VALUE)
            .add(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 134, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel3Layout.createSequentialGroup()
                .add(annotationsScrollPane, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 113, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton2, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(12, 12, 12)
                .add(jPanel4, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 280, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(zoomLevelCombo, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 53, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        jSplitPane3.setRightComponent(jPanel3);

        jPanel1.setToolTipText("select image for viewing.");
        jPanel1.setPreferredSize(new java.awt.Dimension(144, 150));

        jScrollPane1.setToolTipText("select image for viewing");

        fileTree.setBorder(javax.swing.BorderFactory.createTitledBorder("Slides"));
        fileTree.setModel(fileTreeModel);
        fileTree.setToolTipText("select an image for viewing.");
        fileTree.setEditable(true);
        fileTree.setOpaque(false);
        fileTree.setPreferredSize(new java.awt.Dimension(91, 200));
        fileTree.setSelectionModel(null);
        jScrollPane1.setViewportView(fileTree);

        progressBar.setPreferredSize(new java.awt.Dimension(144, 5));

        jButton3.setFont(new java.awt.Font("Lucida Grande", 0, 8));
        jButton3.setText("Reload");
        jButton3.setToolTipText("Reload slides from server");
        jButton3.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jButton3MouseReleased(evt);
            }
        });

        org.jdesktop.layout.GroupLayout jPanel1Layout = new org.jdesktop.layout.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(progressBar, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
            .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 144, Short.MAX_VALUE)
            .add(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .add(jButton3, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 130, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jPanel1Layout.createSequentialGroup()
                .add(progressBar, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 5, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 166, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton3, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 12, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .add(17, 17, 17))
        );

        jSplitPane3.setLeftComponent(jPanel1);

        jSplitPane1.setLeftComponent(jSplitPane3);

        jLayeredPane2.setOpaque(true);

        thumbNailPanel.setMaximumSize(null);
        thumbNailPanel.setOpaque(false);

        thumbnailSlide.setBackground(new java.awt.Color(0, 0, 255));
        thumbnailSlide.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED, java.awt.Color.black, java.awt.Color.black, java.awt.Color.black, java.awt.Color.black));
        thumbnailSlide.setOpaque(false);

        org.jdesktop.layout.GroupLayout thumbnailSlideLayout = new org.jdesktop.layout.GroupLayout(thumbnailSlide);
        thumbnailSlide.setLayout(thumbnailSlideLayout);
        thumbnailSlideLayout.setHorizontalGroup(
            thumbnailSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 403, Short.MAX_VALUE)
        );
        thumbnailSlideLayout.setVerticalGroup(
            thumbnailSlideLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 351, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout thumbNailPanelLayout = new org.jdesktop.layout.GroupLayout(thumbNailPanel);
        thumbNailPanel.setLayout(thumbNailPanelLayout);
        thumbNailPanelLayout.setHorizontalGroup(
            thumbNailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thumbNailPanelLayout.createSequentialGroup()
                .add(thumbnailSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 407, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        thumbNailPanelLayout.setVerticalGroup(
            thumbNailPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(thumbNailPanelLayout.createSequentialGroup()
                .add(thumbnailSlide, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 355, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        thumbNailPanel.setBounds(10, 10, 380, 330);
        jLayeredPane2.add(thumbNailPanel, javax.swing.JLayeredPane.PALETTE_LAYER);

        paintPanel1.setBackground(new java.awt.Color(0, 0, 0));
        paintPanel1.setColor(java.awt.Color.red);
        paintPanel1.setPreferredSize(null);
        paintPanel1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                paintPanel1KeyPressed(evt);
            }
        });
        paintPanel1.addMouseWheelListener(new java.awt.event.MouseWheelListener() {
            public void mouseWheelMoved(java.awt.event.MouseWheelEvent evt) {
                paintPanel1MouseWheelMoved(evt);
            }
        });
        paintPanel1.setLayout(new java.awt.BorderLayout());
        paintPanel1.setBounds(0, 0, 2048, 2048);
        jLayeredPane2.add(paintPanel1, javax.swing.JLayeredPane.DEFAULT_LAYER);

        imageLabel.setText("caMicroscope");
        imageLabel.setBounds(0, 0, 2048, 2048);
        jLayeredPane2.add(imageLabel, javax.swing.JLayeredPane.DEFAULT_LAYER);

        jScrollPane2.setViewportView(jLayeredPane2);

        jSplitPane1.setRightComponent(jScrollPane2);

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 688, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(jSplitPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 725, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents


    private void zoomLevelComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomLevelComboActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        double oldZoomLevel = viewpoint.zoomLevel;
        String selectedStr = (String)cb.getSelectedItem();
        String [] selectedSplit = selectedStr.split("x");


        viewpoint.zoomLevel = Double.parseDouble(selectedSplit[0])/viewpoint.DEFAULT_ZOOM_LEVEL;

        if(viewpoint.zoomLevel > 1){
            viewpoint.needOpticalZoom = true;
            viewpoint.opticalZoomScale = viewpoint.zoomLevel;
            System.out.println("zoomLevel >1: new opticaltilesize="+viewpoint.opticalTileSize);

        } else{
            System.out.println("don't need optical zoom");
            viewpoint.needOpticalZoom = false;
            viewpoint.opticalZoomScale = 1;
        }

        setTileSize();

        int halfTileSize=viewpoint.tileSize/2;
       
        int newX = (int) (Math.round(((viewpoint.currentX+halfTileSize)*viewpoint.zoomLevel/oldZoomLevel-halfTileSize)/halfTileSize))*halfTileSize;
        int newY = (int) (Math.round(((viewpoint.currentY+halfTileSize)*viewpoint.zoomLevel/oldZoomLevel-halfTileSize)/halfTileSize))*halfTileSize;


        thumbnailSlide.setXY(thumbnailSlide.translateSlide2Thumb((int)Math.round(newX/ viewpoint.zoomLevel)),thumbnailSlide.translateSlide2Thumb((int) Math.round(newY/viewpoint.zoomLevel)));
        thumbnailSlide.setSelectionBoxSize(thumbnailSlide.translateSlide2Thumb((int) Math.round(viewpoint.tileSize/viewpoint.zoomLevel)));

        viewpoint.currentX = newX;
        viewpoint.currentY = newY;

        viewpoint.opticalX = (int) Math.round(newX/viewpoint.opticalZoomScale);
        viewpoint.opticalY = (int) Math.round(newY/viewpoint.opticalZoomScale);



        System.out.println("AFTER: currentX,Y:"+viewpoint.currentX+","+viewpoint.currentY);

        System.out.println("AFTER: opticalX,Y:"+viewpoint.opticalX+","+viewpoint.opticalY);


        submitRemoteView();
}//GEN-LAST:event_zoomLevelComboActionPerformed

    private void setTileSize(){

        String selectedStr = (String) tileSizeCombo.getSelectedItem();
        int selectedIndex = tileSizeCombo.getSelectedIndex();

                //(String)cb.getSelectedItem();
        String [] selectedSplit = selectedStr.split("x");

        if(selectedIndex == 0){
            viewpoint.tileSize = java.awt.Toolkit.getDefaultToolkit().getScreenSize().width;
        } else {
            viewpoint.tileSize = Integer.parseInt(selectedSplit[0]);
        }


        if(viewpoint.needOpticalZoom)
            viewpoint.opticalTileSize = (int) Math.round(viewpoint.tileSize/viewpoint.zoomLevel);

        thumbnailSlide.setSelectionBoxSize(thumbnailSlide.translateSlide2Thumb((int) Math.round(viewpoint.tileSize/viewpoint.zoomLevel)));

    }

    private void tileSizeComboActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tileSizeComboActionPerformed
        JComboBox cb = (JComboBox)evt.getSource();
        setTileSize();
        submitRemoteView();
}//GEN-LAST:event_tileSizeComboActionPerformed

    private void classComboStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_classComboStateChange
        JComboBox cb = (JComboBox)evt.getSource();
        int classIndex = cb.getSelectedIndex();

        paintPanel1.setClass(classIndex);
    
        String selectedStr = (String)cb.getSelectedItem();

        System.out.println("Selected:"+selectedStr);
     
}//GEN-LAST:event_classComboStateChange

    private void shapeComboStateChange(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_shapeComboStateChange
        JComboBox cb = (JComboBox)evt.getSource();
        int classIndex = cb.getSelectedIndex();
        switch(classIndex){
            case 0:
                paintPanel1.setShape(Shape.FREEHAND);
                break;
            case 1:
                paintPanel1.setShape(Shape.LINE);
                break;
            case 2:
                paintPanel1.setShape(Shape.OVAL);
                break;
        }

        String selectedStr = (String)cb.getSelectedItem();
    }//GEN-LAST:event_shapeComboStateChange


  
    private void loadAnnotations(File file){
  
        FileReader reader = null;
        try {
            reader = new FileReader(file);
        } catch (IOException e1) {
            e1.printStackTrace();
            System.err.println("error");
        }




        paintPanel1.loadAnnotation(reader);

    }


    private void clearAnnotations(){
        System.out.println("Clear Annotations");
        textMarkupPane.setText("");
        paintPanel1.clearShapes();
    }
   

    private void annotationsListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_annotationsListValueChanged

       annotationsListClicks = 0;
       clearAnnotations();

       javax.swing.JList jl = (javax.swing.JList) evt.getSource();
       int i = jl.getSelectedIndex();
       if(i < 0) return;
       if(annotations == null) return;
       if(i > (annotations.length-1)) return;

       System.out.println("i="+i+"length="+annotations.length);
       ImageAnnotation ann = annotations[i];

       paintPanel1.loadAnnotation(ann);
       if(ann.getTextAnnotationCollection() != null)
           textMarkupPane.setText(ann.getTextAnnotationCollection().getTextAnnotation(0).getText());

       submitRemoteView();

    }//GEN-LAST:event_annotationsListValueChanged

    // config dialog button
    private void okButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_okButtonMouseClicked

        configDialog.dispose();
    }//GEN-LAST:event_okButtonMouseClicked

    private synchronized void toggleDraw() {

        if(erasing){
            toggleErase();
            eraseToggle.setSelected(false);
        }

        annotating = !annotating;

        if(annotating){
            paintPanel1.setState(State.ENABLED);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
            paintPanel1.setState(State.IDLE);

        }
        classCombo.setEnabled(annotating);
        shapeCombo.setEnabled(annotating);
    }


    private synchronized void toggleErase() {


        if(annotating){
            toggleDraw();
            drawToggle.setSelected(false);
        }
        erasing = !erasing;
        if(erasing){
            paintPanel1.setState(State.ERASING);
            setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
            paintPanel1.setState(State.IDLE);

        }
    }


    private void saveButtonMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_saveButtonMouseClicked
        System.out.println("saveButtonMouseClicked");

        if( paintPanel1.numSavedShapes == 0){
            System.out.println("warning: no shapes");

        }

        String[] xmlStrings =
        {
            paintPanel1.getAnnotationXml( selectedTreeNodeFileName, authorName.getText(), textMarkupPane.getText())
        };

        AIMv1DataServiceClient        aimclient;

        try {

            System.out.println(paintPanel1.getAnnotationXml( selectedTreeNodeFileName, authorName.getText(), textMarkupPane.getText()));


            aimclient = new AIMv1DataServiceClient((String)annotationServerComboBox.getSelectedItem());
            aimclient.submit(xmlStrings);

        } catch (MalformedURIException ex) {
            java.util.logging.Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            java.util.logging.Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_saveButtonMouseClicked

    private void drawToggleMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_drawToggleMouseReleased
        toggleDraw();
    }//GEN-LAST:event_drawToggleMouseReleased

    private void jButton3MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton3MouseReleased
        try {
            queryPathologyDataServer("images");

        } catch (Exception ex) {
    			System.out.println(ex);

        }
    }//GEN-LAST:event_jButton3MouseReleased

    private void jButton2MouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jButton2MouseReleased

        if(selectedTreeNodeFileName != null)        queryAnnotationServer();
    }//GEN-LAST:event_jButton2MouseReleased


    private void eraseToggleMouseReleased(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_eraseToggleMouseReleased
        toggleErase();
}//GEN-LAST:event_eraseToggleMouseReleased

    private void eraseToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_eraseToggleActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_eraseToggleActionPerformed

    private void drawToggleActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_drawToggleActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_drawToggleActionPerformed

    private void annotationsListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_annotationsListMouseClicked
        System.out.println("AnnotationsList clicked:"+evt.getSource().getClass().getSimpleName());

        annotationsListClicks++;

        if (annotationsListClicks > 1) {
            Point lowP = paintPanel1.findLowShape();


            if (lowP != null) {

                int tmpx = thumbnailSlide.translateSlide2Thumb((int) lowP.getX());
                int tmpy = thumbnailSlide.translateSlide2Thumb((int) lowP.getY());
                thumbnailSlide.setXY(tmpx, tmpy);

                viewpoint.currentX = (int) Math.round(thumbnailSlide.translateThumb2Slide(thumbnailSlide.getThumbX()) * viewpoint.zoomLevel);
                viewpoint.currentY = (int) Math.round(thumbnailSlide.translateThumb2Slide(thumbnailSlide.getThumbY()) * viewpoint.zoomLevel);

            }

            submitRemoteView();

            System.out.println("after findLowShape" + viewpoint.currentX + "," + viewpoint.currentY);
        }
    }//GEN-LAST:event_annotationsListMouseClicked

    private void formMouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_formMouseWheelMoved
        // TODO add your handling code here:
    }//GEN-LAST:event_formMouseWheelMoved

    private void paintPanel1MouseWheelMoved(java.awt.event.MouseWheelEvent evt) {//GEN-FIRST:event_paintPanel1MouseWheelMoved
        int clicks = evt.getWheelRotation();

        clicks *= -1;

        System.out.println("CLICKS"+clicks);

        int newZoomIndex = zoomLevelCombo.getSelectedIndex()+clicks;

       if((newZoomIndex >=0) && (newZoomIndex < zoomLevelCombo.getItemCount())){
           zoomLevelCombo.setSelectedIndex(newZoomIndex);
       }
    }//GEN-LAST:event_paintPanel1MouseWheelMoved

    private void paintPanel1KeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_paintPanel1KeyPressed
        // TODO add your handling code here:
        int key = evt.getKeyCode();
        switch(key){
            case KeyEvent.VK_SPACE:

                System.out.println("SPACE!");
                break;
            case KeyEvent.VK_TAB:
                System.out.println("TAB!");
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                System.out.println("arrow keys");
        }
    }//GEN-LAST:event_paintPanel1KeyPressed

    private void formKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_formKeyPressed

        int key = evt.getKeyCode();
        switch(key){
            case KeyEvent.VK_SPACE:

                System.out.println("SPACE!");
                break;
            case KeyEvent.VK_TAB:
                System.out.println("TAB!");
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                System.out.println("arrow keys");
        }
    }//GEN-LAST:event_formKeyPressed

    private void drawToggleKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_drawToggleKeyPressed
        int key = evt.getKeyCode();
        switch(key){
            case KeyEvent.VK_SPACE:

                System.out.println("SPACE!");
                break;
            case KeyEvent.VK_TAB:
                System.out.println("TAB!");
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                System.out.println("arrow keys");
        }
    }//GEN-LAST:event_drawToggleKeyPressed

    private void drawToggleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_drawToggleKeyReleased

        int key = evt.getKeyCode();
        switch(key){
            case KeyEvent.VK_SPACE:

                toggleDraw();
                break;
            case KeyEvent.VK_TAB:
                System.out.println("TAB!");
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                System.out.println("arrow keys");
        }
    }//GEN-LAST:event_drawToggleKeyReleased

    private void drawToggleKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_drawToggleKeyTyped
        System.out.println("Typed");
    }//GEN-LAST:event_drawToggleKeyTyped

    private void eraseToggleKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_eraseToggleKeyReleased
        int key = evt.getKeyCode();
        switch(key){
            case KeyEvent.VK_SPACE:

                toggleErase();
                break;
            case KeyEvent.VK_TAB:
                System.out.println("TAB!");
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_RIGHT:
                System.out.println("arrow keys");
        }
    }//GEN-LAST:event_eraseToggleKeyReleased

    ImageAnnotation[] getAnnotationList(){
        System.out.println("in get AnnotationList******");


        if(selectedTreeNodeFileName == null) return null;

        // annotations = new CaAnnotation[0];

        annotations = new ImageAnnotation[0];
        AIMv1DataServiceClient aclient;
        try {


            aclient = new AIMv1DataServiceClient((String)annotationServerComboBox.getSelectedItem());
  
            CQLQuery query = new CQLQuery();
                gov.nih.nci.cagrid.cqlquery.Object tar = new gov.nih.nci.cagrid.cqlquery.Object();
            tar.setName("edu.northwestern.radiology.aim.ImageAnnotation");
            gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();

            attr.setName("name");
            attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
            attr.setValue(selectedTreeNodeFileName);

            tar.setAttribute(attr);

            query.setTarget(tar);



            CQLQueryResults results = null;
            CQLQueryResultsIterator iter = null;

            System.out.println("before query");
            if(aclient == null) System.out.println("null client");
            results = aclient.query(query);



           System.out.println("done");

           // this is how to return strings only for testing
           // ---> iter = new CQLQueryResultsIterator(results, true);

          iter = new CQLQueryResultsIterator(results, true);



           if (results == null || iter == null || iter.hasNext() == false) {
               System.out.println("No results");

               //return empty array so .length still works
               return annotations;
           }


           int count=0;

           annotations = new ImageAnnotation[results.getObjectResult().length];

           System.out.println("results:"+results.getObjectResult().length);


           while (iter.hasNext()) {

               String str = (String) iter.next();
               StringReader s = new StringReader(str);

               org.xml.sax.InputSource i = new org.xml.sax.InputSource(s);



               try {
                   System.out.println("loading annotation");
                   annotations[count] = (ImageAnnotation) ObjectDeserializer.deserialize(i, ImageAnnotation.class);

               } catch (DeserializationException ex) {
                    Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
               }

               count++;
           }

        } catch (MalformedURIException ex) {
            java.util.logging.Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (RemoteException ex) {
            java.util.logging.Logger.getLogger(PathologyClient.class.getName()).log(Level.SEVERE, null, ex);
        }

        
        return annotations;
    }


    /**
    * @param args the command line arguments
    */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                clientGUI = new PathologyClient();          
                Dimension dim = java.awt.Toolkit.getDefaultToolkit().getScreenSize();
                clientGUI.setSize(dim);
                clientGUI.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox annotationServerComboBox;
    private javax.swing.JList annotationsList;
    private javax.swing.JScrollPane annotationsScrollPane;
    private java.awt.TextField authorName;
    private javax.swing.JComboBox classCombo;
    private javax.swing.JDialog configDialog;
    private javax.swing.JScrollPane descriptionScrollPane;
    private javax.swing.JToggleButton drawToggle;
    private javax.swing.JToggleButton eraseToggle;
    private javax.swing.JTree fileTree;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JFileChooser jFileChooser1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLayeredPane jLayeredPane2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JButton okButton;
    private gov.nih.nci.ivi.camicroscope.client.PaintPanel paintPanel1;
    private javax.swing.JComboBox pathologyServerComboBox;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JButton saveButton;
    private javax.swing.JComboBox shapeCombo;
    private javax.swing.JEditorPane textMarkupPane;
    private javax.swing.JPanel thumbNailPanel;
    private gov.nih.nci.ivi.camicroscope.client.ThumbnailSlide thumbnailSlide;
    private javax.swing.JComboBox tileSizeCombo;
    private javax.swing.JComboBox zoomLevelCombo;
    // End of variables declaration//GEN-END:variables

    private static final long serialVersionUID = 1L;

    private static PathologyClient clientGUI;

    private boolean thumbnailSlideView = true;
    private boolean needThumbnail = true;
    //private boolean rightPanelView = true;
    private boolean annotating = false;
    private boolean erasing = false;
    private boolean needSubmit = false;

    int annotationsListClicks;

    private String cacheDirName = null;
    private String [] pathologyDataServices = null;
    private String [] staticPathologyDataServices = null;
    private String [] staticAnnotationDataServices = null;

	private JPanel configurationPanel1 = null;


	// buttons
	private JButton buttonIPQuery = null;

	// tables
	private JTable imageTable = null;
	private ImageTableModel imageTableModel = null;
	private FunctionTableModel functionTableModel = null;

	// tree
    private DefaultTreeModel fileTreeModel = null;
    private DefaultMutableTreeNode root = null;

	// image
	private BufferedImage image = null;


	// file types
	private static int NONE = 0;
	private static int SVS = 1;
	private static int NONSVS = 2;
	private static int DIRECTORY = 3;

	// sizes
	int widgetHeight = -1;
	int configurationPanel1Width = -1;
	int configurationPanel2Width = -1;
	int imagePanelWidth = -1;
	int executionPanelWidth = -1;
    int configurationPanel1Height = -1;
    int configurationPanel2Height = -1;
    int imagePanelHeight = -1;
    int executionPanelHeight = -1;
	int scrollPaneWidth = -1;
	int scrollPaneHeight = -1;
	int displayPanelWidth = -1;
	int rightPanelWidth = -1;
	int rightPanelHeight = -1;
	int displayPanelHeight = -1;

	// others
    Viewpoint viewpoint = null;
	static File inputDir = null;

	static boolean remoteViewEnabled = true;
	static String selectedTreeNodeFileName = null;
	static int selectedTreeNodeFileType = NONE;

    ImageAnnotation[] annotations= null;


	private JTree getFileTree() {
        System.out.println("getFileTree");
		if (fileTree == null) {
            System.out.println("file tree null");
			root = new DefaultMutableTreeNode("database");
			fileTreeModel = new DefaultTreeModel(root);
			fileTree = new JTree(fileTreeModel);
            fileTree.setBorder(javax.swing.BorderFactory.createTitledBorder("Slides"));

			FileTreeSelectionListener listener = new FileTreeSelectionListener(fileTree);
			fileTree.getSelectionModel().addTreeSelectionListener(listener);
			fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        		fileTree.setRootVisible(true);
		}

		return fileTree;
	}



	public void queryPathologyDataServer(String fileType) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                progressBar.setIndeterminate(true);
                clientGUI.setEnabled(false);
            }
        });
		final String finalFileType = fileType;
		new SwingWorker() {
            public Object construct() {
            	queryPathologyDataServerAux(finalFileType);
                return null;
            }
            public void finished() {
 //   			if (finalFileType.equals("images") && !TREE_VIEW && imageTable.getRowCount() > 0)
 //   				imageTable.setRowSelectionInterval(0, 0);
 //   			else {
    				progressBar.setIndeterminate(false);
    				//System.out.println("####queryPathologyDataServer: enabling GUI");
        	    	clientGUI.setEnabled(true);
 //   			}
            }
        }.start();
	}

	public PathologyDataServiceClient getPathClient(){
	    PathologyDataServiceClient client = null;
	    System.out.println("in getPathClient()");
		try {
			System.out.println("**Using this service: " + (String)pathologyServerComboBox.getSelectedItem());
			client = new PathologyDataServiceClient((String)pathologyServerComboBox.getSelectedItem());
		} catch (MalformedURIException e) {
			JOptionPane.showMessageDialog(this, "Malformed URI exception, PathologyServer does not exist");
			e.printStackTrace();
			return null;
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Remote exception" + e.getMessage());
			e.printStackTrace();
			return null;
		}
		return client;
	}


	public void queryPathologyDataServerAux(String fileType) {

		if (!fileType.equals("images") && !fileType.equals("codes")) {
			System.out.println("Invalid file type specified.");
			return;
		}

	    PathologyDataServiceClient client = getPathClient();

		// query for image or code files
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.fileinfo.FileInfo");
		gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("fileType");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue(fileType);
		tar.setAttribute(attr);
		query.setTarget(tar);

		CQLQueryResults results = null;
		CQLQueryResultsIterator iter = null;
		try {
			System.out.println("doing query");
			results = client.query(query);
			System.out.println("query returns");
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Remote exception" + e.getMessage());
			e.printStackTrace();
			return;
		}

		iter = new CQLQueryResultsIterator(results);
		int count = 0;
        if (fileType.equals("images")) {
            remoteViewEnabled = false;
            remoteViewEnabled = true;
            fileTree = null;

            clearFileTree();
            if (results == null || iter == null || iter.hasNext() == false) {
                JOptionPane.showMessageDialog(this, "No image files found on the server");
                return;
            }
            System.out.println("non null results");
            while (iter.hasNext()) {
                System.out.println("iter");
                FileInfo fileInfo = (FileInfo) iter.next();
                insertFileNameIntoTree(fileInfo.getName());
                System.out.println(fileInfo.getName());
                count++;
            }
            getFileTree().setRootVisible(true);
            fileTreeModel.nodeStructureChanged(root);
        }

	}

	public void IPFPretrieve(String fileName, String fileType) {
		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                progressBar.setIndeterminate(true);
                System.out.println("####IPFPretrieve: disabling GUI");
                clientGUI.setEnabled(false);
            }
        });
		final String finalFileName = fileName;
		final String finalFileType = fileType;
		new SwingWorker() {
            public Object construct() {
            	IPFPretrieveAux(finalFileName, finalFileType);
		System.out.println("####IPFPretrieveAux swing worker done###");
                return null;
            }
            public void finished() {
                progressBar.setIndeterminate(false);
                clientGUI.setEnabled(true);
                System.out.println("####retreive finished IPFPretrieve: enabling GUI");
            }
        }.start();
	}

	public void IPFPretrieveAux(String fileName, String fileType) {
		System.out.println("IPFRetrieveAux("+fileName+","+fileType+")");
		if (!fileType.equals("images") && !fileType.equals("cache") && !fileType.equals("codes")) {
			System.out.println("Invalid file type specified.");
			return;
		}

		File targetFile = null;
		if (fileType.equals("cache")) {
			System.out.println("cacheDirName:"+cacheDirName);
			File cacheDir = new File(cacheDirName);
			if (!cacheDir.exists())
				cacheDir.mkdirs();
			String targetFileName = cacheDirName + File.separator + replaceColons(replaceSlashes((String)pathologyServerComboBox.getSelectedItem() + "-" + fileName));
			targetFile = new File(targetFileName);
			System.out.println("targetFileName:"+targetFileName);
			if (targetFile.exists()) {

				System.out.println("Already cached, returning without doing transfer.");
				return;

			}
		}
		else {
			System.err.println("unknown file type");
			return;
			// unknown
		}

		PathologyDataServiceClient pathologyDataService = getPathClient();

		// cql query for retrieving image or code files

		System.out.println("creating query for"+fileName);
		final CQLQuery fcqlq = createFilenameEqualQuery(fileName);

		try {
			System.out.println("***Call to pathologyDataService.retrieve");

			TransferServiceContextReference tsc = pathologyDataService.retrieve(fcqlq);
			System.out.println("***start getDataFromTSCR in aux");
			getDataFromTSCR(tsc);
			System.out.println("***done getDataFromTSCR in aux");
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Remote Exception Thrown" + e.getMessage());
			e.printStackTrace();
		}

		File savedFile = null;

		try {
			if (fileName.indexOf("/") == -1)
				savedFile = new File(inputDir.getCanonicalPath() + File.separator + fileName);
			else
				savedFile = new File(inputDir.getCanonicalPath() + File.separator + fileName.substring(fileName.lastIndexOf("/"), fileName.length()));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Remote exception" + e.getMessage());
			e.printStackTrace();
		}

        System.out.println("delete target file:"+targetFile.getAbsolutePath());
		targetFile.delete();
		System.out.println("Renaming from " + savedFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
		if (!savedFile.renameTo(targetFile)){
			System.err.println("File not renamed");
          
			return;
		}
		System.out.println("Finished unzipping");

	}

	public CQLQuery createFilenameEqualQuery(String fileName){
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.fileinfo.FileInfo");
		gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("fileName");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue(fileName);
		tar.setAttribute(attr);
		query.setTarget(tar);
		return query;
	}

	public void getDataFromTSCR(TransferServiceContextReference tscr){
		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			System.out.println("**getDataFromTSCR new TSCC");
			tclient = new TransferServiceContextClient(tscr.getEndpointReference());
			System.out.println("**getDataFromTSCR getData");
			istream = TransferClientHelper.getData(tclient.getDataTransferDescriptor());
			System.out.println("**after getData");
			if(istream == null) {System.err.println("istream null"); return;}
		} catch (MalformedURIException e2) {
			e2.printStackTrace();
		} catch (RemoteException e2) {
			e2.printStackTrace();
		} catch (Exception e) {
			JOptionPane.showMessageDialog(this, "An Exception Thrown" + e.getMessage());
			e.printStackTrace();
		}

		try {
			inputDir = File.createTempFile("PathologyService", null, new File(System.getProperty("java.io.tmpdir")));
			System.out.println("** getDataFromTSCR create tmp file: "+inputDir.getAbsolutePath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			return;
		}
		String path = inputDir.getAbsolutePath();
		System.out.println("delete inputDir :"+inputDir.getAbsolutePath());
		inputDir.delete();
		inputDir = new File(path);
		inputDir.mkdirs();
		String localDownloadLocation = null;
		try {
			localDownloadLocation = inputDir.getCanonicalPath();
			System.out.println("** getDataFromTSCR localDownloadLocation: "+localDownloadLocation);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "IOException 1 thrown when recieving the zip stream" + e.getMessage());
			e.printStackTrace();
		}
		ZipInputStream zis = new ZipInputStream(istream);
		if(zis == null) {System.err.println("zis null"); return;}
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
				if(zeis == null) {System.err.println("zeis null"); return;}
			} catch (EOFException e) {
				System.out.println("EOF");
				break;
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "IOException 2 thrown when recieving the zip stream" + e.getMessage());
				e.printStackTrace();
				return;
			}

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists()){
				System.out.println("creating localLocation "+localDownloadLocation);
				localLocation.mkdirs();
			} else {
				System.out.println("localLocation already exists"+localDownloadLocation);
			}

			String unzzipedFile = localDownloadLocation + File.separator
			+ zeis.getName();
			bis = new BufferedInputStream(zeis);
				if(bis == null) {System.err.println("bis null"); return;}
			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(unzzipedFile));
				if(bos == null) {System.err.println("bos null"); return;}
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				JOptionPane.showMessageDialog(this, "IOException 3 thrown when reading the zip stream" + e.getMessage() + " bytes read:"+ bytesRead);
				e.printStackTrace();
				return;
			}
			System.out.println("^^End while iter");
		}

		try {
			zis.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "IOException 4 thrown when closing the zip stream" + e.getMessage());
			e.printStackTrace();
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Remote exception 5 thrown when closing the transer context" + e.getMessage());
			e.printStackTrace();
		}

	}

    public void moveImage(int x, int y){
        System.out.println("move image");
		if (image == null){
            System.err.println("Image is null");
			return;
        }
		System.out.println("Opening " + image.getWidth() + " by " + image.getHeight() + " image");

        double oScale = 1;

        System.out.println("oScale="+oScale+"viewpoint.zoomLevel="+viewpoint.zoomLevel);

        if(viewpoint.needOpticalZoom){
            System.out.println("need opticalzoom:"+oScale+"tilesize="+viewpoint.opticalTileSize);
            oScale = (int) viewpoint.opticalZoomScale;
        } else { System.out.println("don't need optical zoom");}


		Image imageScaled = image.getScaledInstance(
                (int) Math.round(image.getWidth()*oScale),
                (int)Math.round(image.getHeight()*oScale),
                Image.SCALE_DEFAULT);


        System.out.println("move 3");


       ImageIcon icon = new ImageIcon(imageScaled);
       imageScaled.flush();

       System.out.println("move image 4"+image.getWidth()+","+image.getHeight()+" scale= "+oScale);

		imageLabel.setBounds(-1*x, -1*y,
                 (int)Math.round(image.getWidth()*oScale),
                 (int)Math.round(image.getHeight()*oScale));

		System.out.println("setting icon");


		imageLabel.setIcon(icon);
        repaint();

    }


	public void displayImage(String fileName, int x, int y) {
		System.out.println("*****displayImage:"+x+","+y);
		ImageIcon pic = null;
		File viewedFile = null;
		if (fileName == null) {
			System.err.println("no filename");
			return;
		}
		else {
			String viewedFileName = cacheDirName + File.separator + replaceColons(replaceSlashes((String)pathologyServerComboBox.getSelectedItem() + "-" + fileName));
			viewedFile = new File(viewedFileName);
		}
		System.out.println("displayImage Locally viewing: " + viewedFile.getAbsolutePath());
		if (viewedFile == null) {
			System.out.println("File is null");
			return;
		}
        System.out.println("displayImage 1");
		image = null;
		try {
            System.out.println("starting ImageIO.read");                    
			image  = ImageIO.read(viewedFile);

		} catch (IOException e) {
            System.err.println("can't read image file");
			JOptionPane.showMessageDialog(this, "Cannot read image file, please make sure that the file name is correct.");
			e.printStackTrace();
		}
        System.out.println("displayImage 2");

		if (image == null){
            System.err.println("Image is null");
			return;
        }
		System.out.println("Opening " + image.getWidth() + " by " + image.getHeight() + " image");

        //  Image imageScaled = image;

        double oScale = 1;

        System.out.println("oScale="+oScale+"viewpoint.zoomLevel="+viewpoint.zoomLevel);

        if(viewpoint.needOpticalZoom){
            oScale = (int) viewpoint.opticalZoomScale;
            System.out.println("need opticalzoom:"+oScale+"tilesize="+viewpoint.opticalTileSize);
        } else { System.out.println("don't need optical zoom");}


		Image imageScaled = image.getScaledInstance(
                (int) Math.round(image.getWidth()*oScale),
                (int)Math.round(image.getHeight()*oScale),
                Image.SCALE_DEFAULT);


        System.out.println("displayImage 3");


       ImageIcon icon = new ImageIcon(imageScaled);

       System.out.println("displayImage 4 "+image.getWidth()+","+image.getHeight()+" * "+oScale);


 
		imageLabel.setBounds(0, 0,
                 (int)Math.round(image.getWidth()*oScale),
                 (int)Math.round(image.getHeight()*oScale));

		System.out.println("setting icon");


		imageLabel.setIcon(icon);

		System.out.println("done drawing");

		System.out.println("done DisplayImage");
		tileSizeCombo.setEnabled(true);
		zoomLevelCombo.setEnabled(true);

	}

  


	public String remoteGetThumbnail(String fileName) {
		System.out.println("remoteGetThumbnail("+fileName+")");
		File targetFile = null;

		File cacheDir = new File(cacheDirName);

		if (!cacheDir.exists())
			cacheDir.mkdirs();

        File thumbCacheDir = new File(cacheDirName+File.separator+"thumbnail");

        if (!thumbCacheDir.exists())
			thumbCacheDir.mkdirs();


		String targetFileName = cacheDirName + File.separator + "thumbnail" + File.separator + replaceColons(replaceSlashes((String)pathologyServerComboBox.getSelectedItem() + "-" + fileName));
		targetFile = new File(targetFileName);
		if (targetFile.exists()) {
			System.out.println("Thumbnail"+targetFileName+" already cached, returning without doing transfer.");
			return targetFileName;

		}

		PathologyDataServiceClient pathologyDataService = getPathClient();

		// cql query for retrieving image or code files
		final CQLQuery fcqlq = createFilenameEqualQuery(fileName);
		try {
			System.out.println("***Call to pathologyDataService.retrieveThumbnail"+	fcqlq.getTarget());
			TransferServiceContextReference tsc = pathologyDataService.retrieveThumbnail(fcqlq);
			System.out.println("***thumb getDataFromTSCR***");
			getDataFromTSCR(tsc);
			System.out.println("***thumb after getDataFromTSCR***");
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, "Remote Exception Thrown" + e.getMessage());
			e.printStackTrace();
		}

		File savedFile = null;

		try {
			if (fileName.indexOf("/") == -1)
				savedFile = new File(inputDir.getCanonicalPath() + File.separator + fileName);
			else
				savedFile = new File(inputDir.getCanonicalPath() + File.separator + fileName.substring(fileName.lastIndexOf("/"), fileName.length()));
		} catch (IOException e) {
			JOptionPane.showMessageDialog(this, "Remote exception" + e.getMessage());
			e.printStackTrace();
		}
		System.out.println("nail delete target file:"+targetFile.getAbsolutePath());
		targetFile.delete();
		System.out.println("nail Renaming thumbnail from " + savedFile.getAbsolutePath() + " to " + targetFile.getAbsolutePath());
		if (!savedFile.renameTo(targetFile)){
			System.err.println("File not renamed");
			return null;
		}

		System.out.println("thumbnail Finished unzipping");

		return targetFileName;

	}



	public String replaceSlashes(String str) {
		return str.replace('/', '-');
	}

	public String replaceColons(String str) {
		return str.replace(':', '-');
	}


	public String extractFileName(String s) {
		int lastIndex = s.lastIndexOf(File.separatorChar);
		if (lastIndex == -1)
			return new String(s);
		else
			return new String(s.substring(lastIndex+1));
	}


	public JButton createGenericButton(String text, int width, int height) {
		JButton genericButton = new JButton(text);
		genericButton.setPreferredSize(new Dimension(width, height));

		return genericButton;
	}

	private void insertFileNameIntoTree(String fileName) {
		boolean found = false;
		String token = null;
		DefaultMutableTreeNode currentNode = root;
		StringTokenizer tokenizer = new StringTokenizer(fileName, "/\\");
		while (tokenizer.hasMoreTokens()) {
			token = tokenizer.nextToken();
			System.out.println("token:" + token);
			Enumeration children = currentNode.children();
			found = false;
			DefaultMutableTreeNode childNode = null;
			while (children.hasMoreElements()) {
				childNode = (DefaultMutableTreeNode) children.nextElement();
				if (token.equals((String)childNode.getUserObject())) {
					found = true;
					break;
				}
			}
			if (found) {
				currentNode = childNode;
				continue;
			}
			else
				break;
		}
		if (!found) {
			do {
				DefaultMutableTreeNode newChild = new DefaultMutableTreeNode(token);
				currentNode.add(newChild);
				currentNode = newChild;
				if (tokenizer.hasMoreTokens())
					token = tokenizer.nextToken();
			} while (tokenizer.hasMoreTokens());
		}
	}


	private void clearFileTree() {
		System.out.println("Clear file tree ");
        fileTree = null;
		getFileTree();
		jScrollPane1.setViewportView(getFileTree());
		selectedTreeNodeFileName = null;
		selectedTreeNodeFileType = NONE;
	}


	public void submitRemoteView(){
        if(viewpoint.remoteViewPending){
            System.err.println("remoteView already pending!");
            return;
        }
        viewpoint.remoteViewPending = true;


        setAllEnabled(false);

    	PathologyDataServiceClient client = getPathClient();
		int slideWidth = 0, slideHeight = 0;


		String selectedImageName = selectedTreeNodeFileName;
		//System.out.println("selectedTreeNodeFileName"+selectedImageName+".metadata");
		CQLQuery query = createFilenameEqualQuery(selectedImageName+".metadata");


		try {
			slideWidth = client.getWidth(query);
			slideHeight = client.getHeight(query);

		//	System.out.println("* * * "+slideWidth+","+slideHeight);

		} catch (RemoteException e) {
			e.printStackTrace();
		}

        //System.out.println("thumbnailSlide set slide size");
    	thumbnailSlide.setSlideSize(slideWidth, slideHeight);

        viewpoint.setSlideSize(slideWidth, slideHeight);
        //viewpoint.currentX = (int) Math.round(thumbnailSlide.translateThumb2Slide(thumbnailSlide.getThumbX())*viewpoint.zoomLevel);
		//viewpoint.currentY = (int) Math.round(thumbnailSlide.translateThumb2Slide(thumbnailSlide.getThumbY())*viewpoint.zoomLevel);

        System.out.println("* * * *selectedImageName:"+selectedImageName+":viewpoint:"+viewpoint.currentX+","+viewpoint.currentY +"* * **************************************");

		remoteView(SwingConstants.CENTER);

        if(viewpoint.annotationsListNeeded){
            queryAnnotationServer();
            viewpoint.annotationsListNeeded = false;
        }

        needSubmit = false;

    }

    public void queryAnnotationServer(){
        annotationsList.setModel(new javax.swing.AbstractListModel() {
            ImageAnnotation[] annotations =      getAnnotationList();
            public int getSize() { return annotations.length; }
            public Object getElementAt(int i) { return annotations[i].getName()
                    +":"+ annotations[i].getUser().getUser().getLoginName() + ":"+ annotations[i].getId();}
        });
    }

    public void setAllEnabled(boolean val){

        /*
        if(val == false){
            System.out.println("Set wait cursor");
        } else {
            System.out.println("Set default cursor");

            //setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
         */


        progressBar.setIndeterminate(!val);
        clientGUI.setEnabled(val);
        annotationsList.setEnabled(val);
        fileTree.setEnabled(val);
        saveButton.setEnabled(val);
        drawToggle.setEnabled(val);
        eraseToggle.setEnabled(val);
        textMarkupPane.setEnabled(val);
        zoomLevelCombo.setEnabled(val);
    }

	public void remoteView(int direction) {

		SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                setAllEnabled(false);

            }
        });
		final int finalDirection = direction;
		new SwingWorker() {
            public Object construct() {
                // BROKEN TILE STUFF:   viewRemoteTile();
                System.out.println("#####RemoteView#####");
            	remoteViewAux(finalDirection);
                System.out.println("#####worker after remoteViewAux");
                return null;
            }
            @Override
            public void finished() {
                thumbnailSlide.repaint();
                setAllEnabled(true);

                System.out.println("#####finished remoteView: enabling GUI");
                viewpoint.remoteViewPending = false;
            }
        }.start();
	}

    public void viewRemoteTile() {
        int subTileSize = 512;
        int subTilesPerSide = viewpoint.tileSize / subTileSize;
        for (int sh = 0; sh < subTilesPerSide; sh++) {
            for (int sv = 0; sv < subTilesPerSide; sv++) {
                TileFetcherThread t= new TileFetcherThread(sh, sv, selectedTreeNodeFileName);
                t.start();
        
            }
        }
        System.out.println("Done viewRemoteTile__________");
    }

   

	public void remoteViewAux(int direction) {
		String selectedImageName = null;

        if (selectedTreeNodeFileType != SVS)   return;

        selectedImageName = selectedTreeNodeFileName;

		System.out.println("Remote viewing image: " + selectedImageName);
		ViewParameters viewParameters = new ViewParameters();
		viewParameters.setImageFileName(selectedImageName);
		if (direction == SwingConstants.WEST){
            viewpoint.currentX -= viewpoint.tileSize/2;
        }
		else if (direction == SwingConstants.EAST){
			viewpoint.currentX += viewpoint.tileSize/2;
            viewpoint.currentX += viewpoint.tileSize/2;

        }
		else if (direction == SwingConstants.NORTH){
            viewpoint.currentY -= viewpoint.tileSize/2;

        }
		else if (direction == SwingConstants.SOUTH){
            viewpoint.currentY += viewpoint.tileSize/2;

        }

		int imageWidth = viewpoint.tileSize;

        if (viewpoint.zoomLevel > 1) {
            //imageWidth = (int)(imageWidth*viewpoint.zoomLevel);
            viewParameters.setZoom(1.0);
            viewParameters.setX(viewpoint.opticalX);
            viewParameters.setY(viewpoint.opticalY);
            viewParameters.setWidth(viewpoint.opticalTileSize);
            viewParameters.setHeight(viewpoint.opticalTileSize);
        }
        else {
            viewParameters.setX(viewpoint.currentX);
            viewParameters.setY(viewpoint.currentY);
            viewParameters.setZoom(viewpoint.zoomLevel);
            viewParameters.setWidth(imageWidth);
            viewParameters.setHeight(imageWidth);
        }




		PathologyDataServiceClient pathClient = getPathClient();

		int result = 0;
		try {
			System.out.println("!!!!pathClient.view COMMAND"+viewParameters+":"+pathClient);

			result = pathClient.view(viewParameters);
			System.out.println("!!!!pathClient.view COMMAND done");
		} catch (RemoteException e) {
			JOptionPane.showMessageDialog(this, e.getMessage());
			e.printStackTrace();
		}
		if (result != 0) {
			JOptionPane.showMessageDialog(this, "An error occurred while viewing the image.");
			return;
		}


		String viewedImage = selectedImageName + "-" + viewParameters.getX() + "-" + viewParameters.getY() + "-" + viewParameters.getZoom() + ".jpg";
		System.out.println("***before ipfpretrieveAux");
		IPFPretrieveAux(viewedImage, "cache");

        System.out.println("after ipfpretrieveAux before thumbs");
		if(needThumbnail == true){

            thumbnailSlide.setImage(remoteGetThumbnail(selectedImageName));
            thumbnailSlide.setSelectionBoxSize(thumbnailSlide.translateSlide2Thumb((int) Math.round(viewpoint.tileSize/viewpoint.zoomLevel)));
            thumbnailSlide.setAvailable(true);
            needThumbnail = false;
		}

        System.out.println("*$$$after IPFPretrieveAux, before displayImage");

		displayImage(viewedImage, viewParameters.getX(), viewParameters.getY());
        System.out.println("---after displayImage, need tn?:"+needThumbnail+":"+selectedImageName);

	}

    private String formFileName(TreePath path) {
    	Object [] nodes = path.getPath();
    	String fileName = "";
    	if (path.getPathCount() > 1) {
	    	fileName = (String) ((DefaultMutableTreeNode)nodes[1]).getUserObject();
	    	for (int i = 2; i < nodes.length; i++)
	    		fileName += "/" + (String) ((DefaultMutableTreeNode)nodes[i]).getUserObject();
    	}
    	return fileName;
    }

	public JComboBox createGenericComboBox(Object [] objects, int width, int height) {
		JComboBox genericComboBox = new JComboBox(objects);
		genericComboBox.setPreferredSize(new Dimension(width, height));

		return genericComboBox;
	}



    private JMenuBar createMenuBar(){
     	JMenuBar mb = new JMenuBar();
    	JMenu fileMenu = new JMenu("File");
    	JMenu viewMenu = new JMenu("View");


        fileMenu.add(new AbstractAction("Properties") {
            public void actionPerformed(ActionEvent evt) {
                    try {
                        configDialog.setSize(450,450);
                        configDialog.setVisible(true);
                    } catch (Exception ex) {
                            System.out.println(ex);
                    }
            }
    });

    mb.add(fileMenu);

    fileMenu.add(new AbstractAction("Reload Slide List") {
    	public void actionPerformed(ActionEvent evt) {
    		try {
    			queryPathologyDataServer("images");
    		} catch (Exception ex) {
    			System.out.println(ex);
    		}
    	}
    });
    mb.add(fileMenu);


    fileMenu.add(new AbstractAction("Upload .SVS file") {
    	public void actionPerformed(ActionEvent evt) {
    		try {
                int returnVal = jFileChooser1.showOpenDialog(clientGUI);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser1.getSelectedFile();
                    JOptionPane.showMessageDialog(clientGUI, "not yet available on server");

                } else {
                    JOptionPane.showMessageDialog(clientGUI, "error saving file");
                }

            } catch (Exception ex) {
    			System.out.println(ex);
    		}
    	}
    });
    mb.add(fileMenu);

    fileMenu.add(new AbstractAction("Save AIM") {
    	public void actionPerformed(ActionEvent evt) {
    		try {
                int returnVal = jFileChooser1.showSaveDialog(clientGUI);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser1.getSelectedFile();
                    paintPanel1.saveAnnotations(file, selectedTreeNodeFileName, authorName.getText(), textMarkupPane.getText());


                } else {
                    JOptionPane.showMessageDialog(clientGUI, "error saving file");
                }

            } catch (Exception ex) {
    			System.out.println(ex);
    		}
    	}
    });
    mb.add(fileMenu);

        fileMenu.add(new AbstractAction("Load AIM") {
    	public void actionPerformed(ActionEvent evt) {
    		try {
                int returnVal = jFileChooser1.showOpenDialog(clientGUI);

                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = jFileChooser1.getSelectedFile();
                    loadAnnotations(file);

                } else {
                    JOptionPane.showMessageDialog(clientGUI, "error saving file");
                }
            } catch (Exception ex) {
    			System.out.println(ex);
    		}
    	}
    });
    mb.add(fileMenu);

    fileMenu.add(new AbstractAction("Quit") {
    	public void actionPerformed(ActionEvent evt) {
    		try {
    			System.exit(0);
    		} catch (Exception ex) {
    			System.out.println(ex);
    		}
    	}
    });
    mb.add(fileMenu);


    JCheckBoxMenuItem item=null;
    viewMenu.add(item=new JCheckBoxMenuItem("Thumbnail"));
    item.setSelected(true);
    item.addActionListener(new AbstractAction("Thumbnail") {
    	public void actionPerformed(ActionEvent evt) {
    		if(thumbnailSlideView){
    			thumbnailSlideView = false;
    			thumbnailSlide.setVisible(false);
    			repaint();

    		} else {
    			thumbnailSlideView = true;
    			thumbnailSlide.setVisible(true);
    			thumbnailSlide.repaint();
    		}

    	}
    });

    mb.add(viewMenu);
    

    return mb;
    }

//////////////////



	class FileTreeSelectionListener implements TreeSelectionListener {
	    JTree tree;

	    // It is necessary to keep the table since it is not possible
	    // to determine the table from the event's source
	    FileTreeSelectionListener(JTree tree) {
	        this.tree = tree;
	    }

		public void valueChanged(TreeSelectionEvent e) {
			System.out.println("FileTree Image changed");

            needSubmit = true;
            viewpoint.annotationsListNeeded = true;
            clearAnnotations();

	        if (e.getSource() == tree.getSelectionModel()) {
	        	TreePath path = e.getNewLeadSelectionPath();
	        	DefaultMutableTreeNode lastNode = (DefaultMutableTreeNode)path.getLastPathComponent();
	        	if (lastNode.isLeaf()) {
                    System.out.println("is leaf");
	        		selectedTreeNodeFileName = formFileName(path);
	        		selectedTreeNodeFileType = SVS;
		        	System.out.println(selectedTreeNodeFileName);
                    zoomLevelCombo.setSelectedIndex(0);
                    thumbnailSlide.setXY(0,0);
                    
		        	viewpoint.currentX = 0;
		        	viewpoint.currentY = 0;

                    System.out.println("set needThumbnail------");
		    		needThumbnail = true;


                    submitRemoteView();

		        	//remoteView(SwingConstants.CENTER);
	        	}
	        	else {
	        		selectedTreeNodeFileName = null;
	        		if (!lastNode.isLeaf())
	        			selectedTreeNodeFileType = DIRECTORY;
	        		else
	        			selectedTreeNodeFileType = NONSVS;

	        		tileSizeCombo.setEnabled(false);
	        		zoomLevelCombo.setEnabled(false);
	        	}
	        }
		}

	}


	public class ImageTableModel extends DefaultTableModel {

    	private static final long serialVersionUID = 1L;

    	public ImageTableModel() {
    		super();
    	}

    	public String getColumnName(int columnIndex) {
    		String [] columnNames = {"Image path", "Available images"};
    		return columnNames[columnIndex];
    	}

    	public boolean isCellEditable(int rowIndex, int columnIndex) {
    		return false;
    	}
    }


	public class FunctionTableModel extends DefaultTableModel {

    	private static final long serialVersionUID = 1L;

    	public FunctionTableModel() {
    		super();
    	}

    	public String getColumnName(int columnIndex) {
    		String [] columnNames = {"", "Available functions"};
    		return columnNames[columnIndex];
    	}

    	public boolean isCellEditable(int rowIndex, int columnIndex) {
    		return false;
    	}
    }

	public class ExitListener extends WindowAdapter {
		public void windowClosing(WindowEvent event) {
			System.exit(0);
		}
	}


    private int X, Y, X1, Y1, X2, Y2;


    public void mouseReleased(MouseEvent event){

            System.out.println("@@@@@@@@@@@@@@@@@@@mouse released in pathclient@@@");
            if(event.getComponent() == paintPanel1){

                if(annotating) return;
                if(erasing) return;

                if(event.getClickCount() ==2){  // double click for zoom and recenter
                    System.out.println("DOUBLE CLICK//////////////////////////////////////////");
                    if((zoomLevelCombo.getSelectedIndex()+1) < zoomLevelCombo.getItemCount()){
                        thumbnailSlide.changeSelectionBox();
                        viewpoint.currentX = viewpoint.translateSlide2Screen(viewpoint.translateScreen2Slide(event.getX())+viewpoint.translateScreen2Slide(viewpoint.currentX))-(viewpoint.tileSize/2);
                        viewpoint.currentY = viewpoint.translateSlide2Screen(viewpoint.translateScreen2Slide(event.getY())+viewpoint.translateScreen2Slide(viewpoint.currentY))-(viewpoint.tileSize/2);

                        zoomLevelCombo.setSelectedIndex(zoomLevelCombo.getSelectedIndex()+1);
                    }
                    return;
                }

                else {
                    if (needSubmit) {
                        System.out.println("NEED SUBMIT!");
                        viewpoint.currentX += X2;
                        viewpoint.currentY += Y2;

                        // optical
                        viewpoint.opticalX += Math.round(X2/viewpoint.opticalZoomScale);
                        viewpoint.opticalY += Math.round(Y2/viewpoint.opticalZoomScale);
                        setAllEnabled(false);
                        submitRemoteView();
                    }
                }

            } else if(event.getComponent() == thumbnailSlide){

                        viewpoint.currentX += X2;
                        viewpoint.currentY += Y2;

                        // optical
                        viewpoint.opticalX += X2;
                        viewpoint.opticalY += Y2;

                        setAllEnabled(false);

                        submitRemoteView();
            } else {
                System.out.println("OTHER");
            }


            thumbnailSlide.setXY(
                    thumbnailSlide.translateSlide2Thumb((int)((viewpoint.currentX)/viewpoint.zoomLevel)),
                    thumbnailSlide.translateSlide2Thumb((int)((viewpoint.currentY)/viewpoint.zoomLevel)));

            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));


        }

		public void mouseEntered(MouseEvent event){}
		public void mouseExited(MouseEvent event){}
		public void mouseClicked(MouseEvent event){
		}

		public void mousePressed(MouseEvent event){

            System.out.println("container pressed");
            if ((!annotating) && (!erasing)) {

                X1 = X = (int) event.getPoint().getX();
                Y1 = Y = (int) event.getPoint().getY();
                X2 = 0;
                Y2 = 0;
            }
        }

		public void mouseDragged(MouseEvent event){
			System.out.println("path container mouse dragged");

            if((!annotating) && (!erasing)){


                X2 = (int) event.getPoint().getX() - X;
                Y2 = (int) event.getPoint().getY() - Y;


                System.out.println("****X2:"+X2);
                System.out.println("****Y2:"+Y2);

                if(event.getComponent() == thumbnailSlide){

                     int tmpx = (int) event.getPoint().getX() - X1;
                     int tmpy = (int) event.getPoint().getY() - Y1;

                     X1 = tmpx+X1;
                     Y1 = tmpy+Y1;
                     thumbnailSlide.moveXY(tmpx, tmpy);

                } else {

                    moveImage(X2,Y2);
                    needSubmit = true;
                    setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

                    int x22 = 0;
                    int y22 = 0;

                    if(viewpoint.needOpticalZoom){

                        x22 = thumbnailSlide.translateSlide2Thumb((int)((viewpoint.opticalX+X2))); //viewpoint.opticalZoomScale));
                        y22 = thumbnailSlide.translateSlide2Thumb((int)((viewpoint.opticalY+Y2)));//viewpoint.opticalZoomScale));
                                                                    System.out.println("need setxy"+x22+","+y22+" opticalX"+X+"X2"+X2);

                    } else {


                        x22 = thumbnailSlide.translateSlide2Thumb((int)((viewpoint.currentX+X2)/viewpoint.zoomLevel));
                        y22 = thumbnailSlide.translateSlide2Thumb((int)((viewpoint.currentY+Y2)/viewpoint.zoomLevel));
                                                System.out.println("don't need setxy"+x22+","+y22);

                    }
                    System.out.println("setxy"+x22+","+y22);
                    thumbnailSlide.setXY(x22,y22);
                }
                
            }
		}



	public void mouseMoved(MouseEvent event) {
        if(annotating) return;

        int X2, Y2;
        if(event.getButton() == 0){
	    	return;
	    }
	    repaint();
	  }


    public class TileFetcherThread extends Thread{
        int sh, sv = 0;
        int subTileSize = 512;
        int subTilesPerSide = viewpoint.tileSize / subTileSize;
        String selectedImageName;
        ViewParameters viewParameters[][] = new ViewParameters[subTilesPerSide][subTilesPerSide];

       /* public TileFetcherThread() {
            Count = 0;
        }*/

        public TileFetcherThread(int h, int v, String selectedTreeNodeFileName) {
            sh=h;
            sv=v;


            System.out.println("viewRemoteTile subtilesperside=" + subTilesPerSide);
            selectedImageName = selectedTreeNodeFileName;

        }

        @Override
        public void run() {
                viewParameters[sh][sv] = new ViewParameters();
                viewParameters[sh][sv].setImageFileName(selectedImageName);

                // is this subtilesize wrong because of zoom?????????????
                viewParameters[sh][sv].setX(viewpoint.currentX + sh * subTileSize);
                viewParameters[sh][sv].setY(viewpoint.currentY + sv * subTileSize);
                int imageWidth = subTileSize;

                if (viewpoint.zoomLevel > 1) {
                    imageWidth = (int) (imageWidth * viewpoint.zoomLevel);
                }
                viewParameters[sh][sv].setWidth(imageWidth);
                viewParameters[sh][sv].setHeight(imageWidth);
                viewParameters[sh][sv].setZoom(viewpoint.zoomLevel);

                PathologyDataServiceClient pathClient = getPathClient();

                int result = 0;
                try {

                    result = pathClient.view(viewParameters[sh][sv]);
                } catch (RemoteException e) {
                    JOptionPane.showMessageDialog(clientGUI, e.getMessage());
                    e.printStackTrace();
                }
                if (result != 0) {
                    JOptionPane.showMessageDialog(clientGUI, "An error occurred while viewing the image.");
                    return;
                }

                String viewedImage = selectedImageName + "-" + viewParameters[sh][sv].getX() + "-" + viewParameters[sh][sv].getY() + "-" + viewParameters[sh][sv].getZoom() + ".jpg";
                IPFPretrieveAux(viewedImage, "cache");

                if (needThumbnail == true) {

                    thumbnailSlide.setImage(remoteGetThumbnail(selectedImageName));
                    thumbnailSlide.setSelectionBoxSize(thumbnailSlide.translateSlide2Thumb((int) Math.round(viewpoint.tileSize / viewpoint.zoomLevel)));
                    thumbnailSlide.setAvailable(true);
                    needThumbnail = false;
                }


                displayImage(viewedImage, sh*subTileSize, sv*subTileSize);

        }

    }


}
