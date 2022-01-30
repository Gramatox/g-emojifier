package read

package object models {

  case class MoviePlotItem(
                          word: String,
                          stemmed: String,
                          emoticon: Option[String] = None
                      )

  case class EmojiRow(
                       unicode: Option[String] = None,
                       emoticon: String,
                       description: Array[String]
                     )

}
