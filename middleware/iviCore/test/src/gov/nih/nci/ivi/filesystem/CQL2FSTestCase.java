package gov.nih.nci.ivi.filesystem;

import gov.nih.nci.cagrid.cqlquery.Attribute;
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

import junit.framework.TestCase;

public class CQL2FSTestCase extends TestCase {

	final static String PATH = "test/resources/filesystem";
	final static int QUERY_COUNT = 6;
	CQLQuery queries[] = new CQLQuery[QUERY_COUNT];

	public CQL2FSTestCase(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		for (int queryId = 0; queryId < QUERY_COUNT; queryId++) {
			String filename = PATH + File.separator + "genericImageFSCQL" + (queryId+1) + ".xml";
			queries[queryId] = formCQLQuery(filename);
		}
	}

	protected static CQLQuery formCQLQuery(String filename) {
		CQLQuery query = null;
		try {
			FileReader inputFile = new FileReader(filename);
			assertNotNull(inputFile);
			InputSource queryInput = new InputSource(inputFile);
			assertNotNull(queryInput);
			query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
			assertNotNull(query);
			FileInputStream fis = new FileInputStream(filename);
			assertNotNull(fis);
			assertTrue(fis.available() > 0);
			byte[] cqlString = new byte[fis.available()];
			assertNotNull(cqlString);
			fis.read(cqlString);
			assertTrue(cqlString.length > 0);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DeserializationException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return query;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testCqlAttributeToPathWithQuery0() {
		int queryId = 0;
		String path;
		try {
			gov.nih.nci.cagrid.cqlquery.Object target = queries[queryId].getTarget();
			Attribute attribute = target.getAttribute();
			path = CQL2FS.cqlAttributeToPath(attribute);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testCqlAttributeToPathWithQuery1() {
		int queryId = 1;
		String path;
		try {
			gov.nih.nci.cagrid.cqlquery.Object target = queries[queryId].getTarget();
			Attribute attribute = target.getAttribute();
			path = CQL2FS.cqlAttributeToPath(attribute);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlAttributeToPathWithQuery2() {
		int queryId = 2;
		String path;
		try {
			gov.nih.nci.cagrid.cqlquery.Object target = queries[queryId].getTarget();
			Attribute attribute = target.getAttribute();
			path = CQL2FS.cqlAttributeToPath(attribute);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testCqlAttributeToPathWithQuery3() {
		int queryId = 3;
		String path;
		try {
			gov.nih.nci.cagrid.cqlquery.Object target = queries[queryId].getTarget();
			Attribute attribute = target.getAttribute();
			path = CQL2FS.cqlAttributeToPath(attribute);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlAttributeToPathWithQuery4() {
		int queryId = 4;
		String path;
		try {
			gov.nih.nci.cagrid.cqlquery.Object target = queries[queryId].getTarget();
			Attribute attribute = target.getAttribute();
			path = CQL2FS.cqlAttributeToPath(attribute);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testCqlAttributeToPathWithQuery5() {
		int queryId = 5;
		String path;
		try {
			gov.nih.nci.cagrid.cqlquery.Object target = queries[queryId].getTarget();
			Attribute attribute = target.getAttribute();
			path = CQL2FS.cqlAttributeToPath(attribute);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlAttributeToPathWithNullQuery() {
		String path = null;
		try {
			path = CQL2FS.cqlAttributeToPath(null);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlGroupToPath() {
		try {
			CQL2FS.cqlGroupToPath(null);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlAssociationToPath() {
		try {
			CQL2FS.cqlAssociationToPath(null);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithQuery0() {
		int queryId = 0;
		String path;
		try {
			path = CQL2FS.cqlToPath(queries[queryId]);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithQuery1() {
		int queryId = 1;
		String path;
		try {
			path = CQL2FS.cqlToPath(queries[queryId]);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithQuery2() {
		int queryId = 2;
		String path;
		try {
			path = CQL2FS.cqlToPath(queries[queryId]);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithQuery3() {
		int queryId = 3;
		String path;
		try {
			path = CQL2FS.cqlToPath(queries[queryId]);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithQuery4() {
		int queryId = 4;
		String path;
		try {
			path = CQL2FS.cqlToPath(queries[queryId]);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithQuery5() {
		int queryId = 5;
		String path;
		try {
			path = CQL2FS.cqlToPath(queries[queryId]);
			assertNotNull("path returned is null (query #" + queryId + ")", path);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testCqlToPathWithNullQuery() {
		String path;
		try {
			path = CQL2FS.cqlToPath(null);
			fail("Should have thrown exception.");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}
}
