package gov.nih.nci.ivi.cerrdataservice.service;

import edu.osu.bmi.ivi.cerr.CERRObject;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.ivi.utils.Zipper;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.util.Date;
import java.util.Properties;
import java.util.Random;

import javax.xml.namespace.QName;

import org.cvrgrid.xml.db.berkeley.BerkeleyDBXMLConnector;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

public class CERRSubmit {
	private Properties queryProcProps = null;
	private BerkeleyDBXMLConnector connector = null;
	private CERRObject objectToBeUploaded = null;
	final private String localFileNameAttributeInXSD = "localFileLocation";


	public CERRSubmit(Properties queryProps, CERRObject cerrObject) {
		//desc = dataStorageDescriptor;
		queryProcProps = queryProps;
		objectToBeUploaded  = cerrObject;

		try {
			connector = (BerkeleyDBXMLConnector)BerkeleyDBXMLConnector.newInstance(queryProcProps);
		} catch (InitializationException e) {
			//throw new RemoteException("Unable to create database connector " + e.getMessage(), e);
			System.out.println("unable to create database connector " + e.getMessage());
			e.printStackTrace();
		}
	}

	public void processCERRUpload(String localLocation) {
		try {
			File file = new File(localLocation);
			Random r = new Random();
			r.setSeed(System.currentTimeMillis());
			int val = r.nextInt();
			File unzipLocation = new File(queryProcProps.getProperty("upload_location") + File.separator + "AIM_upload" + File.separator + val);
			unzipLocation.mkdirs();

			System.out.println("Unzipping " + file.getAbsolutePath() + " to directory " + unzipLocation);
			String[] files = Zipper.unzip(file.getAbsolutePath(), unzipLocation.getAbsolutePath());
			for (String unzipped : files) {
	
				//Write CERRObject to File
				String localCerrFileName = System.getProperty("java.io.tmpdir") + File.pathSeparator + "tempCerrFile.txt";
				try 
				{
					String cerrObject = ObjectSerializer.toString(objectToBeUploaded, new QName(
							"gme://Imaging.caBIG/1.0/edu.osu.bmi.ivi.cerr", objectToBeUploaded
									.getClass().getName()));
					FileWriter fileWriter = new FileWriter(localCerrFileName);
					BufferedWriter buffWriter = new BufferedWriter(fileWriter);
					buffWriter.write(cerrObject);
					buffWriter.close();
				}
				catch(IOException e) {
					e.printStackTrace();
				} catch (SerializationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				
				replaceXMLFileLocation(unzipped, localCerrFileName);
				File unzippedFile = new File(unzipped);
				//String fileContents = FileUtils.readFileAsString(unzippedFile);
				//validate against schema
				//OMElement element = org.cvrgrid.common.axiom.AxiomUtils.readFile(unzipped);


				// TCP temp disabled boolean isValid = isValid(unzippedFile, this.schemaFile);
				// if (isValid) {
					connector.submit(unzippedFile);
					System.out.println("Adding " + unzipped + " to the xml database");
				/* } else {
					System.out.println("File did not validate against schema at location " + this.schemaFile.getAbsolutePath());
					System.out.println("WARNING: File from URL " + unzipped + " NOT added to the data store!");
				}
				*/

			}

		} catch(Exception e) {
			String msg = "Could not process URIs due to reason: " + e.getMessage();
			//throw new RemoteException(msg, e);
			System.out.println(msg);
			e.printStackTrace();
		}
	}
	
	private void replaceXMLFileLocation(String localFileLocation, String localCerrFileName) {
		try {
			Document document = readWithDOM4J(localCerrFileName);
			org.dom4j.Element element = document.getRootElement();
			for ( int i = 0, size = element.attributeCount(); i < size; i++ ) {
	            if(element.attribute(i).getName().equalsIgnoreCase(localFileNameAttributeInXSD) ) {
	            	org.dom4j.QName tqname = element.attribute(i).getQName();
	            	String value = localFileLocation;
	            	element.attribute(i).setValue(value);
	            }
	        }
			this.writeWithDOM4J(document, localCerrFileName);
		}catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private Document readWithDOM4J(String filename) throws DocumentException {
		System.gc();
		MemoryMXBean mem = java.lang.management.ManagementFactory.getMemoryMXBean();		
		MemoryUsage hm1 = mem.getHeapMemoryUsage();
		MemoryUsage m1 = mem.getNonHeapMemoryUsage();
		
		Date now = new Date();
		SAXReader parser = new SAXReader();
		Document doc = parser.read(new File(filename));
		Date later = new Date();
		MemoryUsage hm2 = mem.getHeapMemoryUsage();
		MemoryUsage m2 = mem.getNonHeapMemoryUsage();
	
		//System.out.println(doc.toXML());
		System.out.println("elapsed time = " + (later.getTime() - now.getTime()));
		System.out.println("heap memory used = " + (hm2.getUsed() - hm1.getUsed()));
		System.out.println("non heap memory used = " + (m2.getUsed() - m1.getUsed()));
		return doc;
	}

	private void writeWithDOM4J(Document newDoc, String outputfilename) throws IOException {
		System.gc();
		MemoryMXBean mem = java.lang.management.ManagementFactory.getMemoryMXBean();		
		MemoryUsage hm1 = mem.getHeapMemoryUsage();
		MemoryUsage m1 = mem.getNonHeapMemoryUsage();
		
		Date now = new Date();
		XMLWriter writer = new XMLWriter(new FileWriter(outputfilename));
		writer.write(newDoc);
		writer.flush();
		writer.close();
		Date later = new Date();
		MemoryUsage hm2 = mem.getHeapMemoryUsage();
		MemoryUsage m2 = mem.getNonHeapMemoryUsage();
	
		//System.out.println(doc.toXML());
		System.out.println("elapsed time = " + (later.getTime() - now.getTime()));
		System.out.println("heap memory used = " + (hm2.getUsed() - hm1.getUsed()));
		System.out.println("non heap memory used = " + (m2.getUsed() - m1.getUsed()));
	}



}
