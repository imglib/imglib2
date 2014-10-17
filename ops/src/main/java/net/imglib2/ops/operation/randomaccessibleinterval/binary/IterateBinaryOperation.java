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
 
package net.imglib2.ops.operation.randomaccessibleinterval.binary;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.labeling.Labeling;
import net.imglib2.meta.ImgPlus;
import net.imglib2.ops.operation.BinaryOperation;
import net.imglib2.ops.operation.metadata.unary.CopyCalibratedSpace;
import net.imglib2.ops.operation.metadata.unary.CopyImageMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyNamed;
import net.imglib2.ops.operation.metadata.unary.CopySourced;
import net.imglib2.ops.operation.subset.views.ImgPlusView;
import net.imglib2.ops.operation.subset.views.ImgView;
import net.imglib2.ops.operation.subset.views.LabelingView;
import net.imglib2.ops.operation.subset.views.SubsetViews;
import net.imglib2.type.Type;

/**
 * Applies a given Operation to each interval Separately.
 * 
 * @author Christian Dietz (University of Konstanz)
 */
public final class IterateBinaryOperation< T extends Type< T >, V extends Type< V >, O extends Type< O > > implements BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> >
{

	private final BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > m_op;

	private final Interval[] m_in0Intervals;

	private final Interval[] m_in1Intervals;

	private final Interval[] m_outIntervals;

	private final ExecutorService m_service;

	/**
	 * Intervals are different
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, Interval[] in0InIntervals, Interval[] in1Intervals, Interval[] outIntervals, ExecutorService service )
	{
		m_op = op;
		m_in0Intervals = in0InIntervals;
		m_in1Intervals = in1Intervals;
		m_outIntervals = outIntervals;
		m_service = service;
	}

	/**
	 * Intervals are different
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, Interval[] in0InIntervals, Interval[] in1Intervals, Interval[] outIntervals )
	{
		this( op, in0InIntervals, in1Intervals, outIntervals, null );
	}

	/**
	 * Intervals for in0, in1 are the Same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, Interval[] inInIntervals, Interval[] outIntervals )
	{
		this( op, inInIntervals, inInIntervals, outIntervals, null );
	}

	/**
	 * Intervals for in0, in1 are the Same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, Interval[] inInIntervals, Interval[] outIntervals, ExecutorService service )
	{
		this( op, inInIntervals, inInIntervals, outIntervals, service );
	}

	/**
	 * Intervals for in0, in1 and out are the Same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, Interval[] intervals )
	{
		this( op, intervals, intervals, intervals, null );
	}

	/**
	 * Intervals for in0, in1 and out are the Same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, Interval[] intervals, ExecutorService service )
	{
		this( op, intervals, intervals, intervals, service );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RandomAccessibleInterval<O> compute( final RandomAccessibleInterval<T> in0, RandomAccessibleInterval<V> in1, RandomAccessibleInterval<O> out )
	{

		if ( m_in0Intervals.length != m_outIntervals.length || m_in0Intervals.length != m_in1Intervals.length ) { throw new IllegalArgumentException( "In and out intervals do not match! Most likely an implementation error!" ); }

		Future< ? >[] futures = new Future< ? >[ m_in0Intervals.length ];

		for ( int i = 0; i < m_outIntervals.length; i++ )
		{

			if ( Thread.interrupted() )
				return out;

			OperationTask t = new OperationTask( m_op, createSubType( in0, m_in0Intervals[ i ] ), createSubType( in1, m_in1Intervals[ i ] ), createSubType( out, m_outIntervals[ i ] ) );

			if ( m_service != null )
			{
				if ( m_service.isShutdown() )
					return out;

				futures[ i ] = m_service.submit( t );
			}
			else
			{
				t.run();
			}

		}

		if ( m_service != null )
		{
			try
			{
				for ( Future< ? > f : futures )
				{
					if ( f.isCancelled() )
						return out;

					f.get();
				}
			}
			catch ( InterruptedException e )
			{
				// nothing to do here
			}
			catch ( ExecutionException e )
			{
				// nothing to do here
			}
		}

		return out;
	}

	@SuppressWarnings( { "rawtypes", "unchecked" } )
	private synchronized < TT extends Type< TT >, II extends RandomAccessibleInterval< TT > > II createSubType( final II in, final Interval i )
	{
		if ( in instanceof Labeling ) { return ( II ) new LabelingView( SubsetViews.iterableSubsetView( in, i ), ( ( Labeling ) in ).factory() ); }

		if ( in instanceof ImgPlus )
		{
			ImgPlusView< TT > imgPlusView = new ImgPlusView< TT >( SubsetViews.iterableSubsetView( in, i ), ( ( ImgPlus ) in ).factory() );
			new CopyMetadata( new CopyNamed(), new CopySourced(), new CopyImageMetadata(), new CopyCalibratedSpace( i ) ).compute( ( ImgPlus ) in, imgPlusView );
			return ( II ) imgPlusView;
		}

		if ( in instanceof Img ) { return ( II ) new ImgView< TT >( SubsetViews.iterableSubsetView( in, i ), ( ( Img ) in ).factory() ); }

		return ( II ) SubsetViews.iterableSubsetView( in, i );
	}

	@Override
	public BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > copy()
	{
		return new IterateBinaryOperation< T, V, O >( m_op.copy(), m_in0Intervals, m_in1Intervals, m_outIntervals, m_service );
	}

	/**
	 * Future task
	 * 
	 * @author dietzc, muethingc
	 * 
	 */
	private class OperationTask implements Runnable
	{

		private final BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > m_op;

		private final RandomAccessibleInterval<T> m_in1;

		private final RandomAccessibleInterval<V> m_in2;

		private final RandomAccessibleInterval<O> m_out;

		public OperationTask( final BinaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V>, RandomAccessibleInterval<O> > op, final RandomAccessibleInterval <T> in1, final RandomAccessibleInterval<V> in2, final RandomAccessibleInterval<O> out )
		{
			m_in1 = in1;
			m_in2 = in2;
			m_out = out;
			m_op = op.copy();
		}

		@Override
		public void run()
		{
			m_op.compute( m_in1, m_in2, m_out );
		}

	}
}
