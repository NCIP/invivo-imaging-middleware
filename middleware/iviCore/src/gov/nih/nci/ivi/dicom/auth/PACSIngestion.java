/**
 * 
 */
package gov.nih.nci.ivi.dicom.auth;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.rmi.RemoteException;
import java.util.Properties;

import org.apache.axis.types.URI.MalformedURIException;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomDictionary;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.query.QueryTreeModel;
import com.pixelmed.query.QueryTreeRecord;

import edu.internet2.middleware.subject.SubjectNotFoundException;

import gov.nih.nci.cagrid.gridgrouper.bean.GroupIdentifier;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.GridGrouperRuntimeFault;
import gov.nih.nci.cagrid.gridgrouper.stubs.types.StemNotFoundFault;
import edu.osu.bmi.security.authorization.DataAuthorization;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperHelper;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperHelperException;
import edu.osu.bmi.security.authorization.gridgrouper.GridGrouperUtil;
import gov.nih.nci.ivi.dicom.PixelMedHelper;

/**
 * @author tpan
 *
 */
public class PACSIngestion {
	
	private static final String ROOT = "DICOM";
	private static final String ORG = "OSU";
	private PixelMedHelper pacs = null;
	private GridGrouperHelper grouper = null;
	private String pacsAE = null;
	private String userId;
	
