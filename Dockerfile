FROM anapsix/alpine-java:8_jdk
WORKDIR /app
COPY . /app
RUN apk add --no-cache tzdata zip
ENV TZ=Asia/Jakarta
RUN cp /usr/share/zoneinfo/Asia/Jakarta /etc/localtime
RUN ls -lah target/
RUN ls -lah target/universal/
RUN unzip -o target/universal/whiz-api-1.0-SNAPSHOT.zip -d target/universal/
RUN ls -lah target/universal/whiz-api-1.0-SNAPSHOT/bin
EXPOSE 9001
ENTRYPOINT ["./target/universal/whiz-api-1.0-SNAPSHOT/bin/whiz-api", "-Dconfig.file=conf/application-staging.conf", "-Dhttp.port=9001", "-Duser.timezone=Asia/Jakarta", "-J-Xms128M", "-J-Xmx1G", "-DapplyEvolutions.default=true", "-DapplyDownEvolutions.default=true"]
