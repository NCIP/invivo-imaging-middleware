package gov.nih.nci.ivi.dicom;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

public class DICOM2CQL {
	public static CQLQuery convert2CQL(ModelMap mMap, AttributeList filter) {
//		ModelMap mMap = null;
//		try {
//			mMap = new ModelMap();
//		} catch (FileNotFoundException e2) {
//			e2.printStackTrace();
//		} catch (ModelMapException e2) {
//			e2.printStackTrace();
//		} catch (IOException e2) {
//			e2.printStackTrace();
//		} catch (ClassNotFoundException e2) {
//			e2.printStackTrace();
//		}
		HashMap<String, String> queryHashMap = new HashMap<String, String>();

		// create the list straight from the filter
		int attrArrayIdx = -1;
		String attrValue = null;
		HashMap<String, Integer> attrNames = null;
		AttributeTag tag = null;
		Attribute attr = null;

		// iterate through the attribute list
		Iterator iter = filter.keySet().iterator();
		while (iter.hasNext()) {
			tag = AttributeTag.class.cast(iter.next());
			attr = Attribute.class.cast(filter.get(tag));

//			try {
//				if (mMap == null) {
//					System.out.println("model map is null");
//				} else
//				if (tag == null) {
//					System.out.println("tag is null");
//				} else 
//				if (attr == null) {
//					System.out.println("attribute tag is " + mMap.getDicomDict().getFullNameFromTag(tag) + " attr is null");
//				} else 
//				if (attr.getStringValues() == null) {
//					System.out.println("attribute tag is " + mMap.getDicomDict().getFullNameFromTag(tag) + " attr string values is null");
//				} else {
//					System.out.println("attribute tag is " + mMap.getDicomDict().getFullNameFromTag(tag) + ", value = " + (attr == null ? "null" : attr.toString(mMap.getDicomDict()) + ", string value = " + attr.getStringValues()[0]));
//				}	
//			} catch (DicomException e2) {
//				// TODO Auto-generated catch block
//				e2.printStackTrace();
//			}
			
			// query retrieve level is used as target in the cql
			if (tag.equals(TagFromName.QueryRetrieveLevel)) {
				try {
					// hardcode to string at array index 0
					String qrLevel = attr.getStringValues()[0];
					System.out.println("Query Retrieve Level = " + qrLevel);

					// get the target class name.  It's query, so "contenttype" should not appear
					Class queryRetrieveClass = mMap.getModelClassFromQueryRetrieveLevel(qrLevel);

					queryHashMap.put(HashmapToCQLQuery.TARGET_NAME_KEY, queryRetrieveClass.getCanonicalName());
				} catch (DicomException e) {
					e.printStackTrace();
				} catch (ModelMapException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// get the id to model attribute name mapping from the tag
			attrNames = null;
			try {
				attrNames = mMap.getModelAttributeNamesFromAttributeTag(tag);
			} catch (ModelMapException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (attrNames == null || attrNames.size() <= 0) {
				// tag is not in model, so continue;
				System.out.println("WARNING: dicom tag is not mapped to any attributes: " + mMap.getDicomDict().getFullNameFromTag(tag));
				continue;
			}

			String[] attrValues = null;
			try {
				attrValues = attr.getStringValues();
			} catch (DicomException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			if (attrValues == null) {
				// TODO: will need to change this later to support selectively populating of fields
				continue;
			}
			
			// iterate through the model attribute names.
			for (String attrName : attrNames.keySet()) {

				attrArrayIdx = attrNames.get(attrName);
				if (attrArrayIdx == ModelMap.NON_ARRAY_INDEX) {
					attrArrayIdx = 0;
				}

				// put the attribute and its value in the hashmap
				// check to see if attr value is empty
				System.out.println("  model attr name = " + attrName + ", attribute array index = " + attrArrayIdx + ", value = " + attrValues[attrArrayIdx]);
				queryHashMap.put(attrName, attrValues[attrArrayIdx]);
			}
		}

		System.out.println("queryHashMap = " + queryHashMap);
		
		// now create the cqlquery and return it
		CQLQuery newQuery = null;
		HashmapToCQLQuery makeCQL = new HashmapToCQLQuery(mMap);
		try {
			newQuery = makeCQL.makeCQLQuery(queryHashMap);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		}
		return newQuery;
	}
}