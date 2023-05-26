FROM jdk8:371
#作者
MAINTAINER yk
#声明一个挂载点,容器内此路径会对应宿主机的某个文件夹
VOLUME /tmp

WORKDIR /usr/local/docker/demo-1.0-SNAPSHOT

#复制springboot-docker-service-1.0-SNAPSHOT.jar 到容器里, 注意这里源文件的位置是相对于POM中dockerDirectory配置的相对路径
ADD springboot-docker-service-1.0-SNAPSHOT.jar /usr/local/docker/demo-1.0-SNAPSHOT/springboot-docker-service-1.0-SNAPSHOT.jar
ADD application.yml /usr/local/docker/demo-1.0-SNAPSHOT/application.yml
ADD application-docker.yml /usr/local/docker/demo-1.0-SNAPSHOT/application-docker.yml

ENV  LANG="en_US.UTF-8"
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
#bash方式执行,使demo-1.0-SNAPSHOT可访问
#RUN新建立一层,在其上执行这些命令,执行结束后, commit 这一层的修改,构成新的镜像。
RUN bash -c "touch /usr/local/docker/demo-1.0-SNAPSHOT/springboot-docker-service-1.0-SNAPSHOT.jar"
#声明运行时容器提供服务端口,这只是一个声明,在运行时并不会因为这个声明应用就会开启这个端口的服务
EXPOSE 8899

#指定容器启动程序及参数   <ENTRYPOINT> "<CMD>"
ENTRYPOINT ["java","-jar","/usr/local/docker/demo-1.0-SNAPSHOT/springboot-docker-service-1.0-SNAPSHOT.jar","--spring.config.location=/usr/local/docker/demo-1.0-SNAPSHOT/*.yml"]