package gov.nih.nci.ivi.dicomdataservice.client;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import gov.nih.nci.cagrid.authentication.bean.BasicAuthenticationCredential;
import gov.nih.nci.cagrid.authentication.bean.Credential;
import gov.nih.nci.cagrid.authentication.client.AuthenticationClient;
import gov.nih.nci.cagrid.dorian.client.IFSUserClient;
import gov.nih.nci.cagrid.dorian.ifs.bean.ProxyLifetime;
import gov.nih.nci.cagrid.opensaml.SAMLAssertion;
import gov.nih.nci.ivi.utils.Zipper;
import junit.framework.TestCase;

import org.globus.gsi.GlobusCredential;

public abstract class DICOMDataServiceAbstractSecureTest extends TestCase {
    // Hard wired URLs that should be obtained by programmatic retrieval
    private static final String IDP_URL = "https://dorian.cvrgrid.cci.emory.edu:8443/wsrf/services/cagrid/Dorian";
    private static final String IFS_URL = "https://dorian.cvrgrid.cci.emory.edu:8443/wsrf/services/cagrid/Dorian";
    
//    protected final String serviceUrl = "https://itd161.cc.emory.edu:8443/wsrf/services/cagrid/DICOMDataService";
    protected final String serviceUrl = "http://imgtest1.cci.emory.edu:8080/wsrf/services/cagrid/DICOMDataService";
    protected String localUploadLocation = System.getProperty("java.io.tmpdir") + File.separator + "DicomDataServiceClientUpload";
    
    protected static final String SERIES_A1 = "1.3.6.1.4.1.9328.50.1.144";
    protected static final String SERIES_A2 = "1.3.6.1.4.1.9328.50.1.217";
    protected static final String SERIES_A3 = "1.3.6.1.4.1.9328.50.1.7";
    protected static final String SERIES_B1 = "1.3.6.1.4.1.9328.50.1.2359";
    protected static final String SERIES_B2 = "1.3.6.1.4.1.9328.50.1.2439";
    protected static final String SERIES_B3 = "1.3.6.1.4.1.9328.50.1.2511";
    
    private static final String[] PATIENT_A_SERIES_ARRAY = {SERIES_A1, SERIES_A2, SERIES_A3};
    private static final String[] PATIENT_B_SERIES_ARRAY = {SERIES_B1, SERIES_B2, SERIES_B3};
    
    protected static final String PATIENT_A_SERIES_1 = "test/resources/DICOMData/13614193285010001/1.3.6.1.4.1.9328.50.1.139/1.3.6.1.4.1.9328.50.1.144/1.3.6.1.4.1.9328.50.1.143.dcm";
    protected static final String PATIENT_A_SERIES_2 = "test/resources/DICOMData/13614193285010001/1.3.6.1.4.1.9328.50.1.139/1.3.6.1.4.1.9328.50.1.217/1.3.6.1.4.1.9328.50.1.216.dcm";
    protected static final String PATIENT_A_SERIES_3 = "test/resources/DICOMData/13614193285010001/1.3.6.1.4.1.9328.50.1.2/1.3.6.1.4.1.9328.50.1.7/1.3.6.1.4.1.9328.50.1.8.dcm";
    protected static final String PATIENT_B_SERIES_1 = "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2359/1.3.6.1.4.1.9328.50.1.2364.dcm";
    protected static final String PATIENT_B_SERIES_2 = "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2354/1.3.6.1.4.1.9328.50.1.2439/1.3.6.1.4.1.9328.50.1.2438.dcm";
    protected static final String PATIENT_B_SERIES_3 = "test/resources/DICOMData/13614193285010004/1.3.6.1.4.1.9328.50.1.2506/1.3.6.1.4.1.9328.50.1.2511/1.3.6.1.4.1.9328.50.1.2524.dcm";

    private static GlobusCredential bogus1Credential;
    private static GlobusCredential bogus2Credential;
    private static GlobusCredential bogus3Credential;

