import org.apache.spark
import org.apache.spark.ml.Pipeline
object OCRPDF {

  // main
  def main(args: Array[String]): Unit = {
//    // load pdf
//    val pdfPath = PdfSummary.getFilePath
//
//    // Read pdf files as binary file
//    val df = spark.read
//      .format("binaryFile")
//      .load(pdfPath)

//    // Extract text from PDF text layout
//    val pdfToText = new PdfToText()
//      .setInputCol("content")
//      .setOutputCol("text")
//      .setSplitPage(false)
//
//    // Define pipeline
//    val pipeline = new Pipeline()
//    pipeline.setStages(Array(
//      pdfToImage,
//      ocr
//    ))
//
//    val modelPipeline = pipeline.fit(spark.emptyDataFrame)
//
//    val data = modelPipeline.transform(df)
//
//    data.show()
  }

}
