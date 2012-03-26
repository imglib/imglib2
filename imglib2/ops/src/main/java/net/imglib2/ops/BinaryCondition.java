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
public class BinaryCondition<INDEX,O1,O2> implements Condition<INDEX> {

	private final Function<INDEX,O1> f1;
	private final Function<INDEX,O2> f2;
	private final O1 f1Val;
	private final O2 f2Val;
	private final BinaryRelation<O1,O2> relation;

	public BinaryCondition(Function<INDEX,O1> f1, Function<INDEX,O2> f2, BinaryRelation<O1,O2> relation) {
		this.f1 = f1;
		this.f2 = f2;
		this.f1Val = f1.createOutput();
		this.f2Val = f2.createOutput();
		this.relation = relation;
	}
	
	@Override
	public boolean isTrue(Neighborhood<INDEX> region, INDEX point) {
		f1.evaluate(region, point, f1Val);
		f2.evaluate(region, point, f2Val);
		return relation.holds(f1Val,f2Val);
	}
	
	@Override
	public BinaryCondition<INDEX,O1,O2> copy() {
		return new BinaryCondition<INDEX,O1,O2>(f1.copy(), f2.copy(), relation.copy());
	}

}
