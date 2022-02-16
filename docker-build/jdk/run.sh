docker run -itd --name jdk8_container jdk8:321 /bin/bash

# 进入jdk容器内部, 尝试执行 "java -version"
# docker exec -it [container_id] /bin/bash