/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img;

import net.imglib2.Cursor;
import net.imglib2.FlatIterationOrder;
import net.imglib2.Interval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.Type;
import net.imglib2.util.Util;
import net.imglib2.view.IterableRandomAccessibleInterval;
import net.imglib2.view.Views;
import net.imglib2.view.iteration.SubIntervalIterable;

/**
 * Allows a {@link RandomAccessibleInterval} to be treated as an {@link Img}.
 *
 * @author Tobias Pietzsch
 * @author Christian Dietz
 * @author Curtis Rueden
 */
public class ImgView< T extends Type< T > > extends IterableRandomAccessibleInterval< T > implements Img< T >, SubIntervalIterable< T >
{

	// factory
	private final ImgFactory< T > factory;

	// ImgView ii
	private final IterableInterval< T > ii;

	/**
	 * View on {@link Img} which is defined by a given Interval, but still is an
	 * {@link Img}.
	 *
	 * Deprecation: Use
	 * {@link ImgView#wrap(RandomAccessibleInterval, ImgFactory)} to represent a
	 * RandomAccessibleInterval as an Img
	 *
	 * @param in
	 *            Source interval for the view
	 * @param fac
	 *            T Factory to create img
	 */
	@Deprecated
	public ImgView( final RandomAccessibleInterval< T > in, final ImgFactory< T > fac )
	{
		super( in );
		factory = fac;
		ii = Views.flatIterable( in );
	}

	@Override
	public ImgFactory< T > factory()
	{
		return factory;
	}

	@Override
	public Img< T > copy()
	{
		final Img< T > copy = factory.create( this );

		final Cursor< T > srcCursor = localizingCursor();
		final RandomAccess< T > resAccess = copy.randomAccess();

		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			resAccess.setPosition( srcCursor );
			resAccess.get().set( srcCursor.get() );
		}

		return copy;
	}

	@Override
	public Cursor< T > cursor()
	{
		return ii.cursor();
	}

	@Override
	public Cursor< T > localizingCursor()
	{
		return ii.localizingCursor();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public boolean supportsOptimizedCursor( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).supportsOptimizedCursor( interval );
		else
			return false;
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Object subIntervalIterationOrder( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).subIntervalIterationOrder( interval );
		else
			return new FlatIterationOrder( interval );
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< T > cursor( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).cursor( interval );
		else
			return Views.interval( this.sourceInterval, interval ).cursor();
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public Cursor< T > localizingCursor( final Interval interval )
	{
		if ( this.sourceInterval instanceof SubIntervalIterable )
			return ( ( SubIntervalIterable< T > ) this.sourceInterval ).localizingCursor( interval );
		else
			return Views.interval( this.sourceInterval, interval ).localizingCursor();
	}

	/**
	 * Represent an arbitrary {@link RandomAccessibleInterval} as an
	 * {@link Img}, with a suitable {@link ImgFactory} for its size and type,
	 * created by
	 * {@link Util#getSuitableImgFactory(net.imglib2.Dimensions, Object)}.
	 *
	 * @param accessible
	 *            RandomAccessibleInterval which will be wrapped with an ImgView
	 * @return RandomAccessibleInterval represented as an Img
	 * @see Util#getSuitableImgFactory(net.imglib2.Dimensions, Object)
	 */
	public static < T extends Type< T > > Img< T > wrap( final RandomAccessibleInterval< T > accessible )
	{
		if ( accessible instanceof Img )
			return ( Img< T > ) accessible;
		else
		{
			final T type = accessible.getType();
			final ImgFactory< T > factory = Util.getSuitableImgFactory( accessible, type );
			return wrap( accessible, factory );
		}
	}

	/**
	 * Represent an arbitrary {@link RandomAccessibleInterval} as an
	 * {@link Img}.
	 *
	 * @param accessible
	 *            RandomAccessibleInterval which will be wrapped with an ImgView
	 * @param factory
	 *            ImgFactory returned by {@link ImgView#factory()}
	 * @return RandomAccessibleInterval represented as an Img
	 */
	public static < T extends Type< T > > Img< T > wrap( final RandomAccessibleInterval< T > accessible, final ImgFactory< T > factory )
	{
		if ( accessible instanceof Img )
			return ( Img< T > ) accessible;
		else
			return new ImgView<>( accessible, factory );
	}
}
