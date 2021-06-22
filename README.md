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

Configure the consumer appliation with enviromental variables and save the file (let's say `myconfig.env`)

```bash
export KINESIS_APPLICATION_NAME="<kinesis application name>"
export KINESIS_STREAM_NAME="<kinesis stream name>"
export KINESIS_REGION="<kinesis region: ap-northeast-1>"
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

21-06-20 05:06:18:424  INFO main kinesis.KinesisConfig:111 - applkicationName: mykclapp01 [env KINESIS_APPLICATION_NAME: mykclapp01]
21-06-20 05:06:18:455  INFO main kinesis.KinesisConfig:113 - streamName: test-kds01 [env KINESIS_STREAM_NAME: test-kds01]
21-06-20 05:06:18:456  INFO main kinesis.KinesisConfig:115 - region: ap-northeast-1 [env KINESIS_REGION: ap-northeast-1]
21-06-20 05:06:18:458  INFO main kinesis.KinesisConfig:117 - initialPositionInStream: LATEST [env KINESIS_INITIAL_POSITION_IN_STREAM: null]
21-06-20 05:06:18:460  INFO main kinesis.KinesisConfig:119 - failoverTimeMillis: 10000 [env KINESIS_FAILOVER_TIME_MILLIS: null]
21-06-20 05:06:18:462  INFO main kinesis.KinesisConfig:121 - maxRecords: 10000 [env KINESIS_MAX_RECORDS: null]
21-06-20 05:06:22:810  INFO main dynamodb.DynamoDBLeaseCoordinator:170 - With failover time 10000 ms and epsilon 25 ms, LeaseCoordinator will renew leases every 3308 ms, takeleases every 20050 ms, process maximum of 2147483647 leases and steal 1 lease(s) at a time.
Press enter to shutdown
21-06-20 05:06:22:843  INFO Thread-1 coordinator.Scheduler:263 - Initialization attempt 1
21-06-20 05:06:22:846  INFO Thread-1 coordinator.Scheduler:264 - Initializing LeaseCoordinator
21-06-20 05:06:29:580  INFO Thread-1 coordinator.Scheduler:269 - Syncing Kinesis shard info
21-06-20 05:06:32:079  INFO Thread-1 coordinator.Scheduler:280 - Starting LeaseCoordinator
21-06-20 05:06:32:311  INFO Thread-1 coordinator.Scheduler:238 - Initialization complete. Starting worker loop.
21-06-20 05:06:52:577  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:397 - Worker 2d53ae8c-8669-4eb4-b445-3383a449f28b saw 1 total leases, 1 available leases, 1 workers. Target is 1 leases, I have 0 leases, I will take 1 leases
21-06-20 05:06:52:869  INFO LeaseCoordinator-0000 dynamodb.DynamoDBLeaseTaker:203 - Worker 2d53ae8c-8669-4eb4-b445-3383a449f28b successfully took 1 leases: shardId-000000000000
21-06-20 05:06:53:649  INFO Thread-1 coordinator.Scheduler:682 - Created new shardConsumer for : ShardInfo(shardId=shardId-000000000000, concurrencyToken=857c1159-00e8-421d-b78a-14740591945c, parentShardIds=[], checkpoint={SequenceNumber: 49618728439127861604111470615056250046941505864572338178,SubsequenceNumber: 0})
21-06-20 05:06:53:657  INFO Thread-1 coordinator.DiagnosticEventLogger:41 - Current thread pool executor state: ExecutorStateEvent(executorName=SchedulerThreadPoolExecutor, currentQueueSize=0, activeThreads=1, coreThreads=0, leasesOwned=1, largestPoolSize=1, maximumPoolSize=2147483647)
21-06-20 05:06:53:676  INFO ShardRecordProcessor-0000 lifecycle.BlockOnParentShardTask:78 - No need to block on parents [] of shard shardId-000000000000
21-06-20 05:06:54:826  INFO ShardRecordProcessor-0000 polling.KinesisDataFetcher:182 - Initializing shard shardId-000000000000 with 49618728439127861604111470615056250046941505864572338178
21-06-20 05:06:55:069  INFO ShardRecordProcessor-0000 polling.PrefetchRecordsPublisher:237 - shardId-000000000000 : Starting prefetching thread.
21-06-20 05:06:55:128  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:186 - Initializing @ Sequence: {SequenceNumber: 49618728439127861604111470615056250046941505864572338178,SubsequenceNumber: 0}
21-06-20 05:07:03:189  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:202 - Processing 8 record(s)
21-06-20 05:07:03:198  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473221600657979110386752521502722
21-06-20 05:07:03:200  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473222873656867164591479645536258
21-06-20 05:07:03:200  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473224105552277351898814829559810
21-06-20 05:07:03:200  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473225349536945735352373041168386
21-06-20 05:07:03:200  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473226893335217383234035299385346
21-06-20 05:07:03:204  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473228300524871414662600815804418
21-06-20 05:07:03:204  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473229679909231594954626594504706
21-06-20 05:07:03:205  INFO ShardRecordProcessor-0000 kinesis.SampleKinesisConsumer$SampleRecordProcessor:203 - Processing record pk: 123 -- Seq: 49618728439127861604111473231230961058160524063900958722
```

## Publish data to Kinesis

Using a Golang tool named [kinesis-bulk-loader](https://github.com/yokawasa/kinesis-bulk-loader), you can put bulk messages in parallel to AWS Kinesis Data Stream.

You can download and run the tool like this:

```
# Clone the tool repository
git clone https://github.com/yokawasa/kinesis-bulk-loader.git
cd kinesis-bulk-loader

# Download the compiled command with downloader
./downloader

# Run the downloaded command
kinesis-bulk-loader -stream test-kds01 -k hoge -m test -c 10 -n 100 -verbose
```

For more information, see GitHub [kinesis-bulk-loader](https://github.com/yokawasa/kinesis-bulk-loader).
