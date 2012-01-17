/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the imglib project nor
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
package net.imglib2.realtransform;

import net.imglib2.RealLocalizable;
import net.imglib2.RealPositionable;

/**
 * Final implementation of the inverse of an {@link InvertibleRealTransform}
 * that simply replaces apply by applyInverse and conversely.  The original
 * {@link InvertibleRealTransform} is returned on {@link #inverse()}.
 * 
 * @author Tobias Pietzsch
 * @author Stephan Saalfeld <saalfeld@mpi-cbg.de>
 */
public final class InverseRealTransform implements InvertibleRealTransform
{
	private final InvertibleRealTransform inverse;
	
	public InverseRealTransform( final InvertibleRealTransform transform )
	{
		inverse = transform;
	}

	@Override
	public int numSourceDimensions()
	{
		return inverse.numTargetDimensions();
	}

	@Override
	public int numTargetDimensions()
	{
		return inverse.numSourceDimensions();
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		inverse.applyInverse( target, source );
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		inverse.apply( target, source );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		inverse.apply( target, source );
	}

	@Override
	public InvertibleRealTransform inverse()
	{
		return inverse;
	}

}
