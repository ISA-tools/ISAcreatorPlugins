rm ../../ISAcreator/Plugins/*.jar
mvn clean install
cp ./target/metabolomics*.jar ../../ISAcreator/Plugins/
ls -Fla ../../ISAcreator/Plugins
