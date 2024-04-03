FROM anapsix/alpine-java:8_jdk
WORKDIR /app
COPY . /app
RUN apk add --no-cache tzdata zip
ENV TZ=Asia/Jakarta
RUN cp /usr/share/zoneinfo/Asia/Jakarta /etc/localtime
RUN ls -lah target/
RUN ls -lah target/universal/
EXPOSE 9001
ENTRYPOINT ["/app/target/universal/stage/bin/whiz-api", "-Dconfig.file=/app/target/universal/stage/conf/application.conf", "-Dhttp.port=9001", "-Duser.timezone=Asia/Jakarta", "-J-Xms128M", "-J-Xmx1G", "-DapplyEvolutions.default=true", "-DapplyDownEvolutions.default=true"]
