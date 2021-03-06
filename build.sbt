import com.typesafe.sbt.SbtNativePackager.autoImport.packageName
import com.typesafe.sbt.packager.docker.DockerPlugin.autoImport.dockerBaseImage
import sbt.addCompilerPlugin
import sbt.Keys._

name := "fintech-trading"

version := "0.0.1-SANPSHOT"

val runtimeLibrarySettings = Seq(
  crossScalaVersions := Seq("2.11.12", "2.12.7"),
  scalaVersion := crossScalaVersions.value.head
)

lazy val root = (
  project.in(file("."))
    aggregate(websocketServer, websocketClientKafka, flinkProcessor,
    restCassandra, sparkProcessor2)
  )

lazy val metaParadiseVersion = "3.0.0-M11"
lazy val catsMTLVersion = "0.4.0"
lazy val catsVersion = "1.4.0"
lazy val log4CatsVersion = "0.2.0"
lazy val circeDerivationVersion = "0.11.0-M1"
lazy val circeVersion = "0.11.1"
lazy val pureConfigVersion = "0.10.0"
lazy val enumeratumVersion = "1.5.13"

lazy val flinkVersion = "1.7.2"

lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.4"
lazy val scalaCheck = "org.scalacheck" %% "scalacheck" % "1.13.4"

lazy val sparkVersion = "2.4.1"
lazy val sparkCore = "org.apache.spark" %% "spark-core" % sparkVersion
lazy val sparkStreaming = "org.apache.spark" %% "spark-streaming" % sparkVersion
lazy val kafkaStreaming = "org.apache.spark" %% "spark-streaming-kafka-0-10" % sparkVersion
lazy val kafkaSql = "org.apache.spark" %% "spark-sql-kafka-0-10" % sparkVersion
lazy val sparkSql = "org.apache.spark" %% "spark-sql" % sparkVersion
lazy val typeSafeConfig = "com.typesafe" % "config" % "1.3.2"
lazy val logging = "com.typesafe.scala-logging" %% "scala-logging" % "3.7.2"
lazy val sparkTests = "com.holdenkarau" %% "spark-testing-base" % "2.2.0_0.8.0"
lazy val cats = "org.typelevel" %% "cats-core" % catsVersion
lazy val catsLaws = "org.typelevel" %% "cats-laws" % catsVersion
lazy val catsTests = "org.typelevel" %% "cats-testkit" % catsVersion
lazy val datastaxCassandra = "com.datastax.cassandra"  % "cassandra-driver-core" % "3.6.0"
lazy val datastaxCassandraSpark = "com.datastax.spark" %% "spark-cassandra-connector" % sparkVersion



lazy val websocketServer = (project in file("websocket-server"))
  .dependsOn(model)
  .settings(
     baseSettings,
    compilerPlugins,
    libraryDependencies ++= Seq(
      "io.chrisdavenport" %% "log4cats-core" % log4CatsVersion,
      "io.chrisdavenport" %% "log4cats-slf4j" % log4CatsVersion,
      cats, 
      "io.chrisdavenport" %% "cats-par" % "0.2.0",
      "io.monix" %% "monix" % "3.0.0-RC2"
    ) ++ commonDependencies ++ akka,
    mainClass in Compile := Some("App"),
    packageName in Docker := "websocket-server",
    dockerBaseImage := "openjdk:8",
    dockerUpdateLatest := true
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)


lazy val websocketClientKafka = (project in file("websocket-client-kafka"))
  .dependsOn(model)
  .settings(
     baseSettings,
    compilerPlugins,
    libraryDependencies ++= commonDependencies ++ akka,
    mainClass in Compile := Some("WebSocketClientToKafka"),
    packageName in Docker := "websocket-client",
    dockerBaseImage := "openjdk:8",
    dockerUpdateLatest := true
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)

