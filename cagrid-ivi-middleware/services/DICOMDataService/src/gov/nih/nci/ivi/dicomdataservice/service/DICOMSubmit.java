package gov.nih.nci.ivi.dicomdataservice.service;

//import edu.emory.cci.security.auth.AttributeMetadata;
//import edu.emory.cci.security.auth.Authorization;
import edu.osu.bmi.security.authorization.AuthorizationException;
import gov.nih.nci.ivi.utils.Zipper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.encoding.SerializationException;

import submission.SubmissionInformation;

import com.pixelmed.dicom.DicomException;
import com.pixelmed.network.StorageSOPClassSCU;

class DICOMSubmit {
    private static final Logger myLogger = LogManager.getLogger(DICOMSubmit.class);

    // private DataStorageDescriptor desc = null;
    // private SubmissionInformation subInfo = null;
    private Properties dicomQueryProcProps = null;
//    private String user = null;
    private boolean authOn = false;
    // private ModelMap map = null;
    //private Authorization authObj = null;
//    private DICOMTagToAuthAttributeMetadataMapper tagConverter = new DICOMTagToAuthAttributeMetadataMapper();

    /*
     * public DICOMSubmit(DataStorageDescriptor dataStorageDescriptor,
     * SubmissionInformation submissionInformation, Properties queryProps) {
     * desc = dataStorageDescriptor; subInfo = submissionInformation;
     * dicomQueryProcProps = queryProps; }
     */
    /**
     * @throws NullPointerException
     *             ifdataLevelAuthorizationOn is true and user is null.
     */
    public DICOMSubmit(SubmissionInformation submissionInformation, Properties queryProps,
            boolean dataLevelAuthorizationOn, String user) {
        // desc = dataStorageDescriptor;
        // subInfo = submissionInformation;
        dicomQueryProcProps = queryProps;
//        this.user = user;
        this.authOn = dataLevelAuthorizationOn;

        if (authOn) {
            // String gridGrouperURL =
            // this.dicomQueryProcProps.getProperty(DICOMCQLQueryProcessor.GRID_GROUPER_URL);
            // String permissionServiceURL =
            // this.dicomQueryProcProps.getProperty(DICOMCQLQueryProcessor.PERMISSION_SERVICE_URL);
            if (user == null) {
                String msg = "Data level authorization is on but user is null";
                myLogger.error(msg);
                throw new NullPointerException(msg);
            }
//            authObj = new Authorization();
        }
        if (myLogger.isInfoEnabled()) {
            if (authOn) {
                myLogger.info("Created DICOMSubmit object with data level authorization on and user=" + user);
            } else {
                myLogger.debug("Created DICOMSubmit object with data level authorization off.");
            }
        }
    }

