Name: Patel Sakshikumari Rajeshbhai
Email id: spatel209@hawk.iit.edu

Tomcat Version: 9.0.93
Server Side: JSP Servlet
Client Side: React.js
Data Storage: MySql, MongoDB Database

Steps to run server:
-->cd smarthomes-project
--> cd smarthomes-backend
-->$CATALINA_HOME/bin/shutdown.sh

-->javac -cp /Users/sakshipatel29/Desktop/apache-tomcat-9.0.93/lib/servlet-api.jar:json.jar:mysql-connector-java.jar:mongo.jar:. -d WEB-INF/classes  WEB-INF/classes/filter/*.java WEB-INF/classes/servlets/*.java WEB-INF/classes/utilities/*.java

-->jar -cvf myservlet.war * 

-->cp myservlet.war $CATALINA_HOME/webapps/ 

-->$CATALINA_HOME/bin/startup.sh 

--> or run ./run.sh

-->Start Tomcat on localhost. (http://localhost:8080/myservlet/)

Steps to run Client side:
-->cd smarthomes-project
--> cd frontend 
--> npm start
