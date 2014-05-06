ip=`/sbin/ifconfig eth0 | grep 'inet addr:' | /bin/cut -d ':' -f 2 | /bin/cut -d ' ' -f 1`
java -server -Djava.awt.headless=true -Xms128m -Xmx512m -cp ../../DICOMDataService/lib/pixelmed.jar:../../DICOMDataService/lib/hsqldb.jar:../../DICOMDataService/lib/excalibur-bzip2-1.0.jar:../../DICOMDataService/lib/vecmath1.2-1.14.jar:../../DICOMDataService/lib/commons-codec-1.3.jar com.pixelmed.server.DicomAndWebStorageServer ${ip}.properties

