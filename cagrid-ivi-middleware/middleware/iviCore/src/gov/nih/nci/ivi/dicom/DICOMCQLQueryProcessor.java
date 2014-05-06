/**
 * 
 */
package gov.nih.nci.ivi.dicom;

//import edu.emory.cci.security.auth.Authorization;
import edu.osu.bmi.security.authorization.AllDataAuthorization;
import edu.osu.bmi.security.authorization.AuthorizationException;
import edu.osu.bmi.security.authorization.DataAuthorization;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperDataAuthorization;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperUtil;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.cagrid.data.utilities.ResultsCreationException;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.File;
import java.io.FileReader;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.security.SecurityManager;
import org.xml.sax.InputSource;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;

/**
 * @author tpan
 * 
 */
public class DICOMCQLQueryProcessor extends LazyCQLQueryProcessor {
    public static final Logger myLogger = LogManager.getLogger(DICOMCQLQueryProcessor.class);

    public static final String DATA_STEM = "DICOM";
    public static final String DATA_AUTHORIZATION_CLASS = "dataAuthorizationClass";
    public static final String DATA_LEVEL_AUTH = "dataLevelAuth";
    public static final String CQL_QUERY_DATA_LEVEL_AUTH = "cqlQueryProcessorConfig_dataLevelAuth";
    public static final String ORGANIZATION = "organization";
    public static final String GRID_GROUPER_URL = "gridGrouperUrl";
    public static final String PERMISSION_SERVICE_URL = "permissionServiceUrl";

    private static final String MODELMAP_PROPERTIES = "modelMapProperties";
    private InputStream configStream;
    private PixelMedHelper pixelmedHelper;
    private ModelMap map;
    private DICOM2Model d2m;
    private Mappings ClassnameQNameMappings = null;

    private GridGrouperDataAuthorization authObj;
//    private Authorization authObj;
    private boolean dataLevelAuthorizationOn = true;
    private String datasource;
    private String org;

    /**
	 * 
	 */
    public DICOMCQLQueryProcessor() {
        super();
    }

    public Properties getRequiredParameters() {
        Properties props = new Properties();
        // props.put(MODELMAP_PROPERTIES,
        // "CHANGE ME:   "+"c:/Data/src/middleware/projects/iviCore/resources/modelmap/NCIAModelMap.properties");
        props.put(MODELMAP_PROPERTIES, "resources/modelmap/NCIAModelMap.properties");

        // ////////////////////////////////////////////////////////////////////////
        // Tony Pan says these are no longer used.
        // ////////////////////////////////////////////////////////////////////////
        // props.put(DATA_AUTHORIZATION_CLASS,
        // "edu.emory.cci.security.authorization.permission.PermissionServiceDataAuthorization");
        // props.put(PERMISSION_SERVICE_URL,
        // "https://localhost:8443/wsrf/services/cagrid/PermissionService");
        // ////////////////////////////////////////////////////////////////////////

        props.put(GRID_GROUPER_URL, "https://localhost:8443/wsrf/services/cagrid/GridGrouper");
        props.put(ORGANIZATION, "OSU");
        props.put(DATA_LEVEL_AUTH, "false");

        props.putAll(PixelMedHelper.getParameters());
        return props;
    }

    public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
        super.initialize(parameters, wsdd);

        myLogger.debug("Starting dicom init");

        createMappings();

        Properties pixelmedParams = this.getConfiguredParameters();

        configStream = wsdd;
        if (configStream == null) {
            myLogger.error("ERROR: configStream is null");
        }
        if (myLogger.isInfoEnabled()) {
            myLogger.info("Connecting to pacs at " + pixelmedParams.getProperty("serverip"));
        }
//        if (myLogger.isDebugEnabled()) {
//            myLogger.debug("model map file = " + String.class.cast(pixelmedParams.get(MODELMAP_PROPERTIES)));
//        }
        try {
            map = new ModelMap();
        } catch (Exception e) {
            myLogger.error("Error creating ModelMap while initializing DICOMCQLQueryProcessor", e);
            throw new InitializationException(e);
        }

        d2m = new DICOM2Model(map);
        d2m.debug4 = true;

        this.dataLevelAuthorizationOn = Boolean.parseBoolean(pixelmedParams.getProperty(DATA_LEVEL_AUTH));
        if (dataLevelAuthorizationOn) {
            myLogger.debug("Creating new Authorization object for DICOMCQLQueryProcessor");
            // get pacs AE title

            org = pixelmedParams.getProperty("organization");
            datasource = pixelmedParams.getProperty("serverAE");

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

        pixelmedHelper = new PixelMedHelper(pixelmedParams);
        myLogger.debug("Ending dicom init");
    }

