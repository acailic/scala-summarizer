import com.johnsnowlabs.nlp.annotator._
import com.johnsnowlabs.nlp.base._
import com.johnsnowlabs.nlp.pretrained.PretrainedPipeline
import org.apache.spark.ml.Pipeline
import org.apache.spark.sql.SparkSession

object Main {
  val spark: SparkSession = SparkSession.builder
    .appName("spark-nlp-starter")
    .master("local[*]")
    .getOrCreate

  def main(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    val document = new DocumentAssembler()
      .setInputCol("text")
      .setOutputCol("document")

    val sentenceDetector = new SentenceDetector()
      .setInputCols("document")
      .setOutputCol("sentence")

    val token = new Tokenizer()
      .setInputCols("sentence")
      .setOutputCol("token")

    val posTagger = PerceptronModel
      .pretrained()
      .setInputCols("sentence", "token")
      .setOutputCol("pos")

    val wordEmbeddings = WordEmbeddingsModel
      .pretrained()
      .setInputCols("sentence", "token")
      .setOutputCol("word_embeddings")

    val ner = NerDLModel
      .pretrained("ner_dl", "en")
      .setInputCols("token", "sentence", "word_embeddings")
      .setOutputCol("ner")

    val nerConverter = new NerConverter()
      .setInputCols("sentence", "token", "ner")
      .setOutputCol("ner_converter")

    val finisher = new Finisher()
      .setInputCols("ner", "ner_converter")
      .setCleanAnnotations(false)

    val pipeline = new Pipeline().setStages(
      Array(
        document,
        sentenceDetector,
        token,
        posTagger,
        wordEmbeddings,
        ner,
        nerConverter,
        finisher))

    val testData = spark
      .createDataFrame(
        Seq(
          (1, "Google has announced the release of a beta version of the popular TensorFlow machine learning library"),
          (2, "The Paris metro will soon enter the 21st century, ditching single-use paper tickets for rechargeable electronic cards.")))
      .toDF("id", "text")

    val predicion = pipeline.fit(testData).transform(testData)
    predicion.select("ner_converter.result").show(false)
    predicion.select("pos.result").show(false)

  }

  def pretrainedPipeline(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    val testData = spark
      .createDataFrame(
        Seq(
          (1, "Google has announced the release of a beta version of the popular TensorFlow machine learning library"),
          (2, "The Paris metro will soon enter the 21st century, ditching single-use paper tickets for rechargeable electronic cards.")))
      .toDF("id", "text")

    val pipeline = new PretrainedPipeline("explain_document_dl", lang = "en")
    pipeline.annotate(
      "Google has announced the release of a beta version of the popular TensorFlow machine learning library")
    pipeline.transform(testData).select("entities").show(false)

    val pipelineML = new PretrainedPipeline("explain_document_ml", lang = "en")
    pipelineML.annotate(
      "Google has announced the release of a beta version of the popular TensorFlow machine learning library")
    pipelineML.transform(testData).select("pos").show(false)

  }

  def pretrainedPipelineLD(args: Array[String]): Unit = {

    spark.sparkContext.setLogLevel("ERROR")

    val testData =
      Array(
        "A term??szetes nyelvfeldolgoz??s t??rt??nete ??ltal??ban az 1950-es ??vekben kezd??d??tt, b??r a kor??bbi id??szakokb??l sz??rmaz?? munk??k is megtal??lhat??k. 1950-ben Alan Turing k??zz??tett egy cikket, melynek c??me: ???Sz??m??t??stechnika ??s intelligenciag??pek???, ??s amely intelligenciakrit??riumk??nt javasolta a Turing-tesztet.",
        "Geoffrey Everest Hinton ?? um psic??logo cognitivo brit??nico canadense e cientista da computa????o, mais conhecido por seu trabalho em redes neurais artificiais. Desde 2013, ele trabalha para o Google e a Universidade de Toronto. Em 2017, foi co-fundador e tornou-se Conselheiro Cient??fico Chefe do Vector Institute of Toronto.")

    val pipeline = new PretrainedPipeline("detect_language_43", lang = "xx")
    println(pipeline.annotate(testData).mkString("Array(", ", ", ")"))

  }
}