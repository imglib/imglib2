/*

Copyright (c) 2011, Barry DeZonia.
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

package net.imglib2.ops;

/**
 * 
 * @author Barry DeZonia
 *
 */
public interface Function<INDEX_TYPE, OUTPUT_TYPE> {

	/**
	 * Evaluate the function using a neighborhood and a point within the
	 * neighborhood as input. Places result of evaluation in output.
	 * 
	 * @param neigh
	 * @param point
	 * @param output
	 */
	void evaluate(Neighborhood<INDEX_TYPE> neigh, INDEX_TYPE point, OUTPUT_TYPE output);
	
	/**
	 * A helper that can bypass the limitation of generic classes from creating
	 * new instances of generic types.
	 */
	OUTPUT_TYPE createOutput();
	
	/**
	 * A helper that allows one to create a copy of a function. This method is
	 * key for supporting multithreaded calculations during image assignment.
	 */
	Function<INDEX_TYPE,OUTPUT_TYPE> copy();
}