lazy val restCassandra = (project in file("rest-cassandra"))
  .settings(
     baseSettings,
    addCompilerPlugin("org.scalameta" % "paradise" % "3.0.0-M11" cross CrossVersion.patch),
    addCompilerPlugin("com.olegpy"    %% "better-monadic-for" % "0.3.0-M4"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0"), 
    libraryDependencies ++= Seq(
      "io.chrisdavenport" %% "log4cats-slf4j" % log4CatsVersion,
      "com.github.mpilquist" %% "simulacrum" % "0.16.0",
      "io.chrisdavenport" %% "cats-par" % "0.2.0",
      "io.monix" %% "monix" % "3.0.0-RC2",
      "com.typesafe" % "config" % "1.3.2",
      "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.8",
      "com.typesafe.akka" %% "akka-stream" % "2.5.12",
      "com.typesafe.akka" %% "akka-http" % "10.1.8",
      "com.typesafe.akka" %% "akka-http-core" % "10.1.8",
      "io.getquill" %% "quill-cassandra-monix" % "3.1.0",
      "de.heikoseeberger" %% "akka-http-circe" % "1.24.3",
      "io.circe" %% "circe-core" % circeVersion,
      "io.circe" %% "circe-derivation" % circeDerivationVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-java8" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "com.beachape" %% "enumeratum" % enumeratumVersion,
      "com.beachape" %% "enumeratum-circe" % enumeratumVersion,
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
      "ch.qos.logback" % "logback-classic" % "1.2.3"
    ),
    mainClass in Compile := Some("QuillQueriesToRestApi"),
    packageName in Docker := "rest-cassandra",
    dockerExposedPorts ++= Seq(8077),
    dockerBaseImage := "openjdk:8",
    dockerUpdateLatest := true ,
   
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)

lazy val sparkProcessor2 = (project in file("spark-processor2"))
  .settings(
  runtimeLibrarySettings,
    baseSettings,
    libraryDependencies ++= Seq(
      sparkCore             /* % Provided*/,
      sparkStreaming        /* % Provided*/,
      sparkSql              /* % Provided*/,
      kafkaStreaming,
      kafkaSql,
      typeSafeConfig,
      datastaxCassandra,
        datastaxCassandraSpark,
      cats,
      logging,
      scalaTest % Test,
      scalaCheck % Test,
      catsLaws % Test,
      catsTests % Test,
       sparkTests % Test,
      "com.github.pureconfig" %% "pureconfig" % pureConfigVersion
    ),
    mainClass in Compile := Some("SparkAppRunner"),
    packageName in Docker := "spark-processor2",
    dockerBaseImage := "openjdk:8",
    dockerUpdateLatest := true
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)

lazy val flinkProcessor = (project in file("flink-processor"))
  .dependsOn(model)
  .settings(
     baseSettings,
    compilerPlugins,
    libraryDependencies ++= commonDependencies ++ flink,
    mainClass in Compile := Some("FlinkProcessTopic"),
    packageName in Docker := "flink-processor",
    dockerBaseImage := "openjdk:8",
    dockerUpdateLatest := true
  )
  .enablePlugins(JavaAppPackaging)
  .enablePlugins(DockerPlugin)


lazy val model = (project in file("model"))
  .settings(
     baseSettings,
    libraryDependencies ++= circe,
    libraryDependencies ++= Seq(
      "com.beachape" %% "enumeratum" % enumeratumVersion,
      "com.beachape" %% "enumeratum-circe" % enumeratumVersion
    ),
    addCompilerPlugin("org.scalameta" % "paradise" % metaParadiseVersion cross CrossVersion.full),
    scalacOptions += "-Xplugin-require:macroparadise",
    mainClass in Compile := Some("model")
  )

lazy val commonDependencies = Seq(
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
  "com.github.pureconfig" %% "pureconfig" % pureConfigVersion,
  "com.typesafe" % "config" % "1.3.2",
  "ch.qos.logback" % "logback-classic" % "1.2.3"
)


lazy val flink = Seq(
  "org.apache.flink" %% "flink-scala",
  "org.apache.flink" %% "flink-streaming-scala",
  "org.apache.flink" %% "flink-connector-kafka-0.11",
  "org.apache.flink" %% "flink-connector-cassandra"
).map(_ % flinkVersion)

lazy val akka = Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.22",
  "com.typesafe.akka" %% "akka-http" % "10.1.1",
  "com.typesafe.akka" %% "akka-http-core" % "10.1.1",
  "com.typesafe.akka" %% "akka-stream-kafka" % "1.0.1",
  "de.heikoseeberger" %% "akka-http-circe" % "1.25.2"
)

lazy val circe = Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser",
  "io.circe" %% "circe-java8"
).map(_ % circeVersion) :+ "io.circe" %% "circe-derivation" % circeDerivationVersion


lazy val baseSettings = Seq(
  scalaVersion in ThisBuild := "2.12.7",
  resolvers ++= Seq(
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("ovotech", "maven")
  ),
  scalacOptions ++= commonScalacOptions,
  scalacOptions ++= Seq("-Xmax-classfile-name", "128"),
  parallelExecution in Test := false,
  sources in(Compile, doc) := Nil,
  publishTo := None,
  cancelable in Global := true
)

lazy val compilerPlugins = Seq(
  addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.0"),
  addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.0-M4")
)

lazy val commonScalacOptions = Seq(
  "-deprecation",
  "-encoding",
  "UTF-8",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:experimental.macros",
  "-unchecked",
  "-Xfatal-warnings",
  "-Xlint",
  "-Yno-adapted-args",
  //"-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-value-discard",
  "-Xfuture",
  "-Ypartial-unification"
)