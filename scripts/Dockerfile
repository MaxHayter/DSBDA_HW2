FROM openjdk:8-alpine

RUN apk add --update bash && rm -rf /var/cache/apk/*

ENV FLUME_HOME /opt/lib/flume
ENV FLUME_AGENT_NAME agent

ENV PATH=$PATH:$FLUME_HOME/bin

RUN mkdir -p /opt/lib && \
    wget -q http://apache-mirror.rbc.ru/pub/apache/flume/1.9.0/apache-flume-1.9.0-bin.tar.gz -O /opt/lib/apache-flume-1.9.0-bin.tar.gz && \
    tar xzf /opt/lib/apache-flume-1.9.0-bin.tar.gz -C /opt/lib && \
    mv /opt/lib/apache-flume-1.9.0-bin $FLUME_HOME && \
    rm /opt/lib/apache-flume-1.9.0-bin.tar.gz && \
    mv $FLUME_HOME/conf/flume-conf.properties.template $FLUME_HOME/conf/flume-conf.properties

# Ignite version
ENV IGNITE_VERSION 2.10.0

# Ignite home
ENV IGNITE_HOME /opt/ignite/apache-ignite-${IGNITE_VERSION}-bin
ENV IGNITE_CONF_FILE ${IGNITE_HOME}/config/ignite-config.xml

RUN apk --no-cache add curl \
        unzip \
        maven \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /opt/ignite

COPY ./pom.xml /opt/ignite

RUN mvn dependency:copy-dependencies -DoutputDirectory=/opt/lib/flume/plugins.d/ignite-sink/libext/

RUN curl https://dist.apache.org/repos/dist/release/ignite/${IGNITE_VERSION}/apache-ignite-${IGNITE_VERSION}-bin.zip -o ignite.zip \
    && unzip ignite.zip > /dev/null \
    && rm ignite.zip

# Copy sh files and set permission
COPY configs/flume/run_flume.sh $IGNITE_HOME/

RUN chmod +x $IGNITE_HOME/run_flume.sh

EXPOSE 11211 47100 47500 49112

CMD $IGNITE_HOME/bin/ignite.sh $IGNITE_CONF_FILE
