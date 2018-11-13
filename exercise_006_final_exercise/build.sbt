organization in ThisBuild := "com.example"
version in ThisBuild := "1.0-SNAPSHOT"

// the Scala version that will be used for cross-compiled libraries
scalaVersion in ThisBuild := "2.12.4"


val step = "06"

def project(id: String) = Project(s"${id}_${step}", base = file(id))
  .settings(javacOptions in compile ++= Seq("-encoding", "UTF-8", "-source", "1.8", "-target", "1.8", "-Xlint:unchecked", "-Xlint:deprecation"))

lazy val `wallet-api` = project("wallet-api")
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `wallet-impl` = project("wallet-impl")
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomJavadslTestKit,
      lombok,
      "org.mongodb" % "mongodb-driver-reactivestreams" % "1.7.0"
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`wallet-api`)

lazy val `magicmoneymint-api` = project("magicmoneymint-api")
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslApi,
      lombok
    )
  )

lazy val `magicmoneymint-impl` = project("magicmoneymint-impl")
  .enablePlugins(LagomJava)
  .settings(common: _*)
  .settings(
    libraryDependencies ++= Seq(
      lagomJavadslPersistenceCassandra,
      lagomJavadslKafkaBroker,
      lagomJavadslTestKit,
      lombok
    )
  )
  .settings(lagomForkedTestSettings: _*)
  .dependsOn(`magicmoneymint-api`, `wallet-api`)


val lombok = "org.projectlombok" % "lombok" % "1.16.18"

def common = Seq(
  javacOptions in compile += "-parameters"
)

aggregateProjects(`wallet-api`, `wallet-impl`, `magicmoneymint-api`, `magicmoneymint-impl`)