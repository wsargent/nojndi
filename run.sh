# Run this after `mvn clean install`
java -javaagent:agent/target/nojndi-agent-0.1.0.jar \
    -jar example/target/nojndi-example-0.1.0.jar

