/**
 * 
 */
package edu.osu.bmi.ivy;

import java.io.File;
import java.io.FileFilter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author tpan
 *
 */
public class IvyModuleUtils {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		if (args.length < 2) {
			System.out.println("usage: IVIModuleUtils operation repository [overwrite]");
			System.out.println("       operation = [add ...]");
			return;
		}
		
		String operation = args[0];
		String repoDir = args[1];
		boolean overwrite = false;
		if (args.length > 2) { 
			overwrite = Boolean.parseBoolean(args[2]);
		}
		
		if (!operation.equalsIgnoreCase("add")) {
			System.out.println("operation " + operation + " not supported");
			return;
		}
		
		/* 
		 * organization is repodir/repoName/orgName/moduleName/version/files
		 * 
		 */

		String orgStr = null;
		String modStr = null;
		String revStr = null;
		
		File rootRepo = new File(repoDir);
		if (rootRepo.exists()) {
			File[] repos = rootRepo.listFiles(new FileFilter() {

				public boolean accept(File pathname) {
					return pathname.isDirectory() && !pathname.getName().equals(".svn") && !pathname.getName().equals("CVS");
				}
			});
			
			// for each repository
			for (File repo : repos) {
				
				// get all the organizations
				File[] orgs = repo.listFiles(new FileFilter() {

					public boolean accept(File pathname) {
						return pathname.isDirectory() && !pathname.getName().equals(".svn") && !pathname.getName().equals("CVS");
					}
				});
				
				// for each organizaiton
				for (File org : orgs) {
					orgStr = org.getName();
					
					// get all the organizations
					File[] modules = org.listFiles(new FileFilter() {

						public boolean accept(File pathname) {
							return pathname.isDirectory() && !pathname.getName().equals(".svn") && !pathname.getName().equals("CVS");
						}
					});
					
					// for each module
					for (File module : modules) {
						modStr = module.getName();
						
						// get all the organizations
						File[] revs = module.listFiles(new FileFilter() {

							public boolean accept(File pathname) {
								return pathname.isDirectory() && !pathname.getName().equals(".svn") && !pathname.getName().equals("CVS");
							}
						});

						for (File rev : revs) {
							revStr = rev.getName();
							
							final File ivyxml = new File(rev.getAbsolutePath() + File.separator + modStr + "-" + revStr + ".xml");
							
							if (ivyxml.exists() && !overwrite)  {
								continue;
							} else {
								
								File[] artifacts = rev.listFiles(new FileFilter() {

									public boolean accept(File pathname) {
										if (pathname.isFile() && !pathname.equals(ivyxml) && !pathname.getName().equals(".svn") && !pathname.getName().equals("CVS")) {
											return true;
										}
										return false;
									}
									
								});
								
								
								// create the file
								StringBuffer buf = new StringBuffer();
								
								buf.append("<ivy-module version=\"1.1\">\n");
							    buf.append("	<info\n");
							    buf.append("		organisation=\"" + orgStr + "\"\n");
							    buf.append("		module=\"" + modStr + "\"\n");
							    buf.append("		revision=\"" + revStr + "\"\n");
							    buf.append("		status=\"release\" />\n");
							    buf.append("	<configurations>\n");
							    buf.append("		<!-- TODO: change these -->\n");
							    buf.append("		<conf name=\"default\"/>\n");
							    buf.append("	</configurations>\n");
							    buf.append("	<publications>\n");

							    
							    for (File artifact : artifacts) {
							    	String filename = artifact.getName();
							    	int extIndex = filename.lastIndexOf(".");
							    	buf.append("		<artifact name=\"" + filename.substring(0, extIndex) + "\" type=\"" + filename.substring(extIndex + 1) + "\" conf=\"default\" />\n");
							    }

								buf.append("	</publications>\n");
							    buf.append("	<dependencies>\n");
							    buf.append("		<!-- TODO: change these -->\n");
							    buf.append("	</dependencies>\n");
							    buf.append("</ivy-module>\n");
								
							    System.out.println(buf.toString());
							    
							    try {
									FileWriter writer = new FileWriter(ivyxml);
									writer.write(buf.toString());
									writer.close();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
						}						
					}
				}
			}
		}
	}

}
