name := """whiz-api"""

version := "1.0-SNAPSHOT"

lazy val whizlizApi = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  javaJdbc,
  javaEbean,
  cache,
  javaWs,
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  "com.wordnik" %% "swagger-play2" % "1.3.12" exclude("org.reflections", "reflections"),
  "org.webjars" % "swagger-ui" % "2.1.8-M1",
  "org.reflections" % "reflections" % "0.9.8" notTransitive (),
  "commons-io" % "commons-io" % "2.4",
  "com.sendgrid" % "sendgrid-java" % "4.2.1",
  "com.squareup.okhttp3" % "mockwebserver" % "4.5.0",
  "org.apache.poi" % "poi" % "3.15","org.apache.poi" % "poi-ooxml" % "3.15",
  "org.jsoup" % "jsoup" % "1.7.2",
  "com.amazonaws" % "aws-java-sdk" % "1.3.11",
  "com.sendinblue" % "sib-api-v3-sdk" % "5.1.0",
  "jakarta.xml.bind" % "jakarta.xml.bind-api" % "2.3.2",
  "javax.xml.ws" % "jaxws-api" % "2.3.1",
  "org.projectlombok" % "lombok" % "1.16.16",
  "org.json" % "json" % "20210307",
  ("com.clever-age" % "play2-elasticsearch" % "1.4-SNAPSHOT")
          .exclude("com.typesafe.play", "play-functional_2.4.6")
          .exclude("com.typesafe.akka", "akka-actor_2.10")
          .exclude("com.typesafe.play", "play-json_2.10")
          .exclude("com.typesafe.play", "play_2.10")
          .exclude("com.typesafe.play", "play-iteratees_2.10")
          .exclude("com.typesafe.akka", "akka-slf4j_2.10")
          .exclude("org.scala-stm", "scala-stm_2.10")
          .exclude("com.typesafe.play", "play-datacommons_2.10")
          .exclude("com.typesafe.play", "play-java_2.10")
)

resolvers +=   "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

libraryDependencies += filters

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java

//fork in run := true