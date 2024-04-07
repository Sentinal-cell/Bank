FROM openjdk:11-jdk

RUN apt-get update && apt-get install -y \
    openjdk-11-jdk \
    mysql-server \
    supervisor
COPY /src/conn.java /app/
COPY /src/info.java /app/
COPY /src/transfer.java /app/
COPY /src/ftran.java /app/
COPY /lib/mysql-connector-j-8.1.0.jar /app/
EXPOSE 3333
RUN javac *.java
CMD ["java", "-cp", ".:mysql-connector-j-8.1.0.jar", "conn", "-p", "3333"]