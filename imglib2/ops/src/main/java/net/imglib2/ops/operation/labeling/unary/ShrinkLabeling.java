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
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.imglib2.Cursor;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.view.Views;

/**
 * Shrinks a labeling
 * 
 * @author Christian Dietz (University of Konstanz)
 * @author Felix Schoenenberger (University of Konstanz)
 * 
 * @param <L>
 */
public class ShrinkLabeling< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	private final ConnectedType m_ct;

	private final int m_numIterations;

	public ShrinkLabeling( ConnectedType ct, int numIterations )
	{
		m_ct = ct;
		m_numIterations = numIterations;
	}

	@Override
	public Labeling< L > compute( final Labeling< L > op, final Labeling< L > r )
	{

		OutOfBounds< LabelingType< L >> opRa = Views.extendValue( op, new LabelingType< L >( new ArrayList< L >() ) ).randomAccess();
		OutOfBounds< LabelingType< L >> resRa = Views.extendValue( r, new LabelingType< L >( new ArrayList< L >() ) ).randomAccess();

		int[] pos = new int[ op.numDimensions() ];

		Set< int[] > nextSeeds = new HashSet< int[] >();
		for ( int i = 0; i < m_numIterations; i++ )
		{
			Cursor< LabelingType< L >> labelingCursor = op.cursor();
			if ( i == 0 )
			{
				if ( m_ct == ConnectedType.FOUR_CONNECTED )
				{

					while ( labelingCursor.hasNext() )
					{
						labelingCursor.fwd();

						if ( labelingCursor.get().getLabeling().isEmpty() )
						{
							resRa.setPosition( labelingCursor );
							resRa.get().setLabeling( resRa.get().getMapping().emptyList() );
						}
						else
						{

							opRa.setPosition( labelingCursor );
							opRa.localize( pos );

							nextSeeds.addAll( operate4Connected( pos, labelingCursor.get().getLabeling(), opRa, resRa ) );
						}
					}
				}
				else if ( m_ct == ConnectedType.EIGHT_CONNECTED )
				{

					while ( labelingCursor.hasNext() )
					{
						labelingCursor.fwd();

						if ( labelingCursor.get().getLabeling().isEmpty() )
						{
							resRa.setPosition( labelingCursor );
							resRa.get().setLabeling( resRa.get().getMapping().emptyList() );
						}
						else
						{

							opRa.setPosition( labelingCursor );
							opRa.localize( pos );

							// 8 Connected
							nextSeeds.addAll( operate8Connected( pos, labelingCursor.get().getLabeling(), opRa, resRa ) );
						}
					}
				}
			}
			else
			{
				Set< int[] > currentSeeds = new HashSet< int[] >();
				currentSeeds.addAll( nextSeeds );
				nextSeeds.clear();
				if ( m_ct == ConnectedType.FOUR_CONNECTED )
				{

					for ( int[] currentSeed : currentSeeds )
					{
						resRa.setPosition( currentSeed );

						nextSeeds.addAll( operate4Connected( currentSeed, resRa.get().getLabeling(), resRa, resRa ) );
					}
				}

				if ( m_ct == ConnectedType.EIGHT_CONNECTED )
				{
					for ( int[] currentSeed : currentSeeds )
					{
						resRa.setPosition( currentSeed );

						nextSeeds.addAll( operate8Connected( currentSeed, resRa.get().getLabeling(), resRa, resRa ) );
					}
				}

			}
		}
		return r;

	}

	private synchronized Collection< int[] > operate4Connected( int[] currentPos, List< L > currentLabeling, OutOfBounds< LabelingType< L >> opRa, OutOfBounds< LabelingType< L >> resRa )
	{

		List< int[] > nextSeeds = new ArrayList< int[] >();

		// 4 Connected
		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		if ( !opRa.get().getLabeling().isEmpty() )
		{
			opRa.setPosition( currentPos[ 0 ] + 1, 0 );
			if ( !opRa.get().getLabeling().isEmpty() )
			{
				opRa.setPosition( currentPos[ 0 ], 0 );
				opRa.setPosition( currentPos[ 1 ] - 1, 1 );
				if ( !opRa.get().getLabeling().isEmpty() )
				{
					opRa.setPosition( currentPos[ 1 ] + 1, 1 );
					if ( !opRa.get().getLabeling().isEmpty() )
					{
						nextSeeds.add( currentPos.clone() );
					}
				}
			}
		}

		// Settings result cursor to currentPos
		resRa.setPosition( currentPos );

		// No seeds: Labeling touches empty region
		if ( nextSeeds.size() == 0 )
		{
			resRa.get().setLabeling( opRa.get().getMapping().emptyList() );
		}
		else
		{
			resRa.get().setLabeling( currentLabeling );
		}

		return nextSeeds;
	}

	private synchronized Collection< int[] > operate8Connected( int[] currentPos, List< L > currentLabeling, OutOfBounds< LabelingType< L >> opRa, OutOfBounds< LabelingType< L >> resRa )
	{

		List< int[] > nextSeeds = new ArrayList< int[] >();

		// middle left
		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		if ( !opRa.get().getLabeling().isEmpty() )
		{

			// middle right
			opRa.setPosition( currentPos[ 0 ] + 1, 0 );
			if ( !opRa.get().getLabeling().isEmpty() )
			{

				// upper right
				opRa.setPosition( currentPos[ 1 ] - 1, 1 );
				if ( !opRa.get().getLabeling().isEmpty() )
				{

					// lower right
					opRa.setPosition( currentPos[ 1 ] + 1, 1 );
					if ( !opRa.get().getLabeling().isEmpty() )
					{

						// lower middle
						opRa.setPosition( currentPos[ 0 ], 0 );

						if ( !opRa.get().getLabeling().isEmpty() )
						{

							// lower left
							opRa.setPosition( currentPos[ 0 ] - 1, 0 );

							if ( !opRa.get().getLabeling().isEmpty() )
							{

								// upper left
								opRa.setPosition( currentPos[ 1 ] - 1, 1 );

								if ( !opRa.get().getLabeling().isEmpty() )
								{

									// upper
									// middle
									opRa.setPosition( currentPos[ 0 ], 0 );
									if ( !opRa.get().getLabeling().isEmpty() )
									{
										nextSeeds.add( currentPos.clone() );
									}
								}
							}
						}
					}
				}
			}
		}

		// Settings result cursor to currentPos
		resRa.setPosition( currentPos );

		// No seeds: Labeling touches empty region
		if ( nextSeeds.size() == 0 )
		{
			resRa.get().setLabeling( opRa.get().getMapping().emptyList() );
		}
		else
		{
			resRa.get().setLabeling( currentLabeling );
		}

		return nextSeeds;
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ShrinkLabeling< L >( m_ct, m_numIterations );
	}
}
