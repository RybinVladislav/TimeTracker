name := "TimeTracker"

version := "1.0"

lazy val `timetracker` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(cache, ws, specs2 % Test, filters,
                            "com.mohiva" %% "play-silhouette" % "3.0.4",
                            "net.codingwell" %% "scala-guice" % "4.0.1",
                            "com.iheart" %% "ficus" % "1.2.3",
                            "org.postgresql" % "postgresql" % "9.4.1208",
                            "com.typesafe.play" %% "play-slick" % "1.1.1",
                            "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1")

routesGenerator := InjectedRoutesGenerator

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  