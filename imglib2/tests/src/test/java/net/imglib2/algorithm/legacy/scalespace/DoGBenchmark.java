package net.imglib2.algorithm.legacy.scalespace;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.io.ImgOpener;
import net.imglib2.outofbounds.OutOfBoundsMirrorFactory;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.BenchmarkHelper;

public class DoGBenchmark
{
	private static double computeK( final int stepsPerOctave )
	{
		return Math.pow( 2f, 1f / stepsPerOctave );
	}

	private static float computeKWeight( final float k )
	{
		return 1.0f / ( k - 1.0f );
	}

	private static float getDiffSigma( final float sigmaA, final float sigmaB )
	{
		return ( float ) Math.sqrt( sigmaB * sigmaB - sigmaA * sigmaA );
	}

	private static float[] computeSigmaDiff( final float[] sigma, final float imageSigma )
	{
		final float[] sigmaDiff = new float[ 2 ];

		sigmaDiff[ 0 ] = getDiffSigma( imageSigma, sigma[ 0 ] );
		sigmaDiff[ 1 ] = getDiffSigma( imageSigma, sigma[ 1 ] );

		return sigmaDiff;
	}

	public static void main( final String[] args )
	{
		final String filename = "/home/tobias/workspace/data/e002_t00001-a000-c001-i0.tif";
		final int numRuns = 10;
		final boolean printIndividualTimes = true;

		final float sigma = 3.597f;
		final float minPeakValue = 0.02f;
		final float minInitialPeakValue = minPeakValue / 4;
		final int stepsPerOctave = 4;
		final float imageSigma = 0.5f;

		try
		{
			final ImgPlus< FloatType > image = new ImgOpener().openImg( filename, new ArrayImgFactory< FloatType >(), new FloatType() );
			final OutOfBoundsMirrorFactory<FloatType, RandomAccessibleInterval<FloatType>> oobsFactory = new OutOfBoundsMirrorFactory< FloatType, RandomAccessibleInterval<FloatType> >( OutOfBoundsMirrorFactory.Boundary.SINGLE );

			final float k = (float) computeK( stepsPerOctave );
			final float sigma1 = sigma;
			final float sigma2 = sigma * k;

	        final float[] sigmaXY = new float[]{ sigma1, sigma2 };
	        final float[] sigmaDiffXY = computeSigmaDiff( sigmaXY, imageSigma );

	        final float K_MIN1_INV = computeKWeight(k);

	        final double[][] sigmaDiff = new double[ 2 ][ 3 ];
	        sigmaDiff[ 0 ][ 0 ] = sigmaDiffXY[ 0 ];
	        sigmaDiff[ 0 ][ 1 ] = sigmaDiffXY[ 0 ];
	        sigmaDiff[ 1 ][ 0 ] = sigmaDiffXY[ 1 ];
	        sigmaDiff[ 1 ][ 1 ] = sigmaDiffXY[ 1 ];

	        // sigmaZ is at least twice the image sigma
			if ( image.numDimensions() == 3 )
			{
				final float sigma1Z = Math.max( imageSigma * 2, sigma1 / (float)image.calibration( 2 ) );
				final float sigma2Z = sigma1Z * k;
				final float[] sigmaZ = new float[]{ sigma1Z, sigma2Z };
				final float[] sigmaDiffZ = computeSigmaDiff( sigmaZ, imageSigma );
		        sigmaDiff[ 0 ][ 2 ] = sigmaDiffZ[ 0 ];
		        sigmaDiff[ 1 ][ 2 ] = sigmaDiffZ[ 1 ];
			}

			final DifferenceOfGaussianOld<FloatType> dogOld = new DifferenceOfGaussianOld<FloatType>( image.getImg(), image.factory(), oobsFactory, sigmaDiff[0], sigmaDiff[1], minInitialPeakValue, K_MIN1_INV );
			dogOld.setKeepDoGImg( true );
			final DifferenceOfGaussian<FloatType> dogNew = new DifferenceOfGaussian<FloatType>( image.getImg(), image.factory(), oobsFactory, sigmaDiff[0], sigmaDiff[1], minInitialPeakValue, K_MIN1_INV );
			dogNew.setKeepDoGImg( true );


			System.out.println( "DifferenceOfGaussian old" );
			BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
				@Override
				public void run()
				{
					dogOld.process();
//			        System.out.println( "found " + dogOld.getPeaks().size() + " peaks" );
				}
			} );

			System.out.println( "DifferenceOfGaussian new" );
			BenchmarkHelper.benchmarkAndPrint( numRuns, printIndividualTimes, new Runnable() {
				@Override
				public void run()
				{
					dogNew.process();
//			        System.out.println( "found " + dogNew.getPeaks().size() + " peaks" );
				}
			} );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
		}
	}
}
