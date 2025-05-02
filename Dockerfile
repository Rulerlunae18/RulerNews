FROM tomcat:10.1.34

RUN rm -rf /usr/local/tomcat/webapps/*

COPY target/RulerNews.war /usr/local/tomcat/webapps/ROOT.war
