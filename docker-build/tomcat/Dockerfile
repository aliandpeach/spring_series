FROM jdk8:371
 
MAINTAINER yk
 
#3、在构建镜像时,指定镜像的工作目录,之后的命令都是基于此工作目录,如果不存在,则会创建目录
WORKDIR /usr/local/docker
 
#4、一个复制命令,把jdk安装文件复制到镜像中,语法 ADD SRC DEST ,ADD命令具有自动解压功能(只支持.tar.gz)
ADD package/apache-tomcat-9.0.63.tar.gz /usr/local/docker/

RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# 设置tomcat的环境变量
ENV CATALINA_HOME=/usr/local/docker/apache-tomcat-9.0.63
ENV CATALINA_BASH=/usr/local/docker/apache-tomcat-9.0.63
ENV PATH=$PATH:$JAVA_HOME/bin:$CATALINA_HOME/lib:$CATALINA_HOME/bin

RUN echo $JAVA_HOME
RUN echo $CATALINA_HOME
RUN echo $CATALINA_BASH
RUN ls /usr/local/docker/apache-tomcat-9.0.63

# 暴露端口
EXPOSE 8080

# 不能使用startup.sh因为这个命令是后台执行的, 会导致容器无法启动
ENTRYPOINT ["/bin/sh", "-c", "/usr/local/docker/apache-tomcat-9.0.63/bin/catalina.sh run"]