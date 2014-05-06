function varargout = gridQRGUI(varargin)
% GRIDQRGUI M-file for gridQRGUI.fig
%      GRIDQRGUI, by itself, creates a new GRIDQRGUI or raises the existing
%      singleton*.
%
%      H = GRIDQRGUI returns the handle to a new GRIDQRGUI or the handle to
%      the existing singleton*.
%
%      GRIDQRGUI('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in GRIDQRGUI.M with the given input arguments.
%
%      GRIDQRGUI('Property','Value',...) creates a new GRIDQRGUI or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before gridQRGUI_OpeningFunction gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to gridQRGUI_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help gridQRGUI

% Last Modified by GUIDE v2.5 28-Oct-2007 18:40:45

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @gridQRGUI_OpeningFcn, ...
                   'gui_OutputFcn',  @gridQRGUI_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before gridQRGUI is made visible.
function gridQRGUI_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to gridQRGUI (see VARARGIN)

% Choose default command line output for gridQRGUI
handles.output = hObject;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes gridQRGUI wait for user response (see UIRESUME)
% uiwait(handles.figure1);


% --- Outputs from this function are returned to the command line.
function varargout = gridQRGUI_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;



function archiveField_Callback(hObject, eventdata, handles)
% hObject    handle to archiveField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of archiveField as text
%        str2double(get(hObject,'String')) returns contents of archiveField as a double


% --- Executes during object creation, after setting all properties.
function archiveField_CreateFcn(hObject, eventdata, handles)
% hObject    handle to archiveField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function institutionField_Callback(hObject, eventdata, handles)
% hObject    handle to institutionField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of institutionField as text
%        str2double(get(hObject,'String')) returns contents of institutionField as a double


% --- Executes during object creation, after setting all properties.
function institutionField_CreateFcn(hObject, eventdata, handles)
% hObject    handle to institutionField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function sponsorIdField_Callback(hObject, eventdata, handles)
% hObject    handle to sponsorIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of sponsorIdField as text
%        str2double(get(hObject,'String')) returns contents of sponsorIdField as a double


% --- Executes during object creation, after setting all properties.
function sponsorIdField_CreateFcn(hObject, eventdata, handles)
% hObject    handle to sponsorIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function protocolIdField_Callback(hObject, eventdata, handles)
% hObject    handle to protocolIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of protocolIdField as text
%        str2double(get(hObject,'String')) returns contents of protocolIdField as a double


% --- Executes during object creation, after setting all properties.
function protocolIdField_CreateFcn(hObject, eventdata, handles)
% hObject    handle to protocolIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function subjectIdField_Callback(hObject, eventdata, handles)
% hObject    handle to subjectIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of subjectIdField as text
%        str2double(get(hObject,'String')) returns contents of subjectIdField as a double


% --- Executes during object creation, after setting all properties.
function subjectIdField_CreateFcn(hObject, eventdata, handles)
% hObject    handle to subjectIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end



function submissionIdField_Callback(hObject, eventdata, handles)
% hObject    handle to submissionIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: get(hObject,'String') returns contents of submissionIdField as text
%        str2double(get(hObject,'String')) returns contents of submissionIdField as a double


% --- Executes during object creation, after setting all properties.
function submissionIdField_CreateFcn(hObject, eventdata, handles)
% hObject    handle to submissionIdField (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: edit controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


% --- Executes on button press in queryButton.
function retrieveButton_Callback(hObject, eventdata, handles)
% hObject    handle to queryButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
       inputQuery.archive = get(handles.archiveField ,'String');
       inputQuery.tapeStandardNumber = '';
       inputQuery.intercomparisonStandard = '';
       inputQuery.institution = get(handles.institutionField ,'String');
       inputQuery.dateCreated = '';
       inputQuery.writer = '';
       inputQuery.sponsorID = get(handles.sponsorIdField ,'String');
       inputQuery.protocolID = get(handles.protocolIdField ,'String');
       inputQuery.subjectID = get(handles.subjectIdField ,'String');
       inputQuery.submissionID = get(handles.submissionIdField ,'String');
       inputQuery.timeSaved = '';
       disp(inputQuery);

       index = get(handles.urlList, 'Value');
       cerrURLList = get(handles.urlList, 'String');
       cerrURL = strcat(cerrURLList{index}, '/wsrf/services/cagrid/CERRDataService');
       disp(cerrURL);

       gridRetrieveCERR2(inputQuery, cerrURL, '/tmp/CERRDOWNLOAD3');
       close;



% --- Executes on button press in retrieveButton.
function queryButton_Callback(hObject, eventdata, handles)
% hObject    handle to retrieveButton (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)


% --- Executes on selection change in urlList.
function urlList_Callback(hObject, eventdata, handles)
% hObject    handle to urlList (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = get(hObject,'String') returns urlList contents as cell array
%        contents{get(hObject,'Value')} returns selected item from urlList


% --- Executes during object creation, after setting all properties.
function urlList_CreateFcn(hObject, eventdata, handles)
% hObject    handle to urlList (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: popupmenu controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end
set(hObject, 'String', {'http://127.0.0.1:8080', 'http://167.165.30.13:8080', 'http://140.254.80.50:50015'});


% --- Executes on selection change in resultSetList.
function resultSetList_Callback(hObject, eventdata, handles)
% hObject    handle to resultSetList (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Hints: contents = get(hObject,'String') returns resultSetList contents as cell array
%        contents{get(hObject,'Value')} returns selected item from resultSetList


% --- Executes during object creation, after setting all properties.
function resultSetList_CreateFcn(hObject, eventdata, handles)
% hObject    handle to resultSetList (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called

% Hint: listbox controls usually have a white background on Windows.
%       See ISPC and COMPUTER.
if ispc && isequal(get(hObject,'BackgroundColor'), get(0,'defaultUicontrolBackgroundColor'))
    set(hObject,'BackgroundColor','white');
end


