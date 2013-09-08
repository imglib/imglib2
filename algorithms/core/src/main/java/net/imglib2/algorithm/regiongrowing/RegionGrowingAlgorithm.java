package net.imglib2.algorithm.regiongrowing;

import java.util.Queue;

import net.imglib2.algorithm.OutputAlgorithm;
import net.imglib2.labeling.Labeling;

/**
 * Public interface for Region-growing algorithms.
 * <p>
 * This interface only provides methods to instantiate the region growing
 * results ({@link #initializeLabeling()}), to decide whether or not to grow a
 * region into a pixel ({@link #includeInRegion(long[], long[], Comparable)},
 * and to hook on the growing process ( {@link #finishedLabel(Comparable)} &
 * {@link #finishedGrowStep(Queue, Comparable)}).
 * 
 * @author Jean-Yves Tinevez - Sept 2013
 * 
 * @param <L>
 *            the labeling type. Must extends {@link Comparable}.
 */
public interface RegionGrowingAlgorithm< L extends Comparable< L >> extends OutputAlgorithm< Labeling< L > >
{

	/**
	 * Creates the {@link Labeling} that will contain the grown region. The
	 * returned labeling should have the same dimensions that of the source
	 * image.
	 * <p>
	 * Called before the growing process is started.
	 * 
	 * @return a new {@link Labeling} instance.
	 */
	public Labeling< L > initializeLabeling();

	/**
	 * Returns whether a candidate position can be included in the current
	 * region.
	 * 
	 * @param parentPixel
	 *            the position of the pixel, already part of the current region,
	 *            whose current candidate pixel is a child.
	 * @param candidatePixel
	 *            the position of the candidate pixel to inspect.
	 * @param label
	 *            the label of the current region.
	 * @return <code>true</code> if the candidate pixel can be added to the
	 *         current region, <code>false</code> otherwise.
	 */
	public boolean includeInRegion( long[] parentPixel, long[] candidatePixel, L label );

	/**
	 * Called after a round of growing process on a single region.
	 * 
	 * @param childPixels
	 *            the pixel positions that have been added to the current region
	 *            during this growth step.
	 * @param label
	 *            the label of the current region.
	 */
	public void finishedGrowStep( Queue< long[] > childPixels, L label );

	/**
	 * Called after a region has finished growing.
	 * 
	 * @param label
	 *            the label of the region that finished growing.
	 */
	public void finishedLabel( L label );
}
