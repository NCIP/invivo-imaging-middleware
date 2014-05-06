package gov.nih.nci.ivi.pathologydataservice.service;

import edu.osu.bmi.utils.io.zip.ZipEntryOutputStream;
import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.ivi.fileinfo.ExecutionParameters;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.cagrid.transfer.context.service.helper.TransferServiceHelper;
import org.cagrid.transfer.context.stubs.types.TransferServiceContextReference;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

/** 
 * I am the service side implementation class.  IMPLEMENT AND DOCUMENT ME
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public class PathologyDataServiceImpl extends PathologyDataServiceImplBase {
    private static PooledExecutor workerPool;
    private static final int IDLE_THREAD_LIFETIME = 5 * 60 * 1000;
    private String rootDirName = "/tmp/OCVMLAB";
    private String matlabHome = "/parallel/matlab71";
    private String tmpStorage = "/tmp/OCVMLAB/tmp";
    private String OCVMExecutable = "ocvmlab";
    private String executableName;
    
    private int targetNailHeight = 1000;
    private int targetNailWidth = 1000;
	
	public PathologyDataServiceImpl() throws RemoteException {
		super();
		try {
			rootDirName = PathologyDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
			//matlabHome = PathologyDataServiceConfiguration.getConfiguration().getMatlabHome();
			//tmpStorage = PathologyDataServiceConfiguration.getConfiguration().getTmpStorage();
		} catch (Exception e) {
			e.printStackTrace();
		}
		workerPool = new PooledExecutor(new LinkedQueue(), 1);
		workerPool.setMinimumPoolSize(1);
		workerPool.setMaximumPoolSize(1);
		workerPool.createThreads(1);
		workerPool.setKeepAliveTime(IDLE_THREAD_LIFETIME);
		workerPool.waitWhenBlocked();

		executableName = rootDirName + File.separator + "tiler";
	}
	
  public int view(gov.nih.nci.ivi.fileinfo.ViewParameters viewParams) throws RemoteException {
      String rootDir = rootDirName;
      double nailZoom=0.012;
      int nailHeight=0;
      int nailWidth=0;

/*		try {
			rootDir = PathologyDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
		} catch (Exception e) {
			throw new RemoteException(e.getMessage());
		}
		*/

		File cacheDir = new File(rootDir + File.separator + "cache");
		if (!cacheDir.exists())
			cacheDir.mkdirs();

		String inputFileName = rootDir + File.separator + "images" + File.separator + viewParams.getImageFileName();
		File inputFile = new File(inputFileName);
		if (!inputFile.exists())
			throw new RemoteException("File " + inputFileName + "is not in the repository.");
		String outputFileName = rootDir + File.separator + "cache" + File.separator + viewParams.getImageFileName() +
		                        "-" + viewParams.getX() + "-" + viewParams.getY() + "-" + viewParams.getZoom() + ".jpg";

		File outputFileParentDir = new File(outputFileName).getParentFile();
		outputFileParentDir.mkdirs();
		
		String command = executableName + " " + inputFileName + " " + outputFileName + " " + viewParams.getX() + " " + 
                    viewParams.getY() + " " + viewParams.getWidth() + " " + viewParams.getHeight() + " " +
						                        viewParams.getZoom();

		String nailOutputFileName = rootDir + File.separator + "thumbnails" + File.separator + viewParams.getImageFileName() ;

		
		System.out.println("command:"+command);

		Process p = null;
		/* tiler stuff*/
		try {
			 p = Runtime.getRuntime().exec(command);
		} catch (IOException e) {
			throw new RemoteException(e.getMessage());
		}
		
		BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
		BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		
		// read the output from the command
		System.out.println("Here is the standard output of the command:\n");
		String s = null;
		try {
			// grab height and width from 13th line of tiler output
			for(int x=0; x<13; x++){
				s = stdInput.readLine();
				System.out.println(s);
			}

			String str[] = s.split(" ");

			// create MetaDataFile
			//

			 PrintWriter mdwriter = new PrintWriter(new BufferedWriter(new FileWriter(new File(nailOutputFileName+".metadata"))));
			mdwriter.println(str[2]+" "+str[9]);
			mdwriter.close();

			int owidth= Integer.parseInt(str[2]);
			int oheight= Integer.parseInt(str[9]);

			// tiler doesn't handle small images.  result is blank if too small.
			//
			if(owidth < 84000) nailZoom=0.03;

			nailWidth = (int) Math.round(owidth*nailZoom);
			nailHeight = (int) Math.round(oheight*nailZoom);


			while ((s = stdInput.readLine()) != null)
				System.out.println(s);

		} catch (IOException e) {
			throw new RemoteException(e.getMessage());
		}

		// read any errors from the attempted command
		System.out.println("Here is the standard error of the command (if any):\n");
		try {
			while ((s = stdError.readLine()) != null)
				System.out.println(s);
		} catch (IOException e) {
			throw new RemoteException(e.getMessage());
		}

		try {
			p.waitFor();
		} catch (InterruptedException e) {
			throw new RemoteException(e.getMessage());
		}


		// create thumbnail
		//

		File nailFile = new File(nailOutputFileName);
		if(!nailFile.exists()){

			String nailCommand = executableName + " " + inputFileName + " " + nailOutputFileName + " 0 0 "+nailWidth+" "+nailHeight+" "+nailZoom; 
			System.out.println("nail command:"+nailCommand);

			try {
				p = Runtime.getRuntime().exec(nailCommand);
			} catch (IOException e) {
				throw new RemoteException(e.getMessage());
			}

			stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
			stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
		
			// read the output from the command
			System.out.println("Here is the standard output of the nail command:\n");
			s = null;
			try {
				while ((s = stdInput.readLine()) != null)
					System.out.println(s);
			} catch (IOException e) {
				throw new RemoteException(e.getMessage());
			}

			// read any errors from the attempted command
			System.out.println("Here is the standard error of the command (if any):\n");
			try {
				while ((s = stdError.readLine()) != null)
					System.out.println(s);
			} catch (IOException e) {
				throw new RemoteException(e.getMessage());
			}

			try {
				p.waitFor();
			} catch (InterruptedException e) {
				throw new RemoteException(e.getMessage());
			}
		} else {
			System.out.println("Nail "+nailOutputFileName+" exists");
		}

		return p.exitValue();
	
  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieve(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException {

	  String rootDir = null;
	  try {
		rootDir = PathologyDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
	} catch (Exception e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
		throw new RemoteException();
	}
	  
	  PathologyRetrieve pathRet = new PathologyRetrieve(rootDir, false,
				null, 0);

	  	System.out.println("in Retrieve****");
		final Vector<String> retrievedFiles = pathRet.performRetrieve(cQLQuery);
		// Step 2: We transfer the data

		// set up the piped streams
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		try {
			pis.connect(pos);
		} catch (IOException e) {
			throw new RemoteException("Unable to make a pipe", e);
		}

		// The part below needs to be threaded, since the transfer service
		// creation reads from the stream completely.
		final PipedOutputStream fpos = pos;
		Thread t = new Thread() {

			@Override
			public void run() {
				// now write to the output stream. for this test, use a zip
				// stream.
				// this is really to deal with the fact that we don't have a
				// good way to delimit the files.
				double t1 = System.currentTimeMillis() / 1000.0;

				ZipEntryOutputStream zeos = null;
				ZipOutputStream zos = new ZipOutputStream(
						new BufferedOutputStream(fpos, 8192));
				for (int index = 0; index < retrievedFiles.size(); index++) {
					String transferDoc = retrievedFiles.get(index);
					System.out.println("transferDoc is " + transferDoc);
					File dcmFile = new File(transferDoc);
					try {
						zeos = new ZipEntryOutputStream(zos, dcmFile.getName(), 
								ZipEntry.STORED);
						BufferedInputStream dicomIn = new BufferedInputStream(
								new FileInputStream(transferDoc));
						byte[] data = new byte[8192];
						int bytesRead = 0;
						while ((bytesRead = (dicomIn.read(data, 0, data.length))) > 0) {
							zeos.write(data, 0, bytesRead);
							//System.out.println("Finished reading some part of DICOM file" + transferDoc);
						}
					} catch (IOException e1) {
						System.out.println("ERROR writing to zip entry "
								+ e1.getMessage());
						e1.printStackTrace();
					} finally {
						try {
							zeos.flush();
							zeos.close();
							System.out.println("caGrid transferred at "
									+ new Date().getTime());

						} catch (IOException e) {
							System.out.println("ERROR closing zip entry "
									+ e.getMessage());
							e.printStackTrace();
						}
					}
					dcmFile.delete();
				}
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					System.err.println("ERROR closing zip stream "
							+ e.getMessage());
					e.printStackTrace();
				}
				try {
					fpos.flush();
					fpos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				double t2 = System.currentTimeMillis() / 1000.0;

				System.out.println("Time Taken = " + (t2 - t1));

			}
		};
		t.start();

		// set up the transfer context
		TransferServiceContextReference tscr = TransferServiceHelper
		.createTransferContext(pis, null);
		System.out.println("TransferServiceContextReference " + tscr);
		// set up the transfer context
		return tscr;

  }

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference submit() throws RemoteException {
    //TODO: Implement this autogenerated method
    throw new RemoteException("Not yet implemented");
  }

  public void delete(gov.nih.nci.ivi.fileinfo.FileInfo fileInfo) throws RemoteException {
      System.out.println("Server: deleting file " + fileInfo.getName());
      String rootDir = null;
		try {
			rootDir = PathologyDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
		} catch (Exception e) {
			e.printStackTrace();
		}
      String inputFileName = rootDir + File.separator + fileInfo.getType() + File.separator + fileInfo.getName();
      File inputFile = new File(inputFileName);
      if (!inputFile.exists())
      	throw new RemoteException("File does not exist");
      inputFile.delete();
  }

  public void execute(gov.nih.nci.ivi.fileinfo.ExecutionParameters executionParameters) throws RemoteException {
		ExecutionThread st = new ExecutionThread(this, executionParameters, rootDirName);
		try {
			workerPool.execute(st);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
  }
  

  class ExecutionThread extends Thread {
		final PathologyDataServiceImpl self;
		final ExecutionParameters executionParameters;
		final String rootDirName;

		public ExecutionThread(PathologyDataServiceImpl self, ExecutionParameters executionParameters, String rootDirName) {
			this.self = self;
			this.executionParameters = executionParameters;
			this.rootDirName = rootDirName;
		}       

		public void run() {
			String hostFileName = rootDirName + File.separator + "hosts";
			BufferedReader bufferedReader;
			try {
				bufferedReader = new BufferedReader(new FileReader(hostFileName));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			ArrayList hostArray = new ArrayList();
			String line = null;
			try {
				while ((line = bufferedReader.readLine()) != null)
					hostArray.add(line);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
			String content = "matlab_home     " + matlabHome + "\n" + "hosts           ";
			int j = 0;
			for (int i = 0; i < executionParameters.getNumberOfHosts(); i++) {
				content += hostArray.get(j) + " ";
				j++;
				if (j == hostArray.size())
					j = 0;
			}
			content += "\n\n";
			content += "infiles         " + rootDirName + File.separator + "images" + File.separator + executionParameters.getImageFileName() + "\n" +
					   "outfiles        " + rootDirName + File.separator + "images" + File.separator + "out-" + executionParameters.getImageFileName() + ".txt\n\n" +
					   "tmpstorage      " + tmpStorage + "\n\n" +
					   "functionpath    " + rootDirName + File.separator + "codes\n\n" +
					   "functions       " + executionParameters.getFunctionFileNames() + "\n\n" +
					   "tilesize        " + executionParameters.getTileSize() + " " + executionParameters.getTileSize() + "\n\n" +
					   "overlap         " + executionParameters.getOverlap() + "\n\n" +
					   "defaultrgb      0 0 0\n";
			File configFile = new File(rootDirName + File.separator + "config");
			FileOutputStream fileOutputStream = null;
			try {
				fileOutputStream = new FileOutputStream(configFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			}
			try {
				fileOutputStream.write(content.getBytes());
			} catch (IOException e) {
				e.printStackTrace();
			}
			Runtime rt = Runtime.getRuntime();
			String s = null;
			try {
				System.out.println(OCVMExecutable);
				Process p = rt.exec(OCVMExecutable + " " +
						rootDirName + File.separator + "config");
				BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
				BufferedReader stdError = new BufferedReader(new InputStreamReader(p.getErrorStream()));
				// read the output from the command
				System.out.println("Here is the standard output of the command:\n");
				while ((s = stdInput.readLine()) != null)
					System.out.println(s);
				// read any errors from the attempted command
				System.out.println("Here is the standard error of the command (if any):\n");
				while ((s = stdError.readLine()) != null)
					System.out.println(s);
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}
	}               
	
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieveThumbnail(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException { 
	  String rootDir = rootDirName;
	  System.out.println("retrieveThumbnail");
/*
	  try {
		rootDir = PathologyDataServiceConfiguration.getConfiguration().getCqlQueryProcessorConfig_rootDir();
	} catch (Exception e3) {
		// TODO Auto-generated catch block
		e3.printStackTrace();
		throw new RemoteException();
	}
	*/
	  
	  PathologyRetrieve pathRet = new PathologyRetrieve(rootDir, false,
				null, 0);
		final Vector<String> retrievedFiles = pathRet.performRetrieveThumbnail(cQLQuery);
		// Step 2: We transfer the data

		// set up the piped streams
		PipedOutputStream pos = new PipedOutputStream();
		PipedInputStream pis = new PipedInputStream();
		try {
			pis.connect(pos);
		} catch (IOException e) {
			throw new RemoteException("Unable to make a pipe", e);
		}

		// The part below needs to be threaded, since the transfer service
		// creation reads from the stream completely.
		final PipedOutputStream fpos = pos;
		Thread t = new Thread() {

			@Override
			public void run() {
				// now write to the output stream. for this test, use a zip
				// stream.
				// this is really to deal with the fact that we don't have a
				// good way to delimit the files.
				double t1 = System.currentTimeMillis() / 1000.0;

				ZipEntryOutputStream zeos = null;
				ZipOutputStream zos = new ZipOutputStream(
						new BufferedOutputStream(fpos, 8192));
				for (int index = 0; index < retrievedFiles.size(); index++) {
					String transferDoc = retrievedFiles.get(index);
					System.out.println("transferDoc is " + transferDoc);
					File dcmFile = new File(transferDoc);
					try {
						zeos = new ZipEntryOutputStream(zos, dcmFile.getName(), 
								ZipEntry.STORED);
						BufferedInputStream dicomIn = new BufferedInputStream(
								new FileInputStream(transferDoc));
						byte[] data = new byte[8192];
						int bytesRead = 0;
						while ((bytesRead = (dicomIn.read(data, 0, data.length))) > 0) {
							zeos.write(data, 0, bytesRead);
							//System.out.println("Finished reading some part of DICOM file" + transferDoc);
						}
					} catch (IOException e1) {
						System.out.println("ERROR writing to zip entry "
								+ e1.getMessage());
						e1.printStackTrace();
					} finally {
						try {
							zeos.flush();
							zeos.close();
							System.out.println("caGrid transferred at "
									+ new Date().getTime());

						} catch (IOException e) {
							System.out.println("ERROR closing zip entry "
									+ e.getMessage());
							e.printStackTrace();
						}
					}
					System.out.println("DONT delete Thumbnail");
					//dcmFile.delete();
				}
				try {
					zos.flush();
					zos.close();
				} catch (IOException e) {
					System.err.println("ERROR closing zip stream "
							+ e.getMessage());
					e.printStackTrace();
				}
				try {
					fpos.flush();
					fpos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				double t2 = System.currentTimeMillis() / 1000.0;

				System.out.println("Time Taken = " + (t2 - t1));

			}
		};
		t.start();

		// set up the transfer context
		TransferServiceContextReference tscr = TransferServiceHelper
		.createTransferContext(pis, null);
		System.out.println("TransferServiceContextReference " + tscr);
		// set up the transfer context
		return tscr;


    
  }

  public int getWidth(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException {
       String value = cQLQuery.getTarget().getAttribute().getValue();
       String metaDataFileName = rootDirName + File.separator + "thumbnails" + File.separator + value;

	File metaDataFile = new File(metaDataFileName);

	if(!metaDataFile.exists()){
		System.out.println("MetaData Filename does not exist");
		return 0;
        } 
	else{
		try{
			BufferedReader in = new BufferedReader(new FileReader(metaDataFile));
			String line = in.readLine();
			String[] str = line.split(" ");
			System.out.println("getWidth returning:"+Integer.parseInt(str[0]));
			in.close();
			return Integer.parseInt(str[0]);
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	return 0;

	  //System.out.println("getWidth!!!!");
	  //return 89890;
  }

  public int getHeight(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException {
//	System.out.println("getHeight!!!!");
//	  return 29684;
//
       String value = cQLQuery.getTarget().getAttribute().getValue();
       String metaDataFileName = rootDirName + File.separator + "thumbnails" + File.separator + value;

	File metaDataFile = new File(metaDataFileName);

	if(!metaDataFile.exists()){
		System.out.println("MetaData Filename does not exist");
		return 0;
        } 
	else{
		try{
			BufferedReader in = new BufferedReader(new FileReader(metaDataFile));
			String line = in.readLine();
			String[] str = line.split(" ");
			System.out.println("getHeight returning:"+Integer.parseInt(str[1]));
			in.close();
			return Integer.parseInt(str[1]);
		}
		catch(FileNotFoundException e){
			e.printStackTrace();
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	return 0;

  }



}

