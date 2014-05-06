/**
 *
 */
package gov.nih.nci.ivi.dicom;

//import edu.emory.cci.security.auth.AuthPermission;
//import edu.emory.cci.security.auth.Authorization;
import edu.osu.bmi.security.authorization.DataAuthorization;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperUtil;
import gov.nih.nci.ivi.dicom.embeddedpacs.EmbeddedPACS;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ivi.utils.Zipper;
import gov.nih.nci.ncia.domain.Patient;
import gov.nih.nci.ncia.domain.Series;
import gov.nih.nci.ncia.domain.Study;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pixelmed.dicom.AgeStringAttribute;
import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DecimalStringAttribute;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.IntegerStringAttribute;
import com.pixelmed.dicom.LongStringAttribute;
import com.pixelmed.dicom.SOPClass;
import com.pixelmed.dicom.ShortStringAttribute;
import com.pixelmed.dicom.SpecificCharacterSet;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.dicom.UniqueIdentifierAttribute;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.FindSOPClassSCU;
import com.pixelmed.network.GetSOPClassSCU;
import com.pixelmed.network.IdentifierHandler;
import com.pixelmed.network.MoveSOPClassSCU;
import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.query.QueryInformationModel;
import com.pixelmed.query.QueryTreeModel;
import com.pixelmed.query.StudyRootQueryInformationModel;

/**
 * @author tpan
 * 
 */

public class PixelMedHelper {
    private static Logger myLogger = LogManager.getLogger(PixelMedHelper.class);

    public static final String SERVER_AE = "serverAE";

    private String serverip;
    private String serverport;
    private String serverAE;
    private String clientAE;
    private String tempdir;
    private String useCMOVE;
    private String embeddedPacsAE;
    // private String embeddedPacsPort;

    private QueryInformationModel cachedQueryInfoModel;

    private static Properties propertiesParams = null;

    private Properties currentParams = null;

    private MyReceivedObjectHandler handler = null;
    private boolean cGetComplete = false;
    private boolean isWSEnum = false;

    // private Vector<String> retrieved_files;
    // private HashSet<String> retrieved_fileset;
    ArrayList<String> filenames = null;
    private boolean dataLevelAuthorizationOn;
    private String org;
    private String datasource;

    // private String sopClassForQRServerAE;

    private String cfindQR_SOPClass;

    private String cgetQR_SOPClass;

    private String cmoveQR_SOPClass;

    public static Properties getParameters() {
        if (propertiesParams == null) {
            propertiesParams = new Properties();
            propertiesParams.setProperty("serverip", "127.0.0.1");
            propertiesParams.setProperty("serverport", "4008");
            propertiesParams.setProperty(SERVER_AE, "RIDERPACS1");
            propertiesParams.setProperty("clientAE", "DICOMDATASERVICE");
            propertiesParams.setProperty("useCMOVE", "false");
            propertiesParams.setProperty("embeddedPacsAE", "EMBEDDEDPACS");
            propertiesParams.setProperty("embeddedPacsPort", "4010");
            propertiesParams.setProperty("tempdir", "CHANGE_ME:   " + System.getProperty("java.io.tmpdir"));
            propertiesParams.setProperty("sopClassForQRServerAE", "CHANGE_ME:   Study");
            /*
             * propertiesParams.setProperty("serverip", "CHANGE_ME:   127.0.0.1"
             * ); propertiesParams.setProperty("serverport",
             * "CHANGE_ME:   4008"); propertiesParams.setProperty("serverAE",
             * "CHANGE_ME:   RIDERPACS1");
             * propertiesParams.setProperty("clientAE",
             * "CHANGE_ME:   DICOMDATASERVICE");
             * propertiesParams.setProperty("useCMOVE", "CHANGE_ME:   false");
             * propertiesParams.setProperty("embeddedPacsAE",
             * "CHANGE ME:   EMBEDDEDPACS");
             * propertiesParams.setProperty("embeddedPacsPort",
             * "CHANGE ME:   4010"); propertiesParams.setProperty("tempdir",
             * "CHANGE_ME:   "+System.getProperty("java.io.tmpdir"));
             */
        }
        return propertiesParams;
    }

    public PixelMedHelper(Properties configuration) {
        currentParams = configuration;
        serverip = currentParams.getProperty("serverip");
        serverport = currentParams.getProperty("serverport");
        serverAE = currentParams.getProperty(SERVER_AE);
        clientAE = currentParams.getProperty("clientAE");
        tempdir = currentParams.getProperty("tempdir");
        useCMOVE = currentParams.getProperty("useCMOVE");
        embeddedPacsAE = currentParams.getProperty("embeddedPacsAE");
        // embeddedPacsPort = currentParams.getProperty("embeddedPacsPort");

        setSOPClassForQRServerAE(currentParams.getProperty("sopClassForQRServerAE"));

        this.dataLevelAuthorizationOn = Boolean.parseBoolean(currentParams.getProperty("dataLevelAuth"));
        if (dataLevelAuthorizationOn) {
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Data level authorization is on.");
            }
            // get pacs AE title

            org = currentParams.getProperty("organization");
            datasource = currentParams.getProperty(SERVER_AE);
        }