    public void processDicomUpload(String localLocation) throws FileNotFoundException, IOException, DicomException,
            SerializationException, AuthorizationException {
        // System.out.println("Data has been uploaded to" + desc.getLocation());
        // File uploadedFile = new File(desc.getLocation());
        myLogger.info("Data has been uploaded to" + localLocation);
        // File uploadedFile = new File(localLocation);
        // uploadedFile.renameTo(new File(subInfo.getFileName()));

        java.util.Random r = new java.util.Random();
        r.setSeed(System.currentTimeMillis());
        int val = r.nextInt();
        String unzipUploadDirPath = dicomQueryProcProps.getProperty("tempdir") + File.separator + "DICOM-Upload"
                + File.separator + val;
        File unzipUploadDir = new File(unzipUploadDirPath);
        if (!unzipUploadDir.exists())
            unzipUploadDir.mkdirs();
        myLogger.debug("Unziping to " + unzipUploadDirPath);
        String[] outs = null;
        outs = Zipper.unzip(localLocation, unzipUploadDirPath);
        File uploadedFile = new File(localLocation);
        uploadedFile.delete();
        myLogger.debug("Uploaded file deleted.");

        if (myLogger.isInfoEnabled()) {
            for (int index = 0; index < outs.length; index++)
                myLogger.info(outs[index]);
        }

        myLogger.info("Start uploading files to PACS");
        uploadToPACS(outs);
        myLogger.info("End uploading files to PACS");
        if (authOn) {
//            updateAuthorization(this.user, outs);
        }

        myLogger.debug("Deleting unzipped files and directory.");
        for (int index = 0; index < outs.length; index++) {
            File tmpDICOMFile = new File(outs[index]);
            tmpDICOMFile.delete();
        }
        unzipUploadDir.delete();
    }

//    /**
//     * Add the specified dicom files to the authorization database with the
//     * specified user as the owner.
//     * 
//     * @param user
//     * @param outs
//     * @throws IOException
//     * @throws DicomException
//     * @throws SerializationException
//     * @throws AuthorizationException
//     */
//    private void updateAuthorization(String user, String[] outs) throws IOException, DicomException,
//            SerializationException, AuthorizationException {
//        myLogger.debug("Enter updateAuthorization");
//        String org = this.dicomQueryProcProps.getProperty(DICOMCQLQueryProcessor.ORGANIZATION);
//        String datasource = this.dicomQueryProcProps.getProperty(PixelMedHelper.SERVER_AE);
//
//        List<String> resourceNames = new ArrayList<String>();
//        List<Map<AttributeMetadata, String>> authAttributesList = new ArrayList<Map<AttributeMetadata, String>>();
//        for (String filename : outs) {
//            if (isDicomFileName(filename)) {
//                myLogger.debug("Getting attributes for file " + filename);
//                // this is a dicom file
//                com.pixelmed.dicom.DicomInputStream dis = new DicomInputStream(new File(filename));
//                AttributeList al = new AttributeList();
//                al.read(dis);
//                String resourceName = assembleResourceName(org, datasource, al);
//                if (myLogger.isDebugEnabled()) {
//                    myLogger.debug("Resource name is " + resourceName);
//                }
//                resourceNames.add(resourceName);
//                authAttributesList.add(attributeListToAttributeValueMap(al));
//            }
//        }
//        myLogger.debug("Adding to authorization database");
//        authObj.addDataAuthorization(user, resourceNames, authAttributesList);
//        myLogger.debug("Exit updateAuthorization");
//    }

//    /**
//     * Return a map from AttributeMetadata to string values that is based on the
//     * contents of the given AttributeList object.
//     * 
//     * @param al
//     *            The AttributeList to extract attributes from.
//     * @return the created map.
//     */
//    @SuppressWarnings("unchecked")
//    private Map<AttributeMetadata, String> attributeListToAttributeValueMap(AttributeList al) {
//        HashMap<AttributeMetadata, String> attributeValueMap = new HashMap<AttributeMetadata, String>();
//        Set<AttributeTag> attributeSet = al.keySet();
//        for (AttributeTag tag : attributeSet) {
//            String value = al.get(tag).getDelimitedStringValuesOrNull();
//            if (value != null) {
//                attributeValueMap.put(tagConverter.attributeTagToAttributeMetadata(tag), value);
//            }
//        }
//        return attributeValueMap;
//    }

//    /**
//     * Construct a resource name for a DICOM file.
//     * 
//     * @param org
//     * @param datasource
//     * @param al
//     * @return the resource name.
//     */
//    private String assembleResourceName(String org, String datasource, AttributeList al) {
//        String[] UIDs = new String[3];
//
//        AttributeTag t = TagFromName.PatientID;
//        Attribute a = al.get(t);
//        if (a != null)
//            UIDs[0] = a.getSingleStringValueOrNull();
//
//        t = TagFromName.StudyInstanceUID;
//        a = al.get(t);
//        if (a != null)
//            UIDs[1] = a.getSingleStringValueOrNull();
//
//        t = TagFromName.SeriesInstanceUID;
//        a = al.get(t);
//        if (a != null)
//            UIDs[2] = a.getSingleStringValueOrNull();
//
//        // String resourceName = GridGrouperUtil.toStem(
//        // new String[] { DICOMCQLQueryProcessor.DATA_STEM, org, datasource },
//        // UIDs);
//        String resourceName = UIDs[0] + "!" + UIDs[1] + "!" + UIDs[2];
//        return resourceName;
//    }

    /**
     * Return true if the given file name can be a name of a DICOM file.
     * 
     * @param filename
     *            The file name.
     */
    private boolean isDicomFileName(String filename) {
        return !filename.contains("mainfest") && !filename.endsWith("zip");
    }

    private void uploadToPACS(String[] outs) {
        // SetOfDicomFiles dicomFiles = new SetOfDicomFiles();
        String theirHost = dicomQueryProcProps.getProperty("serverip");
        int theirPort = Integer.parseInt(dicomQueryProcProps.getProperty("serverport"));
        String theirAETitle = dicomQueryProcProps.getProperty("serverAE");
        String ourAETitle = dicomQueryProcProps.getProperty("clientAE");

        try {
            for (int i = 0; i < outs.length; i++) {
                if (!outs[i].contains("mainfest") && !outs[i].endsWith("zip")) {
                    myLogger.info(outs[i]);
                    new StorageSOPClassSCU(theirHost, theirPort, theirAETitle, ourAETitle, outs[i], null, null, 0, 1);
                    // dicomFiles.add(outs[i]);
                }
            }
        } catch (Exception e) {
            myLogger.error("uploadToPACS: ", e);
        }
    }
}