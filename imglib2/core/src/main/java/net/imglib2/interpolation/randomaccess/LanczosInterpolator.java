package net.imglib2.interpolation.randomaccess;

import net.imglib2.Localizable;
import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealLocalizable;
import net.imglib2.RealRandomAccess;
import net.imglib2.Sampler;
import net.imglib2.iterator.OffsetableLocalizingIntervalIterator;
import net.imglib2.type.numeric.RealType;

/**
 * n-dimensional float-based Lanczos Interpolation
 * 
 * @author Stephan Preibisch
 */
public class LanczosInterpolator< T extends RealType< T > > implements RealRandomAccess< T >
{
	final protected static float piSquare = (float) ( Math.PI * Math.PI );

	final double alphaD;
	final int alpha, n;	
	final T interpolatedValue;	
	final long[] min, max, size;
	final double[] position;
	
	final OffsetableLocalizingIntervalIterator iterator;
	final RandomAccess< T > randomAccess;
	
	final double minValue, maxValue;
	final boolean clipping;

	/**
	 * Creates a new Lanczos-interpolation
	 * 
	 * @param randomAccessible - the {@link RandomAccessible} to work on
	 * @param alpha - the radius of values to incorporate (ideally the whole image)
	 * @param clipping - clips the value to range of the {@link RealType}, i.e. tests if the interpolated value is out of range
	 */
	public LanczosInterpolator( final RandomAccessible< T > randomAccessible, final int alpha, final boolean clipping )
	{
		this.alpha = alpha;
		this.alphaD = alpha;
		
		this.n = randomAccessible.numDimensions();
		this.min = new long[ n ];
		this.max = new long[ n ];
		this.size = new long[ n ];
		this.position = new double[ n ];
		
		for ( int d = 0; d < n; ++d )
		{
			size[ d ] = alpha * 2;
			max[ d ] = size[ d ] - 1;
		}
		
		this.clipping = clipping;
		
		this.iterator = new OffsetableLocalizingIntervalIterator( min, max );
		this.randomAccess = randomAccessible.randomAccess();
		
		this.interpolatedValue = randomAccess.get().createVariable();
		this.minValue = interpolatedValue.getMinValue();
		this.maxValue = interpolatedValue.getMaxValue();
	}

	public LanczosInterpolator( final LanczosInterpolator< T > interpolator )
	{
		this.alpha = interpolator.alpha;
		this.alphaD = interpolator.alphaD;
		
		this.n = interpolator.n;
		this.min = interpolator.min.clone();
		this.max = interpolator.max.clone();
		this.size = interpolator.size.clone();
		this.position = interpolator.position.clone();
				
		this.clipping = interpolator.clipping;
		
		this.iterator = new OffsetableLocalizingIntervalIterator( min, max );
		this.randomAccess = interpolator.randomAccess.copyRandomAccess();
		
		this.interpolatedValue = interpolator.interpolatedValue.copy();
		this.minValue = interpolator.minValue;
		this.maxValue = interpolator.maxValue;
	}

	@Override
	public T get() 
	{
		// set now offset and reset
		iterator.setMin( min );
		
		double convolved = 0;
		
		while ( iterator.hasNext() )
		{
			iterator.fwd();
			
			double v = 1.0;
			
			// TODO: this could be smarter, usually only changes in x ...
			// Btw, this cannot be pre-computed as position[] is double ...
			for ( int d = 0; d < n; ++d )
				v *= sinc( position[ d ] - iterator.getLongPosition( d ), alphaD );
			
			randomAccess.setPosition( iterator );
			convolved += randomAccess.get().getRealFloat() * v;
		}
		
		// do clipping if desired (it should be, except maybe for float or double input)
		if ( clipping )
		{
			if ( convolved < minValue )
				convolved = minValue;
			else if ( convolved > maxValue )
				convolved = maxValue;
		}

		interpolatedValue.setReal( convolved );
		
		return interpolatedValue; 
	}

	private static final double sinc( final double x, final double a )
	{
		if ( x == 0 )
			return 1;
		else
			return (( a * Math.sin( Math.PI * x ) * Math.sin( Math.PI * x / a ) ) / ( piSquare * x * x ));
	}
	
	private static final long floor( final double value )
	{
		return value > 0 ? (long)value : (long)value-1;
	}

	@Override
	public void localize( final float[] position ) 
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = (float)this.position[ d ];
	}

	@Override
	public void localize( final double[] position ) 
	{
		for ( int d = 0; d < n; ++d )
			position[ d ] = this.position[ d ];
	}

	@Override
	public float getFloatPosition( final int d ) { return (float)position[ d ]; }

	@Override
	public double getDoublePosition( final int d ) { return position[ d ]; }

	@Override
	public int numDimensions() { return n; }

	@Override
	public void move( final float distance, final int d ) 
	{ 
		position[ d ] += distance;
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;
	}

	@Override
	public void move( final double distance, final int d ) 
	{ 
		position[ d ] += distance; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;
	}

	@Override
	public void move( final RealLocalizable localizable ) 
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += localizable.getDoublePosition( d );
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;
		}
	}

	@Override
	public void move( final float[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;
		}
	}

	@Override
	public void move( final double[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;
		}
	}

	@Override
	public void setPosition( final RealLocalizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = localizable.getDoublePosition( d );
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;
		}
	}

	@Override
	public void setPosition( final float[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;
		}
	}

	@Override
	public void setPosition( final double[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;
		}
	}

	@Override
	public void setPosition( final float position, final int d ) 
	{ 
		this.position[ d ] = position; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void setPosition( final double position, final int d ) 
	{ 
		this.position[ d ] = position; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void fwd( final int d ) 
	{ 
		++position[ d ]; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void bck( final int d ) 
	{ 
		--position[ d ]; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void move( final int distance, final int d ) 
	{ 
		position[ d ] += distance; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void move( final long distance, final int d )  
	{ 
		position[ d ] += distance; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void move( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += localizable.getDoublePosition( d );
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
		}
	}

	@Override
	public void move( final int[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
		}
	}

	@Override
	public void move( final long[] distance )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] += distance[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
		}
	}

	@Override
	public void setPosition( final Localizable localizable )
	{
		for ( int d = 0; d < n; ++d )
		{
			position[ d ] = localizable.getDoublePosition( d );
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
		}
	}

	@Override
	public void setPosition( final int[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
		}
	}

	@Override
	public void setPosition( final long[] position )
	{
		for ( int d = 0; d < n; ++d )
		{
			this.position[ d ] = position[ d ];
			min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
		}
	}

	@Override
	public void setPosition( final int position, final int d ) 
	{ 
		this.position[ d ] = position; 
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public void setPosition( final long position, final int d ) 
	{ 
		this.position[ d ] = position;
		min[ d ] = floor( this.position[ d ] ) - alpha + 1;		
	}

	@Override
	public Sampler< T > copy() { return copy(); }

	@Override
	public RealRandomAccess<T> copyRealRandomAccess() { return new LanczosInterpolator< T >( this ); }
}
