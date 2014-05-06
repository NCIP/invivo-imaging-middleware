package gov.nih.nci.ivi.dicom;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Patient;
import gov.nih.nci.ncia.domain.Series;
import gov.nih.nci.ncia.domain.Study;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import junit.framework.TestCase;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;

public class PixelMedHelperTestCase extends TestCase {

	private static final String NCIA_MODELMAP = "resources/modelmap/NCIAModelMap.properties";
	private static final String PIXELMED_PROPERTIES = "test/resources/dicom/pixelmed.properties";
	private static final String DICOM_QUERY_FILENAME = "test/resources/dicom/query.dcm";
//	private static final String DICOM_RETRIEVE_FILENAME = "test/resources/dicom/retrieve.dcm";
	private static final String PIXELMED_TMPDIR = System.getProperty("java.io.tmpdir") + File.separator + "TestDir";

	private PixelMedHelper pixelMedHelper = null;
	private ModelMap ncia_map = null;

	public PixelMedHelperTestCase(String name) {
		super(name);
		try {
			FileInputStream in = new FileInputStream(PIXELMED_PROPERTIES);
			Properties properties = new Properties(/*defaultProperties*/);
			properties.load(in);
			in.close();
			properties.setProperty("tempdir", PIXELMED_TMPDIR);
			File foo = new File(PIXELMED_TMPDIR);
			foo.mkdirs();
			pixelMedHelper = new PixelMedHelper(properties);

		} catch (FileNotFoundException e) {
			fail("properties file not found");
		} catch (IOException e) {
			fail("property file load failed");
			e.printStackTrace();
		}
		assertNotNull(pixelMedHelper);
		pixelMedHelper.setIsWSEnum(true);
		
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
		File tempDownloadDir = new File(PIXELMED_TMPDIR);
		if(tempDownloadDir.isDirectory())
		{
			String [] tempFilenames = tempDownloadDir.list();
			for(int i = 0; i < tempFilenames.length; i++)
			{
				File tempFile = new File(PIXELMED_TMPDIR + File.separator + tempFilenames[i]);
				tempFile.delete();
			}
		}
		tempDownloadDir.delete();
		tempDownloadDir.mkdirs();
	}

	protected void tearDown() throws Exception {
		File tempDownloadDir = new File(PIXELMED_TMPDIR);
		if(tempDownloadDir.isDirectory())
		{
			String [] tempFilenames = tempDownloadDir.list();
			for(int i = 0; i < tempFilenames.length; i++)
			{
				File tempFile = new File(PIXELMED_TMPDIR + File.separator + tempFilenames[i]);
				tempFile.delete();
			}
		}
		tempDownloadDir.delete();
		super.tearDown();
	}

	public void testCFindPatientLevel(){
		List<?> patientQuery = pixelMedHelper.queryFind(makeDicomAttributeList("Patient"), this.ncia_map, Patient.class.getCanonicalName());
		assertEquals("The Patient Query should return only 0 patient not "+ patientQuery.size(), patientQuery.size(), 0);
	}
	
	public void testCFindStudyLevel(){
		List<?> studyQuery = pixelMedHelper.queryFind(makeDicomAttributeList("Study"), this.ncia_map, Study.class.getName());
		assertEquals("The Study Query should return only 1 study not " + studyQuery.size(), studyQuery.size(), 1);
	}
	
	public void testCFindSeriesLevel() {
		List<?> seriesQuery = pixelMedHelper.queryFind(makeDicomAttributeList("Series"), this.ncia_map, Series.class.getName());
		assertEquals("The Series Query should return only 1 series not " + seriesQuery.size(), seriesQuery.size(), 1);
	}
	
