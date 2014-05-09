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
package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Collection;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.view.Views;

/**
 * @author Christian Dietz (University of Konstanz)
 *
 * @param <L>
 */
public class ExtractLabelingOutline< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	private ConnectedType m_ct;

	public ExtractLabelingOutline( ConnectedType ct )
	{
		m_ct = ct;
	}

	@Override
	public Labeling< L > compute( Labeling< L > op, Labeling< L > r )
	{

		Collection< L > labels = op.getLabels();

		RandomAccess< LabelingType< L >> opRa = Views.extendValue( op, new LabelingType< L >( new ArrayList< L >() ) ).randomAccess();
		RandomAccess< LabelingType< L >> resRa = r.randomAccess();

		IterableInterval< LabelingType< L >> ii = null;
		Cursor< LabelingType< L >> iiCursor = null;

		int[] pos = new int[ op.numDimensions() ];

		if ( m_ct == ConnectedType.FOUR_CONNECTED )
		{

			for ( L label : labels )
			{

				ii = op.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( op );

				iiCursor = ii.cursor();

				while ( iiCursor.hasNext() )
				{
					iiCursor.fwd();

					opRa.setPosition( iiCursor );
					opRa.localize( pos );

					// 4 Connected
					opRa.setPosition( pos[ 0 ] - 1, 0 );
					if ( !opRa.get().getLabeling().isEmpty() )
					{
						opRa.setPosition( pos[ 0 ] + 1, 0 );
						if ( !opRa.get().getLabeling().isEmpty() )
						{
							opRa.setPosition( pos[ 0 ], 0 );
							opRa.setPosition( pos[ 1 ] - 1, 1 );
							if ( !opRa.get().getLabeling().isEmpty() )
							{
								opRa.setPosition( pos[ 1 ] + 1, 1 );
								if ( !opRa.get().getLabeling().isEmpty() )
								{
									continue;
								}
							}
						}
					}

					resRa.setPosition( iiCursor );
					resRa.get().setLabeling( iiCursor.get().getLabeling() );
				}
			}
		}
		else if ( m_ct == ConnectedType.EIGHT_CONNECTED )
		{
			for ( L label : labels )
			{

				ii = op.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( op );

				iiCursor = ii.cursor();

				while ( iiCursor.hasNext() )
				{
					iiCursor.fwd();

					opRa.setPosition( iiCursor );
					opRa.localize( pos );

					// 8 Connected

					// middle left
					opRa.setPosition( pos[ 0 ] - 1, 0 );
					if ( !opRa.get().getLabeling().isEmpty() )
					{

						// middle right
						opRa.setPosition( pos[ 0 ] + 1, 0 );
						if ( !opRa.get().getLabeling().isEmpty() )
						{

							// upper right
							opRa.setPosition( pos[ 1 ] - 1, 1 );
							if ( !opRa.get().getLabeling().isEmpty() )
							{

								// lower right
								opRa.setPosition( pos[ 1 ] + 1, 1 );
								if ( !opRa.get().getLabeling().isEmpty() )
								{

									// lower
									// middle
									opRa.setPosition( pos[ 0 ], 0 );

									if ( !opRa.get().getLabeling().isEmpty() )
									{

										// lower
										// left
										opRa.setPosition( pos[ 0 ] - 1, 0 );

										if ( !opRa.get().getLabeling().isEmpty() )
										{

											// upper
											// left
											opRa.setPosition( pos[ 1 ] - 1, 1 );

											if ( !opRa.get().getLabeling().isEmpty() )
											{

												// upper
												// middle
												opRa.setPosition( pos[ 0 ], 0 );
												if ( !opRa.get().getLabeling().isEmpty() )
												{
													continue;
												}
											}
										}
									}
								}
							}
						}
					}

					resRa.setPosition( iiCursor );
					resRa.get().setLabeling( iiCursor.get().getLabeling() );
				}
			}
		}
		return r;

	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ExtractLabelingOutline< L >( m_ct );
	}

}
