CREATE KEYSPACE IF NOT EXISTS db
  WITH REPLICATION = {
   'class' : 'SimpleStrategy',
   'replication_factor' : 1
  };


CREATE TABLE IF NOT EXISTS db.market_data (
bar_time text,
ticker text,
open float,
high float,
low float,
close float,
volume int,
currency text,
PRIMARY KEY(bar_time, ticker)
);

CREATE TABLE IF NOT EXISTS db.market_data (
bar_time text,
ticker text,
open float,
high float,
low float,
close float,
volume int,
currency text,
PRIMARY KEY(bar_time, ticker)
) WITH comment = 'First sink to test the other "unsafe" Cassandra Foreach Sink.';


