package net.imglib2.algorithm.regiongrowing;

import java.util.Map;
import java.util.Queue;

import net.imglib2.RandomAccess;
import net.imglib2.algorithm.regiongrowing.RegionGrowingTools.GrowingMode;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.numeric.integer.IntType;

/**
 * The simplest region-growing algorithm, that grows a region only on neighbor
 * pixels with an intensity higher than a specified threshold.
 * 
 * @author Jean-Yves Tinevez, Sept 2013
 * 
 * @param <T>
 *            the type of the source image. Must extend {@link Comparable}.
 * @param <L>
 *            the type for the region labels. Must extend {@link Comparable}.
 */
public class ThresholdRegionGrowing< T extends Comparable< T >, L extends Comparable< L >> extends AbstractRegionGrowingAlgorithm< L >
{

	private final T threshold;

	private final RandomAccess< T > ra;

	private final NativeImgLabeling< L, IntType > target;

	/**
	 * Instantiate a new threshold base region algorithm.
	 * 
	 * @param img
	 *            the source image to operate on.
	 * @param threshold
	 *            the threshold value below which pixels will be rejected from
	 *            any region.
	 * @param seedLabels
	 *            the seed point positions and their label, as a {@link Map}.
	 * @param growingMode
	 *            the growing mode.
	 * @param structuringElement
	 *            the structuring element for the growing process.
	 */
	public ThresholdRegionGrowing( final RandomAccess< T > ra, final T threshold, final Map< long[], L > seedLabels, final GrowingMode growingMode, final long[][] structuringElement, final NativeImgLabeling< L, IntType > target )
	{
		super( seedLabels, growingMode, structuringElement, false );
		this.target = target;
		this.ra = ra;
		this.threshold = threshold;
	}

	@Override
	public NativeImgLabeling< L, IntType > initializeLabeling()
	{
		return target;
	}

	@Override
	public boolean includeInRegion( final long[] parentPixel, final long[] candidatePixel, final L label )
	{
		ra.setPosition( candidatePixel );
		return ra.get().compareTo( threshold ) >= 0;
	}

	@Override
	public void finishedGrowStep( final Queue< long[] > childPixels, final L label )
	{}

	@Override
	public void finishedLabel( final L label )
	{}

	@Override
	public NativeImgLabeling< L, IntType > getResult()
	{
		return target;
	}
}
