package net.imglib2.ops.example.rev3.function;

import java.util.ArrayList;

import net.imglib2.image.Image;
import net.imglib2.type.numeric.RealType;

// NOTES & TODO

// This is the last missing part to make rev 3 code do everything that rev 2 code could do plus more.

// Realize we could just compose any functions rather than image functions. that would be more
// general but I am doing a simple implementation.

// The idea here is that let's say I have 4 images that are each one plane and I want to average their values at each XY
// location. The plan is to compose these 4 2-D images into a single virtual 3-D "image" with Z == 4. Then I can use a
// AverageFunction on the XY locations across Z by passing correct axis deltas into the AverageFunction.

// note that as defined this composition allows one to treat multiple subregions of a plane in one image for example as
// a higher dimensional "image".

/** ComposedImageFunction
 * Composes a number of N dimensional Images into a N+1 dimension function
 */
public final class ComposedImageFunction implements IntegerIndexedScalarFunction
{
	private ArrayList<SubImageInfo> subImages;
	private int[] subImageDimensions;
	private int[] subImagePosition;
	private int subImageSpanSize;
	
	private class SubImageInfo
	{
		int[] origin;
		ImageFunction function;
	}

	/** the only constructor. must use addImageRegion() to an instance before one can get values out of it via evaluate(). */
	public ComposedImageFunction()
	{
	}

	/** adds a subregion of an image to this COmposedImageFunction. The span of the region must match exactly the span of all previously added
	 * image subregions. the origin can vary (allowing multiple regions in a single image to be treated as separate planes in the composed image). */
	public void addImageRegion(Image<? extends RealType<?>> image, int[] origin, int[] span)
	{
		if (subImages == null)
		{
			subImages = new ArrayList<SubImageInfo>();
			subImageDimensions = span.clone();
			subImageSpanSize = span.length;
			subImagePosition = new int[subImageSpanSize];
		}

		if (span.length != subImageSpanSize)
			throw new IllegalArgumentException("span of new image subregion is not of the same dimensionality as existing images");

		for (int i = 0; i < subImageSpanSize; i++)
			if (span[i] != subImageDimensions[i])
				throw new IllegalArgumentException("span of new image subregion is not of the same dimension as existing image subregions");
				
		SubImageInfo info = new SubImageInfo();
		info.origin = origin;
		info.function = new ImageFunction(image);
		
		subImages.add(info);
	}
	
	@Override
	public double evaluate(int[] position)
	{
		SubImageInfo info = subImages.get(position[subImageSpanSize]);
		for (int i = 0; i < subImageSpanSize; i++)
			subImagePosition[i] = info.origin[i] + position[i];
		return info.function.evaluate(subImagePosition);
	}
	
}
