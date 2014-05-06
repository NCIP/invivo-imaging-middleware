/**
 *
 */
package gov.nih.nci.ivi.dicom;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.ivi.dicom.modelmap.ModelDictionary;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;

/**
 * @author ashish
 * This is a convenience class the walks through a user input and constructs the
 * appropriate CQL Query.  It uses the ModelDictionary and the ModelMap to facilitate
 * the creation of the CQL Query.
 *
 * The user defined target is selected as the root node.  The Attributes and Associations
 * are recursively determined in a depth first manner.
 */
public class HashmapToCQLQuery {

	public static final String TARGET_NAME_KEY = "TargetName";

	private Map <String, String>queryFromGUI = null;
	private Map <String, String[]>queryFromHash = null;
	private LinkedList <String> list = null;
	ModelDictionary dictOfDataModel = null;

	private boolean debug = false;

	public HashmapToCQLQuery(ModelMap map)
	{
		this.dictOfDataModel = map.getModelDict();
	}

	public boolean validateHashMap(Map <String, String> userQuery)
	{
		if(!userQuery.containsKey(TARGET_NAME_KEY))
			return false;
		try {
			if(!dictOfDataModel.isNameValid(userQuery.get(TARGET_NAME_KEY)))
				return false;
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		userQuery.remove(TARGET_NAME_KEY);
		Iterator itr = userQuery.keySet().iterator();
		while(itr.hasNext())
		{
			String attributeName = (String) itr.next();
			try {
				if(!dictOfDataModel.isNameValid(attributeName))
					return false;
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return true;
	}

	/*
	 * This method will take a user input has a HashMap and create a CQL query
	 * The HashMap must include a key "TargetName" and the corrosponding CQL Target as its value.
	 * All Attribute names and TragetNames have to be fully qualified.  This method
	 * allows you to specify multiple attribute values
	 */
	public CQLQuery makeCQLQuery1(Map <String, String[]> userQuery) throws MalformedQueryException{
		if (userQuery == null) {
			throw new MalformedQueryException("null hashmap");
		}
		if (userQuery.isEmpty()) {
			throw new MalformedQueryException("empty hashmap");
		}

		HashSet<String> visited = new HashSet<String>();

		queryFromHash = userQuery;

		if(queryFromHash.get("TargetName").length!=1)
			throw new MalformedQueryException("Multiple " + queryFromHash.get("TargetName").length + " targets specified");
		final String targetName = queryFromHash.get("TargetName")[0];
		gov.nih.nci.cagrid.cqlquery.Group	  targetGrp = null;

		return null;
	}


	/*
	 * This method will take a user input has a HashMap and create a CQL query
	 * The HashMap must include a key "TargetName" and the corrosponding CQL Target as its value.
	 * All Attribute names and TragetNames have to be fully qualified
	 */
	public CQLQuery makeCQLQuery(Map <String, String> userQuery) throws MalformedQueryException{
		if (userQuery == null) {
			throw new MalformedQueryException("null hashmap");
		}
		if (userQuery.isEmpty()) {
			throw new MalformedQueryException("empty hashmap");
		}

		HashSet<String> visited = new HashSet<String>();

		queryFromGUI = userQuery;

		final String targetName = queryFromGUI.get("TargetName");
		gov.nih.nci.cagrid.cqlquery.Group	  targetGrp = null;

		// Lets make the query
		CQLQuery query = new CQLQuery();
		gov.nih.nci.cagrid.cqlquery.Object      target = new gov.nih.nci.cagrid.cqlquery.Object();
		target.setName(targetName);
		queryFromGUI.remove("TargetName");

		gov.nih.nci.cagrid.cqlquery.Attribute [] targetAttr = getCQLAttributeFromAssociation(targetName, null);
		Vector<gov.nih.nci.cagrid.cqlquery.Association> childAssocVec= new Vector<gov.nih.nci.cagrid.cqlquery.Association> ();

		String [] childAssocNames = getAllAssociations(targetName, null, visited);
		gov.nih.nci.cagrid.cqlquery.Association cqlAssoc = null;

		if(childAssocNames.length != 0)
		{
			for(int i = 0; i < childAssocNames.length; i++)
			{
				cqlAssoc = getCQLAssociationFromUserQuery (childAssocNames[i], targetName, null, visited);
				if(cqlAssoc != null)
					childAssocVec.add(cqlAssoc);
			}
		}

		// Does the target have an attribute
		if(targetAttr.length != 0)
		{
			// 1 or more attributes
			targetGrp = new gov.nih.nci.cagrid.cqlquery.Group();
			targetGrp.setLogicRelation(gov.nih.nci.cagrid.cqlquery.LogicalOperator.fromString("AND"));

			if(targetAttr.length >1)
			{
				// 2 or more attributes and 0 or more associations
				if(childAssocVec.size() != 0)
				{
					gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
					targetGrp.setAssociation(childAssocVec.toArray(currAssocAry));
				}
				targetGrp.setAttribute(targetAttr);
				target.setGroup(targetGrp);
			}
			else if(targetAttr.length == 1)
			{
				// 1 attribute and 0 or more associations
				if(childAssocVec.size() != 0)
				{
					gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
					targetGrp.setAssociation(childAssocVec.toArray(currAssocAry));

					targetGrp.setAttribute(targetAttr);
					target.setGroup(targetGrp);
				}
				else
					target.setAttribute(targetAttr[0]);
			}
		}
		else
		{
			// 0 attributes
			targetGrp = new gov.nih.nci.cagrid.cqlquery.Group();
			targetGrp.setLogicRelation(gov.nih.nci.cagrid.cqlquery.LogicalOperator.fromString("AND"));

			if(childAssocVec.size() > 1)
			{
				// 2 or more associations
				gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
				targetGrp.setAssociation(childAssocVec.toArray(currAssocAry));
				target.setGroup(targetGrp);
			}
			else if(childAssocVec.size() == 1)
			{
				// 1 association
				target.setAssociation(childAssocVec.get(0));
			}
			else if(childAssocVec.size() == 0)
			{
				System.out.println("No Attributes or Associations for this target");
			}
		}

		query.setTarget(target);
		return query;
	}

	/*
	 * This method will examine the user input and see if any of the attributes matches the current
	 * association.  One of the following 2 scenarios can happen:
	 * 1. One or more matches are found, it will return an array of Attributes and also remove those
	 *    attributes from the user input HashMap.
	 * 2. No matches are found, it will return an Attribute array of size 0;
	 *
	 * Input Conditions:
	 * The className is fully qualified i.e. it looks like: edu.osu.bmi.dicom.StudyInstanceUIDType
	 * The userQuery HashMap has fully qualified attribute name as it's key and a string as it's value
	 * i.e. It looks like <"edu.osu.bmi.dicom.StudyInstanceUIDType._value", "1.3.2....">
	 *
	 * Output Assumption
	 * The caller will always get an Attribute array - they can check its validity from its length.
	 */
	private gov.nih.nci.cagrid.cqlquery.Attribute [] getCQLAttributeFromAssociation(String currAssocName,
			HashMap <String, String> userQuery)
	{
		Vector<gov.nih.nci.cagrid.cqlquery.Attribute> cqlAttribsVec= new Vector<gov.nih.nci.cagrid.cqlquery.Attribute> ();

		String [] temp = new String[queryFromGUI.size()];
		String [] userQueryKeys = queryFromGUI.keySet().toArray(temp);
		for(int i = 0; i < userQueryKeys.length; i++)
		{
			int lastID = userQueryKeys[i].lastIndexOf('.');
			String userQueryKeyClassName = userQueryKeys[i].substring(0, lastID);
			if(userQueryKeyClassName.equals(currAssocName))
			{
				//We have an attribute to create
				gov.nih.nci.cagrid.cqlquery.Attribute newAttr = new gov.nih.nci.cagrid.cqlquery.Attribute();

				newAttr.setName(userQueryKeys[i].substring(lastID+1, userQueryKeys[i].length()));
				newAttr.setValue(queryFromGUI.get(userQueryKeys[i]));
				if(newAttr.getValue().endsWith("%") || newAttr.getValue().endsWith("*"))
				{	//Wild Card
					newAttr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("LIKE"));
					if(newAttr.getValue().endsWith("*"))
						newAttr.setValue(newAttr.getValue().substring(0, newAttr.getValue().length()-1).concat("%"));				
				}
				else
					newAttr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));

				cqlAttribsVec.add(newAttr);

				//Remove Attribute from userQuery HashMap
				queryFromGUI.remove(userQueryKeys[i]);
			}
		}
		gov.nih.nci.cagrid.cqlquery.Attribute [] CQLAttribsAry = new gov.nih.nci.cagrid.cqlquery.Attribute[cqlAttribsVec.size()];
		return cqlAttribsVec.toArray(CQLAttribsAry);
	}

