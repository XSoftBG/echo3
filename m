:
export SERVLET_LIB_JAR="/usr/local/apache-tomcat-6.0.26/lib/servlet-api.jar"
ant clean
ant
cp ./dist/lib/Echo3_*.jar ../Dolphin/lib/
cp ./dist/lib/Echo3_*.jar ../echopoint/lib/
