package tests;

import ij.ImageJ;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.numeric.real.FloatType;

public class OpenAndDisplayWithCellContainer
{
	final static public void main( final String[] args )
	{
		new ImageJ();
		
		Img< FloatType > img = null;
		try
		{
			ImgFactory< FloatType > imgFactory = new CellImgFactory<FloatType>( new int[] {64, 64} );
			final ImgOpener io = new ImgOpener();
			img = io.openImg( "/home/tobias/workspace/data/73_float.tif", imgFactory, new FloatType() );
		}
		catch ( Exception e )
		{
			e.printStackTrace();
			return;
		}

		ImageJFunctions.show( img );
	}
}
