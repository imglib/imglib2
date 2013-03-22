/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2013 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

import ij.IJ;
import ij.ImagePlus;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.Img;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;

public class PeronaMalikAnisotropicDiffusionExample {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static <T extends RealType<T> & NativeType< T >> void main(String[] args) throws InterruptedException, URISyntaxException, MalformedURLException, ImgIOException, IncompatibleTypeException {
	
		ij.ImageJ.main(args);
		ImagePlus imp = IJ.openImage("http://rsb.info.nih.gov/ij/images/boats.gif");
		imp.show();
		
		Img<T> source = ImageJFunctions.wrap(imp);
		
//		PeronaMalikAnisotropicDiffusion<?> algo = new PeronaMalikAnisotropicDiffusion(source, 0.15, 20);
		PeronaMalikAnisotropicDiffusion<T> algo = new PeronaMalikAnisotropicDiffusion(source, 0.15, 
				new PeronaMalikAnisotropicDiffusion.WideRegionEnhancer(20));
		algo.setNumThreads();
		
		if (!algo.checkInput()) {
			System.out.println("Check input failed! With: "+algo.getErrorMessage());
			return;
		}
		

		int niter = 20;
		for (int i = 0; i < niter; i++) {
			System.out.println("Iteration "+(i+1)+" of "+niter+".");
			algo.process();
			imp.updateAndDraw();
		}
		
		System.out.println("Done in "+algo.getProcessingTime()+" ms.");

	}
}
