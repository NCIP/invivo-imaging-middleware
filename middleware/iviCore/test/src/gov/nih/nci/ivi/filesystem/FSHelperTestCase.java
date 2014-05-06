package gov.nih.nci.ivi.filesystem;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import junit.framework.TestCase;

public class FSHelperTestCase extends TestCase {

	FSHelper helper = null;

	public FSHelperTestCase(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		helper = formFSHelper();
	}

	protected static FSHelper formFSHelper() {
		Properties params = FSHelper.getParameters();
		params.put(FSHelper.ROOT_DIR, "/tmp/GIdata");
		FSHelper helper = null;
		try {
			helper = new FSHelper(params);
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		return helper;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public final void testFSHelperPropertiesWithEmptyRootDir() {
		Properties params = FSHelper.getParameters();
		params.put(FSHelper.ROOT_DIR, "");
		FSHelper helper = null;
		try {
			helper = new FSHelper(params);
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testFSHelperPropertiesWithNullParameters() {
		FSHelper helper = null;
		try {
			helper = new FSHelper((Properties)null);
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testFSHelperStringWithEmptyRootDir() {
		FSHelper helper = null;
		try {
			helper = new FSHelper("");
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testFSHelperStringWithNullString() {
		FSHelper helper = null;
		try {
			helper = new FSHelper((String)null);
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathWithEmptyFilePattern() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("");
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathWithNullFilePattern() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath(null);
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathWithNonexistingFilePattern() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("#$%EFgerg#$53r23/234r");
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathSecurityHole() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("../usr");
			fail("Should have thrown exception.");
		} catch (IOException e) {
			System.out.println("Successfully caught: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathNoProblem() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("heberedfs/../csdfsdc/..");
			assertEquals("Number of files is not zero.", 0, fullPaths.length);
			System.out.println("Successfully passed the test.");
		} catch (IOException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathNoProblem2() {
		try {
			File path = new File(helper.getRootDir() + File.separator + "heberedfs");
			path.mkdirs();
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("heberedfs/test.jpg");
			path.delete();
			assertEquals("Number of files is not one.", 0, fullPaths.length);
			System.out.println("Successfully passed the test.");
		} catch (IOException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathNoProblem3() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("*");
			assertEquals("Number of files is not zero.", 0, fullPaths.length);
			System.out.println("Successfully passed the test.");
		} catch (IOException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testQueryFSByPathNoProblem4() {
		try {
			String [] fullPaths;
			fullPaths = helper.queryFSByPath("*dfgdfg/*");
			System.out.println("Successfully passed the test.");
		} catch (IOException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

}
