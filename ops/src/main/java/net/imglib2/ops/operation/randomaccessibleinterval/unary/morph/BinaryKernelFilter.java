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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.morph;

import java.util.ArrayList;

import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.iterator.IntervalIterator;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * @author Felix Schonenberger (University of Konstanz)
 */
public final class BinaryKernelFilter< T extends RealType< T > > implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T > >
{

	/*
	 * Number of kernel dimensions. The kernel covers the first
	 * <code>m_kernelNumDim</code> dimensions of the image.
	 */
	private int m_kernelNumDim;

	/*
	 * Speed up dimension. The kernel iterates the image on this dimension.
	 */
	private final int m_dimIndex0;

	/*
	 * Left dead zone to initialize the kernel.
	 */
	private long m_startOffset;

	/*
	 * Kernel queues. One queue is a one dimensional image on the
	 * <code>m_dimIndex0</code> axis. Only the incoming pixels need to be read
	 * from disk, old pixels are shifted.
	 */
	private Queue< T >[] m_kernel;

	/*
	 * A array holding references to all types in all queues.
	 * 
	 * TODO think
	 */
	private ArrayList< T > m_kernelReferenceArray;

	private final UnaryOutputOperation< Iterable< T >, T > m_op;

	public BinaryKernelFilter( final Img< BitType > kernel, final int dimIndex0, final UnaryOutputOperation< Iterable< T >, T > op )
	{
		m_dimIndex0 = dimIndex0;
		setupKernel( kernel );
		m_op = op;
	}

	public BinaryKernelFilter( Queue< T >[] kernel, final int dimIndex0, final UnaryOutputOperation< Iterable< T >, T > op )
	{
		m_dimIndex0 = dimIndex0;
		m_kernel = kernel;
		m_op = op;
	}

