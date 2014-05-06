set IVI_LOCATION=D:\src\ivi
set PACKAGE_HOME=D:\external

set JAVA_HOME=c:\Progra~1\Java\jdk1.5.0_14
set ANT_HOME=%PACKAGE_HOME%\apache-ant-1.6.5

set PATH=%ANT_HOME%\bin;%PATH%
set PATH=%JAVA_HOME%\bin;%PATH%

rem caGRID stuff
set CAGRID_LOCATION=%PACKAGE_HOME%\cagrid-1_2\caGrid
set GLOBUS_LOCATION=%PACKAGE_HOME%\ws-core-4.0.3
set PATH=%PATH%;%GLOBUS_LOCATION%\bin;

rem set LD_LIBRARY_PATH=%JAVA_HOME%\jre\lib\i386;%JAVA_HOME%\jre\lib\i386\xawt;$LD_LIBRARY_PATH

set JAVA_OPTS=-Xmx1000m
set GLOBUS_OPTIONS=-Xmx1000M

set CVS_RSH=ssh

cd %IVI_LOCATION%
