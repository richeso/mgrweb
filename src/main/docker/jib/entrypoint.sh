#!/bin/sh

echo "10.163.169.55    rcstest55" >> /etc/hosts
/opt/mapr/server/configure.sh -N rcs.cluster.com -c -C rcstest55:7222
###cp /app/resources/config/mapr-clusters.conf /opt/mapr/conf/mapr-clusters.conf
###sleep 1d
##exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp /app/resources/:/app/classes/:/app/libs/* "com.mapr.mgrweb.MswebApp"  "$@"
##exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp $( cat /app/jib-classpath-file ) $( cat /app/jib-main-class-file ) "$@"
exec java ${JAVA_OPTS} -noverify -XX:+AlwaysPreTouch -Djava.security.egd=file:/dev/./urandom -cp $( cat /app/jib-classpath-file ) com.mapr.mgrweb.MgrwebApp "$@"
