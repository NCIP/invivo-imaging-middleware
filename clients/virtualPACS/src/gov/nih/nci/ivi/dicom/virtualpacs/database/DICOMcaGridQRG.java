package gov.nih.nci.ivi.dicom.virtualpacs.database;

import java.rmi.RemoteException;
import java.util.Iterator;
import java.util.Vector;

import javax.xml.namespace.QName;

import org.apache.axis.types.URI.MalformedURIException;
import org.globus.wsrf.encoding.ObjectSerializer;
import org.globus.wsrf.encoding.SerializationException;

import EDU.oswego.cs.dl.util.concurrent.LinkedQueue;
import EDU.oswego.cs.dl.util.concurrent.PooledExecutor;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeFactory;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTagAttribute;
import com.pixelmed.dicom.DicomException;
import com.pixelmed.dicom.TagFromName;
import com.pixelmed.query.QueryResponseGenerator;

import gov.nih.nci.cagrid.cqlquery.CQLQuery;
import gov.nih.nci.cagrid.cqlresultset.CQLQueryResults;
import gov.nih.nci.cagrid.data.client.DataServiceClient;
import gov.nih.nci.cagrid.data.utilities.CQLQueryResultsIterator;
import gov.nih.nci.ivi.dicom.DICOM2CQL;
import gov.nih.nci.ivi.dicom.Model2DICOM;
import gov.nih.nci.ivi.dicom.modelmap.ModelMap;
import gov.nih.nci.ivi.dicom.modelmap.ModelMapException;

/**
 * @author Ashish Sharma
 * @author Tony Pan
 * @version 1.2
 */
public class DICOMcaGridQRG implements QueryResponseGenerator {

	// TCP: the result and result iterators should not be 
	// shared since the factory takes care of new instances
	private volatile Vector results = new Vector();
	private Iterator resultIter = null;
	private String[] dataServiceUrls;
	private ModelMap mmap;

	public DICOMcaGridQRG(String[] dicomDataServiceUrls, ModelMap map) {
		System.err.println("DICOMcaGridQRG");
		this.dataServiceUrls = dicomDataServiceUrls;
		this.mmap = map;
	}

	public boolean allOptionalKeysSuppliedWereSupported() {
		return false;
	}

	public void close() {
	}

	public String getErrorComment() {
		System.err
				.println("QueryResponseGenerator getErrorComment not yet implemented");
		return null;
	}

	public AttributeTagAttribute getOffendingElement() {
		System.err
				.println("QueryResponseGenerator getOffendingElement not yet implemented");
		return null;
	}

	public int getStatus() {
		System.err
				.println("QueryResponseGenerator getStatus not yet implemented");
		return 0;
	}

	public AttributeList next() {
		// Result Set Iterator;
		if(this.resultIter.hasNext()) {
			AttributeList result = new AttributeList();

			Object queryResult = this.resultIter.next();
			Model2DICOM m2d = new Model2DICOM(mmap);
			result = m2d.model2DICOM(queryResult, result);
			//System.out.println(" result = " + queryResult);

			Attribute a = null;
			try {
				a = AttributeFactory.newAttribute(TagFromName.QueryRetrieveLevel,
						this.mmap.getDicomDict().getValueRepresentationFromTag(TagFromName.QueryRetrieveLevel));
				a.addValue(this.mmap.getRetrieveLevel(this.mmap.getInformationEntityFromModelClass(queryResult.getClass())));
			} catch (DicomException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ModelMapException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			result.put(TagFromName.QueryRetrieveLevel, a);
			return result;
		}
		return null;
	}

	public void performQuery(String SOPClassUID, AttributeList filter,
			boolean arg2) {
		System.out.println("Executing: DICOMcaGridQRG.performQuery()");
		System.out.println(filter.toString(mmap.getDicomDict()));

		// Step 1: Convert DICOM to CQL
		final CQLQuery dicomAsCqlQuery = DICOM2CQL.convert2CQL(mmap, filter);

		// Check the CQL conversion
		try {
			String TEMP = ObjectSerializer.toString(dicomAsCqlQuery, new QName(
					"http://CQL.caBIG/1/gov.nih.nci.cagrid.CQLQuery",
					"CQLQuery"));
			System.out.println(TEMP);
		} catch (SerializationException e1) {
			e1.printStackTrace();
		}

		// Run CQL qeury on remote site
		// TODO expose the caGrid DICOM Data Services as PACSs with different
		// AETitles <- dynamicaly. also create a "all", as well as user
		// specified subset..
		// all -> the AETitle is VIRTUALPACS -> and will hit all caGRID PACSs as
		// one single PACS
		// some

		int poolSize = Math.min(this.dataServiceUrls.length, 4);
		PooledExecutor workerPool = new PooledExecutor(new LinkedQueue(),
				poolSize);
		workerPool.setMinimumPoolSize(poolSize);
		workerPool.setMaximumPoolSize(poolSize);

		this.results.clear();

		for (int i = 0; i < this.dataServiceUrls.length; i++) {

			ExecutionThread st = new ExecutionThread(this.dataServiceUrls[i],
					dicomAsCqlQuery, this.results);
			try {
				workerPool.execute(st);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}

		// wait for finish
		workerPool.shutdownAfterProcessingCurrentlyQueuedTasks();
		try {
			workerPool.awaitTerminationAfterShutdown();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		if (this.results.size() == 0) {
			System.err
					.println("No results found. Please modify your query and resubmit.");
		}
		this.resultIter = this.results.iterator();
		// Convert result set into DICOM which will be iterated through by
		// this.next()
	}

	class ExecutionThread extends Thread {

		private String dataServiceURL;
		private CQLQuery query;
		private Vector results;

		public ExecutionThread(String dataServiceURL, CQLQuery query, Vector results) {
			this.dataServiceURL = dataServiceURL;
			this.query = query;
			this.results = results;
		}

		public void run() {
			CQLQueryResults dicomQueryResult = null;

				try {
					DataServiceClient dicomclient = new DataServiceClient(this.dataServiceURL);
					dicomQueryResult = dicomclient.query(this.query);
				} catch (MalformedURIException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			if (dicomQueryResult != null) {
				CQLQueryResultsIterator iter = new CQLQueryResultsIterator(dicomQueryResult);
				while (iter.hasNext()) {
					this.results.add(iter.next());
				}
			}
		}
	}

}
