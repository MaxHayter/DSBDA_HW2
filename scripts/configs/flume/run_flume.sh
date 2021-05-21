#!/bin/bash

flume-ng agent --conf $FLUME_HOME/conf/ -f $FLUME_HOME/conf/flume-mapper.conf \
 --no-reload-conf \
 -Dflume.root.logger=ERROR,console -n $FLUME_AGENT_NAME > /dev/null &

flume-ng agent --conf $FLUME_HOME/conf/ -f $FLUME_HOME/conf/flume.conf \
 --no-reload-conf \
 -Dflume.root.logger=ERROR,console -n $FLUME_AGENT_NAME
