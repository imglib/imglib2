/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.container.Img;
import mpicbg.imglib.container.ImgRandomAccess;
import mpicbg.imglib.interpolation.Interpolator;
import mpicbg.imglib.location.transform.RoundRasterPositionable;
import mpicbg.imglib.outofbounds.OutOfBoundsFactory;
import mpicbg.imglib.type.Type;

/**
 * 
 * @param <T>
 *
 * @author Stephan Preibisch and Stephan Saalfeld
 */
public class NearestNeighborInterpolator< T extends Type< T > > extends RoundRasterPositionable< ImgRandomAccess< T > > implements Interpolator< T, Img<T> >
{
	final protected OutOfBoundsFactory< T, Img<T> > outOfBoundsStrategyFactory;
	final protected Img< T > container;
	
	final static private < T extends Type< T > > ImgRandomAccess< T > createPositionableRasterSampler( Img< T > container, final OutOfBoundsFactory<T,Img<T>> outOfBoundsStrategyFactory )
	{
		return container.integerRandomAccess( outOfBoundsStrategyFactory );
	}
	
	protected NearestNeighborInterpolator( Img< T > container, final OutOfBoundsFactory<T,Img<T>> outOfBoundsStrategyFactory )
	{
		super( createPositionableRasterSampler( container, outOfBoundsStrategyFactory ) );
		
		this.outOfBoundsStrategyFactory = outOfBoundsStrategyFactory;
		this.container = container;
	}
	
	
	/* Dimensionality */
	
	@Override
	public int numDimensions()
	{
		return container.numDimensions();
	}

	/**
	 * Returns the {@link RasterOutOfBoundsFactory} used for interpolation
	 * 
	 * @return - the {@link RasterOutOfBoundsFactory}
	 */
	@Override
	public OutOfBoundsFactory< T, Img<T> > getOutOfBoundsStrategyFactory()
	{
		return outOfBoundsStrategyFactory;
	}

	/**
	 * Returns the typed image the interpolator is working on
	 * 
	 * @return - the image
	 */
	@Override
	public Img< T > getFunction()
	{
		return container;
	}
	
	@Override
	public T get() { return target.get(); }
	 
	@Override 
	public T create() { return target.create(); }
	
	@Override
	@Deprecated
	final public T getType()
	{
		return get();
	}
}
