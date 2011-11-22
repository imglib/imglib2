package net.imglib2.algorithm.mser;

import java.util.ArrayList;
import java.util.Collections;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.EllipseRoi;
import ij.gui.Overlay;
import ij.process.ByteProcessor;
import net.imglib2.Localizable;
import net.imglib2.algorithm.componenttree.ComponentTree;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.NumericType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.LongType;

public class SimpleMserTreeTest< T extends IntegerType< T > >
{
	final ImagePlus imp;
	final Overlay ov;
	final ImageStack stack;
	final int w;
	final int h;
	
	public static EllipseRoi createEllipse( final double[] mean, final double[] cov )
	{
		return createEllipse( mean, cov, 3 );
	}

	public static EllipseRoi createEllipse( final double[] mean, final double[] cov, final double nsigmas )
	{
        final double a = cov[0];
        final double b = cov[1];
        final double c = cov[2];
        final double d = Math.sqrt( a*a + 4*b*b - 2*a*c + c*c );
        final double scale1 = Math.sqrt( 0.5 * ( a+c+d ) ) * nsigmas;
        final double scale2 = Math.sqrt( 0.5 * ( a+c-d ) ) * nsigmas;
        final double theta = 0.5 * Math.atan2( (2*b), (a-c) );
        final double x = mean[ 0 ];
        final double y = mean[ 1 ];
        final double dx = scale1 * Math.cos( theta );
        final double dy = scale1 * Math.sin( theta );
        EllipseRoi ellipse = new EllipseRoi( x-dx, y-dy, x+dx, y+dy, scale2 / scale1 );
		return ellipse;
	}

	public SimpleMserTreeTest( final ImagePlus imp, final ImageStack stack, final int w, final int h )
	{
		this.imp = imp;
		ov = new Overlay();
		imp.setOverlay( ov );
		this.stack = stack;
		this.w = w;
		this.h = h;
	}
	
	public void visualise( SimpleMserTree< T >.Mser mser )
	{
		ByteProcessor byteProcessor = new ByteProcessor( w, h );
		byte[] pixels = ( byte[] )byteProcessor.getPixels();
		for ( Localizable l : mser )
		{
			int x = l.getIntPosition( 0 );
			int y = l.getIntPosition( 1 );
			pixels[ y * w + x ] = (byte)(255 & 0xff);
		}
		String label = "" + mser.value();
		stack.addSlice( label, byteProcessor );
	
		ov.add( createEllipse( mser.mean(), mser.cov(), 3 ) );
		
		for ( SimpleMserTree< T >.Mser m : mser.ancestors )
			visualise( m );
	}
	
	public void visualise( SimpleMserTree< T > tree )
	{
		for ( SimpleMserTree< T >.Mser mser : tree.roots() )
		{
			visualise( mser );
		}
	}

	public static Long median( ArrayList<Long> values )
	{
		Collections.sort(values);

		if (values.size() % 2 == 1)
			return values.get((values.size() + 1) / 2 - 1);
		else {
			long lower = values.get(values.size() / 2 - 1);
			long upper = values.get(values.size() / 2);

			return (lower + upper) / 2;
		}
	}
	public interface Benchmark
	{
		public void run();
	}

	public static void benchmark( Benchmark b )
	{
		ArrayList<Long> times = new ArrayList<Long>( 100 );
		final int numRuns = 200;
		for ( int i = 0; i < numRuns; ++i )
		{
			long startTime = System.currentTimeMillis();
			b.run();
			long endTime = System.currentTimeMillis();
			times.add( endTime - startTime );
		}
		for ( int i = 0; i < numRuns; ++i )
		{
			System.out.println( "run " + i + ": " + times.get( i ) + " ms" );
		}
		System.out.println();
		System.out.println( "median: " + median( times ) + " ms" );
		System.out.println();
	}
	
	public static class DarkToBrightDelta< T extends NumericType< T > > implements ComputeDeltaValue< T >
	{
		private final T delta;

		DarkToBrightDelta( final T delta )
		{
			this.delta = delta;
		}

		@Override
		public T valueMinusDelta( T value )
		{
			final T valueMinus = value.copy();
			valueMinus.sub( delta );
			return valueMinus;
		}
	}

	public static void main( String[] args )
	{
		final int delta = 10;
		final long minSize = 10;
		final long maxSize = 100*100;
		final double maxVar = 0.8;
		final double minDiversity = 0.5;
		
		final Img< IntType > img;		
		try
		{
			ImgFactory< IntType > imgFactory = new ArrayImgFactory< IntType >();
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/data/img1.tif", imgFactory, new IntType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}
	
		System.out.println( "benchmarking..." );
		benchmark( new Benchmark()
		{
			public void run()
			{
				final DarkToBrightDelta< IntType > darkToBrightDelta = new DarkToBrightDelta< IntType >( new IntType( delta ) );
				final ComponentTree.DarkToBright< IntType > darkToBrightComparator = new ComponentTree.DarkToBright< IntType >();
				final SimpleMserTree< IntType > tree = new SimpleMserTree< IntType >( minDiversity );
				final SimpleMserFilter< IntType > procNewMser = new SimpleMserFilter< IntType >( minSize, maxSize, maxVar, tree );
				final SimpleMserComponentHandler< IntType > handler = new SimpleMserComponentHandler< IntType >( new IntType( Integer.MAX_VALUE ), darkToBrightComparator, img, new ArrayImgFactory< LongType >(), darkToBrightDelta, procNewMser );
				ComponentTree.buildComponentTree( img, handler, handler, darkToBrightComparator );
				tree.pruneDuplicates();
			}
		} );

		final DarkToBrightDelta< IntType > darkToBrightDelta = new DarkToBrightDelta< IntType >( new IntType( delta ) );
		final ComponentTree.DarkToBright< IntType > darkToBrightComparator = new ComponentTree.DarkToBright< IntType >();
		final SimpleMserTree< IntType > tree = new SimpleMserTree< IntType >( minDiversity );
		final SimpleMserFilter< IntType > procNewMser = new SimpleMserFilter< IntType >( minSize, maxSize, maxVar, tree );
		final SimpleMserComponentHandler< IntType > handler = new SimpleMserComponentHandler< IntType >( new IntType( Integer.MAX_VALUE ), darkToBrightComparator, img, new ArrayImgFactory< LongType >(), darkToBrightDelta, procNewMser );
		ComponentTree.buildComponentTree( img, handler, handler, darkToBrightComparator );
		tree.pruneDuplicates();
		
		new ImageJ();		
		ImagePlus impImg = ImageJFunctions.show( img );
		IJ.run( "Enhance Contrast", "saturated=0.35" );

		final long[] dimensions = new long[ img.numDimensions() ];
		img.dimensions( dimensions );
		final int w = ( int ) dimensions[0];
		final int h = ( int ) dimensions[1];
		ImageStack stack = new ImageStack( w, h );
		
		final SimpleMserTreeTest< IntType > vis = new SimpleMserTreeTest< IntType >( impImg, stack, w, h );
		vis.visualise( tree );

		ImagePlus imp = new ImagePlus("components", stack);
		imp.show();
	}
}