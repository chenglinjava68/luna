由于ES稳定高效的性能，我们希望能将Mysql的数据低延迟的增量同步到ES中，经过多种比较，我们选择了这样一个方案：通过Maxwell从Binlog获取mysql更新消息（insert update delete），以Kafka作为消息队列中间件，将数据增量同步到ES的方案。这个方案有以下几个优点：

 - 低延时性
 
 批次作业同步带来的高延时性往往很多业务不能接受，基于增量同步的方案大大降低了延时性，也极大的增强了稳定性。
 - 保证消息顺序
 
 没有必要保证整个Binlog消息的顺序，通过根据Table划分kafka partition保证Table内消息的有序，最终保证数据最终的完整可靠。
 - 良好的扩展性
 
 Kafka作为一个分布式的消息队列中间件，支持多个生产源生产数据，多个消费端消费数据。可以很方便的扩展生产端（比如添加新的数据库），Kafka集群也可以便捷的扩展以提高容积吞吐，同时消费客户端类luna也可以扩展，以增加订阅等，总之由于Kafka的存在，扩展变得很便捷。
 - 组件间解耦，数据冗余，提高可恢复性
 
 不是直接将Maxwell抓取的消息消费到ES，而是通过Kafka中间件，降低组件间的耦合性，提高安全性，同时Kafka持久化数据使得maxwell,kafka,luna发生错误崩溃时，重启就可以立即继续工作。
 - 峰值处理能力
 
 峰值并不是工作的常态，可能大多数情况下mysql有更新就可以及时的消费掉，当峰值到来时，应当感知到并且提高消费能力，如果能力依然不够，Kafka提供了消息积压的能力（理论上Kafka几乎可以无限积压消息，虽然可能很少这么做），峰值过后可以尽快消化掉积压。

# Maxwell

## Download

    curl -sLo - https://github.com/zendesk/maxwell/releases/download/v1.10.6/maxwell-1.10.6.tar.gz \
       | tar zxvf -
    mv maxwell-1.10.6 /home/maxwell
    cd maxwell
    
## Row based replication

    $ vi my.cnf
    [mysqld]
    server-id=1
    log-bin=master
    binlog_format=row

## Mysql permissions

    mysql> GRANT ALL on maxwell.* to 'maxwell'@'%' identified by 'XXXXXX';
    mysql> GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE on *.* to 'maxwell'@'%';

    # or for running maxwell locally:

    mysql> GRANT SELECT, REPLICATION CLIENT, REPLICATION SLAVE on *.* to 'maxwell'@'localhost' identified by 'XXXXXX';
    mysql> GRANT ALL on maxwell.* to 'maxwell'@'localhost';

## Config.properties

Copy config.properties.example to config.properties and modify the following properties

    producer=kafka
	  log_level=INFO
	  host='xxx.xxx.xxx.xxx'
	  port=3306
	  user='maxwell'
	  password='xxxxxx'
	  kafka.bootstrap.servers=k_host0:9092,k_host1:9092,k_host2:9092,k_host3:9092,k_host4:9092
	  ### One table in one topic
	  kafka_topic=maxwell_%{database}_%{table}
	  kafka.batch.size=16384
	  kafka.compression.type=snappy
	  kafka.metadata.fetch.timeout.ms=5000
	  kafka.retries=5
	  ### default 1 get and ack;0 send and ack;all or -1 replica in ISR get ack
	  kafka.acks=1
	  kafka.request.timeout.ms=10000
	  kafka.linger.ms=0
	  ### One table in one partition
	  producer_partition_by=table # [database, table, primary_key, column]

## Start maxwell

    nohup bin/maxwell --user='maxwell' --password='XXXXXX' --host='127.0.0.1' &
    
# Kafka

## download

Better to download kafka_2.12-0.11.0.0 and newer version. Unzip and move to work dir

## zookeeper.properties

Modify every zookeeper node

	  dataDir=/tmp/zookeeper
	  clientPort=2181
	  initLimit=5
	  syncLimit=2
	  server.0=z_host0:2888:3888
	  server.1=z_host1:2888:3888
	  server.2=z_host2:2888:3888
	  server.3=z_host3:2888:3888
	
## server.properties

Modify every broker server.properties

	  broker.id=0 # Every broker has unique id(1,2,3,4...).  
	  delete.topic.enable=true    
	  auto.create.topics.enable=false
	  listeners=PLAINTEXT://host:9092 # host is your broker host 
	  advertised.listeners=PLAINTEXT://host:9092 # host is your broker host
	  log.dirs=/tmp/kafka-logs
	  #z_host0 is your zookeeper host
	  zookeeper.connect=z_host0:2181,z_host1:2181,z_host2:2181,z_host3:2181
	  ### Avoid purgatory OOME 
	  fetch.purgatory.purge.interval.requests: 100
	  producer.purgatory.purge.interval.requests: 100

## Some work

Create kafka data dir and zookeeper data dir

    mkdir /tmp/kafka-logs
    chmod 755 -R /tmp/kafka-logs
    mkdir /tmp/zookeeper
    chmod 755 -R /tmp/zookeeper
    
Modify firewall (your OS may be different)
    
    vim /etc/sysconfig/iptables
    
    ## add the following
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 2181 -j ACCEPT
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 2888 -j ACCEPT
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 3888 -j ACCEPT
    -A INPUT -m state --state NEW -m tcp -p tcp --dport 9092 -j ACCEPT
    ## save and quit
    
    service iptables restart

## Start zookeeper cluster

    bin/zookeeper-server-start.sh -daemon config/zookeeper.properties

## Start kafka cluster

    bin/kafka-server-start.sh -daemon config/server.properties
    
# Elasticsearch

I think you should have your elasticsearch cluster. If not, learn and make one.

# luna

## Download And Install
    git clone https://github.com/sanguinar/luna
    cd luna
    mvn package # or not

## Config

 - src/main/java/log4j2.xml
 Modify the fileName. If use /data/luna/logs, need to create dir and chomd 
 - conf/mysql.conf
 Change to your mysql host, user, password, db_name(all is all), table(all is all)
 map.py will use the config and add mapping to conf/mapping.yml
 - con/mapping.yml
 You can modify it by self to test. Or you can use script to modify
 - conf/example.yml
 Change to your kafka server host and elasticsearch host and change some other config
 
## ES Mapping
    python conf/map.py
    java -cp target/luna-0.0.1.jar luna.app.AdminApp

## Start luna
    nohup java -cp target/luna-0.0.1.jar luna.app.App &
