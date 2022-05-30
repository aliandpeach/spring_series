FROM tomcat-yk:9

VOLUME /tmp

WORKDIR /usr/local/docker

ADD target/website /usr/local/docker/apache-tomcat-9.0.63/webapps/website

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime