/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
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

package net.imglib2.transform.integer;

/**
 * TODO
 * 
 */
public abstract class AbstractMixedTransform implements Mixed
{
	/**
	 * dimension of target vector.
	 */
	protected final int numTargetDimensions;

	protected AbstractMixedTransform( final int numTargetDimensions )
	{
		this.numTargetDimensions = numTargetDimensions;
	}

	@Override
	public int numSourceDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public int numTargetDimensions()
	{
		return numTargetDimensions;
	}

	@Override
	public void getTranslation( final long[] translation )
	{
		assert translation.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			translation[ d ] = 0;
	}

	@Override
	public long getTranslation( final int d )
	{
		return 0;
	}

	@Override
	public void getComponentZero( final boolean[] zero )
	{
		assert zero.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			zero[ d ] = false;
	}

	@Override
	public boolean getComponentZero( final int d )
	{
		return false;
	}

	@Override
	public void getComponentMapping( final int[] component )
	{
		assert component.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			component[ d ] = d;
	}

	@Override
	public int getComponentMapping( final int d )
	{
		return d;
	}

	@Override
	public void getComponentInversion( final boolean[] invert )
	{
		assert invert.length >= numTargetDimensions;

		for ( int d = 0; d < numTargetDimensions; ++d )
			invert[ d ] = false;
	}

	@Override
	public boolean getComponentInversion( final int d )
	{
		return false;
	}

	@Override
	public BoundingBox transform( final BoundingBox boundingBox )
	{
		assert boundingBox.numDimensions() == numSourceDimensions();

		if ( numSourceDimensions() == numTargetDimensions )
		{ // apply in-place
			final long[] tmp = new long[ numTargetDimensions ];
			boundingBox.corner1( tmp );
			apply( tmp, boundingBox.corner1 );
			boundingBox.corner2( tmp );
			apply( tmp, boundingBox.corner2 );
			return boundingBox;
		}
		final BoundingBox b = new BoundingBox( numTargetDimensions );
		apply( boundingBox.corner1, b.corner1 );
		apply( boundingBox.corner2, b.corner2 );
		return b;
	}
}
