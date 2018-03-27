build:
	rm -rf build/* && find . -type f -name "*.java" -print | xargs javac -d build -g -cp .:Drivers/mysql-connector-java-5.1.23-bin.jar

run:
	java -cp Drivers/mysql-connector-java-5.1.23-bin.jar:. Test

jar: 
	jar cf build.jar build Drivers/*
	
.PHONY: 
	all test clean