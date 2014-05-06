/**
 * 
 */
package gov.nih.nci.ivi.dicom.embeddedpacs;


import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.DicomInputStream;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.network.DicomNetworkException;
import com.pixelmed.network.ReceivedObjectHandler;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author tpan
 *
 */
public class EmbeddedPACSReceivedObjectHandler extends ReceivedObjectHandler {

	/* (non-Javadoc)
	 * @see com.pixelmed.network.ReceivedDataHandler#sendPDataIndication(com.pixelmed.network.PDataPDU, com.pixelmed.network.Association)
	 */

	String savedImagesFolder;
	
	public EmbeddedPACSReceivedObjectHandler(String imagesFolder)
	{
		savedImagesFolder = imagesFolder;
	}

	@Override
    public void sendReceivedObjectIndication(String dicomFileName,String transferSyntax,String callingAETitle) throws DicomNetworkException, DicomException, IOException {
		if (dicomFileName != null) {
            //System.err.println("Received: "+dicomFileName+" from "+callingAETitle+" in "+transferSyntax);
            try {
            	
                DicomInputStream i = new DicomInputStream(new BufferedInputStream(new FileInputStream(dicomFileName)));
                
                AttributeList list = new AttributeList();
                list.read(i);
                String fileStudyInstanceUID = list.get(TagFromName.StudyInstanceUID).getSingleStringValueOrEmptyString();
                String fileSeriesInstanceUID = list.get(TagFromName.SeriesInstanceUID).getSingleStringValueOrEmptyString();
                double z = list.get(TagFromName.ImagePositionPatient).getDoubleValues()[2];
                String dfn = new File(dicomFileName).getName();
                String newDicomDirName = savedImagesFolder + File.separator + fileStudyInstanceUID + File.separator + fileSeriesInstanceUID;
                new File(newDicomDirName).mkdirs();

                File newNameDicomFile = new File(dicomFileName);
                String fileName = newDicomDirName+File.separator+dfn+".dcm";
                newNameDicomFile.renameTo(new File(fileName));
                System.out.println("retrieved fileName: "+fileName);

                String manifestfn = (newDicomDirName + File.separator +
                                     "manifest");
                FileOutputStream f;
                f = new FileOutputStream(manifestfn, true);
                f.write((fileName + " " + z + "\n").getBytes());
                f.close();
            } catch (Exception e) {
                e.printStackTrace(System.err);
                throw new IOException("embedded pacs saving");
            }
        }
    }

}
