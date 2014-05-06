package edu.emory.cci.aim;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import junit.framework.TestCase;


import edu.northwestern.radiology.aim.ImageAnnotation;

public class AIMTCGAModelUtilsTest extends TestCase {

	private JAXBContext jc;
	private Unmarshaller um;
	private Marshaller m;
	

	protected void setUp() throws Exception {
		super.setUp();
		jc = JAXBContext.newInstance( ImageAnnotation.class.getPackage().getName() );
		
		um = jc.createUnmarshaller();
		m = jc.createMarshaller();
		m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testUnmarshall() throws FileNotFoundException, JAXBException {
		FileInputStream fis = new FileInputStream(new File("test/resources/0022BaselineA_Model.xml"));
		
		ImageAnnotation ia = (ImageAnnotation)um.unmarshal(fis);
		assertNotNull(ia);
		assertEquals(ia.getUniqueIdentifier(), "1.2.288.3.2205383238.1512.1207945935.1");
	}

	public void testMarshall() throws JAXBException, FileNotFoundException {
		FileInputStream fis = new FileInputStream(new File("test/resources/0022BaselineA_Model.xml"));
		
		ImageAnnotation ia = (ImageAnnotation)um.unmarshal(fis);
		StringWriter writer = new StringWriter();
		m.marshal(ia, writer);
		System.out.println(writer.toString());
	}

}
