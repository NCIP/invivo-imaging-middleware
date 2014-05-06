package gov.nih.nci.ivi.dicom.virtualpacs.database;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Properties;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.SetOfDicomFiles;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.query.RetrieveResponseGenerator;

import junit.framework.TestCase;

public class DICOMcaGridRRGTestCase extends TestCase {

	ModelMap ncia_map = null;

	private static final String VIRTUALPACS_PROPERTIES = "test/resources/virtualPacsTest.properties";
	private static final String DICOM_QUERY_CORRECT_FILENAME = "test/resources/1.2.392.200036.9116.2.6.1.48.1215545942.1138956454.466354.dcm";
	private static final String DICOM_QUERY_WRONG_FILENAME = "test/resources/queryVirtualPacsSampleWrong.dcm";
	Properties serviceURLs = null;
	SetOfDicomFiles retrievedDicomFiles = null;
	
	protected void setUp() throws Exception {
		super.setUp();
		try {
			ncia_map = new ModelMap();
		} catch (FileNotFoundException e1) {
			fail("Modelmap file not found");
		} catch (ModelMapException e1) {
			fail("Modelmap Exception");
		} catch (IOException e1) {
			fail("Modelmap IO Exception");
		} catch (ClassNotFoundException e1) {
			fail("Modelmap ClassNotFound Exception");
		}

		FileInputStream in = new FileInputStream(VIRTUALPACS_PROPERTIES);
		serviceURLs = new Properties();
		serviceURLs.load(in);
	}

	protected void tearDown() throws Exception {
		
		if(retrievedDicomFiles != null && !retrievedDicomFiles.isEmpty())
		{
			Iterator dicomItr = retrievedDicomFiles.iterator();
			String parentDir1 = null;
			String parentDir2 = null;
			while (dicomItr.hasNext()) {
				SetOfDicomFiles.DicomFile dicomFile = (SetOfDicomFiles.DicomFile)(dicomItr.next());
				String fileName = dicomFile.getFileName();
				File dicomFileName = new File(fileName);
				parentDir1 = dicomFileName.getParent();
				dicomFileName.delete();
			}
			File parentDir = new File(parentDir1);
			parentDir2 = parentDir.getParent();
			parentDir.delete();
			parentDir = new File(parentDir2);
			parentDir.delete();
		}
		super.tearDown();
	}
	
	public void testVirtualPacsCGet_OneSeries_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};
		AttributeList dicomQuery = this.makeDicomAttributeList("Series", DICOM_QUERY_CORRECT_FILENAME);

		DICOMcaGridRRGFactory dicomRRGFactory = new DICOMcaGridRRGFactory(urls, ncia_map);
		RetrieveResponseGenerator retrieveResponseGenerator = dicomRRGFactory.newInstance();
		retrieveResponseGenerator.performRetrieve(null, dicomQuery, false);
		retrievedDicomFiles = retrieveResponseGenerator.getDicomFiles();
		
		assertEquals("The C_GET request should have returned some results", retrievedDicomFiles.isEmpty(), false);
		assertEquals("The C_GET request should have returned 54 images", retrievedDicomFiles.size(), 54);
		
	}

	public void testVirtualPacsCGet_OneStudy_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};
		AttributeList dicomQuery = this.makeDicomAttributeList("Study", DICOM_QUERY_CORRECT_FILENAME);

		DICOMcaGridRRGFactory dicomRRGFactory = new DICOMcaGridRRGFactory(urls, ncia_map);
		RetrieveResponseGenerator retrieveResponseGenerator = dicomRRGFactory.newInstance();
		retrieveResponseGenerator.performRetrieve(null, dicomQuery, false);
		retrievedDicomFiles = retrieveResponseGenerator.getDicomFiles();
		
		assertEquals("The C_GET request should have returned some results", retrievedDicomFiles.isEmpty(), false);
		assertEquals("The C_GET request should have returned 54 images", retrievedDicomFiles.size(), 121);
		
	}
/*
	public void testVirtualPacsCGet_OnePatient_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};
		AttributeList dicomQuery = this.makeDicomAttributeList("Patient", DICOM_QUERY_CORRECT_FILENAME);

		DICOMcaGridRRGFactory dicomRRGFactory = new DICOMcaGridRRGFactory(urls, ncia_map);
		RetrieveResponseGenerator retrieveResponseGenerator = dicomRRGFactory.newInstance();
		retrieveResponseGenerator.performRetrieve(null, dicomQuery, false);
		retrievedDicomFiles = retrieveResponseGenerator.getDicomFiles();
		
		
		assertEquals("The C_GET request should have returned some results", retrievedDicomFiles.isEmpty(), false);
		assertEquals("The C_GET request should have returned 54 images", retrievedDicomFiles.size(), 121);
		
	}
*/	
	private AttributeList makeDicomAttributeList(String level, String dicomFile)
	{
		DicomInputStream i = null;
		AttributeList inputList = null;
		AttributeList dicomQuery = null;
		try {
			i = new DicomInputStream(new BufferedInputStream(new FileInputStream(dicomFile)));
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
