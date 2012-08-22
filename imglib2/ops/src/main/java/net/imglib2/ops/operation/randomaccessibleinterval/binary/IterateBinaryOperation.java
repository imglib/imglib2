package net.imglib2.ops.operation.randomaccessibleinterval.binary;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import net.imglib2.Interval;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgPlus;
import net.imglib2.labeling.Labeling;
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
 * Applies a given Operation to each interval separately.
 * 
 * @author dietzc, hornm University of Konstanz
 */
public final class IterateBinaryOperation< T extends Type< T >, V extends Type< V >, O extends Type< O >, S extends RandomAccessibleInterval< T >, U extends RandomAccessibleInterval< V >, R extends RandomAccessibleInterval< O > > implements BinaryOperation< S, U, R >
{

	private final BinaryOperation< S, U, R > m_op;

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
	public IterateBinaryOperation( BinaryOperation< S, U, R > op, Interval[] in0InIntervals, Interval[] in1Intervals, Interval[] outIntervals, ExecutorService service )
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
	public IterateBinaryOperation( BinaryOperation< S, U, R > op, Interval[] in0InIntervals, Interval[] in1Intervals, Interval[] outIntervals )
	{
		this( op, in0InIntervals, in1Intervals, outIntervals, null );
	}

	/**
	 * Intervals for in0, in1 are the same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< S, U, R > op, Interval[] inInIntervals, Interval[] outIntervals )
	{
		this( op, inInIntervals, inInIntervals, outIntervals, null );
	}

	/**
	 * Intervals for in0, in1 are the same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< S, U, R > op, Interval[] inInIntervals, Interval[] outIntervals, ExecutorService service )
	{
		this( op, inInIntervals, inInIntervals, outIntervals, service );
	}

	/**
	 * Intervals for in0, in1 and out are the same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< S, U, R > op, Interval[] intervals )
	{
		this( op, intervals, intervals, intervals, null );
	}

	/**
	 * Intervals for in0, in1 and out are the same.
	 * 
	 * @param op
	 * @param intervals
	 * @param service
	 */
	public IterateBinaryOperation( BinaryOperation< S, U, R > op, Interval[] intervals, ExecutorService service )
	{
		this( op, intervals, intervals, intervals, service );
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public final R compute( final S in0, U in1, R out )
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
			new CopyMetadata( new CopyNamed(), new CopySourced(), new CopyImageMetadata(), new CopyCalibratedSpace( i ) ).compute( ( ImgPlus ) in, imgPlusView );;
			return ( II ) imgPlusView;
		}

		if ( in instanceof Img ) { return ( II ) new ImgView< TT >( SubsetViews.iterableSubsetView( in, i ), ( ( Img ) in ).factory() ); }

		return ( II ) SubsetViews.iterableSubsetView( in, i );
	}

	@Override
	public BinaryOperation< S, U, R > copy()
	{
		return new IterateBinaryOperation< T, V, O, S, U, R >( m_op.copy(), m_in0Intervals, m_in1Intervals, m_outIntervals, m_service );
	}

	/**
	 * Future task
	 * 
	 * @author dietzc, muethingc
	 * 
	 */
	private class OperationTask implements Runnable
	{

		private final BinaryOperation< S, U, R > m_op;

		private final S m_in1;

		private final U m_in2;

		private final R m_out;

		public OperationTask( final BinaryOperation< S, U, R > op, final S in1, final U in2, final R out )
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
