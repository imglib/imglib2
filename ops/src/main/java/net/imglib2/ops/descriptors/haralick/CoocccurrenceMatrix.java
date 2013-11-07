package net.imglib2.ops.descriptors.haralick;

import java.util.Arrays;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.ops.data.CooccurrenceMatrix;
import net.imglib2.ops.descriptors.AbstractModule;
import net.imglib2.ops.descriptors.Module;
import net.imglib2.ops.descriptors.ModuleInput;
import net.imglib2.ops.descriptors.firstorder.Max;
import net.imglib2.ops.descriptors.firstorder.Min;
import net.imglib2.ops.descriptors.haralick.helpers.CoocParameter;
import net.imglib2.type.numeric.RealType;

public class CoocccurrenceMatrix extends AbstractModule< CooccurrenceMatrix >
{
	@ModuleInput
	CoocParameter parameter;

	@ModuleInput
	IterableInterval< ? extends RealType< ? >> ii;

	@ModuleInput
	Min min;

	@ModuleInput
	Max max;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected CooccurrenceMatrix recompute()
	{

		final Cursor< ? extends RealType< ? > > cursor = ii.cursor();

		final double localMin = this.min.value();

		final double localMax = this.max.value();

		int[][] pixels = new int[ ( int ) ii.dimension( 0 ) ][ ( int ) ii.dimension( 1 ) ];

		for ( int i = 0; i < pixels.length; i++ )
		{
			Arrays.fill( pixels[ i ], Integer.MAX_VALUE );
		}

		CooccurrenceMatrix matrix = new CooccurrenceMatrix( parameter.nrGrayLevels );

		while ( cursor.hasNext() )
		{
			cursor.fwd();
			pixels[ cursor.getIntPosition( 1 ) - ( int ) ii.min( 1 ) ][ cursor.getIntPosition( 0 ) - ( int ) ii.min( 0 ) ] = ( int ) ( ( ( cursor.get().getRealDouble() - localMin ) / ( localMax - localMin ) ) * (parameter.nrGrayLevels - 1) );

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
				int sx = x + parameter.orientation.dx * parameter.distance;
				int sy = y + parameter.orientation.dy * parameter.distance;
				// get third pixel
				int tx = x - parameter.orientation.dx * parameter.distance;
				int ty = y - parameter.orientation.dy * parameter.distance;

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

	@Override
	public boolean isEquivalentModule( Module< ? > output )
	{
		// Haralick is superclass of output.getClass()
		return CoocccurrenceMatrix.class.isAssignableFrom( output.getClass() );
	}

	@Override
	public boolean hasCompatibleOutput( Class< ? > clazz )
	{
		return clazz.isAssignableFrom( CoocccurrenceMatrix.class );
	}
}
