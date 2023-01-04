FROM tomcat-yk:9

VOLUME /tmp

WORKDIR /usr/local/docker

# 拷贝war包自动解压
ADD package/springboot_website-1.0-SNAPSHOT.war /usr/local/docker/apache-tomcat-9.0.63/webapps/

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime