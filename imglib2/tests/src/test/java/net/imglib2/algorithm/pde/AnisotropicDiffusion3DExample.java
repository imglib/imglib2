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

import net.imglib2.Point;
import net.imglib2.algorithm.region.BresenhamLine;
import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.view.Views;


public class AnisotropicDiffusion3DExample {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void main(String[] args) {

		Img<UnsignedByteType> image = createExampleImage(new UnsignedByteType());
		Img<UnsignedByteType> copy = image.copy();

		// Display it via ImgLib using ImageJ
		ImageJ.main(args);

		// Compute tensor

		MomentOfInertiaTensor3D tensor = new MomentOfInertiaTensor3D(image, 5);
//		long[] dimensions = new long[image.numDimensions()];
//		image.dimensions(dimensions);
//		IsotropicDiffusionTensor tensor = new IsotropicDiffusionTensor(dimensions , 1);

		tensor.process();
		Img<FloatType> diffusionTensor = tensor.getResult();
		
		ImagePlus imp = ImageJFunctions.wrap(image, "Processed");
		imp.show();

		// Instantiate diffusion solver

		StandardDiffusionScheme3D algo = new StandardDiffusionScheme3D(image, diffusionTensor);
//		NonNegativityDiffusionScheme3D algo = new NonNegativityDiffusionScheme3D(image, diffusionTensor);

		for (int i = 0; i < 10; i++) {
			System.out.println("Iteration "+(i+1));
			//			tensor.process();
			//			diffusionTensor = tensor.getResult();
			//			algo.setDiffusionTensor(diffusionTensor);

			algo.process();
			imp.updateAndDraw();
		}

		ImageJFunctions.show(algo.getIncrement(), "Increment");
		ImageJFunctions.wrapFloat(diffusionTensor, "Diffusion tensor").show();
		for (int i = 0; i < 6; i++) {
			ImageJFunctions.show(Views.hyperSlice(diffusionTensor, 3, i));
		}
		ImageJFunctions.show(copy, "Original image");
	}

	@SuppressWarnings("rawtypes")
	public static <T extends RealType<T> & NativeType< T >> Img openExampleImage(T type) {
		File file = new File( "/Users/tinevez/Desktop/Data/StarryNight.tif");

		ImgFactory< ? > imgFactory = new ArrayImgFactory< T >();
		Img<T> image = null;
		try {
			image = new ImgOpener().openImg( file.getAbsolutePath(), imgFactory );
		} catch (ImgIOException e) {
			e.printStackTrace();
		} catch (IncompatibleTypeException e) {
			e.printStackTrace();
		}

		return image;
	}
	
	public static <T extends RealType<T> & NativeType< T >> Img<T> createExampleImage(T type) {

		int size = 128;
		double[] phis = new double[] { 0 , 45 , 90,  135 ,  180 ,  225 ,270,  315  };
		double[] thetas = new double[] { 0, 30, 60, 90, 120, 150, 180 };

		final ImgFactory< T > imgFactory = new ArrayImgFactory<T>();
		Img<T> image = imgFactory.create(new int[] { size, size, size }, type);

		double phi, theta;
		Point P1, P2;
		long x1, x2, y1, y2, z1, z2;
		BresenhamLine<T> line = new BresenhamLine<T>(image);

		for (int j = 0; j < thetas.length; j++) {

			theta = Math.toRadians(thetas[j]);

			for (int i = 0; i < phis.length; i++) {

				phi = Math.toRadians(phis[i]);

				x1 = Math.round (size/2 + size/10 * Math.cos(phi) * Math.sin(theta));
				x2 = Math.round (size/2 + (size/4 - 2) * Math.cos(phi) * Math.sin(theta));

				y1 = Math.round (size/2 + size/10 * Math.sin(phi) * Math.sin(theta));
				y2 = Math.round (size/2 + (size/4 - 2) * Math.sin(phi) * Math.sin(theta));
				
				z1 = Math.round (size/2 + size/10 * Math.cos(theta) );
				z2 = Math.round (size/2 + (size/4 - 2) * Math.cos(theta) );
				
				P1 = new Point(x1, y1, z1);
				P2 = new Point(x2, y2, z2);
				line.reset(P1, P2);

				while (line.hasNext()) {
					line.next().setReal(255);
				}

				x1 = Math.round (size/2 + (size/4 + 1) * Math.cos(phi) * Math.sin(theta));
				x2 = Math.round (size/2 + (size/2 - 1) * Math.cos(phi) * Math.sin(theta));

				y1 = Math.round (size/2 + (size/4 + 1) * Math.sin(phi) * Math.sin(theta));
				y2 = Math.round (size/2 + (size/2 - 1) * Math.sin(phi) * Math.sin(theta));

				z1 = Math.round (size/2 + (size/4 + 1) * Math.cos(theta));
				z2 = Math.round (size/2 + (size/2 - 1) * Math.cos(theta));

				P1 = new Point(x1, y1, z1);
				P2 = new Point(x2, y2, z2);
				line.reset(P1, P2);

				while (line.hasNext()) {
					line.next().setReal(255);
				}

			}

		}

		return image;
	}

}
