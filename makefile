build:
	find . -type f -name "*.java" -print | xargs javac -g -cp .:Drivers/mysql-connector-java-5.1.23-bin.jar

run:
	java -cp Drivers/mysql-connector-java-5.1.23-bin.jar:. Test

.PHONY: 
	all test clean