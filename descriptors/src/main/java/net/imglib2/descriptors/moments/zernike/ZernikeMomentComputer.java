package net.imglib2.descriptors.moments.zernike;

import java.util.ArrayList;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.descriptors.AbstractModule;
import net.imglib2.descriptors.Module;
import net.imglib2.descriptors.ModuleInput;
import net.imglib2.descriptors.geometric.centerofgravity.CenterOfGravity;
import net.imglib2.descriptors.moments.zernike.helper.FactComputer;
import net.imglib2.descriptors.moments.zernike.helper.Polynom;
import net.imglib2.descriptors.moments.zernike.helper.ZernikeParameter;
import net.imglib2.type.numeric.RealType;

public class ZernikeMomentComputer extends AbstractModule< double[] >
{
	@ModuleInput
	ZernikeParameter param;

	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@ModuleInput
	CenterOfGravity center;

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{

		return ZernikeMomentComputer.class.isAssignableFrom( output.getClass() );
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
			List< Integer > list = computeRepitionsOfOrder( o );

			for ( int i = 0; i < list.size(); i++ )
			{
				double[] val = this.computeMoment( o, list.get( i ) );
				fR.add( val[ 0 ] );
				fR.add( val[ 1 ] );
			}
		}

		double[] finalResult = new double[ fR.size() ];
		for ( int i = 0; i < fR.size(); i++ )
			finalResult[ i ] = fR.get( i );

		return finalResult;
	}

	private List< Integer > computeRepitionsOfOrder( int n )
	{

		List< Integer > list = new ArrayList< Integer >();
		int m = 0;
		while ( m <= n )
		{
			if ( ( n - m ) % 2 == 0 )
				list.add( m );
			m++;
		}

		return list;
	}

	/**
	 * implements the actual algoritm.
	 * 
	 * @return the complex value of the Zernike moment
	 */
	protected double[] computeMoment( int _n, int _m )
	{

		double real = 0;
		double imag = 0;

		// order
		final int n = _n;

		// repetition
		final int m = _m;

		if ( ( n < 0 ) || ( ( ( n - Math.abs( m ) ) % 2 ) != 0 ) || ( Math.abs( m ) > n ) )
			throw new IllegalArgumentException( "n and m do not satisfy the" + "Zernike moment properties" );

		final double centerX = center.get()[ 0 ];
		final double centerY = center.get()[ 1 ];
		final double max = Math.max( centerX, centerY );
		final double radius = Math.sqrt( 2 * max * max );

		final Polynom polynomOrthogonalRadial = createR( n, m );

		final Cursor< ? extends RealType< ? > > it = ii.localizingCursor();

		final double maxVal = it.get().getMaxValue();

		while ( it.hasNext() )
		{
			it.fwd();
			final double x = it.getIntPosition( 0 ) - centerX;
			final double y = it.getIntPosition( 1 ) - centerY;

			// compute polar coordinates for x and y
			final double r = Math.sqrt( ( x * x ) + ( y * y ) ) / radius;
			final double ang = m * Math.atan2( y, x );

			final double value = polynomOrthogonalRadial.evaluate( r );
			final double pixel = it.get().getRealDouble() / maxVal;

			real += pixel * value * Math.cos( ang );
			imag -= pixel * value * Math.sin( ang );
		}

		real = ( real * ( n + 1 ) ) / Math.PI;
		imag = ( imag * ( n + 1 ) ) / Math.PI;

		double[] res = { real, imag };
		return res;
	}

	/**
	 * create the polynom R_mn. see zernike documentation for more.
	 * 
	 * @param n
	 *            the "order"
	 * @param m
	 *            the "repetition"
	 * @return the F polynom
	 */
	public static Polynom createR( final int n, final int m )
	{
		final Polynom result = new Polynom( n );
		int sign = 1;
		for ( int s = 0; s <= ( ( n - Math.abs( m ) ) / 2 ); ++s )
		{
			final int pos = n - ( 2 * s );
			result.setCoefficient( pos, sign * computeF( n, m, s ) );
			sign = -sign;
		}
		return result;
	}

	/**
	 * compute F(n, m, s). see zernike documentation for more.
	 * 
	 * @param n
	 *            the "order"
	 * @param m
	 *            the "repetition"
	 * @param s
	 *            the index
	 * @return the Coefficient of r^(n-2*s) from R_mn(r)
	 */
	public static int computeF( final int n, final int m, final int s )
	{
		assert ( ( n + Math.abs( m ) ) % 2 ) == 0;
		assert ( ( n - Math.abs( m ) ) % 2 ) == 0;
		assert ( n - Math.abs( m ) ) >= 0;
		assert ( ( ( n - Math.abs( m ) ) / 2 ) - s ) >= 0;

		final int absN = Math.abs( m );

		final FactComputer fc = new FactComputer( n );
		fc.multiplyByFactorialOf( n - s );
		fc.divideByFactorialOf( s );
		fc.divideByFactorialOf( ( ( n + absN ) / 2 ) - s );
		fc.divideByFactorialOf( ( ( n - absN ) / 2 ) - s );

		return fc.value();
	}
}
