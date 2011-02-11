package imglib.ops.example.rev3.function;

import java.util.ArrayList;

import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.RealType;

// NOTE & TODO
//   I am doing the most brain dead, general, and slow composed image position calcs in determineSubImageVariables().
//   Speed up by only allowing fixed sized n-dim chunks. Or implement some indexing structure for fast lookup.
//   This is the last missing part to make rev 3 code do everything that rev 2 code could do plus more.

// UNWORKING AND NOT FINISHED. REALIZE could just compose any functions rather than image functions. that would be more
// general but I am doing a quick and dirty impl.

// The idea here is that let's say I have 4 images that are each one plane and I want to average their values at each XY
// location. The plan is to compose these 4 2-D images into a single virtual 3-D "image" with Z == 4. Then I can use a
// AverageFunction on the XY locations across Z by passing correct axis deltas into the AverageFunction.

// note that as defined this composition allows one to treat multiple subregions of a plane in one image for example as
// a bigger dimensional "image".

/** ComposedImageFunction
 * Composes a number of N dimensional Images into a N+1 dimension function
 *   huh - stack XY planes into a 3d dataset. but why not stack two 3d volumes into a bigger 3d volume.
 *   as implemented it does the latter. Think about what is best.
 */
public class ComposedImageFunction<T extends RealType<T>> implements IntegralScalarFunction<T>
{
	private int axisOfComposition;
	private ArrayList<LocalizableByDimCursor<T>> subImageCursors;
	private ArrayList<int[]> subImageOrigins;
	private ArrayList<int[]> subImageSpans;
	private ArrayList<int[]> subImagePositions;
	private int localizedSubImageNumber;
	private int[] localizedPosition;
	
	public ComposedImageFunction(int axis)
	{
		axisOfComposition = axis;
		subImageCursors = new ArrayList<LocalizableByDimCursor<T>>();
		subImageOrigins = new ArrayList<int[]>();
		subImageSpans = new ArrayList<int[]>();
		subImagePositions = new ArrayList<int[]>();
	}
	
	public void addSubregionOfImage(Image<T> image, int[] origin, int[] span)
	{
		if (subImageCursors.size() == 0)
		{
			subImageOrigins.add(origin);
			subImageSpans.add(span);
			subImagePositions.add(new int[image.getNumDimensions()]);
			return;
		}

		Image<T> firstImage = subImageCursors.get(0).getImage();
		
		if (image.getNumDimensions() != firstImage.getNumDimensions())
			throw new IllegalArgumentException("incompatibly shaped images cannot be glued together: num dimensions different");
		
		// TODO -- add the subregion info to instance vars so we can access it
	}
	
	@Override
	public T createVariable()
	{
		if (subImageCursors.size() == 0)
			return null;
		
		return subImageCursors.get(0).getType().createVariable();
	}

	@Override
	public void evaluate(int[] position, T output)
	{
		if (subImageCursors.size() == 0)
		{
			output.setZero();    // TODO - is this what we really want to do???? or NaN????
			return;
		}
		
		determineSubImageVariables(position);
		
		LocalizableByDimCursor<T> cursor = subImageCursors.get(localizedSubImageNumber);
		
		cursor.setPosition(localizedPosition);

		double value = cursor.getType().getRealDouble();
		
		output.setReal(value);
	}

	// TODO - not completed
	private void determineSubImageVariables(int[] position)
	{
		int sublistNum = 0;
		int totalSublists = subImageCursors.size();
		int totalAxisPlanesSoFar = 0;
		int prevAxisPlanesSoFar = 0;
		while (sublistNum < totalSublists)
		{
			int[] span = subImageSpans.get(sublistNum);
			totalAxisPlanesSoFar += span[axisOfComposition];
			if (position[axisOfComposition] < totalAxisPlanesSoFar)
			{
				localizedSubImageNumber = sublistNum;
				int[] localOrigin = subImageOrigins.get(sublistNum);
				localizedPosition = subImagePositions.get(sublistNum);
				for (int i = 0; i < position.length; i++)
					localizedPosition[i] = localOrigin[i] + position[i];
				localizedPosition[axisOfComposition] = localOrigin[axisOfComposition] + position[axisOfComposition] - prevAxisPlanesSoFar;
				return;
			}
			prevAxisPlanesSoFar = totalAxisPlanesSoFar;
			sublistNum++;
		}
		throw new IllegalArgumentException("position out of range");
	}
}
