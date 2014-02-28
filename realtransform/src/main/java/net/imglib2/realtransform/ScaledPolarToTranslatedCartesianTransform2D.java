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
 * Transforms scaled polar to translated cartesian coordinates.
 * 
 * The source coordinate <em>(r,&theta;)</em> is scaled by
 * <em>s<sub>r</sub></em>, <em>s<sub>&theta;</sub></em> then converted to
 * cartesian <em>(x',y')</em> and translated by <em>t</em> to obtain the target
 * coordinate <em>(x,y)</em>.
 * 
 * The translation <em>t</em> represents the origin of the polar transform. The
 * scale factors <em>s<sub>r</sub></em> and <em>s<sub>&theta;</sub></em>
 * respresent the radial and angular resolution of the polar image. For example,
 * <em>s<sub>r</sub>=3</em> means that pixel width in the polar image
 * corresponds to <em>1/3</em> pixel along the ray.
 * <em>s<sub>&theta;</sub>=36/(2*&Pi;)</em> means that pixel height in the polar
 * image corresponds to 10 degree.
 * 
 * For the inverse transform (cartesian to polar), the range of the computed
 * (unscaled) <em>&theta;</em> is <em>-pi</em> to <em>pi</em>.
 * 
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class ScaledPolarToTranslatedCartesianTransform2D implements InvertibleRealTransform
{
	private static double x( final double r, final double t )
	{
		return r * Math.cos( t );
	}

	private static double y( final double r, final double t )
	{
		return r * Math.sin( t );
	}

	private static double r( final double x, final double y )
	{
		return Math.sqrt( x * x + y * y );
	}

	private static double t( final double x, final double y )
	{
		return Math.atan2( y, x );
	}

	private final double tx;

	private final double ty;

	private final double sr;

	private final double st;

	private final InverseRealTransform inverse;

	public ScaledPolarToTranslatedCartesianTransform2D( final double tx, final double ty, final double sr, final double st )
	{
		this.tx = tx;
		this.ty = ty;
		this.sr = sr;
		this.st = st;
		inverse = new InverseRealTransform( this );
	}

	@Override
	public int numSourceDimensions()
	{
		return 2;
	}

	@Override
	public int numTargetDimensions()
	{
		return 2;
	}

	@Override
	public void apply( final double[] source, final double[] target )
	{
		final double r = source[ 0 ] / sr;
		final double t = source[ 1 ] / st;
		target[ 0 ] = x( r, t ) + tx;
		target[ 1 ] = y( r, t ) + ty;
	}

	@Override
	public void apply( final float[] source, final float[] target )
	{
		final double r = source[ 0 ] / sr;
		final double t = source[ 1 ] / st;
		target[ 0 ] = ( float ) ( x( r, t ) + tx );
		target[ 1 ] = ( float ) ( y( r, t ) + ty );
	}

	@Override
	public void apply( final RealLocalizable source, final RealPositionable target )
	{
		final double r = source.getDoublePosition( 0 ) / sr;
		final double t = source.getDoublePosition( 1 ) / st;
		target.setPosition( x( r, t ) + tx, 0 );
		target.setPosition( y( r, t ) + ty, 1 );
	}

	@Override
	public void applyInverse( final double[] source, final double[] target )
	{
		final double x = target[ 0 ] - tx;
		final double y = target[ 1 ] - ty;
		source[ 0 ] = r( x, y ) * sr;
		source[ 1 ] = t( x, y ) * st;
	}

	@Override
	public void applyInverse( final float[] source, final float[] target )
	{
		final double x = target[ 0 ] - tx;
		final double y = target[ 1 ] - ty;
		source[ 0 ] = ( float ) ( r( x, y ) * sr );
		source[ 1 ] = ( float ) ( t( x, y ) * st );
	}

	@Override
	public void applyInverse( final RealPositionable source, final RealLocalizable target )
	{
		final double x = target.getDoublePosition( 0 ) - tx;
		final double y = target.getDoublePosition( 1 ) - ty;
		source.setPosition( r( x, y ) * sr, 0 );
		source.setPosition( t( x, y ) * st, 1 );
	}

	@Override
	public InvertibleRealTransform inverse()
	{
		return inverse;
	}

	@Override
	public ScaledPolarToTranslatedCartesianTransform2D copy()
	{
		return new ScaledPolarToTranslatedCartesianTransform2D( tx, ty, sr, st );
	}
}
