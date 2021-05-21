docker run --env FLUME_AGENT_NAME=docker_flume \
  --env FLUME_CONF_FILE=/opt/lib/flume/conf/flume.conf \
  --env IGNITE_CLIENT_CONF_FILE=/opt/lib/flume/conf/ignite-config.xml \
  --env IGNITE_CONF_FILE=/opt/ignite/apache-ignite-2.10.0-bin/my_config/ignite-config.xml \
  --env APPLICATION=/opt/lib/flume/plugins.d/ignite-sink/lib/BigData-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --volume "$(pwd)"/configs/flume/:/opt/lib/flume/conf/ \
  --volume "$(pwd)"/configs/flume/ignite-config.xml:/opt/lib/flume/ignite-config.xml \
  --volume "$(pwd)"/configs/ignite/:/opt/ignite/apache-ignite-2.10.0-bin/my_config/ \
  --volume "$(pwd)"/inputs:/opt/lib/flume/inputs \
  --volume "$( dirname "$PWD" )"/target/BigData-1.0-SNAPSHOT-jar-with-dependencies.jar:/opt/lib/flume/plugins.d/ignite-sink/lib/BigData-1.0-SNAPSHOT-jar-with-dependencies.jar \
  --name flume_ignite \
  flume_ignite:latest