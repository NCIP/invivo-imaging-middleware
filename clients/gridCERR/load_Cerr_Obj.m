%function load_Cerr_Obj(localFileLocation)
global planC stateS;
sliceCallBack('init');

%matFileWildcard = [localFileLocation '/*.mat'];
%matFiles = dir(matFileWildcard);
%numFiles = size(matFiles);
%    for i = 1:1:numFiles(1, 1)
%        path = [localFileLocation '/' matFiles(i, 1).name];
%        planC = load(path);
%        planC = planC.planC;
%        stateS.CERRFile = '/data/RT_Object/CERR Object/CERRPlan.mat';
%        sliceCallBack('load');
%    end
%end
%planC = load(path);

%planC = planC.planC;

%stateS.CERRFile = '/data/RT_Object/CERR Object/CERRPlan.mat';

%sliceCallBack('load');
        

%Get the name of the local file
%ASHISH for Testing path = '/data/RT_Object/CERR Object/CERRPlan.mat';

%path = localFileLocation;
path = '/data/RT_Object/CERR Object/CERRPlan.mat';
%global planC 

%global stateS

planC = load(path);

planC = planC.planC;

stateS.CERRFile = '/data/RT_Object/CERR Object/CERRPlan.mat';

sliceCallBack('load');

% This version Works BEGIN HERE
% function load_Cerr_Obj2(localFileLocation)
% sliceCallBack('init');
% global planC stateS;
% planC = load(localFileLocation);
% planC = planC.planC;
% stateS.CERRFile = localFileLocation;
% sliceCallBack('load');
% This version Works STOP HERE
