package gov.nih.nci.ivi.cerrdataservice.service;

import gov.nih.nci.cagrid.common.FaultHelper;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.CQLQueryProcessor;
import gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType;
import gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;

import java.io.File;
import java.util.Vector;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;

public class CERRRetrieve {

	private CQLQueryProcessor localQueryProcessor;
	private boolean debug1;
	private boolean debug2;
	private Vector <String> retrievedZipFileNames = null;

	final private String localFileNameAttributeInXSD = "localFileLocation";


	public CERRRetrieve(CQLQueryProcessor processor, int debugLevel) {
		this.localQueryProcessor = processor;

		if(debugLevel == 1)
			debug1 = true;
		else if(debugLevel == 2)
		{
			debug1 = true; debug2 = true;
		}

		if(retrievedZipFileNames == null)
			retrievedZipFileNames = new Vector<String>();

	}

	public Vector<String> performRetrieve(CQLQuery cqlQuery) throws MalformedQueryExceptionType, QueryProcessingExceptionType {
		final CQLQuery fcqlq = cqlQuery;

		// TODO: switch over from the normal query to lazy query iterator
		CQLQueryResults queryResults = null;
		try {
			queryResults = localQueryProcessor.processQuery(fcqlq);
		} catch (MalformedQueryException e1) {
			FaultHelper helper = new FaultHelper(new MalformedQueryExceptionType());
			helper.addFaultCause(e1);
			throw (MalformedQueryExceptionType) helper.getFault();
		} catch (QueryProcessingException e1) {
			FaultHelper helper = new FaultHelper(new QueryProcessingExceptionType());
			helper.addFaultCause(e1);
			throw (QueryProcessingExceptionType) helper.getFault();
		}
		// get back the results strings
		CQLQueryResultsIterator iter = new CQLQueryResultsIterator(queryResults, true);
		final CQLQueryResultsIterator fiter = iter;
		String xmlDoc = null;
		int i = 1;
		while (fiter.hasNext()) {
			xmlDoc = (String)fiter.next();
			try {
				System.out.println("Output #" + i++ + " = " + xmlDoc);
				Document document = DocumentHelper.parseText(xmlDoc);
				org.dom4j.Element element = document.getRootElement();
				for ( int j = 0, size = element.attributeCount(); j < size; j++ ) {
					System.out.println("Attrib Name = " + element.attribute(j).getName());
					if(element.attribute(j).getName().equalsIgnoreCase(localFileNameAttributeInXSD) ) {
						org.dom4j.QName tqname = element.attribute(j).getQName();
						//String value = "NewArchive1";
						String cerrFileLocation = element.attribute(j).getValue();
						System.out.println("The local file location is: " + cerrFileLocation);
						if(!cerrFileLocation.equals(null))
						{
							File cerrFileToBeRetrieved = new File(cerrFileLocation);
							if(cerrFileToBeRetrieved.exists())
								retrievedZipFileNames.add(cerrFileLocation);
							else
								System.out.println("The CERR object has the wrong file location");
						}
						else
							System.out.println("The CERR object file location is null");
					}
				}
			} catch (DocumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return retrievedZipFileNames;
	}

}
