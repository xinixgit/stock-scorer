package smc.scorer

object Test2 {
	val numbers = Map(2->3, 4->5)             //> numbers  : scala#27.collection#2760.immutable#5845.Map#7884[Int#1109,Int#1109
                                                  //| ] = Map(2 -> 3, 4 -> 5)
	numbers.map{ case (x, y) => x + y}        //> res0: scala#27.collection#2760.immutable#5845.Iterable#8385[Int#1109] = List
                                                  //| (5, 9)
}