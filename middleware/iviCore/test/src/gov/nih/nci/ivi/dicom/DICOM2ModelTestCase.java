package gov.nih.nci.ivi.dicom;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;
import gov.nih.nci.ncia.domain.Patient;
import gov.nih.nci.ncia.domain.Series;
import gov.nih.nci.ncia.domain.Study;

import java.io.File;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import junit.framework.TestCase;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.InformationEntity;
import com.pixelmed.dicom.TagFromName;

public class DICOM2ModelTestCase extends TestCase {

//	private static final String TEST_RESOURCE_PATH = "resources";

	private DICOM2Model d2m = null;
	private ModelMap map = null;
	private DicomDictionary dict = null;

	public DICOM2ModelTestCase(String arg0) {
		super(arg0);

		try {
			this.map = new ModelMap(new File("resources/modelmap/NCIAModelMap.properties"));
			assertNotNull("map should not be null", this.map);
		} catch (Exception e) {
			fail("failed to create model map");
		}

		this.dict = this.map.getDicomDict();
		assertNotNull(this.dict);

		d2m = new DICOM2Model(map);
		assertNotNull(this.d2m);
	}

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testConvertDateStringInvalid() {
		String result = null;

		String test = "yyyy.MM.dd";
		try {
			result = DICOM2Model.convertDateString(test);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
	}

	public void testConvertTimeStringInvalid() {

		String result = null;
		try {
			result = DICOM2Model.convertTimeString("abcde");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertTimeString("123");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertTimeString("98752");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertTimeString("12345");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertTimeString("98752.2.0");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertTimeString("-98752");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertTimeString("98752.0-0400");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);


	}

	public void testConvertTimeStringNullOrEmpty() {
		String result = null;
		try {
			result = DICOM2Model.convertTimeString(null);
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		assertNull("should be null", result);

		try {
			result = DICOM2Model.convertTimeString("");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		assertNull("should be null", result);
	}

	public void testConvertTimeString() {

		String test2="12";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "120000.000");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="1234";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123400.000");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="123456";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.000");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="123456.789012";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.789");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="123456.789500";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.790");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="123456.7";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.700");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="123456.78";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.780");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="123456.789";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.789");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="123456.7890";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.789");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="123456.78901";
		try {
			assertEquals(DICOM2Model.convertTimeString(test2), "123456.789");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}


	}


	public void testConvertDateTimeStringInvalid() {

		String result = null;
		try {
			result = DICOM2Model.convertDateTimeString("abcde");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertDateTimeString("98752.2.0");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertDateTimeString("-98752");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertDateTimeString("98752.0-0400");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		try {
			result = DICOM2Model.convertDateTimeString("98752.0-04");
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		String test2="19981";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		test2="1998123";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		test2="199812301";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		test2="19981230123";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		test2="1998123012345";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		test2="19981230123456.789010+020";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);

		test2="19981230123456.789010+02000";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
		test2="19981230123456.7890103+0200";
		try {
			result = DICOM2Model.convertDateTimeString(test2);
			fail("should not have reached here");
		} catch (DataConversionException e) {
		}
		assertNull("should have generated exception", result);
	}

	public void testConvertDateTimeStringNullOrEmpty() {
		String result = null;
		try {
			result = DICOM2Model.convertDateTimeString(null);
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		assertNull("should be null", result);

		try {
			result = DICOM2Model.convertDateTimeString("");
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		assertNull("should be null", result);
	}



	public void testConvertDateTimeString() {
		String test2="1998";
		try {
			System.out.println(DICOM2Model.convertDateTimeString(test2));
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19980101000000.000+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="199812";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981201000000.000+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230000000.000+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="1998123012";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230120000.000+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="199812301234";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123400.000+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230123456";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.000+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.789012";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230123456.789612";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.790+0000"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230123456.789012-0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789-0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230123456.789012+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="1998+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19980101000000.000+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="199812+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981201000000.000+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230000000.000+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="1998123012+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230120000.000+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="199812301234+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123400.000+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}
		test2="19981230123456+0500";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.000+0500"));
		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.7";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.700+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.78";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.780+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.789";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.7890";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.7891";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.7895";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.790+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.78901";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0000"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

		test2="19981230123456.789010+02";
		try {
			assertTrue("should match", DICOM2Model.convertDateTimeString(test2).equals("19981230123456.789+0200"));

		} catch (DataConversionException e) {
			fail("should not have generated exception");
		}

	}

	private AttributeList getAttributeListForClass(Class c) throws ModelMapException, DicomException {
		AttributeList filter = new AttributeList();
		
        // fill in any other fields for the target class. list is from the model
        // also put the parent classes's attributes in.
        Collection<String> attributeNames = map.getModelDict().getAttributeNamesFromInformationEntityClass(c);
        // convert each to dicom attribute
        Iterator<String> iter = attributeNames.iterator();
        String attributeName = null;
        while (iter.hasNext()) {
            attributeName = String.class.cast(iter.next());
            AttributeTag t = map.getAttributeTagFromModelAttributeName(attributeName);
            if (t != null && filter.get(t) == null) {
                byte[] vr = dict.getValueRepresentationFromTag(t);
                Attribute a = com.pixelmed.dicom.AttributeFactory.newAttribute(t, vr);
                filter.put(t, a);
            }
        }

        AttributeTag tag = TagFromName.QueryRetrieveLevel;
        byte[] vr = this.dict.getValueRepresentationFromTag(tag);
        Attribute qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
        qlAttr.addValue(this.map.getRetrieveLevel(map.getInformationEntityFromModelClass(c)));
        filter.put(TagFromName.QueryRetrieveLevel, qlAttr);
        return filter;
	}
	
	private AttributeList getAttributeListForClassWithParent(Class c) throws ModelMapException, DicomException {
		AttributeList filter = new AttributeList();
		
        // fill in any other fields for the target class. list is from the model
        // also put the parent classes's attributes in.
        Class parent = c;
        while (parent != null) {
        	filter.putAll(this.getAttributeListForClass(parent));
        	
	        parent = map.getModelDict().getParentClassFromInformationEntityClass(parent);
        }
        AttributeTag tag = TagFromName.QueryRetrieveLevel;
        byte[] vr = this.dict.getValueRepresentationFromTag(tag);
        Attribute qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
        qlAttr.addValue(this.map.getRetrieveLevel(map.getInformationEntityFromModelClass(c)));
        filter.put(TagFromName.QueryRetrieveLevel, qlAttr);

		return filter;
	}
	
	public void testDicomPatientToModelObject() throws ModelMapException, DicomException, DataConversionException, InstantiationException, IllegalAccessException, SerializationException {
		AttributeList filter = this.getAttributeListForClass(Patient.class);		
		Object result = d2m.populateFields(Patient.class.newInstance(), filter);
		
		StringWriter writer = new StringWriter();
		ObjectSerializer.serialize(writer, (Patient)result, new QName("Patient"));
		System.out.println(writer.toString());
	}
	public void testDicomStudyToModelObject() throws ModelMapException, DicomException, DataConversionException, InstantiationException, IllegalAccessException, SerializationException {
		AttributeList filter = this.getAttributeListForClass(Study.class);		
		Object result = d2m.populateFields(Study.class.newInstance(), filter);
		StringWriter writer = new StringWriter();
		ObjectSerializer.serialize(writer, (Study)result, new QName("Study"));
		System.out.println(writer.toString());
	}
	public void testDicomSeriesToModelObject() throws ModelMapException, DicomException, DataConversionException, InstantiationException, IllegalAccessException, SerializationException {
		AttributeList filter = this.getAttributeListForClass(Series.class);		
		Object result = d2m.populateFields(Series.class.newInstance(), filter);
		StringWriter writer = new StringWriter();
		ObjectSerializer.serialize(writer, (Series)result, new QName("Series"));
		System.out.println(writer.toString());
	}
	public void testDicomPatientToFullModelObject() throws ModelMapException, DicomException, DataConversionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SerializationException {
		AttributeList filter = this.getAttributeListForClass(Patient.class);		
		Object result = d2m.createObjectAndParentsFromAttributes(filter);
		StringWriter writer = new StringWriter();
		ObjectSerializer.serialize(writer, (Patient)result, new QName("Patient"));
		System.out.println(writer.toString());
	}
	public void testDicomStudyToFullModelObject() throws ModelMapException, DicomException, DataConversionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SerializationException {
		AttributeList filter = this.getAttributeListForClass(Study.class);		
		Object result = d2m.createObjectAndParentsFromAttributes(filter);
		StringWriter writer = new StringWriter();
		ObjectSerializer.serialize(writer, (Study)result, new QName("Study"));
		System.out.println(writer.toString());
	}
	public void testDicomSeriesToFullModelObject() throws ModelMapException, DicomException, DataConversionException, InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, SerializationException {
		AttributeList filter = this.getAttributeListForClass(Series.class);		
		Object result = d2m.createObjectAndParentsFromAttributes(filter);
		StringWriter writer = new StringWriter();
		ObjectSerializer.serialize(writer, (Series)result, new QName("Series"));
		System.out.println(writer.toString());
	}

	public void testPopulateFields() {
		AttributeList al = new AttributeList();

		AttributeTag tag = com.pixelmed.dicom.TagFromName.Modality;
		byte[] vr = dict.getValueRepresentationFromTag(tag);
		Attribute attr = null;
		try {
			attr = AttributeFactory.newAttribute(tag, vr);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr);

		try {
			attr.addValue("CT");
		} catch (DicomException e) {
			fail("unable to set new attr value");
		}
		al.put(tag, attr);
		assertTrue("inserted", al.containsKey(tag));


		// try setting the values
		Class<?> instanceClass = null;
		try {
			instanceClass = map.getModelClassFromQueryRetrieveLevel(map.getRetrieveLevel(InformationEntity.SERIES));
		} catch (ModelMapException e4) {
			fail("unable to get the class");
		}
		assertNotNull(instanceClass);

		Object image = null;
		try {
			image = instanceClass.newInstance();
		} catch (InstantiationException e3) {
			fail("unable to instantiate");
		} catch (IllegalAccessException e3) {
			fail("not allowed to instantiate");
		}
		assertNotNull(image);

		try {
			d2m.populateFields(image, al);
		} catch (ModelMapException e) {
			fail("unable to populate");
		} catch (DataConversionException e) {
			fail("date time conversion failed");
		}

		HashMap<String, Integer> names = null;
		try {
			names = map.getModelAttributeNamesFromAttributeTag(tag);
		} catch (ModelMapException e3) {
			fail("unable to get the attributes");
		}
		assertNotNull(names);
		assertTrue(names.size()>0);

		Iterator<String> iter = names.keySet().iterator();
		while (iter.hasNext()) {
			Method getter = null;
			try {
				getter = map.getModelDict().getGettersFromAttributeName(iter.next()).get(0);
			} catch (SecurityException e) {
				fail("unable to get the getter " + e);
			} catch (ModelMapException e) {
				fail("unable to get the getter " + e);
			} catch (NoSuchMethodException e) {
				fail("unable to get the getter " + e);
			}
			assertNotNull(getter);

			Object result = null;
			try {
				System.out.println(getter);
				result = getter.invoke(image, new Object[0]);
			} catch (IllegalArgumentException e) {
				fail("unable to invoke getter " + e);
			} catch (IllegalAccessException e) {
				fail("unable to invoke getter " + e);
			} catch (InvocationTargetException e) {
				fail("unable to invoke getter " + e);
			}
			assertNotNull(result);
			assertTrue(result.equals("CT"));
		}

	}

	public void testPopulateFieldsArray() {
		AttributeList al = new AttributeList();

		// test getting one element from an array
		AttributeTag tag = com.pixelmed.dicom.TagFromName.PixelSpacing;
		byte[] vr = dict.getValueRepresentationFromTag(tag);
		Attribute attr = null;
		try {
			attr = AttributeFactory.newAttribute(tag, vr);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr);

		try {
			attr.addValue(10.5);
			attr.addValue(20.7);
		} catch (DicomException e) {
			fail("unable to set new attr value");
		}
		al.put(tag, attr);
		assertTrue("inserted", al.containsKey(tag));


		// try setting the values
		Class<?> instanceClass = null;
		try {
			instanceClass = map.getModelClassFromQueryRetrieveLevel(map.getRetrieveLevel(InformationEntity.INSTANCE));
		} catch (ModelMapException e4) {
			fail("unable to get the class");
		}
		assertNotNull(instanceClass);

		Object image = null;
		try {
			image = instanceClass.newInstance();
		} catch (InstantiationException e3) {
			fail("unable to instantiate");
		} catch (IllegalAccessException e3) {
			fail("not allowed to instantiate");
		}
		assertNotNull(image);

		try {
			d2m.populateFields(image, al);
		} catch (ModelMapException e) {
			fail("unable to populate");
		} catch (DataConversionException e) {
			fail("date time conversion failed");
		}

		HashMap<String, Integer> names = null;
		try {
			names = map.getModelAttributeNamesFromAttributeTag(tag);
		} catch (ModelMapException e3) {
			fail("unable to get the attributes");
		}
		assertNotNull(names);
		assertTrue(names.size()>0);

		Iterator<String> iter = names.keySet().iterator();
		while (iter.hasNext()) {
			Method getter = null;
			try {
				getter = map.getModelDict().getGettersFromAttributeName(iter.next()).get(0);
			} catch (SecurityException e) {
				fail("unable to get the getter " + e);
			} catch (ModelMapException e) {
				fail("unable to get the getter " + e);
			} catch (NoSuchMethodException e) {
				fail("unable to get the getter " + e);
			}
			assertNotNull(getter);

			Object result = null;
			try {
				result = getter.invoke(image, new Object[] {});
			} catch (IllegalArgumentException e) {
				fail("unable to invoke getter " + e);
			} catch (IllegalAccessException e) {
				fail("unable to invoke getter " + e);
			} catch (InvocationTargetException e) {
				fail("unable to invoke getter " + e);
			}
			assertNotNull(result);
			assertTrue(result.equals(10.5));

			// the model does not support arrays.
			//			assertTrue(result.getClass().isArray());
			//			assertTrue(Arrays.equals((Object[]) result, new Object[] {10.5, 20.7}));

		}

	}

}
