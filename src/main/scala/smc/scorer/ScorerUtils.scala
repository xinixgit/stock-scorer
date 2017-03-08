package smc.scorer

import scala.collection.mutable.ArrayBuffer
import java.util.ArrayList
import java.util.HashMap

object ScorerUtils {
    /**
     * Summarize the score results to a map by symbol and term length, so scores for each cat is summed. The format
     * would be like below:
     *
     * { symbol : { termLength : score }}
     */
    def sumScoreResultsByCat(rs: List[ScoreResult]): Map[String, Map[String, Double]] = {
        rs.groupBy(_.symbol).mapValues(l => l.groupBy(_.termLength).mapValues(l => l.map(_.score).sum))
    }

    def toScalaArrayBuffer[T](javaList: java.util.List[T]): ArrayBuffer[T] = {
        val scalaList: ArrayBuffer[T] = new ArrayBuffer(javaList.size())
        val iter: java.util.Iterator[T] = javaList.iterator();
        while (iter.hasNext()) {
            scalaList += iter.next()
        }

        scalaList
    }

    def toScalaList[T](javaList: java.util.List[T]): List[T] = {
        var scalaList = List.empty[T]
        val iter: java.util.Iterator[T] = javaList.iterator();
        while (iter.hasNext()) {
            scalaList = scalaList ++ List(iter.next())
        }

        scalaList
    }

    def toJavaList[T](scalaList: List[T]): java.util.List[T] = {
        val javaList: java.util.List[T] = new ArrayList[T]()
        scalaList.foreach(javaList.add(_))
        javaList
    }

    def toJavaHashMap[K, V](scalaMap: Map[K, V]): java.util.Map[K, V] = {
        val javaMap: java.util.HashMap[K, V] = new HashMap[K, V]()
        scalaMap.foreach { case (k, v) => javaMap.put(k, v) }
        javaMap
    }
}