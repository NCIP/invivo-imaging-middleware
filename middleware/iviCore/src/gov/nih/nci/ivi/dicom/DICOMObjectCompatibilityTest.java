/**
 * 
 */
package gov.nih.nci.ivi.dicom;

import gov.nih.nci.ivi.dicom.modelmap.ModelDictionary;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;

/**
 * @author tpan
 *
 */
public class DICOMObjectCompatibilityTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		if (args.length < 1) {
			System.out.println("usage: java " + DICOMObjectCompatibilityTest.class.getCanonicalName() + " dicomFilename");
			return;
		}
		
		try {
			// read the file
			DicomInputStream dis = new DicomInputStream(new BufferedInputStream(new FileInputStream(args[0])));
			AttributeList attrs = new AttributeList();
			attrs.read(dis);
			
			// try to convert it to model
			ModelMap map = new ModelMap();
			DICOM2Model d2m = new DICOM2Model(map);
			
			ModelDictionary dict = map.getModelDict();
			Iterator<Class> iter = dict.getInformationEntityMap().values().iterator();
			
			while (iter.hasNext()) {
				Class c = iter.next();
				Object o = c.newInstance();
				Object o2 = d2m.populateFields(o, attrs);
				
				String t = ObjectSerializer.toString(o2, 
						new QName("http://caCORE.caBIG/3.0/gov.nih.nci.ncia.domain", c.getSimpleName()));
				System.out.println(t);

			}
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DataConversionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