	private String getRoleNameFromClassName(String myClassName, String parentClassName)
	{
		String myRoleName = null;
		Class parentClass = null;
		try {
			// TODO: revisit.  may be possible to make this more efficient.
			// String tempAttribName = dictOfDataModel.getAttributeNameContains(parentClassName).toArray()[0].toString();
			parentClass = Class.forName(parentClassName); //dictOfDataModel.getInformationEntityClassFromAttributeName(tempAttribName);

			// Get All Children including the parent
			Iterator iter2 = dictOfDataModel.getChildFieldsFromClass(parentClass).iterator();
			while (iter2.hasNext()) {
				Field f = Field.class.cast(iter2.next());
				String childName = null;
				if(f.getType().isArray())
					childName = f.getType().getCanonicalName().substring(0, f.getType().getCanonicalName().indexOf('['));
				else
					childName = f.getType().getCanonicalName();

				if(childName.equals(myClassName))
					myRoleName = f.getName();
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return myRoleName;
	}


	private String[] getAllAssociations(String currAssocName, String parentAssocName, HashSet<String> visited)
	{
		visited.add(currAssocName);

		Class currAssocClass = null;
		Vector <String> allAssociations = new Vector<String>();
		String actualParentName = null;

		try {
			// TODO: revisit.  may be possible to make this more efficient.
			// String tempAttribName = dictOfDataModel.getAttributeNameContains(currAssocName).toArray()[0].toString();
			currAssocClass = Class.forName(currAssocName); //dictOfDataModel.getInformationEntityClassFromAttributeName(tempAttribName);

			// Get Actual Parent
			Vector<Class> lineage = dictOfDataModel.getLineage(currAssocClass);
			int lineageSize = lineage.size();
			if(lineageSize == 1)
				actualParentName = null;
			else if(lineageSize > 1)
				actualParentName = lineage.get(1).getCanonicalName();
			else
				System.err.println("lineage equals zero.  This should not happen");

			// Get All Children including the parent
			Iterator iter2 = dictOfDataModel.getChildFieldsFromClass(currAssocClass).iterator();
			while (iter2.hasNext()) {
				Field f = Field.class.cast(iter2.next());
				String childName = f.getType().getCanonicalName();
				if(f.getType().isArray())
					childName = f.getType().getComponentType().getCanonicalName();

				if(childName.equals(currAssocName) == false && childName.equals(parentAssocName) == false &&
						!visited.contains(childName)) {
					// exclude the true parent class and also self from the child list.
					// however, this does not deal with cycles of circumference of more than 2
					allAssociations.add(childName);
				}
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ModelMapException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		String assocArray[] = new String[allAssociations.size()];
		return allAssociations.toArray(assocArray);
	}

	private gov.nih.nci.cagrid.cqlquery.Association getCQLAssociationFromUserQuery(String currAssocName,
			String parentAssocName,
			HashMap <String, String> userQuery, HashSet<String> visited)
			{
		String [] temp = new String[queryFromGUI.size()];
		String [] userQueryKeys = queryFromGUI.keySet().toArray(temp);
		if(userQueryKeys.length == 0)
			return null;

		Vector<gov.nih.nci.cagrid.cqlquery.Association> childAssocVec= new Vector<gov.nih.nci.cagrid.cqlquery.Association> ();
		gov.nih.nci.cagrid.cqlquery.Group currGrp = null;
		gov.nih.nci.cagrid.cqlquery.Association currAssoc = null;

		// Get All Attributes for Current Association
		gov.nih.nci.cagrid.cqlquery.Attribute [] currAttr = getCQLAttributeFromAssociation(currAssocName, null);

		// Get All Child_Associations for Current Association
		String [] childAssocNames = getAllAssociations(currAssocName, parentAssocName, visited);
		gov.nih.nci.cagrid.cqlquery.Association cqlAssoc = null;

		if(childAssocNames.length != 0)
		{
			String [] temp1 = new String[queryFromGUI.size()];
			String [] userQueryKeysRev = queryFromGUI.keySet().toArray(temp1);
			if(userQueryKeysRev.length > 0)
			{
				for(int i = 0; i < childAssocNames.length; i++)
				{
					if (debug) System.out.println("cild assoc name " + childAssocNames[i]);
					if (debug) System.out.println("curr assoc name " + currAssocName);
					cqlAssoc = getCQLAssociationFromUserQuery (childAssocNames[i], currAssocName, null, visited);
					if(cqlAssoc != null)
						childAssocVec.add(cqlAssoc);
				}
			}
		}

		if(currAttr.length != 0) // ASSOC has values
		{
			currAssoc = new gov.nih.nci.cagrid.cqlquery.Association();
			currAssoc.setName(currAssocName);
			currAssoc.setRoleName(getRoleNameFromClassName(currAssocName, parentAssocName));

			currGrp = new gov.nih.nci.cagrid.cqlquery.Group();
			currGrp.setLogicRelation(gov.nih.nci.cagrid.cqlquery.LogicalOperator.fromString("AND"));

			if(currAttr.length > 1)
			{
				// 2 or more attributes and 0 or more associations
				currGrp.setAttribute(currAttr);

				if(childAssocVec.size() != 0)
				{
					gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
					currGrp.setAssociation(childAssocVec.toArray(currAssocAry));
				}

				currAssoc.setGroup(currGrp);
			}
			else if(childAssocVec.size() > 1)
			{
				// 1 attribute and 2 or more associations
				currGrp.setAttribute(currAttr);

				gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
				currGrp.setAssociation(childAssocVec.toArray(currAssocAry));

				currAssoc.setGroup(currGrp);
			}
			else if(currAttr.length == 1 && childAssocVec.size() <= 1)
			{
				// 1 attribute and 0 or 1 associations
				if(childAssocVec.size() == 0)
				{
					currAssoc.setAttribute(currAttr[0]);
				}
				else if(childAssocVec.size() == 1)
				{
					gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
					currGrp.setAssociation(childAssocVec.toArray(currAssocAry));
					currGrp.setAttribute(currAttr);
					currAssoc.setGroup(currGrp);
				}
			}
		}
		else if(currAttr.length == 0) // ASSOC has no values
		{
			currAssoc = new gov.nih.nci.cagrid.cqlquery.Association();
			currAssoc.setName(currAssocName);
			currAssoc.setRoleName(getRoleNameFromClassName(currAssocName, parentAssocName));

			currGrp = new gov.nih.nci.cagrid.cqlquery.Group();
			currGrp.setLogicRelation(gov.nih.nci.cagrid.cqlquery.LogicalOperator.fromString("AND"));

			if(childAssocVec.size() > 1)
			{
				gov.nih.nci.cagrid.cqlquery.Association [] currAssocAry = new gov.nih.nci.cagrid.cqlquery.Association[childAssocVec.size()];
				currGrp.setAssociation(childAssocVec.toArray(currAssocAry));

				currAssoc.setGroup(currGrp);
			}
			else if(childAssocVec.size() == 1)
			{
				currAssoc.setAssociation(childAssocVec.get(0));
			}
			else if(childAssocVec.size() == 0)
			{
				currAssoc = null;
			}
		}
		return currAssoc;

			}

	public static void main(String [] args)
	{
		ModelMap map = null;
		try {
			map = new ModelMap();
		} catch (FileNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ModelMapException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (IOException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		} catch (ClassNotFoundException e2) {
			// TODO Auto-generated catch block
			e2.printStackTrace();
		}

		HashmapToCQLQuery makeCQL = new HashmapToCQLQuery(map);
		com.pixelmed.dicom.DicomDictionary dict = new com.pixelmed.dicom.DicomDictionary();

		HashMap <String, String> testQuery = new HashMap<String, String>();


		String[] targets = new String[] {"gov.nih.nci.ncia.domain.Patient", "gov.nih.nci.ncia.domain.Study",
				"gov.nih.nci.ncia.domain.Series", "gov.nih.nci.ncia.domain.Image", 
				"gov.nih.nci.ncia.domain.ClinicalTrialSite"};
		for (int i = 0 ; i < targets.length; i++) {
			testQuery.put("TargetName", targets[i]);
			testQuery.put("gov.nih.nci.ncia.domain.Series.seriesInstanceUID", "1.3.6.1.4.1.9328.50.1.1918");
			testQuery.put("gov.nih.nci.ncia.domain.Study.studyInstanceUID", "1.3.6.1.4.1.9328.50.1.1832");
			testQuery.put("gov.nih.nci.ncia.domain.Patient.patientId", "SOMETHING*");


			CQLQuery newQuery = new CQLQuery();

			try {
				newQuery = makeCQL.makeCQLQuery(testQuery);
			}
			catch (Exception e) {
				e.printStackTrace();
			}

			try {
				String TEMP = ObjectSerializer.toString(newQuery,
						new QName("http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery", "CQLQuery"));
				System.out.println(TEMP);
			} catch (SerializationException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			CQL2DICOM cql2 = null;
			try {
				cql2 = new CQL2DICOM(map);
			} catch (ModelMapException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
	 		try {
				AttributeList newfilter = cql2.cqlToPixelMed(newQuery);
				System.out.println("xpath =\n" + newfilter.toString(dict));
			}
			catch (DicomException e) {
				e.printStackTrace();
			} catch (MalformedQueryException e) {
				e.printStackTrace();
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}

}