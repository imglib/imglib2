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
public final class ComplexFamily implements DataCopier<ComplexFamily>
{
	private Complex base;
	private Real delta;

	public ComplexFamily() {
		base = new Complex();
		delta = new Real();
	}
	
	public static ComplexFamily createCartesian(double x, double y, double delta) {
		ComplexFamily c = new ComplexFamily();
		c.getBase().setCartesian(x,y);
		c.getDelta().setReal(delta);
		return c;
	}
	
	public static ComplexFamily createPolar(double r, double theta, double delta) {
		ComplexFamily c = new ComplexFamily();
		c.getBase().setPolar(r,theta);
		c.getDelta().setReal(delta);
		return c;
	}
	
	public Complex getBase() {
		return base;
	}
	
	public Real getDelta() {
		return delta;
	}

	@Override
	public void setValue(ComplexFamily fromValue) {
		Complex fromBase = fromValue.getBase();
		base.setCartesian(fromBase.getX(), fromBase.getY());
		delta.setValue(fromValue.getDelta());
	}

}

