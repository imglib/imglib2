package net.imglib2.ops.operation;

import net.imglib2.RandomAccess;
import net.imglib2.type.numeric.RealType;
import net.imglib2.img.Img;

//TODO
//Figure out Imglib's preferred way to handle linked cursors. Can they work where span dimensionality differs?
//    (a 2D Image to run against a plane in a 5D Image)  Or do I avoid ROICurs and use some transformational view
//    where dims exactly match?

@SuppressWarnings("unchecked")
public class MultiImageIterator<T extends RealType<T>>  // don't want to implement full Cursor API
{
	private Img<T>[] images;
	private long[][] origins;
	private long[][] spans;
	private RegionCursor<T>[] regionCursors;
	
	// -----------------  public interface ------------------------------------------

	public MultiImageIterator(Img<T>[] images)
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

	public RegionCursor<T>[] getCursors()
	{
		return regionCursors;
	}

	/** call after subregions defined and before reset() or next() call. tests that all subregions defined are compatible. */
	void initialize()  // could call lazily in hasNext() or fwd() but a drag on performance
	{
		// make sure all specified regions are shape compatible : for now just test num elements in spans are same
		long totalSamples = numInSpan(spans[0]);
		for (int i = 1; i < spans.length; i++)
			if (numInSpan(spans[i]) != totalSamples)
				throw new IllegalArgumentException("incompatible span shapes");

		regionCursors = new RegionCursor[images.length];
		for (int i = 0; i < images.length; i++) {
			RandomAccess<T> accessor = images[i].randomAccess();
			regionCursors[i] = new RegionCursor<T>(accessor, origins[i], spans[i]);
		}

		resetAll();
	}
	
	public boolean isValid() {
		boolean firstValid = regionCursors[0].isValid();

		for (int i = 1; i < regionCursors.length; i++)
			if (firstValid != regionCursors[i].isValid())
				throw new IllegalArgumentException("linked cursors are out of sync");
		
		return firstValid;
	}
	
	public void next()
	{
		for (RegionCursor<T> cursor : regionCursors)
			cursor.next();
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
	
	// -----------------  private interface ------------------------------------------
	
	private long numInSpan(long[] span)  // TODO - call Imglib equivalent instead
	{
		long total = 1;
		for (long axisLen : span)
			total *= axisLen;
		return total;
	}
	
	private void resetAll() {
		for (RegionCursor<T> cursor : regionCursors)
			cursor.reset();
	}
}

