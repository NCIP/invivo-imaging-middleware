 Welcome to the Invivo Imaging Middleware Project!
=========================

The In Vivo Imaging Middleware (IVIM) (better known as IVI Middleware) is an Open Source project the provides the functionalities of interoperability between DICOM and the caGrid. IVIM development is driven by common use cases from radiology and pathology for common activities, such as central review in image-based clinical trials and data and application sharing in secure high-performance environments. IVIM is designed to provide grid-based, federated access to existing DICOM-based data repositories and analytical resources and to facilitate development of grid-enabled in vivo imaging applications. IVIM is layered over the grid infrastructure provided by caGrid, leveraging caGrid's core services, toolkits, and wizards for the development and deployment of community-provided services and APIs for building client applications. IVIM is compatible with caGrid 1.2.
IVIM includes:
  *  A DICOM data service for grid-based access to DICOM PACS. Provides a two-way interface between caGrid and DICOM. Uses the NCIA DICOM data model to provide a caGrid query, retrieve, and submit interface to PACS. Supports DICOM C_FIND, C_GET, C_MOVE, and C_STORE commands. The service leverages caGrid for service discovery, remote access, and security (authentication and authorization).
  *	An AIM data service to store AIM annotations, providing a caGrid query, retrieve and submit interface to an XML database that stores AIM objects
  *	A generic image data service to store generic image data types, allowing grid clients to query, retrieve, and submit generic image files stored on the file system. This service can be extended to use a database that contains image metadata.
  *	VirtualPACS, a grid client application for the DICOM Data Service. VirtualPACS appears as a PACS server to a radiologist's DICOM workstation and acts as middleware that allows the workstation to interact with DICOM grid services.
  *	Introduce Extensions, a series of extensions to the caGrid Introduce toolkit and helper classes that allow developers to interface with IVIM grid services
  *	Security Extensions, a role-based data access control mechanism to control the query and retrieval of objects exposed by the DICOM Data Service. It leverages the hierarchy in the DICOM information model. The security enhancements to IVIM build on GAARDS.  
  *	Federated Query Processing, a processor and client that extend the caGrid Federated Query Processor to query and make joins across multiple domain models, for example, "Find all DICOM images where the RECIST length criterion is greater than a certain value". By using the FQP client, a user can formulate queries that encompass both DICOM and AIM attributes and execute these queries to retrieve all DICOM and AIM objects that satisfy the query. The query is executed on a federation of multiple grid services and add service-level and data-level authentication and authorization to secure access to services and data exposed by grid services.


It is written in Java using caGrid's core services, toolkits, and wizards technologies.
The goal of IVIM is to provide interoperability between DICOM and the Grid. It is designed to provide Gridbased
federated access to existing DICOM-based data repositories and analytical resources and to facilitate
development of Grid-enabled in vivo imaging applications in caBIG.

In Vivo Imaging Middleware (IVIM) is distributed under the BSD 3-Clause License. 
Please see the NOTICE and LICENSE files for details.

You will find more details about In Vivo Imaging Middleware (IVIM) in the following links:
  * [Community Wiki] (https://wiki.nci.nih.gov/display/IVIM/In+Vivo+Imaging+Middleware+-+IVIM+and+Virtual+PACS)
  * [Issue Tracker ] (https://tracker.nci.nih.gov/browse/IVIM)
  * [Code Repository] (https://github.com/NCIP/invivo-imaging-middleware)

Please join us in further developing and improving In Vivo Imaging Middleware (IVIM).
