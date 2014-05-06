package gov.nih.nci.ivi.helper;


import junit.framework.TestCase;

/**
 * @author ashish
 *
 */
public class ImageDataServiceHelperTestCase extends TestCase {

	/**
	 * @param name
	 */
	public ImageDataServiceHelperTestCase(String name) {
		super(name);
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#setUp()
	 */
	protected void setUp() throws Exception {
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see junit.framework.TestCase#tearDown()
	 */
	protected void tearDown() throws Exception {
		super.tearDown();
	}

	//
	// Test method for {@link gov.nih.nci.ivi.helper.ImageDataServiceHelper#ImageDataServiceHelper()}.
	//
	public void testImageDataServiceHelper() {
		ImageDataServiceHelper helper = new ImageDataServiceHelper();
		if(helper == null)
			fail("Error in ImageDataServiceHelper constructor");
	}
/*
	//
	// Test method for {@link gov.nih.nci.ivi.helper.ImageDataServiceHelper#retrieveImageData(gov.nih.nci.cagrid.cqlquery.CQLQuery, java.lang.String, java.lang.String)}.
	//
	public void testRetrieveImageData() {
		fail("Not yet implemented"); // TODO
	}

	//
	/ Test method for {@link gov.nih.nci.ivi.helper.ImageDataServiceHelper#submitImageData(java.lang.String[], java.lang.String[])}.
	//
	public void testSubmitImageDataStringArrayStringArray() {
		fail("Not yet implemented"); // TODO
	}

	//
	// Test method for {@link gov.nih.nci.ivi.helper.ImageDataServiceHelper#submitImageData(java.io.File, java.lang.String[])}.
	//
	public void testSubmitImageDataFileStringArray() {
		fail("Not yet implemented"); // TODO
	}
*/
}
