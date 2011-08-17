package net.imglib2.ops.example;

// a composed function example

// imagine a composed function requires a couple things
//   composition happens along an axis
//   all compositions have to be shape compatible to a hyperrectangular region
//   the compositions can be nested

public class ExampleNotes {

}


// Things worth noting
// - discrete and continuous
// - real, complex, and bool (add int later?)
// - functions can be inputs (not just images)
// - 

// TODOs
//   PointNeigh & PointNeighIterator
//   ComposedFunction
//   old examples from the various revs
//   convolution, median, etc.
//   unary ops
//   constraints
//   assign operation
//   imglib integration

//Make a function that uses a condition to decide whether to evaluate
//the function or not (i.e. condition could be a mask)
//Make lots of classes final.


// note that regions always define a point. so there is no such empty
//   region. this might cause minor problems with assign ops passed
//   zero sized regions. but maybe that is impossible.

// note that many of the unary ops use a constant value
//   could make them binary ops passing a constant function