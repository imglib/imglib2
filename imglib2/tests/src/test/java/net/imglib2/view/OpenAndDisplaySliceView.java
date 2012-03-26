package net.imglib2.view;

import ij.ImageJ;
import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;


public class OpenAndDisplaySliceView
{
	final static public void main( final String[] args )
	{
		new ImageJ();

		Img< FloatType > img = null;
		try
		{
			final ImgFactory< FloatType > imgFactory = new ArrayImgFactory< FloatType >();
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/data/73_float.tif", imgFactory, new FloatType() );
		}
		catch ( final Exception e )
		{
			e.printStackTrace();
			return;
		}

		final RandomAccessibleInterval< FloatType > view = Views.hyperSlice( img, 2, 10 );

		ImageJFunctions.show( view );
	}
}
