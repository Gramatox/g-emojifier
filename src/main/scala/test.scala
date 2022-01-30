import api.models.ImdbId._
import api.utils.hitOMDBAPI
import cats.effect.{IO, IOApp}
import read.FileReaderUtils.{checkEmojiData, createScannerResource}
import write.FileWriterUtils.{acquireFileWriter, writeLineToFile}

import java.io.FileWriter
import java.util.Scanner
import scala.concurrent.duration.DurationInt

object test extends IOApp.Simple {

  val idsReadPath: String = "src/main/resources/imdb_ids.csv"
  val movieDataWritePath: String = "src/main/data/movie_data.txt"

  def readAndWriteLines(scanner: Scanner, writer: FileWriter): IO[Unit] = {
    if (scanner.hasNextLine) for {
      line <- IO.sleep(100.millis) >> IO(scanner.nextLine())
      parsedId <- IO(parseToImdbId(line))
      response <- hitOMDBAPI(parsedId, "")
      emojifiedResponse <- checkEmojiData(response.split(' '))
      _ <- writeLineToFile(writer, emojifiedResponse) >> readAndWriteLines(scanner, writer)
    } yield ()
     else IO()
  }

  def readLinesFromFile(readPath: String, writePath: String): IO[Unit] = {
    createScannerResource(readPath).both(acquireFileWriter(writePath)).use{
      case (scanner, writer) => readAndWriteLines(scanner, writer)
    }
  }

  def checkFileBeginning(): IO[Unit] = {
    for {
      fib <- readLinesFromFile(readPath = idsReadPath, writePath = movieDataWritePath).start
      _ <- IO.sleep(15.seconds) >> fib.cancel
    } yield ()
  }

  override def run: IO[Unit] = checkFileBeginning()

}
