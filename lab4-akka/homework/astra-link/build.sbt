enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

val scala3Version = "3.0.0-RC1"
fork := true

name := "astra-link"
version := "1.0.0"

scalaVersion := scala3Version
libraryDependencies ++= Seq(
  // cats
//  ("org.typelevel" %% "cats-core" % "2.4.2").cross(CrossVersion.for3Use2_13),

  // monix
//  ("io.monix" %% "monix" % "3.3.0").cross(CrossVersion.for3Use2_13),

  // akka
  ("com.typesafe.akka" %% "akka-actor-typed" % "2.6.14").cross(CrossVersion.for3Use2_13),
  ("com.typesafe.akka" %% "akka-slf4j" % "2.6.14").cross(CrossVersion.for3Use2_13),
  ("ch.qos.logback" % "logback-classic" % "1.2.3"),

//   database
  ("org.tpolecat" %% "doobie-core"      % "0.12.1"),
  ("org.tpolecat" %% "doobie-h2"        % "0.12.1"),
  ("org.tpolecat" %% "doobie-hikari"    % "0.12.1")
)
