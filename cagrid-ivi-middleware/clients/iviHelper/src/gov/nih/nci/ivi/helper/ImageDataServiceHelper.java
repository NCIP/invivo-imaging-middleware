package gov.nih.nci.ivi.helper;

public class ImageDataServiceHelper{

	boolean debug;

	public ImageDataServiceHelper()
	{
		//may modify to handle security
		//One of the properties that must be created when using 
	}
/*	
	public void retrieveImageData(CQLQuery cqlQuery, String dataSourceURL, String localDataDestination)
	{
		ImageDataServiceClient imageClient;
		try {
			imageClient = new ImageDataServiceClient(dataSourceURL);
			ImageDataServiceBulkDataHandlerClient eprForRetrieval = imageClient.retrieveWithGridFTP(cqlQuery);
			System.out.println("EPR Recieved " + eprForRetrieval.toString());
			org.apache.axis.types.URI[] gridFtpUrls = eprForRetrieval.getGridFTPURLs();
			for(int i = 0; i < gridFtpUrls.length; i++)
				CommonUtilities.gridFTPDataRetrieval(gridFtpUrls[i].toString(), localDataDestination);
		} catch (MalformedURIException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return;
	}

	public void submitImageData(String []localDataSource, String []dataDestinationURL)
	{
		for(int i = 0; i < dataDestinationURL.length; i++)
		{
			try {
				ImageDataServiceClient imageClientSubmit = new ImageDataServiceClient(dataDestinationURL[i]);
				SubmissionInformation subInfo = new SubmissionInformation();
				subInfo.setFileType("null");
				ImageDataServiceSubmitClient eprForSubmission = imageClientSubmit.submit(subInfo);
				org.apache.axis.types.URI[] gridFtpUrls = eprForSubmission.getUploadURLs();
				int j;
				for(j = 0; j < gridFtpUrls.length; j++)
					CommonUtilities.gridFTPDataSubmission(gridFtpUrls[j].toString(), localDataSource, false);
				eprForSubmission.processURLs(gridFtpUrls);
				
			} catch (MalformedURIException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("gridFTP submission of " + localDataSource.length + " number of image files failed");
				e.printStackTrace();
			}
		}
	}


	public void submitImageData(File localDataSource, String []dataDestinationURL)
	{
		for(int i = 0; i < dataDestinationURL.length; i++)
		{
			try {
				ImageDataServiceClient imageClientSubmit = new ImageDataServiceClient(dataDestinationURL[i]);
				SubmissionInformation subInfo = new SubmissionInformation();
				subInfo.setFileType("null");
				ImageDataServiceSubmitClient eprForSubmission = imageClientSubmit.submit(subInfo);
				org.apache.axis.types.URI[] gridFtpUrls = eprForSubmission.getUploadURLs();
				
				GridFTPFetcher gftpPutter = new GridFTPFetcher();
				for(int j = 0; j < gridFtpUrls.length; j++)
					gftpPutter.upload(gridFtpUrls[j].toString(), localDataSource.getAbsolutePath(), true);
				eprForSubmission.processURLs(gridFtpUrls);
				
			} catch (MalformedURIException e) {
				e.printStackTrace();
			} catch (RemoteException e) {
				e.printStackTrace();
			} catch (IOException e) {
				System.err.println("gridFTP submission of files in " + localDataSource.getAbsolutePath() + " failed");
				e.printStackTrace();
			}
		}
	}
*/
}
