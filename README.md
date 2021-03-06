youtube mini tutorial how to run the project through intellij idea https://youtu.be/IUtauUax86g

Pet project to experiment with kafka, flink & cassandra and etc 

##### Connect to Cassandra Data in IntelliJ
https://www.cdata.com/kb/tech/cassandra-jdbc-intellij.rst

 start up optional steps ( for latest windows OS) in case you install all the environment inside 
 your OS ( in case you use Docker containers skill the steps below ) : 
 - `cd C:\zookeeper-3.4.12`
 - `zkServer.cmd`
 - `cd C:\kafka_2.12-2.1.0`
 - `.\bin\windows\kafka-server-start.bat .\config\server.properties`
 - `cassandra -f`

### Websocket server

To be in control over the incomning data I've setup a websocket server that reads from a huge CSV file and post each line in regular intervals to it's clients

Run:

`sbt "project websocket-server" "run"`

### Kafka 

Start Kafka on Docker with zookeeper

`docker run -d -p 2181:2181 -p 9092:9092 --env ADVERTISED_HOST=127.0.0.1 --env ADVERTISED_PORT=9092 --name kafka johnnypark/kafka-zookeeper`

`ADVERTISTED_HOST` was set to `127.0.0.1`, which will allow other containers to be able to run Producers and Consumers.

Setting `ADVERTISED_HOST` to `localhost`, `127.0.0.1`, or `0.0.0.0` will work great only if Producers and Consumers are started within the `kafka` container itself, or if you are using DockerForMac (like me) and you want to run Producers and Consumers from OSX.


### Websocket client -> Kafka Producer

When websocket server & kafka are running we can run the websocket client / kafka producer


`sbt "project websocket-client-kafka" "run"`

### Cassandra

`docker run --name cassandra  -d -p 9042:9042 cassandra
`
### Portainer

`docker run -d -p 9000:9000 -v /var/run/docker.sock:/var/run/docker.sock -v portainer_data:/data portainer/portainer
`

### Kafka Consumer -> Flink -> Cassandra

Have a kafka consumer to Flink read from the kafka stream for :  
- process the data
- store the results in cassandra

### Cassandra 

 cassandra prep STEPS:
 
 `docker exec -it cassandra cqlsh -e "CREATE KEYSPACE IF NOT EXISTS db WITH
 REPLICATION = {'class' : 'SimpleStrategy', 'replication_factor' : 1 };"`
 
 `docker exec -it cassandra cqlsh -e "CREATE TABLE IF NOT EXISTS db.market_data (id uuid, ticker text, time text,
 price text, volume int, currency text, PRIMARY KEY (id));"`

 `docker exec -it cassandra cqlsh -e "CREATE TABLE IF NOT EXISTS  db.market_data (bar_time text, ticker text, open float, high float, low float, close float, volume int, currency text, PRIMARY KEY(bar_time, ticker));"`
 

 `docker exec -it cassandra cqlsh -e "DROP TABLE test_keyspace.table_ticker;"`


####experimental table 
 `CREATE TABLE IF NOT EXISTS test_keyspace.table_ohlc30s (bar_time text, ticker text, open float, 
 high float, low float, close float, volume int, currency text, PRIMARY KEY(bar_time, ticker));`
 
 `CREATE TABLE IF NOT EXISTS db.market_data (bar_time text, ticker text, open float, high float, low float, close float, volume int, currency text, PRIMARY KEY(bar_time, ticker));`

####helping cql 
`Desc test_keyspace.table_ohlc30s ;`


##### for local kafka debuging 
- cd C:\kafka_2.12-2.1.0
- .\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic 
topic-name-here --from-beginning

 .\bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic FXUS --from-beginning


