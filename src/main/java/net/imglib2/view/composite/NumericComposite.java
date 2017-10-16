/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2016 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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
package net.imglib2.view.composite;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.list.ListImgFactory;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.NumericType;

/**
 * A vector of {@link NumericType} scalars.  It is a {@link NumericType}
 * itself, implementing the {@link NumericType} algebra as element-wise
 * operations.
 *
 * @author Stephan Saalfeld
 */
public class NumericComposite< T extends NumericType< T > > extends AbstractNumericComposite< T, NumericComposite< T > >
{
	static public class Factory< T extends NumericType< T > > implements CompositeFactory< T, NumericComposite< T > > 
	{
		final protected int numChannels;
		
		public Factory( final int numChannels )
		{
			this.numChannels = numChannels;
		}
		
		@Override
		public NumericComposite< T > create( final RandomAccess< T > sourceAccess )
		{
			return new NumericComposite< T >( sourceAccess, numChannels );
		}
	}
	
	public NumericComposite( final RandomAccess< T > sourceAccess, final int length )
	{
		super( sourceAccess, length );
	}

	/**
	 * Generates a 1D {@link ArrayImg}&lt;T&gt; 
	 */
	@Override
	@SuppressWarnings( { "unchecked", "rawtypes" } )
	public NumericComposite< T > createVariable()
	{
		final T t = sourceAccess.get();
		final Img< T > img;
		if ( NativeType.class.isInstance( t ) )
			img = new ArrayImgFactory().create( new long[] { length }, ( NativeType ) t );
		else
			img = new ListImgFactory< T >().create( new long[]{ length }, t );
		return new NumericComposite< T >( img.randomAccess(), length );
	}

	@Override
	public NumericComposite< T > copy()
	{
		return new NumericComposite< T >( sourceAccess.copyRandomAccess(), length );
	}
}
