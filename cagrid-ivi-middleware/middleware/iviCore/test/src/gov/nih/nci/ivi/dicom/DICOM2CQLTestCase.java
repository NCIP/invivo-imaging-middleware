/**
 * 
 */
package gov.nih.nci.ivi.dicom;

import java.io.File;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.sun.org.apache.bcel.internal.generic.NEW;

import junit.framework.TestCase;

/**
 * @author tcpan
 *
 */
public class DICOM2CQLTestCase extends TestCase {

	private DicomDictionary dict = new DicomDictionary();
	private ModelMap map;
	/**
	 * @param name
	 */
	public DICOM2CQLTestCase(String name) {
		super(name);
		
		try {
			this.map = new ModelMap(new File("resources/modelmap/NCIAModelMap.properties"));
			assertNotNull("map should not be null", this.map);
		} catch (Exception e) {
			fail("failed to create model map");
		}

		this.dict = this.map.getDicomDict();
		assertNotNull(this.dict);

	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	/**
	 * Test method for {@link gov.nih.nci.ivi.dicom.DICOM2CQL#convert2CQL(com.pixelmed.dicom.AttributeList)}.
	 */
	public void testConvert2CQL() {
		AttributeList al = new AttributeList();
		
//		AttributeTag tag = com.pixelmed.dicom.TagFromName.Modality;
//		byte[] vr = dict.getValueRepresentationFromTag(tag);
//		Attribute attr = null;
//		try {
//			attr = AttributeFactory.newAttribute(tag, vr);
//		} catch (DicomException e) {
//			fail("unable to create new attr");
//		}
//		assertNotNull(attr);
//
//		try {
//			attr.addValue("CT");
//		} catch (DicomException e) {
//			fail("unable to set new attr value");
//		}
//		al.put(tag, attr);
//		assertTrue("inserted", al.containsKey(tag));
	
		AttributeTag tag3 = com.pixelmed.dicom.TagFromName.PatientID;
		byte[] vr3 = dict.getValueRepresentationFromTag(tag3);
		Attribute attr3 = null;
		try {
			attr3 = AttributeFactory.newAttribute(tag3, vr3);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr3);

		try {
			attr3.addValue("TPAN");
		} catch (DicomException e) {
			fail("unable to set new attr value");
		}
		al.put(tag3, attr3);
		assertTrue("inserted", al.containsKey(tag3));
		
		
		AttributeTag tag4 = com.pixelmed.dicom.TagFromName.QueryRetrieveLevel;
		byte[] vr4 = dict.getValueRepresentationFromTag(tag4);
		Attribute attr4 = null;
		try {
			attr4 = AttributeFactory.newAttribute(tag4, vr4);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr4);

		try {
			attr4.addValue("STUDY");
		} catch (DicomException e) {
			fail("unable to set new attr value");
		}
		al.put(tag4, attr4);
		assertTrue("inserted", al.containsKey(tag4));

		AttributeTag tag5 = com.pixelmed.dicom.TagFromName.PatientName;
		byte[] vr5 = dict.getValueRepresentationFromTag(tag5);
		Attribute attr5 = null;
		try {
			attr5 = AttributeFactory.newAttribute(tag5, vr5);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr5);

		al.put(tag5, attr5);
		assertTrue("inserted", al.containsKey(tag5));

		AttributeTag tag6 = com.pixelmed.dicom.TagFromName.ModalitiesInStudy;
		byte[] vr6 = dict.getValueRepresentationFromTag(tag6);
		Attribute attr6 = null;
		try {
			attr6 = AttributeFactory.newAttribute(tag6, vr6);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr6);

		try {
			attr6.addValue("CT");
		} catch (DicomException e) {
			fail("unable to set new attr value");
		}
		al.put(tag6, attr6);
		assertTrue("inserted", al.containsKey(tag6));

		
		// test getting one element from an array
		AttributeTag tag2 = com.pixelmed.dicom.TagFromName.PixelSpacing;
		byte[] vr2 = dict.getValueRepresentationFromTag(tag2);
		Attribute attr2 = null;
		try {
			attr2 = AttributeFactory.newAttribute(tag2, vr2);
		} catch (DicomException e) {
			fail("unable to create new attr");
		}
		assertNotNull(attr2);

		try {
			attr2.addValue(10.5);
			attr2.addValue(20.7);
		} catch (DicomException e) {
			fail("unable to set new attr value");
		}
		al.put(tag2, attr2);
		assertTrue("inserted", al.containsKey(tag2));

		try {
			System.out.println(attr6.getStringValues()[0]);
			System.out.println(attr2.getStringValues()[0]);
			System.out.println(attr3.getStringValues()[0]);
			System.out.println(attr4.getStringValues()[0]);
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		CQLQuery query = DICOM2CQL.convert2CQL(this.map, al);
		
		try {
			String TEMP = ObjectSerializer.toString(query,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
			System.out.println(TEMP);
		} catch (SerializationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		
	}

}
