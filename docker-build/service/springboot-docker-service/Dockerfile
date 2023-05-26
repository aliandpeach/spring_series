FROM jdk8:371

# VOLUME 指定了临时目录为/tmp。
# 其效果是在主机 /var/lib/docker 目錄下建立了一个临时档案，并连接到容器的/tmp
VOLUME /tmp

WORKDIR /usr/local/docker/springboot-docker-service

# 將jar包新增到容器中並更名為springboot-docker-service.jar
ADD package/springboot-docker-service-1.0-SNAPSHOT.jar /usr/local/docker/springboot-docker-service/springboot-docker-service.jar
ADD package/application.yml /usr/local/docker/springboot-docker-service/application.yml
ADD package/application-docker.yml /usr/local/docker/springboot-docker-service/application-docker.yml

# 执行jar包
# RUN bash -c 'touch /usr/local/docker/springboot-docker-service/springboot-docker-service.jar'
ENV  LANG="en_US.UTF-8"
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime

# CMD ["java","-Ddocker.path=/usr/local/docker/", "-Djava.security.egd=file:/dev/./urandom","-jar","/usr/local/docker/springboot-docker-service/springboot-docker-service.jar","--spring.config.location=/usr/local/docker/springboot-docker-service/*.yml"]

# 执行springboot-docker-service.jar 为了缩短 Tomcat 启动时间，新增一个系统属性指向 “/dev/./urandom” 作为 Entropy Source
ENTRYPOINT ["java","-Ddocker.path=/usr/local/docker/", "-Djava.security.egd=file:/dev/./urandom","-jar","/usr/local/docker/springboot-docker-service/springboot-docker-service.jar","--spring.config.location=/usr/local/docker/springboot-docker-service/*.yml"]