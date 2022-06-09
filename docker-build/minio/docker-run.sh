docker run -p 9120:9120 -p 9121:9121 --name minio \
-d --restart=always \
-e "MINIO_ACCESS_KEY=AKIAIOSFODNN7EXAMPLE" \
-e "MINIO_SECRET_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" \
-v /mnt/minio/data:/data \
-v /mnt/minio/config:/root/.minio \
minio/minio server /data --console-address ":9121" --address ":9120"