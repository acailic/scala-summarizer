import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.text.PDFTextStripper

import java.io.File


object PdfToText {

  System.setProperty("java.awt.headless", "true")

  def main(args: Array[String]) {
    val filename = PdfSummary.getFilePath

    val startPage = 1
    val endPage = 1

    // sanity check
    if (startPage > endPage) printUsageAndExit

    println(getTextFromPdf(startPage, endPage, filename))
  }

  def printUsageAndExit {
    println("")
    println("Usage: pdftotext startPage endPage filename")
    println("       (endPage must be >= startPage)")
    System.exit(1)
  }

  def getTextFromPdf(startPage: Int, endPage: Int, filename: String): Option[String] = {
    try {
      val pdf = PDDocument.load(new File(filename))
      val stripper = new PDFTextStripper
      stripper.setStartPage(startPage)
      stripper.setEndPage(endPage)
      Some(stripper.getText(pdf))
      pdf.close()
    } catch {
      case t: Throwable =>
        t.printStackTrace
        None
    } finally {
      // do nothing

    }
  }
}
