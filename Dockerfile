# FROM maven:onbuild
FROM maven:latest

#RUN mkdir -p /usr/src/conduct
#WORKDIR /usr/src/conduct
#ADD . /usr/src/conduct

#VOLUME ["/usr/share/conduct"]

#RUN mkdir -p /usr/src/conduct
WORKDIR target
#ADD . target

VOLUME ["target"]

EXPOSE 8080

#ENTRYPOINT ["mvn", "install", "exec:java", "-P"]
ENTRYPOINT ["mvn", "install", "-P"]
CMD ["build"]
