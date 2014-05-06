#!/usr/bin/python
import os, os.path, sys, re, commands, pickle, tempfile, getopt, datetime
import socket, string, random, time, traceback, shutil, popen2

if sys.version_info[0] == 2 and sys.version_info[1] >= 4 \
    or sys.version_info[0] >= 3:
    from subprocess import *

def usage():
    s = 'usage: ' + os.path.basename(sys.argv[0]) + ' [-r <run_for_this_many_seconds>] [-c <Full path of new pixelmed dB>] [-x <Full path of existing dB>] <dicom_dir>\n'
    sys.stderr.write(s)
    sys.exit(1)

run_for = None
perm_db = None
run_only = False

while len(sys.argv)-1 > 0:
    if sys.argv[1]=="-r":
        run_for = int(sys.argv[2])
        sys.argv = sys.argv[:1] + sys.argv[2:]
    elif sys.argv[1] == "-c":
        perm_db = sys.argv[2]
        sys.argv = sys.argv[:1] + sys.argv[2:]
    elif sys.argv[1] == "-x":
        perm_db = sys.argv[2]
        run_only = True
        sys.argv = sys.argv[:1] + sys.argv[2:]
    else:
        break
    sys.argv = sys.argv[:1] + sys.argv[2:]

if run_for:
    if sys.version_info[0] == 2 and sys.version_info[1] >= 4 \
        or sys.version_info[0] >= 3:
        pass
    else:
        sys.stderr.write("ERROR: you need python 2.4 or above\n")

if perm_db:
    if not run_only:
        if not os.path.exists(perm_db):
            os.makedirs(perm_db)
    dbdir = perm_db
else:
    dbdir = tempfile.mkdtemp(suffix=".pixdb")
    
if (not run_only and (len(sys.argv)-1) != 1):
    usage()
if not run_only and not os.access(sys.argv[1],os.F_OK):
    usage()

jardir=os.path.join(os.path.dirname(os.path.dirname(os.path.dirname(os.path.realpath(sys.argv[0])))), 'share/lib/pixelmed')
dict = {'jardir':jardir, 'dbdir':dbdir}
if not run_only:
    dict['inputdir']=sys.argv[1]
    cmd = 'java -Djava.awt.headless=true -Xms128m -Xmx512m -cp "%(jardir)s/pixelmed.jar:%(jardir)s/hsqldb.jar:%(jardir)s/excalibur-bzip2-1.0.jar:%(jardir)s/vecmath1.2-1.14.jar:%(jardir)s/commons-codec-1.3.jar" com.pixelmed.database.RebuildDatabaseFromInstanceFiles PatientStudySeriesConcatenationInstanceModel %(dbdir)s/serverdb %(inputdir)s' % dict
    print cmd
    os.system(cmd)
    print 'dbdir is ', dbdir

