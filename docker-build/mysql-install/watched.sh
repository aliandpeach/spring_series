#!/bin/bash

#拷贝tomcat-watch到 /etc/init.d/
#赋权 chmod +x /etc/init.d/tomcat-watch,
#拷贝watched.sh到/apache-tomcat/bin/

#TOMCAT_HOME=
#JAVA_HOME=

contains() {
    string="$1"
    substring="$2"
    if test "${string#*$substring}" != "$string"
    then
        return 0    # $substring is in $string
    else
        return 1    # $substring is not in $string
    fi
}

#contains "abcd" "e" || echo "abcd does not contain e"

while true
do
  catalina_file_name="catalina.out"
  catalina_last_data_file_name="catalina.$(date "+%Y-%m-%d").out"
  if [ ! -f "${TOMCAT_HOME}/logs/${catalina_file_name}" ]; then
    catalina_file_name=${catalina_last_data_file_name}
  fi

#  if [ ! -f "${TOMCAT_HOME}/logs/${catalina_last_data_file_name}" ]; then
#    echo "$(date "+%Y-%m-%d %H:%M:%S") catalina_last_data_file_name = ${catalina_last_data_file_name} not exist!" >> "${TOMCAT_HOME}"/logs/watched.log
#  fi

  #echo "==========================================================start========================================================================"
  if [ -f "${TOMCAT_HOME}/logs/${catalina_file_name}" ]; then
    log_result=`tail -n500 ${TOMCAT_HOME}/logs/${catalina_file_name} | grep 'GC overhead limit exceeded\|Javaheap space\|java.lang.OutOfMemoryError'`

    if [ ! -z "$log_result" -a "$log_result" != " " ]; then echo "$(date "+%Y-%m-%d %H:%M:%S") $log_result" >> ${TOMCAT_HOME}/logs/watched.log; fi

    if [[ $log_result == *"GC overhead limit exceeded"* || $log_result == *"Javaheap space"* || $log_result == *"java.lang.OutOfMemoryError"* ]]; then
      tomcat_pid=`${JAVA_HOME}/bin/jps|grep Bootstrap|grep -v grep|awk '{print $1}'`
      echo "$(date "+%Y-%m-%d %H:%M:%S") tomcat_pid=${tomcat_pid}," >> ${TOMCAT_HOME}/logs/watched.log

      while [ ! -z "$tomcat_pid" -a "$tomcat_pid" != " " ]; do
        kill -9 ${tomcat_pid}
        sleep 2
        tomcat_pid=`${JAVA_HOME}/bin/jps|grep Bootstrap|grep -v grep|awk '{print $1}'`
      done

      echo "$(date "+%Y-%m-%d %H:%M:%S") mv ${catalina_file_name} backup" >> ${TOMCAT_HOME}/logs/watched.log
      mv ${TOMCAT_HOME}/logs/${catalina_file_name} ${TOMCAT_HOME}/logs/${catalina_file_name}."`date +%Y%m%d%H%M%S`"

      echo "$(date "+%Y-%m-%d %H:%M:%S") `${JAVA_HOME}/bin/jps`" >> ${TOMCAT_HOME}/logs/watched.log
      echo "$(date "+%Y-%m-%d %H:%M:%S") restart tomcat again" >> ${TOMCAT_HOME}/logs/watched.log
      echo "$(date "+%Y-%m-%d %H:%M:%S") catalina_file_name = ${catalina_file_name}" >> "${TOMCAT_HOME}"/logs/watched.log

      if [ -f "${TOMCAT_HOME}/tomcat.pid" ]; then
        echo "$(date "+%Y-%m-%d %H:%M:%S") delete last tomcat.pid" >> "${TOMCAT_HOME}"/logs/watched.log
        rm -f "${TOMCAT_HOME}"/tomcat.pid
      fi

      service tomcat start
      sleep 1
      echo "$(date "+%Y-%m-%d %H:%M:%S") `${JAVA_HOME}/bin/jps`" >> ${TOMCAT_HOME}/logs/watched.log
    fi
  fi
  #echo "==========================================================end================================================================================"
done