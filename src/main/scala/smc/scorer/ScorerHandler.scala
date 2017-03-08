package smc.scorer

import scala.collection.mutable.HashMap

case class AnalysisTerm(symbol: String, termEndDate: String, termLength: String, num: Int, value: String)

case class ScoreResult(symbol: String, termLength: String, category: String, score: Double)

/**
 * @param qf A query function to retrieve all analysisTerm objects associated with a symbol
 * @param termCatConfig A map of termNum to its associated category
 */
class ScorerHandler(scorerListIter: java.util.Iterator[Scorer], symbolListJava: java.util.List[String],
                    termCatConfig: java.util.Map[Int, String], qf: java.util.function.Function[String, java.util.List[AnalysisTerm]]) {
    /**
     * Main entry point, run all scorers on each symbol
     */
    def run(): java.util.List[ScoreResult] = {
        val symbolList = ScorerUtils.toScalaArrayBuffer(symbolListJava)
        val resList = symbolList.flatMap(runScorersOnSymbol(_)).toList
        ScorerUtils.toJavaList(resList)
    }

    def runScorersOnSymbol(symbol: String): List[ScoreResult] = {
        val anlterms = ScorerUtils.toScalaList(qf.apply(symbol))
        val termsByLength = anlterms.groupBy(_.termLength)

        termsByLength.flatMap {
            case (termLength, termList) => {
                val sortedByTermNum = termList
                    .groupBy(term => term.num)
                    .mapValues(list => list.sortBy(_.termEndDate)) // (termNum, List[AnalysisTerm])

                val groupByCat = sortedByTermNum
                    .map { case (termNum, termList) => { (termCatConfig.get(termNum), termNum, termList) } }
                    .filterNot(tuple3 => tuple3._1.isEmpty())
                    .groupBy(tuple3 => tuple3._1)

                val termsByCat = groupByCat
                    .mapValues(iter => iter.map { case (cat, termNum, list) => (termNum, list) })
                    .mapValues(iter => iter.toMap) // (cat, Map[termNum, List[AnalysisTerm])

                // Calculate total scores
                termsByCat.map {
                    case (cat, termsMap) => {
                        var catScore = 0.0
                        while (scorerListIter.hasNext()) {
                            val scorer = scorerListIter.next()
                            catScore += scorer.score(termsMap)
                        }

                        println(f"(cat:$cat%s, score:$catScore%.4f)")
                        ScoreResult(symbol, termLength, cat, catScore)
                    }
                }
            }
        }.toList
    }
}