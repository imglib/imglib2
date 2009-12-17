package mpicbg.imglib.interpolation;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.outside.OutsideStrategyFactory;
import mpicbg.imglib.type.numeric.FloatType;

public class LinearInterpolator3DFloat extends LinearInterpolator<FloatType> 
{
	final int tmpLocation[];

	protected LinearInterpolator3DFloat( final Image<FloatType> img, final InterpolatorFactory<FloatType> interpolatorFactory, final OutsideStrategyFactory<FloatType> outsideStrategyFactory )
	{
		super( img, interpolatorFactory, outsideStrategyFactory, false );
		
		tmpLocation = new int[ 3 ];				
		moveTo( position );		
	}
	
	@Override
	public FloatType getType() { return tmp2; }
	
	@Override
	public void moveTo( final float[] position )
	{
		final float x = position[ 0 ];
		final float y = position[ 1 ];
		final float z = position[ 2 ];
		
		this.position[ 0 ] = x;
		this.position[ 1 ] = y;
		this.position[ 2 ] = z;
		
		//       y7     y6
		//        *-------*
		//       /       /|
		//   y3 /    y2 / |
		//     *-------*  * y5
		//     |    x  | /
		//     |       |/
		//     *-------*
		//     y0    y1

		// base offset (y0)
		final int baseX1 = x > 0 ? (int)x: (int)x-1;
		final int baseX2 = y > 0 ? (int)y: (int)y-1;
		final int baseX3 = z > 0 ? (int)z: (int)z-1;

		// update iterator position
		tmpLocation[ 0 ] = baseX1;
		tmpLocation[ 1 ] = baseX2;
		tmpLocation[ 2 ] = baseX3;
		
		cursor.moveTo( tmpLocation );

		// How to iterate the cube
		//
		//       y7     y6
		//        *------>*
		//       ^       /|
		//   y3 /    y2 / v
		//     *<------*  * y5
		//     |    x  ^ /
		//     |       |/
		//     *------>*
		//     y0    y1

        //final float y0 = strategy.get(baseX1    , baseX2,     baseX3);
        final float y0 = cursor.getType().get(); 
        
        //final float y1 = strategy.get(baseX1 + 1, baseX2,     baseX3);
        cursor.fwd( 0 );
        final float y1 = cursor.getType().get(); 
        
        //final float y2 = strategy.get(baseX1 + 1, baseX2 + 1, baseX3);
        cursor.fwd( 1 );
        final float y2 = cursor.getType().get(); 
                
        //final float y3 = strategy.get(baseX1    , baseX2 + 1, baseX3);
        cursor.bck( 0 );
        final float y3 = cursor.getType().get(); 
        
        //final float y7 = strategy.get(baseX1    , baseX2 + 1, baseX3 + 1);
        cursor.fwd( 2 );
        final float y7 = cursor.getType().get();

        //final float y6 = strategy.get(baseX1 + 1, baseX2 + 1, baseX3 + 1);
        cursor.fwd( 0 );
        final float y6 = cursor.getType().get();

        //final float y5 = strategy.get(baseX1 + 1, baseX2,     baseX3 + 1);
        cursor.bck( 1 );
        final float y5 = cursor.getType().get();
        
        //final float y4 = strategy.get(baseX1    , baseX2,	  baseX3 + 1);
        cursor.bck( 0 );
        final float y4 = cursor.getType().get();        

        // weights
        final float t = x - baseX1; 
        final float u = y - baseX2; 
        final float v = z - baseX3;

        final float t1 = 1 - t;
        final float u1 = 1 - u;
        final float v1 = 1 - v;

        final float value = t1*u1*v1*y0 + t*u1*v1*y1 + t*u*v1*y2 + t1*u*v1*y3 + 
                            t1*u1*v*y4  + t*u1*v*y5  + t*u*v*y6  + t1*u*v*y7;
        
        tmp2.set( value );
	}
	
	@Override
	public void setPosition( final float[] position )
	{
		final float x = position[ 0 ];
		final float y = position[ 1 ];
		final float z = position[ 2 ];
		
		this.position[ 0 ] = x;
		this.position[ 1 ] = y;
		this.position[ 2 ] = z;
		
		//       y7     y6
		//        *-------*
		//       /       /|
		//   y3 /    y2 / |
		//     *-------*  * y5
		//     |    x  | /
		//     |       |/
		//     *-------*
		//     y0    y1

		// base offset (y0)
		final int baseX1 = x > 0 ? (int)x: (int)x-1;
		final int baseX2 = y > 0 ? (int)y: (int)y-1;
		final int baseX3 = z > 0 ? (int)z: (int)z-1;

		// update iterator position
		tmpLocation[ 0 ] = baseX1;
		tmpLocation[ 1 ] = baseX2;
		tmpLocation[ 2 ] = baseX3;
		
		cursor.setPosition( tmpLocation );

		// How to iterate the cube
		//
		//       y7     y6
		//        *------>*
		//       ^       /|
		//   y3 /    y2 / v
		//     *<------*  * y5
		//     |    x  ^ /
		//     |       |/
		//     *------>*
		//     y0    y1

        //final float y0 = strategy.get(baseX1    , baseX2,     baseX3);
        final float y0 = cursor.getType().get(); 
        
        //final float y1 = strategy.get(baseX1 + 1, baseX2,     baseX3);
        cursor.fwd( 0 );
        final float y1 = cursor.getType().get(); 
        
        //final float y2 = strategy.get(baseX1 + 1, baseX2 + 1, baseX3);
        cursor.fwd( 1 );
        final float y2 = cursor.getType().get(); 
                
        //final float y3 = strategy.get(baseX1    , baseX2 + 1, baseX3);
        cursor.bck( 0 );
        final float y3 = cursor.getType().get(); 
        
        //final float y7 = strategy.get(baseX1    , baseX2 + 1, baseX3 + 1);
        cursor.fwd( 2 );
        final float y7 = cursor.getType().get();

        //final float y6 = strategy.get(baseX1 + 1, baseX2 + 1, baseX3 + 1);
        cursor.fwd( 0 );
        final float y6 = cursor.getType().get();

        //final float y5 = strategy.get(baseX1 + 1, baseX2,     baseX3 + 1);
        cursor.bck( 1 );
        final float y5 = cursor.getType().get();
        
        //final float y4 = strategy.get(baseX1    , baseX2,	  baseX3 + 1);
        cursor.bck( 0 );
        final float y4 = cursor.getType().get();        

        // weights
        final float t = x - baseX1; 
        final float u = y - baseX2; 
        final float v = z - baseX3;

        final float t1 = 1 - t;
        final float u1 = 1 - u;
        final float v1 = 1 - v;

        final float value = t1*u1*v1*y0 + t*u1*v1*y1 + t*u*v1*y2 + t1*u*v1*y3 + 
                            t1*u1*v*y4  + t*u1*v*y5  + t*u*v*y6  + t1*u*v*y7;
        
        tmp2.set( value );
	}	
	
}
