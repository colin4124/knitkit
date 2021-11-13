import ammonite.ops._
import mill._
import mill.scalalib._
import publish._

trait CommonModule extends ScalaModule {
  def scalaVersion = "2.13.6"
  def scalacOptions = Seq(
    "-language:reflectiveCalls", // reflective access of structural type member value
    "-deprecation",  // Emit warning and location for usages of deprecated APIs.
    "-explaintypes", // Explain type errors in more detail.
    "-feature",      // Emit warning and location for usages of features that should
    "-unchecked",    // Enable additional warnings where generated code depends on assumptions.
  )
}

object knitkit extends CommonModule with PublishModule {
  def millSourcePath = super.millSourcePath / ammonite.ops.up

  def ivyDeps = Agg(
    ivy"org.apache.commons:commons-text:1.8",
  )

  def publishVersion = "0.3.0"

  def pomSettings = PomSettings(
    description = "Knitkit",
    organization = "io.github.colin4124",
    url = "https://github.com/colin4124",
    licenses = Seq(License.`Apache-2.0`),
    versionControl = VersionControl.github("colin4124", "knitkit"),
    developers = Seq(
      Developer("colin4124", "Leway Colin", "https://github.com/colin4124")
    )
  )
}
