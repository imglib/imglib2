package net.imglib2.ops.operation;

import net.imglib2.Cursor;
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
	private Cursor<T>[] cursors;
	private RegionCursor[] regionCursors;
	
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

	public void setRegion(int i, long[] origin, long[] span)
	{
		origins[i] = origin;
		spans[i] = span;
	}
	
	public Cursor<T>[] getSubcursors()
	{
		return cursors;
	}

	/** call after subregions defined and before first hasNext() or fwd() call. tests that all subregions defined are compatible. */
	void initialize()  // could call lazily in hasNext() or fwd() but a drag on performance
	{
		// make sure all specified regions are shape compatible : for now just test num elements in spans are same
		long totalSamples = numInSpan(spans[0]);
		for (int i = 1; i < spans.length; i++)
			if (numInSpan(spans[i]) != totalSamples)
				throw new IllegalArgumentException("incompatible span shapes");

		cursors = new Cursor[images.length];
		for (int i = 0; i < images.length; i++) {
			cursors[i] = images[i].cursor();
		}
		
		regionCursors = new RegionCursor[images.length];
		for (int i = 0; i < images.length; i++)
			regionCursors[i] = new RegionCursor<T>(cursors[i], origins[i], spans[i]);
	}
	
	public boolean hasNext()
	{
		boolean hasNext = regionCursors[0].hasNext();
		
		for (int i = 1; i < regionCursors.length; i++)
			if (hasNext != regionCursors[i].hasNext())
				throw new IllegalArgumentException("linked cursors are out of sync");
		
		return hasNext;
	}
	
	public void fwd()
	{
		System.out.println("Cursors moving forward");
		long[] pos = new long[images[0].numDimensions()];
		for (int i = 0; i < regionCursors.length; i++) {
			regionCursors[i].fwd();
			regionCursors[i].getPosition(pos);
			System.out.println(" cursor "+i+" now at position "+positionToString(pos));
		}
	}
	
	// -----------------  private interface ------------------------------------------
	
	private String positionToString(long[] position) {
		String posStr = "[";
		for (int i = 0; i < position.length; i++) {
			if (i > 0)
				posStr += ",";
			posStr += position[i];
		}
		posStr += "]";
		return posStr;
	}

	private long numInSpan(long[] span)  // TODO - call Imglib equivalent instead
	{
		long total = 1;
		for (long axisLen : span)
			total *= axisLen;
		return total;
	}
	
}

