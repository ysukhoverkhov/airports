import sbtassembly.AssemblyPlugin.defaultUniversalScript

name := "airports"
organization in ThisBuild := "com.example"
scalaVersion in ThisBuild := "2.12.6"


// PROJECTS

lazy val global = project
  .in(file("."))
  .settings(settings)
  .aggregate(
    common,
    web
  )

lazy val common = project
  .settings(
    name := "common",
    version := "0.0.1-SNAPSHOT",
    settings,
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full),
    libraryDependencies ++= commonDependencies
  )

lazy val web = project
  .settings(
    name := "web",
    version := "0.0.1-SNAPSHOT",
    settings,
    assemblySettings,
    libraryDependencies ++= webDependencies
  )
  .dependsOn(
    common
  )


// DEPENDENCIES

lazy val deps =
  new {
    val Http4sVersion = "0.18.14"
    val Specs2Version = "4.2.0"
    val LogbackVersion = "1.2.3"
    val ScalaLoggingVersion = "3.9.0"
    val SimulacrumVersion = "0.13.0"
    val CSVParserVersion = "0.11.4"
    val ScoptVersion = "3.7.0"
    val CirceVersion = "0.9.3"
  }


lazy val commonDependencies = Seq(
  "org.http4s"                 %% "http4s-blaze-server" % deps.Http4sVersion,
  "org.http4s"                 %% "http4s-circe"        % deps.Http4sVersion,
  "org.http4s"                 %% "http4s-dsl"          % deps.Http4sVersion,
  "com.github.mpilquist"       %% "simulacrum"          % deps.SimulacrumVersion,
  "zamblauskas"                %% "scala-csv-parser"    % deps.CSVParserVersion,
  "org.specs2"                 %% "specs2-core"         % deps.Specs2Version % Test,
  "org.specs2"                 %% "specs2-scalacheck"   % deps.Specs2Version % Test,
  "ch.qos.logback"             %  "logback-classic"     % deps.LogbackVersion,
)

lazy val webDependencies = commonDependencies ++ Seq(
  "com.typesafe.scala-logging" %% "scala-logging"       % deps.ScalaLoggingVersion,
  "io.circe"                   %% "circe-core"          % deps.CirceVersion,
  "io.circe"                   %% "circe-generic"       % deps.CirceVersion
)


// SETTINGS

lazy val settings = Seq(
  scalacOptions ++= compilerOptions,
  resolvers ++= Seq(
    "Local Maven Repository" at "file://" + Path.userHome.absolutePath + "/.m2/repository",
    Resolver.sonatypeRepo("releases"),
    Resolver.sonatypeRepo("snapshots"),
    Resolver.bintrayRepo("zamblauskas", "maven")
  )
)

lazy val compilerOptions = Seq(
  "-unchecked",
  "-feature",
  "-language:existentials",
  "-language:higherKinds",
  "-language:implicitConversions",
  "-language:postfixOps",
  "-deprecation",
  "-encoding",
  "utf8",
  "-Ypartial-unification",
  "-Yrangepos"
)


lazy val assemblySettings = Seq(
//  assemblyOption in assembly := (assemblyOption in assembly).value.copy(prependShellScript = Some(defaultUniversalScript(shebang = false))),
  assemblyJarName in assembly := s"${name.value}-${version.value}.jar",
  assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case _                             => MergeStrategy.first
  }
)