	@SuppressWarnings( "unchecked" )
	private final void setupKernel( final Img< BitType > kernel )
	{
		// Check kernel dimensions
		final int[] kernelDim = new int[ kernel.numDimensions() ];
		for ( int i = 0; i < kernelDim.length; i++ )
			kernelDim[ i ] = ( int ) kernel.dimension( i );
		final int[] kernelRadius = kernelDim.clone();
		for ( int i = 0; i < kernelRadius.length; i++ )
		{
			if ( kernelRadius[ i ] % 2 == 0 ) { throw new IllegalArgumentException( "Only odd kernel sizes supported (dim[" + i + "] = " + kernelRadius[ i ] + ")." ); }
			kernelRadius[ i ] = kernelRadius[ i ] / 2;
		}
		// Setup line cursor, points on the first pixel in each line
		final int[] lineDim = kernelDim.clone();
		lineDim[ m_dimIndex0 ] = 1;
		final IntervalIterator ii = new IntervalIterator( lineDim );
		long x0, dim0 = kernel.dimension( m_dimIndex0 );
		// Iterate over kernel
		ArrayList< Queue< T >> tmp = new ArrayList< Queue< T >>();
		RandomAccess< BitType > cur = kernel.randomAccess();
		int count = 0;
		int kernelSize = 0;
		int[] pos = new int[ kernel.numDimensions() ];
		while ( ii.hasNext() )
		{
			ii.fwd();
			cur.setPosition( ii );
			for ( x0 = 0; x0 < dim0; x0++ )
			{
				cur.setPosition( x0, m_dimIndex0 );
				if ( cur.get().get() )
				{
					count++;
					kernelSize++;
				}
				else if ( count > 0 )
				{
					ii.localize( pos );
					pos[ m_dimIndex0 ] = pos[ m_dimIndex0 ] - 1;
					for ( int i = 0; i < pos.length; i++ )
					{
						pos[ i ] -= kernelRadius[ i ];
					}
					// System.out.println("I" + count +
					// Arrays.toString(pos));
					tmp.add( new Queue< T >( pos.clone(), count ) );
					count = 0;
				}
			}
			if ( count > 0 )
			{
				ii.localize( pos );
				for ( int i = 0; i < pos.length; i++ )
				{
					pos[ i ] -= kernelRadius[ i ];
				}
				// System.out.println("F" + count +
				// Arrays.toString(pos));
				tmp.add( new Queue< T >( pos.clone(), count ) );
				count = 0;
			}
		}
		m_kernelNumDim = kernel.numDimensions();
		m_startOffset = kernel.dimension( m_dimIndex0 );
		m_kernelReferenceArray = new ArrayList< T >( kernelSize );
		m_kernel = new Queue[ tmp.size() ];
		tmp.toArray( m_kernel );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final RandomAccessibleInterval< T > compute( final RandomAccessibleInterval< T > op, final RandomAccessibleInterval< T > r )
	{
		IterableInterval< T > iterOp = Views.iterable( op );
		int i = 0;
		final long dim0 = op.dimension( m_dimIndex0 );
		for ( Queue< T > q : m_kernel )
		{
			q.init( iterOp.firstElement().createVariable() );
			for ( int j = 0; j < q.size(); j++ )
				m_kernelReferenceArray.add( q.getType( j ) );
		}
		// Setup line cursor, points on the first pixel in each line
		final long[] dim = new long[ op.numDimensions() ];
		op.dimensions( dim );
		dim[ m_dimIndex0 ] = 1;
		// TODO: IntervalIterator
		final IntervalIterator ii = new IntervalIterator( dim );
		RandomAccess< T > cr = r.randomAccess();
		RandomAccess< T > co = Views.extendValue( op, iterOp.firstElement().createVariable() ).randomAccess();
		// Iterate over image
		long x0;
		while ( ii.hasNext() )
		{
			ii.fwd();
			// Initialize kernel in new line
			for ( x0 = -m_startOffset; x0 < 0; x0++ )
			{
				for ( Queue< T > q : m_kernel )
				{
					for ( i = 0; i < m_kernelNumDim; i++ )
					{
						co.setPosition( ii.getLongPosition( i ) + q.getOffset( i ), i );
					}
					for ( ; i < dim.length; i++ )
					{
						co.setPosition( ii.getLongPosition( i ), i );
					}
					co.setPosition( x0 + q.getOffset( m_dimIndex0 ), m_dimIndex0 );
					q.circle( co.get() );
				}
			}
			for ( x0 = 0; x0 < dim0; x0++ )
			{
				for ( Queue< T > q : m_kernel )
				{
					for ( i = 0; i < m_kernelNumDim; i++ )
					{
						co.setPosition( ii.getLongPosition( i ) + q.getOffset( i ), i );
					}
					for ( ; i < dim.length; i++ )
					{
						co.setPosition( ii.getLongPosition( i ), i );
					}
					co.setPosition( x0 + q.getOffset( m_dimIndex0 ), m_dimIndex0 );
					q.circle( co.get() );
				}
				cr.setPosition( ii );
				cr.setPosition( x0, m_dimIndex0 );
				// cr.get().set(calculateType());
				cr.get().set( m_op.compute( m_kernelReferenceArray, m_op.createEmptyOutput( m_kernelReferenceArray ) ) );
			}
		}

		return r;
	}

	/**
	 * Kernel queue.
	 * 
	 * @author schoenen
	 * @param <Q>
	 */
	final class Queue< Q extends RealType< Q >>
	{
		private Q m_ret;

		private final int[] m_offset;

		private final Q[] m_queue;

		private int m_index;

		@SuppressWarnings( "unchecked" )
		public Queue( int[] offset, int length )
		{
			m_offset = offset;
			m_queue = ( Q[] ) new RealType[ length ];
		}

		public final Q getType( int i )
		{
			return m_queue[ i ];
		}

		public final int getOffset( int i )
		{
			return m_offset[ i ];
		}

		public final int size()
		{
			return m_queue.length;
		}

		public final void init( Q type )
		{
			m_ret = type.createVariable();
			for ( int i = 0; i < m_queue.length; i++ )
			{
				m_queue[ i ] = type.createVariable();
			}
		}

		public final Q circle( Q v )
		{
			m_ret.set( m_queue[ m_index ] );
			m_queue[ m_index ].set( v );
			m_index = ( m_index + 1 ) % m_queue.length;
			return m_ret;
		}
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T > > copy()
	{
		return new BinaryKernelFilter< T >( m_kernel, m_dimIndex0, m_op );
	}
}
