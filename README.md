# kinesis-consumer

Sample [KCL 2.X consumer](https://docs.aws.amazon.com/streams/latest/dev/enhanced-consumers.html) for AWS Kinesis streams. The consumer is configurable via environmental variables and can be containerized (dockerfile for it is provided), which can be run anywhere.


**Table of Contents**
<!-- TOC -->

- [kinesis-consumer](#kinesis-consumer)
	- [Quickstart](#quickstart)
		- [Create Kinesis Data Stream](#create-kinesis-data-stream)
		- [Configurations](#configurations)
		- [Create the package](#create-the-package)
		- [Run the consumer](#run-the-consumer)
	- [Publish data to Kinesis](#publish-data-to-kinesis)

<!-- /TOC -->

## Quickstart

Before running the samples, you'll want to make sure that your environment is configured to allow the samples to use your [AWS Security Credentials](https://docs.aws.amazon.com/general/latest/gr/aws-security-credentials.html).

### Create Kinesis Data Stream

You can create the the Kinesis data stream with AWS CLI.

```bash
STREAM_NAME=test-kds01
REGION=ap-northeast-1

aws kinesis create-stream --stream-name ${STREAM_NAME} --shard-count 1 --region ${REGION}

# describe the stream
aws kinesis describe-stream-summary --stream-name ${STREAM_NAME}

# list
aws kinesis list-streams
```

ref: [Perform Basic Kinesis Data Stream Operations Using the AWS CLI](https://docs.aws.amazon.com/streams/latest/dev/fundamental-stream.html)

### Configurations

Enviroment Variables to configure the Kinesis Consumer

|Parameter|Required|Default Value|Description|
|:--:|:--:|:--:|:--|
|`KINESIS_APPLICATION_NAME`|`true`| `""`| Kinesis KCL Application name |
|`KINESIS_STREAM_NAME`|`true`|`""`| Kinesis Stream Name |
|`KINESIS_REGION`|`false`|`"ap-northeast-1"`| Kinesis Region |
|`KINESIS_INITIAL_POSITION_IN_STREAM`|`false`|`"LATEST"`| Initial Position in Stream: `LATEST` or `TRIM_HORIZON` |
|`KINESIS_IDLETIME_BETWEEN_READS_MILLIS`|`false`|`1000` | Idle time between record reads in milliseconds |
|`KINESIS_FAILOVER_TIME_MILLIS`|`false`|`10000` | Fail over time in milliseconds. A worker which does not renew it's lease within this time interval will be regarded as having problems and it's shards will be assigned to other workers. For applications that have a large number of shards, this msy be set to a higher number to reduce the number of DynamoDB IOPS required for tracking leases |
|`KINESIS_MAX_RECORDS`|`false`|`10000`| Max records to fetch per Kinesis getRecords call |



Configure the consumer with enviromental variables like this and save the file (let's say `myconfig.env`)

```bash
export KINESIS_APPLICATION_NAME="<kinesis application name>"
export KINESIS_STREAM_NAME="<kinesis stream name>"
export KINESIS_REGION="<kinesis region: ap-northeast-1>"
export KINESIS_IDLETIME_BETWEEN_READS_MILLIS="<idle time between record reads in milliseconds: default 1000>"
export KINESIS_INITIAL_POSITION_IN_STREAM="<initial Position In Stream: LATEST or TRIM_HORIZON>"
export KINESIS_FAILOVER_TIME_MILLIS="<failover time millis: default 10000>"
export KINESIS_MAX_RECORDS="<max records to fetch per Kinesis getRecords call: default 10000>"

export JAVA_HEAP_XMX=512M
export JAVA_HEAP_XMS=512M
```

### Create the package

Build and create the package
```
make 
```
or You can create the package with `mvn` command
```
mvn package
```

### Run the consumer

You can run the consumer app with the configuration files (`myconfig.env`)

```bash
./helpers/run_local_consumer.sh myconfig.env
```

Or you can run a conatinized consumer app. You need to give the following 2 args
- App configuration file (`myconfig.env`)
- AWS Profile name that you use to interact with AWS resources (`my-aws-profile`)

```bash
./helpers/run_docker_consumer.sh myconfig.env my-aws-profile
```

Here is an sample output:
```
./helpers/run_docker_consumer.sh ./myconfig.env my-aws-profile

21-07-27 20:32:47:661  INFO main kinesis.KinesisConfig:137 - applkicationName: mykclapp01 [env KINESIS_APPLICATION_NAME: mykclapp01]
21-07-27 20:32:47:663  INFO main kinesis.KinesisConfig:139 - streamName: test-kds01 [env KINESIS_STREAM_NAME: test-stream01]
21-07-27 20:32:47:664  INFO main kinesis.KinesisConfig:141 - region: ap-northeast-1 [env KINESIS_REGION: ap-northeast-1]
21-07-27 20:32:47:664  INFO main kinesis.KinesisConfig:143 - idleTimeBetweenReadsInMillis: 1000 [env KINESIS_IDLETIME_BETWEEN_READS_MILLIS: null]
21-07-27 20:32:47:665  INFO main kinesis.KinesisConfig:145 - initialPositionInStream: LATEST [env KINESIS_INITIAL_POSITION_IN_STREAM: null]
21-07-27 20:32:47:665  INFO main kinesis.KinesisConfig:147 - failoverTimeMillis: 10000 [env KINESIS_FAILOVER_TIME_MILLIS: null]
21-07-27 20:32:47:667  INFO main kinesis.KinesisConfig:149 - maxRecords: 10000 [env KINESIS_MAX_RECORDS: null]
21-07-27 20:32:48:037  INFO main dynamodb.DynamoDBLeaseCoordinator:170 - With failover time 10000 ms and epsilon 25 ms, LeaseCoordinator will renew leases every 3308 ms, takeleases every 20050 ms, process maximum of 2147483647 leases and steal 1 lease(s) at a time. Press enter to shutdown
21-07-27 20:32:48:044  INFO Thread-1 coordinator.Scheduler:263 - Initialization attempt 1
21-07-27 20:32:48:045  INFO Thread-1 coordinator.Scheduler:264 - Initializing LeaseCoordinator
21-07-27 20:32:53:681  INFO Thread-1 coordinator.Scheduler:269 - Syncing Kinesis shard info
21-07-27 20:32:54:017  INFO Thread-1 coordinator.Scheduler:280 - Starting LeaseCoordinator
21-07-27 20:32:54:037  INFO Thread-1 coordinator.Scheduler:238 - Initialization complete. Starting worker loop.
21-07-27 20:32:54:054  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:389 - Worker 5a9a7d70-61bc-46cc-9882-142a144ae2e7 needed 1 leases but none were expired, so it will steal lease shardId-000000000001 from 4bc1b045-6858-4165-a6b4-845b290e68b4
21-07-27 20:32:54:055  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:397 - Worker 5a9a7d70-61bc-46cc-9882-142a144ae2e7 saw 2 total leases, 0 available leases, 2 workers. Target is 1 leases, I have 0 leases, I will take 1 leases
21-07-27 20:32:54:108  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:203 - Worker 5a9a7d70-61bc-46cc-9882-142a144ae2e7 successfully took 1 leases: shardId-000000000001
21-07-27 20:32:55:056  INFO Thread-1 coordinator.Scheduler:682 - Created new shardConsumer for : ShardInfo(shardId=shardId-000000000001, concurrencyToken=16582df2-200f-4f7e-8edc-c7dc677f0220, parentShardIds=[], checkpoint={SequenceNumber: LATEST,SubsequenceNumber: 0})
21-07-27 20:32:55:058  INFO ShardRecordProcessor-0000 lifecycle.BlockOnParentShardTask:78 - No need to block on parents [] of shard shardId-000000000001
21-07-27 20:32:56:091  INFO ShardRecordProcessor-0000 polling.KinesisDataFetcher:182 - Initializing shard shardId-000000000001 with LATEST
21-07-27 20:32:56:153  INFO ShardRecordProcessor-0000 polling.PrefetchRecordsPublisher:237 - shardId-000000000001 : Starting prefetching thread.
21-07-27 20:32:56:156  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:202 - Initializing @ Sequence: {SequenceNumber: LATEST,SubsequenceNumber: 0}
21-07-27 20:33:14:178  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:397 - Worker 5a9a7d70-61bc-46cc-9882-142a144ae2e7 saw 2 total leases, 1 available leases, 1 workers. Target is 2 leases, I have 1 leases, I will take 1 leases
21-07-27 20:33:14:198  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:203 - Worker 5a9a7d70-61bc-46cc-9882-142a144ae2e7 successfully took 1 leases: shardId-000000000000
21-07-27 20:33:15:138  INFO Thread-1 coordinator.Scheduler:682 - Created new shardConsumer for : ShardInfo(shardId=shardId-000000000000, concurrencyToken=270d7175-ec4f-488a-98a0-be42e76557f3, parentShardIds=[], checkpoint={SequenceNumber: LATEST,SubsequenceNumber: 0})
21-07-27 20:33:48:227  INFO Thread-1 coordinator.DiagnosticEventLogger:41 - Current thread pool executor state: ExecutorStateEvent(executorName=SchedulerThreadPoolExecutor, currentQueueSize=0, activeThreads=0, coreThreads=0, leasesOwned=2, largestPoolSize=2, maximumPoolSize=2147483647)
21-07-27 20:33:48:227  INFO Thread-1 coordinator.Scheduler:677 - Sleeping ...
21-07-27 20:34:05:270  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:218 - Processing 8 record(s)
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261799205369756139518011899906 Arrived(milsec ago): 1157 Data: testmsg
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261802832147214983405536018434 Arrived(milsec ago): 1144 Data: testmsg
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261805249998854212663885430786 Arrived(milsec ago): 1127 Data: testmsg
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261806458924673827293060136962 Arrived(milsec ago): 1111 Data: testmsg
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261808876776313056551409549314 Arrived(milsec ago): 1096 Data: testmsg
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261813712479591515068108374018 Arrived(milsec ago): 1083 Data: testmsg
21-07-27 20:34:05:271  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:244 - PartitionKey: 123 SequenceNumber: 49619723974008913634774903261816130331230744326457786370 Arrived(milsec ago): 1067 Data: testmsg
```

## Publish data to Kinesis

Using a Golang tool named [kinesis-bulk-loader](https://github.com/yokawasa/kinesis-bulk-loader), you can put bulk messages in parallel to AWS Kinesis Data Stream.

You can download and run the tool like this:

```
# Download the compiled command
curl -sS https://raw.githubusercontent.com/yokawasa/kinesis-bulk-loader/main/downloader | bash --

# Run the downloaded command
kinesis-bulk-loader -stream test-kds01 -k hoge -m test -c 10 -n 100 -verbose
```

For more information, see GitHub [kinesis-bulk-loader](https://github.com/yokawasa/kinesis-bulk-loader).
