/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.algorithm.scalespace;

import net.imglib2.algorithm.kdtree.node.Leaf;
import net.imglib2.algorithm.scalespace.DifferenceOfGaussian.SpecialPoint;
import net.imglib2.cursor.Localizable;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.util.Util;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class DifferenceOfGaussianPeak< T extends NumericType<T> > implements Localizable, Leaf<DifferenceOfGaussianPeak<T>>
{
	SpecialPoint specialPoint;
	String errorMessage;
	
	final protected int[] pixelLocation;
	final protected float[] subPixelLocationOffset;
	final protected T value, fitValue, sumValue;
	final int numDimensions;
	
	public DifferenceOfGaussianPeak( final int[] pixelLocation, final T value, final SpecialPoint specialPoint )
	{
		this.specialPoint = specialPoint;
		this.pixelLocation = pixelLocation.clone();
		this.subPixelLocationOffset = new float[ pixelLocation.length ];
		
		this.numDimensions = pixelLocation.length;
		
		this.value = value.copy();
		this.sumValue = value.copy();
		this.fitValue = value.createVariable();
		this.fitValue.setZero();
		
		this.errorMessage = "";
	}
	
	public DifferenceOfGaussianPeak<T> copy()
	{
		final DifferenceOfGaussianPeak<T> copy = new DifferenceOfGaussianPeak<T>(
				pixelLocation, 
				value, 
				specialPoint );
		
		copy.setFitValue( fitValue );
		copy.setSubPixelLocationOffset( subPixelLocationOffset );
		
		return copy;
	}
	
	public boolean isMin() { return specialPoint == SpecialPoint.MIN; }
	public boolean isMax() { return specialPoint == SpecialPoint.MAX; }
	public boolean isValid() { return specialPoint != SpecialPoint.INVALID; }
	public SpecialPoint getPeakType() { return specialPoint; }
	public float[] getSubPixelPositionOffset() { return subPixelLocationOffset.clone(); }
	public float getSubPixelPositionOffset( final int dim ) { return subPixelLocationOffset[ dim ]; }
	public float[] getSubPixelPosition() 
	{
		final float[] loc = subPixelLocationOffset.clone();
		
		for ( int d = 0; d < loc.length; ++d )
			loc[ d ] += pixelLocation[ d ];
		
		return loc; 
	}
	public void getSubPixelPosition( final float[] loc )
	{
		for ( int d = 0; d < loc.length; ++d )
			loc[ d ] = subPixelLocationOffset[ d ] + pixelLocation[ d ];		
	}

	public float getSubPixelPosition( final int dim ) { return subPixelLocationOffset[ dim ] + pixelLocation[ dim ]; }
	public T getValue() { return sumValue; }
	public T getImgValue() { return value; }
	public T getFitValue() { return fitValue; }
	public String getErrorMessage() { return errorMessage; }
	
	public void setPeakType( final SpecialPoint specialPoint ) { this.specialPoint = specialPoint; }
	public void setSubPixelLocationOffset( final float subPixelLocationOffset, final int dim ) { this.subPixelLocationOffset[ dim ] = subPixelLocationOffset; }
	public void setSubPixelLocationOffset( final float[] subPixelLocationOffset )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			this.subPixelLocationOffset[ d ] = subPixelLocationOffset[ d ];
	}
	public void setPixelLocation( final int location, final int dim ) { pixelLocation[ dim ] = location; }
	public void setPixelLocation( final int[] pixelLocation )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			this.pixelLocation[ d ] = pixelLocation[ d ];
	}
	public void setImgValue( final T value ) 
	{ 
		this.value.set( value );
		
		sumValue.set( this.value );
		sumValue.add( this.fitValue );
	}
	public void setFitValue( final T value ) 
	{
		this.fitValue.set( value ); 

		sumValue.set( this.value );
		sumValue.add( this.fitValue );
	}
	public void setErrorMessage( final String errorMessage ) { this.errorMessage = errorMessage; }

	@Override
	public void getPosition( final int[] position )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			position[ d ] = pixelLocation[ d ];
	}

	@Override
	public int[] getPosition() { return pixelLocation.clone(); }

	@Override
	public int getPosition( final int dim ) { return pixelLocation[ dim ]; }

	@Override
	public String getPositionAsString() { return Util.printCoordinates( pixelLocation );	}

	@Override
	public void fwd( final long steps ) {}

	@Override
	public void fwd() {}

	@Override
	public boolean isLeaf() { return true; }

	@Override
	public float get( final int k ) { return getSubPixelPosition( k ); }

	@Override
	public float distanceTo( final DifferenceOfGaussianPeak<T> other ) 
	{
		double sum = 0;
		
		for ( int d = 0; d < numDimensions; ++d )
		{
			final double tmp = other.get( d ) - get( d ); 
			sum += tmp * tmp;
		}

		return (float)Math.sqrt( sum );
	}

	@Override
	public int getNumDimensions() {	return numDimensions; }

	@SuppressWarnings("unchecked")
	@Override
	public DifferenceOfGaussianPeak<T>[] createArray( final int n ) { return new DifferenceOfGaussianPeak[ n ]; }

	@Override
	public DifferenceOfGaussianPeak<T> getEntry() { return this; }
}
