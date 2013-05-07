package net.imglib2.ops.expression.examples;

import net.imglib2.img.Img;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.newroi.HyperSphereRegion;
import net.imglib2.ops.expression.ops.Ops;
import net.imglib2.ops.expression.ops.RegionFilter;
import net.imglib2.type.logic.BoolType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;

public class RegionFilterExample
{
	/**
	 * Apply a reduce function to each local neighborhood (of a particular shape, e.g. hypersphere) in the input image to compute pixels of the output image.
	 */
	public static void main( final String[] args ) throws ImgIOException
	{
		final String fn = "/Users/pietzsch/workspace/data/DrosophilaWing.tif";
		final int span = 2;

		final ArrayImgFactory< FloatType > factory = new ArrayImgFactory< FloatType >();
		final FloatType type = new FloatType();
		final Img< FloatType > imgInput = new ImgOpener().openImg( fn, factory, type );
		final Img< FloatType > imgOutput = factory.create( imgInput, type );

		// a min filter using hypersphere neighborhoods of radius span = 2.
		final RegionFilter< FloatType, BoolType, HyperSphereRegion > op = Ops.regionfilter( new HyperSphereRegion( 2, span ), Ops.reduce( Ops.<FloatType>min() ) );
		Ops.compute( op, imgOutput, Views.extendBorder( imgInput ) );

		// a sum filter using 5x5 hyperbox neighborhoods.
//		Ops.compute( Ops.regionfilter( new HyperBoxRegion( Intervals.createMinMax( -2, -2, 2, 2 ) ), Ops.reduce( Ops.<FloatType>add() ) ), imgOutput, Views.extendBorder( imgInput ) );

		// a max filter using hypersphere neighborhoods of radius 5.
//		Ops.compute( Ops.regionfilter( new HyperSphereRegion( 2, 5 ), Ops.reduce( Ops.<FloatType>max() ) ), imgOutput, Views.extendBorder( imgInput ) );

		ImageJFunctions.show( imgInput, "input" );
		ImageJFunctions.show( imgOutput, "min filtered" );
	}

}
