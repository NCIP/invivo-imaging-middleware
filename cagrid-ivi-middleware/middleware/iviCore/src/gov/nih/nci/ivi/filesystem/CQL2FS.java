/**
 * 
 */
package gov.nih.nci.ivi.filesystem;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.data.MalformedQueryException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

/**
 * @author tpan
 * used only for navigating the filesystem
 * does not support groups right now.
 *  
 *  IDEAL The cql target is the class to be returned.
 *  	the target contains an association with no RoleName.  this represents the root
 *  	the rolename of the association is the identifier of the dataset
 *  	an attribute terminates the traversal
 *  
 *  
 *  CURRENT  for now.  have a very simple structure.  cql is used for getting a file
 *  	target is the class to be returned. (image only)
 *  		target has attribute that is the filename (name=file, value="filename", predicate="EQUALS_TO")
 *  query with empty attribute should return everything.
 *  assume images are in a flat space.  space in filename is allowed but not preferred.  do not need back slash to escape space
 */
public class CQL2FS {

	// convert a cql attribute statement to path.   this is the easy one.
	// attribute value can be something, or wildcard
	public static String cqlAttributeToPath(gov.nih.nci.cagrid.cqlquery.Attribute attribute) throws MalformedQueryException {
		if (attribute == null)
			throw new MalformedQueryException("CQL2FS: attribute is null");
		if (!attribute.getName().equals("path"))
			throw new MalformedQueryException("CQL2FS: only attribute with the name \"path\" is supported in CQL for filesystem navigation");
		if (!attribute.getPredicate().equals(gov.nih.nci.cagrid.cqlquery.Predicate.EQUAL_TO))
			throw new MalformedQueryException("CQL2FS: only attribute with \"EQUAL_TO\" predicate is supported in CQL for filesystem navigation");
		if (attribute.getValue() == null || attribute.getValue().equals(""))
			throw new MalformedQueryException("CQL2FS: attribute should have some value");

		return attribute.getValue();
	}

	// convert the cql group statement to paths.  this is semi easy.  remember that the stuff here is relative,
	public static String cqlGroupToPath(gov.nih.nci.cagrid.cqlquery.Group group) throws MalformedQueryException {
		throw new MalformedQueryException("CQL2FS: group is not supported in CQL for filesystem navigation");
	}
		
	
	//	 convert the cql association statement to path.
	public static String cqlAssociationToPath(gov.nih.nci.cagrid.cqlquery.Association association) throws MalformedQueryException {
		throw new MalformedQueryException("CQL2FS: association is not supported in CQL for filesystem navigation");
	}

	// very specific to the CAD Result Data service.  does not support multiple levels
	public static String cqlToPath(CQLQuery query) throws MalformedQueryException {		
		if (query == null)
			throw new MalformedQueryException("CQL2FS: query is null");
		// first get the target
		gov.nih.nci.cagrid.cqlquery.Object target = query.getTarget();
		String name = target.getName();
		if (name == null || name.equals(""))
			throw new MalformedQueryException("CQL2FS: Target has no name");
		gov.nih.nci.cagrid.cqlquery.Attribute attr = target.getAttribute();
		if (attr != null)
			return CQL2FS.cqlAttributeToPath(attr);
		else
			throw new MalformedQueryException("CQL2FS: no attribute defined");
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		// testing with some basic cqls.
		try {

			String filename;
			String path = "C:\\Data\\src\\middleware\\middleware\\resources";
			filename = path + File.separator + "genericImageFSCQL1.xml";

			InputSource queryInput = new InputSource(new FileReader(filename));
			CQLQuery query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);

			FileInputStream fis = new FileInputStream(filename);
			byte[] cqlString = new byte[fis.available()];
			fis.read(cqlString);
			System.out.println("cqlquery =\n" + new String(cqlString));

			String path2 = CQL2FS.cqlToPath(query);
			System.out.println("Path =\n" + path2);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DeserializationException e) {
			e.printStackTrace();
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
