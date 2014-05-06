function gridRetrieveCERR2(inputQuery, url, localLocation)
import edu.osu.bmi.ivi.cerr.CERRObject;

% disp(inputQuery);
% disp(url);
%     if (isstruct(inputQuery))
%        inputQuery.archive = '';
%        inputQuery.tapeStandardNumber = '';
%        inputQuery.intercomparisonStandard = '';
%        inputQuery.institution = '';
%        inputQuery.dateCreated = '';
%        inputQuery.writer = '';
%        inputQuery.sponsorID = '';
%        inputQuery.protocolID = '';
%        inputQuery.subjectID = '';
%        inputQuery.submissionID = '';
%        inputQuery.timeSaved = '';
%     end    
% % Convert the structure to MATLAB Object
% inputQueryCERRObject = javaObject('edu.osu.bmi.ivi.cerr.CERRObject');
% inputQueryCERRObject.setArchive(inputQuery.archive);
% inputQueryCERRObject.setTapeStandardNumber(inputQuery.tapeStandardNumber);
% inputQueryCERRObject.setIntercomparisonStandard(inputQuery.intercomparisonStandard);
% inputQueryCERRObject.setInstitution(inputQuery.institution);
% inputQueryCERRObject.setDateCreated(inputQuery.dateCreated);
% inputQueryCERRObject.setWriter(inputQuery.writer);
% inputQueryCERRObject.setSponsorID(inputQuery.sponsorID);
% inputQueryCERRObject.setProtocolID(inputQuery.protocolID);
% inputQueryCERRObject.setSubjectID(inputQuery.subjectID);
% inputQueryCERRObject.setSubmissionID(inputQuery.submissionID);
% inputQueryCERRObject.setTimeSaved(inputQuery.timeSaved);

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
matlabInterface = javaObject('gov.nih.nci.ivi.cerrdataservice.client.CERRMatlabInterface');
retrievedFile = javaObject('java.lang.String', matlabInterface.retrieveCERRDataService(inputQueryCERRObject, url, localLocation));
%load_Cerr_Obj2(strcat(localLocation, '/CERRPlan.mat'));
if(retrievedFile.length ~= 0)
    load_Cerr_Obj2(strcat(localLocation, '/', char(retrievedFile)));
else
    disp('No data matching query criteria was found on the grid')
end

end
