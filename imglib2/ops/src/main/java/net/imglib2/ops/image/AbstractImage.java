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

package net.imglib2.ops.image;

/**
 * Test class
 * 
 * @author Barry DeZonia
 *
 */
public abstract class AbstractImage {

	private long[] dims;
	private String[] axes;
	
	protected AbstractImage(long[] dims, String[] axes) {
		this.dims = dims;
		this.axes = axes;
	}

	protected long totalElements(long[] dimens) {
		long num = 1;
		for (long d : dimens)
			num *= d;
		return num;
	}
	
	protected int elementNumber(long[] pos) {
		int delta = 1;
		int num = 0;
		for (int i = 0; i < pos.length; i++) {
			num += delta * pos[i];
			delta *= dims[i];
		}
		return num;
	}
	
	public int numDimensions() {
		return dims.length;
	}
	
	public long dimension(int i) {
		return dims[i];
	}
	
	public String axis(int i) {
		return axes[i];
	}
	
	public long[] dimensions() { return dims; }
	
	public String[] axes() { return axes; }
}
