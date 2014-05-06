package gov.nih.nci.ivi.dicom.virtualpacs.database;

import gov.nih.nci.ivi.utils.URLChooser;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import com.pixelmed.network.ReceivedObjectHandler;
import com.pixelmed.network.ReceivedObjectHandlerFactory;

public class VirtualPACSReceivedObjectHandlerFactory implements ReceivedObjectHandlerFactory {
	private class DefaultURLChooser extends URLChooser {
		public String chooseURL(String[] dataServiceURLs) {
            // TODO: later want to select the appropriate data service to send to.  for now, use random or random
			// NCIA data service does not support upload.
			Set<String> urls = new HashSet<String>();
			for (String url : dataServiceURLs) {
				if (url.endsWith("/DICOMDataService")) {
					urls.add(url);
				}
			}
			if (urls.size() == 0) {
				return null;
			}
			
            int index = (int)Math.floor(Math.random() * (double)(urls.size()));
            return urls.toArray(new String[] {})[index];
		}
	}

	private String[] dataServiceURLs;
	private URLChooser urlChooser;

	public VirtualPACSReceivedObjectHandlerFactory(String[] dataServiceURLs, URLChooser urlChooser)
	{
		Arrays.sort(dataServiceURLs);
		this.dataServiceURLs = dataServiceURLs;
		
		if (urlChooser == null) {
			this.urlChooser = new DefaultURLChooser();
		} else {
			this.urlChooser = urlChooser;
		}
	}

	public ReceivedObjectHandler newInstance(File dir) {
		VirtualPACSReceivedObjectHandler handler = null;

		String url = this.urlChooser.chooseURL(this.dataServiceURLs);

		return new VirtualPACSReceivedObjectHandler(dir.getAbsolutePath(), url);		
	}

}
