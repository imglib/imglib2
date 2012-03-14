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
 */
package net.imglib2.outofbounds;

import java.util.Random;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.numeric.RealType;

/**
 * Return a random value in a certain range when outside of the Interval
 * 
 * @param <T>
 *
 * @author Stephan Preibisch
 */
public class OutOfBoundsRandomValue< T extends RealType< T > > extends AbstractOutOfBoundsValue< T >
{
	final T value;
	
	final double min, max, range;
	final Random rnd;

	protected OutOfBoundsRandomValue( final OutOfBoundsRandomValue< T > outOfBounds )
	{
		super( outOfBounds );
		
		this.value = outOfBounds.value.copy();
		this.min = outOfBounds.min;
		this.max = outOfBounds.max;
		this.range = outOfBounds.range;
		this.rnd = new Random();
	}
	
	public < F extends Interval & RandomAccessible< T > > OutOfBoundsRandomValue( final F f, final T value, final Random rnd, final double min, final double max )
	{
		super( f, value );
	
		this.value = value;
		this.rnd = rnd;
		this.min = min;
		this.max = max;
		this.range = max - min;
	}
	
	@Override
	final public T get()
	{
		if ( isOutOfBounds )
		{
			value.setReal( rnd.nextDouble() * range + min );
			return value;
		}
		else
		{
			return sampler.get();
		}
	}
	
	@Override
	final public OutOfBoundsRandomValue< T > copy()
	{
		return new OutOfBoundsRandomValue< T >( this );
	}


	/* RandomAccess */

	@Override
	final public OutOfBoundsRandomValue< T > copyRandomAccess()
	{
		return copy();
	}
}
