/**
 * 
 */
package gov.nih.nci.ivi.filesystem;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.data.utilities.ResultsCreationException;
//import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsUtil;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.ivi.genericimage.ImageDataItem;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;

import org.globus.wsrf.encoding.DeserializationException;
import org.globus.wsrf.encoding.ObjectDeserializer;
import org.xml.sax.InputSource;

/**
 * @author tpan
 *
 */
public class FSCQLQueryProcessor extends LazyCQLQueryProcessor {

	private InputStream configStream;
	private FSHelper fsHelper;
	private File rootDir;
	private Mappings ClassnameQNameMappings;

	public FSCQLQueryProcessor() {
		super();
	}

	@Override
	public Properties getRequiredParameters() {
		return FSHelper.getParameters();
	}

	@Override
	public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
		super.initialize(parameters, wsdd);
		configStream = wsdd;//(InputStream) configuration.get(AXIS_WSDD_CONFIG_STREAM);		
		// get the config file
		if (this.getConfiguredParameters() == null)
			throw new InitializationException("FSCQLQueryProcessor: this.getConfiguredParameters() is null");
		try {
			fsHelper = new FSHelper(this.getConfiguredParameters());
		} catch (IOException e) {
			throw new InitializationException("FSCQLQueryProcessor: cannot get fsHelper" + e.getMessage());
		}
		if (fsHelper == null)
			throw new InitializationException("FSCQLQueryProcessor: fsHelper is null");
		this.rootDir = fsHelper.getRootDir();
		if (this.rootDir == null)
			throw new InitializationException("FSCQLQueryProcessor: rootDir is null");
		