	public PACSIngestion(String propertiesFileName, String gridGrouperURL, String userId) {
		
		this.userId = userId; 
		
		
		Properties properties = null;
		try {
			FileInputStream in = new FileInputStream(propertiesFileName);
			properties = new Properties(/*defaultProperties*/);
			properties.load(in);
			in.close();

			Properties propertiesParams =  new Properties();
			propertiesParams.setProperty("serverip", (String)properties.get("cqlQueryProcessorConfig_serverip"));
			propertiesParams.setProperty("serverport", (String)properties.get("cqlQueryProcessorConfig_serverport"));
			propertiesParams.setProperty("serverAE", (String)properties.get("cqlQueryProcessorConfig_serverAE"));
			propertiesParams.setProperty("clientAE", (String)properties.get("cqlQueryProcessorConfig_clientAE"));
			propertiesParams.setProperty("useCMOVE", (String)properties.get("cqlQueryProcessorConfig_useCMOVE"));
			propertiesParams.setProperty("embeddedPacsAE", (String)properties.get("cqlQueryProcessorConfig_embeddedPacsAE"));
			propertiesParams.setProperty("embeddedPacsPort", (String)properties.get("cqlQueryProcessorConfig_embeddedPacsPort"));
			propertiesParams.setProperty("tempdir", (String)properties.get("cqlQueryProcessorConfig_tempdir"));
			
			pacs = new PixelMedHelper(propertiesParams);
		}
		catch (Exception e) {
			System.err.println(e);
		}
	
		pacsAE = (String)properties.get("cqlQueryProcessorConfig_serverAE");
		System.out.println("PACS AE = " + pacsAE);
		try {
			grouper = new GridGrouperHelper(new URI(gridGrouperURL), ROOT);
			grouper.initialize();
		} catch (MalformedURIException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GridGrouperHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SubjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void ingest() {
		AttributeList filter = new AttributeList();
		
		DicomDictionary dict = new DicomDictionary();
		
		// put the query retrieve level in
		AttributeTag tag = TagFromName.QueryRetrieveLevel;
		byte[] vr = dict.getValueRepresentationFromTag(tag);
		Attribute qlAttr = null;
		try {
			qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
		} catch (DicomException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			qlAttr.addValue("SERIES");
		} catch (DicomException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		filter.put(tag, qlAttr);

		tag = TagFromName.PatientID;
		vr = dict.getValueRepresentationFromTag(tag);
		qlAttr = null;
		try {
			qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
		} catch (DicomException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		filter.put(tag, qlAttr);
		
		
		tag = TagFromName.StudyInstanceUID;
		vr = dict.getValueRepresentationFromTag(tag);
		qlAttr = null;
		try {
			qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
		} catch (DicomException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		filter.put(tag, qlAttr);

		tag = TagFromName.SeriesInstanceUID;
		vr = dict.getValueRepresentationFromTag(tag);
		qlAttr = null;
		try {
			qlAttr = com.pixelmed.dicom.AttributeFactory.newAttribute(tag, vr);
		} catch (DicomException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		filter.put(tag, qlAttr);

		
		QueryTreeModel result = null;
		try {
			result = pacs.query(filter);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (DicomNetworkException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		if (result == null) {
			System.out.println("no results found in PACS");
			return;
		}
		
		QueryTreeRecord treeRoot = QueryTreeRecord.class.cast(result.getRoot());
		
		String[] UIDs = new String[3];
		QueryTreeRecord study = null;
		QueryTreeRecord series = null;

		int permission = DataAuthorization.READ |
		DataAuthorization.defer(DataAuthorization.READ);

		
		// study root, so there is no patient.
		for (int i = 0; i < treeRoot.getChildCount(); i++) {
			study = (QueryTreeRecord) treeRoot.getChildAt(i);
				
			AttributeList studyAttributes = study.getAllAttributesReturnedInIdentifier();
			UIDs[0] = studyAttributes.get(TagFromName.PatientID).getSingleStringValueOrNull();
			UIDs[1] = studyAttributes.get(TagFromName.StudyInstanceUID).getSingleStringValueOrNull();
			
			for (int j = 0; j < study.getChildCount(); j++) {
				series = (QueryTreeRecord) study.getChildAt(j);
				
				AttributeList seriesAttributes = series.getAllAttributesReturnedInIdentifier();
				UIDs[2] = seriesAttributes.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrNull();
				
				try {
					System.out.println("creating " + GridGrouperUtil.toStem(ROOT, ORG, pacsAE, UIDs));
					grouper.createStem(GridGrouperUtil.toStem(ROOT, ORG, pacsAE, UIDs));
				} catch (GridGrouperHelperException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (GridGrouperRuntimeFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (StemNotFoundFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
		try {
			grouper.configureStems(GridGrouperUtil.toStem(ROOT, ORG, null, null), permission);
		} catch (GridGrouperHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GridGrouperRuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StemNotFoundFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SubjectNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// study root, so there is no patient.
		for (int i = 0; i < treeRoot.getChildCount(); i++) {
			study = (QueryTreeRecord) treeRoot.getChildAt(i);
				
			AttributeList studyAttributes = study.getAllAttributesReturnedInIdentifier();
			UIDs[0] = studyAttributes.get(TagFromName.PatientID).getSingleStringValueOrNull();
			UIDs[1] = studyAttributes.get(TagFromName.StudyInstanceUID).getSingleStringValueOrNull();
			UIDs[2] = null;
			
			try {
				System.out.println("adding member to " + GridGrouperUtil.toStem(ROOT, ORG, pacsAE, UIDs));
				GroupIdentifier group = new GroupIdentifier();
				group.setGroupName(GridGrouperUtil.toStem(ROOT, ORG, pacsAE, UIDs) + GridGrouperUtil.SEPARATOR + "Read");
				grouper.addMember(group, this.userId);
			} catch (GridGrouperRuntimeFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (StemNotFoundFault e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (RemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			for (int j = 0; j < study.getChildCount(); j++) {
				series = (QueryTreeRecord) study.getChildAt(j);
				
				AttributeList seriesAttributes = series.getAllAttributesReturnedInIdentifier();
				UIDs[2] = seriesAttributes.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrNull();
				
				try {
					System.out.println("adding member to " + GridGrouperUtil.toStem(ROOT, ORG, pacsAE, UIDs));
					GroupIdentifier group = new GroupIdentifier();
					group.setGroupName(GridGrouperUtil.toStem(ROOT, ORG, pacsAE, UIDs) + GridGrouperUtil.SEPARATOR + "Read");
					grouper.addMember(group, this.userId);
				} catch (GridGrouperRuntimeFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (StemNotFoundFault e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			}
			
		}
	}

	private void clean() {
		try {
			grouper.deleteStems(GridGrouperUtil.toStem(new String[] {ROOT}, null));
		} catch (GridGrouperHelperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (GridGrouperRuntimeFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (StemNotFoundFault e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		if (args.length < 2) {
			System.err.println("usage: java PACSIngestion grouperURL propertiesFile defaultUserId[overwrite]");
			return;
		}
		String propertiesFileName = args[1];
		String gridGrouperURL = args[0];
		String userId = args[2];
		boolean overwrite = Boolean.parseBoolean(args[3]);
		
//		String gridGrouperURL = "https://localhost:8443/wsrf/services/cagrid/GridGrouper";
//		String propertiesFileName = "c:\\Data\\src\\middleware\\projects\\DICOMDataService\\service.properties";
		PACSIngestion ingester = new PACSIngestion(propertiesFileName, gridGrouperURL, userId);

		if (overwrite) {
			System.out.println("deleting existing stem " + ROOT);
			ingester.clean();
		}
		
		ingester.ingest();
	}

}
