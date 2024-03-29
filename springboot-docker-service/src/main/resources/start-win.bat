
@echo off
set "BLOCK_CHAIN_CURRENT_PATH=%cd%"
set "BLOCK_CHAIN_JAR_PATH=%BLOCK_CHAIN_CURRENT_PATH%\springboot-docker-service-1.0-SNAPSHOT.jar"
set "BLOCK_CHAIN_YML_PATH=%BLOCK_CHAIN_CURRENT_PATH%\*.yml"
set "BLOCK_CHAIN_JAVA_EXEC_PATH=%BLOCK_CHAIN_CURRENT_PATH%\jdk1.8.0_271\bin\java"

%BLOCK_CHAIN_JAVA_EXEC_PATH% -Xmx1024m -Xms64m -XX:ThreadStackSize=512 -jar "%BLOCK_CHAIN_JAR_PATH%" --spring.config.location="%BLOCK_CHAIN_YML_PATH%"