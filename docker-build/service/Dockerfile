FROM jdk8:321

# VOLUME 指定了臨時檔案目錄為/tmp。
# 其效果是在主機 /var/lib/docker 目錄下建立了一個臨時檔案，並連結到容器的/tmp
VOLUME /tmp

WORKDIR /usr/local/docker/springboot-docker-service

# 將jar包新增到容器中並更名為springboot-docker-service.jar
ADD target/springboot-docker-service-1.0-SNAPSHOT.jar /usr/local/docker/springboot-docker-service/springboot-docker-service.jar
ADD target/application.yml /usr/local/docker/springboot-docker-service/application.yml
ADD target/application-docker.yml /usr/local/docker/springboot-docker-service/application-docker.yml

# 执行jar包
RUN bash -c 'touch /usr/local/docker/springboot-docker-service/springboot-docker-service.jar'
# 执行springboot-docker-service.jar 為了縮短 Tomcat 啟動時間，新增一個系統屬性指向 “/dev/./urandom” 作為 Entropy Source
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/usr/local/docker/springboot-docker-service/springboot-docker-service.jar","--spring.config.location=/usr/local/docker/springboot-docker-service/*.yml"]