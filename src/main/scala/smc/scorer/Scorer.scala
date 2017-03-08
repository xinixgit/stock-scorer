package smc.scorer

/**
 * Interface for scorer that yield a score for information provided for a stock.
 * In general, better score means more positive outlook.
 *
 * @author xinix
 *
 */
trait Scorer {
    /**
     * Provide scores for a list of financial terms. Returns a score for this symbol.
     * Note that it's caller's responsibility to make sure all analysis terms passed
     * in belong to the same symbol.
     *
     * @param anlTerms a map of finTerms with a sorted list of finTerms for each
     * termNum by termEndDate, meaning earliest terms comes first.
     */
    def score(anlTerms: Map[Int, List[AnalysisTerm]]): Double
}