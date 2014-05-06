package gov.nih.nci.ivi.dicom;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;

import junit.framework.TestCase;

import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;

public class CQL2DICOMTestCase extends TestCase {

	private static final String TEST_RESOURCE_PATH = "resources";
	private ModelMap map = null;
	private DicomDictionary dict = null;
	private CQL2DICOM cql2 = null;
	
	public CQL2DICOMTestCase() {
		try {
			this.map = new ModelMap(new File("resources/modelmap/NCIAModelMap.properties"));
			assertNotNull("map should not be null", this.map);
		} catch (Exception e) {
			fail("failed to create model map");
		}

		this.dict = this.map.getDicomDict();
		assertNotNull(this.dict);

		try {
			this.cql2 = new CQL2DICOM(this.map);
		} catch (ModelMapException e1) {
			e1.printStackTrace();
			fail("cql2dicom creation failed with selected map "
					+ e1.getMessage());
		}

		
	}
	
	protected void setUp() throws Exception {
		// initialization
		super.setUp();
	}

	protected void tearDown() throws Exception {
		// clean up
		super.tearDown();
	}

	public void testCQL2DICOMWithNullMap() {
		try {
			new CQL2DICOM(null);
			fail(" passing null to pointer should have thrown exception ");
		} catch (ModelMapException e1) {
			// should always get here
		}
		
	}

	private CQLQuery loadCQLQuery(String filename, String targetName) {
		// load the cqlquery
		File f = new File(TEST_RESOURCE_PATH + File.separator
				+ filename);
		assertTrue(f.exists());
		assertTrue(f.canRead());
	
		FileReader fileReader = null;
		try {
			fileReader = new FileReader(f);
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
		assertNotNull(fileReader);
	
		InputSource queryInput = new InputSource(fileReader);
		assertNotNull(queryInput);
	
		CQLQuery query = null;
		try {
			query = (CQLQuery) ObjectDeserializer.deserialize(queryInput,
					CQLQuery.class);
		} catch (DeserializationException e1) {
			e1.printStackTrace();
		}
		assertNotNull(query);
		assertTrue("target is " + query.getTarget().getName() + " but should match " + targetName, targetName.equals(query.getTarget().getName()));
		return query;
	}

	private void checkQRLevel(AttributeList filter, String targetQRLevel) {
		System.out.println("filter = " + filter);
		
		// first check the query retrieve levels
		Attribute attr = filter.get(com.pixelmed.dicom.TagFromName.QueryRetrieveLevel);
		assertNotNull("query retrieve level attribute should be present for patient", attr);
		String qrLevel = attr.getSingleStringValueOrNull();
		assertNotNull("query retrieve level value should be " + targetQRLevel + " but is " + qrLevel, qrLevel);
		assertTrue("query retrive level should be " + targetQRLevel + " but is " + qrLevel, qrLevel.equals(targetQRLevel));
	}

	public void testCqlToPixelMedWithNull() {
		try {
			this.cql2.cqlToPixelMed(null);
			fail("should have failed with null input");
		} catch (MalformedQueryException e) {
		} catch (DicomException e) {
			fail("should have thrown malformed query exception");
		} catch (ModelMapException e) {
			fail("should have thrown malformed query exception");
		}
	}
	
	public void testCqlToPixelMedWithEmpty() {
		CQLQuery q  = new CQLQuery();
		
		try {
			this.cql2.cqlToPixelMed(q);
			fail("should have failed with empty input query");
		} catch (MalformedQueryException e) {
		} catch (DicomException e) {
			fail("should have thrown malformed query exception");
		} catch (ModelMapException e) {
			fail("should have thrown malformed query exception");
		}
	}

	public void testCqlToPixelMedWithInvalidCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("dicomCQL1.xml", "edu.osu.bmi.dicom.SeriesType");

		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
			fail("should have failed since the target and other things are not correct for the model.");
		} catch (Exception e) {
		}
		assertNull("result should be null", filter);
		
		// verify that the output is correct.
	}

	
	public void testCqlToPixelMedWithPatientCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-patientOnly.xml", "gov.nih.nci.ncia.domain.Patient");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		checkQRLevel(filter, ModelMap.PATIENT_STR);
	}
	
	public void testCqlToPixelMedWithStudyCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-studyOnly.xml", "gov.nih.nci.ncia.domain.Study");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.STUDY_STR);
	}

	public void testCqlToPixelMedWithSeriesCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-seriesOnly.xml", "gov.nih.nci.ncia.domain.Series");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.SERIES_STR);
	}

	public void testCqlToPixelMedWithImageCQL() {

		CQLQuery query = this.loadCQLQuery("nciaCQL2-imageOnly.xml", "gov.nih.nci.ncia.domain.Image");

		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.IMAGE_STR);
	}

	
	public void testCqlToPixelMedWithPatientFilterByStudyCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-patientFilterByStudy.xml", "gov.nih.nci.ncia.domain.Patient");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// query for patient has to have Study.  just the way DICOM is...
		checkQRLevel(filter, ModelMap.PATIENT_STR);
	}
	
	public void testCqlToPixelMedWithStudyFilterByPatientCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-studyFilterByPatient.xml", "gov.nih.nci.ncia.domain.Study");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.STUDY_STR);
	}

	public void testCqlToPixelMedWithSeriesFilterByPatientCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-seriesFilterByPatient.xml", "gov.nih.nci.ncia.domain.Series");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.SERIES_STR);
	}

	public void testCqlToPixelMedWithStudyFilterByPatientSeriesCQL() {

		CQLQuery query = this.loadCQLQuery("nciaCQL2-studyFilterByPatientSeries.xml", "gov.nih.nci.ncia.domain.Study");

		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.STUDY_STR);
	}
	
	public void testCqlToPixelMedWithPatientFilterBySeriesCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-patientFilterBySeries.xml", "gov.nih.nci.ncia.domain.Patient");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		checkQRLevel(filter, ModelMap.PATIENT_STR);
	}
	
	public void testCqlToPixelMedWithStudyFilterBySeriesCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-studyFilterBySeries.xml", "gov.nih.nci.ncia.domain.Study");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.STUDY_STR);
	}

	public void testCqlToPixelMedWithSeriesFilterByStudyCQL() {
		// load the cqlquery
		CQLQuery query = this.loadCQLQuery("nciaCQL2-seriesFilterByStudy.xml", "gov.nih.nci.ncia.domain.Series");
		
		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.SERIES_STR);
	}

	public void testCqlToPixelMedWithImageFilterBySeriesCQL() {

		CQLQuery query = this.loadCQLQuery("nciaCQL2-imageFilterBySeries.xml", "gov.nih.nci.ncia.domain.Image");

		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.IMAGE_STR);
	}
	

	public void testCqlToPixelMedWithSeriesFilterByImageCQL() {

		CQLQuery query = this.loadCQLQuery("nciaCQL2-seriesFilterByImage.xml", "gov.nih.nci.ncia.domain.Series");

		AttributeList filter = null;
		try {
			filter = this.cql2.cqlToPixelMed(query);
		} catch (Exception e) {
			e.printStackTrace();
			fail("failed to convert query  " + e.getMessage());
		}
		assertNotNull("result should not be null", filter);
		
		// verify that the output is correct.
		checkQRLevel(filter, ModelMap.SERIES_STR);
	}
	

}
