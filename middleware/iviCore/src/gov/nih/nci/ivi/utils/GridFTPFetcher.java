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
import org.globus.gsi.GlobusCredential;
import org.globus.gsi.GlobusCredentialException;
import org.globus.gsi.gssapi.auth.IdentityAuthorization;

public class GridFTPFetcher {

	private String lastHost;
	private int lastPort;
	private String lastUser;
	private GridFTPClient client;
	private static boolean PARALLEL = false;
	
	public GridFTPFetcher()
	{
		client = null;
		lastHost = null;
		lastPort = -1;
	}

	@Deprecated
	public void fetch(String url, String fullLocalFile, boolean nosec)
    throws IOException {
    try {
        boolean parallel = false;
        String rest = url.split("gsiftp://")[1];
        String host = rest.split(":")[0];
        rest = rest.substring(host.length()+1);
        String port_s = rest.split("/")[0];
        int port = Integer.parseInt(port_s);
        String fullRemoteFile = rest.substring(port_s.length());
	
        if (lastHost==null || !lastHost.equals(host)||lastPort!=port) {
            lastHost = host;
            lastPort = port;
            // hit server started with
            // $ globus-gridftp-server -aa -p 3253 -d ALL -chdir
            // -chdir-to / -auth-level 0
            if (client!=null){
                client.close();
            }
            System.out.println("In fetch the host is: " + host + " and the port is " + port);
            client= new GridFTPClient(host, port);
            if (nosec) {
                client.authorize("ftp","");
            }
            else {
                System.out.println("gridftp fetching with security:  my subject is " + GlobusCredential.getDefaultCredential().getIdentity());
                client.setAuthorization(new IdentityAuthorization(GlobusCredential.getDefaultCredential().getIdentity()));
//                client.setAuthorization(new IdentityAuthorization("/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid04/OU=IdP [1]/CN=rutttest"));
                client.authenticate(null); // cog 1.1
            }
            client.setProtectionBufferSize(16384);
            client.setType(Session.TYPE_IMAGE);
            if (!parallel) {
                client.setMode(Session.MODE_STREAM);
            }
            else {   
                client.setMode(GridFTPSession.MODE_EBLOCK);
            }

            if (nosec) {
                client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
                client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
            }
            else {
                client.setDataChannelAuthentication(DataChannelAuthentication.SELF);
                client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
            }
		
            if (!parallel) {
                ;
            }
            else {
                int parallelism = 2;
                client.setOptions(new RetrieveOptions(parallelism));
                HostPortList hpl = null;
                hpl = client.setLocalStripedPassive(); 
                client.setStripedActive(hpl);
            }
        }
        if (new File(fullLocalFile).exists()) {
            new File(fullLocalFile).delete();
        }
        DataSink sink = null;
        if (!parallel) {
            HostPort hp2 = client.setPassive();
            client.setLocalActive();
            sink = new DataSinkStream(new FileOutputStream(fullLocalFile));
            client.get(fullRemoteFile,
                       sink,
                       null);
        }
        else {
            //			sink = new FileRandomIO(new RandomAccessFile(fullLocalFile,"rw"));
            //			long size = client.getSize(fullRemoteFile);
            //			client.extendedGet(fullRemoteFile,
            //					size,
            //					sink,
            //					null);
        }
        // client.close();
    }
    catch (Exception e) {
        e.printStackTrace();
        throw new IOException("fetch");
    }
}

	
	protected void disconnect() throws IOException {
		if (client!=null){
            try {
				client.close();
			} catch (ServerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			client = null;
        }
	}

	/**
	 * 
	 * @param client
	 * @param user.   null means use default credential.  only allows null when authenticate is true
	 * @param authenticate
	 * @return
	 * @throws IOException
	 */
	protected boolean authenticate(String user, boolean authenticate) throws IOException {
		if (authenticate) {
			try {
				//System.out.println("gridftp fetching with identity authorization:  my subject is " + user);
				
				// to authneticate the remote server
				//client.setAuthorization(new IdentityAuthorization(GlobusCredential.getDefaultCredential().getIdentity()));
            
				// if set to null, uses default credential
				if (user != ClientSecurityUtil.ANONYMOUS_USER) {
					System.out.println("gridftp fetching with identity authorization:  my subject is " + GlobusCredential.getDefaultCredential().getIdentity());
					client.authenticate(null); // cog 1.1
					return true;
				}
			} catch (GlobusCredentialException e2) {
				System.out.println("WARNING: invalid credential");
			} catch (ServerException e) {
				System.out.println("WARNING: unable to authorize connection securely.  trying unsecured connection");
			}
		} else {
            try {
//            	org.ietf.jgss.GSSCredential gss = new org.globus.gsi.gssapi.GlobusGSSCredentialImpl(
//						null, org.ietf.jgss.GSSCredential.INITIATE_AND_ACCEPT);
//            	client.authenticate(null);
            	// next try to see if we can connect unsecured
            	client.authorize(ClientSecurityUtil.ANONYMOUS_USER, ClientSecurityUtil.ANONYMOUS_USER);
				return true;
            } catch (ServerException e1) {
				System.out.println("WARNING: unable to connect unsecurely.  ther server may not allow anonymous");
//            } catch (GSSException e) {
				// TODO Auto-generated catch block
//				e.printStackTrace();
			}			
		}
		return false;
	}
	
	protected void configureTransport( 
			boolean encrypt, boolean signature, boolean secureAuth) throws IOException {
		// set the datachannel authentication mechanisms
		try {
			if (secureAuth) {
				client.setDataChannelAuthentication(DataChannelAuthentication.SELF);
			} else  {
				client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
			}
		} catch (ServerException e) {
			System.err.println("ERROR: unable to set the data channel authentication");
		}

		// set the datachannel encryption mechanisms
		try {
			if (secureAuth) {
	            if (encrypt && signature) {
	                client.setDataChannelProtection(GridFTPSession.PROTECTION_PRIVATE);
	            } else if (signature) {
	            	client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
	            } else {
	            	client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
	            }
			} else  {
                // confidential is not supported.  only clear can be used here.
				client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
			}
		} catch (ServerException e) {
			System.err.println("ERROR: unable to set the data channel protection");
			e.printStackTrace();
		}
		
	}
	
	protected void configureSecurity(String user, 
			boolean encrypt, boolean signature) throws IOException {
		
		String host = client.getHost();
		int port = client.getPort();
		System.out.println("TCP user = " + user);
		boolean authenticate = (user != null && !user.equalsIgnoreCase(ClientSecurityUtil.ANONYMOUS_USER));
		System.out.println("authenticate = " + authenticate);
		/* if auth flag = true then authenticate
		 * 		if authenticate succeed then continue
		 * 		if 				fail then
		 * 			reopen connection
		 * 			authenticate with false flag
		 * 			if authenticate succeed then continue
		 * 			if              fail then error
		 * if auth flag = false then authenticate
		 * 		if authenticate succeed then continue
		 * 		if 			    fail then error
		 */
		boolean authResult = authenticate(user, authenticate); 
		if (!authResult) {
			if (authenticate) {
				System.out.println("DEBUG: failed authentication earlier.  connection is now closed.  try reopening it.");
	            try {
					client= new GridFTPClient(host, port);
				} catch (ServerException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if (!authenticate(user, false)) {
					// critical error, can't connect
					throw new IOException("ERROR: unable to connect to the gridFTP server securely or unsecurely");
				}
			} else {
				throw new IOException("ERROR: unable to connect to the gridFTP server unsecurely");
			}
		}
		
		// set protection buffer
		try {
			client.setProtectionBufferSize(16384);
		} catch (ServerException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		configureTransport(encrypt, signature, authenticate && authResult);
	}

	
	
	protected void configureSession() throws IOException {
		// set data type settings
        try {
            client.setType(Session.TYPE_IMAGE);
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// set the paralle mode stuff
        try {
            if (PARALLEL) {
                client.setMode(GridFTPSession.MODE_EBLOCK);
                int parallelism = 2;
                client.setOptions(new RetrieveOptions(parallelism));
                HostPortList hpl = client.setLocalStripedPassive(); 
                client.setStripedActive(hpl);
            } else {
                client.setMode(Session.MODE_STREAM);
            }
		} catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected GridFTPClient getGridFTPClient(URI url, String user, 
			boolean encrypt, boolean signature) throws IOException {

		
		if (url == null) {
			disconnect();
			return null;
		}
		
        String host = url.getHost();
        int port = url.getPort();

        System.out.println("In fetch the host is: " + host + " and the port is " + port + " and user is " + user);

        if (host== null || host.equals("")) {
        	disconnect();
        	return null;
        }        
        
        
        if (host.equalsIgnoreCase(lastHost) && port == lastPort && user.equals(lastUser)) {
        	return client;
        }

        // reset 
        disconnect();
        //      hit server started with
        // $ globus-gridftp-server -aa -p 3253 -d ALL -chdir
        // -chdir-to / -auth-level 0
        try {
			client= new GridFTPClient(host, port);
        } catch (ServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        configureSecurity(user, encrypt, signature);
        configureSession();
        
        // save for later
        lastHost = host;
        lastPort = port;
        lastUser = user;
        
        return client;
	}
	
	
	public void secureFetch(URI url, String fullLocalFile, String user, 
			boolean encrypt, boolean signature) throws IOException
     {
        
        client = getGridFTPClient(url, user, encrypt, signature);
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

	@Deprecated
	public void upload(String url, String fullLocalFile, boolean nosec)
        throws IOException {
        try {
            boolean parallel = false;
            String rest = url.split("gsiftp://")[1];
            String host = rest.split(":")[0];
            rest = rest.substring(host.length()+1);
            String port_s = rest.split("/")[0];
            int port = Integer.parseInt(port_s);
            String fullRemoteFile = rest.substring(port_s.length());
		
            if (lastHost==null || !lastHost.equals(host)||lastPort!=port) {
                lastHost = host;
                lastPort = port;
                // hit server started with
                // $ globus-gridftp-server -aa -p 3253 -d ALL -chdir
                // -chdir-to / -auth-level 0
                if (client!=null){
                    client.close();
                }
                System.out.println("In upload the host is: " + host + " and the port is " + port);
                client= new GridFTPClient(host, port);
                if (nosec) {
                    client.authorize("ftp","");
                }
                else {
                    System.out.println("gridftp fetching with security:  my subject is " + GlobusCredential.getDefaultCredential().getIdentity());
                    client.setAuthorization(new IdentityAuthorization(GlobusCredential.getDefaultCredential().getIdentity()));
                    client.setAuthorization(new IdentityAuthorization("/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid04/OU=IdP [1]/CN=rutttest"));
                    client.authenticate(null); // cog 1.1
                }
                client.setProtectionBufferSize(16384);
                client.setType(Session.TYPE_IMAGE);
                if (!parallel) {
                    client.setMode(Session.MODE_STREAM);
                }
                else {   
                    client.setMode(GridFTPSession.MODE_EBLOCK);
                }

                if (nosec) {
                    client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
                    client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);
                }
                else {
                    client.setDataChannelAuthentication(DataChannelAuthentication.SELF);
                    client.setDataChannelProtection(GridFTPSession.PROTECTION_SAFE);
                }
			
                if (!parallel) {
                    ;
                }
                else {
                    int parallelism = 2;
                    client.setOptions(new RetrieveOptions(parallelism));
                    HostPortList hpl = null;
                    hpl = client.setLocalStripedPassive(); 
                    client.setStripedActive(hpl);
                }
            }
//            if (new File(fullLocalFile).exists()) {
//                new File(fullLocalFile).delete();
//            }
            DataSource source = null;
            if (!parallel) {
                HostPort hp2 = client.setPassive();
                client.setLocalActive();
                source = new DataSourceStream(new FileInputStream(fullLocalFile));
                client.put(fullRemoteFile,
                		source,
                           null);
            }
            else {
                //			sink = new FileRandomIO(new RandomAccessFile(fullLocalFile,"rw"));
                //			long size = client.getSize(fullRemoteFile);
                //			client.extendedGet(fullRemoteFile,
                //					size,
                //					sink,
                //					null);
            }
            // client.close();
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new IOException("fetch");
        }
	}

	public void secureUpload(URI url, String fullLocalFile, String user, 
			boolean encrypt, boolean signature) throws IOException
     {
        
        client = getGridFTPClient(url, user, encrypt, signature);
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
