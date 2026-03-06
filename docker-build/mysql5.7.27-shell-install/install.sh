#!/bin/bash

# sh install [安装目录] [服务所属用户]

if [ ! $# -eq 2  ];then
  echo "lack of install path and user"
  exit 1
fi

path=$1
username=$2

if [ ! -d ${path} ]; then
 mkdir -p ${path}
fi

if [ -z "${username}" ]; then
 echo "username is empty"
 exit 1
fi

# 查询传入的用户是否存在, 不存在则不创建, 创建mysql用户组
egrep "^$username" /etc/passwd > /dev/null 2>&1
if [ $? -eq 0 ]; then
  echo "user ${username} doesn't exist, then use create mysql user"
  groupadd mysql
  useradd -r -g mysql mysql
  username = "mysql"
fi

group=$(id -Gn ${username})


cp -f my.cnf /etc/my.cnf
cp -f mysql /etc/init.d/mysql
cp -f mysqld.service /etc/systemd/system/mysqld.service

# mysqld.service中执行启动的命令放在了/etc/init.d/mysql中, 因此如果是非root用户, 需要保证mysqld.service的User有/etc/init.d/mysql的权限
chown "${username}":"${group}" /etc/init.d/mysql
chown "${username}":"${group}" /etc/systemd/system/mysqld.service


sed  -i 's/User=/User='"${username}"'/' /etc/systemd/system/mysqld.service
sed  -i 's/Group=/Group='"${group}"'/' /etc/systemd/system/mysqld.service

echo "${path//\//\\/}"
sed -i "s/\/Path/${path//\//\\/}/g" /etc/init.d/mysql
sed -i "s/\/Path/${path//\//\\/}/g" /etc/my.cnf


sed  -i 's/user=/user='"${username}"'/'  /etc/my.cnf
#if [ "${username}" = 'root' ]; then
#    sed  -i 's/user=/user='"${username}"'/'  /etc/my.cnf
#else
#    sed  -i '/user=/s/^/#/'  /etc/my.cnf
#fi

chmod u+x /etc/init.d/mysql
chmod u+x /etc/systemd/system/mysqld.service

tar -zxvf mysql-5.7.27-linux-glibc2.12-x86_64.tar.gz
mv mysql-5.7.27-linux-glibc2.12-x86_64 ${path}/mysql

chown "${username}":"${group}" -R "${path}/mysql/"

${path}/mysql/bin/mysqld --defaults-file=/etc/my.cnf \
   --initialize \
   --socket=${path}/mysql/mysql.sock \
   --basedir=${path}/mysql \
   --datadir=${path}/mysql/data
   --user=${username}
sleep 1

str=`cat ${path}/mysql/error.log | grep @localhost`
password_init=`echo ${str#*@localhost:}`
echo "=========[$password_init]========"

systemctl daemon-reload
systemctl stop mysqld
systemctl start mysqld

${path}/mysql/bin/mysqladmin -uroot -p"$password_init" password "root" -S ${path}/mysql/mysql.sock

${path}/mysql/bin/mysql -uroot -proot -S ${path}/mysql/mysql.sock << EOF
grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
flush privileges;
EOF

echo "done"

