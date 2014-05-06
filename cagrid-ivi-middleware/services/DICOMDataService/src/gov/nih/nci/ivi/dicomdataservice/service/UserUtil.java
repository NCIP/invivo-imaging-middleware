package gov.nih.nci.ivi.dicomdataservice.service;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 * Utility class for managing user credentials.
 * 
 * @author Mark Grand
 */
public class UserUtil {
    private static final Logger myLogger = LogManager.getLogger(UserUtil.class);

    /**
     * Private constructor because there should be no reason to instantiate this
     * class.
     */
    private UserUtil() {
        // Do nothing
    }

    /**
     * Return the ID of the user that requested the current operation.
     * 
     * @return the ID of the user that requested the current operation.
     * @throws NullPointerException
     *             if the user is null. This is probably caused by this method
     *             being called from a different thread than the one that
     *             initiated the operation.
     */
    static String getUserId() {
        final String user = org.globus.wsrf.security.SecurityManager.getManager().getCaller();
        if (myLogger.isDebugEnabled()) {
            myLogger.debug("getUserId: user=" + user);
        }
        if (user == null) {
            String msg = "User is null";
            NullPointerException ex = new NullPointerException(msg);
            ex.fillInStackTrace();
            myLogger.error("User is null", ex);
            throw ex;
        }
        return user;
    }
}
