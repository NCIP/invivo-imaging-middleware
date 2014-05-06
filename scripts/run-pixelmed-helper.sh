#! /bin/sh
set -x

if [ "$1" = "-g" ]; then
    (echo "stop in gov.nih.nci.ivi.dicom.PixelMedHelper.main"; echo run; cat) | jdb -sourcepath $(dirname $0)/../src -classpath $(cat $(dirname $0)/../.ant-classpath|xargs echo|sed 's/ /:/g') gov.nih.nci.ivi.dicom.PixelMedHelper
else
    java -cp $(cat $(dirname $0)/../.ant-classpath|xargs echo|sed 's/ /:/g') gov.nih.nci.ivi.dicom.PixelMedHelper
fi

