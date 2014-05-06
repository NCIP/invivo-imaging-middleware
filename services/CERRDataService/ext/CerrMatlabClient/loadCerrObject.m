% Working Script
% delete(gcf);clear all,close all,clc
% sliceCallBack('init');
% global planC
% global stateS
% path = '/data/RT_Object/CERR Object/CERRPlan.mat';
% planC = load(path);
% planC = planC.planC;
% stateS.CERRFile = '/data/RT_Object/CERR Object/CERRPlan.mat';
% sliceCallBack('load');
% Working Script

function loadCerrObject(localFileLocation)
sliceCallBack('init');
global planC
global stateS
matFileWildcard = [localFileLocation '/*.mat'];
matFiles = dir(matFileWildcard);
numFiles = size(matFiles);
    for i = 1:1:numFiles(1, 1)
        path = [localFileLocation '/' matFiles(i, 1).name];
        planC = load(path);
        planC = planC.planC;
        stateS.CERRFile = path;
        sliceCallBack('load')
    end
end
