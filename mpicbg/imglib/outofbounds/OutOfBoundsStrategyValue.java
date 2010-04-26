/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.outofbounds;

import mpicbg.imglib.cursor.IterableCursor;
import mpicbg.imglib.cursor.PositionableCursor;
import mpicbg.imglib.type.Type;

public class OutOfBoundsStrategyValue<T extends Type<T>> extends OutOfBoundsStrategy<T>
{
	final T value;
	
	public OutOfBoundsStrategyValue( final PositionableCursor<T> parentCursor, final T value )
	{
		super( parentCursor );
		this.value = value;
	}

	@Override
	final public T getType(){ return value; }

	/**
	 * Has nothing to do when the parent cursor moves while being out of image bounds
	 * 
	 * @see mpicbg.imglib.outofbounds.OutOfBoundsStrategy#notifyOutOfBounds()
	 */
	@Override
	final public void notifyOutOfBOunds() {}

	/**
	 * Has nothing to do when the parent cursor moves while being out of image bounds
	 * 
	 * @see mpicbg.imglib.outofbounds.OutOfBoundsStrategy#notifyOutOfBOunds(int, int)
	 */
	@Override
	final public void notifyOutOfBOunds( final int dim, final int steps ) {}

	@Override
	final public void notifyOutOfBOundsFwd( final int dim ) {}

	@Override
	final public void notifyOutOfBoundsBck( final int dim ) {}
	
	/**
	 * Updates the array of the Cursor type to the constant value and sets the index to 0
	 * 
	 * @see mpicbg.imglib.outofbounds.OutOfBoundsStrategy#initOutOfBOunds()
	 */
	@Override
	final public void initOutOfBOunds() {}
	
	@Override
	final public void close() {}
}
