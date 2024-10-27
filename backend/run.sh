$CATALINA_HOME/bin/shutdown.sh

javac -cp /Users/sakshipatel29/Desktop/apache-tomcat-9.0.93/lib/servlet-api.jar:json.jar:mysql-connector-java.jar:mongo.jar:. -d WEB-INF/classes  WEB-INF/classes/filter/*.java WEB-INF/classes/servlets/*.java WEB-INF/classes/utilities/*.java

jar -cvf myservlet.war * 

export CATALINA_HOME=/Users/sakshipatel29/Desktop/apache-tomcat-9.0.93

cp myservlet.war $CATALINA_HOME/webapps/ 

$CATALINA_HOME/bin/startup.sh 