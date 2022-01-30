package api.models

case class ImdbId(
                 id: Option[String] = None
                 )

object ImdbId{
  def parseToImdbId(input: String): ImdbId = {
    if (input.matches("tt\\d{7}")) ImdbId(id = Some(input))
    else ImdbId()
  }
}
