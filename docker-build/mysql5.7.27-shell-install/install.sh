#!/bin/bash

# sh install [安装目录] [安装到哪个用户组]

if [ ! $# -eq 2  ];then
  echo "lack of install path and user"
  exit 1
fi

path=$1
username=$2

if [ ! -d ${path} ]; then
 mkdir -p ${path}
fi


cp -f my.cnf /etc/my.cnf
cp -f mysql /etc/init.d/mysql
cp -f mysqld.service /etc/systemd/system/mysqld.service

sed  -i 's/User=/User='"${username}"'/' /etc/systemd/system/mysqld.service
sed  -i 's/Group=/Group='"${username}"'/' /etc/systemd/system/mysqld.service

echo "${path//\//\\/}"
sed -i "s/\/Path/${path//\//\\/}/g" /etc/init.d/mysql
sed -i "s/\/Path/${path//\//\\/}/g" /etc/my.cnf


if [ "${username}" = 'root' ]; then
    sed  -i 's/user=/user='"${username}"'/'  /etc/my.cnf
else
    sed  -i '/user=/s/^/#/'  /etc/my.cnf
fi

chmod u+x /etc/init.d/mysql
chmod u+x /etc/systemd/system/mysqld.service

tar -zxvf mysql-5.7.27-linux-glibc2.12-x86_64.tar.gz
mv mysql-5.7.27-linux-glibc2.12-x86_64 ${path}/mysql

${path}/mysql/bin/mysqld --defaults-file=/etc/my.cnf \
   --initialize-insecure \
   --socket=${path}/mysql/mysql.sock \
   --basedir=${path}/mysql \
   --datadir=${path}/mysql/data

sleep 1

systemctl daemon-reload
systemctl stop mysqld
systemctl start mysqld

${path}/mysql/bin/mysqladmin -uroot -p password "root" -S ${path}/mysql/mysql.sock

${path}/mysql/bin/mysql -uroot -proot -S ${path}/mysql/mysql.sock << EOF
grant all privileges on *.* to 'root'@'%' identified by 'root' with grant option;
flush privileges;
EOF

echo "done"

