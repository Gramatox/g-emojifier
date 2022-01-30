package write

import cats.effect.IO
import cats.effect.kernel.Resource

import java.io.{File, FileWriter}

object FileWriterUtils {

  def acquireFileWriter(path: String): Resource[IO, FileWriter] = Resource
    .make(
      IO("Opening File") >> IO(new FileWriter(new File(path)))
    )(
      writer => IO("closing writer") >> IO(writer.close())
    )

  def writeLineToFile(writer: FileWriter, line: String): IO[Unit] = IO(writer.write(line + "\n"))

}
