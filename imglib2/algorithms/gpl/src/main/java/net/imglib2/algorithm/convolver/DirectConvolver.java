package net.imglib2.algorithm.convolver;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * @author Martin Horn (University of Konstanz)
 * @author Christian Dietz (University of Konstanz)
 */
public class DirectConvolver< T extends RealType< T >, K extends RealType< K >, O extends RealType< O >> implements Convolver< T, K, O >
{

	public DirectConvolver()
	{}

	@Override
	public RandomAccessibleInterval< O > compute( RandomAccessible< T > input, RandomAccessibleInterval< K > kernel, RandomAccessibleInterval< O > output )
	{

		// TODO: Workaround until fix in imglib2 (outofbounds gets lost
		// during optimization of transformation)
		long[] min = new long[ input.numDimensions() ];
		long[] max = new long[ input.numDimensions() ];

		for ( int d = 0; d < kernel.numDimensions(); d++ )
		{
			min[ d ] = -kernel.dimension( d );
			max[ d ] = kernel.dimension( d ) + output.dimension( d );
		}

		final RandomAccess< T > srcRA = input.randomAccess( new FinalInterval( min, max ) );

		final Cursor< K > kernelC = Views.iterable( kernel ).localizingCursor();

		final Cursor< O > resC = Views.iterable( output ).localizingCursor();

		final long[] pos = new long[ input.numDimensions() ];
		final long[] kernelRadius = new long[ kernel.numDimensions() ];
		for ( int i = 0; i < kernelRadius.length; i++ )
		{
			kernelRadius[ i ] = kernel.dimension( i ) / 2;
		}

		float val;

		while ( resC.hasNext() )
		{
			// image
			resC.fwd();
			resC.localize( pos );

			// kernel inlined version of the method convolve
			val = 0;
			srcRA.setPosition( pos );

			kernelC.reset();
			while ( kernelC.hasNext() )
			{
				kernelC.fwd();

				for ( int i = 0; i < kernelRadius.length; i++ )
				{
					if ( kernelRadius[ i ] > 0 )
					{ // dimension
						// can have
						// zero
						// extension
						// e.g.
						// vertical
						// 1d kernel
						srcRA.setPosition( pos[ i ] + kernelC.getLongPosition( i ) - kernelRadius[ i ], i );
					}
				}

				val += srcRA.get().getRealDouble() * kernelC.get().getRealDouble();
			}

			resC.get().setReal( val );
		}

		return output;
	}

	@Override
	public DirectConvolver< T, K, O > copy()
	{
		return new DirectConvolver< T, K, O >();
	}

}
