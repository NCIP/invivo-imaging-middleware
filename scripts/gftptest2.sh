#! /bin/sh
set -x

java -cp $(cat $(dirname $0)/../.ant-classpath|xargs echo|sed 's/ /:/g') gov.nih.nci.ivi.utils.gftptest2 ${@+"$@"}
