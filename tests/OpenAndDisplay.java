package tests;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.display.imagej.ImgLib2Display;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.real.FloatType;
import ij.ImageJ;

public class OpenAndDisplay
{
	public static void main( String[] args )
	{
		new ImageJ();
		
		Container<FloatType, ?> img = LOCI.openLOCIFloatType( "D:/Temp/Truman/MoreTiles/73.tif",  new ArrayContainerFactory() );
		
		ImgLib2Display.copyToImagePlus( img, new int[] {2, 0, 1} ).show();
	}
}
