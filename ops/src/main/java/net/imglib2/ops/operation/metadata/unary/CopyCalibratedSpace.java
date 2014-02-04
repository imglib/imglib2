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

package net.imglib2.ops.operation.metadata.unary;

import net.imglib2.Interval;
import net.imglib2.meta.CalibratedAxis;
import net.imglib2.meta.CalibratedSpace;
import net.imglib2.ops.operation.UnaryOperation;

/**
 * @author Christian Dietz (University of Konstanz)
 * @author Curtis Rueden
 * 
 * @param <S>
 *            The type of the space to copy
 */
public class CopyCalibratedSpace< S extends CalibratedSpace< CalibratedAxis > > implements UnaryOperation< S, S >
{
	private Interval interval;

	public CopyCalibratedSpace()
	{
		interval = null;
	}

	public CopyCalibratedSpace( Interval interval )
	{
		this.interval = interval;
	}

	@Override
	public S compute( S input, S output )
	{

		int offset = 0;
		for ( int d = 0; d < input.numDimensions(); d++ )
		{
			if ( interval != null && interval.dimension( d ) == 1 )
			{
				offset++;
			}
			else
			{
				// NB: Axes are copied by reference here. If an axis is later
				// mutated, this could cause unintuitive side effects...
				output.setAxis( input.axis( d ), d - offset );

				// No longer needed:
				// output.setCalibration(input.averageScale(d), d - offset);
			}
		}

		return output;
	}

	@Override
	public UnaryOperation< S, S > copy()
	{
		return new CopyCalibratedSpace< S >();
	}

}
