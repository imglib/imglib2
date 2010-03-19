package mpicbg.imglib.algorithm.transformation;

import java.util.ArrayList;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.PickImagePeaks;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.ComparableType;
import mpicbg.imglib.type.NumericType;
/**
 * This abstract class provides some basic functionality for use with arbitrary Hough-like
 * transforms. 
 * 
 * @author lindsey
 *
 * @param <S> the data type used for storing votes, usually IntType, but possibly LongType or even DoubleType.
 * @param <T> the data type of the input image.
 */
public abstract class HoughTransform<S extends NumericType<S>, T extends ComparableType<T>>
implements OutputAlgorithm<S>, Benchmark
{
	protected long pTime;
	private String errorMsg;
	private final Image<T> image;
	private final Image<S> voteSpace;
	private LocalizableByDimCursor<S> voteCursor;
	private ArrayList<int[]> peaks;
	
	/**
	 * Constructor for a HoughTransform using an ArrayContainerFactory to back the ImageFactory
	 * used to generate the voteSpace image.
	 * @param inputImage the image for the HoughTransform to operate over
	 * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
	 * directly into ImageFactory to create a voteSpace image.
	 * @param type the Type to use for generating the voteSpace image.
	 */
	protected HoughTransform(final Image<T> inputImage, final int[] voteSize, final S type)
	{
		this(inputImage, voteSize, new ImageFactory<S>(type, new ArrayContainerFactory()));
	}
	
	/**
	 * Constructor for a HoughTransform with a specific ImageFactory.  Use this if you have
	 * something specific in mind as to how the vote data should be stored.
	 * @param inputImage the image for the HoughTransform to operate over
	 * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
	 * directly into ImageFactory to create a voteSpace image.
	 * @param voteFactory the ImageFactory used to generate the voteSpace image.
	 */
	protected HoughTransform(final Image<T> inputImage, final int[] voteSize, 
			final ImageFactory<S> voteFactory)
	{
		image = inputImage;
		voteCursor = null;
		pTime = 0;
		voteSpace = voteFactory.createImage(voteSize);		
		peaks = null;
		
	}
	
	/**
	 * Place a vote with a specific value.
	 * @param loc the integer array indicating the location where the vote is to be placed in 
	 * voteSpace.
	 * @param vote the value of the vote
	 * @return whether the vote was successful.  This here particular method should always return
	 * true.
	 */
	protected boolean placeVote(final int[] loc, final S vote)
	{
			if (voteCursor == null)
			{
				voteCursor = voteSpace.createLocalizableByDimCursor();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.getType().add(vote);
			
			return true;
	}
	
	/**
	 * Place a vote of value 1.
	 * @param loc the integer array indicating the location where the vote is to be placed in 
	 * voteSpace.
	 * @return whether the vote was successful.  This here particular method should always return
	 * true.
	 */
	protected boolean placeVote(final int[] loc)
	{
		if (voteSpace != null)
		{
			if (voteCursor == null)
			{
				voteCursor = voteSpace.createLocalizableByDimCursor();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.getType().inc();
			
			return true;
		}
		else
		{
			errorMsg = "Uninitialized Vote Space";
			return false;
		}		
	}
	
	public ArrayList<int[]> getPeakList()
	{
		return peaks;
	}
		
	protected void setErrorMsg(final String msg)
	{
		errorMsg = msg;
	}
	
	/**
	 * Pick vote space peaks with a {@link PickImagePeaks}.
	 * @return whether peak picking was successful
	 */
	protected boolean pickPeaks()
	{
		final PickImagePeaks<S> peakPicker = new PickImagePeaks<S>(voteSpace);
		boolean ok = peakPicker.process();
		if (ok)
		{
			peaks = peakPicker.getPeakList();
			return true;
		}
		else
		{
			return false;
		}
	}
	
	@Override
	public boolean checkInput() {
		if (voteSpace == null)
		{
			return false;
		}
		else
		{
			return true;
		}
	}

	@Override
	public String getErrorMessage() {
		return errorMsg;
	}
	
	@Override
	public long getProcessingTime() {		
		return pTime;
	}
	
	public Image<T> getImage()
	{
		return image;
	}
	
	@Override
	public Image<S> getResult()
	{
		return voteSpace;
	}

}
