import PdfToText.getTextFromPdf
import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.annotator.T5Transformer
import com.typesafe.scalalogging.Logger
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession

object Transformer {
  val spark = SparkSession.builder().appName("PdfSummary").master("local").getOrCreate()
  val logger = Logger("transformer")
  val outputDir = "/Users/aleksandarilic/Documents/github/acailic/scala-summarizer/output"

  // main
  def main(args: Array[String]): Unit = {
    // TODO: edit path, start page and end page
    val filePath = PdfSummary.getFilePath
    val chapterName="Chapter 1"
    val startPage = 21
    val endPage = 31


    val fileName = filePath.split("/").last.replace(".pdf", "")
    val outputName = outputDir + "/" + fileName + "-"+chapterName+".txt"
    /// create data frame from text
    val pdf = spark.createDataFrame(Seq(
      (1, getTextFromPdf(startPage,endPage,filePath)),
    )).toDF("id", "content")

    val documentAssembler = new DocumentAssembler()
      .setInputCol("content")
      .setOutputCol("documents")

    val tokensToIgnore = ",:;?!\"'-+*/|&^%$#@~`_=\\t\\n\\r\\s+".map(_.toInt).toArray
    val t5 = T5Transformer
      .pretrained("t5_small", "en")
      .setTask("summarize:")
      .setInputCols(Array("documents"))
      .setOutputCol("summaries")
      .setMaxOutputLength(600)
      .setMinOutputLength(200)
      .setDoSample(true)
      .setIgnoreTokenIds(tokensToIgnore) // ignore tokens like . , : ; ? ! " ' - + * / | & ^ % $ # @ ~ ` _ = { } \t \n \r \s+

    val pipeline = new Pipeline().setStages(Array(documentAssembler, t5))
    val model = pipeline.fit(pdf)
    val results = model.transform(pdf)
    //results.select("summaries.result").show(truncate = false)
    saveColumnToTextFile(results.select("summaries.result"), "result", outputName)
    logger.info("Done! Saved to: " + outputName)
  }


  // save column of data frame to text file
  def saveColumnToTextFile(df: org.apache.spark.sql.DataFrame, column: String, path: String): Unit = {
    val arrayOfStrings = df.select(column)
    // convert wrapped array to string
    val collectedString = arrayOfStrings.collect().map(_.toString).mkString("\n")
    logger.info("String to save: " + collectedString)
    // write to file
    reflect.io.File(path).writeAll(collectedString)
  }

}
