package gov.nih.nci.ivi.utils;

import java.io.*;
import java.net.*;
import java.util.*;
import java.security.*;

public class x509hasher {
    public static String getHash(String inputFilename) throws IOException {
        if (!new File(inputFilename).exists()) {
            throw new IOException("input filename " + inputFilename +
                                  " doesn't exist");
        }
        String gl = System.getenv("GLOBUS_LOCATION");
        String openssl = (gl + "/bin/openssl");
        if (!new File(openssl).exists()) {
            throw new IOException("cannot find openssl");
        }
        String cmd = openssl + " x509 -hash -noout -in " + inputFilename;
        System.out.println(cmd);
        Process p = Runtime.getRuntime().exec(cmd);
        String hash = new BufferedReader(new InputStreamReader(p.getInputStream())).readLine();
        try {
            p.waitFor();
        }
        catch (Exception ex) {
            ex.printStackTrace();
            throw new IOException("calling openssl");
        }
        return hash;
    }
    /**

* Convert a byte[] array to readable string format. This makes the "hex"
readable!

* @return result String buffer in String format 

* @param in byte[] buffer to convert to string format

*/

    static String byteArrayToHexString(byte in[]) {
        byte ch = 0x00;
        int i = 0; 
        if (in == null || in.length <= 0)
            return null;
        String pseudo[] = {"0", "1", "2",
                           "3", "4", "5", "6", "7", "8",
                           "9", "a", "b", "c", "d", "e",
                           "f"};
        StringBuffer out = new StringBuffer(in.length * 2);
        while (i < in.length) {
            ch = (byte) (in[i] & 0xF0); // Strip off high nibble
            ch = (byte) (ch >>> 4);
            // shift the bits down
            ch = (byte) (ch & 0x0F);    
            // must do this is high order bit is on!
            out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
            ch = (byte) (in[i] & 0x0F); // Strip off low nibble 
            out.append(pseudo[ (int) ch]); // convert the nibble to a String Character
            i++;
        }
        String rslt = new String(out);
        return rslt;
    }
    public static String getOpensslHash(String theDEREncodedSubject)
        throws java.security.NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        byte[] b = theDEREncodedSubject.getBytes();
        System.out.println("bytes for " +theDEREncodedSubject + ": ");
        for (int i = 0; i < b.length; i++) {
            System.out.printf(" %d", b[i]);
        }
        System.out.println();
        md.update(b);
        byte[] digest = md.digest();
        System.out.println("digest for " +theDEREncodedSubject + ": ");
        for (int i = 0; i < digest.length; i++) {
            System.out.printf("%x", digest[i]);
        }
        System.out.println();
        byte[] first4rev = new byte[4];
        for (int i = 0; i < 4; i++) {
            first4rev[3-i] = digest[i];
        }
        return byteArrayToHexString(first4rev);
    }
    public static void main(String[] args) throws Exception {
        System.out.println(x509hasher.getHash("/home/rutt/.globus/certificates/dorian-cagrid-cacert.1"));
        System.out.println(x509hasher.getOpensslHash("/O=OSU/OU=BMI/OU=caGrid/OU=Dorian/OU=cagrid04/CN=caGrid 1.0Beta Dorian CA"));

        /*
          $ echo -n abcd|md5sum
          f5ac8127b3b6b85cdc13f237c6005d80
        */
        System.out.println(x509hasher.getOpensslHash("abcd"));
    }
}
