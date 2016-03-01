name := "TimeTracker"

version := "1.0"

lazy val `timetracker` = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(cache, ws, specs2 % Test,
                            "org.postgresql" % "postgresql" % "9.4.1208",
                            "be.objectify" %% "deadbolt-scala" % "2.4.3",
                            "com.typesafe.play" %% "play-slick" % "1.1.1",
                            "com.typesafe.play" %% "play-slick-evolutions" % "1.1.1")

unmanagedResourceDirectories in Test <+=  baseDirectory ( _ /"target/web/public/test" )  

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"  