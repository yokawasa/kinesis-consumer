# Change Log

All notable changes to the "kinesis-consumer" will be documented in this file.

## 0.0.4

- Add ageOfRecordInMillisFromArrival and data field on processing record log

## 0.0.3

- Add initialPositionInStream config param (default 1sec (1000L))
- Add maxRecrods config param (default 10sec (10000L))
- Add initialPositionInStream config param (LATEST or TRIM_HORIZON, default LATEST)
- Add idleTimeBetweenReadsInMillis config param (default 1 sec (1000L))

## 0.0.2

- Run consumer app with -Djavax.net.ssl.trustStore parameter to resolve "unable to find valid certification path to requested target" issue

## 0.0.1

- Initial release
