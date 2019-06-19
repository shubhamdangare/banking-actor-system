name := "banking-application-Akka-ActorSystem"
version := "0.1"
scalaVersion := "2.12.6"

lazy val makeDockerVersion = taskKey[Seq[File]]("Creates a docker-version.sbt file we can find at runtime.")
lazy val akkaVersion = "2.6.0-M2"

lazy val banking = Project("banking", file(".")).
  settings(
    Compile / mainClass := Some("com.knoldus.BankingSystem"),
    libraryDependencies ++= Seq(
      "com.typesafe.akka" %% "akka-actor" % akkaVersion,
      "com.typesafe.akka" %% "akka-testkit" % akkaVersion,
      "org.scalatest" %% "scalatest" % "3.0.5" % "test",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
      "ch.qos.logback" % "logback-classic" % "1.1.2",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.1.0",
      "log4j" % "log4j" % "1.2.17",
    )
  )
  .settings(dockerSettings: _*)
  .enablePlugins(JavaAppPackaging, DockerPlugin)

lazy val dockerSettings = Seq(
  dockerBaseImage := "openjdk:8-jre-alpine",
  dockerRepository := Some(organization.value),
  dockerEntrypoint := Seq("bin/%s" format executableScriptName.value),
  dockerBuildOptions := Seq("--force-rm", "-t", dockerAlias.value.versioned),
  makeDockerVersion := makeDockerVersionTaskImpl.value
)

lazy val makeDockerVersionTaskImpl = Def.task {
  val propFile = file(".") / "target/docker-image.version"
  val content = dockerAlias.value.versioned
  println(s"Docker-version: $content")
  IO.write(propFile, content)
  Seq(propFile)
}

import com.typesafe.sbt.packager.docker._

dockerCommands ++= Seq(
  Cmd("USER", "root"),
  Cmd("RUN", "apk add --update bash"),
  Cmd(
    "RUN",
    "sed -e app_mainclass=com.knoldus.BankingSystem",
    s"${(defaultLinuxInstallLocation in Docker).value}/bin/${executableScriptName.value}"
  )
)

