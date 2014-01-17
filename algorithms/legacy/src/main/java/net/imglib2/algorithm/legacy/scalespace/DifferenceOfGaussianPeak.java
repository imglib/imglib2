/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package net.imglib2.algorithm.legacy.scalespace;

import net.imglib2.Localizable;
import net.imglib2.algorithm.legacy.scalespace.DifferenceOfGaussian.SpecialPoint;
import net.imglib2.type.numeric.NumericType;

/**
 * TODO
 *
 * @author Stephan Preibisch
 */
public class DifferenceOfGaussianPeak< T extends NumericType<T> > implements Localizable
{
	SpecialPoint specialPoint;
	String errorMessage;

	final protected long[] pixelLocation;
	final protected float[] subPixelLocationOffset;
	final protected T value, fitValue, sumValue;
	final int numDimensions;

	public DifferenceOfGaussianPeak( final long[] pixelLocation, final T value, final SpecialPoint specialPoint )
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

	public DifferenceOfGaussianPeak( final Localizable pixelLocation, final T value, final SpecialPoint specialPoint )
	{
		this.numDimensions = pixelLocation.numDimensions();
		this.specialPoint = specialPoint;
		this.pixelLocation = new long[ numDimensions ];
		pixelLocation.localize( this.pixelLocation );
		this.subPixelLocationOffset = new float[ numDimensions ];

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
	public void setPixelLocation( final long[] pixelLocation )
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
	public void localize( final int[] position )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			position[ d ] = (int)pixelLocation[ d ];
	}

	@Override
	public void localize( final long[] position )
	{
		for ( int d = 0; d < pixelLocation.length; ++d )
			position[ d ] = pixelLocation[ d ];
	}

	public long[] localize() { return pixelLocation.clone(); }

	@Override
	public long getLongPosition( final int dim ) { return pixelLocation[ dim ]; }

	@Override
	public int getIntPosition( final int dim ) { return (int)pixelLocation[ dim ]; }

//	@Override
//	public float get( final int k ) { return getSubPixelPosition( k ); }

//	@Override
//	public float distanceTo( final DifferenceOfGaussianPeak<T> other )
//	{
//		double sum = 0;
//
//		for ( int d = 0; d < numDimensions; ++d )
//		{
//			final double tmp = other.get( d ) - get( d );
//			sum += tmp * tmp;
//		}
//
//		return (float)Math.sqrt( sum );
//	}

	@Override
	public int numDimensions() { return numDimensions; }

	@Override
	public void localize( final float[] position ) { getSubPixelPosition( position ); }

	@Override
	public void localize( final double[] position)
	{
		for ( int d = 0; d < position.length; ++d )
			position[ d ] = subPixelLocationOffset[ d ] + pixelLocation[ d ];
	}

	@Override
	public float getFloatPosition( final int d ) { return getSubPixelPosition( d ); }

	@Override
	public double getDoublePosition( final int d ) { return getSubPixelPosition( d ); }
}
