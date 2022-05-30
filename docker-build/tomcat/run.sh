docker run -p 9090:8080 \
    -v /opt/tomcat/logs:/usr/local/docker/apache-tomcat-9.0.63/logs \
    -itd --name tomcat-yk-container  tomcat-yk:9 /bin/bash