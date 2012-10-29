/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
package net.imglib2.ops.operation.labeling.unary;

import java.util.HashSet;
import java.util.List;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;

/**
 * @author Christian Dietz (University of Konstanz)
 *
 * @param <L>
 */
public class ExcludeOnEdges< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	@Override
	public Labeling< L > compute( Labeling< L > inLabeling, Labeling< L > outLabeling )
	{

		if ( inLabeling.numDimensions() != 2 ) { throw new IllegalArgumentException( "Exclude on edges works only on two dimensional images" ); }

		long[] dims = new long[ inLabeling.numDimensions() ];
		inLabeling.dimensions( dims );

		HashSet< List< L >> indices = new HashSet< List< L >>();

		RandomAccess< LabelingType< L >> outRndAccess = outLabeling.randomAccess();
		RandomAccess< LabelingType< L >> inRndAccess = inLabeling.randomAccess();

		Cursor< LabelingType< L >> cur = inLabeling.cursor();

		long[] pos = new long[ inLabeling.numDimensions() ];

		for ( int d = 0; d < dims.length; d++ )
		{

			for ( int i = 0; i < Math.pow( 2, dims.length - 1 ); i++ )
			{

				int offset = 0;
				for ( int dd = 0; dd < dims.length; dd++ )
				{
					if ( dd == d )
					{
						offset++;
						continue;
					}
					pos[ dd ] = ( i % Math.pow( 2, dd - offset + 1 ) == 0 ) ? 0 : dims[ dd ] - 1;
				}

				pos[ d ] = 0;
				for ( int k = 0; k < dims[ d ]; k++ )
				{
					pos[ d ] = k;
					inRndAccess.setPosition( pos );

					if ( 0 != inRndAccess.get().getLabeling().size() )
					{
						indices.add( inRndAccess.get().getLabeling() );
					}
				}
			}
		}

		while ( cur.hasNext() )
		{
			cur.fwd();
			if ( !indices.contains( cur.get().getLabeling() ) )
			{
				cur.localize( pos );
				outRndAccess.setPosition( pos );
				outRndAccess.get().setLabeling( cur.get().getLabeling() );
			}
		}
		return outLabeling;

	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ExcludeOnEdges< L >();
	}

}
