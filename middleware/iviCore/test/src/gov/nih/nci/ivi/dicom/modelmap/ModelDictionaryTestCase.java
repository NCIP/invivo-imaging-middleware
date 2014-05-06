package gov.nih.nci.ivi.dicom.modelmap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Vector;

import junit.framework.TestCase;

public class ModelDictionaryTestCase extends TestCase {
	
	

	private static final String BAD_HIERARCHY = "resources/modelmap/BadHierarchy.properties";
	private static final String NON_EXIST_HIERARCHY = "resources/modelmap/MissingHierarchy.properties";
	private static final String BMI_HIERARCHY = "resources/modelmap/ModelHierarchy.properties";
	private static final String NCIA_HIERARCHY = "resources/modelmap/NCIAModelHierarchy.properties";
	private static final String NCIA_PATIENT = "gov.nih.nci.ncia.domain.Patient";
	private static final String BMI_PATIENT = "edu.osu.bmi.dicom.PatientType";
	private static final String NCIA_SERIES = "gov.nih.nci.ncia.domain.Series";
	private static final String BMI_SERIES = "edu.osu.bmi.dicom.SeriesType";

	private Class bmiPatientClass = null;
	private Class bmiSeriesClass = null;
	private Class nciaPatientClass = null;
	private Class nciaSeriesClass = null;
	
	private ModelDictionary nciaDict = null;
	
	public ModelDictionaryTestCase() {
		// set up the class files
		try {
			bmiPatientClass = Class.forName(BMI_PATIENT);
			assertNotNull("can't create bmi patient class", bmiPatientClass);
		} catch (ClassNotFoundException e) {
			fail("can't create " + bmiPatientClass.getCanonicalName());
		}
		
		try {
			nciaPatientClass = Class.forName(NCIA_PATIENT);
			assertNotNull("can't create ncia patient class", nciaPatientClass);
		} catch (ClassNotFoundException e) {
			fail("can't create " + nciaPatientClass.getCanonicalName());
		}

		try {
			bmiSeriesClass = Class.forName(BMI_SERIES);
			assertNotNull("can't create bmi series class", bmiSeriesClass);
		} catch (ClassNotFoundException e) {
			fail("can't create " + bmiSeriesClass.getCanonicalName());
		}
		
		try {
			nciaSeriesClass = Class.forName(NCIA_SERIES);
			assertNotNull("can't create ncia series class", nciaSeriesClass);
		} catch (ClassNotFoundException e) {
			fail("can't create " + nciaSeriesClass.getCanonicalName());
		}

		
		// check to make sure the hierarchy file is available
		File f = new File(BMI_HIERARCHY);
		assertTrue("Hierarhcy Properties file does not exist", f.exists());
		assertTrue("Hierarhcy Properties file can be read", f.canRead());

		f = new File(NCIA_HIERARCHY);
		assertTrue("NCIA Hierarhcy Properties file does not exist", f.exists());
		assertTrue("NCIA Hierarhcy Properties file can be read", f.canRead());

	
	}

	protected void setUp() throws Exception {
		super.setUp();
		
		nciaDict = new ModelDictionary(new File("resources/modelmap/NCIAModelHierarchy.properties"));
		assertNotNull(nciaDict);
		nciaDict.initialize(gov.nih.nci.ncia.domain.TrialDataProvenance.class);
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
		nciaDict = null;
	}

	public void testModelDictionaryBooleanFalse() {
		try {
			new ModelDictionary(false);
		} catch (ModelMapException e) {
			fail("instantiation failed for false for parse hierarchy");
		}
	}

	public void testModelDictionaryBooleanTrue() {
		try {
			new ModelDictionary(true);
		} catch (ModelMapException e) {
			fail("instantiation failed for true for parse hierarchy");
		}
	}

