:
export SERVLET_LIB_JAR="$HOME/NetBeansProjects/Dolphin/lib/servlet-api.jar"
ant clean
ant
cp ./dist/lib/Echo3_*.jar ../Dolphin/lib/
cp ./dist/lib/Echo3_*.jar ../echopoint/lib/
cp ./dist/lib/Echo3_*.jar ../echolot/echolot-app/lib/
