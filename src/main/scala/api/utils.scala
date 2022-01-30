package api

import cats.effect.IO
import models.ImdbId
import org.json4s.DefaultFormats
import org.json4s.jackson.JsonMethods.parse

import scala.io.BufferedSource

object utils {

  def jsonStrToMap(jsonStr: String): Map[String, Any] = {
    implicit val formats: DefaultFormats.type = org.json4s.DefaultFormats
    parse(jsonStr).extract[Map[String, Any]]
  }

  def openSource(url: String): IO[BufferedSource] =
    IO(scala.io.Source.fromURL(url))

  def hitOMDBAPI(imdb_id: ImdbId, key: String): IO[String] = {
    for {
      valid_id <- IO.fromOption(imdb_id.id)(new Error("no id, uhh")).handleErrorWith {
        case e: Error => IO(e.getMessage)
        case _ => IO("Random error")
      }
      source <- IO(s"hitting the following url: http://www.omdbapi.com/?apikey=$key&i=$valid_id&plot=full") >>
        openSource(url = s"http://www.omdbapi.com/?apikey=$key&i=$valid_id&plot=full")
      parsedResult <- IO(jsonStrToMap(source.mkString).getOrElse("Plot", "").toString)
      _ <- IO("closing buffered api result stream") >> IO(source.close())
    } yield parsedResult
  }

}
