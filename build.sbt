name := "scala-summarizer"

version := "0.1"

scalaVersion := "2.12.17"

libraryDependencies += "com.johnsnowlabs.nlp" %% "spark-nlp-m1" % "4.2.8"
// libraryDependencies += "com.johnsnowlabs.nlp" %% "spark-ocr" % "4.2.8"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5"

// add pdf box dependancy
libraryDependencies += "org.apache.pdfbox" % "pdfbox" % "2.0.24"
val sparkVer = "3.3.1"


libraryDependencies ++= {
  Seq(
    "org.apache.spark" %% "spark-core" % sparkVer ,
    "org.apache.spark" %% "spark-mllib" % sparkVer  )
}