	public void testCGetPatientLevel(){
		AttributeList patientLevelQuery = makeDicomAttributeList("Patient");
		List<String> retrievedFiles = null;
		try {
			retrievedFiles = pixelMedHelper.retrieve(patientLevelQuery, new String[1]);
		} catch (DicomNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull("No data was retrieved at PATIENT Level", retrievedFiles);
		assertEquals("C_GET at Series should have returned 0 images not " + retrievedFiles.size(), retrievedFiles.size(), 0);
	}
	
	public void testCGetStudyLevel(){
		AttributeList patientLevelQuery = makeDicomAttributeList("Study");
		List<String> retrievedFiles = null;
		try {
			retrievedFiles = pixelMedHelper.retrieve(patientLevelQuery, new String[1]);
		} catch (DicomNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull("No data was retrieved at STUDY Level", retrievedFiles);
		assertEquals("C_GET at Study should have returned 11 images not " + retrievedFiles.size(), retrievedFiles.size(), 11);
	}
	
	public void testCGetSeriesLevel(){
		AttributeList patientLevelQuery = makeDicomAttributeList("Series");
		List<String> retrievedFiles = null;
		try {
			retrievedFiles = pixelMedHelper.retrieve(patientLevelQuery, new String[1]);
		} catch (DicomNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertNotNull("No data was retrieved at SERIES Level", retrievedFiles);
		assertEquals("C_GET at Series should have returned 5 images not " + retrievedFiles.size(), retrievedFiles.size(), 5);
	}
	
	private AttributeList makeDicomAttributeList(String level)
	{
		DicomInputStream i = null;
		AttributeList inputList = null;
		AttributeList dicomQuery = null;
		try {
			i = new DicomInputStream(new BufferedInputStream(new FileInputStream(DICOM_QUERY_FILENAME)));
		} catch (FileNotFoundException e) {
			fail("DICOM Query file not found");
		} catch (IOException e) {
			fail("DICOM Query file could not be parsed");
		}
		assertNotNull(i);

		try {
			inputList = new AttributeList();
			inputList.read(i);
			dicomQuery = new AttributeList();
			dicomQuery.put(TagFromName.PatientID, inputList.get(TagFromName.PatientID));
		} catch (IOException e) {
			fail("DICOM Query file could not be parsed");
		} catch (DicomException e) {
			fail("DICOM Exception: Query file could not be parsed");
		}
		assertNotNull(dicomQuery);
		assertNotNull(inputList);

		if(level.equals("Patient"))
		{
			try {
				AttributeTag t = TagFromName.QueryRetrieveLevel;
				Attribute a = AttributeFactory.newAttribute(t, this.ncia_map.getDicomDict().getValueRepresentationFromTag(t));
				a.addValue(this.ncia_map.getRetrieveLevel(InformationEntity.PATIENT));
				dicomQuery.put(t,a);
			} catch (DicomException e) {
				fail("DICOM parsing exception");
			} catch (ModelMapException e) {
				fail("ModelMap parsing exception");
			}

		}
		else if(level.equals("Study"))
		{
			try {
				dicomQuery.put(TagFromName.StudyInstanceUID, inputList.get(TagFromName.StudyInstanceUID));
				AttributeTag t = TagFromName.QueryRetrieveLevel;
				Attribute a = AttributeFactory.newAttribute(t, this.ncia_map.getDicomDict().getValueRepresentationFromTag(t));
				a.addValue(this.ncia_map.getRetrieveLevel(InformationEntity.STUDY));
				dicomQuery.put(t,a);
			} catch (DicomException e) {
				fail("DICOM parsing exception");
			} catch (ModelMapException e) {
				fail("ModelMap parsing exception");
			}
		}
		else if(level.equals("Series"))
		{
			try {
				dicomQuery.put(TagFromName.StudyInstanceUID, inputList.get(TagFromName.StudyInstanceUID));
				dicomQuery.put(TagFromName.SeriesInstanceUID, inputList.get(TagFromName.SeriesInstanceUID));
				AttributeTag t = TagFromName.QueryRetrieveLevel;
				Attribute a = AttributeFactory.newAttribute(t, this.ncia_map.getDicomDict().getValueRepresentationFromTag(t));
				a.addValue(this.ncia_map.getRetrieveLevel(InformationEntity.SERIES));
				dicomQuery.put(t,a);
			} catch (DicomException e) {
				fail("DICOM parsing exception");
			} catch (ModelMapException e) {
				fail("ModelMap parsing exception");
			}
		}
		else if(level.equals("Image"))
		{
			
		}
		return dicomQuery;
	}

	
	
}
