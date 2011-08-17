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

import java.lang.reflect.Array;

/**
 * 
 * @author Barry DeZonia
 *
 */
public abstract class Neighborhood<ARRAY> {

	private ARRAY keyPoint;
	private ARRAY negOffsets;
	private ARRAY posOffsets;
	
	public Neighborhood(ARRAY keyPt, ARRAY negOffs, ARRAY posOffs) {
		keyPoint = keyPt;
		negOffsets = negOffs;
		posOffsets = posOffs;
		int len1 = Array.getLength(keyPt);
		int len2 = Array.getLength(negOffs);
		int len3 = Array.getLength(posOffs);
		if ((len1 != len2) || (len2 != len3))
			throw new IllegalArgumentException("Neighborhood poorly defined: input array lengths differ");
	}
	
	public ARRAY getKeyPoint() {
		return keyPoint;
	}

	public ARRAY getNegativeOffsets() {
		return negOffsets;
	}

	public int getNumDims() {
		return Array.getLength(keyPoint);
	}

	public ARRAY getPositiveOffsets() {
		return posOffsets;
	}

	public void moveTo(ARRAY newKeyPoint) {
		if (newKeyPoint != keyPoint) {
			if (Array.getLength(newKeyPoint) != Array.getLength(keyPoint))
				throw new IllegalArgumentException("moveTo() - new key point has wrong number of dimensions");
			keyPoint = newKeyPoint;
		}
	}
}

