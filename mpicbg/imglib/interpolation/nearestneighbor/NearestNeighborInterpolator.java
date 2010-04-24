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
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.interpolation.nearestneighbor;

import mpicbg.imglib.cursor.PositionableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.interpolation.InterpolatorFactory;
import mpicbg.imglib.interpolation.AbstractInterpolator;
import mpicbg.imglib.location.Positionable;
import mpicbg.imglib.location.RasterPositionable;
import mpicbg.imglib.location.link.LocalizableRoundRasterPositionable;
import mpicbg.imglib.outofbounds.OutOfBoundsStrategyFactory;
import mpicbg.imglib.type.Type;

public class NearestNeighborInterpolator<T extends Type<T>> extends AbstractInterpolator<T>
{
	final protected PositionableCursor<T> cursor;
	final private LocalizableRoundRasterPositionable roundLink; 
	
	protected NearestNeighborInterpolator( final Image<T> img, final InterpolatorFactory<T> interpolatorFactory, final OutOfBoundsStrategyFactory<T> outOfBoundsStrategyFactory )
	{
		super(img, interpolatorFactory, outOfBoundsStrategyFactory);
		
		cursor = img.createPositionableCursor( outOfBoundsStrategyFactory );
		linkedRasterPositionable = linkedPositionable = roundLink = new LocalizableRoundRasterPositionable( this, cursor );
		
		
		moveTo( position );		
	}

	@Override
	public void close() { cursor.close(); }

	@Override
	public T type() { return cursor.type(); }
	
	
	/* LinkablePositionable */
	
	@Override
	public void linkPositionable( final Positionable positionable )
	{
		roundLink.linkPositionable( positionable );
	}

	@Override
	public Positionable unlinkPositionable()
	{
		return roundLink.unlinkPositionable();
	}

	@Override
	public void linkRasterPositionable( final RasterPositionable rasterPositionable )
	{
		roundLink.linkRasterPositionable( rasterPositionable );
	}

	@Override
	public RasterPositionable unlinkRasterPositionable()
	{
		return roundLink.unlinkRasterPositionable();
	}
}
