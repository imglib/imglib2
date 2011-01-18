package mpicbg.imglib.algorithm.transformation;

import java.util.ArrayList;
import java.util.Arrays;

import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.Type;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.util.Util;


/**
 * A class that extends {@link HoughTransform} to handle Hough Line voting over an edge map.
 * This implementation uses a threshold to determine whether a pixel at a certain point is 
 * an edge or not.  Comparison is strictly-greater-than.  This implementation is fairly dumb
 * in that it does not take gradients into account.  The threshold used is the default value
 * returned by calling the constructor for the {@link Type} of the input {@link Image}. 
 * 
 * Vote space here has two dimensions: rho and theta.  Theta is measured in radians
 * [-pi/2 pi/2), rho is measured in [-rhoMax, rhoMax).
 * 
 * Lines are modeled as
 * 
 * l(t) = | x | = rho * |  cos(theta) | + t * | sin(theta) |
 *        | y |         | -sin(theta) |       | cos(theta) |
 * 
 * In other words, rho represents the signed minimum distance from the image origin to the line,
 * and theta indicates the angle between the row-axis and the minimum offset vector.
 * 
 * For a given point, then, votes are placed along the curve
 * 
 * rho = y * sin(theta) - x * cos(theta)
 * @Override 
 */
public class HoughLineTransform <T extends Type<T> & Comparable<T>>
    extends HoughTransform<T>
{
    public static final int DEFAULT_THETA = 180;
    private final double dTheta;
    private final double dRho;
    private final T threshold;
    private final int nRho;
    private final int nTheta;
    private final double[] rho;
    private final double[] theta;
    private final int[][] voteMap;
    private ArrayList<double[]> rtPeaks;

    /**
     * Calculates a default number of rho bins, which corresponds to a resolution of one pixel.
     * @param inputImage the {@link Image} in question.
     * @return default number of rho bins.
     */
    public static int defaultRho(final Image<?> inputImage)
    {
        return (int)(2 * Util.computeLength(inputImage.getDimensions()));
    }

    /**
     * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
     * a specific {@link Type} of vote space.
     * Defaults are used for rho- and theta-resolution.
     * @param inputImage the {@link Image} to operate against.
     * @param type the {@link Type} for the vote space.
     */
    public HoughLineTransform(final Image<T> inputImage)
    {
        this(inputImage, DEFAULT_THETA);
    }

    /**
     * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
     * a specific {@link Type} of vote space and theta-resolution.
     * Rho-resolution is set to the default.
     * @param inputImage the {@link Image} to operate against.
     * @param theta the number of bins for theta-resolution.
     * @param type the {@link Type} for the vote space.
     */
    public HoughLineTransform(final Image<T> inputImage, final int theta)
    {
        this(inputImage, defaultRho(inputImage), theta);
    }

    /**
     * Create a {@link HoughLineTransform} to operate against a given {@link Image}, with
     * a specific {@link Type} of vote space and rho- and theta-resolution.
     * @param inputImage the {@link Image} to operate against.
     * @param theta the number of bins for theta resolution.
     * @param type the {@link Type} for the vote space.
     */
    public HoughLineTransform(final Image<T> inputImage, final int inNRho, final int inNTheta)
    {
        super(inputImage, new int[]{inNRho, inNTheta});
        //Theta by definition is in [0..pi].
        dTheta = (float)Math.PI / (float)inNTheta;
        /*The furthest a point can be from the origin is the length calculated
         * from the dimensions of the Image.
         */
        dRho = 2 * Util.computeLength(inputImage.getDimensions()) / (float)inNRho;
        threshold = inputImage.createType();
        nRho = inNRho;
        nTheta = inNTheta;
        theta = new double[inNTheta];
        rho = new double[inNRho];
        rtPeaks = null;

        voteMap = new int[getVoteSize()[0]][getVoteSize()[1]];

        for (int[] array : voteMap)
        {
            Arrays.fill(array, 0);
        }
    }

    public void setThreshold(final T inThreshold)
    {
        threshold.set(inThreshold);
    }

    @Override
    public boolean process()
    {
        final LocalizableCursor<T> imageCursor = getImage().createLocalizableCursor();
        final int[] position = new int[getImage().getDimensions().length];
        final double[] cosines = new double[theta.length];
        final double[] sines = new double[theta.length];
        final double minTheta = -Math.PI/2;
        final double minRho = -Util.computeLength(getImage().getDimensions());
        final long sTime = System.currentTimeMillis();
        boolean success;

        for (int t = 0; t < nTheta; ++t)
        {
            theta[t] = dTheta * (double)t + minTheta;
            sines[t] = Math.sin(theta[t]);
            cosines[t] = Math.cos(theta[t]);
        }

        for (int r = 0; r < nRho; ++r)
        {
            rho[r] = dRho * (double)r + minRho;
        }

        while (imageCursor.hasNext())
        {
            double fRho;
            int r;
            int[] voteLoc = new int[2];

            imageCursor.fwd();
            imageCursor.getPosition(position);

            if (imageCursor.getType().compareTo(threshold) > 0)
            {
                for (int t = 0; t < nTheta; ++t)
                {
                    long mTime = System.currentTimeMillis();
                    fRho = cosines[t] * position[0] + sines[t] * position[1];
                    r = (int)( ((fRho - minRho)/ dRho) + 0.5);
                    voteLoc[0] = r;
                    voteLoc[1] = t;

                    super.placeVote(voteLoc);
                }
            }
        }

        success = super.pickPeaks();

        super.pTime = System.currentTimeMillis() - sTime;
        return success;
    }

    public ArrayList<double[]> getTranslatedPeakList()
    {
        if (rtPeaks == null)
        {
            ArrayList<int[]> peaks = getPeakList();
            rtPeaks = new ArrayList<double[]>(peaks.size());

            for (int[] irt : peaks)
            {
                double[] rt = new double[2];
                rt[0] = rho[irt[0]];
                rt[1] = theta[irt[1]];
                rtPeaks.add(rt);
            }
        }

        return rtPeaks;
    }

}
