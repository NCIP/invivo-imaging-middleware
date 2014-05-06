/*
 * Created on Apr 22, 2006
 */
package gov.nih.nci.ivi.test.story.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class StdIOThread
	extends Thread
{
	private BufferedReader br;
	
	public StdIOThread(InputStream is)
	{
		super();
		this.br = new BufferedReader(new InputStreamReader(is));
		super.setDaemon(true);
	}
	
	public void run()
	{
		try {
			String line = null;
			line = br.readLine();
			while (line!= null) {
				System.out.println(line);
				if(br!=null){
					line = br.readLine();
				} else {
					line = null;
				}
			}

		} catch (IOException e) {
			
		} finally {
			try{
				if(br!=null){
					br.close();
				}
			} catch(IOException ex){
			
			}
		}
	}

}
