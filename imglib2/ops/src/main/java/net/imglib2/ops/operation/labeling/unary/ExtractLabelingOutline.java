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
