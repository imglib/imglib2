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
package net.imglib2.ops.operation.randomaccessibleinterval.unary;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.type.numeric.RealType;

/**
 * @author Clemens Muething (University of Konstanz)
 */
public class GrayscaleReconstructionByErosion< T extends RealType< T >, V extends RealType< V >> extends AbstractGrayscaleReconstruction< T, V >
{

	public GrayscaleReconstructionByErosion( final ConnectedType connection )
	{
		super( connection );
	}

	public GrayscaleReconstructionByErosion( final GrayscaleReconstructionByErosion< T, V > copy )
	{
		super( copy );
	}

	@Override
	protected final boolean checkPixelFromQueue( final V p, final V q, final T i )
	{
		double pd = p.getRealDouble();
		double qd = q.getRealDouble();
		double id = i.getRealDouble();

		return qd > pd && qd != id;
	}

	@Override
	protected final V morphOp( final V a, final V b )
	{
		if ( a.getRealDouble() < b.getRealDouble() )
			return a;
		return b;
	}

	@Override
	protected final V pointwiseOp( final V a, final T b )
	{
		if ( a.getRealDouble() > b.getRealDouble() )
		{
			return a;
		}
		V r = a.createVariable();
		r.setReal( b.getRealDouble() );
		return r;
	}

	@Override
	protected final boolean checkPixelAddToQueue( final V p, final V q, final T i )
	{
		double pd = p.getRealDouble();
		double qd = q.getRealDouble();
		double id = i.getRealDouble();

		return qd > pd && qd > id;
	}

	@Override
	protected V getVMinValue( final V var )
	{
		var.setReal( var.getMinValue() );
		return var;
	}

	@Override
	protected T getTMinValue( final T var )
	{
		var.setReal( var.getMinValue() );
		return var;
	}

	@Override
	public UnaryOperation< RandomAccessibleInterval<T>, RandomAccessibleInterval<V> > copy()
	{
		return new GrayscaleReconstructionByErosion< T, V >( this );
	}
}
