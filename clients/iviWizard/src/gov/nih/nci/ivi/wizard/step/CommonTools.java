package gov.nih.nci.ivi.wizard.step;

import java.io.File;
import java.util.StringTokenizer;

/**
 * @author <A HREF="MAILTO:hastings@bmi.osu.edu">Shannon Hastings </A>
 * @author <A HREF="MAILTO:oster@bmi.osu.edu">Scott Oster </A>
 * @author <A HREF="MAILTO:langella@bmi.osu.edu">Stephen Langella </A>
 */
public class CommonTools {

	public static Process createAndOutputProcess(String cmd) throws Exception {
		final Process p;

		p = Runtime.getRuntime().exec(cmd);
		StreamGobbler errGobbler = new StreamGobbler(p.getErrorStream(), "ERR");
		StreamGobbler outGobbler = new StreamGobbler(p.getInputStream(), "OUT");
		errGobbler.start();
		outGobbler.start();

		return p;
	}

	public static String getAntCommand(String antCommand, String buildFileDir)
			throws Exception {
		String cmd = " " + antCommand;
		cmd = getAntCommandCall(buildFileDir) + cmd;
		return cmd;
	}

	public static String getAntAllCommand(String buildFileDir) throws Exception {
		return getAntCommand("all", buildFileDir);
	}

	public static String getAntMergeCommand(String buildFileDir)
			throws Exception {
		return getAntCommand("merge", buildFileDir);
	}

	public static String getAntDeployTomcatCommand(String buildFileDir)
			throws Exception {
		return createDeploymentCommand(buildFileDir, "deployTomcat");
	}

	private static String fixPathforOS(String path) {
		String os = System.getProperty("os.name");
		if ((os.indexOf("Windows") >= 0) || (os.indexOf("windows") >= 0)) {
			path = "\"" + path + "\"";
		} else {
			path = path.replaceAll(" ", "\\ ");
		}
		return path;
	}

	public static String getAntDeployGlobusCommand(String buildFileDir)
			throws Exception {
		return createDeploymentCommand(buildFileDir, "deployGlobus");
	}

	private static String createDeploymentCommand(String buildFileDir,
			String deployTarget) throws Exception {
		String dir = buildFileDir;
		File dirF = new File(dir);
		if (!dirF.isAbsolute()) {
			dir = buildFileDir + File.separator + dir;
		}
		dir = fixPathforOS(dir);
		String cmd = " -Dservice.properties.file=" + dir + File.separator
				+ "service.properties";

		cmd = getAntCommand(deployTarget, buildFileDir) + " " + cmd;
		return cmd;
	}

	public static String getAntSkeletonCreationCommand(String buildFileDir,
			String name, String dir, String packagename,
			String namespacedomain, String extensions) throws Exception {
		// fix dir path if it relative......
		System.out.println("CREATION: builddir: " + buildFileDir);
		System.out.println("CREATION: destdir: " + dir);
		File dirF = new File(dir);
		if (!dirF.isAbsolute()) {
			dir = buildFileDir + File.separator + dir;
		}
		dir = fixPathforOS(dir);
		String cmd = " -Dintroduce.skeleton.destination.dir=" + dir
				+ " -Dintroduce.skeleton.service.name=" + name
				+ " -Dintroduce.skeleton.package=" + packagename
				+ " -Dintroduce.skeleton.package.dir="
				+ packagename.replace('.', File.separatorChar)
				+ " -Dintroduce.skeleton.namespace.domain=" + namespacedomain
				+ " -Dintroduce.skeleton.extensions=" + extensions
				+ " createService";
		cmd = getAntCommandCall(buildFileDir) + cmd;
		System.out.println("CREATION: cmd: " + cmd);
		return cmd;
	}

	public static String getAntSkeletonPostCreationCommand(String buildFileDir,
			String name, String dir, String packagename,
			String namespacedomain, String extensions) throws Exception {
		// fix dir path if it relative......
		System.out.println("CREATION: builddir: " + buildFileDir);
		System.out.println("CREATION: destdir: " + dir);
		File dirF = new File(dir);
		if (!dirF.isAbsolute()) {
			dir = buildFileDir + File.separator + dir;
		}
		dir = fixPathforOS(dir);
		String cmd = " -Dintroduce.skeleton.destination.dir=" + dir
				+ " -Dintroduce.skeleton.service.name=" + name
				+ " -Dintroduce.skeleton.package=" + packagename
				+ " -Dintroduce.skeleton.package.dir="
				+ packagename.replace('.', File.separatorChar)
				+ " -Dintroduce.skeleton.namespace.domain=" + namespacedomain
				+ " -Dintroduce.skeleton.extensions=" + extensions
				+ " postCreateService";
		cmd = getAntCommandCall(buildFileDir) + cmd;
		System.out.println("CREATION: cmd: " + cmd);
		return cmd;
	}

	static String getAntCommandCall(String buildFileDir) throws Exception {
		String os = System.getProperty("os.name");
		String cmd = "";
		if ((os.indexOf("Windows") >= 0) || (os.indexOf("windows") >= 0)) {
			cmd = "-classpath \""
					+ CommonTools.getAntLauncherJarLocation(System
							.getProperty("java.class.path"), true)
					+ "\" org.apache.tools.ant.launch.Launcher -buildfile "
					+ "\"" + buildFileDir + File.separator + "build.xml\""
					+ cmd;
			cmd = "java.exe " + cmd;
		} else {
			// escape out the spaces.....
			buildFileDir = buildFileDir.replaceAll("\\s", "\\ ");
			cmd = "-classpath "
					+ CommonTools.getAntLauncherJarLocation(System
							.getProperty("java.class.path"), false)
					+ " org.apache.tools.ant.launch.Launcher -buildfile "
					+ buildFileDir + File.separator + "build.xml" + cmd;
			cmd = "java " + cmd;
		}
		return cmd;
	}

	static String getAntLauncherJarLocation(String path, boolean isWindows) {
		String separator = isWindows ? ";" : ":";
		StringTokenizer pathTokenizer = new StringTokenizer(path, separator);
		while (pathTokenizer.hasMoreTokens()) {
			String pathElement = pathTokenizer.nextToken();
			if (pathElement.indexOf("ant-launcher") != -1
					&& pathElement.endsWith(".jar")) {
				return pathElement;
			}
		}
		return null;
	}

}
