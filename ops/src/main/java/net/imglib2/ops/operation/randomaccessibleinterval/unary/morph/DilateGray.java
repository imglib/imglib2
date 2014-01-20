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

package net.imglib2.ops.operation.randomaccessibleinterval.unary.morph;

import net.imglib2.Cursor;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.outofbounds.OutOfBoundsFactory;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.view.Views;

/**
 * Dilate operation on gray-level.
 * 
 * @author Felix Schoenenberger (University of Konstanz)
 * @author Jonathan Hale (University of Konstanz)
 * 
 * @param <T>
 */
public class DilateGray< T extends RealType< T >> implements UnaryOperation< RandomAccessibleInterval< T >, RandomAccessibleInterval< T > >
{

	private final long[][] m_struc;

	private final OutOfBoundsFactory< T, RandomAccessibleInterval< T >> m_factory;

	/**
	 * 
	 * @param structuringElement
	 * @param factory
	 */
	public DilateGray( final long[][] structuringElement, final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > factory )
	{
		m_factory = factory;
		m_struc = structuringElement;
	}

	/**
	 * @param structuringElement
	 * @param factory
	 */
	public DilateGray( final Img< BitType > structuringElement, final OutOfBoundsFactory< T, RandomAccessibleInterval< T > > factory )
	{
		m_struc = StructuringElementCursor.createElementFromImg( structuringElement );
		m_factory = factory;
	}

	@Override
	public RandomAccessibleInterval< T > compute( final RandomAccessibleInterval< T > input, final RandomAccessibleInterval< T > output )
	{
		final StructuringElementCursor< T > inStructure = new StructuringElementCursor< T >( Views.extend( input, m_factory ).randomAccess(), m_struc );
		final Cursor< T > out = Views.iterable( output ).localizingCursor();
		double m;
		while ( out.hasNext() )
		{
			out.next();
			inStructure.relocate( out );
			inStructure.next();
			m = inStructure.get().getRealDouble();
			while ( inStructure.hasNext() )
			{
				inStructure.next();
				m = Math.max( m, inStructure.get().getRealDouble() );
			}
			out.get().setReal( m );
		}
		return output;
	}

	@Override
	public DilateGray< T > copy()
	{
		return new DilateGray< T >( m_struc, m_factory );
	}
}
