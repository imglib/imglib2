/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */
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
