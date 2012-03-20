/**
 *
 */
package tests.labeling;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.labeling.LabelingOutOfBoundsRandomAccessFactory;
import net.imglib2.labeling.LabelingType;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.outofbounds.OutOfBounds;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class LabelingOutOfBoundsTest
{

	private < T extends Comparable< T >> OutOfBounds< LabelingType< T >> makeOOB( final long[] dimensions, final long[][] coordinates, final List< T > values )
	{
		final ArrayImgFactory< LabelingType< T >> factory = new ArrayImgFactory< LabelingType< T >>();
		final NativeImgLabeling< T > labeling = new NativeImgLabeling< T >( dimensions, factory );
		labeling.setLinkedType( new LabelingType< T >( labeling ) );
		final LabelingOutOfBoundsRandomAccessFactory< T, Img< LabelingType< T >>> oobFactory = new LabelingOutOfBoundsRandomAccessFactory< T, Img< LabelingType< T >>>();
		final OutOfBounds< LabelingType< T >> result = oobFactory.create( labeling );
		final RandomAccess< LabelingType< T >> ra = labeling.randomAccess();
		for ( int i = 0; i < coordinates.length; i++ )
		{
			ra.setPosition( coordinates[ i ] );
			ra.get().setLabel( values.get( i ) );
		}
		return result;
	}

	/**
	 * Test method for
	 * {@link net.imglib2.labeling.LabelingOutOfBoundsRandomAccess#LabelingOutOfBoundsRandomAccess(net.imglib2.img.Img)}
	 * .
	 */
	@Test
	public void testLabelingOutOfBoundsRandomAccess()
	{
		final OutOfBounds< LabelingType< Integer >> result = makeOOB( new long[] { 10, 10 }, new long[][] { { 1, 2 } }, Arrays.asList( new Integer[] { 1 } ) );
		assertNotNull( result );
	}

	@Test
	public void testWithinBounds()
	{
		final OutOfBounds< LabelingType< Integer >> result = makeOOB( new long[] { 10, 10 }, new long[][] { { 1, 2 } }, Arrays.asList( new Integer[] { 1 } ) );
		result.setPosition( new long[] { 1, 2 } );
		assertFalse( result.isOutOfBounds() );
		assertEquals( result.get().getLabeling().size(), 1 );
		assertTrue( result.get().getLabeling().contains( 1 ) );
	}

	@Test
	public void testOutOfBounds()
	{
		final OutOfBounds< LabelingType< Integer >> result = makeOOB( new long[] { 10, 10 }, new long[][] { { 1, 2 } }, Arrays.asList( new Integer[] { 1 } ) );
		result.setPosition( new long[] { 11, 11 } );
		assertTrue( result.isOutOfBounds() );
		assertTrue( result.get().getLabeling().isEmpty() );
	}
}
