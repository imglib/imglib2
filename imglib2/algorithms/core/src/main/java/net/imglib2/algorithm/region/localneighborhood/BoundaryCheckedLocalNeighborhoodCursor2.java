/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 *
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.region.localneighborhood;

import net.imglib2.AbstractCursor;
import net.imglib2.Interval;
import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.util.IntervalIndexer;

/**
 * Iterates all pixels in a 3 by 3 by .... by 3 neighborhood of a certain
 * location but skipping the central pixel
 *
 * @param <T>
 *
 * @author Stephan Preibisch
 * @author Stephan Saalfeld
 * @author Benjamin Schmid
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class BoundaryCheckedLocalNeighborhoodCursor2< T > extends AbstractCursor< T >
{
	private final Interval sourceInterval;

	private final RandomAccess< T > source;

	private final long[] center;

	private final long[] min;

	private final long[] max;

	private final long span;

	private long maxCount;

	private long midCount;

	private long count;

	public BoundaryCheckedLocalNeighborhoodCursor2( final RandomAccessibleInterval< T > source )
	{
		this(source, new long[source.numDimensions()]);
	}

	public BoundaryCheckedLocalNeighborhoodCursor2( final RandomAccessibleInterval< T > source, final long[] center, final long span )
	{
		super( source.numDimensions() );
		this.source = source.randomAccess();
		this.center = center;
		max = new long[ n ];
		min = new long[ n ];
		this.span = span;
		maxCount = ( long ) Math.pow( span + 1 + span, n );
		midCount = maxCount / 2 + 1;
		this.sourceInterval = source;
		reset();
	}

	public BoundaryCheckedLocalNeighborhoodCursor2( final RandomAccessibleInterval< T > source, final long[] center )
	{
		this( source, center, 1 );
	}

	protected BoundaryCheckedLocalNeighborhoodCursor2( final BoundaryCheckedLocalNeighborhoodCursor2< T > c )
	{
		super( c.numDimensions() );
		this.source = c.source.copyRandomAccess();
		this.center = c.center;
		max = c.max.clone();
		min = c.min.clone();
		span = c.span;
		maxCount = c.maxCount;
		midCount = c.midCount;
		this.sourceInterval = c.sourceInterval;
	}

	@Override
	public T get()
	{
		return source.get();
	}

	@Override
	public void fwd()
	{
		for ( int d = 0; d < n; ++d )
		{
			source.fwd( d );
			if ( source.getLongPosition( d ) > max[ d ] )
				source.setPosition( min[ d ], d );
			else
				break;
		}
		if ( ++count == midCount )
			fwd();
	}

	@Override
	public void reset()
	{
		long[] dff = new long[n];
		maxCount = 1;
		for ( int d = 0; d < n; ++d )
		{
			min[ d ] = Math.max(sourceInterval.min(d), center[ d ] - span);
			max[ d ] = Math.min(sourceInterval.max(d), center[ d ] + span);
			dff[ d ] = max[d] - min[d] + 1;
			maxCount *= dff[d];
		}
		source.setPosition( min );
		source.bck( 0 );
		count = 0;
	 	midCount = IntervalIndexer.positionWithOffsetToIndex(center, dff, min) + 1;
	}

	@Override
	public boolean hasNext()
	{
		return count < maxCount;
	}

	@Override
	public float getFloatPosition( final int d )
	{
		return source.getFloatPosition( d );
	}

	@Override
	public double getDoublePosition( final int d )
	{
		return source.getDoublePosition( d );
	}

	@Override
	public int getIntPosition( final int d )
	{
		return source.getIntPosition( d );
	}

	@Override
	public long getLongPosition( final int d )
	{
		return source.getLongPosition( d );
	}

	@Override
	public void localize( final long[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final float[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final double[] position )
	{
		source.localize( position );
	}

	@Override
	public void localize( final int[] position )
	{
		source.localize( position );
	}

	@Override
	public BoundaryCheckedLocalNeighborhoodCursor2< T > copy()
	{
		return new BoundaryCheckedLocalNeighborhoodCursor2< T >(this);
	}

	@Override
	public BoundaryCheckedLocalNeighborhoodCursor2< T > copyCursor()
	{
		return copy();
	}

	public void updateCenter(Localizable center) {
		for(int d = 0; d < n; d++)
			this.center[d] = center.getLongPosition(d);
		reset();
	}

	public static void main(String[] args) {
		Img<UnsignedByteType> img = new ArrayImgFactory<UnsignedByteType>().
				create(new long[] {10, 10, 10}, new UnsignedByteType());
		BoundaryCheckedLocalNeighborhoodCursor2<UnsignedByteType> lnc =
				new BoundaryCheckedLocalNeighborhoodCursor2<UnsignedByteType>(img);

		RandomAccess<UnsignedByteType> a = img.randomAccess();
		a.setPosition(new int[] {0, 0, 0});
		lnc.updateCenter(a);

		int count = 0;
		while(lnc.hasNext()) {
			lnc.next();
			count++;
		}

		System.out.println("count = " + count);

	}
}
