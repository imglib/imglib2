package mpicbg.imglib.algorithm.transformation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import mpicbg.imglib.algorithm.MultiThreaded;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.multithreading.Chunk;
import mpicbg.imglib.multithreading.SimpleMultiThreading;
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
    extends HoughTransform<T> implements MultiThreaded
{
    public static final int DEFAULT_THETA = 180;
    private final T threshold;
    private final double[] divSines, divCosines, rho, theta;
    private ArrayList<double[]> rtPeaks;
    private int numThreads;
    private double divMinRho;

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
    public HoughLineTransform(final Image<T> inputImage, final int nRho, final int nTheta)
    {
        super(inputImage, new int[]{nRho, nTheta});
        setNumThreads();
        
        theta = new double[nTheta];
        rho = new double[nRho];
        divSines = new double[nTheta];
        divCosines = new double[nTheta];
        rtPeaks = null;
        threshold = inputImage.createType();

        initConstants(Util.computeLength(inputImage.getDimensions()), nRho, nTheta);
    }

    private void initConstants(final double imDiag, final int nRho, final int nTheta)
    {
        //Theta by definition is in [0..pi].
        final double dTheta = Math.PI / nTheta;
        /*The furthest a point can be from the origin is the length calculated
         * from the dimensions of the Image.
         */
        final double dRho = 2 * imDiag / (double)nRho;
        final double minTheta = -Math.PI/2;        
        final double minRho = -imDiag;

        divMinRho = minRho / dRho;

        for (int t = 0; t < nTheta; ++t)
        {
            theta[t] = dTheta * (double)t + minTheta;
            divSines[t] = Math.sin(theta[t]) / dRho;
            divCosines[t] = Math.cos(theta[t]) / dRho;
        }

        for (int r = 0; r < nRho; ++r)
        {
            rho[r] = dRho * (double)r + minRho;
        }
  
    }
    
    public void setThreshold(final T inThreshold)
    {
        threshold.set(inThreshold);
    }

    @Override
    public boolean process()
    {
        final long sTime = System.currentTimeMillis();
        boolean success = false;
        final int[][] localVoteSpace = new int[getNumThreads()][numel];
        
        //(Lifted from ComputeMinMax)
        final AtomicInteger ai = new AtomicInteger(0);
        final Thread[] threads = SimpleMultiThreading.newThreads( getNumThreads() );
        final Vector<Chunk> threadChunks = 
            SimpleMultiThreading.divideIntoChunks(getImage().getNumPixels(), numThreads);

        for (int i = 0; i < getNumThreads(); ++i)
        {
            threads[i] = new Thread(new Runnable()
            {
                //Also lifted from ComputeMinMax
                public void run()
                {
                    final int id = ai.getAndIncrement();
                    final Chunk chunk = threadChunks.get(id);
                    
                    threadedProcess(chunk.getStartPosition(), chunk.getLoopSize(),
                            localVoteSpace[id]);
                }
            });
        }
        
        SimpleMultiThreading.startAndJoin(threads);
        
        for (int i = 0; i < numel; ++i)
        {
                getVoteSpace()[i] = 0;
                for (int j = 0; j < getNumThreads(); ++j)
                {
                    getVoteSpace()[i] += localVoteSpace[j][i];
                }
        }
        
        success = pickPeaks();

        super.pTime = System.currentTimeMillis() - sTime;
        return success;
    }

    protected void threadedProcess(final long startPos, final long loopSize,
            final int[] localVotes)
    {
        final LocalizableCursor<T> imageCursor = getImage().createLocalizableCursor();
        final int[] position = new int[getImage().getDimensions().length];
        
        imageCursor.fwd(startPos);
        
        for(long j = 0; j < loopSize; ++j)
        {
            double divFRho;
            int r;
            int[] voteLoc = new int[2];

            imageCursor.fwd();
            imageCursor.getPosition(position);

            if (imageCursor.getType().compareTo(threshold) > 0)
            {
                for (int t = 0; t < theta.length; ++t)
                {
                    divFRho = divCosines[t] * position[0] + divSines[t] * position[1];
                    r = (int)( (divFRho - divMinRho) + 0.5);
                    voteLoc[0] = r;
                    voteLoc[1] = t;

                    ++localVotes[locationToIndex(voteLoc)];
                }
            }
        }
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

    public int getNumThreads() {        
        return numThreads;
    }

    public void setNumThreads() {
        numThreads = Runtime.getRuntime().availableProcessors();
        
    }

    public void setNumThreads(int nt) {
        numThreads = nt;
    }

}
