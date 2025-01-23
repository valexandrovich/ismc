FROM nexus.otpbank.com.ua:10443/repository/tools/openjdk-17-jdk

#ENV JAVA_OPTS="-Xms256m -Xmx2G -XX:+ExitOnOutOfMemoryError -Ddebug=true -Dhttp.proxyHost=chckproxy.raiffeisenbank.com.ua -Dhttp.proxyPort=8080 -Dhttps.proxyHost=chckproxy.raiffeisenbank.com.ua -Dhttps.proxyPort=8080 -Dhttp.nonProxyHosts="localhost|127.0.*|*.raiffeisenbank.com.ua|10.247.*|10.244.*|195.248.*|10.233.*|antifrauddbdev.raiffeisenbank.com.ua|*.svc.cluster.*|wrapper.antifraud.svc.cluster.local|rabbitmq.antifraud-deps.svc.cluster.local" -Djava.security.egd=file:///dev/./urandom -Dsecurerandom.source=file:///dev/./urandom"
ENV JAVA_OPTS="-Xms256m -Xmx2G -XX:+ExitOnOutOfMemoryError  -Dhttp.proxyHost=chckproxy.raiffeisenbank.com.ua -Dhttp.proxyPort=8080 -Dhttps.proxyHost=chckproxy.raiffeisenbank.com.ua -Dhttps.proxyPort=8080 -Dhttp.nonProxyHosts="localhost|127.0.*|*.raiffeisenbank.com.ua|10.247.*|10.244.*|195.248.*|10.233.*|antifrauddbdev.raiffeisenbank.com.ua|*.svc.cluster.*|wrapper.antifraud.svc.cluster.local|rabbitmq.antifraud-deps.svc.cluster.local" -Djava.security.egd=file:///dev/./urandom -Dsecurerandom.source=file:///dev/./urandom"
ENV TZ=Europe/Kiev

#ENV http_proxy=http://chckproxy.raiffeisenbank.com.ua:8080
#ENV https_proxy=http://chckproxy.raiffeisenbank.com.ua:8080
#ENV no_proxy=localhost,127.0.*,*.raiffeisenbank.com.ua,10.247.*,10.244.*,195.248.*,10.233.*,antifrauddbdev.raiffeisenbank.com.ua,*.svc.cluster.*,wrapper.antifraud.svc.cluster.local,rabbitmq.antifraud-deps.svc.cluster.local

ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar ${0} ${@}"]#