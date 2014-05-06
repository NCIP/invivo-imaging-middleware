function outputOfQuery = gridQueryCERR(inputQuery, url)
import edu.osu.bmi.ivi.cerr.CERRObject;
% import gov.nih.nci.ivi.cerrdataservice.client.CERRDataServiceClient.queryCERRDataService;

% Convert the structure to MATLAB Object
inputQueryCERRObject = javaObject('edu.osu.bmi.ivi.cerr.CERRObject');
inputQueryCERRObject.setArchive(inputQuery.archive);
inputQueryCERRObject.setTapeStandardNumber(inputQuery.tapeStandardNumber);
inputQueryCERRObject.setIntercomparisonStandard(inputQuery.intercomparisonStandard);
inputQueryCERRObject.setInstitution(inputQuery.institution);
inputQueryCERRObject.setDateCreated(inputQuery.dateCreated);
inputQueryCERRObject.setWriter(inputQuery.writer);
inputQueryCERRObject.setSponsorID(inputQuery.sponsorID);
inputQueryCERRObject.setProtocolID(inputQuery.protocolID);
inputQueryCERRObject.setSubjectID(inputQuery.subjectID);
inputQueryCERRObject.setSubmissionID(inputQuery.submissionID);
inputQueryCERRObject.setTimeSaved(inputQuery.timeSaved);

% Run the query using the interface code
matlabInterface = javaObject('gov.nih.nci.ivi.cerrdataservice.client.CERRMatlabInterface')
outputOfQuery = matlabInterface.queryCERRDataService(inputQueryCERRObject, url);
% outputOfQuery = CERRDataServiceClient.queryCERRDataService(inputQueryCERRObject, url);


% outputOfQuery = MatlabInterfaceForCERR.queryCERRDataService(inputQueryCERRObject, url);

end
