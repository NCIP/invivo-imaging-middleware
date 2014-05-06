package gov.nih.nci.ivi.dicom.virtualpacs.database;

import com.pixelmed.query.QueryResponseGenerator;
import com.pixelmed.query.QueryResponseGeneratorFactory;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;

/**
 * @author Ashish Sharma
 * @author Tony Pan
 * @version 1.2
 */

public class DICOMcaGridQRGFactory implements QueryResponseGeneratorFactory {

	private String[] dataServiceUrls;
	private ModelMap map;

	public DICOMcaGridQRGFactory(String[] dicomDataServiceUrls, ModelMap map) {
		System.err.println("DICOMcaGridQRGFactory");
		dataServiceUrls = dicomDataServiceUrls;
		this.map = map;
	}

	public QueryResponseGenerator newInstance() {
		DICOMcaGridQRG QRG;
		try {
			QRG = new DICOMcaGridQRG(dataServiceUrls, this.map);
		} catch (Exception e) {
			System.err.println("DICOMcaGridQRG" + e.getMessage());
			return null;
		}
		return QRG;
	}

}
