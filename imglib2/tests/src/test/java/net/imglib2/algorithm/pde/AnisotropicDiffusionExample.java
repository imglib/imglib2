package net.imglib2.algorithm.pde;

import ij.ImageJ;
import ij.ImagePlus;

import java.io.File;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;


public class AnisotropicDiffusionExample {

public static <T extends RealType<T> & NativeType< T >> void  main(String[] args) throws ImgIOException, IncompatibleTypeException {
		
		// Open file in imglib2
		File file = new File( "E:/Users/JeanYves/Desktop/Data/Y.tif");
		//		File file = new File( "/Users/tinevez/Desktop/Data/sYn.tif");
//		File file = new File( "/Users/tinevez/Desktop/Data/cross2.tif");
		
		ImgFactory< ? > imgFactory = new ArrayImgFactory< T >();
		Img< T > image = new ImgOpener().openImg( file.getAbsolutePath(), imgFactory );

		// Display it via ImgLib using ImageJ
		new ImageJ();
		ImagePlus imp = ImageJFunctions.wrap(image, "source");
		imp.show();
		
		// Compute tensor
		MomentOfInertiaTensor2D<T> tensor = new MomentOfInertiaTensor2D<T>(image, 5);
		tensor.process();
		Img<FloatType> diffusionTensor = tensor.getResult();
//		ImageJFunctions.show(diffusionTensor);
		
		
		// Instantiate algo
		NonNegativityDiffusionScheme2D<T> algo = new NonNegativityDiffusionScheme2D<T>(image, diffusionTensor);

		for (int i = 0; i < 60; i++) {
			System.out.println("Iteration "+i);
			algo.process();
			imp.getProcessor().setPixels(ImageJFunctions.wrap(image, "result").getProcessor().getPixelsCopy());
			imp.updateAndDraw();
		}

//		ImageJFunctions.show(algo.getIncrement());

	}
	
}
