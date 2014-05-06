package gov.nih.nci.ivi.dicomdataservice.service;

//import edu.emory.cci.security.auth.Authorization;
import edu.osu.bmi.security.authorization.AllDataAuthorization;
import edu.osu.bmi.security.authorization.AuthorizationException;
import edu.osu.bmi.security.authorization.DataAuthorization;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperDataAuthorization;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.ivi.dicom.CQL2DICOM;
import gov.nih.nci.ivi.dicom.DICOMCQLQueryProcessor;
import gov.nih.nci.ivi.dicom.PixelMedHelper;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Series;
import gov.nih.nci.ncia.domain.Study;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.CodeStringAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;

class DICOMRetrieve {
    private static Logger myLogger = LogManager.getLogger(DICOMRetrieve.class);

    private CQLQuery threadQuery;

    // private Class seriesClass = null;
    // private Class studyClass = null;
    private String user = null;
    private boolean isRetrievalRequest;
//    private Authorization authObj = null;
    private GridGrouperDataAuthorization authObj = null;

    private Properties queryProcessorProperties;
    private boolean dataLevelAuthorizationOn;

    private ModelMap map = null;
    private List<String> retrievedZipFileNames = null;
    private PixelMedHelper pixelMedHelper = null;

    public DICOMRetrieve(Properties parentQueryProcessorProperties, boolean dataLevelAuthorizationOn, String user,
            int debugLevel) {
        this.queryProcessorProperties = parentQueryProcessorProperties;
        this.dataLevelAuthorizationOn = dataLevelAuthorizationOn;
        // usr =
        // org.globus.wsrf.security.SecurityManager.getManager().getCaller();
        this.user = user;

        try {
            map = new ModelMap();
        } catch (Exception e) {
            myLogger.fatal("Failed to create a ModelMap while creating a DICOMRetrieve Object", e);
        }
        if (dataLevelAuthorizationOn) {
//            authObj = new Authorization();
            try {
                authObj = new GridGrouperDataAuthorization(null, true);
            } catch (GridGrouperRuntimeFault e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (MalformedURIException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (pixelMedHelper == null) {
            pixelMedHelper = new PixelMedHelper(queryProcessorProperties);
        }
    }

    /**
     * @param cqlQuery
     * @return
     * @throws RemoteException
     *             If unable to complete the request.
     */
    public List<String> performRetrieve(CQLQuery cqlQuery) throws RemoteException {
        this.threadQuery = cqlQuery;
        String dicomRetrieveTarget = threadQuery.getTarget().getName();
        long t1 = System.currentTimeMillis() / 1000;

        // try {
        // studyClass =
        // map.getModelClassFromQueryRetrieveLevel(ModelMap.STUDY_STR);
        // seriesClass =
        // map.getModelClassFromQueryRetrieveLevel(ModelMap.SERIES_STR);
        // } catch (ModelMapException e1) {
        // myLogger.error("Error getting study or series class", e1);
        // }
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("DICOM retrieve class caller: " + user);
        }
        isRetrievalRequest = true;

        try {
            if (dataLevelAuthorizationOn)
                retrievedZipFileNames = queryAndRetrieve(threadQuery, authObj, user);
            else
                retrievedZipFileNames = unsecureWithoutAuthChecks(threadQuery);
        } catch (RemoteException e) {
            // just rethrow the remote exception
            throw e;
        } catch (AuthorizationException e) {
            String msg = "authObj error: ";
            myLogger.error(msg + dicomRetrieveTarget, e);
            throw new RemoteException(msg, e);
        } catch (Exception e) {
            String msg = "Error performing retrieve";
            myLogger.error(msg + dicomRetrieveTarget, e);
            throw new RemoteException(msg, e);
        }
        if (retrievedZipFileNames == null) {
            retrievedZipFileNames = new ArrayList<String>();
        }
        if (myLogger.isInfoEnabled()) {
            long t2 = System.currentTimeMillis() / 1000;
            myLogger.info("[TIMING] completed retrieval in total sec = " + (t2 - t1));
        }
        isRetrievalRequest = false;
        return retrievedZipFileNames;
    }

//    private List<String> queryAndRetrieve(CQLQuery query, Authorization auth, String user)
    private List<String> queryAndRetrieve(CQLQuery query, GridGrouperDataAuthorization auth, String user)
            throws MalformedURIException, RemoteException, AuthorizationException {
        myLogger.info("[TIMING] begin retrieve");
        long t1 = System.currentTimeMillis() / 1000;

        DICOMCQLQueryProcessor queryProcessor = new DICOMCQLQueryProcessor();
        try {
            queryProcessor.initialize(this.queryProcessorProperties, null);
        } catch (InitializationException e2) {
            String msg = "DICOMCQLQueryProcessor Initialization in BDTREsource failed.";
            myLogger.error(msg, e2);
            throw new RemoteException(msg, e2);
        }
        List<?> results = null;
        try {
            results = queryProcessor.queryDICOMSecurely(query, user, auth);
        } catch (QueryProcessingException e2) {
            String msg = "Error processing query";
            myLogger.error(msg, e2);
            throw new RemoteException(msg, e2);
        }
        if (myLogger.isInfoEnabled()) {
            long t2 = System.currentTimeMillis() / 1000;
            myLogger.info("[TIMING] query to get the attributes in sec = " + (t2 - t1));
            t1 = System.currentTimeMillis() / 1000;
        }
        Iterator<?> iter2 = results.iterator();
        List<String> output = new ArrayList<String>();

        while (iter2.hasNext()) {
            // Object series = iter2.next();
            Series series = Series.class.cast(iter2.next());
            if (series == null) {
                myLogger.error("something not right.  series is null");
                continue;
            }
            AttributeList newFilter = createAuthorizedRetrieveQuery(series);
            output.addAll(unsecureWithoutAuthChecks(newFilter));
        }
        if (myLogger.isInfoEnabled()) {
            long t2 = System.currentTimeMillis() / 1000;
            myLogger.info("[TIMING] retrieval completed for all series in sec = " + (t2 - t1));
        }
        if (output.size() != 0)
            // return zippedFiles.toArray(new String[zippedFiles.size()]);
            return output;
        else
            return null;
    }

    private List<String> unsecureWithoutAuthChecks(CQLQuery cqlQuery) {
        CQL2DICOM cql2 = null;
        try {
            cql2 = new CQL2DICOM(map);
        } catch (ModelMapException e1) {
            myLogger.error("ModelMap problem", e1);
        }
        AttributeList filter = null;

        String retrieveLevel = null;
        try {
            Class<?> targetClass = Class.forName(cqlQuery.getTarget().getName());
            InformationEntity ie = map.getInformationEntityFromModelClass(targetClass);
            retrieveLevel = map.getRetrieveLevel(ie);
        } catch (Exception e) {
            myLogger.error("Error querying model map", e);
        }

        InformationEntity queryRetrieveDepth = null;
        if (retrieveLevel.equalsIgnoreCase(ModelMap.PATIENT_STR)) {
            queryRetrieveDepth = InformationEntity.PATIENT;
        } else if (retrieveLevel.equalsIgnoreCase(ModelMap.STUDY_STR)) {
            queryRetrieveDepth = InformationEntity.STUDY;
        } else if (retrieveLevel.equalsIgnoreCase(ModelMap.SERIES_STR)) {
            queryRetrieveDepth = InformationEntity.SERIES;
        } else if (retrieveLevel.equalsIgnoreCase(ModelMap.IMAGE_STR)) {
            queryRetrieveDepth = InformationEntity.STUDY;
        } else {
            myLogger.error("We should not be here - The retrieve level is: " + retrieveLevel);
        }
        try {
            filter = cql2.cqlToPixelMed(cqlQuery);
            AttributeTag t = TagFromName.QueryRetrieveLevel;
            Attribute a = new CodeStringAttribute(t);
            // at this point, the retrievalDepth should already be set.

            if (myLogger.isDebugEnabled()) {
                myLogger.debug("QueryRetrieveLevel: " + map.getRetrieveLevel(queryRetrieveDepth));
            }
            a.addValue(map.getRetrieveLevel(queryRetrieveDepth));
            filter.put(t, a);

            // System.out.println(filter.toString());
            com.pixelmed.dicom.DicomDictionary dict = new com.pixelmed.dicom.DicomDictionary();
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("dicom QR =\n" + filter.toString(dict));
            }
        } catch (DicomException e) {
            myLogger.error("DICOM from CQL error: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            myLogger.error("CQL Query failed", e);
        }
        return unsecureWithoutAuthChecks(filter);
    }

    private List<String> unsecureWithoutAuthChecks(AttributeList filter) {
        // pixelMedHelper.setIsWSEnum(false);
        List<String> retrievedZipFile = new ArrayList<String>();
        String username = System.getenv("LOGNAME");
        if (username == null) {
            username = "";
        } else {
            username = "-" + username;
        }

        try {
            String[] manifest = new String[1];
            retrievedZipFile = pixelMedHelper.retrieve(filter, manifest);
            /*
             * String retrievedZipFilename = pixelMedHelper
             * .retrieveZippedCaching( filter, false,
             * (System.getProperty("java.io.tmpdir") + File.separator +
             * "DICOMCQLQueryProcessor-cache" + username));
             * retrievedZipFile.add(retrievedZipFilename);
             */
        } catch (Exception e) {
            myLogger.error("Attribute filtered query failed", e);
        }

        return retrievedZipFile;
    }

    private AttributeList createAuthorizedRetrieveQuery(Series series) {
        // Object study = (seriesClass.cast(series).getStudy();
        Study study = series.getStudy();

        AttributeList newFilter = new AttributeList();
        try {
            AttributeTag t = TagFromName.SeriesInstanceUID;
            Attribute a = AttributeFactory.newAttribute(t, map.getDicomDict().getValueRepresentationFromTag(t));
            a.addValue(series.getSeriesInstanceUID());
            newFilter.put(t, a);

            t = TagFromName.StudyInstanceUID;
            a = AttributeFactory.newAttribute(t, map.getDicomDict().getValueRepresentationFromTag(t));
            a.addValue(study.getStudyInstanceUID());
            newFilter.put(t, a);

            t = TagFromName.PatientID;
            a = AttributeFactory.newAttribute(t, map.getDicomDict().getValueRepresentationFromTag(t));
            a.addValue(study.getPatient().getPatientId());
            newFilter.put(t, a);

            t = TagFromName.QueryRetrieveLevel;
            a = AttributeFactory.newAttribute(t, map.getDicomDict().getValueRepresentationFromTag(t));
            a.addValue(map.getRetrieveLevel(InformationEntity.SERIES));
            newFilter.put(t, a);
        } catch (Exception e) {
            myLogger.error("Error in createAuthorizedRetrieveQuery", e);
        }
        return newFilter;
    }

    public void setIsWSEnum(boolean b) {
        pixelMedHelper.setIsWSEnum(true);

    }

    public List<String> getRetrievedFileNames() {
        return pixelMedHelper.getRetrievedFileNames();
    }

    public boolean isRetrievalRequest() {
        return isRetrievalRequest;
    }

}