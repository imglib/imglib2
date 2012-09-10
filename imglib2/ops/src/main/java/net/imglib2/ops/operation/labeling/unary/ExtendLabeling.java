package net.imglib2.ops.operation.labeling.unary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.labeling.Labeling;
import net.imglib2.labeling.LabelingType;
import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.types.ConnectedType;
import net.imglib2.outofbounds.OutOfBounds;
import net.imglib2.view.Views;

public class ExtendLabeling< L extends Comparable< L >> implements UnaryOperation< Labeling< L >, Labeling< L >>
{

	private final ConnectedType m_ct;

	private int m_numIterations = 1;

	public ExtendLabeling( final ConnectedType type, final int numIterations )
	{
		m_ct = type;
		m_numIterations = numIterations;
	}

	@Override
	public Labeling< L > compute( final Labeling< L > op, final Labeling< L > r )
	{
		Collection< L > labels = null;

		labels = op.getLabels();

		Set< int[] > newSeeds = new HashSet< int[] >();

		OutOfBounds< LabelingType< L >> opRa = Views.extendValue( op, new LabelingType< L >( op.firstElement().getMapping().emptyList() ) ).randomAccess();
		OutOfBounds< LabelingType< L >> resRa = Views.extendValue( r, new LabelingType< L >( op.firstElement().getMapping().emptyList() ) ).randomAccess();

		IterableInterval< LabelingType< L >> ii = null;
		Cursor< LabelingType< L >> iiCursor = null;
		int[] pos = new int[ op.numDimensions() ];

		for ( int i = 0; i < m_numIterations; i++ )
		{

			switch ( i )
			{
			case 0:
				newSeeds.clear();
				for ( L label : labels )
				{

					ii = op.getIterableRegionOfInterest( label ).getIterableIntervalOverROI( op );

					iiCursor = ii.cursor();

					while ( iiCursor.hasNext() )
					{
						iiCursor.fwd();

						opRa.setPosition( iiCursor );
						resRa.setPosition( iiCursor );
						opRa.localize( pos );

						setLabeling( iiCursor.get().getLabeling(), resRa );

						if ( m_ct == ConnectedType.EIGHT_CONNECTED )
						{
							newSeeds.addAll( operate8Connected( pos, iiCursor.get().getLabeling(), opRa, resRa ) );
						}
						else if ( m_ct == ConnectedType.FOUR_CONNECTED )
						{
							newSeeds.addAll( operate4Connected( pos, iiCursor.get().getLabeling(), opRa, resRa ) );
						}

					}
				}
				break;
			default:
				Set< int[] > currentSeeds = new HashSet< int[] >();
				currentSeeds.addAll( newSeeds );
				newSeeds.clear();
				for ( int[] nextPos : currentSeeds )
				{
					opRa.setPosition( nextPos );
					resRa.setPosition( nextPos );
					if ( m_ct == ConnectedType.EIGHT_CONNECTED )
					{
						newSeeds.addAll( operate8Connected( nextPos, resRa.get().getLabeling(), resRa, resRa ) );
					}
					else if ( m_ct == ConnectedType.FOUR_CONNECTED )
					{
						newSeeds.addAll( operate4Connected( nextPos, resRa.get().getLabeling(), resRa, resRa ) );
					}
				}

			}
		}
		return r;
	}

	private static synchronized < L extends Comparable< L >> Set< int[] > operate8Connected( final int[] currentPos, final List< L > currentLabeling, final OutOfBounds< LabelingType< L >> opRa, final OutOfBounds< LabelingType< L >> resRa )
	{

		Set< int[] > nextSeeds = new HashSet< int[] >();

		// middle left
		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// middle right
		opRa.setPosition( currentPos[ 0 ] + 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// upper right
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		opRa.setPosition( currentPos[ 0 ] + 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// lower right
		opRa.setPosition( currentPos[ 1 ] + 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// lower middle
		opRa.setPosition( currentPos[ 0 ], 0 );
		opRa.setPosition( currentPos[ 1 ] + 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// lower left
		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// upper left
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		// upper middle
		opRa.setPosition( currentPos[ 0 ], 0 );
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		return nextSeeds;
	}

	private static synchronized < L extends Comparable< L >> void checkAndSet( final List< L > currentLabeling, int[] currentPos, final OutOfBounds< LabelingType< L >> opRa, final OutOfBounds< LabelingType< L >> resRa, final Set< int[] > nextSeeds )
	{

		if ( !opRa.get().getLabeling().containsAll( currentLabeling ) && !opRa.isOutOfBounds() )
		{
			// result access is set
			resRa.setPosition( opRa );

			// position is retrieved
			int[] tmpPos = currentPos.clone();
			resRa.localize( tmpPos );

			// Labeling is set
			setLabeling( currentLabeling, resRa );

			// pos is added to the list of new seeds
			nextSeeds.add( tmpPos.clone() );
		}

	}

	private static synchronized < L extends Comparable< L >> Collection< int[] > operate4Connected( final int[] currentPos, final List< L > currentLabeling, final OutOfBounds< LabelingType< L >> opRa, final OutOfBounds< LabelingType< L >> resRa )
	{

		// 4 Connected
		Set< int[] > nextSeeds = new HashSet< int[] >();

		opRa.setPosition( currentPos[ 0 ] - 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		opRa.setPosition( currentPos[ 0 ] + 1, 0 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		opRa.setPosition( currentPos[ 0 ], 0 );
		opRa.setPosition( currentPos[ 1 ] - 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		opRa.setPosition( currentPos[ 1 ] + 1, 1 );
		checkAndSet( currentLabeling, currentPos, opRa, resRa, nextSeeds );

		return nextSeeds;
	}

	private static synchronized < L extends Comparable< L >> void setLabeling( final List< L > currentLabels, final RandomAccess< LabelingType< L >> resRa )
	{
		HashSet< L > tmpLabels = new HashSet< L >();
		tmpLabels.clear();
		tmpLabels.addAll( currentLabels );
		tmpLabels.addAll( resRa.get().getLabeling() );

		resRa.get().setLabeling( new ArrayList< L >( tmpLabels ) );
	}

	@Override
	public UnaryOperation< Labeling< L >, Labeling< L >> copy()
	{
		return new ExtendLabeling< L >( m_ct, m_numIterations );
	}
}
