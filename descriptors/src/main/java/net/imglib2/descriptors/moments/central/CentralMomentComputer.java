package net.imglib2.descriptors.moments.central;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractModule;
import net.imglib2.descriptors.Module;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.descriptors.moments.helper.ImageMomentsParameter;
import net.imglib2.type.numeric.RealType;

public class CentralMomentComputer extends AbstractModule< double[] >
{
	@ModuleInput
	ImageMomentsParameter param;

	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@ModuleInput
	CenterOfGravity center;

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		return CentralMomentComputer.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public boolean hasCompatibleOutput( Class< ? > clazz )
	{
		return clazz.isAssignableFrom( double[].class );
	}

	@Override
	protected double[] recompute()
	{
		int order = param.getOrder();

		List< Double > fR = new ArrayList< Double >();

		for ( int o = 0; o <= order; o++ )
		{
			List< int[] > list = computeMomentsByOrder( o );

			for ( int i = 0; i < list.size(); i++ )
			{
				System.out.println( list.get( i )[ 0 ] + " " + list.get( i )[ 1 ] );
				double val = this.computeCentralMoment( list.get( i )[ 0 ], list.get( i )[ 1 ] );
				fR.add( val );
			}
		}

		double[] finalResult = new double[ fR.size() ];
		for ( int i = 0; i < fR.size(); i++ )
			finalResult[ i ] = fR.get( i );

		return finalResult;
	}

	protected double computeCentralMoment( int p, int q )
	{
		final double centerX = center.get()[ 0 ];
		final double centerY = center.get()[ 1 ];

		double result = 0.0;

		final Cursor< ? extends RealType< ? > > it = ii.localizingCursor();
		while ( it.hasNext() )
		{
			it.fwd();
			final double x = it.getIntPosition( 0 ) - centerX;
			final double y = it.getIntPosition( 1 ) - centerY;

			result += it.get().getRealDouble() * Math.pow( x, p ) * Math.pow( y, q );
		}

		return result;
	}

	protected List< int[] > computeMomentsByOrder( int order )
	{
		List< int[] > moments = new ArrayList< int[] >();

		if ( order == 0 )
		{
			int[] val = new int[ 2 ];
			val[ 0 ] = 0;
			val[ 1 ] = 0;
			moments.add( val );
			return moments;
		}

		int p = 0;
		int q = 0;

		boolean run1 = true;
		boolean run2 = true;

		while ( run1 )
		{
			while ( run2 )
			{
				if ( p + q == order )
				{
					int[] val = new int[ 2 ];
					val[ 0 ] = p;
					val[ 1 ] = q;
					moments.add( val );
					run2 = false;
				}
				if ( q > order )
					run2 = false;
				q++;
			}
			run2 = true;
			if ( p > order )
				run1 = false;
			q = 0;
			p++;
		}

		return moments;
	}
}
