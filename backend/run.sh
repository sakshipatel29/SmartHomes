$CATALINA_HOME/bin/shutdown.sh

javac -cp /Users/sakshipatel29/Desktop/apache-tomcat-9.0.93/lib/servlet-api.jar:json.jar:mysql-connector-java.jar:mongo.jar:jakarta.json-api-2.0.1.jar:httpclient-4.5.14.jar:jackson-databind-2.18.1.jar:elasticsearch-java-8.16.0.jar:commons-io-2.16.1.jar:httpcore5-5.3.1.jar:elasticsearch-rest-client-8.16.0.jar:elasticsearch-8.16.0.jar:jakarta.servlet-api-5.0.0.jar -d WEB-INF/classes  WEB-INF/classes/filter/*.java WEB-INF/classes/servlets/*.java WEB-INF/classes/utilities/*.java

jar -cvf myservlet.war * 

export CATALINA_HOME=/Users/sakshipatel29/Desktop/apache-tomcat-9.0.93

cp myservlet.war $CATALINA_HOME/webapps/ 

$CATALINA_HOME/bin/startup.sh 