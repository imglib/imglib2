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

import java.util.ArrayList;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPoint;
import net.imglib2.RealPositionable;

/**
 * Shared properties of {@link RealTransformSequence} and
 * {@link InvertibleRealTransformSequence}, sequences of something that extends
 * {@link RealTransform RealTransforms}.
 * 
 * @author Stephan Saalfeld <saalfelds@janelia.hhmi.org>
 */
public class AbstractRealTransformSequence< R extends RealTransform > implements RealTransform
{
	final protected ArrayList< R > transforms = new ArrayList< R >();;

	protected double[] tmp = new double[ 0 ];

	protected RealPoint ptmp = RealPoint.wrap( tmp );

	protected int nSource = 0;

	protected int nTarget = 0;

	/**
	 * Append a {@link RealTransform} to the sequence.
	 * 
	 * @param transform
	 */
	public void add( final R transform )
	{
		transforms.add( transform );
		
		if ( transforms.size() == 1 )
		{
			nSource = transform.numSourceDimensions();
			
			/**
			 * tmp has to be initialized at source size to enable
			 * #apply(float[], float[]) later which requires initial copy of
			 * source into tmp.
			 */
			tmp = new double[ nSource ];
			ptmp = RealPoint.wrap( tmp );
		}
		
		nTarget = transform.numTargetDimensions();
		
		if ( tmp.length < nTarget )
		{
			tmp = new double[ nTarget ];
			ptmp = RealPoint.wrap( tmp );
		}
	}

	@Override
	public int numSourceDimensions()
	{
		return nSource;
	}

	@Override
	public int numTargetDimensions()
	{
		return nTarget;
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		assert source.length >= nSource && target.length >= nTarget: "Input dimensions too small.";

		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			if ( s > 0 )
			{
				transforms.get( 0 ).apply( source, tmp );
				
				for ( int i = 1; i < s; ++i )
					transforms.get( i ).apply( tmp, tmp );
				
				transforms.get( s ).apply( tmp, target );
			}
			else
				transforms.get( 0 ).apply( source, target );
		}
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		assert source.length >= nSource && target.length >= nTarget: "Input dimensions too small.";

		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			for ( int d = 0; d < nSource; ++d )
				tmp[ d ] = source[ d ];

			for ( final RealTransform t : transforms )
				t.apply( tmp, tmp );

			for ( int d = 0; d < nTarget; ++d )
				target[ d ] = ( float )tmp[ d ];
		}
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		assert source.numDimensions() >= nSource && target.numDimensions() >= nTarget: "Input dimensions too small.";

		final int s = transforms.size() - 1;
		if ( s > -1 )
		{
			if ( s > 0 )
			{
				transforms.get( 0 ).apply( source, ptmp );
				for ( int i = 1; i < s; ++i )
					transforms.get( i ).apply( tmp, tmp );

				transforms.get( s ).apply( ptmp, target );
			}
			else
				transforms.get( 0 ).apply( source, target );
		}
	}

	@SuppressWarnings( "unchecked" )
	@Override
	public AbstractRealTransformSequence< R > copy()
	{
		final AbstractRealTransformSequence< R > copy = new AbstractRealTransformSequence< R >();
		for ( final R t : transforms )
			copy.add( ( R ) t.copy() );
		return copy;
	}
}
