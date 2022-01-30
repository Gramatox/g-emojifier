package utils.io

import cats.effect.IO

object ImplicitUtils {

  implicit class IOImplicits[A](io: IO[A]) {
    def debug: IO[A] = for {
      a <- io
      t = Thread.currentThread().getName
      _ = println(s"[$t] $a")
    } yield a
  }

}
