function load_Cerr_Obj2(localFileLocation)
sliceCallBack('init');
global planC stateS;
planC = load(localFileLocation);
planC = planC.planC;
stateS.CERRFile = localFileLocation;
sliceCallBack('load');