        // retrieved_files = new Vector<String>();
        // retrieved_fileset = new HashSet<String>();
        filenames = new ArrayList<String>();
        handler = new MyReceivedObjectHandler(filenames, null);
    }

    /*
     * // Query-Retrieve SOP Classes ...
     * 
     * public static final String StudyRootQueryRetrieveInformationModelFind =
     * "1.2.840.10008.5.1.4.1.2.2.1"; public static final String
     * StudyRootQueryRetrieveInformationModelMove =
     * "1.2.840.10008.5.1.4.1.2.2.2"; public static final String
     * StudyRootQueryRetrieveInformationModelGet =
     * "1.2.840.10008.5.1.4.1.2.2.3"; public static final String
     * PatientRootQueryRetrieveInformationModelFind =
     * "1.2.840.10008.5.1.4.1.2.1.1"; public static final String
     * PatientRootQueryRetrieveInformationModelMove =
     * "1.2.840.10008.5.1.4.1.2.1.2"; public static final String
     * PatientRootQueryRetrieveInformationModelGet =
     * "1.2.840.10008.5.1.4.1.2.1.3"; public static final String
     * PatientStudyOnlyQueryRetrieveInformationModelFind =
     * "1.2.840.10008.5.1.4.1.2.3.1"; public static final String
     * PatientStudyOnlyQueryRetrieveInformationModelMove =
     * "1.2.840.10008.5.1.4.1.2.3.2"; public static final String
     * PatientStudyOnlyQueryRetrieveInformationModelGet =
     * "1.2.840.10008.5.1.4.1.2.3.3";
     */

    private void setSOPClassForQRServerAE(String property) {
        if (property.equalsIgnoreCase("Patient")) {
            cfindQR_SOPClass = SOPClass.PatientRootQueryRetrieveInformationModelFind;
            cgetQR_SOPClass = SOPClass.PatientRootQueryRetrieveInformationModelGet;
            cmoveQR_SOPClass = SOPClass.PatientRootQueryRetrieveInformationModelMove;
        } else if (property.equalsIgnoreCase("Study")) {
            cfindQR_SOPClass = SOPClass.StudyRootQueryRetrieveInformationModelFind;
            cgetQR_SOPClass = SOPClass.StudyRootQueryRetrieveInformationModelGet;
            cmoveQR_SOPClass = SOPClass.StudyRootQueryRetrieveInformationModelMove;
        } else if (property.equalsIgnoreCase("PatientStudy")) {
            cfindQR_SOPClass = SOPClass.PatientStudyOnlyQueryRetrieveInformationModelFind;
            cgetQR_SOPClass = SOPClass.PatientStudyOnlyQueryRetrieveInformationModelGet;
            cmoveQR_SOPClass = SOPClass.PatientStudyOnlyQueryRetrieveInformationModelMove;
        } else {
            myLogger.debug("SOPClasses for Query/Retrieve Server Application Entity are not properly identified\n"
                    + "Permissible values are: Patient, Study, PatientStudy\n"
                    + "Please refer to the DICOM Conformance statement of the DICOMDataService and your PACS"
                    + "This will default to StudyRootQueryRetrieveInformationModel");

            cfindQR_SOPClass = SOPClass.StudyRootQueryRetrieveInformationModelFind;
            cgetQR_SOPClass = SOPClass.StudyRootQueryRetrieveInformationModelGet;
            cmoveQR_SOPClass = SOPClass.StudyRootQueryRetrieveInformationModelMove;
        }
    }

    public QueryInformationModel getQueryInformationModel() {
        if (cachedQueryInfoModel == null) {
            cachedQueryInfoModel = new StudyRootQueryInformationModel(serverip, Integer.parseInt(serverport), serverAE,
                    clientAE, 0);
        }
        return cachedQueryInfoModel;
    }

//    private Set<AuthPermission> authorize(Authorization auth, AttributeList filter, String user,
//            AuthPermission permission) {
    private int authorize(DataAuthorization auth, AttributeList filter, String user, int permission) {
        String[] UIDs = new String[3];

        AttributeTag t = TagFromName.PatientID;
        Attribute a = filter.get(t);
        if (a == null) {
            UIDs[0] = null;
        } else {
            UIDs[0] = a.getSingleStringValueOrNull();
        }

        t = TagFromName.StudyInstanceUID;
        a = filter.get(t);
        if (a == null) {
            UIDs[1] = null;
        } else {
            UIDs[1] = a.getSingleStringValueOrNull();
        }

        t = TagFromName.SeriesInstanceUID;
        a = filter.get(t);
        if (a == null) {
            UIDs[2] = null;
        } else {
            UIDs[2] = a.getSingleStringValueOrNull();
        }

        // now get the user ID
        // now check to see if user has permission.
//        String resourceName = UIDs[0] + "!" + UIDs[1] + "!" + UIDs[2];
//        return auth.authorize(user, resourceName, permission);
        return auth.authorize(user, GridGrouperUtil.toStem(DICOMCQLQueryProcessor.DATA_STEM, org, datasource, UIDs),
                permission);
    }

