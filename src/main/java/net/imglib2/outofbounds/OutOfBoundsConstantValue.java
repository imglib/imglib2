/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

package net.imglib2.outofbounds;

import java.util.function.Supplier;

import net.imglib2.Interval;
import net.imglib2.RandomAccessible;
import net.imglib2.type.Type;

/**
 * 
 * @param <T>
 * 
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Philipp Hanslovsky
 */
public class OutOfBoundsConstantValue< T > extends AbstractOutOfBoundsValue< T >
{
	private final Supplier< T > valueSupplier;

	final protected T value;

	protected OutOfBoundsConstantValue( final OutOfBoundsConstantValue< T > outOfBounds )
	{
		super( outOfBounds );
		this.valueSupplier = outOfBounds.valueSupplier;
		this.value = this.valueSupplier.get();
	}

	public < F extends Interval & RandomAccessible< T > > OutOfBoundsConstantValue( final F f, final Supplier< T > valueSupplier )
	{
		super( f );
		this.valueSupplier = valueSupplier;
		this.value = this.valueSupplier.get();
	}

	public < F extends Interval & RandomAccessible< T > > OutOfBoundsConstantValue( final F f, final T value )
	{
		this( f, makeSupplierFrom( value ) );
	}

	/* Sampler */

	@Override
	final public T get()
	{
		// System.out.println( getLocationAsString() + " " + isOutOfBounds );
		if ( isOutOfBounds )
			return value;
		return sampler.get();
	}

	@Override
	final public OutOfBoundsConstantValue< T > copy()
	{
		return new OutOfBoundsConstantValue<>( this );
	}

	/* RandomAccess */

	@Override
	final public OutOfBoundsConstantValue< T > copyRandomAccess()
	{
		return copy();
	}

	private static < T > Supplier< T > makeSupplierFrom( final T t )
	{
		if ( t instanceof Type< ? > )
		{
			final Type< ? > type = ( Type< ? > ) t;
			return () -> ( T ) type.copy();
		}
		return () -> t;
	}
}
