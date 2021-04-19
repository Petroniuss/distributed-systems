enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

val scala3Version = "3.0.0-RC2"
fork := true

name := "astra-link"
version := "1.0.0"

scalaVersion := scala3Version
libraryDependencies ++= Seq(
  ("org.typelevel" %% "cats-core" % "2.4.2").cross(CrossVersion.for3Use2_13),
  ("io.monix" %% "monix" % "3.3.0").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-actor-typed" % "2.6.14").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-slf4j" % "2.6.14").cross(CrossVersion.for3Use2_13),
  ("ch.qos.logback" % "logback-classic" % "1.2.3")
)
