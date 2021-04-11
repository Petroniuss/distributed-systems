enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

val scala3Version = "3.0.0-RC2"
fork := true

name := "z-watcher"
version := "1.0.0"

scalaVersion := scala3Version
libraryDependencies ++= Seq(
  ("org.typelevel" %% "cats-core" % "2.4.2").cross(CrossVersion.for3Use2_13),
  ("io.monix" %% "monix" % "3.3.0").cross(CrossVersion.for3Use2_13),
  ("org.apache.zookeeper" % "zookeeper" % "3.7.0"),
  ("org.apache.logging.log4j" % "log4j-api" % "2.14.1"),
  ("org.apache.logging.log4j" % "log4j-core" % "2.14.1"),
) 
