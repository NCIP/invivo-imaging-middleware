/**
 * 
 */
package gov.nih.nci.ivi.dsdrepository;

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
import gov.nih.nci.ivi.dsd.URL;
import gov.nih.nci.ivi.dsd._package;
import gov.nih.nci.ivi.dsdrepository.DSDRepositoryHelper;

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

public class DSDRepositoryCQLQueryProcessor extends LazyCQLQueryProcessor {

	private InputStream configStream;
	private DSDRepositoryHelper helper;
	private File rootDir;
	private Mappings ClassnameQNameMappings = null;

	public DSDRepositoryCQLQueryProcessor() {
		super();
	}

	@Override
	public Properties getRequiredParameters() {
		return DSDRepositoryHelper.getParameters();
	}

	@Override
	public void initialize(Properties parameters, InputStream wsdd) throws InitializationException {
		super.initialize(parameters, wsdd);
		configStream = wsdd;//(InputStream) configuration.get(AXIS_WSDD_CONFIG_STREAM);
		// get the config file
		if (this.getConfiguredParameters() == null)
			throw new InitializationException("DSDRepositoryCQLQueryProcessor: this.getConfiguredParameters() is null");
		try {
			helper = new DSDRepositoryHelper(this.getConfiguredParameters());
		} catch (IOException e) {
			throw new InitializationException("DSDRepositoryCQLQueryProcessor: cannot get fsHelper" + e.getMessage());
		}
		if (helper == null)
			throw new InitializationException("DSDRepositoryCQLQueryProcessor: fsHelper is null");
		this.rootDir = helper.getRootDir();
		if (this.rootDir == null)
			throw new InitializationException("DSDRepositoryCQLQueryProcessor: rootDir is null");
		

        createMappings();
        

	}
	private void createMappings() {

		if (ClassnameQNameMappings==null)
			ClassnameQNameMappings = new Mappings();

		ClassToQname containerInfoMap = new ClassToQname();
		containerInfoMap.setClassName(gov.nih.nci.ivi.dsd.ContainerInfo.class.getName());
		containerInfoMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "ContainerInfo").toString());

		ClassToQname packageContainerRequisiteInfoMap = new ClassToQname();
		packageContainerRequisiteInfoMap.setClassName(gov.nih.nci.ivi.dsd.PackageContainerRequisiteInfo.class.getName());
		packageContainerRequisiteInfoMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "PackageContainerRequisiteInfo").toString());

		ClassToQname packageContainerURLMap = new ClassToQname();
		packageContainerURLMap.setClassName(gov.nih.nci.ivi.dsd.PackageContainerURL.class.getName());
		packageContainerURLMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "PackageContainerURL").toString());

		ClassToQname packageDeployedURLMap = new ClassToQname();
		packageDeployedURLMap.setClassName(gov.nih.nci.ivi.dsd.PackageDeployedURL.class.getName());
		packageDeployedURLMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "PackageDeployedURL").toString());

		ClassToQname packageRepositoryURLMap = new ClassToQname();
		packageRepositoryURLMap.setClassName(gov.nih.nci.ivi.dsd.PackageRepositoryURL.class.getName());
		packageRepositoryURLMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "PackageRepositoryURL").toString());

		ClassToQname _packageMap = new ClassToQname();
		_packageMap.setClassName(gov.nih.nci.ivi.dsd._package.class.getName());
		_packageMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "_package").toString());

		ClassToQname URLMap = new ClassToQname();
		URLMap.setClassName(gov.nih.nci.ivi.dsd.URL.class.getName());
		URLMap.setQname(new QName("gme://Middleware.Imaging.caBIG/1.0/gov.nih.nci.ivi.dsd", "URL").toString());

		ClassnameQNameMappings.setMapping(new ClassToQname[] {containerInfoMap, packageContainerRequisiteInfoMap,
				packageContainerURLMap, packageDeployedURLMap, packageRepositoryURLMap, _packageMap, URLMap});

	}

	@Override
	public Iterator processQueryLazy(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		List coreResultsList = queryDSDRepository(cqlQuery);
		return coreResultsList.iterator();
	}

	@Override
	public CQLQueryResults processQuery(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		List coreResultsList = queryDSDRepository(cqlQuery);
		try {
			CQLQueryResults results = null;
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
			System.out.println("processQuery finished at " + new java.util.Date() + "\n");
			return results;
		} catch (QueryProcessingException e) {
			throw new QueryProcessingException(e.getMessage());
		} catch (ResultsCreationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	private List queryDSDRepository(CQLQuery cqlQuery) throws MalformedQueryException, QueryProcessingException {
		if (this.rootDir == null || !this.rootDir.exists() || !this.rootDir.canRead())
			throw new QueryProcessingException("DSDRepositoryCQLQueryProcessor: root dir is not set");
		if (cqlQuery == null)
			throw new MalformedQueryException("DSDRepositoryCQLQueryProcessor: query is null");

		// perform the query
		String[] filelist;
		_package[] packageList = null;
		ArrayList<Object> resultList = new ArrayList<Object>();
		if (cqlQuery.getTarget().getAttribute().getName().equals("availablePackages")) {
			try {
				packageList = helper.queryAvailablePackages();
			} catch (IOException e) {
				throw new QueryProcessingException("DSDRepositoryCQLQueryProcessor: " + e.getMessage());
			}
			if (packageList != null)
				for (int i = 0; i < packageList.length; i++) {
					resultList.add(packageList[i]);
					System.out.println("Available package: " + packageList[i].getIdentifier());
				}
			else
				System.out.println("Package list is null");
		}
		else if (cqlQuery.getTarget().getAttribute().getName().equals("DSDContainerServices")) {
			filelist = null;
			try {
				filelist = helper.queryDSDContainerServices(cqlQuery.getTarget().getAttribute().getValue());
			} catch (IOException e) {
				throw new QueryProcessingException("DSDRepositoryCQLQueryProcessor: " + e.getMessage());
			}
			if (filelist != null && filelist.length > 0)
				for (int i = 0; i < filelist.length; i++) {
					URL containerServiceURL = new URL();
					containerServiceURL.setValue(filelist[i]);
					resultList.add(containerServiceURL);
					System.out.println("DSD container service URL: " + filelist[i]);
				}
		}
		else if (cqlQuery.getTarget().getAttribute().getName().equals("deployedPackages")) {
			filelist = null;
			try {
				filelist = helper.queryDeployedPackages(cqlQuery.getTarget().getAttribute().getValue());
			} catch (IOException e) {
				throw new QueryProcessingException("DSDRepositoryCQLQueryProcessor: " + e.getMessage());
			}
			if (filelist != null && filelist.length > 0)
				for (int i = 0; i < filelist.length; i++) {
					_package packageInfo = new _package();
					packageInfo.setIdentifier(filelist[i]);
					resultList.add(packageInfo);
					System.out.println("deployed package: " + filelist[i]);
				}
		}
		else {
			throw new QueryProcessingException("DSDRepositoryCQLQueryProcessor: invalid attribute name in CQL query: " + cqlQuery.getTarget().getAttribute().getName());
		}

		return resultList;
	}

	public static void main(String[] args) {
		String rootDir = "/tmp/DSDR";
		Properties params = DSDRepositoryHelper.getParameters();
		params.put(DSDRepositoryHelper.ROOT_DIR, rootDir);
		DSDRepositoryCQLQueryProcessor processor = new DSDRepositoryCQLQueryProcessor();
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

		// query for package names
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.dsd._package");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("availablePackages");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("");
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
			System.out.println("no available packages found");
		while (iter.hasNext()) {
			_package packageInfo = (_package)iter.next();
			System.out.println(packageInfo.getIdentifier());
		}

		// query for DSD container service URLs
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.dsd.URL");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("DSDContainerServices");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("http://cagrid01.bmi.ohio-state.edu:8080/wsrf/services/DefaultIndexService");
		tar.setAttribute(attr);
		query.setTarget(tar);

		results = null;
		try {
			results = processor.processQuery(query);
		} catch (MalformedQueryException e1) {
			e1.printStackTrace();
		} catch (QueryProcessingException e1) {
			e1.printStackTrace();
		}
		iter = new CQLQueryResultsIterator(results);
		if (iter == null || iter.hasNext() == false)
			System.out.println("no container services found");
		while (iter.hasNext()) {
			URL containerServiceURL = (URL)iter.next();
			System.out.println(containerServiceURL.getValue());
		}

		// query for packages deployed on a container service
		query = new CQLQuery();
		tar = new gov.nih.nci.cagrid.cqlquery.Object();
		tar.setName("gov.nih.nci.ivi.dsd._package");
		attr = new gov.nih.nci.cagrid.cqlquery.Attribute();
		attr.setName("deployedPackages");
		attr.setPredicate(gov.nih.nci.cagrid.cqlquery.Predicate.fromString("EQUAL_TO"));
		attr.setValue("http://140.254.80.191:8080/wsrf/services/cagrid/DSDContainerService");
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
			System.out.println("no deployed packages found for this service");
		while (iter.hasNext()) {
			_package packageInfo = (_package)iter.next();
			System.out.println(packageInfo.getIdentifier());
		}

	}

}
