package gov.nih.nci.ivi.dicom.modelmap;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;

import junit.framework.TestCase;

import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;

public class ModelMapTestCase extends TestCase {

	private static final String NCIA_MODELMAP = "resources/modelmap/NCIAModelMap.properties";
	private static final String BMI_MODELMAP = "resources/modelmap/ModelMap.properties";
	private ModelMap ncia_map = null;
	private ModelMap bmi_map = null;

	public ModelMapTestCase(String arg0) {
		super(arg0);

		try {
			bmi_map = new ModelMap(new File(BMI_MODELMAP));
		} catch (Exception e) {
			e.printStackTrace();
			fail("model map creation with ncia model map failed: " + e.getMessage());
		}
		assertNotNull("creation of model map should not fail", bmi_map);

		try {
			ncia_map = new ModelMap(new File(NCIA_MODELMAP));
		} catch (Exception e) {
			e.printStackTrace();
			fail("model map creation with ncia model map failed: " + e.getMessage());
		}
		assertNotNull("creation of model map should not fail", ncia_map);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testGetRetrieveLevelInvalid() {
		try {
			ncia_map.getRetrieveLevel((InformationEntity)null);
			fail("retrieve level for null should be null");
		} catch (ModelMapException e) {
		}
	}

	public void testGetRetrieveLevel() {
		try {
			if (!ncia_map.getRetrieveLevel(InformationEntity.PATIENT).equals(ModelMap.PATIENT_STR)) {
				fail("patient ie should match to retrieve level of patient");
			}
			if (!ncia_map.getRetrieveLevel(InformationEntity.STUDY).equals(ModelMap.STUDY_STR)) {
				fail("study ie should match to retrieve level of study");
			}
			if (!ncia_map.getRetrieveLevel(InformationEntity.PROCEDURESTEP).equals(ModelMap.STUDY_STR)) {
				fail("procedure step ie should match to retrieve level of study");
			}
			if (!ncia_map.getRetrieveLevel(InformationEntity.SERIES).equals(ModelMap.SERIES_STR)) {
				fail("series ie should match to retrieve level of series");
			}
			if (!ncia_map.getRetrieveLevel(InformationEntity.CONCATENATION).equals(ModelMap.SERIES_STR)) {
				fail("concatenation ie should match to retrieve level of series");
			}
			if (!ncia_map.getRetrieveLevel(InformationEntity.INSTANCE).equals(ModelMap.IMAGE_STR)) {
				fail("instance ie should match to retrieve level of image");
			}
			if (!ncia_map.getRetrieveLevel(InformationEntity.FRAME).equals(ModelMap.IMAGE_STR)) {
				fail("frame ie should match to retrieve level of image");
			}
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}

	public void testGetRetrieveLevelRequiredTagNull() {
		try {
			ncia_map.getRetrieveLevelRequiredTag((String)null);
			fail("null retrieve level should return null tag");
		} catch (ModelMapException e) {
		}
	}
	public void testGetRetrieveLevelRequiredTagEmpty() {
		try {
			AttributeTag t = ncia_map.getRetrieveLevelRequiredTag("");
			assertNull("empty retrieve level should return null tag", t);
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}
	public void testGetRetrieveLevelRequiredTagRandom() {
		try {
			AttributeTag t = ncia_map.getRetrieveLevelRequiredTag("something");
			assertNull("invalid retrieve level should return null tag", t);
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}
	public void testGetRetrieveLevelRequiredTag() {
		try {
			if (!ncia_map.getRetrieveLevelRequiredTag(ModelMap.PATIENT_STR).equals(TagFromName.PatientID)) {
				fail("patient required tag should be PatientID");
			}
			if (!ncia_map.getRetrieveLevelRequiredTag(ModelMap.STUDY_STR).equals(TagFromName.StudyInstanceUID)) {
				fail("study required tag should be StudyInstanceUID");
			}
			if (!ncia_map.getRetrieveLevelRequiredTag(ModelMap.SERIES_STR).equals(TagFromName.SeriesInstanceUID)) {
				fail("series required tag should be SeriesInstanceUID");
			}
			if (!ncia_map.getRetrieveLevelRequiredTag(ModelMap.IMAGE_STR).equals(TagFromName.SOPInstanceUID)) {
				fail("image required tag should be SOPInstanceUID");
			}
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}

	public void testGetModelClassFromQueryRetrieveLevelNull() {
		try {
			ncia_map.getModelClassFromQueryRetrieveLevel((String)null);
			fail("null retrieve level should return null class");
		} catch (ModelMapException e) {
		}
	}
	public void testGetModelClassFromQueryRetrieveLevelEmpty() {
		try {
			Class t = ncia_map.getModelClassFromQueryRetrieveLevel("");
			assertNull("empty retrieve level should return null class", t);
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}
	public void testGetModelClassFromQueryRetrieveLevelRandom() {
		try {
			Class t = ncia_map.getModelClassFromQueryRetrieveLevel("something");
			assertNull("invalid retrieve level should return null class", t);
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}
	public void testGetModelClassFromQueryRetrieveLevelNCIA() {
		try {
			if (!ncia_map.getModelClassFromQueryRetrieveLevel(ModelMap.PATIENT_STR).equals(gov.nih.nci.ncia.domain.Patient.class)) {
				fail("patient class should be gov.nih.nci.ncia.domain.Patient");
			}
			if (!ncia_map.getModelClassFromQueryRetrieveLevel(ModelMap.STUDY_STR).equals(gov.nih.nci.ncia.domain.Study.class)) {
				fail("study class should be gov.nih.nci.ncia.domain.Study");
			}
			if (!ncia_map.getModelClassFromQueryRetrieveLevel(ModelMap.SERIES_STR).equals(gov.nih.nci.ncia.domain.Series.class)) {
				fail("series class should be gov.nih.nci.ncia.domain.Series");
			}
			if (!ncia_map.getModelClassFromQueryRetrieveLevel(ModelMap.IMAGE_STR).equals(gov.nih.nci.ncia.domain.Image.class)) {
				fail("image class should be gov.nih.nci.ncia.domain.Image");
			}
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}

/*	public void testGetModelClassFromQueryRetrieveLevelBMI() {
		try {
			if (!bmi_map.getModelClassFromQueryRetrieveLevel(ModelMap.PATIENT_STR).equals(edu.osu.bmi.dicom.PatientType.class)) {
				fail("patient class should be gov.nih.nci.ncia.domain.Patient");
			}
			if (!bmi_map.getModelClassFromQueryRetrieveLevel(ModelMap.STUDY_STR).equals(edu.osu.bmi.dicom.StudyType.class)) {
				fail("study class should be edu.osu.bmi.dicom.StudyType");
			}
			if (!bmi_map.getModelClassFromQueryRetrieveLevel(ModelMap.SERIES_STR).equals(edu.osu.bmi.dicom.SeriesType.class)) {
				fail("series class should be edu.osu.bmi.dicom.SeriesType");
			}
			if (!bmi_map.getModelClassFromQueryRetrieveLevel(ModelMap.IMAGE_STR).equals(edu.osu.bmi.dicom.ImageType.class)) {
				fail("image class should be edu.osu.bmi.dicom.ImageType");
			}
		} catch (ModelMapException e) {
			fail("exception should not have been thrown " + e.getMessage());
		}
	}
*/

	public void testModelMapNull() {
		try {
			new ModelMap((File)null);
			fail("passing in null file name for model map should fail");
		} catch (Exception e) {
		}
	}
	public void testModelMapEmpty() {
		try {
			new ModelMap(new File(""));
			fail("passing in null file name for model map should fail");
		} catch (Exception e) {
		}
	}
	public void testModelMapNonExistent() {
		try {
			new ModelMap(new File("random_file"));
			fail("passing in null file name for model map should fail");
		} catch (Exception e) {
		}
	}
	public void testModelMapNonProperties() {
		try {
			new ModelMap(new File("resources/testResults/modelMap.txt"));
			fail("passing in null file name for model map should fail");
		} catch (Exception e) {
		}
	}
	public void testModelMapBMI() {
		try {
			ModelMap map = new ModelMap(new File(BMI_MODELMAP));
			assertNotNull("should have generated correctly ", map);
		} catch (Exception e) {
			fail("should have completed correctly");
		}
	}
	public void testModelMapNCIA() {
		try {
			ModelMap map = new ModelMap(new File(NCIA_MODELMAP));
			assertNotNull("should have generated correctly ", map);
		} catch (Exception e) {
			fail("should have completed correctly");
		}
	}


	public void testGetInformationEntityFromModelClassNull() {
		try {
			ncia_map.getInformationEntityFromModelClass((Class)null);
			fail("should have thrown an exception");
		} catch (Exception e) {
		}
	}
/*	public void testGetInformationEntityFromModelClassInvalid() {
		try {
			InformationEntity ie = ncia_map.getInformationEntityFromModelClass(edu.osu.bmi.dicom.ImageType.class);
			assertNull("using the wrong class, should have returned null ", ie);
		} catch (Exception e) {
			fail("should not get here");
		}
	}
*/	public void testGetInformationEntityFromModelClassNCIA() {
		try {
			InformationEntity ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.Patient.class);
			assertNotNull(ie);
			assertTrue("should have matched to patient information entity", ie.equals(InformationEntity.PATIENT));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.Study.class);
			assertNotNull(ie);
			assertTrue("should have matched to study information entity", ie.equals(InformationEntity.STUDY));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.Series.class);
			assertNotNull(ie);
			assertTrue("should have matched to series information entity", ie.equals(InformationEntity.SERIES));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.Image.class);
			assertNotNull(ie);
			assertTrue("should have matched to instance information entity", ie.equals(InformationEntity.INSTANCE));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.Equipment.class);
			assertNotNull(ie);
			assertTrue("should have matched to series information entity", ie.equals(InformationEntity.SERIES));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.TrialDataProvenance.class);
			assertNull("trial data provence dicom tags are private, so the ie should be null", ie);

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.Annotation.class);
			assertNull("there should not be an IE for annotation data.", ie);

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.ClinicalTrialProtocol.class);
			assertNotNull(ie);
			assertTrue("should have matched to patient information entity", ie.equals(InformationEntity.PATIENT));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.ClinicalTrialSponsor.class);
			assertNotNull(ie);
			assertTrue("should have matched to series information entity", ie.equals(InformationEntity.SERIES));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.ClinicalTrialSubject.class);
			assertNotNull(ie);
			assertTrue("should have matched to patient information entity", ie.equals(InformationEntity.PATIENT));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.ClinicalTrialSite.class);
			assertNotNull(ie);
			assertTrue("should have matched to patient information entity", ie.equals(InformationEntity.PATIENT));

			ie = ncia_map.getInformationEntityFromModelClass(gov.nih.nci.ncia.domain.CurationData.class);
			assertNull("there should not be an IE for curation data.", ie);

		} catch (Exception e) {
			e.printStackTrace();
			fail("should not get here " + e.getMessage());

		}

	}

	public void testGetDeepestDicomInformationEntityFromModelAttributeNamesNull() {
		try {
			ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames((HashSet<String>)null);
			fail("using null paramter should have resulted in exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetDeepestDicomInformationEntityFromModelAttributeNamesEmpty() {
		try {
			InformationEntity ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(new HashSet<String>());
			assertNull("using empty paramter should have returned null", ie);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
/*	public void testGetDeepestDicomInformationEntityFromModelAttributeNamesInvalid() {
		try {
			HashSet<String> attrNames = bmi_map.getModelDict().getAttributeNamesFromInformationEntityClass(edu.osu.bmi.dicom.PatientType.class);
			InformationEntity ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(new HashSet<String>());
			assertNull("using empty paramter should have returned null", ie);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
*/	public void testGetDeepestDicomInformationEntityFromModelAttributeNames() {
		try {
			HashSet<String> attrNames;
			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Patient.class);
			assertNotNull("should not be null ", attrNames);
			InformationEntity ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return patient", ie);
			assertEquals("information entity should match", ie, InformationEntity.PATIENT);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Study.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return study", ie);
			assertEquals("information entity should match", ie, InformationEntity.STUDY);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Series.class);
			assertNotNull("should not be null ", attrNames);
						ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return series", ie);
			assertEquals("information entity should match", ie, InformationEntity.SERIES);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Equipment.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return series", ie);
			assertEquals("information entity should match ", ie, InformationEntity.SERIES);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Image.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return instance", ie);
			assertEquals("information entity should match", ie, InformationEntity.INSTANCE);


			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.ClinicalTrialProtocol.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return series", ie);
			assertEquals("information entity should match", ie, InformationEntity.PATIENT);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.TrialDataProvenance.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNull("should return null since provenance have only private tags", ie);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.ClinicalTrialSite.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNotNull("should return patient", ie);
			assertEquals("information entity should match", ie, InformationEntity.PATIENT);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.Annotation.class);
			assertNotNull("should not be null ", attrNames);
			ie = ncia_map.getDeepestDicomInformationEntityFromModelAttributeNames(attrNames);
			assertNull("should return null since annotation just replicates study instance uid, which is excluded from map", ie);

			attrNames = ncia_map.getModelDict().getAttributeNamesFromInformationEntityClass(gov.nih.nci.ncia.domain.CurationData.class);
			assertNull("should be null ", attrNames);

		} catch (ModelMapException e) {
			e.printStackTrace();
			fail("should not get here " + e.getMessage() );
		}
	}

	public void testGetArrayIndexFromModelAttributeNameNull() {
		try {
			ncia_map.getArrayIndexFromModelAttributeName((String)null);
			fail("should have generated an exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetArrayIndexFromModelAttributeNameEmpty() {
		try {
			ncia_map.getArrayIndexFromModelAttributeName("");
			fail("should have generated an exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetArrayIndexFromModelAttributeNameInvalid() {
		try {
			ncia_map.getArrayIndexFromModelAttributeName("something not valid");
			fail("should have generated an exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetArrayIndexFromModelAttributeNameNCIA() {
		try {
			int id = ncia_map.getArrayIndexFromModelAttributeName("gov.nih.nci.ncia.domain.Image.rows");
			assertTrue("should be -1 for index", id == -1);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
	public void testGetArrayIndexFromModelAttributeNameBMIWithArray() {
		try {
			int id = bmi_map.getArrayIndexFromModelAttributeName("edu.osu.bmi.dicom.ImageType.PIXEL_SPAC_MM_NUM_Y");
			assertTrue("should be 1 for index", id == 1);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}

	public void testGetAttributeTagFromModelAttributeNameNull() {
		try {
			ncia_map.getAttributeTagFromModelAttributeName((String)null);
			fail("should have generated an exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetAttributeTagFromModelAttributeNameEmpty() {
		try {
			ncia_map.getAttributeTagFromModelAttributeName("");
			fail("should have generated an exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetAttributeTagFromModelAttributeNameInvalid() {
		try {
			AttributeTag t = ncia_map.getAttributeTagFromModelAttributeName("edu.osu.bmi.dicom.ImageType.PIXEL_SPAC_MM_NUM_Y");
			assertNull("bad attribute name should generate a null attribute tag", t);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
	public void testGetAttributeTagFromModelAttributeNameValid() {
		try {
			AttributeTag t = ncia_map.getAttributeTagFromModelAttributeName("gov.nih.nci.ncia.domain.Image.rows");
			assertNotNull("valid attribute name should generate a valid attribute tag", t);
			assertEquals("rows should match...", t, TagFromName.Rows);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}

	public void testGetModelAttributeNamesFromAttributeTagNull() {
		try {
			ncia_map.getModelAttributeNamesFromAttributeTag((AttributeTag)null);
			fail("should have generated an exception");
		} catch (ModelMapException e) {
		}
	}
	public void testGetModelAttributeNamesFromAttributeTagInvalid() {
		try {
			HashMap<String, Integer> attrNames = ncia_map.getModelAttributeNamesFromAttributeTag(TagFromName.AbstractPriorCodeSequence);
			assertNull("should have returned a hashmap", attrNames);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
	public void testGetModelAttributeNamesFromAttributeTagDuplicate() {
		try {
			HashMap<String, Integer> attrNames = ncia_map.getModelAttributeNamesFromAttributeTag(TagFromName.StudyInstanceUID);
			assertNotNull("should have returned a hashmap", attrNames);
			assertTrue("should have returned a hashmap with only one entry", attrNames.size() == 1);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
	public void testGetModelAttributeNamesFromAttributeTag2StudyLevels() {
		try {
			HashMap<String, Integer> attrNames = ncia_map.getModelAttributeNamesFromAttributeTag(TagFromName.StudyInstanceUID);
			assertNotNull("should have returned a hashmap", attrNames);
			assertTrue("should have returned a hashmap with only one entry", attrNames.size() == 1);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
	public void testGetModelAttributeNamesFromAttributeTagValid() {
		try {
			HashMap<String, Integer> attrNames = ncia_map.getModelAttributeNamesFromAttributeTag(TagFromName.StudyInstanceUID);
			assertNotNull("should have returned a hashmap", attrNames);
			assertTrue("hashmap of attribute names should be empty", attrNames.size() > 0);
		} catch (ModelMapException e) {
			fail("should not get here");
		}
	}
}
