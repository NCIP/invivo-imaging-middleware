package gov.nih.nci.ivi.utils;

import java.io.*;
import java.net.*;

import org.globus.ftp.*;
import org.globus.gsi.gssapi.auth.*;
import org.globus.gsi.GlobusCredential;
import org.gridforum.jgss.ExtendedGSSManager;
import org.ietf.jgss.GSSCredential;

public class gftptest2 {
    public static void usage(String appname) {
        System.out.println("usage: " + appname + " <localdir> <server> <remotefile...>");
        System.exit(1);
    }
    public static void main(String[] args) throws Exception {
        if (args.length < 3) {
            usage("gftptest2");
        }
        double b4 = System.currentTimeMillis();
        
        boolean nosec = false;
        boolean parallel = false;
        GridFTPClient client;
        if (nosec) {
            // hit server started with
            // $ globus-gridftp-server -aa -p 3253 -d ALL -chdir -chdir-to / -auth-level 0
//            client= new GridFTPClient("140.254.80.132", 3253);
            //client= new GridFTPClient("dca01.bmi.ohio-state.edu", 3253);
//             client= new GridFTPClient("localhost", 3253);
            client = new GridFTPClient(args[1], 3253);
        }
        else {
            // requires valid grid-proxy-init
            client = new GridFTPClient(args[1], 2811);
        }
        String[] fullRemoteFiles= new String[args.length - 2];
        String[] fullLocalFiles= new String[args.length - 2];
        for (int i = 2; i < args.length; i++) {
            fullRemoteFiles[i-2] = args[i];
            fullLocalFiles[i-2] = args[0] + (File.separator) +
                new File(args[i]).getName();
        }
        if (nosec) {
            client.authorize("ftp","");
        }
        else {
            client.setAuthorization(
                new IdentityAuthorization(
                    GlobusCredential.getDefaultCredential().getIdentity()));

//                     "/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid04/OU=IdP [1]/CN=rutttest"));
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
        client.setDataChannelAuthentication(DataChannelAuthentication.NONE);
        client.setDataChannelProtection(GridFTPSession.PROTECTION_CLEAR);

        if (!parallel) {
//             client.setTCPBufferSize(65536);
//             client.setLocalTCPBufferSize(65536);
        }
        else {
            int parallelism = 2;
            client.setOptions(new RetrieveOptions(parallelism));
//             HostPortList hpl = null;
//             hpl = client.setLocalStripedPassive(); 
//             client.setStripedActive(hpl);
        }
        int i;
        for (i = 0; i < fullLocalFiles.length; i++) {
            HostPort hp2 = client.setPassive();
            client.setLocalActive();
            String fullLocalFile = fullLocalFiles[i];
            String fullRemoteFile = fullRemoteFiles[i];
            if (new File(fullLocalFile).exists()) {
                new File(fullLocalFile).delete();
            }
            DataSink sink = null;
            if (!parallel) {
                sink = new DataSinkStream(new FileOutputStream(fullLocalFile));
                client.get(fullRemoteFile,
                           sink,
                           null);
            }
            else {
                sink = new FileRandomIO(new RandomAccessFile(fullLocalFile,"rw"));
                long size = client.getSize(fullRemoteFile);
                client.extendedGet(fullRemoteFile,
                                   size,
                                   sink,
                                   null);
            }
        }
        double after = System.currentTimeMillis();
        System.out.println("elapsed for " + fullLocalFiles.length +
                           " files: "+ (after-b4)/1000.0);
    }
}

