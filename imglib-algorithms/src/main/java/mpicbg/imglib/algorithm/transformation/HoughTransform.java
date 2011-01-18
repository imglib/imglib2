package mpicbg.imglib.algorithm.transformation;

import java.util.ArrayList;
import java.util.Arrays;

import mpicbg.imglib.algorithm.Algorithm;
import mpicbg.imglib.algorithm.Benchmark;
import mpicbg.imglib.algorithm.math.PickImagePeaks;
import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.array.IntArray;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.integer.IntType;
/**
 * This abstract class provides some basic functionality for use with arbitrary Hough-like
 * transforms. 
 * 
 * @author lindsey
 *
 * @param <S> the data type used for storing votes, usually IntType, but possibly LongType or even DoubleType.
 * @param <T> the data type of the input image.
 */
public abstract class HoughTransform<T extends Type<T> & Comparable<T>>
    implements Algorithm, Benchmark
{
    protected long pTime;
    private String errorMsg;
    private final Image<T> image;
    private final  int[]  voteSpace;
    private ArrayList<int[]> peaks;
    private final double[] peakExclusion;
    private final int[] voteSize;
    private final int[] cumProd;
    protected Image<IntType> voteSpaceImage;

    /**
     * 
     * @param inputImage the image for the HoughTransform to operate over
     * @param voteSize and integer array indicating the size of the voteSpace.  This is passed
     * directly into ImageFactory to create a voteSpace image.
     * @param voteFactory the ImageFactory used to generate the voteSpace image.
     */
    protected HoughTransform(final Image<T> inputImage, final int[] voteSize)
    {
        int numel;

        pTime = 0;

        image = inputImage;		
        peaks = null;
        peakExclusion = new double[voteSize.length];
        cumProd = new int[voteSize.length];
        Arrays.fill(peakExclusion, 0);

        numel = voteSize[0];
        cumProd[0] = 1;
        for (int d = 1; d < voteSize.length; ++d)
        {
            cumProd[d] = cumProd[d - 1] * voteSize[d - 1];
            numel *= voteSize[d];		   
        }

        voteSpace = new int[numel];
        voteSpaceImage = null;
        this.voteSize = voteSize;
    }

    protected int locationToIndex(final int[] loc)
    {
        int index = 0;

        for (int i = 0; i < loc.length; ++i)
        {
            index += cumProd[i] * loc[i];
        }

        return index;
    }

    /**
     * Place a vote with a specific value.
     * @param loc the integer array indicating the location where the vote is to be placed in 
     * voteSpace.
     * @param vote the value of the vote
     */
    protected void  placeVote(final int[] loc, int vote)
    {
        voteSpace[locationToIndex(loc)] += vote;
    }

    /**
     * Place a vote of value 1.
     * @param loc the integer array indicating the location where the vote is to be placed in 
     * voteSpace.
     */
    protected void placeVote(final int[] loc)
    {
        voteSpace[locationToIndex(loc)]++;
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
        final PickImagePeaks<IntType> peakPicker =
            new PickImagePeaks<IntType>(getVoteSpaceImage());
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

    public Image<T> getImage()
    {
        return image;
    }

    public Image<IntType> getVoteSpaceImage()
    {
        if (voteSpaceImage == null)
        {
            ImageFactory<IntType> factory = new ImageFactory<IntType>(
                    new IntType(), new ArrayContainerFactory());
            LocalizableCursor<IntType> cursor;
            final int[] loc = new int[voteSize.length];

            voteSpaceImage = factory.createImage(voteSize);
            cursor = voteSpaceImage.createLocalizableCursor();

            while(cursor.hasNext())
            {
                cursor.fwd();
                cursor.getPosition(loc);
                cursor.getType().set(voteSpace[locationToIndex(loc)]);
            }
        }

        return voteSpaceImage;
    }

    protected int[] getVoteSize()
    {
        return voteSize;
    }

}
