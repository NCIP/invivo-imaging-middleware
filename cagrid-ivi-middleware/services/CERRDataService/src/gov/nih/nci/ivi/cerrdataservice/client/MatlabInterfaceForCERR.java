package gov.nih.nci.ivi.cerrdataservice.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Vector;
import java.util.zip.ZipInputStream;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI;
import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import edu.osu.bmi.ivi.cerr.CERRObject;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Object;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.utils.CommonUtilities;
import gov.nih.nci.ivi.utils.ClientSecurityUtil;
import gov.nih.nci.ivi.utils.GridFTPFetcher;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;

public class MatlabInterfaceForCERR {

	String url;
	public MatlabInterfaceForCERR() {
		System.out.println("Constructor for MatlabInterfaceForCERR called now");
	}

	public MatlabInterfaceForCERR(String dataSourceURL) {
		dataSourceURL = url;
	}

	private static CQLQuery CERR2CQL(CERRObject cerrQuery)
	{
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object      target = new gov.nih.nci.cagrid.cqlquery.Object();
		target.setName("edu.osu.bmi.ivi.cerr.CERRObject");

		Vector<gov.nih.nci.cagrid.cqlquery.Attribute> attrVec= new Vector<gov.nih.nci.cagrid.cqlquery.Attribute> ();

		if(cerrQuery.getArchive().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("archive");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getArchive());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getTapeStandardNumber().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("tapeStandardNumber");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getTapeStandardNumber());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getIntercomparisonStandard().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("intercomparisonStandard");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getIntercomparisonStandard());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getArchive().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("archive");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getArchive());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getInstitution().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("institution");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getInstitution());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getDateCreated() != null) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("dateCreated");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getDateCreated().toString());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getWriter().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("writer");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getWriter());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getSponsorID().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("sponsorID");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getSponsorID());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getProtocolID().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("protocolID");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getProtocolID());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getSubjectID().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("subjectID");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getSubjectID());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getSubmissionID().length() != 0) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("submissionID");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getSubmissionID());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(cerrQuery.getTimeSaved() != null) {
			gov.nih.nci.cagrid.cqlquery.Attribute attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
			attr.setName("timeSaved");
			attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
			attr.setValue(cerrQuery.getTimeSaved().toString());
			attrVec.add(attr);
		}
		else
			System.out.println("Attrib is null");

		if(attrVec.size() == 0)
		{
			query.setTarget(target);
			return query;
		}
		else if(attrVec.size() == 1)
		{
			target.setAttribute(attrVec.firstElement());
			query.setTarget(target);
			return query;
		}
		else
		{
			gov.nih.nci.cagrid.cqlquery.Group grp = new gov.nih.nci.cagrid.cqlquery.Group();
			grp.setLogicRelation(gov.nih.nci.cagrid.cqlquery.LogicalOperator.fromString("AND"));
			grp.setAttribute(attrVec.toArray(new gov.nih.nci.cagrid.cqlquery.Attribute[attrVec.size()]));
			target.setGroup(grp);
			query.setTarget(target);
			return query;
		}
	}

	public static CERRObject[] queryCERRDataService(CERRObject queryObject, String dataSourceURL)
	{
		Vector <CERRObject> queryResults = new Vector <CERRObject>();
		CQLQuery query = CERR2CQL(queryObject);
		try {
			CERRDataServiceClient client = new CERRDataServiceClient(dataSourceURL);
			CQLQueryResults results = client.query(query);

			CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results);
			while (iter.hasNext()) {
				CERRObject result = (CERRObject)iter.next();
				queryResults.add(result);

				try {
					System.out.println(ObjectSerializer.toString(result, new QName(
							"gme://ncia.caBIG/1.0/gov.nih.nci.ncia.domain", result
									.getClass().getName())));
				} catch (SerializationException e) {
				}
			}
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return queryResults.toArray(new CERRObject[queryResults.size()]);
	}

	public static String retrieveCERRDataService(CERRObject queryObject, String dataSourceURL, String localDestination)
	{
		final CQLQuery fcqlQuery = CERR2CQL(queryObject);
		InputStream istream = null;
		TransferServiceContextClient tclient = null;
		try {
			CERRDataServiceClient cerrDataService = new CERRDataServiceClient(dataSourceURL);
			tclient = new TransferServiceContextClient(cerrDataService
					.retrieveCERRObjects(fcqlQuery).getEndpointReference());
			istream = TransferClientHelper.getData(tclient
					.getDataTransferDescriptor());
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			} catch (IOException e) {
				System.err.println("IOException thrown when recieving the zip stream");
				e.printStackTrace();
			}

			File localLocation = new File(localDestination);
			if (!localLocation.exists())
				localLocation.mkdirs();

			String retrievedFile = localDestination + File.separator
					+ zeis.getName();
			bis = new BufferedInputStream(zeis);
			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			try {
				BufferedOutputStream bos = new BufferedOutputStream(
						new FileOutputStream(retrievedFile));
				while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
					bos.write(data, 0, bytesRead);
					// System.out.println(new String(data));
					// System.out.println("caGrid transferred at " + new
					// Date().getTime());
				}
				bos.flush();
				bos.close();
			} catch (IOException e) {
				System.err.println("IOException thrown when reading the zip stream");
				e.printStackTrace();
			}
		}

		try {
			zis.close();
		} catch (IOException e) {
			System.err.println("IOException thrown when closing the zip stream");
			e.printStackTrace();
		}

		try {
			tclient.destroy();
		} catch (RemoteException e) {
			System.err.println("Remote exception thrown when closing the transer context");
			e.printStackTrace();
		}
		File localLocation = new File(localDestination);
		return localLocation.list()[0];

	}

	public static void submitCERRDataService(String dataDestinationURL, String localFileLocation)
	{

	}


}