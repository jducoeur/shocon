import SonatypeKeys._

sonatypeSettings

def preventPublication(p: Project) =
  p.settings(
    publish :=(),
    publishLocal :=(),
//    publishSigned :=(),
//    publishLocalSigned :=(),
    publishArtifact := false,
    publishTo := Some(Resolver.file("Unused transient repository", target.value / "fakepublish")),
    packagedArtifacts := Map.empty)

lazy val root = preventPublication(project.in(file("."))).
  enablePlugins(ScalaJSPlugin).
  aggregate(shoconJS, shoconJVM)

lazy val shocon = crossProject.in(file(".")).
  settings(
    name := "shocon",
    organization := "org.querki",
    version := "0.2",
    scalaVersion := "2.11.7",
    libraryDependencies += "com.lihaoyi" %%% "fastparse" % "0.2.1",
    
    publishTo := {
	  val nexus = "https://oss.sonatype.org/"
	  if (isSnapshot.value)
	    Some("snapshots" at nexus + "content/repositories/snapshots")
	  else
	    Some("releases" at nexus + "service/local/staging/deploy/maven2")
	},
	homepage := Some(url("http://www.querki.net/")),
	licenses += ("MIT License", url("http://www.opensource.org/licenses/mit-license.php")),
	scmInfo := Some(ScmInfo(
	  url("https://github.com/jducoeur/shocon"),
	  "scm:git:git@github.com/jducoeur/shocon.git",
	  Some("scm:git:git@github.com/jducoeur/shocon.git"))),
	publishMavenStyle := true,
	pomExtra := (
	  <developers>
	    <developer>
	      <id>jducoeur</id>
	      <name>Mark Waks</name>
	      <url>https://github.com/jducoeur/</url>
	    </developer>
	  </developers>
	),
	pomIncludeRepository := { _ => false }
  )

lazy val shoconJS = shocon.js
lazy val shoconJVM = shocon.jvm
