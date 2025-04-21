/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2025 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.converter;

import java.util.Random;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.util.Pair;
import net.imglib2.view.Views;

/**
 *
 * @author Philipp Hanslovsky
 *
 */
public class AbstractConvertedRandomAccessibleIntervalTest
{

	Random rng = new Random();

	static final ArrayImg< DoubleType, DoubleArray > data = ArrayImgs.doubles( 10, 20, 30 );
	{
		for ( final DoubleType d : data )
			d.set( rng.nextDouble() );
	}

	@Test
	public void test()
	{
		final AbstractConvertedRandomAccessibleInterval< DoubleType, DoubleType > converted = new AbstractConvertedRandomAccessibleInterval< DoubleType, DoubleType >( data )
		{

			@Override
			public AbstractConvertedRandomAccess< DoubleType, DoubleType > randomAccess()
			{
				return new AbstractConvertedRandomAccess< DoubleType, DoubleType >( data.randomAccess() )
				{

					DoubleType t = new DoubleType();

					@Override
					public DoubleType get()
					{
						t.set( this.source.get() );
						t.mul( this.source.getDoublePosition( this.source.numDimensions() - 1 ) );
						return t;
					}

					@Override
					public DoubleType getType()
					{
						return t;
					}

					@Override
					public AbstractConvertedRandomAccess< DoubleType, DoubleType > copy()
					{
						return null;
					}
				};
			}

			@Override
			public AbstractConvertedRandomAccess< DoubleType, DoubleType > randomAccess( final Interval interval )
			{
				return randomAccess();
			}

			@Override
			public DoubleType getType()
			{
				return new DoubleType();
			}
		};

		for ( final Cursor< Pair< DoubleType, DoubleType > > c = Views.interval( Views.pair( data, converted ), data ).cursor(); c.hasNext(); )
		{
			final Pair< DoubleType, DoubleType > p = c.next();
			final double pos = c.getDoublePosition( c.numDimensions() - 1 );
			Assert.assertEquals( pos * p.getA().get(), p.getB().get(), 0.0 );
		}

	}

}
