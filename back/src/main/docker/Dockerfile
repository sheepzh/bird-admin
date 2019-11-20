FROM frolvlad/alpine-oraclejdk11:slim
VOLUME /tmp
ADD bird-back-demo.jar
RUN sh -c 'touch /bird-back-demo.jar'
ENV JAVA_OPTS=""
ENTRYPOINT [ "sh", "-c", "java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar /bird-back-demo.jar" ]