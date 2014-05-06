package gov.nih.nci.ivi.dicom.virtualpacs.database;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;

import junit.framework.TestCase;

public class DICOMcaGridQRGTestCase extends TestCase {

	ModelMap ncia_map = null;
	DICOMcaGridQRG queryResponseGen = null;

	private static final String VIRTUALPACS_PROPERTIES = "test/resources/virtualPacsTest.properties";
	private static final String DICOM_QUERY_CORRECT_FILENAME = "test/resources/queryVirtualPacsSample.dcm";
	private static final String DICOM_QUERY_WRONG_FILENAME = "test/resources/queryVirtualPacsSampleWrong.dcm";
	Properties serviceURLs = null;


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
		super.tearDown();
	}

	public void testVirtualPacsCFind_QueryAllPatients_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};

		DICOMcaGridQRG qrg = new DICOMcaGridQRG(urls, ncia_map);

		AttributeList dicomQuery = new AttributeList();
		DicomDictionary dict = dicomQuery.getDictionary();

		try {
			AttributeTag t = TagFromName.QueryRetrieveLevel;
			Attribute a = AttributeFactory.newAttribute(t, dict.getValueRepresentationFromTag(t));
			a.addValue(ncia_map.getRetrieveLevel(InformationEntity.PATIENT));
			dicomQuery.put(t,a);
		} catch (DicomException e) {
			fail("DICOM parsing exception");
		} catch (ModelMapException e) {
			fail("ModelMap parsing exception");
		}

		//System.out.println("Attributes = " + dicomQuery);

		qrg.performQuery(null, dicomQuery, false);
		boolean hasNext = true;
		int i = 0;
		AttributeList attr = null;
		while(hasNext)
		{
			attr = qrg.next();
			if(attr == null)
				hasNext = false;
			else
			{
				//System.out.println("Result " + i +" = " + attr);
				i++;
			}
		}
		assertEquals("The result was not as expected", i, 13);
	}


	public void testVirtualPacsCFind_QueryOnePatient_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};

		DICOMcaGridQRG qrg = new DICOMcaGridQRG(urls, ncia_map);

		AttributeList dicomQuery = this.makeDicomAttributeList("Patient", DICOM_QUERY_CORRECT_FILENAME);


		//System.out.println("Attributes = " + dicomQuery);

		// TODO: finish the test
		qrg.performQuery(null, dicomQuery, false);
		boolean hasNext = true;
		int i = 0;
		while(hasNext)
		{
			AttributeList attr = qrg.next();
			if(attr == null)
				hasNext = false;
			else
				i++;
		}
		assertEquals("The result was not as expected", i, 1);

	}
	/*
	public void testVirtualPacsCFind_QueryWrongPatient_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};

		DICOMcaGridQRG qrg = new DICOMcaGridQRG(urls, ncia_map);

		AttributeList dicomQuery = this.makeDicomAttributeList("Patient", DICOM_QUERY_CORRECT_FILENAME);


		System.out.println("Attributes = " + dicomQuery);

		// TODO: finish the test
		qrg.performQuery(null, dicomQuery, false);
		boolean hasNext = true;
		int i = 0;
		AttributeList attr = null;
		while(hasNext)
		{
			attr = qrg.next();
			if(attr == null)
				hasNext = false;
			else
				i++;
		}
		assertNull("The result should have been null", attr);

	}

	public void testVirtualPacsCFind_QueryOnePatient_TwoGridServices()
	{
		;
	}

	public void testVirtualPacsCFind_QueryWrongPatient_TwoGridServices()
	{
		;
	}
	 */
	public void testVirtualPacsCFind_QueryOneStudy_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};

		DICOMcaGridQRG qrg = new DICOMcaGridQRG(urls, ncia_map);

		AttributeList dicomQuery = this.makeDicomAttributeList("Study", DICOM_QUERY_CORRECT_FILENAME);


		//System.out.println("Attributes = " + dicomQuery);

		qrg.performQuery(null, dicomQuery, false);
		boolean hasNext = true;
		int i = 0;
		AttributeList attr = null;
		while(hasNext)
		{
			attr = qrg.next();
			if(attr == null)
				hasNext = false;
			else
			{
				//System.out.println("Result " + i +" = " + attr);
				i++;
			}
		}
		assertEquals("The result was not as expected", i, 1);

	}

	public void testVirtualPacsCFind_QueryWrongStudy_OneGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceMain")};

		DICOMcaGridQRG qrg = new DICOMcaGridQRG(urls, ncia_map);

		AttributeList dicomQuery = this.makeDicomAttributeList("Study", DICOM_QUERY_WRONG_FILENAME);


		//System.out.println("Attributes = " + dicomQuery);

		// TODO: finish the test
		qrg.performQuery(null, dicomQuery, false);
		boolean hasNext = true;
		int i = 0;
		AttributeList attr = null;
		while(hasNext)
		{
			attr = qrg.next();
			if(attr == null)
				hasNext = false;
			else
			{
				//System.out.println("Result " + i +" = " + attr);
				i++;
			}
		}
		assertNull("The result should have been null", attr);

	}
	/*	
	public void testVirtualPacsCFind_QueryOneStudy_TwoGridServices()
	{
		;
	}

	public void testVirtualPacsCFind_QueryWronglStudy_TwoGridServices()
	{
		;
	}
	 */
	public void testVirtualPacsCFind_WrongGridService()
	{
		assertNotNull(ncia_map);
		assertNotNull(serviceURLs);

		String [] urls = new String[] {serviceURLs.getProperty("dicomdataserviceWrong")};

		DICOMcaGridQRG qrg = new DICOMcaGridQRG(urls, ncia_map);

		AttributeList dicomQuery = new AttributeList();
		DicomDictionary dict = dicomQuery.getDictionary();

		try {
			AttributeTag t = TagFromName.QueryRetrieveLevel;
			Attribute a = AttributeFactory.newAttribute(t, dict.getValueRepresentationFromTag(t));
			a.addValue(ncia_map.getRetrieveLevel(InformationEntity.PATIENT));
			dicomQuery.put(t,a);
		} catch (DicomException e) {
			fail("DICOM parsing exception");
		} catch (ModelMapException e) {
			fail("ModelMap parsing exception");
		}

		//System.out.println("Attributes = " + dicomQuery);
		qrg.performQuery(null, dicomQuery, false);
		boolean hasNext = true;
		int i = 0;
		AttributeList attr = null;
		while(hasNext)
		{
			attr = qrg.next();
			if(attr == null)
				hasNext = false;
			else
			{
				//System.out.println("Result " + i +" = " + attr);
				i++;
			}
		}
		assertNull("The result should have been null", attr);



	}


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
