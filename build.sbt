import java.nio.file.Files

name := "jiayiwei.textVary"

version := "1.0"

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  "com.lihaoyi" %% "fastparse" % "0.3.7",
  "com.lihaoyi" %% "utest" % "0.4.3" % "test",
  "com.lihaoyi" %% "scalatags" % "0.5.4",
  "com.lihaoyi" %% "scalarx" % "0.3.1"
)


testFrameworks += new TestFramework("utest.runner.Framework")

mainClass in (Compile, run) := Some("jiayiwei.essay_ui.UITest")

mainClass in assembly := Some("jiayiwei.essay_ui.UIMain")
assemblyJarName in assembly := "textVary.jar"
test in assembly := {}




lazy val copyRes = TaskKey[Unit]("copyRes")
copyRes <<= (baseDirectory, target, version) map {
  (base, trg, version) =>
    val packDir = "textVary-" + version
    val dataDir = s"$packDir/standard-data"

    def mkDir(f: File) = {
      if(!f.isDirectory){
        f.mkdir()
      }
    }

    def copyFile(from: File, to: File) = {
      if(to.exists()){
        to.delete()
      }
      Files.copy(from.toPath, to.toPath)
    }

    mkDir(new File(trg,packDir))
    mkDir(new File(trg,dataDir))

    new File(base, "standard-data").listFiles().foreach(
      file => copyFile(file, new File(trg, dataDir+"/"+file.name))
    )
    copyFile(new File(trg, "scala-2.11/textVary.jar"), new File(trg, packDir + "/textVary.jar"))
}
