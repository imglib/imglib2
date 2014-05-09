/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.img.constant;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.RandomAccess;
import net.imglib2.img.AbstractImg;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.Type;

/**
 * A simple {@link Img} that has only one value and returns it at each location.
 * 
 * Btw, we only need T to be instance of Type for the copy() method.
 * 
 * @param <T>
 * 
 * @author Tobias Pietzsch
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 */
public class ConstantImg< T extends Type< T > > extends AbstractImg< T >
{
	final T type;

	ImgFactory< T > factory;

	final protected long[] dim;

	public ConstantImg( final long[] size, final T type )
	{
		super( size );

		this.type = type;
		this.dim = size;
	}

	@Override
	public ImgFactory< T > factory()
	{
		// only create it if necessary
		if ( factory == null )
			factory = new ConstantImgFactory< T >();

		return factory;
	}

	@Override
	public Img< T > copy()
	{
		return new ConstantImg< T >( dim, type );
	}

	@Override
	public RandomAccess< T > randomAccess()
	{
		return new ConstantRandomAccess< T >( type, this.numDimensions() );
	}

	@Override
	public Cursor< T > cursor()
	{
		return new ConstantCursor< T >( type, this.numDimensions(), dim, this.numPixels );
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		final long[] m = new long[ this.numDimensions() ];
		this.max( m );

		return new ConstantLocalizingCursor< T >( type, this.numDimensions(), m, this.numPixels );
	}

	@Override
	public FlatIterationOrder iterationOrder()
	{
		return new FlatIterationOrder( this );
	}
}
