package read

import cats.effect.IO
import cats.effect.kernel.Resource
import read.models.{EmojiRow, MoviePlotItem}

import java.io.{File, FileReader}
import java.util.Scanner
import scala.annotation.tailrec

object FileReaderUtils {

  def openFileScanner(path: String): IO[Scanner] =
    IO(new Scanner(new FileReader(new File(path))))

  def createScannerResource(path: String): Resource[IO, Scanner] = Resource
    .make(IO("opening file") >> openFileScanner(path))(scanner => IO("closing file") >> IO(scanner.close()))

  def checkEmojiData(phrase: Array[String]): IO[String] = {
    val phraseStems: Array[MoviePlotItem] = phrase.map(item => MoviePlotItem(item, item))
    val scanner: Resource[IO, Scanner] = createScannerResource("src/main/resources/emoji_data.csv")
    @tailrec
    def checkEmojiDataRec(moviePlot: Array[MoviePlotItem], scanner: Scanner): IO[Array[MoviePlotItem]] = {
        if (!scanner.hasNextLine) IO(moviePlot)
        else {
          checkEmojiDataRec(compareStringArrays(emojiLineTuple(scanner.nextLine()), moviePlot), scanner)
        }
    }
    scanner.use{myScanner =>
      for {
        line <- checkEmojiDataRec(phraseStems, myScanner)
      } yield reformPlot(line)
    }
  }

  def emojiLineTuple(input: String): EmojiRow = {
    EmojiRow(emoticon = input.split(',')(1),
      description = input.split(',')(2).split(' ').map(item => item.toLowerCase))
  }

  def compareStringArrays(emojiWords: EmojiRow, moviePlot: Array[MoviePlotItem]): Array[MoviePlotItem] = {
    moviePlot.map{
      item =>
        if (emojiWords.description.contains(item.stemmed)) {
          item.copy(emoticon = Some(emojiWords.emoticon))
        }
        else item
    }
  }

  def reformPlot(emojifiedPlot: Array[MoviePlotItem]): String =
    emojifiedPlot.map{
      case MoviePlotItem(word, _, None) => word
      case MoviePlotItem(word, _, Some(emoji)) => word + ' ' + emoji
  }.mkString(" ")
}
