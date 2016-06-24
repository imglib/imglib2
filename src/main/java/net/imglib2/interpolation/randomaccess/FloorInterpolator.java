/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2015 Tobias Pietzsch, Stephan Preibisch, Barry DeZonia,
 * Stephan Saalfeld, Curtis Rueden, Albert Cardona, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Jonathan Hale, Lee Kamentsky, Larry Lindsey, Mark
 * Hiner, Michael Zinsmaier, Martin Horn, Grant Harris, Aivar Grislis, John
 * Bogovic, Steffen Jaensch, Stefan Helfrich, Jan Funke, Nick Perry, Mark Longair,
 * Melissa Linkert and Dimiter Prodanov.
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

package net.imglib2.interpolation.randomaccess;

import net.imglib2.RandomAccess;
import net.imglib2.RandomAccessible;
import net.imglib2.RealRandomAccess;
import net.imglib2.position.transform.Floor;

/**
 * {@link RealRandomAccess} to a {@link RandomAccess} by nearest floor
 * interpolation (i.e., the value is sampled at floor(<em>x</em>)).
 * 
 * <p>In ImgLib2, the coordinate of a sample corresponds to the 'center' point
 * of the sample, i.e. the location at which the sample was acquired.  This
 * scheme is intuitive in both rasters and irregularly samples data but can
 * trigger confusion when displaying images on a screen with a pixel raster
 * which, in this scheme, spans the range [-0.5,<em>width</em>-0.5].  In the
 * screen-friendly alternative scheme, where sample coordinates reference the
 * top left corner of the pixel rectangle representing a sample the range
 * covered by an image is [0,<em>width</em>], however, coordinate transfer
 * functions other than translation and homogeneous scaling generate different
 * results than in the center-scheme.  Rendering an image using
 * {@link FloorInterpolator} means using the top-left-scheme, rendering it
 * using {@link NearestNeighborInterpolator}, {@link NLinearInterpolator}, or
 * {@link LanczosInterpolator} means using the center-scheme.</p>
 * 
 * @param <T>
 * 
 * @author Stephan Saalfeld (saalfelds@janelia.hhmi.org)
 */
public class FloorInterpolator< T > extends Floor< RandomAccess< T > > implements RealRandomAccess< T >
{
	protected FloorInterpolator( final FloorInterpolator< T > floorInterpolator )
	{
		super( floorInterpolator.target.copyRandomAccess() );
	}

	protected FloorInterpolator( final RandomAccessible< T > randomAccessible )
	{
		super( randomAccessible.randomAccess() );
	}

	@Override
	public T get()
	{
		return target.get();
	}

	@Override
	public FloorInterpolator< T > copy()
	{
		return new FloorInterpolator< T >( this );
	}

	@Override
	public FloorInterpolator< T > copyRealRandomAccess()
	{
		return copy();
	}
}
