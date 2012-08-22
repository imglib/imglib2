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
 * @author dietzc, schoenenf
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
