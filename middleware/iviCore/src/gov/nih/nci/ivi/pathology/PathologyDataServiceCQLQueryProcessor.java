package gov.nih.nci.ivi.pathology;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.InitializationException;
import gov.nih.nci.cagrid.data.MalformedQueryException;
import gov.nih.nci.cagrid.data.QueryProcessingException;
import gov.nih.nci.cagrid.data.cql.LazyCQLQueryProcessor;
import gov.nih.nci.cagrid.data.mapping.ClassToQname;
import gov.nih.nci.cagrid.data.mapping.Mappings;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.cagrid.data.utilities.CQLResultsCreationUtil;
import gov.nih.nci.cagrid.data.utilities.ResultsCreationException;
import gov.nih.nci.ivi.fileinfo.FileInfo;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import javax.xml.namespace.QName;


public class PathologyDataServiceCQLQueryProcessor extends LazyCQLQueryProcessor {

	private InputStream configStream;
	private PathologyDataServiceHelper helper;
	private File rootDir;
	private Mappings ClassnameQNameMappings = null;
	
	
	public PathologyDataServiceCQLQueryProcessor() {
		super();
	}

	public Properties getRequiredParameters() {
		Properties props = new Properties();
		props.putAll(PathologyDataServiceHelper.getParameters());
		return props;
	}
	public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
		super.initialize(parameters, wsdd);

		if (this.getConfiguredParameters() == null)
			throw new InitializationException("PathologyDataServiceCQLQueryProcessor: this.getConfiguredParameters() is null");

		try {
			helper = new PathologyDataServiceHelper(this.getConfiguredParameters());
		} catch (IOException e) {
			throw new InitializationException("PathologyDataServiceCQLQueryProcessor: cannot get fsHelper" + e.getMessage());
		}
		if (helper == null)
			throw new InitializationException("PathologyDataServiceCQLQueryProcessor: fsHelper is null");
		this.rootDir = helper.getRootDir();
		if (this.rootDir == null)
			throw new InitializationException("PathologyDataServiceCQLQueryProcessor: rootDir is null");

        createMappings();
	}
        
  
	private void createMappings() {

		if (ClassnameQNameMappings==null)
			ClassnameQNameMappings = new Mappings();
		
		ClassToQname fileInfoMap = new ClassToQname();
		fileInfoMap.setClassName(gov.nih.nci.ivi.fileinfo.FileInfo.class.getName());
		fileInfoMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.fileinfo", "FileInfo").toString());

		ClassnameQNameMappings.setMapping(new ClassToQname[] {fileInfoMap});
	}
	@Override
	public Iterator processQueryLazy(CQLQuery cqlQuery)
			throws MalformedQueryException, QueryProcessingException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CQLQueryResults processQuery(CQLQuery cqlQuery)
			throws MalformedQueryException, QueryProcessingException {
    	List coreResultsList = null;
    	try {
    		coreResultsList = queryPathologyRepository(cqlQuery);
    	} catch (MalformedQueryException e) {
    		throw new MalformedQueryException(e.getMessage());
    	} catch (QueryProcessingException e) {
    		throw new QueryProcessingException(e.getMessage());
    	}
    	CQLQueryResults results = null;
		try {
			results = CQLResultsCreationUtil.createObjectResults(coreResultsList, cqlQuery.getTarget().getName(), ClassnameQNameMappings);
		} catch (ResultsCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	return results;
	}

    private List queryPathologyRepository(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		if (this.rootDir == null || !this.rootDir.exists() || !this.rootDir.canRead())
			throw new QueryProcessingException("PathologyDataServiceCQLQueryProcessor: root dir is not set");
		if (cqlQuery == null)
			throw new MalformedQueryException("PathologyDataServiceCQLQueryProcessor: query is null");
		if (!cqlQuery.getTarget().getAttribute().getName().equals("fileType"))
			throw new MalformedQueryException("PathologyDataServiceCQLQueryProcessor: can only query on fileType attribute");
		if (!cqlQuery.getTarget().getAttribute().getValue().equals("images") &&
			!cqlQuery.getTarget().getAttribute().getValue().equals("codes"))
			throw new MalformedQueryException("PathologyDataServiceCQLQueryProcessor: fileType attribute value can only be images or codes");

		ArrayList<Object> resultList = new ArrayList<Object>();
		FileInfo []fileNames = null;
		
		try {
			fileNames = helper.queryFiles(cqlQuery.getTarget().getAttribute().getValue());
		} catch (IOException e) {
			throw new QueryProcessingException("PathologyDataServiceCQLQueryProcessor: " + e.getMessage());
		}
		if (fileNames != null)
			for (int i = 0; i < fileNames.length; i++) {
				System.out.println("File name: " + fileNames[i].getName());
				System.out.println("File type: " + fileNames[i].getType());
				resultList.add(fileNames[i]);
			}
		else
			System.out.println("Package list is null");
		
		return resultList;  
    }
    
    public static void main(String[] args) {
		String rootDir = "/tmp/pathology";
		Properties params = PathologyDataServiceHelper.getParameters();
		params.put(PathologyDataServiceHelper.ROOT_DIR, rootDir);
		PathologyDataServiceCQLQueryProcessor processor = new PathologyDataServiceCQLQueryProcessor();
		try {
			processor.initialize(params, null);
		} catch (InitializationException e) {
			e.printStackTrace();
		}
		CQLQuery query = null;
		gov.nih.nci.cagrid.cqlquery.Object tar = null;
		gov.nih.nci.cagrid.cqlquery.Attribute attr = null;
		CQLQueryResults results = null;
		CQLQueryResultsIterator iter = null;

		// query for file names
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.fileinfo.FileInfo");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("fileType");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("images");
		tar.setAttribute(attr);
		query.setTarget(tar);

		results = null;
		try {
			results = processor.processQuery(query);
		} catch (MalformedQueryException e) {
			e.printStackTrace();
		} catch (QueryProcessingException e) {
			e.printStackTrace();
		}
		iter = new CQLQueryResultsIterator(results);
		if (iter == null || iter.hasNext() == false)
			System.out.println("no files found");
		while (iter.hasNext()) {
			FileInfo fileInfo = (FileInfo)iter.next();
			System.out.println(fileInfo.getName() + " " + fileInfo.getType());
		}
    }


}