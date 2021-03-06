val dottyVersion = "3.0.0-RC1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "server",
    version := "1.0.0",

    scalaVersion := dottyVersion,

    libraryDependencies ++= Seq(
        ("io.monix" %% "monix" % "3.3.0").withDottyCompat(scalaVersion.value),
        ("org.typelevel" %% "cats-core" % "2.4.2").withDottyCompat(scalaVersion.value)
    )
  )
