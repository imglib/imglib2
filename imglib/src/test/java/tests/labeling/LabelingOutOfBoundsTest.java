/**
 * 
 */
package tests.labeling;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import mpicbg.imglib.RandomAccess;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.labeling.Labeling;
import mpicbg.imglib.labeling.LabelingOutOfBoundsRandomAccessFactory;
import mpicbg.imglib.labeling.LabelingType;
import mpicbg.imglib.labeling.NativeImgLabeling;
import mpicbg.imglib.outofbounds.OutOfBounds;

import org.junit.Test;

/**
 * @author leek
 *
 */
public class LabelingOutOfBoundsTest {

	private <T extends Comparable<T>> OutOfBounds<LabelingType<T>> makeOOB(
			long [] dimensions, 
			long [][] coordinates,
			List<T> values) {
		ArrayImgFactory<LabelingType<T>> factory =  new ArrayImgFactory<LabelingType<T>>();
		NativeImgLabeling<T> labeling = new NativeImgLabeling<T>(dimensions, factory);
		labeling.setLinkedType(new LabelingType<T>(labeling));
		LabelingOutOfBoundsRandomAccessFactory<T, Img<LabelingType<T>>> oobFactory =
			new LabelingOutOfBoundsRandomAccessFactory<T, Img<LabelingType<T>>>();
		OutOfBounds<LabelingType<T>> result = oobFactory.create(labeling);
		RandomAccess<LabelingType<T>> ra = labeling.randomAccess();
		for (int i = 0; i < coordinates.length; i++) {
			ra.setPosition(coordinates[i]);
			ra.get().setLabel(values.get(i));
		}
		return result;
	}
	
	/**
	 * Test method for {@link mpicbg.imglib.labeling.LabelingOutOfBoundsRandomAccess#LabelingOutOfBoundsRandomAccess(mpicbg.imglib.img.Img)}.
	 */
	@Test
	public void testLabelingOutOfBoundsRandomAccess() {
		OutOfBounds<LabelingType<Integer>> result = makeOOB(
				new long[] {10,10}, 
				new long[][] { {1,2}},
				Arrays.asList(new Integer [] { 1 }));
		assertNotNull(result);
	}
	
	@Test
	public void testWithinBounds() {
		OutOfBounds<LabelingType<Integer>> result = makeOOB(
				new long[] {10,10}, 
				new long[][] { {1,2}},
				Arrays.asList(new Integer [] { 1 }));
		result.setPosition(new long[] { 1,2});
		assertFalse(result.isOutOfBounds());
		assertEquals(result.get().getLabeling().size(), 1);
		assertTrue(result.get().getLabeling().contains(1));
	}
	@Test
	public void testOutOfBounds() {
		OutOfBounds<LabelingType<Integer>> result = makeOOB(
				new long[] {10,10}, 
				new long[][] { {1,2}},
				Arrays.asList(new Integer [] { 1 }));
		result.setPosition(new long[] { 11,11 });
		assertTrue(result.isOutOfBounds());
		assertTrue(result.get().getLabeling().isEmpty());
	}
}
