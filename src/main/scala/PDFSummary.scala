import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp.{DocumentAssembler, Finisher}
import com.typesafe.scalalogging.{AnyLogging, Logger}
import org.apache.spark.sql.{DataFrame, Encoder, Encoders, SparkSession}

import java.io.File

object PdfSummary {
  val spark = SparkSession.builder().appName("PdfSummary").master("local").getOrCreate()
  val logger = Logger("pdf-summary")
  def main(args: Array[String]): Unit = {

    // Load the PDF file into a DataFrame

    val pdf: DataFrame = getPdfContent
    // get few wo
    // Convert the PDF file into a Spark NLP document
    val document = new DocumentAssembler().setInputCol("content").setOutputCol("document").transform(pdf)
    // print info about document
    logger.info(s"Document schema: ${document.schema}")
    logger.info(s"Document columns: ${document.columns.mkString(", ")}")

    // Use Spark NLP's CommonSenseParserModel to extract meaning from the text
    //document, pos, token
    val parser = new DependencyParserModel().setInputCols("document").setOutputCol("parser")
    val parsed = parser.transform(document)

    // Use the Finisher to extract the summarized text
    val finisher = new Finisher().setInputCols("parser").setIncludeMetadata(false)
    import spark.implicits._
    val summary = finisher.transform(parsed).select("finished_parser").map(r => r.getString(0)).take(5).mkString(" ")

    // Print the summarized text
    println(summary)
  }

  def getPdfContent = {
    var pathToPdfFile: String = getFilePath
    logger.info(s"Loading PDF file from $pathToPdfFile")
    // assert pathToPdfFile exists
    assert(new File(pathToPdfFile).exists, "File does not exist")
    val pdf = spark.read.format("binaryFile").load(pathToPdfFile)
    logger.info(s"Loaded PDF file with ${pdf.count} rows")
    // display pdf schema
    pdf.printSchema()
    logger.info(s"PDF schema: ${pdf.schema}")
    logger.info(s"PDF columns: ${pdf.columns.mkString(", ")}")
    //display length of pdf
    logger.info(s"PDF length: ${pdf.first().getAs[Array[Byte]]("content").length}")
    // convert content column to decoded string
    import org.apache.spark.sql.functions._
    import spark.implicits._
    pdf.withColumn("content", decode(col("content"), "UTF-8"))
  }

   def getFilePath = {
    //var pathToPdfFile = "/Users/aleksandarilic/Documents/PDFS/Software Architechture/Monolith-to-Microservices.pdf"
    //var pathToPdfFile = "/Users/aleksandarilic/Documents/PDFS/Software Architechture/Competency-framework.pdf"
    var pathToPdfFile = "/Users/aleksandarilic/Documents/PDFS/Software Architechture/[CLOUD][Cloud Architecture Patterns].pdf"
    pathToPdfFile
  }
}
