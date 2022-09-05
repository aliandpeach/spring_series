#!/bin/bash

current_dir="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
username=$(whoami)
echo "step1: check install environment"
#tomcat_pid=$("${JAVA_HOME}"/bin/jps|grep Bootstrap|grep -v grep|awk '{print $1}')
mysql_pid=$(ps -ef |grep -w mysql|grep -v grep -c)
if [ "${mysql_pid}" -gt 0 ];then
    echo "mysql process has been already exist,this install will exit! Please uninstall before installing this program!"
    exit 1
else
    echo "Installation environment inspection passed,this install program  will  begin ..."
fi

echo "step2: waiting for database installation ..."
if [ ! -d "${current_dir}/run" ];then
   mkdir -p  "${current_dir}/run"
fi
tar -zxvf ./tools/mysql-5.7.12-linux-glibc2.5-x86_64.tar.gz -C "${current_dir}"/run

setupreg=$(pwd |sed 's/\//\\\\\//g')

sed "s/\/run/${setupreg}\/run/g"  "${current_dir}"/Base/tools/mysql/my-default.cnf > "${current_dir}"/Base/tools/mysql/my-default.cnf_out
sed "s/\/run/${setupreg}\/run/g"  "${current_dir}"/Base/tools/mysql/mysql.server > "${current_dir}"/Base/tools/mysql/mysql.server_out

chmod u+x "${current_dir}"/Base/tools/mysql/mysql.server_out

\cp -ra "${current_dir}"/tools/mysql/my-default.cnf_out "${current_dir}"/run/mysql/my.cnf
\cp -r "${current_dir}"/tools/mysql/mysql.server_out "${current_dir}"/run/mysql/support-files/mysql.server
\cp -ra "${current_dir}"/tools/mysql/my-default.cnf_out /etc/my.cnf
\cp -r "${current_dir}"/tools/mysql/mysql.server_out /etc/init.d/mysqld

echo "step3:waiting for database service to start ..."
cd "${current_dir}"/run/mysql
mkdir -p "${current_dir}"/Spinfo/mysql/data
if [[ ${username} != 'root' ]];then
    bin/mysqld \
    --defaults-file=${current_dir}/Spinfo/mysql/my.cnf \
    --initialize \
    --socket=${current_dir}/Spinfo/mysql/mysql.sock \
    --basedir=${current_dir}/Spinfo/mysql \
    --datadir=${current_dir}/Spinfo/mysql/data \
    --user=${current_user}
	chown ${username}:${group} -R ${current_dir}/Spinfo/mysql
else
    bin/mysqld \
    --defaults-file=${current_dir}/Spinfo/mysql/my.cnf \
    --initialize \
    --socket=${current_dir}/Spinfo/mysql/mysql.sock \
    --basedir=${current_dir}/Spinfo/mysql \
    --datadir=${current_dir}/Spinfo/mysql/data
    chown -R mysql:mysql ${current_dir}/Spinfo/mysql
fi
service mysqld start

echo "step4:update database password ..."
str=`cat ${current_dir}/Spinfo/mysql/error.log | grep @localhost`
password_init=`echo ${str#*@localhost:}`
sleep 1

if [[ ! -f "${current_dir}/Spinfo/mysql/mysql.sock" ]]; then
 echo "${current_dir}/Spinfo/mysql/mysql.sock does not exist,waiting to be created ..."
 sleep 5
else
 echo "${current_dir}/Spinfo/mysql/mysql.sock ready to install ."
fi

bin/mysqladmin -uroot -p"$password_init" password "Spinfo@0123" -S mysql.sock

echo "step3:waiting for mysql remote access to be enabled ..."
bin/mysql -uroot -pSpinfo@0123 -S mysql.sock << EOF
grant all privileges on *.* to 'root'@'%' identified by 'Spinfo@0123' with grant option;
flush privileges;
EOF
echo 'OK! database Installation Complete!'


echo "step4:Waiting for application database initialization ..."
if [ ! -d "${current_dir}/Spinfo/Base/db-script/mysql" ];then
  mkdir -p  ${current_dir}/Spinfo/Base/db-script/mysql
else
  rm -rf  ${current_dir}/Spinfo/Base/db-script/mysql/*.sql
fi

\cp -r ${current_dir}/Base/db-script/* /${current_dir}/Spinfo/Base/db-script/ &&
rm -rf ${current_dir}/Base/db-script/mysql
rm -rf ${current_dir}/Base/tools
 rm -rf ${current_dir}/Base/lib
chmod 777 ${current_dir}/Spinfo/Base/db-script/mysql/*.sql &&

if [ `ls -a ${current_dir}/Spinfo/Base/db-script/mysql/ | wc -l` -gt 2 ]
then
    for i in `ls ${current_dir}/Spinfo/Base/db-script/mysql/*.sql`
    do
        echo "import $i"
        bin/mysql -uroot -pSpinfo@0123 -S mysql.sock < $i
        if [ $? -ne 0 ]
        then
            echo -e "\033[1;40;31mimport TMC db-script is failed.\033[0m"
            exit 4
        fi
    done
else
    echo -e "\033[1;40;31mdb-script is null,install TMC is failed.\033[0m"
    exit 4
	fi


echo 'Application database has been install Complete!'

echo "step5:waiting for tomcat to start ... "
cd   ${current_dir}/Spinfo/apache-tomcat/bin
./startup.sh

cat << EOF
+---------------------------------------+
|    $PROG INSTALL FINISHED.             |
|       CONGRATULATIONS!                |
+---------------------------------------+
EOF