//    public List<?> queryFindSecure(AttributeList filter, ModelMap map, String retrieveLevel, String user,
//            Authorization localAuth) {
    public List<?> queryFindSecure(AttributeList filter, ModelMap map, String retrieveLevel, String user,
            DataAuthorization localAuth) {

        if (myLogger.isDebugEnabled()) {
            myLogger.debug("queryFindSecure: " + filter.toString());
        }

        MySecureIdentifierHandler dicomFindIdentifierHandler = new MySecureIdentifierHandler(map, retrieveLevel, user,
                localAuth);
        myLogger.debug("MySecureIdentifierHandler created.");
        try {
            new MyFindSOPClassSCU(serverip, Integer.parseInt(serverport), serverAE, clientAE, cfindQR_SOPClass, filter,
                    dicomFindIdentifierHandler, 0);
        } catch (Exception e) {
            myLogger.error("queryFindSecure caught an exception:", e);
        }
        myLogger.debug("queryFindSecure: Getting output objects");
        List<?> oo = dicomFindIdentifierHandler.getOutputObjects();
        myLogger.debug("queryFindSecure:   Size of outputObjects is " + oo.size());
        return oo;
    }

    public List<?> queryFind(AttributeList filter, ModelMap map, String retrieveLevel) {

        if (myLogger.isDebugEnabled()) {
            myLogger.debug("queryFind: " + filter.toString());
        }

        MyUnsecureIdentifierHandler dicomFindIdentifierHandler = new MyUnsecureIdentifierHandler(map, retrieveLevel);
        try {

            new MyFindSOPClassSCU(serverip, Integer.parseInt(serverport), serverAE, clientAE, cfindQR_SOPClass, filter,
                    dicomFindIdentifierHandler, 0);
        } catch (Exception e) {
            myLogger.error("queryFindSecure caught an exception:", e);
        }
        myLogger.debug("Getting output objects");
        return dicomFindIdentifierHandler.getOutputObjects();
    }

    // no payloads just metadata
    public QueryTreeModel query(AttributeList filter) throws IOException, DicomException, DicomNetworkException {
        QueryInformationModel model = getQueryInformationModel();
        QueryTreeModel tree = null;
        try {

            if (myLogger.isDebugEnabled()) {
                myLogger.debug("query: " + filter.toString());
            }
            /*
             * Attribute study_uid = filter.get(TagFromName.StudyInstanceUID);
             * study_uid.addValue("1.3.6.1.4.1.9328.50.1.1195");
             * filter.remove(TagFromName.StudyInstanceUID);
             * filter.put(TagFromName.StudyInstanceUID, study_uid);
             * 
             * Attribute series_uid = filter.get(TagFromName.SeriesInstanceUID);
             * series_uid.addValue("1.3.6.1.4.1.9328.50.1.1261");
             * filter.remove(TagFromName.SeriesInstanceUID);
             * filter.put(TagFromName.SeriesInstanceUID, series_uid);
             * 
             * System.out.println(filter.toString());
             * 
             * new
             * FindSOPClassSCU(serverip,Integer.parseInt(serverport),serverAE
             * ,clientAE, SOPClass.StudyRootQueryRetrieveInformationModelFind,
             * filter, new IdentifierHandler() { private int count = 0; public
             * void doSomethingWithIdentifier(AttributeList responseIdentifier)
             * throws DicomException { System.err.println("PatientID = "+
             * Attribute
             * .getSingleStringValueOrEmptyString(responseIdentifier,TagFromName
             * .PatientID)); System.err.println("StudyInstanceUID = "+
             * Attribute.
             * getSingleStringValueOrEmptyString(responseIdentifier,TagFromName
             * .StudyInstanceUID)); System.err.println("SeriesInstanceUID = "+
             * Attribute
             * .getSingleStringValueOrEmptyString(responseIdentifier,TagFromName
             * .SeriesInstanceUID));
             * 
             * System.out.println("Final count = " + ++count);
             * 
             * } }, 0);
             */
            tree = model.performHierarchicalQuery(filter);
        } catch (IOException e1) {
            myLogger.error("ERROR: query failed during I/O", e1);
            throw e1;
        } catch (DicomException e1) {
            myLogger.error("ERROR: query failed during dicom parsign", e1);
            throw e1;
        } catch (DicomNetworkException e1) {
            myLogger.error("ERROR: query failed during dicom network i/o", e1);
            throw e1;
        }
        return tree;
    }

    public List<String> getRetrievedFileNames() {
        if (handler != null)
            return handler.getRecievedFilenames();
        else
            return null;
        /*
         * if (handler != null) { Vector<String> filenames =
         * handler.getRecievedFilenames(); for (int i = 0; i < filenames.size();
         * i++) if (!retrieved_fileset.contains(filenames.elementAt(i))) {
         * retrieved_fileset.add(filenames.elementAt(i));
         * retrieved_files.add(filenames.elementAt(i)); } //
         * retrieved_files.addAll(handler.getRecievedFilenames()); return
         * retrieved_files; } else return retrieved_files;
         */
    }

    // with payloads; return written filenames
    public List<String> retrieve(AttributeList filter, String[] manifestOut) throws DicomNetworkException,
            DicomException, IOException {
        // String tmpdir;
        {
//            java.util.Random r = new java.util.Random();
//            r.setSeed(System.currentTimeMillis());
            /*
             * int val = r.nextInt(); while (true) { tmpdir =
             * System.getProperty("java.io.tmpdir") + File.separator + ".tmp." +
             * val; if (new File(tmpdir).mkdirs()) { break; } val += 1; }
             */
        }

        // Vector<String> filenames = new Vector<String>();
        if (this.useCMOVE.equals("false")) {
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Using C_GET");
                myLogger.debug("tmpdir is " + tempdir);
            }
            // do C-GET
            this.cGetComplete = false;
            long t1 = System.currentTimeMillis() / 1000;
            // handler = new MyReceivedObjectHandler(filenames, tmpdir);
            handler.setTmpDir(tempdir);

            new GetSOPClassSCU(serverip, Integer.parseInt(serverport), serverAE, clientAE, cgetQR_SOPClass, filter,
                    new IdentifierHandler(), new File(tempdir), handler, SOPClass.getSetOfStorageSOPClasses(), true,
                    false, false, 0);
            if (myLogger.isDebugEnabled()) {
                long t2 = System.currentTimeMillis() / 1000;
                myLogger.debug("elapsed GetSOPClassSCU seconds: " + (t2 - t1));
            }
            this.cGetComplete = true;
            if (!isWSEnum)
                manifestOut[0] = handler.getManifest();
        } else { // C-MOVE
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Using C_MOVE");
                myLogger.debug("destAE is " + embeddedPacsAE);
            }
            // long t1cm = System.currentTimeMillis() / 1000;

            String username = System.getenv("LOGNAME");
            if (username == null)
                username = "";
            else
                username = "-" + username;
            String dumpdir = System.getProperty("java.io.tmpdir") + File.separator + "DICOMCQLQueryProcessor-embedded"
                    + username + File.separator + "db";
            if (!new File(dumpdir).exists()) {
                new File(dumpdir).mkdirs();
            }

            myLogger.debug("dumpdir " + dumpdir);
            // FIX THIS
            File tempDumpDir = new File(dumpdir);
            File downloadDir = createTempDir("Pixelmed", "CMoveDownload", tempDumpDir);

            EmbeddedPACS epacs = null;
            epacs = startEmbeddedPACS(epacs, downloadDir.getCanonicalPath());
            // Start EmbeddedPACS and point to download DIR
            MoveSOPClassSCU moveSopScu = new MoveSOPClassSCU(serverip, Integer.parseInt(serverport), serverAE,
                    clientAE, embeddedPacsAE, cmoveQR_SOPClass, filter, 0);
            // Retrieval is happening
            while (moveSopScu.getStatus() == 0xFF00)
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    myLogger.warn("Caught interrupted exception", e);
                    throw new DicomException("Thread Interrupted");
                }
            if (moveSopScu.getStatus() != 0x0000) {
                throw new DicomException("CMOVE FAILED"); // Retrieval failed
            }

            stopEmbeddedPACS(epacs);
            myLogger.debug("my part of the cmove is done");
            /*
             * File[] cmovedDICOMFiles = downloadDir.listFiles(new FileFilter()
             * { public boolean accept(File pathname) { String name =
             * pathname.getName(); if (name.equals("") ||
             * name.equalsIgnoreCase("manifest")) { return false; } else return
             * pathname.isFile(); } });
             */File[] cmovedDICOMFiles = recursiveFileFilter(downloadDir, new FileFilter() {
                public boolean accept(File pathname) {
                    String name = pathname.getName();
                    if (name.equals("") || name.equalsIgnoreCase("manifest")) {
                        return false;
                    } else
                        return pathname.isFile();
                }
            });

            for (int i = 0; i < cmovedDICOMFiles.length; i++)
                filenames.add(cmovedDICOMFiles[i].getAbsolutePath());
        }
        // Vector<String> recvdfiles = handler.getRecievedFilenames();
        // handler.resetRecievedFilenames();
        if (!isWSEnum)
            return handler.getRecievedFilenames();
        else
            return filenames;
    }

    private File[] recursiveFileFilter(File downloadDir, FileFilter fileFilter) {
        File[] dirsOnly = downloadDir.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (pathname.isDirectory())
                    return true;
                else
                    return false;
            }
        });
        List<File> filesInDir = new ArrayList<File>(Arrays.asList(downloadDir.listFiles(fileFilter)));
        for (int i = 0; i < dirsOnly.length; i++) {
            filesInDir.addAll(new ArrayList<File>(Arrays.asList(recursiveFileFilter(dirsOnly[i], fileFilter))));
        }
        return (File[]) filesInDir.toArray(new File[filesInDir.size()]);
    }

    private void stopEmbeddedPACS(EmbeddedPACS epacs) {
        if (epacs != null)
            epacs.stop();
    }

    private EmbeddedPACS startEmbeddedPACS(EmbeddedPACS epacs, String canonicalPath) {
        try {
            myLogger.debug("starting embedded pacs...");
            Properties prop = gov.nih.nci.cagrid.data.service.ServiceConfigUtil
                    .getQueryProcessorConfigurationParameters();
            epacs = new EmbeddedPACS(canonicalPath, prop);
            epacs.start();
        } catch (Exception ex) {
            myLogger.error("Embedded pacs threw and exception", ex);
            epacs.stop(); // TODO get rid of call to stop()
        }
        return epacs;
    }

    /**
     * Create a directory with a path that is unique during the current JVM
     * invocation.
     * 
     * @param prefix
     *            The prefix string to be used in generating the file's name;
     *            must be at least three characters long.
     * @param suffix
     *            The suffix string to be used in generating the file's name;
     *            may be null, in which case the suffix ".tmp" will be used.
     * @param tempDir
     *            The directory in which the file is to be created, or null if
     *            the default temporary-file directory is to be used. If the
     *            directory specified by this parameter does not exist, it is
     *            created.
     * @return A file object that identifies the newly created directory.
     * @throws IOException
     *             if there is a problem.
     */
    private File createTempDir(String prefix, String suffix, File tempDir) throws IOException {
        // Ensure that the directory we want to create in exists.
        if (!tempDir.exists()) {
            myLogger.info("Creating parent directory for temporary directories: " + tempDir.getAbsolutePath());
            if (!tempDir.mkdirs()) {
                String msg = "Directory does not exist and attempt to create it failed: " + tempDir.getAbsolutePath();
                myLogger.error(msg);
                throw new IOException(msg);
            }
        }
        // First create a file with a unique name because we have a way to do
        // that for a file but not a directory.
        File dir;
        try {
            dir = File.createTempFile(prefix, suffix, tempDir);
        } catch (IOException e) {
            String msg = "createTempDir failed: prefix=\"" + prefix + "\" suffix=\"" + suffix + "\" tempDir=\""
                    + tempDir + "\"";
            myLogger.error(msg, e);
            throw e;
        }
        dir.delete();
        if (!dir.mkdirs()) {
            String msg = "createTempDir: mkdirs() failed for " + dir.getAbsolutePath();
            myLogger.error(msg);
            throw new IOException(msg);
        }
        return dir;
    }

    public boolean getCGetComplete() {
        return cGetComplete;
    }

    public void setIsWSEnum(boolean isWSEnum) {
        this.isWSEnum = isWSEnum;
    }

    public String retrieveZipped(AttributeList filter, boolean compressed, String storeDir, String fileBaseName)
            throws DicomNetworkException, DicomException, IOException {
        assert (storeDir != null);
        File storeDirFile = new File(storeDir);
        if (!storeDirFile.exists()) {
            storeDirFile.mkdirs();
        }

        String[] manifest = new String[1];
        List<String> files = this.retrieve(filter, manifest);
        if (isWSEnum)
            return null;

        if (files.size() == 0)
            throw new DicomException("Retrieved a null dataset");
        String[] files2 = new String[files.size() + 1];
        for (int i = 0; i < files.size(); i++) {
            files2[i] = files.get(i);
        }
        String manifestfn = (new File(files2[0]).getParent() + File.separator + "manifest");
        FileOutputStream f;
        f = new FileOutputStream(manifestfn);
        f.write(manifest[0].getBytes());
        f.close();
        files2[files.size()] = manifestfn;

        String fn = (storeDir + File.separator + fileBaseName);
        if (fileBaseName == null) {
            java.util.Random r = new java.util.Random();
            r.setSeed(System.currentTimeMillis());
            while (true) {
                int val = r.nextInt();
                if (val < 0) {
                    val = 0 - val;
                }
                fn = (storeDir + File.separator + val + ".zip");
                if (!new File(fn).exists()) {
                    break;
                }
            }
        }
        String tempfn = File.createTempFile("tmp", ".zip", new File(storeDir)).getCanonicalPath();
        Zipper.zip(tempfn, files2, compressed);
        // delete the .dcm files
        for (int i = 0; i < files2.length; i++)
            new File(files2[i]).delete();
        if (files2.length > 1)
            new File(new File(files2[0]).getParent()).delete();
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("tempfn " + tempfn);
            myLogger.debug("fn " + fn);
        }
        FileLock[] lock = new FileLock[1];
        FileChannel[] channel = new FileChannel[1];
        lockfile((storeDir + File.separator + "lck"), lock, channel);
        File tempfnf = new File(tempfn);
        File fnf = new File(fn);
        if (fnf.exists()) {
            tempfnf.delete();
        } else if (tempfnf.renameTo(fnf) == false) {
            throw new IOException("rename failed from " + tempfn + " to " + fn);
        }
        unlockfile(lock[0], channel[0]);
        if (new File(tempfn).exists()) {
            myLogger.error("ERROR: file " + tempfn + " still exists!");
            throw new IOException("rename failed from " + tempfn + " to " + fn);
        }
        return fn;
    }

    public void lockfile(String filename, FileLock[] lockout, FileChannel[] chanout) {
        try {
            if (!new File(filename).exists()) {
                new File(filename).createNewFile();
            }
            // Get a file channel for the file
            File file = new File(filename);
            chanout[0] = new RandomAccessFile(file, "rw").getChannel();

            // Use the file channel to create a lock on the file.
            // This method blocks until it can retrieve the lock.
            myLogger.debug("blocking locking...");
            lockout[0] = chanout[0].lock();
            myLogger.debug("blocking locking...done");
        } catch (Exception e) {
            myLogger.error("lockfile caught an exception", e);
        }
    }

    public void unlockfile(FileLock lock, FileChannel channel) {
        try {
            // Release the lock
            lock.release();

            // Close the file
            channel.close();
        } catch (Exception ex) {
            myLogger.error("unlockfile caught an exception", ex);
        }
    }

    public static String getSha1Digest(byte[] array) {
        try {
            String tempfn = File.createTempFile("tmp", "").getCanonicalPath();
            PrintStream ps = new PrintStream(tempfn);
            MessageDigest md = MessageDigest.getInstance("SHA");
            md.update(array);
            byte[] digest = md.digest();
            for (int i2 = 0; i2 < digest.length; i2++) {
                ps.printf("%02x", digest[i2]);
            }
            ps.close();
            FileInputStream f;
            f = new FileInputStream(tempfn);
            int len = (int) new File(tempfn).length();
            byte[] b = new byte[len];
            f.read(b);
            f.close();
            new File(tempfn).delete();
            return new String(b);
        } catch (Exception e) {
            // e.printStackTrace();
            // System.exit(1);
            myLogger.error("getSha1Digest caught an exception.", e);
            throw new ThreadDeath();
        }
    }

    public String retrieveZippedCaching(AttributeList filter, boolean compressed, String cacheDir)
            throws DicomNetworkException, DicomException, IOException {
        assert (cacheDir != null);
        if (!new File(cacheDir).exists()) {
            new File(cacheDir).mkdirs();
        }
        String filter_as_str = filter.toString();
        myLogger.debug("filter_as_str: " + filter_as_str);
        String filter_as_digest = getSha1Digest(filter_as_str.getBytes());
        String fn = (cacheDir + File.separator + filter_as_digest + ".zip");
        if (new File(fn).exists()) {
            return fn;
        }
        return retrieveZipped(filter, compressed, cacheDir, filter_as_digest + ".zip");
    }

    public String retrieveZippedCaching(String studyID, String seriesID, String imageID, boolean compressed,
            String cacheDir) throws DicomNetworkException, DicomException, IOException {
        if (!new File(cacheDir).exists()) {
            new File(cacheDir).mkdirs();
        }
        String cache_fn = "study_" + studyID + "_series_" + seriesID + "_image_" + imageID + ".zip";
        String full_fn = (cacheDir + File.separator + cache_fn);
        myLogger.debug("full_fn is " + full_fn);
        if (new File(full_fn).exists()) {
            return full_fn;
        }
        AttributeList filter = new AttributeList();
        {
            AttributeTag t = TagFromName.QueryRetrieveLevel;
            Attribute a = new CodeStringAttribute(t);
            if (studyID != null && seriesID == null && imageID == null) {
                a.addValue("STUDY");
            } else if (studyID != null && seriesID != null && imageID == null) {
                a.addValue("SERIES");
            } else if (studyID != null && seriesID != null && imageID != null) {
                a.addValue("IMAGE");
            } else {
                throw new DicomException(
                        "invalid input, studyID/seriesID/imageID must be all nonnull, or studyID/seriesID must be the only ones nonnull, or studyID must be the only one nonnull.");
            }
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.StudyInstanceUID;
            Attribute a = new UniqueIdentifierAttribute(t);
            if (studyID != null) {
                a.addValue(studyID);
            }
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.SeriesInstanceUID;
            Attribute a = new UniqueIdentifierAttribute(t);
            if (seriesID != null) {
                a.addValue(seriesID);
            }
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.SOPInstanceUID;
            Attribute a = new UniqueIdentifierAttribute(t);
            if (imageID != null) {
                a.addValue(imageID);
            }
            filter.put(t, a);
        }
        return retrieveZipped(filter, compressed, cacheDir, cache_fn);
    }

    protected static AttributeList mainMakeSimpleFilter() {
        // SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(
        // null);
        SpecificCharacterSet specificCharacterSet = new SpecificCharacterSet(new String[1]);
        AttributeList filter = new AttributeList();
        {
            AttributeTag t = TagFromName.PatientID;
            Attribute a = new ShortStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.PatientSex;
            Attribute a = new CodeStringAttribute(t);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.StudyDescription;
            Attribute a = new LongStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.AdmittingDiagnosesDescription;
            Attribute a = new LongStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.PatientAge;
            Attribute a = new AgeStringAttribute(t);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.PatientSize;
            Attribute a = new DecimalStringAttribute(t);
            // retrieve only
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.PatientWeight;
            Attribute a = new DecimalStringAttribute(t);
            // retrieve only
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.Occupation;
            Attribute a = new ShortStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.NumberOfStudyRelatedSeries;
            Attribute a = new IntegerStringAttribute(t);
            // retreive only
            filter.put(t, a);
        }

        {
            AttributeTag t = TagFromName.SeriesDescription;
            Attribute a = new LongStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.Modality;
            Attribute a = new CodeStringAttribute(t);
            filter.put(t, a);
        }

        {
            AttributeTag t = TagFromName.ProtocolName;
            Attribute a = new LongStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.BodyPartExamined;
            Attribute a = new CodeStringAttribute(t);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.Manufacturer;
            Attribute a = new LongStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }
        // {
        // AttributeTag t = TagFromName.InstitutionName;
        // Attribute a = new LongStringAttribute(t, specificCharacterSet);
        // }
        {
            AttributeTag t = TagFromName.NumberOfSeriesRelatedInstances;
            Attribute a = new IntegerStringAttribute(t);
            // retrieve only
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.InstanceNumber;
            Attribute a = new IntegerStringAttribute(t);
            // retrieve only
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.ImageType;
            Attribute a = new CodeStringAttribute(t);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.ContrastBolusAgent;
            Attribute a = new LongStringAttribute(t, specificCharacterSet);
            filter.put(t, a);
        }

        {
            AttributeTag t = TagFromName.StudyInstanceUID;
            Attribute a = new UniqueIdentifierAttribute(t);
            filter.put(t, a);
        }
        {
            AttributeTag t = TagFromName.SeriesInstanceUID;
            Attribute a = new UniqueIdentifierAttribute(t);
            filter.put(t, a);
        }
        return filter;
    }

    public static void main(String[] args) throws Exception {
    }

    private class MyFindSOPClassSCU extends FindSOPClassSCU {
        public MyFindSOPClassSCU(String hostname, int port, String calledAETitle, String callingAETitle,
                String affectedSOPClass, AttributeList identifier, IdentifierHandler identifierHandler, int debugLevel)
                throws DicomNetworkException, DicomException, IOException {
            super(hostname, port, calledAETitle, callingAETitle, affectedSOPClass, identifier, identifierHandler,
                    debugLevel);
        }
    }

    // private class MySimpleQueryIdentifierHandler extends IdentifierHandler {
    //
    // private String queryRetrieveLevel = null;
    //
    // public MySimpleQueryIdentifierHandler(String qrLevel) {
    // queryRetrieveLevel = qrLevel;
    // }
    //
    // public String[] getOutputObjects() {
    // // xTODO Auto-generated method stub
    // return null;
    // }
    //
    // public void doSomethingWithIdentifier(AttributeList responseIdentifier)
    // throws DicomException {
    //
    // }
    //
    // }

    private class MySecureIdentifierHandler extends IdentifierHandler {
        private int count = 0;
        private ModelMap handlerMap;
        // private ModelDictionary handlerDict;
        private String retrieveLevel;
        private String user;
//        private Authorization localAuth;
        private DataAuthorization localAuth;

        // Object retrieveLevelObject = null;
        Class<?> retrieveLevelClass = null;
        String queryRetrieveLevel = null;

        List<Object> outputObjects = null;
        DicomDictionary dict = null;
        DICOM2Model d2m = null;

        /*
         * private static final SimpleDateFormat dateFormatter = new
         * SimpleDateFormat("yyyyMMdd"); private static final SimpleDateFormat
         * timeFormatter = new SimpleDateFormat("HHmmss.SSS"); private static
         * final SimpleDateFormat dateTimeFormatter = new
         * SimpleDateFormat("yyyyMMddHHmmss.SSSZ");
         */
        public List<?> getOutputObjects() {
            return outputObjects;
        }

//        public MySecureIdentifierHandler(ModelMap map, String Level, String userId, Authorization authObj) {
        public MySecureIdentifierHandler(ModelMap map, String level, String userId, DataAuthorization authObj) {
            super();
            myLogger.debug("MySecureIdentifierHandler: level is " + level);
            handlerMap = map;
            // handlerDict = handlerMap.getModelDict();
            retrieveLevel = level;
            dict = new DicomDictionary();
            try {
                queryRetrieveLevel = retrieveLevel;
                retrieveLevelClass = Class.forName(retrieveLevel);
                outputObjects = new ArrayList<Object>();
            } catch (ClassNotFoundException e) {
                myLogger.error("Constructor for MYSecureIdentifierHandler caught an exception.", e);
            }
            user = userId;
            localAuth = authObj;
            d2m = new DICOM2Model(handlerMap);
        }

        public void doSomethingWithIdentifier(AttributeList responseIdentifier) throws DicomException {
            // HashMap<String, Integer> names = null;

            if (myLogger.isDebugEnabled()) {
                myLogger.debug("response is : " + responseIdentifier.toString(dict));
            }
            try {
                if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Patient")) {
                    handlePatients(responseIdentifier);
                } else if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Study")) {
                    handleStudies(responseIdentifier);
                } else if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Series")) {
                    handleSeries(responseIdentifier);
                } else if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Image")) {
                    myLogger.warn("Query of individual image attributes is not currently supported");
                } else {
                    myLogger.error("Query for unknown target (not patient, study, series or image): "
                            + queryRetrieveLevel);
                }
            } catch (Exception e) {
                myLogger.debug("doSomethingWithIdentifier caught an exception", e);
            }
            myLogger.debug("Final count = " + ++count);

        }

        private void handleSeries(AttributeList responseIdentifier) throws ModelMapException, DataConversionException {
            if (checkIfAttributeCanBeAdded(responseIdentifier)) {
                Patient parentPatient = populatePatientAttribs(responseIdentifier);
                Study parentStudy = populateStudyAttribs(responseIdentifier);
                Series retrieveLevelObject = populateSeriesAttribs(responseIdentifier);

                parentStudy.setPatient(parentPatient);
                retrieveLevelObject.setStudy(parentStudy);

                this.outputObjects.add(retrieveLevelObject);
            }
        }

        private void handleStudies(AttributeList responseIdentifier) throws ModelMapException, DataConversionException {
            if (checkIfAttributeCanBeAdded(responseIdentifier)) {
                Patient parentPatient = populatePatientAttribs(responseIdentifier);
                Study retrieveLevelObject = populateStudyAttribs(responseIdentifier);
                retrieveLevelObject.setPatient(parentPatient);

                this.outputObjects.add(retrieveLevelObject);
            }
        }

        private void handlePatients(AttributeList responseIdentifier) throws ModelMapException, DataConversionException {
            if (checkIfAttributeCanBeAdded(responseIdentifier)) {
                Patient retrieveLevelObject = populatePatientAttribs(responseIdentifier);

                this.outputObjects.add(retrieveLevelObject);
            }
        }

        private Series populateSeriesAttribs(AttributeList responseIdentifier) throws ModelMapException,
                DataConversionException {
            return (Series) d2m.populateFields(new Series(), responseIdentifier);
        }

        private Study populateStudyAttribs(AttributeList responseIdentifier) throws ModelMapException,
                DataConversionException {
            return (Study) d2m.populateFields(new Study(), responseIdentifier);
        }

        private Patient populatePatientAttribs(AttributeList responseIdentifier) throws ModelMapException,
                DataConversionException {
            return (Patient) d2m.populateFields(new Patient(), responseIdentifier);
        }

        private boolean checkIfAttributeCanBeAdded(AttributeList responseIdentifier) {
//            Set<AuthPermission> permissions = authorize(localAuth, responseIdentifier, user, AuthPermission.READ);
//            boolean result = permissions.contains(AuthPermission.READ);
            boolean result = (authorize(localAuth, responseIdentifier, user, DataAuthorization.READ) & DataAuthorization.READ) == DataAuthorization.READ;

            System.out.println(user + " is authorized for this data");

            return result;
        }

        // private Object getObjectValueFromAttribute(AttributeList
        // responseIdentifier, AttributeTag attrTag,
        // Class paramClass) {
        //
        // byte[] vr = dict.getValueRepresentationFromTag(attrTag);
        //
        // String attributeValueAsString =
        // Attribute.getSingleStringValueOrEmptyString(responseIdentifier,
        // attrTag);
        // Object objectValue = null;
        // /*
        // * if (ValueRepresentation.isDateVR(vr)) { // return a java Date
        // * object objectValue =
        // * this.dateFormatter.parse(DICOM2Model.convertDateString
        // * (attributeValueAsString)); } else if
        // * (ValueRepresentation.isTimeVR(vr)) { // return a Time object
        // * (axis) objectValue = construct.newInstance(new Object[]
        // * {DICOM2Model.convertTimeString(attributeValueAsString)}); } else
        // * if (ValueRepresentation.isDateTimeVR(vr)) { // return an object,
        // * which presumably is a java Date object. objectValue =
        // * this.dateTimeFormatter
        // * .parseObject(DICOM2Model.convertDateTimeString
        // * (attributeValueAsString)); } else { objectValue =
        // * construct.newInstance(new Object[] {strValue}); }
        // */return objectValue;
        // }

    }

    private class MyUnsecureIdentifierHandler extends IdentifierHandler {
        private int count = 0;
        private ModelMap handlerMap;
        // private ModelDictionary handlerDict;
        private String retrieveLevel;

        // Object retrieveLevelObject = null;
        Class<?> retrieveLevelClass = null;
        String queryRetrieveLevel = null;

        ArrayList<Object> outputObjects = null;
        DicomDictionary dict = null;
        DICOM2Model d2m = null;

        /*
         * private static final SimpleDateFormat dateFormatter = new
         * SimpleDateFormat("yyyyMMdd"); private static final SimpleDateFormat
         * timeFormatter = new SimpleDateFormat("HHmmss.SSS"); private static
         * final SimpleDateFormat dateTimeFormatter = new
         * SimpleDateFormat("yyyyMMddHHmmss.SSSZ");
         */
        public List<?> getOutputObjects() {
            return outputObjects;
        }

        public MyUnsecureIdentifierHandler(ModelMap map, String level) {
            super();
            myLogger.debug("MyUnsecureIdentifierHandler: level is " + level);
            handlerMap = map;
            // handlerDict = handlerMap.getModelDict();
            retrieveLevel = level;
            dict = new DicomDictionary();
            try {
                queryRetrieveLevel = retrieveLevel;
                retrieveLevelClass = Class.forName(retrieveLevel);
                outputObjects = new ArrayList<Object>();
            } catch (ClassNotFoundException e) {
                myLogger.debug("constructor for MyUnsecureIdentifierHandler threw and exception.", e);
            }
            d2m = new DICOM2Model(handlerMap);

        }

        public void doSomethingWithIdentifier(AttributeList responseIdentifier) throws DicomException {
            // DICOM2Model d2m = new DICOM2Model(handlerMap);
            // HashMap<String, Integer> names = null;
            try {
                if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Patient"))
                    handlePatients(responseIdentifier);
                else if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Study"))
                    handleStudies(responseIdentifier);
                else if (queryRetrieveLevel.equalsIgnoreCase("gov.nih.nci.ncia.domain.Series"))
                    handleSeries(responseIdentifier);
                // String[] namesFull =
                // handlerDict.getAttributeNameContains(retrieveLevel).toArray(new
                // String[handlerMap.getModelDict().getAttributeNameContains("gov.nci.nih.domain.Patient").size()]);
                // Object retrieveLevelObject =
                // retrieveLevelClass.newInstance();
                // retrieveLevelObject = d2m.populateFields(retrieveLevelObject,
                // responseIdentifier);
                /*
                 * if(false) for(int i = 0; i < namesFull.length; i++) { //
                 * byte[] vr = dict.getValueRepresentationFromTag(tag); String
                 * namesFullValue = namesFull[i];
                 * if(!namesFull[i].endsWith(".id")) {
                 * 
                 * AttributeTag attrTag =
                 * handlerMap.getAttributeTagFromModelAttributeName
                 * (namesFull[i]); String stringValue =
                 * Attribute.getSingleStringValueOrEmptyString
                 * (responseIdentifier, attrTag); if(stringValue.length()>0) {
                 * System.out.println(namesFull[i] + " = " + stringValue);
                 * Method setterMethod =
                 * handlerMap.getModelDict().getSettersFromAttributeName
                 * (namesFull[i]).get(0); Class paramClass =
                 * setterMethod.getParameterTypes()[0]; Object objectValue =
                 * getObjectValueFromAttribute(responseIdentifier, attrTag,
                 * paramClass); setterMethod.invoke(retrieveLevelObject, new
                 * Object[]{objectValue}); System.out.println("The setter for "
                 * + namesFull[i] +" is " +
                 * handlerMap.getModelDict().getSettersFromAttributeName
                 * (namesFull[i]).get(0).getName()); } } }
                 */// this.outputObjects.add(retrieveLevelObject);
                // retrieveLevelObject = null;
            } catch (Exception e) {
                myLogger.error("doSomethingWithIdentifier caught an exception", e);
            }
            /*
             * System.err.println("PatientID = "+
             * Attribute.getSingleStringValueOrEmptyString
             * (responseIdentifier,TagFromName.PatientID));
             * System.err.println("StudyInstanceUID = "+
             * Attribute.getSingleStringValueOrEmptyString
             * (responseIdentifier,TagFromName.StudyInstanceUID));
             * System.err.println("SeriesInstanceUID = "+
             * Attribute.getSingleStringValueOrEmptyString
             * (responseIdentifier,TagFromName.SeriesInstanceUID));
             */
            myLogger.debug("Final count = " + ++count);
        }

        private void handleSeries(AttributeList responseIdentifier) throws ModelMapException, DataConversionException {
            if (checkIfAttributeCanBeAdded(responseIdentifier)) {
                Patient parentPatient = populatePatientAttribs(responseIdentifier);
                Study parentStudy = populateStudyAttribs(responseIdentifier);
                Series retrieveLevelObject = populateSeriesAttribs(responseIdentifier);

                parentStudy.setPatient(parentPatient);
                retrieveLevelObject.setStudy(parentStudy);

                this.outputObjects.add(retrieveLevelObject);
            }
        }

        private void handleStudies(AttributeList responseIdentifier) throws ModelMapException, DataConversionException {
            if (checkIfAttributeCanBeAdded(responseIdentifier)) {
                Patient parentPatient = populatePatientAttribs(responseIdentifier);
                Study retrieveLevelObject = populateStudyAttribs(responseIdentifier);
                retrieveLevelObject.setPatient(parentPatient);

                this.outputObjects.add((Object) retrieveLevelObject);
            }
        }

        private void handlePatients(AttributeList responseIdentifier) throws ModelMapException, DataConversionException {
            if (checkIfAttributeCanBeAdded(responseIdentifier)) {
                Patient retrieveLevelObject = populatePatientAttribs(responseIdentifier);

                this.outputObjects.add(retrieveLevelObject);
            }
        }

        private Series populateSeriesAttribs(AttributeList responseIdentifier) throws ModelMapException,
                DataConversionException {
            return (Series) d2m.populateFields(new Series(), responseIdentifier);
        }

        private Study populateStudyAttribs(AttributeList responseIdentifier) throws ModelMapException,
                DataConversionException {
            return (Study) d2m.populateFields(new Study(), responseIdentifier);
        }

        private Patient populatePatientAttribs(AttributeList responseIdentifier) throws ModelMapException,
                DataConversionException {
            return (Patient) d2m.populateFields(new Patient(), responseIdentifier);
        }

        private boolean checkIfAttributeCanBeAdded(AttributeList responseIdentifier) {
            // TODO Auto-generated method stub
            return true;
        }

        // private Object getObjectValueFromAttribute(AttributeList
        // responseIdentifier, AttributeTag attrTag,
        // Class paramClass) {
        //
        // byte[] vr = dict.getValueRepresentationFromTag(attrTag);
        //
        // String attributeValueAsString =
        // Attribute.getSingleStringValueOrEmptyString(responseIdentifier,
        // attrTag);
        // Object objectValue = null;
        // /*
        // * if (ValueRepresentation.isDateVR(vr)) { // return a java Date
        // * object objectValue =
        // * this.dateFormatter.parse(DICOM2Model.convertDateString
        // * (attributeValueAsString)); } else if
        // * (ValueRepresentation.isTimeVR(vr)) { // return a Time object
        // * (axis) objectValue = construct.newInstance(new Object[]
        // * {DICOM2Model.convertTimeString(attributeValueAsString)}); } else
        // * if (ValueRepresentation.isDateTimeVR(vr)) { // return an object,
        // * which presumably is a java Date object. objectValue =
        // * this.dateTimeFormatter
        // * .parseObject(DICOM2Model.convertDateTimeString
        // * (attributeValueAsString)); } else { objectValue =
        // * construct.newInstance(new Object[] {strValue}); }
        // */return objectValue;
        // }

    }

    private class MyReceivedObjectHandler extends ReceivedObjectHandler {
        List<String> filenames;
        String dir;
        Map<Double, String> m;

        private Integer lastRequestIndex = 0;

        public MyReceivedObjectHandler(List<String> filenames, String dir) {
            this.filenames = filenames;
            this.dir = dir;
            this.m = new TreeMap<Double, String>();
        }

        public void setTmpDir(String dir) {
            myLogger.debug("Setting tmp dir to" + dir);
            this.dir = dir;
        }

        public String getManifest() {
            StringBuffer sb = new StringBuffer();
            for (Map.Entry<Double, String> e : m.entrySet()) {
                Double z = e.getKey();
                String fn = e.getValue();
                sb.append(fn + " " + z + "\n");
            }
            return sb.toString();
        }

        public List<String> getRecievedFilenames() {
            List<String> rcvdfiles = new ArrayList<String>();

            Integer tmp = this.lastRequestIndex;
            this.lastRequestIndex = filenames.size();

            rcvdfiles.addAll(filenames.subList(tmp, this.lastRequestIndex));

            for (int i = 0; i < rcvdfiles.size(); i++)
                myLogger.debug("sending file " + rcvdfiles.get(i));

            myLogger.debug("sent " + rcvdfiles.size() + " files");

            return rcvdfiles;
        }

        /*
         * public void resetRecievedFilenames() { filenames.removeAllElements();
         * }
         */public void sendReceivedObjectIndication(String fileName, String transferSyntax, String callingAETitle)
                throws DicomNetworkException, DicomException, IOException {
            String bn = fileName + ".dcm";
            myLogger.debug("retrieved image name = " + bn);
            fileName = (this.dir + File.separator + fileName);
            new File(fileName).renameTo(new File(fileName + ".dcm"));
            fileName += ".dcm";
            myLogger.debug("retrieved fileName: " + fileName + " from " + callingAETitle);
            filenames.add(fileName);

            boolean readhdr = true;
            // readhdr = false;
            if (readhdr) {
                BufferedInputStream bis = new BufferedInputStream(new FileInputStream(fileName));
                DicomInputStream dis = new DicomInputStream(bis);
                AttributeList al = new AttributeList();
                try {
                    al.read(dis);
                    bis.close();
                    double z = al.get(TagFromName.ImagePositionPatient).getDoubleValues()[2];
                    m.put(new Double(z), bn);
                } catch (Exception ex) {
                    myLogger.error("sendReceivedObjectIndication caught an exception", ex);
                    throw new IOException("dicom hdr read");
                }
            }
        }
    }

}
