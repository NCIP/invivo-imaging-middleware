/**
 *
 */
package gov.nih.nci.ivi.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.axis.types.URI;
import org.globus.ftp.DataChannelAuthentication;
import org.globus.ftp.DataSink;
import org.globus.ftp.DataSinkStream;
import org.globus.ftp.DataSource;
import org.globus.ftp.DataSourceStream;
import org.globus.ftp.GridFTPClient;
import org.globus.ftp.GridFTPSession;
import org.globus.ftp.HostPort;
import org.globus.ftp.HostPortList;
import org.globus.ftp.RetrieveOptions;
import org.globus.ftp.Session;
import org.globus.ftp.exception.ClientException;
import org.globus.ftp.exception.ServerException;
import org.ietf.jgss.GSSCredential;
import org.ietf.jgss.GSSException;


/**
 * this class establishes a connection with the remote gridFTP server and performs
 * upload and retrieve operations.
 *
 * The class is used by grid service client for third party transport.  As such, it
 * needs to deal with different security configurations.  The goal is to match the
 * security configuration of the gridFTP channel to that of the grid service.
 *
 * several use cases are possible:
 * 		user credential present
 * 		user credential not present, default credential present
 * 		neither credentials present
 *
 * for each of these cases, service may have either anonymous allowed or anonymous not allowed.
 * 		in the case of anonymous allowed, the service gets "anonymous"
 * 		in the case of anonymous not allowed, the service needs real credential
 *
 * the channel encryption and signature follow from the rest of these configurations
 *
 *
 * Grid Service Client can connect to secure and unsecure services.
 * 	For secure services, the service is typically in a https container, and may or may
 * 		not allow anonymous connection.  when service allows anonymous connection, only anonymous
 * 		credential is sent across by service client.
 * 			here we can either disallow anonymous connection, or
 * 				send credential on GridFTP and have GridFTP auth handle this.
 * 			either way, SEND GRID CREDENTIAL ALWAYS...
 * 	For unsecure services, only anonymous connection is present.
 * 		connect via anonymous.
 *
 * 	Q. how to determine 1. if grid service is secure,
 * 		this is done via "CommunicationMechanism.getNone()" - is this really unsecure?
 * 	Q. which to use? ftp anonymous or GSSCredential anonymous?
 * 		GSSCredential when initialized wiht null is anonymous...
 *
 *
 * @author tpan
 *
 */

public class GridFTPHelper {

	private static boolean PARALLEL = false;
	private boolean connected = false;
	private GridFTPClient client = null;



	public GridFTPHelper() {
		connected = false;
		client = null;

	}


	/**
	 * open a new GridFTPFetcher connection.  The user parameter should be a GSSCredential
	 * It is the responsibility of the user of this class to supply either default credential
	 * or the client credential, or anonymous credential, in the form of GSSCredential.
	 *
	 * GridFTPFetcher
	 *
	 * @param url
	 * @param user
	 * @param encrypt
	 * @param signature
	 * @throws IOException
	 * @throws ServerException
	 */
	public void connect(URI url, GSSCredential user, boolean secure, boolean encrypt, boolean signature)

		throws IOException, ServerException {

		if (url == null) throw new IOException("ERROR: url should not be null");

        String host = url.getHost();
        int port = url.getPort();

        System.out.println("In fetch the host is: " + host + " and the port is " + port + " and user is " + user);

        if (host== null || host.equals("")) {
			throw new IOException("ERROR: host should not be null or empty");
        }

        //      hit server started with
        // $ globus-gridftp-server -aa -p 3253 -d ALL -chdir
        // -chdir-to / -auth-level 0
        client= new GridFTPClient(host, port);

        configureSecurity(user, secure, encrypt, signature);
        configureSession();

        connected = true;
	}


	public void disconnect() throws IOException, ServerException {
		if (connected == true){
			client.close();
			client = null;
			connected = false;
        }
	}

	/**
	 *
	 * configure the client security
	 * @param user
	 * @param encrypt
	 * @param signature
	 * @throws IOException
	 * @throws ServerException
	 */
	protected void configureSecurity(GSSCredential user, boolean secure, boolean encrypt, boolean signature) throws IOException, ServerException {

		// authenticate the user.
		// TODO: verify that this works for unsecured
		authenticate(user);

		// set protection buffer
		client.setProtectionBufferSize(16384);

		if (secure) {
			configureSecureTransport(encrypt, signature);
		} else {
			configureOpenTransport();
		}
	}


