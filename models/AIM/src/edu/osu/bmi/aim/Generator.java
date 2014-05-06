package edu.osu.bmi.aim;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import edu.northwestern.radiology.aim.AnatomicEntity;
import edu.northwestern.radiology.aim.Annotation;
import edu.northwestern.radiology.aim.AnnotationAnatomicEntityCollection;
import edu.northwestern.radiology.aim.AnnotationUser;
import edu.northwestern.radiology.aim.ImageAnnotation;
import edu.northwestern.radiology.aim.User;

public class Generator {

	public void marshall(File xmlFile){
        BigInteger bi1 = new BigInteger("1234567890123456890");
        Annotation annot = new ImageAnnotation();
        annot.setId(bi1);
        annot.setName("ImageAnnotation1");
        annot.setComment("Some comment");

        User user = new User();
        user.setAuthorName("Jarek");
        user.setId(new BigInteger("123"));
        AnnotationUser user2 = new AnnotationUser();
        user2.setUser(user);
        annot.setUser(user2);


        AnatomicEntity anatEnt = new AnatomicEntity();
        anatEnt.setCodeMeaning("Some meaning 1");
        anatEnt.setId(bi1);
        AnnotationAnatomicEntityCollection anatEntColl = new AnnotationAnatomicEntityCollection();
        anatEntColl.setAnatomicEntity(new AnatomicEntity[] {anatEnt});
        annot.setAnatomicEntityCollection(anatEntColl);




        FileWriter writer = null;
		try {
			writer = new FileWriter(xmlFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        try {
			ObjectSerializer.serialize(writer, annot, new QName("gme://caCORE.caCORE/3.2/edu.northwestern.radiology.AIM", "Annotation"));
		} catch (SerializationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		Generator gen = new Generator();

		gen.marshall(new File("test.xml"));
	}

}
