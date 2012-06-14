package net.imglib2.algorithm.edge;

import ij.IJ;
import ij.ImageJ;
import ij.ImagePlus;
import ij.gui.Line;
import ij.gui.Overlay;

import java.awt.Color;
import java.util.ArrayList;

import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;

public class EdgelDetectionExample
{
	final static public void main( final String[] args ) throws ImgIOException
	{
		new ImageJ();

		final ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();

		// load image
		final Img< FloatType > img = new ImgOpener().openImg( "/home/tobias/workspace/HisYFP/blob.tif", imgFactory, new FloatType() );

		// detect edgels
		final ArrayList< Edgel > edgels = SubpixelEdgelDetection.getEdgels( img, imgFactory, 2 );
		final ImagePlus imp = ImageJFunctions.show( img );
		imp.setOverlay( paintEdgels( edgels, 0.05 ) );

		for (int i = 0; i<7; ++i) IJ.run("In");
	}

	public static Overlay paintEdgels( final ArrayList< Edgel > edgels, final double magnitudeScale )
	{
		final Overlay ov = new Overlay();

		for ( final Edgel e : edgels )
		{
			final float[] position = e.getPosition();
			final float[] gradient = e.getGradient();
			final float magnitude = e.getMagnitude();

			final double x0 = position[0] + 0.5 * gradient[1];
			final double y0 = position[1] - 0.5 * gradient[0];
			final double x1 = x0 - gradient[1];
			final double y1 = y0 + gradient[0];
			Line l = new Line( x0 + 0.5, y0 + 0.5, x1 + 0.5, y1 + 0.5 );
			l.setStrokeColor( Color.cyan );
			ov.add( l );

			final double x2 = position[0];
			final double y2 = position[1];
			final double x3 = x2 + magnitudeScale * magnitude * gradient[0];
			final double y3 = y2 + magnitudeScale * magnitude * gradient[1];
			l = new Line( x2 + 0.5, y2 + 0.5, x3 + 0.5, y3 + 0.5 );
			l.setStrokeColor( Color.green );
			ov.add( l );
		}

		return ov;
	}
}
