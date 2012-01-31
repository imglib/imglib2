package net.imglib2.interpolation.randomaccess;

import net.imglib2.RandomAccessible;
import net.imglib2.RealInterval;
import net.imglib2.interpolation.InterpolatorFactory;
import net.imglib2.type.numeric.RealType;

public class LanczosInterpolatorFactory<T extends RealType<T>> implements InterpolatorFactory< T, RandomAccessible< T > >
{
	int alpha;
	boolean clipping;
	
	/**
	 * Creates a new {@link LanczosInterpolatorFactory} using the Lanczos (sinc) interpolation in a certain window
	 * 
	 * @param alpha - the rectangular radius of the window for perfoming the lanczos interpolation
	 * @param clipping - the lanczos-interpolation can create values that are bigger or smaller than the original values,
	 *        so they can be clipped to the range of the {@link Type} if wanted
	 */
	public LanczosInterpolatorFactory( final int alpha, final boolean clipping )
	{
		this.alpha = alpha;
		this.clipping = clipping;
	}

	/**
	 * Creates a new {@link LanczosInterpolatorFactory} with standard parameters (do clipping, alpha=3)
	 */
	public LanczosInterpolatorFactory()
	{
		this( 3, true );
	}
	
	@Override
	public LanczosInterpolator< T > create( final RandomAccessible< T > randomAccessible )
	{
		return new LanczosInterpolator< T >( randomAccessible, alpha, clipping );
	}

	/**
	 * For now, ignore the {@link RealInterval} and return
	 * {@link #create(RandomAccessible)}.
	 */
	@Override
	public LanczosInterpolator< T > create( final RandomAccessible< T > randomAccessible, final RealInterval interval )
	{
		return create( randomAccessible );
	}

	/**
	 * Set the rectangular radius of the window for perfoming the lanczos interpolation
	 * @param alpha - radius
	 */
	public void setAlpha( final int alpha ) { this.alpha = alpha; }
	
	/**
	 * The lanczos-interpolation can create values that are bigger or smaller than the original values,
	 * so they can be clipped to the range of the {@link RealType} if wanted
	 * 
	 * @param clipping - perform clipping (true)
	 */
	public void setClipping( final boolean clipping ) { this.clipping = clipping; }
	
	/**
	 * @return - rectangular radius of the window for perfoming the lanczos interpolation 
	 */
	public int getAlpha() { return alpha; }
	
	/**
	 * @return - if clipping to the {@link RealType} range will be performed 
	 */
	public boolean getClipping() { return clipping; }
}
