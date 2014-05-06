package gov.nih.nci.ivi.utils;

import java.io.File;

public class IOUtils {

    public static boolean deleteDirectory(String path) {
        File f = new File(path);
        if (f.exists() && f.isDirectory()) {
            File[] files = f.listFiles();
            for (int i = 0; i < files.length; i++)
                if (files[i].isDirectory())
                    deleteDirectory(files[i].getAbsolutePath());
                else
                    files[i].delete();
        }

        return f.delete();
    }

}
