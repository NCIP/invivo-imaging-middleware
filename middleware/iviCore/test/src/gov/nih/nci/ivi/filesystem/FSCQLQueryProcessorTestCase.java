package gov.nih.nci.ivi.filesystem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.utils.IOUtils;
import junit.framework.TestCase;

public class FSCQLQueryProcessorTestCase extends TestCase {

	final static String PATH = "test/resources/filesystem";
	final static String PATH_IMAGES = "test/resources/images";
	final static int QUERY_COUNT = 7;
	final static int TEST_IMAGE_COUNT = 3;
	CQLQuery queries[] = new CQLQuery[QUERY_COUNT];
	String testImages[] = new String[] {"mail.jpg", "empty.jpg", "mailDir" + File.separator + "mailDir.jpg"};
	FSHelper helper = null;
	Properties params;

	public FSCQLQueryProcessorTestCase(String arg0) {
		super(arg0);
	}

	protected void setUp() throws Exception {
		super.setUp();
		params = FSHelper.getParameters();
		params.put(FSHelper.ROOT_DIR, "/tmp/GIdata");
		helper = FSHelperTestCase.formFSHelper();
		helper.getRootDir().mkdirs();
		if (!helper.getRootDir().exists() || !helper.getRootDir().canRead())
			fail("Cannot create the root directory.");
		for (int queryId = 0; queryId < QUERY_COUNT; queryId++) {
			String filename = PATH + File.separator + "genericImageFSCQL" + (queryId+1) + ".xml";
			queries[queryId] = CQL2FSTestCase.formCQLQuery(filename);
		}
		for (int testImageId = 0; testImageId < TEST_IMAGE_COUNT; testImageId++)
			copyTestImageFile(testImages[testImageId]);
	}

	protected void copyTestImageFile(String testImage) {
		byte []imageContent = null;
		try {
			FileInputStream fis = new FileInputStream(PATH_IMAGES + File.separator + testImage);
			imageContent = new byte[fis.available()];
			fis.read(imageContent);
			fis.close();
		} catch (FileNotFoundException e) {
			fail("file not found " + e);
		} catch (IOException e) {
			fail("can't read image " + e);
		}
		assertNotNull(imageContent);
		FileOutputStream fos;
		try {
			File outFile = new File(helper.getRootDir() + File.separator + testImage);
			outFile.getParentFile().mkdirs();
			fos = new FileOutputStream(outFile);
			fos.write(imageContent);
			fos.close();
		} catch (FileNotFoundException e) {
			fail("file not found " + e);
		} catch (IOException e2) {
			fail("can't write image " + e2);
		}
	}

	protected void tearDown() throws Exception {
		super.tearDown();
		assertTrue(IOUtils.deleteDirectory(helper.getRootDir().getAbsolutePath()));
	}

	public final void testProcessQueryCQLQuery0NonexistingFile() {
		int queryId = 0;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			assertNotNull("Query results is null", results);
			assertNotNull("Query results.getObjectResult() is null", results.getObjectResult());
			for (int i = 0; i < results.getObjectResult().length; i++)
				assertNotNull("Query results.getObjectResult() is null", results.getObjectResult(i).get_any()[0]);
			CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results);
			while (iter.hasNext()) {
				Object tile = iter.next();
			}
			assertEquals(0, results.getObjectResult().length);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testProcessQueryCQLQuery1EmptyQuery() {
		int queryId = 1;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");			
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			assertNotNull("Query results is null", results);
			assertNotNull("Query results.getObjectResult() is null", results.getObjectResult());
			for (int i = 0; i < results.getObjectResult().length; i++ )
				assertNotNull("Query results.getObjectResult() is null", results.getObjectResult(i).get_any()[0]);
			CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results);
			while (iter.hasNext()) {
				Object tile = iter.next();
			}
			fail("Should have thrown exception");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testProcessQueryCQLQuery2SelectAllFiles() {
		int queryId = 2;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");			
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			assertNotNull("Query results is null", results);
			assertNotNull("Query results.getObjectResult() is null", results.getObjectResult());
			for (int i = 0; i < results.getObjectResult().length; i++ )
				assertNotNull("Query results.getObjectResult() is null", results.getObjectResult(i).get_any()[0]);
			CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results);
			while (iter.hasNext()) {
				Object tile = iter.next();
				assertNotNull(tile);
			}
			assertEquals(1, results.getObjectResult().length);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			e.printStackTrace();
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testProcessQueryCQLQuery3InvalidPredicate() {
		int queryId = 3;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			fail("Should have thrown exception");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testProcessQueryCQLQuery4SelectAllManyStars() {
		int queryId = 4;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");			
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			assertNotNull("Query results is null", results);
			assertNotNull("Query results.getObjectResult() is null", results.getObjectResult());
			for (int i = 0; i < results.getObjectResult().length; i++ )
				assertNotNull("Query results.getObjectResult() is null", results.getObjectResult(i).get_any()[0]);
			assertEquals(1, results.getObjectResult().length);
			CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results);
			while (iter.hasNext()) {
				Object tile = iter.next();
				assertNotNull(tile);
			}
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			e.printStackTrace();
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			e.printStackTrace();
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}

	public final void testProcessQueryCQLQuery5InvalidAttributeName() {
		int queryId = 5;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");			
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			fail("Should have thrown exception");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

	public final void testProcessQueryCQLQuery6EmptyFile() {
		int queryId = 6;
		System.out.println("QUERY: " + queryId);
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");
		}
		try {
			CQLQueryResults results = processor.processQuery(queries[queryId]);
			assertNotNull("Query results is null", results);
			assertNotNull("Query results.getObjectResult() is null", results.getObjectResult());
			assertEquals(0, results.getObjectResult().length);
			System.out.println("Successfully passed the test.");
		} catch (MalformedQueryException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			fail("Should not have thrown exception: " + e.getMessage());
		}
	}
	
	public final void testProcessQueryCQLQueryWithNullQuery() {
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			fail("Query processor initialization failed");			
		}
		try {
			CQLQueryResults results = processor.processQuery(null);
			fail("Should have thrown an exception");
		} catch (MalformedQueryException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		} catch (QueryProcessingException e) {
			System.out.println("Successfully caught exception: " + e.getMessage());
		}
	}

}
