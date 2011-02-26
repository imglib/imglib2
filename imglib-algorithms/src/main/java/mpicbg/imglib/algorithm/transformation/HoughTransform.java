package mpicbg.imglib.algorithm.transformation;

import java.util.ArrayList;
import java.util.Arrays;

import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.OutputAlgorithm;
import mpicbg.imglib.algorithm.math.PickImagePeaks;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.ImgFactory;
import mpicbg.imglib.img.ImgRandomAccess;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.type.NativeType;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.RealType;
/**
 * This abstract class provides some basic functionality for use with arbitrary Hough-like
 * transforms. 
 * 
 * @author lindsey
 *
 * @param <S> the data type used for storing votes, usually IntType, but possibly LongType or even DoubleType.
 * @param <T> the data type of the input image.
 */
public abstract class HoughTransform<S extends RealType<S> & NativeType<S>, T extends Type<T> & Comparable<T>>
implements OutputAlgorithm<Img<S>>, Benchmark
{
	protected long pTime;
	private String errorMsg;
	private final Img<T> image;
	private final Img<S> voteSpace;
	private ImgRandomAccess<S> voteCursor;
	private ArrayList<int[]> peaks;
	private final double[] peakExclusion;
	private final S one;
	
	/**
	 * Constructor for a HoughTransform using an ArrayContainerFactory to back the ImageFactory
	 * used to generate the voteSpace image.
	 * @param inputImage the image for the HoughTransform to operate over
	 * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
	 * directly into ImageFactory to create a voteSpace image.
	 * @param type the Type to use for generating the voteSpace image.
	 */
	protected HoughTransform(final Img<T> inputImage, final long[] voteSize, final S type)
	{
		this(inputImage, voteSize, new ArrayImgFactory<S>(), type);
	}
	
	/**
	 * Constructor for a HoughTransform with a specific ImageFactory.  Use this if you have
	 * something specific in mind as to how the vote data should be stored.
	 * @param inputImage the image for the HoughTransform to operate over
	 * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
	 * directly into ImageFactory to create a voteSpace image.
	 * @param voteFactory the ImageFactory used to generate the voteSpace image.
	 */
	protected HoughTransform(final Img<T> inputImage, final long[] voteSize, 
			final ImgFactory<S> voteFactory, final S type)
	{
		image = inputImage;
		voteCursor = null;
		pTime = 0;
		voteSpace = voteFactory.create(voteSize, type);		
		peaks = null;
		peakExclusion = new double[voteSize.length];
		one = type.createVariable();
		one.setOne();
		Arrays.fill(peakExclusion, 0);
		
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
				voteCursor = voteSpace.randomAccess();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.get().add(vote);
			
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
				voteCursor = voteSpace.randomAccess();
			}
			voteCursor.setPosition(loc);
			
			voteCursor.get().add(one);
			
			return true;
		}
		else
		{
			errorMsg = "Uninitialized Vote Space";
			return false;
		}		
	}
	
	/**
	 * Returns an ArrayList of int arrays, representing the positions in the vote space
	 * that correspond to peaks.
	 * @return an ArrayList of vote space peak locations.
	 */
	public ArrayList<int[]> getPeakList()
	{
		return peaks;
	}
		
	public boolean setExclusion(double[] newExclusion)
	{
		if (newExclusion.length >= peakExclusion.length)
		{
			System.arraycopy(newExclusion, 0, peakExclusion, 0, peakExclusion.length);
			return true;
		}
		return false;
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
		boolean ok;
		
		peakPicker.setSuppression(peakExclusion);
		ok = peakPicker.process();
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
	
	public Img<T> getImage()
	{
		return image;
	}
	
	@Override
	public Img<S> getResult()
	{
		return voteSpace;
	}

}
