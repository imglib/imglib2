package net.imglib2.algorithm.regiongrowing;

import java.util.Map;
import java.util.Queue;

import net.imglib2.RandomAccess;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.labeling.NativeImgLabeling;
import net.imglib2.type.numeric.integer.IntType;

public class ThresholdRegionGrowing< T extends Comparable< T >> extends AbstractRegionGrowingAlgorithm< T, Integer >
{

	private final Img< T > img;

	private final T threshold;

	private final RandomAccess< T > ra;

	public ThresholdRegionGrowing( final Img< T > img, final T threshold, final Map< long[], Integer > seedLabels, final GrowingMode growingMode, final long[][] structuringElement )
	{
		super( seedLabels, growingMode, structuringElement, false );
		this.img = img;
		this.ra = img.randomAccess();
		this.threshold = threshold;
	}

	@Override
	protected NativeImgLabeling< Integer, IntType > initializeLabeling()
	{
		Img< IntType > backup = null;
		try
		{
			backup = img.factory().imgFactory( new IntType() ).create( img, new IntType() );
		}
		catch ( final IncompatibleTypeException e )
		{}
		return new NativeImgLabeling< Integer, IntType >( backup );
	}

	@Override
	protected boolean includeInRegion( final long[] parentPixel, final long[] candidatePixel, final Integer label )
	{
		ra.setPosition( candidatePixel );
		return ra.get().compareTo( threshold ) >= 0;
	}

	@Override
	protected void finishedGrowStep( final Queue< long[] > childPixels, final Integer label )
	{
		System.out.println( "Finished one grow step for label " + label + ". Added " + childPixels.size() + " candidates for next step." );// DEBUG
	}

	@Override
	protected void finishedLabel( final Integer label )
	{
		System.out.println( "Finished growing label " + label + "." );// DEBUG
	}

}
