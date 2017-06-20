FROM java:8

WORKDIR /app
RUN apt-get update && apt-get -y install curl
RUN curl -LO https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
RUN java -jar BuildTools.jar --rev 1.12
