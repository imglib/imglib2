package mpicbg.imglib.interpolation.linear;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.NumericType;

public class LinearInterpolator1D<T extends NumericType<T>> extends LinearInterpolator<T> 
{
	final int[] tmpLocation;

	protected LinearInterpolator1D( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutsideStrategyFactory<T> outsideStrategyFactory )
	{
		super( img, interpolatorFactory, outsideStrategyFactory, false );

		tmpLocation = new int[ 1 ];
		moveTo( position );		
	}
	
	@Override
	public T getType() { return tmp2; }
	
	@Override
	public void moveTo( final float[] position )
	{
		final float x = position[ 0 ];
		
		this.position[ 0 ] = x;
		
		//     *----x--*
		//   y0         y1

		// base offset (y0)
		final int baseX1 = x > 0 ? (int)x: (int)x-1;

		// update iterator position
		tmpLocation[ 0 ] = baseX1;
		
		cursor.moveTo( tmpLocation );

		// How to iterate the cube
		//
		//     *----x->*
		//   y0         y1

		// weights
		final float t = x - baseX1;
		final float t1 = 1 - t;

		tmp1.set( cursor.getType() );
		tmp1.mul( t1 );
		tmp2.set( tmp1 );

		cursor.fwd( 0 );
		tmp1.set( cursor.getType() );
		tmp1.mul( t );
		tmp2.add( tmp1 );
	}
	
	@Override
	public void setPosition( final float[] position )
	{
		final float x = position[ 0 ];
		
		this.position[ 0 ] = x;
		
		//     *----x--*
		//   y0         y1

		// base offset (y0)
		final int baseX1 = x > 0 ? (int)x: (int)x-1;

		// update iterator position
		tmpLocation[ 0 ] = baseX1;
		
		cursor.setPosition( tmpLocation );

		// How to iterate the cube
		//
		//     *----x->*
		//   y0         y1

		// weights
		final float t = x - baseX1;
		final float t1 = 1 - t;

		tmp1.set( cursor.getType() );
		tmp1.mul( t1 );
		tmp2.set( tmp1 );

		cursor.fwd( 0 );
		tmp1.set( cursor.getType() );
		tmp1.mul( t );
		tmp2.add( tmp1 );
	}	
	
}
