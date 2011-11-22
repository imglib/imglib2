package tests;

import ij.ImageJ;
import ij.ImagePlus;
import net.imglib2.converter.TypeIdentity;
import net.imglib2.img.ImgPlus;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJVirtualStackFloat;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.util.Util;

public class HyperStackTest
{
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		String imgName = "D:/Temp/73.tif";
		final ImgOpener io = new ImgOpener();

		try
		{
			final ImgPlus<FloatType> img = io.openImg( imgName,  new ArrayImgFactory<FloatType>(), new FloatType() );
			final float[] calibration = img.getCalibration();
			
			System.out.println( "Calibration: " + Util.printCoordinates( calibration ) );
			
			final ImageJVirtualStackFloat< FloatType > stack = new ImageJVirtualStackFloat< FloatType >( img, new TypeIdentity< FloatType >() );
			final ImagePlus imp = new ImagePlus( imgName, stack );
			/*
			calibration = imp.getCalibration();
			calibration.pixelWidth = ...;
			calibration.pixelHeight = ...;
			calibration.pixelDepth = ...;
			calibration.frameInterval = ...;
			calibration.setUnit( "um" );
			imp.setDimensions( numChannels, numZSlices, numFrames );
			*/
			
			/*
			ImagePlus imp = getImage();
			Overlay ov = new Overlay();
			for ( int r = 0; r < regions.length; r++ )
			{
				ov.add( regions[ r ] );
			}
			imp.setOverlay( ov );
			*/
	
			imp.setDimensions( 1, ( int ) img.dimension( 2 ), 1 );
			imp.setOpenAsHyperStack( true );
			imp.show();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
}