    private void createMappings() {

        if (ClassnameQNameMappings == null)
            ClassnameQNameMappings = new Mappings();

        ClassToQname annotationMap = new ClassToQname();
        annotationMap.setClassName(gov.nih.nci.ncia.domain.Annotation.class.getName());
        annotationMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "Annotation").toString());

        ClassToQname curationDataMap = new ClassToQname();
        curationDataMap.setClassName(gov.nih.nci.ncia.domain.CurationData.class.getName());
        curationDataMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "CurationData").toString());

        ClassToQname trialDataProvenanceMap = new ClassToQname();
        trialDataProvenanceMap.setClassName(gov.nih.nci.ncia.domain.TrialDataProvenance.class.getName());
        trialDataProvenanceMap
                .setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "TrialDataProvenance").toString());

        ClassToQname clinicalTrialProtocolMap = new ClassToQname();
        clinicalTrialProtocolMap.setClassName(gov.nih.nci.ncia.domain.ClinicalTrialProtocol.class.getName());
        clinicalTrialProtocolMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain",
                "ClinicalTrialProtocol").toString());

        ClassToQname clinicalTrialSponsorMap = new ClassToQname();
        clinicalTrialSponsorMap.setClassName(gov.nih.nci.ncia.domain.ClinicalTrialSponsor.class.getName());
        clinicalTrialSponsorMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain",
                "ClinicalTrialSponsor").toString());

        ClassToQname clinicalTrialSubjectMap = new ClassToQname();
        clinicalTrialSubjectMap.setClassName(gov.nih.nci.ncia.domain.ClinicalTrialSubject.class.getName());
        clinicalTrialSubjectMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain",
                "ClinicalTrialSubject").toString());

        ClassToQname clinicalTrialSiteMap = new ClassToQname();
        clinicalTrialSiteMap.setClassName(gov.nih.nci.ncia.domain.ClinicalTrialSite.class.getName());
        clinicalTrialSiteMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "ClinicalTrialSite")
                .toString());

        ClassToQname equipmentMap = new ClassToQname();
        equipmentMap.setClassName(gov.nih.nci.ncia.domain.Equipment.class.getName());
        equipmentMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "Equipment").toString());

        ClassToQname patientMap = new ClassToQname();
        patientMap.setClassName(gov.nih.nci.ncia.domain.Patient.class.getName());
        patientMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "Patient").toString());

        ClassToQname studyMap = new ClassToQname();
        studyMap.setClassName(gov.nih.nci.ncia.domain.Study.class.getName());
        studyMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "Study").toString());

        ClassToQname seriesMap = new ClassToQname();
        seriesMap.setClassName(gov.nih.nci.ncia.domain.Series.class.getName());
        seriesMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "Series").toString());

        ClassToQname imageMap = new ClassToQname();
        imageMap.setClassName(gov.nih.nci.ncia.domain.Image.class.getName());
        imageMap.setQname(new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", "Image").toString());

        ClassnameQNameMappings.setMapping(new ClassToQname[] { annotationMap, trialDataProvenanceMap,
                clinicalTrialProtocolMap, clinicalTrialSponsorMap, clinicalTrialSubjectMap, clinicalTrialSiteMap,
                equipmentMap, patientMap, studyMap, curationDataMap, seriesMap, imageMap });

    }

    public Iterator<?> processQueryLazy(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        List<?> coreResultsList = queryDICOM(cqlQuery);
        return coreResultsList.iterator();
    }

    public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
        myLogger.debug("Entering DICOMCQLQueryProcessor.processQuery");

        List<?> coreResultsList = null;
        if (dataLevelAuthorizationOn) {
            String user = SecurityManager.getManager().getCaller();
            myLogger.debug("Processing query for user = " + user);
            try {
                coreResultsList = queryDICOMSecurely(cqlQuery, user, authObj);
            } catch (AuthorizationException e) {
                myLogger.error("Authorization problem wile processing query", e);
            }
        } else {
            myLogger.debug("Processing unsecured query.");
            coreResultsList = queryDICOM(cqlQuery);
        }
        myLogger.debug("Returned from processing query");

        if (coreResultsList == null || coreResultsList.isEmpty()) {
            myLogger.debug("Query produced null or empty results.");
            return new CQLQueryResults(null, null, null, null, cqlQuery.getTarget().getName());
        }
        myLogger.debug("Raw result set produced by query is not empty.");

        CQLQueryResults results = null;
        try {
            if (cqlQuery.getQueryModifier() == null) {
                myLogger.debug("No query modifier to process.");
                results = CQLResultsCreationUtil.createObjectResults(coreResultsList, cqlQuery.getTarget().getName(),
                        ClassnameQNameMappings);
            } else if (cqlQuery.getQueryModifier().isCountOnly()) {
                myLogger.info("Processing query for result count");
                results = CQLResultsCreationUtil.createCountResults(coreResultsList.size(), cqlQuery.getTarget()
                        .getName());
            } else if (cqlQuery.getQueryModifier().getDistinctAttribute() != null) {
                myLogger.info("Processing query for distinct attribute values");
                String[] distinctAttributes = new String[1];
                distinctAttributes[0] = cqlQuery.getQueryModifier().getDistinctAttribute();
                List<String[]> attribValuesList = generateDistinctAttributeValues(cqlQuery, coreResultsList);
                if (myLogger.isDebugEnabled()) {
                    myLogger.debug("Attribute count = " + attribValuesList.size());
                    myLogger.debug("Attribute value count = " + ((String[]) attribValuesList.get(0)).length);
                }
                results = CQLResultsCreationUtil.createAttributeResults(attribValuesList, cqlQuery.getTarget()
                        .getName(), distinctAttributes);
            } else if (cqlQuery.getQueryModifier().getAttributeNames() != null) {
                String msg = "Processing query for attribute names is not supported yet";
                myLogger.error(msg);
                throw new QueryProcessingException(msg);
            }
            myLogger.info("dicom processQuery finished");
            return results;
        } catch (ResultsCreationException e) {
            myLogger.error("DICOMCQLQueryProcessor.processQuery had a problem assemlbin query results", e);
        }
        return null;
    }

    protected static List<String[]> generateDistinctAttributeValues(CQLQuery cqlQuery, List<?> coreResultsList)
            throws QueryProcessingException {
        String distinctAttribute = cqlQuery.getQueryModifier().getDistinctAttribute();
        myLogger.debug("Processing query at the attribute level for the " + distinctAttribute + " attribute");
        HashMap<String, ?> distinctAttributes = new HashMap<String, Object>();
        for (int i = 0; i < coreResultsList.size(); i++) {
            Class<?> targetClass = null;
            String className = cqlQuery.getTarget().getName();
            myLogger.debug("Target class name " + className);
            try {
                targetClass = Class.forName(className);
            } catch (ClassNotFoundException e) {
                myLogger.error("No class found for " + className, e);
            }
            if (targetClass == null) {
                throw new QueryProcessingException("ERROR: can't instantiate target class " + className);
            }
            Object obj = (Object) coreResultsList.get(i);
            Field[] fields = targetClass.getDeclaredFields();
            myLogger.debug("Number of fields = " + fields.length);
            // for (int j = 0; j < fields.length; j++) {
            // System.out.println(fields[j].getName());
            // }
            Field field = null;
            try {
                field = targetClass.getDeclaredField(distinctAttribute);
                field.setAccessible(true);
            } catch (Exception e) {
                myLogger.error("Error setting field access enforcement" + distinctAttribute, e);
            }
            if (myLogger.isDebugEnabled()) {
                myLogger.debug("Field name " + field.getName());
            }
            String fieldValue = null;
            try {
                Object fieldValueObject = field.get(obj);
                fieldValue = (fieldValueObject == null) ? null : fieldValueObject.toString();
            } catch (Exception e) {
                myLogger.error("Error getting field value", e);
            }
            myLogger.debug("Field value " + fieldValue);
            if (!distinctAttributes.containsKey(fieldValue)) {
                distinctAttributes.put(fieldValue, null);
                myLogger.debug("added field value: " + fieldValue);
            }
        }
        Set<String> keySet = distinctAttributes.keySet();
        ArrayList<String[]> list = new ArrayList<String[]>();
        if (keySet.size() > 0) {
            Iterator<String> iterator = keySet.iterator();
            int count = 0;
            while (iterator.hasNext()) {
                String[] array = new String[1];
                array[0] = (String) iterator.next();
                list.add(array);
                count++;
            }
        }

        return list;
    }

    protected List<?> queryDICOM(CQLQuery cqlQuery) throws QueryProcessingException {
        myLogger.debug("queryDICOM Querying database r3");

        // temp
        CQL2DICOM cql2 = createCql2Dicom();

        AttributeList filter = null;
        List<?> output = new ArrayList<Object>();
        // QueryTreeModel tree;
        try {
            filter = cql2.cqlToPixelMed(cqlQuery);
        } catch (DicomException e) {
            myLogger.error("DICOM from CQL error: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            myLogger.error("Error converting from CQL to DICOM", e);
        }

        if (myLogger.isDebugEnabled()) {
            myLogger.debug("querying with " + filter);
        }
        try {
            output = pixelmedHelper.queryFind(filter, this.map, cqlQuery.getTarget().getName());
            // tree = pixelmedHelper.query(filter);
        } catch (Exception e) {
            myLogger.error("queryDICOM caught an exception from PixelmedHelper.queryFind", e);
            throw new QueryProcessingException("processing " + filter.toString());
        }

        // parse the tree
        // navigate through the tree to the requested depth. remember that
        // tree is rooted at study level, not patient, so the tree depth
        // is actually 1 shorter than the dicom hierarchy. the
        // requested depth is gotten from the CQL2DICOM
        /*
         * Class targetClass = null; try { targetClass =
         * Class.forName(cqlQuery.getTarget().getName()); } catch
         * (ClassNotFoundException e) { e.printStackTrace(); } if (targetClass
         * == null) { throw new
         * QueryProcessingException("ERROR: can't instantiate target class"); }
         * 
         * try { output = d2m.dicomToModelObject(tree, targetClass); } catch
         * (InstantiationException e) { e.printStackTrace(); } catch
         * (IllegalAccessException e) { e.printStackTrace(); } catch
         * (DataConversionException e) { e.printStackTrace(); }
         * 
         * // the results are expected to be fully materialized (series contain
         * study contain patient) System.out.println("DICOM OUTPUT");
         * System.out.println(output.toString());
         */
        return output;
    }

    /**
     * @return
     * @throws QueryProcessingException
     */
    private CQL2DICOM createCql2Dicom() throws QueryProcessingException {
        try {
            return new CQL2DICOM(map);
        } catch (ModelMapException e1) {
            String msg = "Error constructing CQL2ICOM object";
            myLogger.error(msg, e1);
            throw new QueryProcessingException(msg, e1);
        }
    }
    
//    public List<?> queryDICOMSecurely(CQLQuery cqlQuery, String user, Authorization auth1)
    public List<?> queryDICOMSecurely(CQLQuery cqlQuery, String user, DataAuthorization auth1)
            throws QueryProcessingException, AuthorizationException {
        myLogger.debug("Entering queryDICOMSecurely");

        CQL2DICOM cql2 = createCql2Dicom();

        AttributeList filter = null;
        try {
            filter = cql2.cqlToPixelMed(cqlQuery);
        } catch (DicomException e) {
            myLogger.error("DICOM from CQL error: " + e.getLocalizedMessage(), e);
        } catch (Exception e) {
            myLogger.error("Error converting from CQL to DICOM", e);
        }
        DataAuthorization localAuth = getAuthorization(auth1, user, filter);

        if (myLogger.isDebugEnabled()) {
            myLogger.debug("querying with " + filter);
        }

        List<?> output;
        try {
            System.out.println("querying with " + filter);
//            output = pixelmedHelper.queryFindSecure(filter, this.map, cqlQuery.getTarget().getName(), user, auth1);
            output = pixelmedHelper.queryFindSecure(filter, this.map, cqlQuery.getTarget().getName(), user, localAuth);
            // tree = pixelmedHelper.query(filter);
        } catch (Exception e) {
            throw new QueryProcessingException("processing " + filter.toString(), e);
        }
        myLogger.debug("Exiting queryDICOMSecurely");
        return output;
    }

    /**
     * @param authObj
     * @param user
     * @param filter
     * @return
     * @throws AuthorizationException
     */
    private DataAuthorization getAuthorization(DataAuthorization auth, String user, AttributeList filter)
            throws AuthorizationException {
        // now check for explicit queries and see if it's explicitly disallowed.
        int permission = getPermission(auth, user, filter);
        System.out.println("permission for query is " + permission);
        DataAuthorization newAuth = auth;
        if (dataLevelAuthorizationOn) {
            if (!DataAuthorization.isDeferred(permission, DataAuthorization.READ)) {
                // if there is no delegation, then the decision is made right
                // here
                if ((permission & DataAuthorization.READ) != DataAuthorization.READ) {
                    // if this is not a read operation, then there is no authObj
                    throw new AuthorizationException("ERROR: access to data is not authorized for "
                            + filter.toString(this.map.getDicomDict()));
                } else {
                    // permission is established, so just use the AllDataAuth
                    // object
                    newAuth = new AllDataAuthorization();
                }
            } // need to delegate, so use the original authObj object
        } else {
            // no permission checking, use the all authObj object
            newAuth = new AllDataAuthorization();
        }
        return newAuth;
    }

    private int getPermission(DataAuthorization auth, String user, AttributeList filter) {
        String[] UIDs = new String[3];

        AttributeTag t = TagFromName.QueryRetrieveLevel;
        Attribute a = filter.get(t);
        String retrieveLevel = a.getSingleStringValueOrNull();
        if (retrieveLevel == null) {
            // no information in query to allow determination
            return DataAuthorization.NONE;
        }

        // get the UIDs
        InformationEntity qrLevel = null;
        try {
            qrLevel = map.getInformationEntityFromModelClass(map.getModelClassFromQueryRetrieveLevel(retrieveLevel));
        } catch (ModelMapException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (qrLevel != null) {

            System.out.println("QRLevel is " + qrLevel.toString());
            if (qrLevel.compareTo(InformationEntity.PATIENT) >= 0) {
                t = TagFromName.PatientID;
                a = filter.get(t);
                if (a != null)
                    UIDs[0] = a.getSingleStringValueOrNull();
                System.out.println("Patient : " + UIDs[0]);

                if (qrLevel.compareTo(InformationEntity.STUDY) >= 0) {
                    t = TagFromName.StudyInstanceUID;
                    a = filter.get(t);
                    if (a != null)
                        UIDs[1] = a.getSingleStringValueOrNull();
                    System.out.println("study : " + UIDs[1]);

                    if (qrLevel.compareTo(InformationEntity.SERIES) >= 0) {
                        t = TagFromName.SeriesInstanceUID;
                        a = filter.get(t);
                        if (a != null)
                            UIDs[2] = a.getSingleStringValueOrNull();
                        System.out.println("series : " + UIDs[2]);

                    }
                }
            }
        }

        // now get the user ID
        // now check to see if user has permission.
        System.out.println("Authorizing user " + user + " for "
                + GridGrouperUtil.toStem(DICOMCQLQueryProcessor.DATA_STEM, org, datasource, UIDs));
        return auth.authorize(user, GridGrouperUtil.toStem(DICOMCQLQueryProcessor.DATA_STEM, org, datasource, UIDs),
                DataAuthorization.READ);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        DICOMCQLQueryProcessor processor = new DICOMCQLQueryProcessor();
        try {
            Properties params = processor.getRequiredParameters();
            // params.put("serverip", "192.168.126.128");
            params.put("serverip", "127.0.0.1");
            processor.initialize(params, null);
        } catch (InitializationException e) {
            e.printStackTrace();
        }

        String path = "resources";
        // path = "/home/rutt/dev/ivi/ivi-vc/middleware/resources";
        String[] filenames = { path = path + File.separator + "nciaCQL2-seriesOnly.xml" };
        /*
         * path + File.separator + "nciaCQL2-patientOnly.xml" path +
         * File.separator + "nciaCQL2-studyOnly.xml", path + File.separator +
         * "nciaCQL2-seriesOnly.xml",
         * 
         * 
         * try { Class resultClass=
         * Class.forName("gov.nih.nci.ncia.domain.Study"); Method[] methods =
         * resultClass.getMethods();
         * 
         * System.out.println("Testing capability of java reflect loading"); for
         * (int i = 0; i < methods.length; i++ ){
         * System.out.println(methods[i].getName()); } } catch
         * (ClassNotFoundException e1) { e1.printStackTrace(); }
         */
        try {
            for (int i = 0; i < filenames.length; i++) {
                InputSource queryInput = new InputSource(new FileReader(filenames[i]));
                CQLQuery query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
                CQLQueryResults results = processor.processQuery(query);
                /*
                 * CQLQueryResultsIterator iter2 = new
                 * CQLQueryResultsIterator(results); int ii = 1; while
                 * (iter2.hasNext()) { java.lang.Object obj = iter2.next(); if
                 * (obj == null) {
                 * System.out.println("something not right.  obj is null");
                 * continue; } Study fullResult = Study.class.cast(obj);
                 * System.out.println("Result " + ii++ + ". ");
                 * System.out.println(ObjectSerializer.toString(fullResult, new
                 * QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain",
                 * obj.getClass().getName()))); Patient assocPatient =
                 * fullResult.getPatient();
                 * System.out.println(ObjectSerializer.toString(assocPatient,
                 * new QName("gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain",
                 * assocPatient.getClass().getName())));
                 * 
                 * }
                 */
                System.out.println(ObjectSerializer.toString(results, new QName(
                        "http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLResultSet", "CQLQueryResultCollection")));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
