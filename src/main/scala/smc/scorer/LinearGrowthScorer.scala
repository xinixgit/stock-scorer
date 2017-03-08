package smc.scorer

case class LinearGrowthTermConfig(termNum: Int, weight: Double, multiplier: Int)

/**
 * Group together financial terms with the same term number, then
 * analyze its movement across all date points provided for that termNum
 *
 * @param noDataChecker A function to determine if a data is null
 * @author xinix
 */
class LinearGrowthScorer(wgtMap: java.util.Map[Int, LinearGrowthTermConfig], minPositiveGrowthPerc: Double, noData: java.util.function.Predicate[String]) extends Scorer {

    override def score(anlTerms: Map[Int, List[AnalysisTerm]]): Double = {
        anlTerms.map(e => scoreOneRow(e._2, wgtMap.get(e._1))).sum
    }

    def scoreOneRow(terms: List[AnalysisTerm], wgtConfig: LinearGrowthTermConfig): Double = {
        if (terms == Nil || wgtConfig == null)
            0.0

        var growthTermsCount = 0
        val termMultiplier = wgtConfig.multiplier

        for (i <- 1 until terms.size) {
            val currValStr = terms(i).value
            val lastValStr = terms(i - 1).value

            if (noData.test(currValStr) || noData.test(lastValStr)) {
                0.0
            }

            val currVal = currValStr.toDouble
            val lastVal = lastValStr.toDouble

            if (isPositiveGrowth(currVal, lastVal)) {
                growthTermsCount = growthTermsCount + 1
            }
        }

        val growthPerc = growthTermsCount / (terms.size - 1).toDouble
        growthPerc * wgtConfig.weight
    }

    def isPositiveGrowth(currVal: Double, lastVal: Double): Boolean = {
        (currVal > lastVal) && (lastVal != 0) && ((currVal / lastVal) > minPositiveGrowthPerc)
    }
}