        createMappings();
        

	}
	private void createMappings() {

		if (ClassnameQNameMappings==null)
			ClassnameQNameMappings = new Mappings();

		ClassToQname channelMap = new ClassToQname();
		channelMap.setClassName(gov.nih.nci.ivi.genericimage.Channel.class.getName());
		channelMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "Channel").toString());

		ClassToQname channelResolutionCollectionMap = new ClassToQname();
		channelResolutionCollectionMap.setClassName(gov.nih.nci.ivi.genericimage.ChannelResolutionCollection.class.getName());
		channelResolutionCollectionMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "ChannelResolutionCollection").toString());

		ClassToQname chunkMap = new ClassToQname();
		chunkMap.setClassName(gov.nih.nci.ivi.genericimage.Chunk.class.getName());
		chunkMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "Chunk").toString());

		ClassToQname dataItemMap = new ClassToQname();
		dataItemMap.setClassName(gov.nih.nci.ivi.genericimage.DataItem.class.getName());
		dataItemMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "DataItem").toString());

		ClassToQname dataItemChunkCollectionMap = new ClassToQname();
		dataItemChunkCollectionMap.setClassName(gov.nih.nci.ivi.genericimage.DataItemChunkCollection.class.getName());
		dataItemChunkCollectionMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "DataItemChunkCollection").toString());

		ClassToQname imageMap = new ClassToQname();
		imageMap.setClassName(gov.nih.nci.ivi.genericimage.Image.class.getName());
		imageMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "Image").toString());

		ClassToQname imageChannelCollectionMap = new ClassToQname();
		imageChannelCollectionMap.setClassName(gov.nih.nci.ivi.genericimage.ImageChannelCollection.class.getName());
		imageChannelCollectionMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "ImageChannelCollection").toString());

		ClassToQname imageDataItemMap = new ClassToQname();
		imageDataItemMap.setClassName(gov.nih.nci.ivi.genericimage.ImageDataItem.class.getName());
		imageDataItemMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "ImageDataItem").toString());

		ClassToQname resolutionMap = new ClassToQname();
		resolutionMap.setClassName(gov.nih.nci.ivi.genericimage.Resolution.class.getName());
		resolutionMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "Resolution").toString());

		ClassToQname resolutionTileCollectionMap = new ClassToQname();
		resolutionTileCollectionMap.setClassName(gov.nih.nci.ivi.genericimage.ResolutionTileCollection.class.getName());
		resolutionTileCollectionMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "ResolutionTileCollection").toString());

		ClassToQname tileMap = new ClassToQname();
		tileMap.setClassName(gov.nih.nci.ivi.genericimage.Tile.class.getName());
		tileMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "Tile").toString());

		ClassToQname timeSeriesMap = new ClassToQname();
		timeSeriesMap.setClassName(gov.nih.nci.ivi.genericimage.TimeSeries.class.getName());
		timeSeriesMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "TimeSeries").toString());

		ClassToQname annotationMap = new ClassToQname();
		annotationMap.setClassName(gov.nih.nci.ivi.genericimage.TimeSeriesVolumeCollection.class.getName());
		annotationMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "ImageDataItem").toString());

		ClassToQname volumeMap = new ClassToQname();
		volumeMap.setClassName(gov.nih.nci.ivi.genericimage.Volume.class.getName());
		volumeMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "Volume").toString());

		ClassToQname volumeImageCollectionMap = new ClassToQname();
		volumeImageCollectionMap.setClassName(gov.nih.nci.ivi.genericimage.VolumeImageCollection.class.getName());
		volumeImageCollectionMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.genericimage", "VolumeImageCollection").toString());

		ClassnameQNameMappings.setMapping(new ClassToQname[] {channelMap, channelResolutionCollectionMap, chunkMap,
				dataItemMap, dataItemChunkCollectionMap, imageMap, imageChannelCollectionMap, imageDataItemMap,
				resolutionMap, resolutionTileCollectionMap, tileMap, timeSeriesMap, annotationMap, volumeMap,
				volumeImageCollectionMap});

	}

	@Override
	public Iterator processQueryLazy(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		List coreResultsList = queryFS(cqlQuery);
		return coreResultsList.iterator();
	}

	@Override
	public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		List coreResultsList = queryFS(cqlQuery);
		CQLQueryResults results = null;
		try {
			if(cqlQuery.getQueryModifier() == null)
			{
				results = CQLResultsCreationUtil.createObjectResults(coreResultsList, cqlQuery.getTarget().getName(), ClassnameQNameMappings);
			}
			else if(cqlQuery.getQueryModifier().isCountOnly())
			{
				System.out.println("Processing query for result count");
				results = CQLResultsCreationUtil.createCountResults(coreResultsList.size(), cqlQuery.getTarget().getName());
			}
			else if(cqlQuery.getQueryModifier().getDistinctAttribute() != null)
			{
				System.out.println("Processing query for distinct attribute values");
				results = CQLResultsCreationUtil.createObjectResults(coreResultsList, cqlQuery.getTarget().getName(), ClassnameQNameMappings);
			}
			else if (cqlQuery.getQueryModifier().getAttributeNames() != null) {
				System.out.println("Processing query for attribute names is not supported yet");
				throw new QueryProcessingException("Processing query for attribute names is not supported yet");
			}
	        System.out.println("dicom processQuery finished at " + new java.util.Date() + "\n");
			return results;
		} catch (ResultsCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * this function returns a list of imagetile objects (only supports query at this level of generic image for now)
	 * 
	 * @param cqlQuery
	 * @return
	 * @throws MalformedQueryException
	 * @throws QueryProcessingException
	 */
	private List queryFS(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		if (this.rootDir == null || !this.rootDir.exists() || !this.rootDir.canRead())
			throw new QueryProcessingException("FSCQLQueryProcessor: root dir is not set");
		if (cqlQuery == null)
			throw new MalformedQueryException("FSCQLQueryProcessor: query is null");
		String className = cqlQuery.getTarget().getName();

		// transform cql to xpath
		String path = null;
		try {
			path = CQL2FS.cqlToPath(cqlQuery);
		} catch (MalformedQueryException e) {
			throw new MalformedQueryException(e.getMessage());
		}

		// perform the query
		String[] filelist = null;
		try {
			filelist = fsHelper.queryFSByPath(path);
		} catch (IOException e) {
			throw new QueryProcessingException("FSCQLQueryProcessor: " + e.getMessage());
		}
		ArrayList<Object> resultList = new ArrayList<Object>();
		if (filelist != null && filelist.length > 0) {
			for (int i = 0; i < filelist.length; i++) {
				System.err.println(filelist[i]);
				File tmpFile = new File(filelist[i]);
				if (tmpFile.isDirectory())
					continue;
				// get the file as binary
				byte[] imageContent = null;
				try {
					FileInputStream fis = new FileInputStream(filelist[i]);
					imageContent = new byte[fis.available()];
					fis.read(imageContent);
					fis.close();
				} catch (FileNotFoundException e) {
					throw new QueryProcessingException("FSCQLQueryProcessor: file not found " + e);
				} catch (IOException e) {
					throw new QueryProcessingException("FSCQLQueryProcessor: can't read image " + e);
				}
				if (imageContent == null || imageContent.length == 0) {
					continue;
				}

				// create the image object
				ImageDataItem image = new ImageDataItem();
				image.setMinimumX(0);
				image.setMaximumX(-1);
				image.setMinimumY(0);
				image.setMaximumY(-1);
				image.setNumberOfChunks(1);
				image.setName(filelist[i]);
				resultList.add(image);
			}
		}
		System.err.println("number of result images = " + resultList.size());
		return resultList;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Properties params = FSHelper.getParameters();
		params.put(FSHelper.ROOT_DIR, "/tmp/GIdata" );
		FSCQLQueryProcessor processor = new FSCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			e.printStackTrace();
		}
		String path = "../iviCore/resources/filesystem";
		String filename = path + File.separator + "genericImageFSCQL3.xml";
		try {
			InputSource queryInput = new InputSource(new FileReader(filename));
			CQLQuery query = (CQLQuery) ObjectDeserializer.deserialize(queryInput, CQLQuery.class);
			CQLQueryResults results = processor.processQuery(query);
			System.out.println("count = " + results.getObjectResult().length);
			for (int i = 0; i < results.getObjectResult().length; i++ ) {
				try {
					System.out.println(results.getObjectResult(i).get_any()[0].getAsString());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			CQLQueryResultsIterator iter = new CQLQueryResultsIterator(results);
			while (iter.hasNext()) {
				//TODO: failed right here.  cannot deserialize the result.  says invalid element Chunk.  (NO) suspect it has to do with complex types that do not have a "public" element for introduce.
				ImageDataItem tile = (ImageDataItem)iter.next();
				System.out.println(tile.getName());
			}
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryProcessingException e) {
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (DeserializationException e) {
			e.printStackTrace();
		}
	}

}
