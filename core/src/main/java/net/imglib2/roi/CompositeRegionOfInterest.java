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
 * #L%
 */

package net.imglib2.roi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.imglib2.RealRandomAccess;
import net.imglib2.type.logic.BitType;

/**
 * A composite region of interest contains all points in its member regions of
 * interest
 * 
 * <b>Note</b>: please use the class {@link GeneralPathRegionOfInterest}
 * instead.
 * 
 * @deprecated
 * 
 * @author Stephan Saalfeld
 * @author Lee Kamentsky
 */
@Deprecated
public class CompositeRegionOfInterest extends AbstractRegionOfInterest
{
	private enum Operation
	{
		OR, AND, XOR, NOT
	}

	final private ArrayList< RegionOfInterest > rois = new ArrayList< RegionOfInterest >();

	final private Map< RegionOfInterest, Operation > operations = new HashMap< RegionOfInterest, Operation >();

	final private ThreadLocal< Map< RegionOfInterest, RealRandomAccess< BitType >>> randomAccess = new ThreadLocal< Map< RegionOfInterest, RealRandomAccess< BitType >>>();

	/**
	 * Initialize an empty composite region of interest.
	 * 
	 * @param nDimensions
	 */
	public CompositeRegionOfInterest( final int nDimensions )
	{
		super( nDimensions );
	}

	/**
	 * Initialize a composite region of interest with a single ROI
	 * 
	 * @param roi
	 */
	public CompositeRegionOfInterest( final RegionOfInterest roi )
	{
		super( roi.numDimensions() );
		rois.add( roi );
		operations.put( roi, Operation.OR );
	}

	public CompositeRegionOfInterest( final Collection< RegionOfInterest > rois )
	{
		super( rois.size() == 0 ? 0 : rois.iterator().next().numDimensions() );
		for ( final RegionOfInterest roi : rois )
		{
			this.rois.add( roi );
			operations.put( roi, Operation.OR );
		}
	}

	/**
	 * Make the region of interest a combination of the new rest and all
	 * previous
	 * 
	 * @param roi
	 */
	public void or( final RegionOfInterest roi )
	{
		rois.add( roi );
		operations.put( roi, Operation.OR );
	}

	/**
	 * Remove the region from the composite
	 * 
	 * @param roi
	 */
	public void remove( final RegionOfInterest roi )
	{
		rois.remove( roi );
		operations.remove( roi );
	}

	/**
	 * Make the region the union of the current and this
	 * 
	 * @param roi
	 */
	public void and( final RegionOfInterest roi )
	{
		rois.add( roi );
		operations.put( roi, Operation.AND );
	}

	/**
	 * For points within the region, invert the membership.
	 * 
	 * @param roi
	 */
	public void xor( final RegionOfInterest roi )
	{
		rois.add( roi );
		operations.put( roi, Operation.XOR );
	}

	/**
	 * Remove this region from the composite region of interest
	 * 
	 * @param roi
	 */
	public void not( final RegionOfInterest roi )
	{
		rois.add( roi );
		operations.put( roi, Operation.NOT );
	}

	@Override
	public void move( final double displacement, final int d )
	{
		for ( final RegionOfInterest roi : rois )
			roi.move( displacement, d );
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractRegionOfInterest#isMember(double[])
	 */
	@Override
	public boolean contains( final double[] position )
	{
		boolean result = false;
		for ( final RegionOfInterest roi : rois )
		{
			final Operation operation = operations.get( roi );
			switch ( operation )
			{
			case AND:
				if ( result == true )
				{
					result = isMember( roi, position );
				}
				break;
			case OR:
				if ( result == false )
				{
					result = isMember( roi, position );
				}
				break;
			case NOT:
				if ( result == true )
				{
					result = !isMember( roi, position );
				}
				break;
			case XOR:
				result = ( result ^ isMember( roi, position ) );
				break;
			}
		}
		return result;
	}

	private boolean isMember( final RegionOfInterest roi, final double[] position )
	{
		if ( randomAccess.get() == null )
		{
			randomAccess.set( new HashMap< RegionOfInterest, RealRandomAccess< BitType >>() );
		}
		if ( !randomAccess.get().containsKey( roi ) )
		{
			randomAccess.get().put( roi, roi.realRandomAccess() );
		}
		final RealRandomAccess< BitType > ra = randomAccess.get().get( roi );
		ra.setPosition( position );
		return ra.get().get();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see net.imglib2.roi.AbstractRegionOfInterest#getRealExtrema(double[],
	 * double[])
	 */
	@Override
	protected void getRealExtrema( final double[] minima, final double[] maxima )
	{
		Arrays.fill( minima, Double.MAX_VALUE );
		Arrays.fill( maxima, -Double.MAX_VALUE );
		for ( final RegionOfInterest roi : rois )
		{
			for ( int i = 0; i < numDimensions(); i++ )
			{
				minima[ i ] = Math.min( minima[ i ], roi.realMin( i ) );
				maxima[ i ] = Math.max( maxima[ i ], roi.realMax( i ) );
			}
		}
	}
}
