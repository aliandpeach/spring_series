docker run -p 31111:31111 \
-e "DATABASE_HOST=127.0.0.1:3306" \
-v /opt/demo-1.0-SNAPSHOT/static/logs:/usr/local/docker/demo-1.0-SNAPSHOT/static/logs \
-itd --name demo-1.0-SNAPSHOT-container springboot-docker-service:1.0-SNAPSHOT /bin/bash