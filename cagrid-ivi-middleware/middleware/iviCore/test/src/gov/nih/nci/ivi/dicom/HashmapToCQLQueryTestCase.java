package gov.nih.nci.ivi.dicom;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.DICOM2Model;
import gov.nih.nci.ivi.dicom.HashmapToCQLQuery;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import javax.xml.namespace.QName;

import junit.framework.TestCase;

import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;
import org.xml.sax.InputSource;

import com.pixelmed.dicom.DicomDictionary;

public class HashmapToCQLQueryTestCase extends TestCase {

	private static final String TEST_RESOURCE_PATH = "resources";
	private DICOM2Model d2m = null;
	private ModelMap map = null;
	private DicomDictionary dict = null;
	private HashmapToCQLQuery makeCQL = null; 
	private HashMap <String, String> testQuery = null;
	
	public HashmapToCQLQueryTestCase(String arg0) {
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

		this.makeCQL = new HashmapToCQLQuery(this.map);
		assertNotNull(this.makeCQL);
		this.testQuery = new HashMap<String, String>();
	}

	protected void setUp() throws Exception {
		super.setUp();
		this.testQuery.clear();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testMakeCQLQueryNull() {
		
		try {
			makeCQL.makeCQLQuery(null);
			fail("should not get here");
		} catch (MalformedQueryException e) {
		}
		
		try {
			makeCQL.makeCQLQuery(testQuery);
			fail("should not get here");
		} catch (MalformedQueryException e) {
		}
	}

	
	
	public void testMakeCQLQueryPatient() {

		String test = null;
		try {
			test = convert("gov.nih.nci.ncia.domain.Patient");
		} catch (SerializationException e) {
			fail("could not serialize");
		} catch (MalformedQueryException e) {
			fail("bad query");
		}
		assertNotNull(test);
		
		CQLQuery truthQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader("test/resources/dicom/Hashmap2CQLoutputPatient.xml"));
			truthQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
		} catch (FileNotFoundException e2) {
			fail("Unable to open file for reading");
		} catch (DeserializationException e2) {
			fail("Bad truth query");
		}
		assertNotNull(truthQuery);
		
		String truth = null;
		try {
			truth = ObjectSerializer.toString(truthQuery,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
		} catch (SerializationException e) {
			fail("could not serialize");
		}
		
		areEqual(truth, test);
	}

	
	public void testMakeCQLQueryStudy() {

		String test = null;
		try {
			test = convert("gov.nih.nci.ncia.domain.Study");
		} catch (SerializationException e) {
			fail("could not serialize");
		} catch (MalformedQueryException e) {
			fail("bad query");
		}
		assertNotNull(test);
		
		CQLQuery truthQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader("test/resources/dicom/Hashmap2CQLoutputStudy.xml"));
			truthQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
		} catch (FileNotFoundException e2) {
			fail("Unable to open file for reading");
		} catch (DeserializationException e2) {
			fail("Bad truth query");
		}
		assertNotNull(truthQuery);
		
		String truth = null;
		try {
			truth = ObjectSerializer.toString(truthQuery,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
		} catch (SerializationException e) {
			fail("could not serialize");
		}
		
		areEqual(truth, test);
	}

	
	public void testMakeCQLQuerySeries() {

		String test = null;
		try {
			test = convert("gov.nih.nci.ncia.domain.Series");
		} catch (SerializationException e) {
			fail("could not serialize");
		} catch (MalformedQueryException e) {
			fail("bad query");
		}
		assertNotNull(test);
		
		CQLQuery truthQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader("test/resources/dicom/Hashmap2CQLoutputSeries.xml"));
			truthQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
		} catch (FileNotFoundException e2) {
			fail("Unable to open file for reading");
		} catch (DeserializationException e2) {
			fail("Bad truth query");
		}
		assertNotNull(truthQuery);
		
		String truth = null;
		try {
			truth = ObjectSerializer.toString(truthQuery,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
		} catch (SerializationException e) {
			fail("could not serialize");
		}
		
		areEqual(truth, test);
	}

	
	public void testMakeCQLQueryImage() {

		String test = null;
		try {
			test = convert("gov.nih.nci.ncia.domain.Image");
		} catch (SerializationException e) {
			fail("could not serialize");
		} catch (MalformedQueryException e) {
			fail("bad query");
		}
		assertNotNull(test);
		
		CQLQuery truthQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader("test/resources/dicom/Hashmap2CQLoutputImage.xml"));
			truthQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
		} catch (FileNotFoundException e2) {
			fail("Unable to open file for reading");
		} catch (DeserializationException e2) {
			fail("Bad truth query");
		}
		assertNotNull(truthQuery);
		
		String truth = null;
		try {
			truth = ObjectSerializer.toString(truthQuery,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
		} catch (SerializationException e) {
			fail("could not serialize");
		}
		
		areEqual(truth, test);
	}

	public void testMakeCQLQueryTrialDataProvenance() {
		String test = null;
		try {
			test = convert("gov.nih.nci.ncia.domain.TrialDataProvenance");
		} catch (SerializationException e) {
			fail("could not serialize");
		} catch (MalformedQueryException e) {
			fail("bad query");
		}
		assertNotNull(test);
		
		CQLQuery truthQuery = null;
		try {
			InputSource queryInput = new InputSource(new FileReader("test/resources/dicom/Hashmap2CQLoutputTrialDataProvenance.xml"));
			truthQuery = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
		} catch (FileNotFoundException e2) {
			fail("Unable to open file for reading");
		} catch (DeserializationException e2) {
			fail("Bad truth query");
		}
		assertNotNull(truthQuery);
		
		String truth = null;
		try {
			truth = ObjectSerializer.toString(truthQuery,
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
		} catch (SerializationException e) {
			fail("could not serialize");
		}
		
		areEqual(truth, test);
	}

	private void areEqual(String truth, String test) {
		// TODO Auto-generated method stub
		assertTrue("test string : " + test.trim() + " should match truth " + truth.trim(), test.trim().equals(truth.trim()));

	}

	private String convert(String target) throws MalformedQueryException, SerializationException {
		testQuery.put("TargetName", target);
		testQuery.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", "1.3.6.1.4.1.9328.50.1.1918");
		testQuery.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", "1.3.6.1.4.1.9328.50.1.1832");
		testQuery.put("gov.nih.nci.ncia.domain.Patient.patientId", "SOMETHING");
		
		CQLQuery newQuery = null;
		newQuery = makeCQL.makeCQLQuery(testQuery);

		return ObjectSerializer.toString(newQuery, 
					new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
		
	}
	
	
}