    /**
     * Return a credential for the specified user name and password
     * 
     * @param idpURL
     * @param ifsURL
     * @param userId
     *            The user id to get the credential for.
     * @param passwd
     *            The password to authenticate the user id.
     * @throws Exception
     *             if the credential could not be obtained.
     */
    private static GlobusCredential login(String idpURL, String ifsURL, String userId, String passwd) throws Exception {
        Credential credential = new Credential();
        BasicAuthenticationCredential cred = new BasicAuthenticationCredential();
        cred.setUserId(userId);
        cred.setPassword(passwd);
        credential.setBasicAuthenticationCredential(cred);

        System.out.println("Creating authentication client: URL="+idpURL+"; userId=" + userId + "; password="+passwd);
        AuthenticationClient client = new AuthenticationClient(idpURL, credential);
        System.out.println("Authenticating");
        SAMLAssertion saml = client.authenticate();
        System.out.println("Authenticated");

        // Requested Grid Credential lifetime (12 hours)
        ProxyLifetime lifetime = new ProxyLifetime();
        lifetime.setHours(12);

        int delegationLifetime = 0;
//        System.out.println(saml.toString());
        System.out.println("Creating IFSUSerClient");
        IFSUserClient dorian = new IFSUserClient(ifsURL);
        System.out.println("Creating proxy");
        GlobusCredential proxy = dorian.createProxy(saml, lifetime, delegationLifetime); 
        System.out.println("Got proxy");
        return proxy;
    }

    public DICOMDataServiceAbstractSecureTest() {
        super();
    }

    public DICOMDataServiceAbstractSecureTest(String name) {
        super(name);
    }

    /**
     * Return the Credential for bogus1, logging in if necessary.
     * 
     * @throws Exception
     *             if there is a problem
     */
    protected static GlobusCredential getBogus1Credential() throws Exception {
        if (bogus1Credential == null) {
            System.out.println("Logging in bogus1");
            bogus1Credential = login(IDP_URL, IFS_URL, "bogus1", "pswd123456X$");
        }
        return bogus1Credential;
    }

    /**
     * Return the Credential for bogus2, logging in if necessary.
     * 
     * @throws Exception
     *             if there is a problem
     */
    protected static GlobusCredential getBogus2Credential() throws Exception {
        if (bogus2Credential == null) {
            System.out.println("Logging in bogus2");
            bogus2Credential = login(IDP_URL, IFS_URL, "bogus2", "pswd123456X$");
        }
        return bogus2Credential;
    }

    /**
     * Return the Credential for bogus3, logging in if necessary.
     * 
     * @throws Exception
     * @throws Exception
     *             if there is a problem
     */
    protected static GlobusCredential getBogus3Credential() throws Exception {
        if (bogus3Credential == null) {
            System.out.println("Logging in bogus3");
            bogus3Credential = login(IDP_URL, IFS_URL, "bogus3", "pswd123456X$");
        }
        return bogus3Credential;
    }

    /**
     * Return the subject represented by a credential.
     * @param cred the Credential
     */
    protected String getSubject(GlobusCredential cred) {
        return (cred==null) ? "<anonymous>" : cred.getSubject();
    }

    /**
     * Return a set of the Series IDs that are associated with patient A 
     */
    protected Set<String> getPatientASeriesSet() {
       return new HashSet<String>(Arrays.asList(PATIENT_A_SERIES_ARRAY)); 
    }

    /**
     * Return a set of the Series IDs that are associated with patient B
     */
    protected Set<String> getPatientBSeriesSet() {
       return new HashSet<String>(Arrays.asList(PATIENT_B_SERIES_ARRAY)); 
    }

    /**
     * Return true if the URL begins with https:
     * 
     * @param url
     *            the URL to check
     */
    protected boolean isSecure(String url) {
        return url.startsWith("https:");
    }
    
    protected void enterTest(String name) {
        System.out.print("\n\n Entering ");
        System.out.println(name);
    }
    
    protected void exitTest(String name) {
        System.out.print("Exiting ");
        System.out.println(name);
        System.out.println();
    }

    /**
     * Return true if the URL begins with http:
     * 
     * @param url
     *            the URL to check
     */
    protected boolean isUnsecure(String url) {
        return url.toLowerCase().startsWith("http:");
    }

    /**
     * @param sampleFileNames
     * @param localFileName
     * @throws FileNotFoundException
     * @throws IOException
     */
    protected void zip(String[] sampleFileNames, String localFileName) throws FileNotFoundException, IOException {
        System.out.println(".Zip file will contain these files:");
        for (String fileName : sampleFileNames) {
            System.out.println("    " + fileName);
        }
        Zipper.zip(localFileName, sampleFileNames, false);
    }
}