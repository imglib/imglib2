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
package net.imglib2.display.projector.volatiles;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.Volatile;
import net.imglib2.converter.Converter;
import net.imglib2.display.projector.Projector2D;
import net.imglib2.view.Views;

/**
 * {@link XYRandomAccessibleProjector} for {@link Volatile} input. After each
 * {@link #map()} call, the projector has a {@link #isValid() state} that
 * signalizes whether all projected pixels were valid.
 * 
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public class Volatile2DRandomAccessibleProjector< T, A extends Volatile< T >, B > extends Projector2D< A, B >
{
	protected boolean valid = false;

	public Volatile2DRandomAccessibleProjector( final int dimX, final int dimY, final RandomAccessible< A > source, final RandomAccessibleInterval< B > target, final Converter< ? super A, B > converter )
	{
		super( dimX, dimY, source, Views.iterable( target ), converter );
	}

	/**
	 * @return true if all mapped pixels were {@link Volatile#isValid() valid}.
	 */
	public boolean isValid()
	{
		return valid;
	}

	/**
	 * projects data from the source to the target and applies the former
	 * specified {@link Converter} e.g. for normalization.
	 */
	@Override
	public void map()
	{
		// fix interval for all dimensions
		for ( int d = 0; d < position.length; ++d )
			min[ d ] = max[ d ] = position[ d ];

		min[ X ] = target.min( X );
		min[ Y ] = target.min( Y );
		max[ X ] = target.max( X );
		max[ Y ] = target.max( Y );

		final IterableInterval< A > srcIterable = Views.iterable( Views.interval( source, new FinalInterval( min, max ) ) );
		final Cursor< B > targetCursor = target.localizingCursor();

		if ( target.iterationOrder().equals( srcIterable.iterationOrder() ) )
		{
			// use cursors
			final Cursor< A > sourceCursor = srcIterable.cursor();
			while ( targetCursor.hasNext() )
			{
				converter.convert( sourceCursor.next(), targetCursor.next() );
			}
		}
		else
		{
			// use localizing cursor
			final RandomAccess< A > sourceRandomAccess = source.randomAccess();
			while ( targetCursor.hasNext() )
			{
				final B b = targetCursor.next();
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( X ), X );
				sourceRandomAccess.setPosition( targetCursor.getLongPosition( Y ), Y );

				converter.convert( sourceRandomAccess.get(), b );
			}
		}
	}
}
