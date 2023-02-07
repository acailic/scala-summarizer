import PdfToText.getTextFromPdf
import com.johnsnowlabs.nlp.DocumentAssembler
import com.johnsnowlabs.nlp.annotator.T5Transformer
import com.typesafe.scalalogging.Logger
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession

object Transformer {
  val spark = SparkSession.builder().appName("PdfSummary").master("local").getOrCreate()
  val logger = Logger("transformer")
  // main
  def main(args: Array[String]): Unit = {

//    val pdf = PdfSummary.getPdfContent
//    // print info about document
//    logger.info(s"Document schema: ${pdf.schema}")
//    logger.info(s"Document columns: ${pdf.columns.mkString(", ")}")
//    // display content of document
//    logger.info("Displaying content of document")
//    pdf.select("content").show(truncate = false)

    /// create data frame from text
    val pdf = spark.createDataFrame(Seq(
      (1, getTextFromPdf(21,31,PdfSummary.getFilePath)),
    )).toDF("id", "content")

    val documentAssembler = new DocumentAssembler()
      .setInputCol("content")
      .setOutputCol("documents")

    val t5 = T5Transformer
      .pretrained("t5_small")
      .setTask("summarize:")
      .setInputCols(Array("documents"))
      .setOutputCol("summaries")
    val pipeline = new Pipeline().setStages(Array(documentAssembler, t5))
    val model = pipeline.fit(pdf)
    val results = model.transform(pdf)
    logger.info(s"Results schema: ${results.schema}")
    results.select("summaries.result").show(truncate = false)
    logger.info("Done!")
  }

}