	public void testModelDictionaryFileNull() {
		try {
			new ModelDictionary((File)null);
			fail("instantiation with null Hierarchy file, should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void testModelDictionaryFileNonexistent() {
		try {
			new ModelDictionary(new File(NON_EXIST_HIERARCHY));
			fail("instantiation with nonexistent hierarchy file, should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void testModelDictionaryFileBad() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY));
			fail("instantiation with mal-formed hierarchy, should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void testModelDictionaryFileBMI() {
		try {
			new ModelDictionary(new File(BMI_HIERARCHY));
		} catch (ModelMapException e) {
			fail("instantiation failed for BMI hierarchy");
		} catch (FileNotFoundException e) {
			fail("instantiation failed for BMI hierarchy");
		} catch (IOException e) {
			fail("instantiation failed for BMI hierarchy");
		}
	}

	public void testModelDictionaryFileNCIA() {
		try {
			new ModelDictionary(new File(NCIA_HIERARCHY));
		} catch (ModelMapException e) {
			fail("instantiation failed for NCIA hierarchy");
		} catch (FileNotFoundException e) {
			fail("instantiation failed for NCIA hierarchy");
		} catch (IOException e) {
			fail("instantiation failed for NCIA hierarchy");
		}
	}

	
	public void testInitializeClassNull_BooleanFalse() {
		try {
			new ModelDictionary(false).initialize((Class)null);
			fail("instantiation with null class, false parseHierarchy, should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	public void testInitializeClassNull_BooleanTrue() {
		try {
			new ModelDictionary(true).initialize((Class)null);
			fail("instantiation with null class, true parseHierarchy, should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	

	public void testInitializeClassBMIPatient_BooleanFalse() {
		try {
			ModelDictionary dict = new ModelDictionary(false);
			assertNotNull(dict);
			dict.initialize(bmiPatientClass);
		} catch (ModelMapException e) {
			fail("instantiation with BMIPatient class, false parseHierarchy, did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with BMIPatient class, false parseHierarchy, did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with BMIPatient class, false parseHierarchy, did not complete " + e.getMessage());
		}
	}

	public void testInitializeClassBMIPatient_BooleanTrue() {
		try {
			ModelDictionary dict = new ModelDictionary(true);
			assertNotNull(dict);
			dict.initialize(bmiPatientClass);
		} catch (ModelMapException e) {
			fail("instantiation with BMIPatient class, true parseHierarchy, did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		}
	}
	
	public void testInitializeClassNCIAPatient_BooleanFalse() {
		try {
			ModelDictionary dict = new ModelDictionary(false);
			assertNotNull(dict);
			dict.initialize(nciaPatientClass);
		} catch (ModelMapException e) {
			fail("instantiation with NCIAPatient class, false parseHierarchy, did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		}
	}
	
	public void testInitializeClassNCIAPatient_BooleanTrue() {
		try {
			ModelDictionary dict = new ModelDictionary(true);
			assertNotNull(dict);
			dict.initialize(nciaPatientClass);
		} catch (ModelMapException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with NCIAPatient class, true parseHierarchy, did not complete " + e.getMessage());
		}
	}

		
	public void testInitializeFileNCIA_ClassNull() {
		try {
			new ModelDictionary(new File(NCIA_HIERARCHY)).initialize((Class)null);
			fail("instantiation with ncia hierarchy file and null root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBMI_ClassNull() {
		try {
			new ModelDictionary(new File(BMI_HIERARCHY)).initialize((Class)null);
			fail("instantiation with bmi hierarchy file and null root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBad_ClassNull() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY)).initialize((Class)null);
			fail("instantiation with bad hierarchy file and null root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public void testInitializeFileNCIA_ClassBMIPatient() {
		try {
			new ModelDictionary(new File(NCIA_HIERARCHY)).initialize(bmiPatientClass);
			fail("instantiation with ncia hierarchy file and bmi Patient root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBMI_ClassBMIPatient() {
		try {
			ModelDictionary dict = new ModelDictionary(new File(BMI_HIERARCHY));
			assertNotNull(dict);
			dict.initialize(bmiPatientClass);
		} catch (ModelMapException e) {
			fail("instantiation with bmi hierarchy file and bmi Patient root class did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with bmi hierarchy file and bmi Patient root class did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with bmi hierarchy file and bmi Patient root class did not complete " + e.getMessage());
		}
	}
	public void testInitializeFileBad_ClassBMIPatient() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY)).initialize(bmiPatientClass);
			fail("instantiation with bad hierarchy file and bmi Patient root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public void testInitializeFileNCIA_ClassBMISeries() {
		try {
			new ModelDictionary(new File(NCIA_HIERARCHY)).initialize(bmiSeriesClass);
			fail("instantiation with ncia hierarchy file and bmi Series root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBMI_ClassBMISeries() {
		try {
			ModelDictionary dict = new ModelDictionary(new File(BMI_HIERARCHY));
			assertNotNull(dict);
			dict.initialize(bmiSeriesClass);
		} catch (ModelMapException e) {
			fail("instantiation with bmi hierarchy file and bmi Series root class did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with bmi hierarchy file and bmi Series root class did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with bmi hierarchy file and bmi Series root class did not complete " + e.getMessage());
		}
	}
	public void testInitializeFileBad_ClassBMISeries() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY)).initialize(bmiSeriesClass);
			fail("instantiation with bad hierarchy file and bmi Series root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public void testInitializeFileNCIA_ClassNCIAPatient() {
		try {
			ModelDictionary dict = new ModelDictionary(new File(NCIA_HIERARCHY));
			assertNotNull(dict);
			dict.initialize(nciaPatientClass);
		} catch (ModelMapException e) {
			e.printStackTrace();
			fail("instantiation with ncia hierarchy file and ncia Patient root class did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with ncia hierarchy file and ncia Patient root class did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with ncia hierarchy file and ncia Patient root class did not complete " + e.getMessage());
		}
	}
	public void testInitializeFileBMI_ClassNCIAPatient() {
		try {
			new ModelDictionary(new File(BMI_HIERARCHY)).initialize(nciaPatientClass);
			fail("instantiation with bmi hierarchy file and ncia Patient root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBad_ClassNCIAPatient() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY)).initialize(nciaPatientClass);
			fail("instantiation with bad hierarchy file and ncia Patient root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	
	public void testInitializeFileNCIA_ClassNCIASeries() {
		try {
			ModelDictionary dict = new ModelDictionary(new File(NCIA_HIERARCHY));
			assertNotNull(dict);
			dict.initialize(nciaSeriesClass);
		} catch (ModelMapException e) {
			fail("instantiation with ncia hierarchy file and ncia Series root class did not complete " + e.getMessage());
		} catch (FileNotFoundException e) {
			fail("instantiation with ncia hierarchy file and ncia Series root class did not complete " + e.getMessage());
		} catch (IOException e) {
			fail("instantiation with ncia hierarchy file and ncia Series root class did not complete " + e.getMessage());
		}
	}
	public void testInitializeFileBMI_ClassNCIASeries() {
		try {
			new ModelDictionary(new File(BMI_HIERARCHY)).initialize(nciaSeriesClass);
			fail("instantiation with bmi hierarchy file and ncia Series root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBad_ClassNCIASeries() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY)).initialize(nciaSeriesClass);
			fail("instantiation with bad hierarchy file and ncia Series root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}

	
	
	public void testInitializeFileNCIA_ClassBad() {
		try {
			new ModelDictionary(new File(NCIA_HIERARCHY)).initialize(java.lang.String.class);
			fail("instantiation with ncia hierarchy file and bad root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBMI_ClassBad() {
		try {
			new ModelDictionary(new File(BMI_HIERARCHY)).initialize(java.lang.String.class);
			fail("instantiation with bmi hierarchy file and bad root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	public void testInitializeFileBad_ClassBad() {
		try {
			new ModelDictionary(new File(BAD_HIERARCHY)).initialize(java.lang.String.class);
			fail("instantiation with bad hierarchy file and bad root class should not complete");
		} catch (ModelMapException e) {
		} catch (FileNotFoundException e) {
		} catch (IOException e) {
		}
	}
	


	public void testGetAttributeNameContainsNull() {
		try {
			nciaDict.getAttributeNameContains((String)null);
			fail("getAttributeNameContains with null as input should fail");
		} catch (ModelMapException e) {
		}
	}
	public void testGetAttributeNameContainsEmpty() {
		try {
			Collection<String> result = nciaDict.getAttributeNameContains("");
			assertNotNull("result is null", result);
			assertTrue("result size should be all", result != null && result.size() >= 0);
		} catch (ModelMapException e) {
			fail("getAttributeNameContains with empty string as parameter should pass with all fiels returned.");
		}
	}

	public void testGetAttributeNameContainsNCIAValid() {
		try {
			Collection<String> result = nciaDict.getAttributeNameContains("series");
			assertNotNull("result is null", result);
			assertTrue("result should not be empty", result != null && result.size() > 0);
		} catch (ModelMapException e) {
			fail("getAttributeNameContains with correct string as parameter should pass.");
		}
	}

	public void testGetAttributeNameContainsNCIAInvalid() {
		try {
			Collection<String> result = nciaDict.getAttributeNameContains("something");
			assertNotNull("result is null", result);
			assertTrue("result size should be 0", result != null && result.size() == 0);
		} catch (ModelMapException e) {
			fail("getAttributeNameContains with incorrect string as parameter should return 0 sized collection.");
		}
	}
	
	public void testGetAttributeNameMatchesNull() {
		try {
			nciaDict.getAttributeNameMatches((String)null);
			fail("getAttributeNameMatches with null as input should fail");
		} catch (ModelMapException e) {
		}
	}
	public void testGetAttributeNameMatchesEmpty() {
		try {
			Collection<String> result = nciaDict.getAttributeNameMatches("");
			assertNotNull("result is null", result);
			assertTrue("result size should be empty", result != null && result.size() == 0);
		} catch (ModelMapException e) {
			fail("getAttributeNameMatches with empty string as parameter should pass with all fiels returned.");
		}
	}

	public void testGetAttributeNameMatchesNCIAValid() {
		try {
			Collection<String> result = nciaDict.getAttributeNameMatches("seriesDate");
			assertNotNull("result is null", result);
			assertTrue("result should not be empty", result != null && result.size() > 0);
		} catch (ModelMapException e) {
			fail("getAttributeNameMatches with correct string as parameter should pass.");
		}
	}

	public void testGetAttributeNameMatchesNCIAInvalid() {
		try {
			Collection<String> result = nciaDict.getAttributeNameMatches("something");
			assertNotNull("result is null", result);
			assertTrue("result size should be 0", result != null && result.size() == 0);
		} catch (ModelMapException e) {
			fail("getAttributeNameMatches with incorrect string as parameter should return 0 sized collection.");
		}
	}

	public void testGetAttributeNamesFromInformationEntityClassNull() {
		try {
			nciaDict.getAttributeNamesFromInformationEntityClass((Class)null);
			fail("should not be able to get attributes with null class");
		} catch (ModelMapException e) {
		}
	}
	public void testGetAttributeNamesFromInformationEntityClassNCIASeries() {
		try {
			HashSet<String> result = nciaDict.getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Series.class);
			assertNotNull("result should not be null ", result);
			assertTrue("result should not be empty ", result.size() > 0);
			assertTrue("result should not contain series ", result.contains("gov.nih.nci.ncia.domain.Series.modality"));
		} catch (ModelMapException e) {
			fail("should be able to get attributes with ncia series class");
		}
	}
/*	public void testGetAttributeNamesFromInformationEntityClassBMISeries() {
		try {
			HashSet<String> result = nciaDict.getAttributeNamesFromInformationEntityClass(edu.osu.bmi.dicom.SeriesType.class);
			assertNull("should not be able to get attributes with incorrect class", result);
		} catch (ModelMapException e) {
		}
	}
*/	

	public void testGetInformationEntityClassFromAttributeNameNull() {
		try {
			nciaDict.getInformationEntityClassFromAttributeName((String)null);
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetInformationEntityClassFromAttributeNameEmpty() {
		try {
			nciaDict.getInformationEntityClassFromAttributeName("");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetInformationEntityClassFromAttributeNameNotQualified() {
		try {
			nciaDict.getInformationEntityClassFromAttributeName("studyInstanceUID");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetInformationEntityClassFromAttributeNameNCIA() {
		try {
			Class result = nciaDict.getInformationEntityClassFromAttributeName("gov.nih.nci.ncia.domain.Series.seriesInstanceUID");
			assertNotNull("result class should not be null", result);
		} catch (ModelMapException e) {
			fail("should not have failed.");
		}		
	}
	public void testGetInformationEntityClassFromAttributeNameBMI() {
		try {
			Class result = nciaDict.getInformationEntityClassFromAttributeName("edu.osu.bmi.dicom.StudyInstanceUIDType._value");
			assertNull("result class should be null for bmi attribute", result);
		} catch (ModelMapException e) {
			//fail("should not be able to get a result object from bmi attribute");
		}
	}

	public void testGetParentClassFromInformationEntityClassNull() {
		try {
			nciaDict.getParentClassFromInformationEntityClass((Class)null);
			fail("call should not complete");
		} catch (ModelMapException e) {
		}
	}
	public void testGetParentClassFromInformationEntityClassRandom() {
		try {
			Class result = nciaDict.getParentClassFromInformationEntityClass(java.lang.String.class);
			assertNull("result should be null", result);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
/*	public void testGetParentClassFromInformationEntityClassBMI() {
		try {
			Class result = nciaDict.getParentClassFromInformationEntityClass(edu.osu.bmi.dicom.SeriesType.class);	
			assertNull("result should be null", result);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
*/	public void testGetParentClassFromInformationEntityClassNCIA() {
		try {
			Class result = nciaDict.getParentClassFromInformationEntityClass(gov.nih.nci.ncia.domain.Series.class);
			assertNotNull("result should not be null", result);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}

	
	public void testGetLineageNull() {
		try {
			nciaDict.getLineage((Class)null);
			fail("should not be able to get lineage of null class");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetLineageRandom() {
		try {
			Vector<Class> result = nciaDict.getLineage(java.lang.String.class);
			assertNotNull(result);
			assertTrue("should return empty for incorrect class", result.size() == 0);
		} catch (ModelMapException e) {
			fail("should not reach here.");
		}		
	}
/*	public void testGetLineageBMI() {
		try {
			Vector<Class> result = nciaDict.getLineage(edu.osu.bmi.dicom.SeriesType.class);
			assertNotNull(result);
			assertTrue("should return empty for incorrect class", result.size() == 0);
		} catch (ModelMapException e) {
			fail("should not reach here.");
		}		
	}
*/	public void testGetLineageNCIA() {
		try {
			Vector<Class> result = nciaDict.getLineage(gov.nih.nci.ncia.domain.Series.class);
			assertNotNull("should return null for incorrect class", result);
			assertTrue("should return non-empty for correct class", result.size() > 0);
		} catch (ModelMapException e) {
			fail("should not reach here.");
		}		
	}


	public void testGetAttributeClassFromAttributeNameNull() {
		try {
			nciaDict.getAttributeClassFromAttributeName((String)null);
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetAttributeClassFromAttributeNameEmpty() {
		try {
			nciaDict.getAttributeClassFromAttributeName("");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetAttributeClassFromAttributeNameNotQualified() {
		try {
			nciaDict.getAttributeClassFromAttributeName("studyInstanceUID");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		}		
	}
	public void testGetAttributeClassFromAttributeNameNCIA() {
		try {
			Class result = nciaDict.getAttributeClassFromAttributeName("gov.nih.nci.ncia.domain.Series.seriesInstanceUID");
			assertNotNull("result class should not be null", result);
		} catch (ModelMapException e) {
			fail("should not have failed.");
		}		
	}
	public void testGetAttributeClassFromAttributeNameBMI() {
		try {
			Class result = nciaDict.getAttributeClassFromAttributeName("edu.osu.bmi.dicom.StudyInstanceUIDType._value");
			assertNull("result class should be null for bmi attribute", result);
		} catch (ModelMapException e) {
			//fail("should not be able to get a result object from bmi attribute");
		}
	}


	public void testGetChildFieldsFromClassNull() {
		try {
			nciaDict.getChildFieldsFromClass((Class)null);
			fail("call should not complete");
		} catch (ModelMapException e) {
		}
	}
	public void testGetChildFieldsFromClassRandom() {
		try {
			Collection<Field> result = nciaDict.getChildFieldsFromClass(java.lang.String.class);
			assertNull("result should be null", result);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
/*	public void testGetChildFieldsFromClassBMI() {
		try {
			Collection<Field> result = nciaDict.getChildFieldsFromClass(edu.osu.bmi.dicom.SeriesType.class);	
			assertNull("result should be null", result);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
*/	public void testGetChildFieldsFromClassNCIA() {
		try {
			Collection<Field> result = nciaDict.getChildFieldsFromClass(gov.nih.nci.ncia.domain.Series.class);
			assertNotNull("result should not be null", result);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}

	public void testGetSettersFromAttributeNameNull() {
		try {
			nciaDict.getSettersFromAttributeName((String)null);
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
	public void testGetSettersFromAttributeNameEmpty() {
		try {
			nciaDict.getSettersFromAttributeName("");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
	public void testGetSettersFromAttributeNameNotQualified() {
		try {
			nciaDict.getSettersFromAttributeName("studyInstanceUID");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
	public void testGetSettersFromAttributeNameNCIA() {
		try {
			Vector<Method> result = nciaDict.getSettersFromAttributeName("gov.nih.nci.ncia.domain.Series.seriesInstanceUID");
			assertNotNull("result class should not be null", result);
		} catch (ModelMapException e) {
			fail("should not have failed.");
		} catch (SecurityException e) {
			fail("should not have failed.");
		} catch (NoSuchMethodException e) {
			fail("should not have failed.");
		}		
	}
	public void testGetSettersFromAttributeNameNCIAChild() {
		try {
			nciaDict.getSettersFromAttributeName("edu.osu.bmi.dicom.Series.generalImageCollection");
			fail("result class should not be created when using the wrong attribute name");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}
	public void testGetSettersFromAttributeNameBMI() {
		try {
			nciaDict.getSettersFromAttributeName("edu.osu.bmi.dicom.StudyInstanceUIDType._value");
			fail("result class should not be created when using the wrong attribute name");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	public void testGetSettersFromFieldNull() {
		try {
			nciaDict.getSettersFromField((Field)null);
			fail("should not be able to get lineage of null class");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
/*	public void testGetSettersFromFieldBMI() {
		try {
			Vector<Method> result = nciaDict.getSettersFromField(edu.osu.bmi.dicom.SeriesType.class.getDeclaredField("IMAGE_MODAL_TP_TXT"));
			assertNotNull(result);
			assertTrue("should return empty for incorrect class", result.size() == 0);
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (NoSuchFieldException e) {
		}		
	}
	public void testGetSettersFromFieldNCIAChild() {
		try {
			Vector<Method> result = nciaDict.getSettersFromField(edu.osu.bmi.dicom.SeriesType.class.getDeclaredField("generalImageCollection"));
			assertNotNull(result);
			assertTrue("should return empty for incorrect class", result.size() == 0);
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (NoSuchFieldException e) {
		}		
	}
*/	public void testGetSettersFromFieldNCIA() {
		try {
			Vector<Method> result = nciaDict.getSettersFromField(gov.nih.nci.ncia.domain.Series.class.getDeclaredField("seriesDate"));
			assertNotNull("should return null for incorrect class", result);
			assertTrue("should return non-empty for correct class", result.size() > 0);
		} catch (ModelMapException e) {
			fail("should not reach here.");
		} catch (SecurityException e) {
			fail("should not reach here.");
		} catch (NoSuchMethodException e) {
			fail("should not reach here.");
		} catch (NoSuchFieldException e) {
			fail("should not reach here.");
		}		
	}

	public void testGetGettersFromAttributeNameNull() {
		try {
			nciaDict.getGettersFromAttributeName((String)null);
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
	public void testGetGettersFromAttributeNameEmpty() {
		try {
			nciaDict.getGettersFromAttributeName("");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
	public void testGetGettersFromAttributeNameNotQualified() {
		try {
			nciaDict.getGettersFromAttributeName("studyInstanceUID");
			fail("should not be able to get a result object");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
	public void testGetGettersFromAttributeNameNCIA() {
		try {
			Vector<Method> result = nciaDict.getGettersFromAttributeName("gov.nih.nci.ncia.domain.Series.seriesInstanceUID");
			assertNotNull("result class should not be null", result);
		} catch (ModelMapException e) {
			fail("should not have failed.");
		} catch (SecurityException e) {
			fail("should not have failed.");
		} catch (NoSuchMethodException e) {
			fail("should not have failed.");
		}		
	}
	public void testGetGettersFromAttributeNameBMI() {
		try {
			nciaDict.getGettersFromAttributeName("edu.osu.bmi.dicom.StudyInstanceUIDType._value");
			fail("result class should not be created when using the wrong attribute name");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}
	public void testGetGettersFromAttributeNameNCIAChild() {
		try {
			nciaDict.getGettersFromAttributeName("edu.osu.bmi.dicom.Series.generalImageCollection");
			fail("result class should not be created when using the wrong attribute name");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}
	}

	public void testGetGettersFromFieldNull() {
		try {
			nciaDict.getGettersFromField((Field)null);
			fail("should not be able to get lineage of null class");
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		}		
	}
/*	public void testGetGettersFromFieldBMI() {
		try {
			Vector<Method> result = nciaDict.getGettersFromField(edu.osu.bmi.dicom.SeriesType.class.getDeclaredField("IMAGE_MODAL_TP_TXT"));
			assertNotNull(result);
			assertTrue("should return empty for incorrect class", result.size() == 0);
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (NoSuchFieldException e) {
		}		
	}
	public void testGetGettersFromFieldNCIAChild() {
		try {
			Vector<Method> result = nciaDict.getGettersFromField(edu.osu.bmi.dicom.SeriesType.class.getDeclaredField("generalImageCollection"));
			assertNotNull(result);
			assertTrue("should return empty for incorrect class", result.size() == 0);
		} catch (ModelMapException e) {
		} catch (SecurityException e) {
		} catch (NoSuchMethodException e) {
		} catch (NoSuchFieldException e) {
		}		
	}
*/	public void testGetGettersFromFieldNCIA() {
		try {
			Vector<Method> result = nciaDict.getGettersFromField(gov.nih.nci.ncia.domain.Series.class.getDeclaredField("seriesDate"));
			assertNotNull("should return null for incorrect class", result);
			assertTrue("should return non-empty for correct class", result.size() > 0);
		} catch (ModelMapException e) {
			fail("should not reach here.");
		} catch (SecurityException e) {
			fail("should not reach here.");
		} catch (NoSuchMethodException e) {
			fail("should not reach here.");
		} catch (NoSuchFieldException e) {
			fail("should not reach here.");
		}		
	}



}
