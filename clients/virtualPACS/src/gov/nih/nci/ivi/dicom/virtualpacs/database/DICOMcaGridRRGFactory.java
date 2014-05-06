package gov.nih.nci.ivi.dicom.virtualpacs.database;

import com.pixelmed.query.RetrieveResponseGenerator;
import com.pixelmed.query.RetrieveResponseGeneratorFactory;

import gov.nih.nci.ivi.dicom.modelmap.ModelMap;

public class DICOMcaGridRRGFactory implements RetrieveResponseGeneratorFactory {

	private String[] dataServiceUrls;
	private ModelMap map;

	public DICOMcaGridRRGFactory(String[] dicomDataServiceUrls, ModelMap map) {
		System.err.println("DICOMcaGridRRGFactory");
		dataServiceUrls = dicomDataServiceUrls;
		this.map = map;
	}

	public RetrieveResponseGenerator newInstance() {
		DICOMcaGridRRG RRG;
		try {
			RRG = new DICOMcaGridRRG(dataServiceUrls, map);
		}
		catch (Exception e) {
			System.err.println("DICOMcaGridRRG" + e.getMessage());
			return null;
		}
		return RRG;
	}

}
