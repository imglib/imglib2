package net.imglib2.ops.operation;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.img.Axis;
import net.imglib2.img.ImgPlus;

//TODO
//Figure out Imglib's preferred way to handle linked cursors. Can they work
//  where span dimensionality differs? (a 2D Image to run against a plane in a
//  5D Image)  Or do I avoid ROICursor and use some transformational view
//  where dims exactly match?

// Note - don't want to implement full Cursor API

public class MultiImageIterator
{
	private ImgPlus<? extends RealType<?>>[] images;
	private long[][] origins;
	private long[][] spans;
	private RegionIterator[] regionIterators;
	
	// -----------------  public interface --------------------------------------

	public MultiImageIterator(ImgPlus<? extends RealType<?>>[] images)
	{
		this.images = images;
		int totalImages = images.length;
		origins = new long[totalImages][];
		spans = new long[totalImages][];
		for (int i = 0; i < totalImages; i++)
		{
			origins[i] = new long[images[i].numDimensions()];
			spans[i] = new long[images[i].numDimensions()];
			images[i].dimensions(spans[i]);
		}
	}

	public RegionIterator[] getIterators()
	{
		return regionIterators;
	}

  // could call lazily next method in hasNext() or fwd() but performance drag
	
	/** call after subregions defined and before reset() or next() call. tests
	 *  that all subregions defined are compatible. */
	public void initialize()
	{
		testAllSpansCompatible();

		regionIterators = new RegionIterator[images.length];
		for (int i = 0; i < images.length; i++) {
			RandomAccess<? extends RealType<?>> accessor = images[i].randomAccess();
			regionIterators[i] =
				new RegionIterator(accessor, origins[i], spans[i]);
		}

		resetAll();
	}
	
	public boolean hasNext() {
		boolean firstHasNext = regionIterators[0].hasNext();

		for (int i = 1; i < regionIterators.length; i++)
			if (firstHasNext != regionIterators[i].hasNext())
				throw new IllegalArgumentException("linked cursors are out of sync");
		
		return firstHasNext;
	}
	
	public void next()
	{
		for (RegionIterator iterator : regionIterators)
			iterator.next();
	}
	
	public void reset()
	{
		resetAll();
	}
	
	public void setRegion(int i, long[] origin, long[] span)
	{
		origins[i] = origin;
		spans[i] = span;
	}
	
	// -----------------  private interface -------------------------------------

	/** resets each RegionIterator */
	private void resetAll() {
		for (RegionIterator iterator : regionIterators)
			iterator.reset();
	}

	/** tests that all given spans are shape compatible (and thus iteration
	 * order compatible) */
	private void testAllSpansCompatible() {
		// all span values != 1 must be present and same size in all images
		// any span value == 1 must either equal 1 in other images or not present

		ImgPlus<?> firstImgPlus = images[0];
		
		for (int i = 1; i < images.length; i++) {
			testSpansCompatible(firstImgPlus, spans[0], images[i], spans[i]);
		}
	}
	
	/** checks that spans of two different images are iteration order
	 *  compatible. each subtest checks one direction of compatibility
	 *  and thus has to be called twice with parameters reversed to be
	 *  fully thorough */
	private void testSpansCompatible(ImgPlus<?> img1, long[] span1,
		ImgPlus<?> img2, long[] span2)
	{
		testAxisSizesCompatible(img1, span1, img2, span2);
		testAxisSizesCompatible(img2, span2, img1, span1);
		testAxisOrdersCompatible(img1, img2);
		testAxisOrdersCompatible(img2, img1);
	}
	
	/** imagine you have a 2d XY image and a 5d XCZYT image. Note that since
	 * the X & Y axes are in the same relative order these two images can be
	 * considered shape compatible if the ranges of C, Z, & T are all 1. The
	 * RegionIterator will work as expected with such data. The following
	 * method checks that the given span sizes two images are comparable. I.e.
	 * either a dimension == 1 (or is not present) or the dimension matches in
	 * size between the two spans.
	 */
	private void testAxisSizesCompatible(ImgPlus<?> img1, long[] span1,
		ImgPlus<?> img2, long[] span2)
	{
		int imgOneNumDims = img1.numDimensions();
		Axis[] axes = new Axis[imgOneNumDims];
		img1.axes(axes);
		for (int i = 0; i < imgOneNumDims; i++) {
			Axis axis = axes[i];
			int axisIndex = img2.getAxisIndex(axis);
			long dimSize = span1[i];
			if (dimSize == 1) {
				if (axisIndex >= 0)
					if (span2[axisIndex] != 1)
						throw new IllegalArgumentException(
							"span issue: expecting a dimension of size 1 but have size " +
							img2.dimension(axisIndex));
			}
			else { // dimSize != 1
				if (axisIndex < 0)
					throw new IllegalArgumentException(
						"span issue: expecting image to have "+axis+" axis");
				if (span2[axisIndex] != dimSize)
					throw new IllegalArgumentException(
						"span issue: differing sizes detected for "+axis+" axis");
			}
		}
	}

	/** imagine you have a 2d XY image and a 5d XCZYT image. Note that since
	 * the X & Y axes are in the same relative order these two images can be
	 * considered shape compatible if the ranges of C, Z, & T are all 1. The
	 * RegionIterator will work as expected with such data. The following
	 * method checks that the relative order matches for axes common to two
	 * images.
	 */
	private void testAxisOrdersCompatible(ImgPlus<?> img1, ImgPlus<?> img2)
	{
		Axis[] axes = new Axis[img1.numDimensions()];
		img1.axes(axes);
		int lastAxisIndex = -1;
		for (int i = 0; i < axes.length; i++) {
			Axis axis = axes[i];
			int axisIndex = img2.getAxisIndex(axis);
			if (axisIndex > 0) {
				if (axisIndex <= lastAxisIndex)
					throw new IllegalArgumentException(
						"span issue: axes not in increasing order");
				lastAxisIndex = axisIndex;
			}
		}
	}
}
