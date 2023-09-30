mvn clean install

mv target/seasonsforce-ms-address-api-1.0-SNAPSHOT.jar api-image/seasonsforce-ms-address-api-1.0-SNAPSHOT.jar

cd api-image

docker build -t address-api .

