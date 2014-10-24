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

package net.imglib2.realtransform;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * An {@link InvertibleRealTransform} that is a sequence of
 * {@link InvertibleRealTransform InvertibleRealTransforms}.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class InvertibleRealTransformSequence extends AbstractRealTransformSequence< InvertibleRealTransform > implements InvertibleRealTransform
{
	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		assert source.length >= nSource && target.length >= nTarget: "Input dimensions too small.";

		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			if ( s > 0 )
			{
				transforms.get( s ).applyInverse( tmp, target );
				
				for ( int i = s - 1; i > 0; --i )
					transforms.get( i ).applyInverse( tmp, tmp );

				transforms.get( 0 ).applyInverse( source, tmp );
			}
			else
				transforms.get( 0 ).applyInverse( source, target );
		}
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		assert source.length >= nSource && target.length >= nTarget: "Input dimensions too small.";

		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			for ( int d = 0; d < nTarget; ++d )
				tmp[ d ] = target[ d ];

			for ( int i = s; i > -1; --i )
				transforms.get( i ).applyInverse( tmp, tmp );

			for ( int d = 0; d < nSource; ++d )
				source[ d ] = ( float )tmp[ d ];
		}
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		assert source.numDimensions() >= nSource && target.numDimensions() >= nTarget: "Input dimensions too small.";

		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			if ( s > 0 )
			{
				transforms.get( s ).applyInverse( ptmp, target );
				
				for ( int i = s - 1; i > 0; --i )
					transforms.get( i ).applyInverse( tmp, tmp );

				transforms.get( 0 ).applyInverse( source, ptmp );
			}
			else
				transforms.get( 0 ).applyInverse( source, target );
		}
	}

	@Override
	public InvertibleRealTransform inverse()
	{
		return new InverseRealTransform( this );
	}

	@Override
	public InvertibleRealTransformSequence copy()
	{
		final InvertibleRealTransformSequence copy = new InvertibleRealTransformSequence();
		for ( final InvertibleRealTransform t : transforms )
			copy.add( t.copy() );
		return copy;
	}
}
