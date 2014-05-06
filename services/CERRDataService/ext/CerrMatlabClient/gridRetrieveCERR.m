function gridRetrieveCERR2(inputQuery, url, localLocation)
import edu.osu.bmi.ivi.cerr.CERRObject;
% import gov.nih.nci.ivi.cerrdataservice.client.CERRDataServiceClient.queryCERRDataService;

disp(inputQuery);
disp(url);
    if (isstruct(inputQuery))
       inputQuery.archive = '';
       inputQuery.tapeStandardNumber = '';
       inputQuery.intercomparisonStandard = '';
       inputQuery.institution = '';
       inputQuery.dateCreated = '';
       inputQuery.writer = '';
       inputQuery.sponsorID = '';
       inputQuery.protocolID = '';
       inputQuery.subjectID = '';
       inputQuery.submissionID = '';
       inputQuery.timeSaved = '';
    end    
% Convert the structure to MATLAB Object
inputQueryCERRObject = javaObject('edu.osu.bmi.ivi.cerr.CERRObject');
%LOCAT
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
matlabInterface.retrieveCERRDataService(inputQueryCERRObject, url, localLocation)
% CERRDataServiceClient.retrieveCERRDataService(inputQueryCERRObject, url, localLocation)


load_Cerr_Obj2(strcat(localLocation, '/CERRPlan.mat'));

end
