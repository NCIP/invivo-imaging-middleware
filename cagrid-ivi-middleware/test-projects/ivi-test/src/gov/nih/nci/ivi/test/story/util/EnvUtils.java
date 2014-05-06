/*
 * Created on Apr 22, 2006
 */
package gov.nih.nci.ivi.test.story.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class EnvUtils
{
	private EnvUtils() { super(); }
	
	public static String[] parseEnvVar(String envVar)
	{
		int index = envVar.indexOf('=');
		if (index == -1) throw new IllegalArgumentException("envVar " + envVar + " not of the form name=val");
		return new String[] { envVar.substring(0, index), envVar.substring(index+1) };
	}
	
	public static String[] overrideEnv(String[] envp)
	{
		Map envm = new HashMap(System.getenv());
		for (int i = 0; i < envp.length; i++) {
			String[] envVar = parseEnvVar(envp[i]);
			envm.put(envVar[0], envVar[1]);
		}
		envp = new String[envm.size()];
		Iterator keys = envm.keySet().iterator();
		int i = 0;
		while (keys.hasNext()) {
			String key = (String)keys.next();
			envp[i++] = key + "=" + envm.get(key);
		}
		return envp;
	}
}
