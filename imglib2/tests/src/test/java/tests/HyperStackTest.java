package tests;

import ij.ImageJ;
import ij.ImagePlus;
import mpicbg.imglib.converter.TypeIdentity;
import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImgFactory;
import mpicbg.imglib.io.ImgOpener;
import mpicbg.imglib.io.ImgPlus;
import mpicbg.imglib.type.numeric.real.FloatType;
import mpicbg.imglib.util.Util;

public class HyperStackTest
{
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		String imgName = "D:/Temp/73.tif";
		final ImgOpener io = new ImgOpener();

		try
		{
			final ImgPlus<FloatType> imgPlus = io.openImg( imgName,  new ArrayImgFactory<FloatType>(), new FloatType() );
			final Img<FloatType> img = imgPlus.getImg();
			final float[] calibration = imgPlus.getCalibration();
			
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
