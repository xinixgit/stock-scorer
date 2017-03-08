package smc.scorer

import java.util.Arrays
import java.util.function.Predicate

object ScoreHandlerTest {
    def main(args: Array[String]): Unit = {
        val g1 = AnalysisTerm("GOOG", "2016-09-30", "QTR", 1, "0.36")
        val g2 = AnalysisTerm("GOOG", "2016-06-30", "QTR", 1, "0.32")
        val g3 = AnalysisTerm("GOOG", "2016-03-30", "QTR", 1, "0.13")
        val g4 = AnalysisTerm("GOOG", "2016-09-30", "QTR", 2, "104.23")
        val g5 = AnalysisTerm("GOOG", "2016-06-30", "QTR", 2, "120.11")
        val g6 = AnalysisTerm("GOOG", "2016-03-30", "QTR", 2, "90.05")

        val lgConfig = LinearGrowthTermConfig(1, 1, 1)
        val lgConfig2 = LinearGrowthTermConfig(2, 2, 1)
        val configMap = ScorerUtils.toJavaHashMap(Map(1 -> lgConfig, 2 -> lgConfig2))
        val noData: Predicate[String] = new Predicate[String](){
            override def test(str: String): Boolean = {
                "-".equals(str)
            }
        }
        
        val lgScorer: Scorer = new LinearGrowthScorer(configMap, 0.01, noData)
        
        val qf = new java.util.function.Function[String, java.util.List[AnalysisTerm]]() {
            override def apply(str: String): java.util.List[AnalysisTerm] = {
                ScorerUtils.toJavaList(List(g1, g2, g3, g4, g5, g6))
            }
        }
        
        val termCatConfig =  ScorerUtils.toJavaHashMap(Map(1 -> "1", 2 -> "2"))
        val sh = new ScorerHandler(Arrays.asList(lgScorer).iterator(), Arrays.asList("GOOG"), termCatConfig, qf)

        val resList = ScorerUtils.toScalaList(sh.run())
        val scoreMap = ScorerUtils.sumScoreResultsByCat(resList)
        scoreMap.foreach{ case (symb, map) => map.foreach{ case (tl, score) => println(f"($symb%s, $tl%s, $score%.4f)")}}
    }
}