	/**
	 * authenticate with supplied user credential.
	 * the credential can be: 1. real credential
	 * 	2. anonymous GSS credential
	 *  3. null.  this maps to default credential.  If there is none, then what?
	 *
	 * @param client
	 * @param user.   null means use default credential.
	 *
	 * @param authenticate
	 * @return
	 * @throws IOException
	 */
	protected boolean authenticate(GSSCredential user) throws IOException {

		try {
			//System.out.println("gridftp fetching with identity authorization:  my subject is " + user);

			// to authneticate the remote server
			//client.setAuthorization(new IdentityAuthorization(GlobusCredential.getDefaultCredential().getIdentity()));

			// if set to null, uses default credential
			System.out.println("gridftp fetching with identity authorization:  my subject is " + user.getName().toString());
			client.authenticate(user); // cog 1.1
			return true;
		} catch (ServerException e) {
			System.out.println("WARNING: unable to authorize connection securely.  trying unsecured connection");
		} catch (GSSException e) {
			System.out.println("WARNING: unable to get user.  trying unsecured connection");
		}
		return false;
	}



	protected void configureOpenTransport() throws IOException, ServerException {
		client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
        // confidential is not supported.  only clear can be used here.
		client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
	}

	protected void configureSecureTransport(boolean encrypt, boolean signature) throws IOException, ServerException {
		// set the datachannel authentication mechanisms
		client.setDataChannelAuthentication(DataChannelAuthentication.SELF);

		// set the datachannel encryption mechanisms
		if (encrypt && signature) {
            client.setDataChannelProtection(GridFTPSession.PROTECTION_PRIVATE);
        } else if (signature) {
        	client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
        } else {
        	client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
        }
	}




	protected void configureSession() throws IOException, ServerException {
		// set data type settings
		client.setType(Session.TYPE_IMAGE);

		// set the paralle mode stuff
        if (PARALLEL) {
            client.setMode(GridFTPSession.MODE_EBLOCK);
            int parallelism = 2;
            client.setOptions(new RetrieveOptions(parallelism));
            HostPortList hpl = client.setLocalStripedPassive();
            client.setStripedActive(hpl);
        } else {
            client.setMode(Session.MODE_STREAM);
        }
	}



	public void secureFetch(URI url, String fullLocalFile) throws IOException
     {

        File localFile = new File(fullLocalFile);

        // completed setting up gridFTP connection.  now download
        if (client != null) {
            String fullRemoteFile = url.getPath();

	        if (localFile.exists()) {
	            localFile.delete();
	        }

	        try {
		        DataSink sink = null;
		        if (!PARALLEL) {
		            HostPort hp2 = client.setPassive();
		            client.setLocalActive();
		            FileOutputStream fos = new FileOutputStream(localFile);
		            sink = new DataSinkStream(fos);
		            client.get(fullRemoteFile, sink, null);
		            fos.close();
		        }
		        else {
		            //			sink = new FileRandomIO(new RandomAccessFile(fullLocalFile,"rw"));
		            //			long size = client.getSize(fullRemoteFile);
		            //			client.extendedGet(fullRemoteFile,
		            //					size,
		            //					sink,
		            //					null);
		        }
			} catch (ServerException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			} catch (ClientException e) {
				e.printStackTrace();
				throw new IOException(e.getMessage());
			}
        }
        // client connection is saved.  client.close();
	}


	/**
	 * upload a file using the established connection
	 *
	 * @param url
	 * @param fullLocalFile
	 * @throws IOException
	 */
	public void secureUpload(URI url, String fullLocalFile) throws IOException
     {
        File localFile = new File(fullLocalFile);

        // completed setting up gridFTP connection.  now download
        if (client != null) {
            String fullRemoteFile = url.getPath();

	        try {
	            DataSource source = null;
	            if (!PARALLEL) {
	                HostPort hp2 = client.setPassive();
	                client.setLocalActive();
	                FileInputStream fis = new FileInputStream(localFile);
	                source = new DataSourceStream(fis);
	                client.put(fullRemoteFile, source, null);
	                fis.close();
	            }
	            else {
	                //			source = new FileRandomIO(new RandomAccessFile(fullLocalFile,"rw"));
	                //			long size = client.getSize(fullRemoteFile);
	                //			client.extendedPut(fullRemoteFile,
	                //					size,
	                //					source,
	                //					null);
	            }
			} catch (ServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        }
        // client connection is saved.  client.close();
	}

}
