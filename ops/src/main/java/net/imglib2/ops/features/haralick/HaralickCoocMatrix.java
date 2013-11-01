package net.imglib2.ops.features.haralick;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.data.CooccurrenceMatrix.MatrixOrientation;
import net.imglib2.ops.features.AbstractFeature;
import net.imglib2.ops.features.RequiredFeature;
import net.imglib2.ops.features.firstorder.Max;
import net.imglib2.ops.features.firstorder.Min;
import net.imglib2.ops.features.providers.GetIterableInterval;
import net.imglib2.type.numeric.RealType;

public class HaralickCoocMatrix< T extends RealType< T >> extends AbstractFeature< CooccurrenceMatrix >
{

	@RequiredFeature
	GetIterableInterval< T > ii;

	@RequiredFeature
	Min< T > min;

	@RequiredFeature
	Max< T > max;

	private final int nrGrayLevels;

	private final int distance;

	private final MatrixOrientation orientation;

	public HaralickCoocMatrix( final int nrGrayLevels, final int distance, final MatrixOrientation orientation )
	{
		this.nrGrayLevels = nrGrayLevels;
		this.distance = distance;
		this.orientation = orientation;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String name()
	{
		return "Cooccurence Matrix Provider";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CooccurrenceMatrix recompute()
	{

		final IterableInterval< T > input = ii.get();

		final Cursor< T > cursor = input.cursor();

		final double localMin = this.min.get().get();

		final double localMax = this.max.get().get();

		int[][] pixels = new int[ ( int ) input.dimension( 0 ) ][ ( int ) input.dimension( 1 ) ];

		for ( int i = 0; i < pixels.length; i++ )
		{
			Arrays.fill( pixels[ i ], Integer.MAX_VALUE );
		}

		CooccurrenceMatrix matrix = new CooccurrenceMatrix( nrGrayLevels );

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			pixels[ cursor.getIntPosition( 1 ) - ( int ) input.min( 1 ) ][ cursor.getIntPosition( 0 ) - ( int ) input.min( 0 ) ] = ( int ) ( ( ( cursor.get().getRealDouble() - localMin ) / ( localMax - localMin + 1 ) ) * nrGrayLevels );

		}

		int nrPairs = 0;

		for ( int y = 0; y < pixels.length; y++ )
		{
			for ( int x = 0; x < pixels[ y ].length; x++ )
			{
				// ignore pixels not in mask
				if ( pixels[ y ][ x ] == Integer.MAX_VALUE )
				{
					continue;
				}

				// get second pixel
				int sx = x + getOrientation().dx * distance;
				int sy = y + getOrientation().dy * distance;
				// get third pixel
				int tx = x - getOrientation().dx * distance;
				int ty = y - getOrientation().dy * distance;

				// second pixel in interval and mask
				if ( sx >= 0 && sy >= 0 && sy < pixels.length && sx < pixels[ sy ].length && pixels[ sy ][ sx ] != Integer.MAX_VALUE )
				{
					matrix.incValueAt( pixels[ y ][ x ], pixels[ sy ][ sx ] );
					nrPairs++;
				}
				// third pixel in interval
				if ( tx >= 0 && ty >= 0 && ty < pixels.length && tx < pixels[ ty ].length && pixels[ ty ][ tx ] != Integer.MAX_VALUE )
				{
					matrix.incValueAt( pixels[ y ][ x ], pixels[ ty ][ tx ] );
					nrPairs++;
				}
			}
		}

		if ( nrPairs > 0 )
		{
			matrix.divideBy( nrPairs );
		}

		return matrix;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public HaralickCoocMatrix< T > copy()
	{
		return new HaralickCoocMatrix< T >( nrGrayLevels, getDistance(), getOrientation() );
	}

	public int getNrGrayLevels()
	{
		return nrGrayLevels;
	}

	public int getDistance()
	{
		return distance;
	}

	public MatrixOrientation getOrientation()
	{
		return orientation;
	}
}