cfg_file = """
# Test DicomAndWebStorageServer properties file
#
# Where to store the database support files
#
Application.DatabaseFileName=%(dbdir)s/serverdb
#
# Where to store the images stored in the database
#
# Application.SavedImagesFolderName=%(dbdir)s/serverimages
#
# Dicom.CalledAETitle should be set to whatever this DicomImageViewer application is to
# call itself when accepting an association.
#
Dicom.CalledAETitle=RIDERPACS1
#
# Dicom.CallingAETitle should be set to whatever this DicomImageViewer application is to
# call itself when initiating an association.
#
Dicom.CallingAETitle=RIDERPACS1
#
# WebServer.ListeningPort should be set to whatever port the web server listens
# on for incoming connections.
#
WebServer.ListeningPort=7091
#
# Dicom.ListeningPort should be set to whatever port this DicomImageViewer application is to
# listen on to accept incoming associations.
#
Dicom.ListeningPort=4008
#
# The root URL for the WebServer
#
WebServer.RootURL=
#
# The name of the syylesheet for the WebServer
#
WebServer.StylesheetPath=stylesheet.css
#
# WebServer.DebugLevel should be 0 for no debugging (silent), > 0 for more
# verbose levels of debugging
#
WebServer.DebugLevel=0
#
# Dicom.StorageSCUCompressionLevel determines what types of compressed Transfer Syntaxes are
# proposed:
#	0 = none
#	1 = propose deflate
#	2 = propose deflate and bzip2 (if bzip2 codec is available)
#
Dicom.StorageSCUCompressionLevel=0
#
# Dicom.StorageSCUDebugLevel should be 0 for no debugging (silent), > 0 for more
# verbose levels of debugging
#
Dicom.StorageSCUDebugLevel=0
#
# Dicom.StorageSCPDebugLevel should be 0 for no debugging (silent), > 0 for more
# verbose levels of debugging
#
Dicom.StorageSCPDebugLevel=0
#
# Dicom.QueryDebugLevel should be 0 for no debugging (silent), > 0 for more
# verbose levels of debugging
#
Dicom.QueryDebugLevel=0
#
# Dicom.RemoteAEs is a space or comma separated list of all the available remote AEs;
# each AE may be named anything unique (in this file) without a space or comma; the name
# does not need to be the same as the actual AE title.
#
Dicom.RemoteAEs=defiance vmstor01 vmstor02 localhost cabig1
#
# Each remote AE (listed in Dicom.RemoteAEs) needs to be described by three
# properties:
# Dicom.RemoteAEs.XXXXX.CalledAETitle
# Dicom.RemoteAEs.XXXXX.HostNameOrIPAddress
# Dicom.RemoteAEs.XXXXX.Port
#
# where XXXXX is the name of the AE displayed to the user and used in this file
#
Dicom.RemoteAEs.defiance.CalledAETitle=DEFIANCE
Dicom.RemoteAEs.defiance.HostNameOrIPAddress=140.254.80.41
Dicom.RemoteAEs.defiance.Port=4051
Dicom.RemoteAEs.defiance.QueryModel=STUDYROOT
#
Dicom.RemoteAEs.vmstor01.CalledAETitle=VMSTOR01
Dicom.RemoteAEs.vmstor01.HostNameOrIPAddress=140.254.80.15
Dicom.RemoteAEs.vmstor01.Port=4052
Dicom.RemoteAEs.vmstor01.QueryModel=STUDYROOT
#
Dicom.RemoteAEs.vmstor02.CalledAETitle=VMSTOR02
Dicom.RemoteAEs.vmstor02.HostNameOrIPAddress=140.254.80.16
Dicom.RemoteAEs.vmstor02.Port=4053
Dicom.RemoteAEs.vmstor02.QueryModel=STUDYROOT
#
Dicom.RemoteAEs.localhost.CalledAETitle=LOCALHOST
Dicom.RemoteAEs.localhost.HostNameOrIPAddress=127.0.0.1
Dicom.RemoteAEs.localhost.Port=4054
Dicom.RemoteAEs.localhost.QueryModel=STUDYROOT
#
Dicom.RemoteAEs.cabig1.CalledAETitle=CABIG1
Dicom.RemoteAEs.cabig1.HostNameOrIPAddress=140.254.80.136
Dicom.RemoteAEs.cabig1.Port=4055
Dicom.RemoteAEs.cabig1.QueryModel=STUDYROOT
""" % dict

cfgfn = None
if not run_only:
    if perm_db:
        cfgfn = perm_db + '.cfg'     
    else:
        (tempfd, cfgfn) = tempfile.mkstemp(suffix=".cfg")
        os.close(tempfd)
    open(cfgfn,'w').write(cfg_file)
else:
    cfgfn = perm_db + '.cfg'

dict['cfg_file'] = cfgfn
cmd = 'java -server -Djava.awt.headless=true -Xms128m -Xmx512m -cp "%(jardir)s/pixelmed.jar:%(jardir)s/hsqldb.jar:%(jardir)s/excalibur-bzip2-1.0.jar:%(jardir)s/vecmath1.2-1.14.jar:%(jardir)s/commons-codec-1.3.jar" com.pixelmed.server.DicomAndWebStorageServer %(cfg_file)s' % dict
print cmd

if not run_for:
    os.system(cmd)
else:
    p = Popen(cmd, shell=True, stdin=PIPE, stdout=PIPE, stderr=STDOUT)
    (cout, cin) = (p.stdout, p.stdin)
    cin.close()
    pid = p.pid
    try:
        time.sleep(run_for)
    except KeyboardInterrupt:
        traceback.print_exc()
    os.system('ps')
    print 'killing %d' % (pid)
    import signal
    os.kill(pid, signal.SIGTERM)
    time.sleep(2)
if not perm_db:
    os.remove(cfgfn)
    shutil.rmtree(dbdir)

