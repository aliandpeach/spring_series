docker run -p 31111:31111 -e "DATABASE_HOST=127.0.0.1:3306" -v /opt/springboot-docker-service/static/logs:/usr/local/docker/springboot-docker-service/static/logs -itd --name springboot-docker-service-container  springboot-docker-service:1.0-SNAPSHOT /bin/bash