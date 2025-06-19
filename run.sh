#!/bin/sh

java -cp ../lab8/hazelcast-5.1.3/lib/hazelcast-5.1.3.jar:. -Dhazelcast.config=../lab8/hazelcast-5.1.3/config/hazelcast.xml $1 $2 $3
