package tests;

import mpicbg.imglib.algorithm.gauss.GaussianConvolutionRealType;
import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.image.display.imagej.ImgLib2Display;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.outofbounds.OutOfBoundsConstantValueFactory;
import mpicbg.imglib.type.numeric.real.FloatType;
import ij.ImageJ;

public class OpenAndDisplay
{
	public static void main( String[] args )
	{
		new ImageJ();
		
		Container<FloatType> img = LOCI.openLOCIFloatType( "D:/Temp/Truman/MoreTiles/73.tif",  new ArrayContainerFactory() );
		
		ImgLib2Display.copyToImagePlus( img, new int[] {2, 0, 1} ).show();
		
		GaussianConvolutionRealType<FloatType> gauss = new GaussianConvolutionRealType<FloatType>( img, new OutOfBoundsConstantValueFactory<FloatType, Container<FloatType>>(), 2 );
		
		if ( !gauss.checkInput() || !gauss.process() )
		{
			System.out.println( gauss.getErrorMessage() );
			return;
		}
		
		ImgLib2Display.copyToImagePlus( gauss.getResult() ).show();
		
	}
}
