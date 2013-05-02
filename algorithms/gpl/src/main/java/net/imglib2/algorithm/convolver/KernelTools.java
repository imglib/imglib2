package net.imglib2.algorithm.convolver;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.ops.operation.SubsetOperations;
import net.imglib2.ops.operation.subset.views.ImgView;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

import org.apache.commons.math3.linear.AbstractRealMatrix;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.BlockRealMatrix;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularValueDecomposition;

/**
 * @author Martin Horn (University of Konstanz)
 * @author Christian Dietz (University of Konstanz)
 */
public class KernelTools
{

	public static < K extends RealType< K >> Img< K > adjustKernelDimensions( int numResDimensions, int[] kernelDims, Img< K > kernel )
	{

		if ( kernelDims.length > kernel.numDimensions() ) { throw new IllegalStateException( "Number of selected dimensions greater than KERNEL dimensions in KernelTools." ); }

		if ( kernelDims.length > numResDimensions ) { throw new IllegalStateException( "Number of selected dimensions greater than result dimensions in KernelTools." ); }

		if ( kernelDims.length == numResDimensions ) { return kernel; }

		RandomAccessible< K > res = kernel;

		for ( int d = kernel.numDimensions(); d < numResDimensions; d++ )
		{
			res = Views.addDimension( res );
		}

		long[] max = new long[ numResDimensions ];
		for ( int d = 0; d < kernel.numDimensions(); d++ )
		{
			max[ d ] = kernel.max( d );
		}

		long[] resDims = new long[ max.length ];
		Arrays.fill( resDims, 1 );
		for ( int d = 0; d < kernelDims.length; d++ )
		{
			res = Views.permute( res, d, kernelDims[ d ] );
			resDims[ kernelDims[ d ] ] = kernel.dimension( d );
		}

		return new ImgView< K >( Views.interval( res, new FinalInterval( resDims ) ), kernel.factory() );
	}

	private static < K extends RealType< K >, KERNEL extends RandomAccessibleInterval< K >> SingularValueDecomposition isDecomposable( KERNEL kernel )
	{

		if ( kernel.numDimensions() != 2 )
			return null;

		final RealMatrix mKernel = new ImgBasedRealMatrix< K >( kernel );

		final SingularValueDecomposition svd = new SingularValueDecomposition( mKernel );

		if ( svd.getRank() > 1 )
			return null;

		return svd;

	}

	@SuppressWarnings( "unchecked" )
	public static < K extends RealType< K > & NativeType< K >> Img< K >[] decomposeKernel( Img< K > kernel )
	{

		SingularValueDecomposition svd = isDecomposable( SubsetOperations.subsetview( kernel, kernel ) );

		if ( svd != null )
		{
			int tmp = 0;
			for ( int d = 0; d < kernel.numDimensions(); d++ )
			{
				if ( kernel.dimension( d ) > 1 )
					tmp++;
			}
			int[] kernelDims = new int[ tmp ];
			tmp = 0;
			for ( int d = 0; d < kernel.numDimensions(); d++ )
			{
				if ( kernel.dimension( d ) > 1 )
					kernelDims[ tmp++ ] = d;
			}

			final RealVector v = svd.getV().getColumnVector( 0 );
			final RealVector u = svd.getU().getColumnVector( 0 );
			final double s = -Math.sqrt( svd.getS().getEntry( 0, 0 ) );
			v.mapMultiplyToSelf( s );
			u.mapMultiplyToSelf( s );

			K type = kernel.randomAccess().get().createVariable();

			Img< K >[] decomposed = new Img[ 2 ];

			decomposed[ 0 ] = KernelTools.adjustKernelDimensions( kernel.numDimensions(), new int[] { kernelDims[ 0 ] }, KernelTools.vectorToImage( v, type, 1, new ArrayImgFactory< K >() ) );
			decomposed[ 1 ] = KernelTools.adjustKernelDimensions( kernel.numDimensions(), new int[] { kernelDims[ 1 ] }, KernelTools.vectorToImage( u, type, 1, new ArrayImgFactory< K >() ) );
			return decomposed;

		}
		else
		{
			return new Img[] { kernel };
		}

	}

	/**
	 * Creates a numDims dimensions image where all dimensions d<numDims are of
	 * size 1 and the last dimensions contains the vector.
	 * 
	 * @param <R>
	 * @param ar
	 * @param type
	 * @param numDims
	 *            number of dimensions
	 * @return
	 */
	public static < R extends RealType< R >> Img< R > vectorToImage( final RealVector ar, final R type, int numDims, ImgFactory< R > fac )
	{
		long[] dims = new long[ numDims ];

		for ( int i = 0; i < dims.length - 1; i++ )
		{
			dims[ i ] = 1;
		}
		dims[ dims.length - 1 ] = ar.getDimension();
		Img< R > res = fac.create( dims, type );
		Cursor< R > c = res.cursor();
		while ( c.hasNext() )
		{
			c.fwd();
			c.get().setReal( ar.getEntry( c.getIntPosition( numDims - 1 ) ) );
		}

		return res;
	}

	/**
	 * 
	 * @param <R>
	 * @param img
	 *            A two dimensional image.
	 * @return
	 */
	public static < R extends RealType< R >> RealMatrix toMatrix( final Img< R > img )
	{

		assert img.numDimensions() == 2;
		RealMatrix ret = new BlockRealMatrix( ( int ) img.dimension( 0 ), ( int ) img.dimension( 1 ) );
		Cursor< R > c = img.cursor();
		while ( c.hasNext() )
		{
			c.fwd();
			ret.setEntry( c.getIntPosition( 0 ), c.getIntPosition( 1 ), c.get().getRealDouble() );
		}
		return ret;
	}

	private static class ImgBasedRealMatrix< TT extends RealType< TT >> extends AbstractRealMatrix
	{

		private final RandomAccess< TT > m_rndAccess;

		private final RandomAccessibleInterval< TT > m_in;

		public ImgBasedRealMatrix( final RandomAccessibleInterval< TT > in )
		{
			if ( in.numDimensions() != 2 ) { throw new IllegalArgumentException( "In must have exact two dimensions to be handled as a matrix" ); }
			m_in = in;
			m_rndAccess = in.randomAccess();
		}

		@Override
		public RealMatrix createMatrix( final int rowDimension, final int columnDimension )
		{
			return new Array2DRowRealMatrix( rowDimension, columnDimension );
		}

		@Override
		public RealMatrix copy()
		{
			throw new UnsupportedOperationException( "Unsupported" );
		}

		@Override
		public double getEntry( final int row, final int column )
		{
			m_rndAccess.setPosition( row, 1 );
			m_rndAccess.setPosition( column, 0 );

			return m_rndAccess.get().getRealDouble();
		}

		@Override
		public void setEntry( final int row, final int column, final double value )
		{
			m_rndAccess.setPosition( row, 1 );
			m_rndAccess.setPosition( column, 0 );
			m_rndAccess.get().setReal( value );
		}

		@Override
		public int getRowDimension()
		{
			return ( int ) m_in.dimension( 0 );
		}

		@Override
		public int getColumnDimension()
		{
			return ( int ) m_in.dimension( 1 );
		}

	}
}
