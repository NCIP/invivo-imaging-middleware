package gov.nih.nci.ivi.pathologydataservice.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public interface PathologyDataServiceI {

  public org.oasis.wsrf.properties.GetMultipleResourcePropertiesResponse getMultipleResourceProperties(org.oasis.wsrf.properties.GetMultipleResourceProperties_Element params) throws RemoteException ;

  public org.oasis.wsrf.properties.GetResourcePropertyResponse getResourceProperty(javax.xml.namespace.QName params) throws RemoteException ;

  public org.oasis.wsrf.properties.QueryResourcePropertiesResponse queryResourceProperties(org.oasis.wsrf.properties.QueryResourceProperties_Element params) throws RemoteException ;

  /**
   * The standard caGrid Data Service query method.
   *
   * @param cqlQuery
   *	The CQL query to be executed against the data source.
   * @return The result of executing the CQL query against the data source.
   * @throws QueryProcessingException
   *	Thrown when an error occurs in processing a CQL query
   * @throws MalformedQueryException
   *	Thrown when a query is found to be improperly formed
   */
  public gov.nih.nci.cagrid.cqlresultset.CQLQueryResults query(gov.nih.nci.cagrid.cqlquery.CQLQuery cqlQuery) throws RemoteException, gov.nih.nci.cagrid.data.faults.QueryProcessingExceptionType, gov.nih.nci.cagrid.data.faults.MalformedQueryExceptionType ;

  public int view(gov.nih.nci.ivi.fileinfo.ViewParameters viewParams) throws RemoteException ;

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieve(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException ;

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference submit() throws RemoteException ;

  public void delete(gov.nih.nci.ivi.fileinfo.FileInfo fileInfo) throws RemoteException ;

  public void execute(gov.nih.nci.ivi.fileinfo.ExecutionParameters executionParameters) throws RemoteException ;

  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieveThumbnail(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException ;

  public int getWidth(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException ;

  public int getHeight(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException ;

}

