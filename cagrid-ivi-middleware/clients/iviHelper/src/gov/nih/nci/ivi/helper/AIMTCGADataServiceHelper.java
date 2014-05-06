package gov.nih.nci.ivi.helper;

import edu.emory.cci.aim.client.AIMTCGADataServiceClient;
import edu.osu.bmi.utils.io.zip.ZipEntryInputStream;
import gov.nih.nci.cagrid.cqlquery.Association;
import gov.nih.nci.cagrid.cqlquery.Attribute;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlquery.Group;
import gov.nih.nci.cagrid.cqlquery.LogicalOperator;
import gov.nih.nci.cagrid.cqlquery.Predicate;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.utils.Zipper;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.zip.ZipInputStream;

import org.apache.axis.types.URI.MalformedURIException;
import org.cagrid.transfer.context.client.TransferServiceContextClient;
import org.cagrid.transfer.context.client.helper.TransferClientHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;
import org.cagrid.transfer.descriptor.Status;

public class AIMTCGADataServiceHelper {

	boolean debug;

	public AIMTCGADataServiceHelper() {
		//may modify to handle security
		//One of the properties that must be created when using
	}

	public Iterator queryAnnotations(CQLQuery cqlQuery, String dataSourceURL) throws MalformedURIException, RemoteException {
		AIMTCGADataServiceClient imageClient;
		imageClient = new AIMTCGADataServiceClient(dataSourceURL);
		CQLQueryResults result = imageClient.query(cqlQuery);
		CQLQueryResultsIterator iter2 = new CQLQueryResultsIterator(result,
				true);

		return iter2;
	}

	public void retrieveAnnotations(CQLQuery cqlQuery, String dataSourceURL,
			String localDownloadLocation) throws Exception {

		AIMTCGADataServiceClient aimDataService;
		InputStream istream = null;
		TransferServiceContextClient tclient = null;

			aimDataService = new AIMTCGADataServiceClient(dataSourceURL);
			TransferServiceContextReference ref = aimDataService
			.queryByTransfer(cqlQuery);
			
		if (ref == null) throw new Exception("no results were located");
				
		tclient = new TransferServiceContextClient(ref.getEndpointReference());
		istream = TransferClientHelper.getData(tclient
				.getDataTransferDescriptor());

		if(istream == null)
		{
			System.err.println("Inputstream recvd from TransferServiceClient was null");
			return;
		}

		ZipInputStream zis = new ZipInputStream(istream);
		ZipEntryInputStream zeis = null;
		BufferedInputStream bis = null;
		while (true) {
			try {
				zeis = new ZipEntryInputStream(zis);
			} catch (EOFException e) {
				break;
			}

			// System.out.println(zeis.getName());

			File localLocation = new File(localDownloadLocation);
			if (!localLocation.exists())
				localLocation.mkdirs();

			String unzzipedFile = localDownloadLocation + File.separator
					+ zeis.getName() + ".xml";
			bis = new BufferedInputStream(zeis);
			// do something with the content of the inputStream

			byte[] data = new byte[8192];
			int bytesRead = 0;
			BufferedOutputStream bos = new BufferedOutputStream(
					new FileOutputStream(unzzipedFile));
			while ((bytesRead = (bis.read(data, 0, data.length))) > 0) {
				bos.write(data, 0, bytesRead);
				// System.out.println(new String(data));
				// System.out.println("caGrid transferred at " + new
				// Date().getTime());
			}
			bos.flush();
			bos.close();
		}

		zis.close();

		tclient.destroy();

		File localLocation = new File(localDownloadLocation);
		System.out.println(localLocation.listFiles().length);
		// assertEquals(localLocation.listFiles().length, 79);
	}

	public void submitAnnotations(String aimFilesToBeUploaded,
			String dataDestinationURL) throws Exception {

		// Step 1. Zip the local annotations
		String zippedFileName = System.getProperty("java.io.tmpdir")
				+ File.separator + "localZippedFile.zip";
		File zippedFile = new File("zippedFileName");
		if (zippedFile.exists())
			zippedFile.delete();
		
		Zipper.zip(zippedFileName, new String[] { aimFilesToBeUploaded }, true);
		
		// Step 2. Transfer them via transfer service
		AIMTCGADataServiceClient aimDataService = null;
		aimDataService = new AIMTCGADataServiceClient(dataDestinationURL);

		TransferServiceContextClient tclient = null;
		tclient = new TransferServiceContextClient(aimDataService
				.submitByTransfer().getEndpointReference());

		File transferDoc = new File(zippedFileName);
		System.out.println("transferDoc is " + transferDoc);
		BufferedInputStream aimIn = new BufferedInputStream(
				new FileInputStream(transferDoc));
		TransferClientHelper.putData(aimIn, transferDoc.length(), tclient
				.getDataTransferDescriptor());
		aimIn.close();

		tclient.setStatus(Status.Staged);

	}

	public static CQLQuery generateImageAnnotationQuery(String studyInstanceUID, String seriesInstanceUID, String loginName) {
		CQLQuery newQuery = null;
		newQuery = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object target = new gov.nih.nci.cagrid.cqlquery.Object();
		target.setName("edu.northwestern.radiology.aim.ImageAnnotation");
		newQuery.setTarget(target);

		Association images = null;
		if (studyInstanceUID != null) {
		
			images = new Association("imageReferenceCollection");
			images.setName("edu.northwestern.radiology.aim.DICOMImageReference");

			Association study = new Association("study");
			study.setName("edu.northwestern.radiology.aim.Study");
			images.setAssociation(study);
			
			Attribute studyUID = new Attribute("instanceUID", Predicate.EQUAL_TO, studyInstanceUID);

			if (seriesInstanceUID != null) {
				
				Association series = new Association("series");
				series.setName("edu.northwestern.radiology.aim.Series");
				Attribute seriesUID = new Attribute("instanceUID", Predicate.EQUAL_TO, seriesInstanceUID);
				series.setAttribute(seriesUID);
								
				Group studyGroup = new Group(new Association[] {series}, new Attribute[] {studyUID}, null, LogicalOperator.AND);
				study.setGroup(studyGroup);
				
			} else {
				study.setAttribute(studyUID);
			}
		}
				
		Association user = null;
		if (loginName != null) {

			user = new Association("user");
			user.setName("edu.northwestern.radiology.aim.User");
			
			user.setAttribute(new Attribute("loginName", Predicate.EQUAL_TO, loginName));
		}
		
		if (loginName != null && studyInstanceUID != null) {
			// both, so need a group
			
			Group annotationGroup = new Group(new Association[] {user, images}, null, null, LogicalOperator.AND );
			target.setGroup(annotationGroup);
			
		} else if (loginName != null) {
			target.setAssociation(user);
		} else if (studyInstanceUID != null) {
			target.setAssociation(images);
		}
				
		return newQuery;

	}

}
