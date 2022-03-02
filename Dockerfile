FROM openjdk:8u111-jdk-alpine

RUN apk update && apk add bash wget unzip

ENV ACTIVATOR_VERSION 1.3.12

RUN mkdir -p /opt/build /etc/app

WORKDIR /opt

RUN wget -q --show-progress \
  http://downloads.typesafe.com/typesafe-activator/$ACTIVATOR_VERSION/typesafe-activator-$ACTIVATOR_VERSION-minimal.zip && \
  unzip -qq typesafe-activator-$ACTIVATOR_VERSION-minimal.zip && \
  mv activator-$ACTIVATOR_VERSION-minimal activator && \
  ln -s /opt/activator/bin/activator /usr/local/bin/activator && \
  rm -f typesafe-activator-$ACTIVATOR_VERSION-minimal.zip

COPY conf /etc/app/

ADD . /opt/build/

WORKDIR /opt/build

RUN /opt/activator/bin/activator clean stage && \
  rm -f target/universal/stage/bin/*.bat && \
  mv target/universal/stage/bin/* target/universal/stage/bin/app && \
  mv target/universal /opt/app && \
  ln -s /opt/app/stage/logs /var/log/app && \
  rm -rf /opt/build /opt/activator $HOME/.ivy2

WORKDIR /opt/app

ENTRYPOINT ["/opt/app/stage/bin/app", "-Dconfig.file=/etc/app/application.conf"]

EXPOSE 9001