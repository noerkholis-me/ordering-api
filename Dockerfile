FROM mozilla/sbt:8u292_1.5.7
WORKDIR /app
COPY . /app
RUN sbt clean stage
EXPOSE 9001
ENTRYPOINT ["/app/target/universal/stage/bin/whiz-api", "-Dconfig.file=/app/target/universal/stage/conf/application.conf", "-Dhttp.port=9001", "-Duser.timezone=Asia/Jakarta", "-J-Xms128M", "-J-Xmx1G", "-DapplyEvolutions.default=true", "-DapplyDownEvolutions.default=true"]
