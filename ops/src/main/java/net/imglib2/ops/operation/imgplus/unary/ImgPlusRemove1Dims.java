/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

package net.imglib2.ops.operation.imgplus.unary;

import java.util.BitSet;

import net.imglib2.Cursor;
import net.imglib2.FinalInterval;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.meta.ImgPlus;
import net.imglib2.meta.Metadata;
import net.imglib2.ops.operation.UnaryOutputOperation;
import net.imglib2.ops.operation.metadata.unary.CopyCalibratedSpace;
import net.imglib2.ops.operation.metadata.unary.CopyImageMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyMetadata;
import net.imglib2.ops.operation.metadata.unary.CopyNamed;
import net.imglib2.ops.operation.metadata.unary.CopySourced;
import net.imglib2.type.Type;

/**
 * Reduces the dimensions of an image by removing all dimensions having only one
 * pixel.
 * 
 * @author Martin Horn (University of Konstanz)
 */
public class ImgPlusRemove1Dims< T extends Type< T >> implements UnaryOutputOperation< ImgPlus< T >, ImgPlus< T >>
{

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ImgPlus< T > createEmptyOutput( ImgPlus< T > op )
	{

		BitSet isLength1 = new BitSet( op.numDimensions() );
		for ( int d = 0; d < op.numDimensions(); d++ )
		{
			if ( op.dimension( d ) == 1 )
			{
				isLength1.set( d );
			}
		}

		long[] min = new long[ op.numDimensions() - isLength1.cardinality() ];
		long[] max = new long[ min.length ];

		int d = 0;
		for ( int i = 0; i < op.numDimensions(); i++ )
		{
			if ( !isLength1.get( i ) )
			{
				max[ d ] = op.dimension( i ) - 1;
				d++;
			}
		}
		Img< T > res = op.factory().create( new FinalInterval( min, max ), op.firstElement().createVariable() );
		return new ImgPlus< T >( res );
	}

	/**
	 * {@inheritDoc}
	 */
	@SuppressWarnings( "unchecked" )
	@Override
	public ImgPlus< T > compute( ImgPlus< T > op, ImgPlus< T > r )
	{
		Cursor< T > srcCur = op.localizingCursor();
		RandomAccess< T > resRA = r.randomAccess();

		new CopyMetadata( new CopyNamed< Metadata >(), new CopyImageMetadata< Metadata >(), new CopySourced< Metadata >(), new CopyCalibratedSpace< Metadata >( r ) ).compute( op, r );

		BitSet isLength1 = new BitSet( op.numDimensions() );
		for ( int d = 0; d < op.numDimensions(); d++ )
		{
			if ( op.dimension( d ) == 1 )
			{
				isLength1.set( d );
			}
		}

		int d;
		while ( srcCur.hasNext() )
		{
			srcCur.fwd();
			d = 0;
			for ( int i = 0; i < op.numDimensions(); i++ )
			{
				if ( !isLength1.get( i ) )
				{
					resRA.setPosition( srcCur.getLongPosition( i ), d );
					d++;
				}
			}
			resRA.get().set( srcCur.get() );

		}
		return r;
	}

	@Override
	public UnaryOutputOperation< ImgPlus< T >, ImgPlus< T >> copy()
	{
		return new ImgPlusRemove1Dims< T >();
	}

	@Override
	public ImgPlus< T > compute( ImgPlus< T > op )
	{
		return compute( op, createEmptyOutput( op ) );
	}

}
