FROM centos:7
 
MAINTAINER yk
 
#3、在构建镜像时,指定镜像的工作目录,之后的命令都是基于此工作目录,如果不存在,则会创建目录
WORKDIR /usr/local/docker
 
#4、一个复制命令,把jdk安装文件复制到镜像中,语法 ADD SRC DEST ,ADD命令具有自动解压功能(只支持.tar.gz)
ADD package/jdk-8u321-linux-x64.tar.gz /usr/local/docker
 
#5、配置环境变量,此处目录为tar.gz包解压后的名称,需提前解压知晓：
ENV JAVA_HOME=/usr/local/docker/jdk1.8.0_321
ENV CLASSPATH=.:$JAVA_HOME/lib/dt.jar:$JAVA_HOME/lib/tools.jar
ENV PATH=$JAVA_HOME/bin:$PATH
 
#6、设置启动命令
CMD ["java","-version"]