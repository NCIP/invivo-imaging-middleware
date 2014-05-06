package gov.nih.nci.ivi.dicomdataservice.common;

import java.rmi.RemoteException;

/** 
 * This class is autogenerated, DO NOT EDIT.
 * 
 * This interface represents the API which is accessable on the grid service from the client. 
 * 
 * @created by Introduce Toolkit version 1.2
 * 
 */
public interface DICOMDataServiceI {

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

  /**
   * retrieve DICOM data using caGrid Transfer Service
   *
   * @param cQLQuery
   */
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference retrieveDICOMData(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException ;

  /**
   * upload DICOM data using caGrid Transfer Service
   *
   * @param submissionInformation
   */
  public org.cagrid.transfer.context.stubs.types.TransferServiceContextReference submitDICOMData(submission.SubmissionInformation submissionInformation) throws RemoteException ;

  public gov.nih.nci.cagrid.enumeration.stubs.response.EnumerationResponseContainer retrieveDICOMDataProgressively(gov.nih.nci.cagrid.cqlquery.CQLQuery cQLQuery) throws RemoteException ;

}

