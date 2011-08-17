/*

Copyright (c) 2011, Stephan Preibisch & Stephan Saalfeld.
All rights reserved.

Redistribution and use in source and binary forms, with or without
modification, are permitted provided that the following conditions are met:
  * Redistributions of source code must retain the above copyright
    notice, this list of conditions and the following disclaimer.
  * Redistributions in binary form must reproduce the above copyright
    notice, this list of conditions and the following disclaimer in the
    documentation and/or other materials provided with the distribution.
  * Neither the name of the Fiji project developers nor the
    names of its contributors may be used to endorse or promote products
    derived from this software without specific prior written permission.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
POSSIBILITY OF SUCH DAMAGE.
*/

package net.imglib2.ops.example;

// a composed function example

// imagine a composed function requires a couple things
//   composition happens along an axis
//   all compositions have to be shape compatible to a hyperrectangular region
//   the compositions can be nested

/**
 * 
 * @author Barry DeZonia
 *
 */
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