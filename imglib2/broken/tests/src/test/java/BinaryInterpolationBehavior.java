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
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */

import ij.ImageJ;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import net.imglib2.algorithm.labeling.BinaryInterpolation2D;
import net.imglib2.container.shapelist.ShapeList;
import net.imglib2.container.shapelist.ShapeListCached;
import net.imglib2.img.Image;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.logic.BitType;

/**
 * TODO
 *
 */
public class BinaryInterpolationBehavior {

	static public final void main(String[] args) {
		
		int w = 600,
			h = 400;

		BitType background = new BitType(false);
		BitType foreground = new BitType(true);

		final ShapeList<BitType> shapeList1 = new ShapeListCached<BitType>(new int[]{w, h}, background, 32);
		shapeList1.addShape(new Rectangle(50, 50, 200, 200), foreground, new int[]{0});
		final Image<BitType> img1 = new Image<BitType>(shapeList1, shapeList1.getBackground(), "ShapeListContainer");

		final ShapeList<BitType> shapeList2 = new ShapeListCached<BitType>(new int[]{w, h}, background, 32);
		shapeList2.addShape(new Ellipse2D.Float(200, 60, 180, 180), foreground, new int[]{0});
		final Image<BitType> img2 = new Image<BitType>(shapeList2, shapeList2.getBackground(), "ShapeListContainer");

		new ImageJ();
		ImageJFunctions.displayAsVirtualStack(img1).show();
		ImageJFunctions.displayAsVirtualStack(img2).show();
		
		BinaryInterpolation2D interpol = new BinaryInterpolation2D(img1, img2, 0.5f);
		if (!interpol.checkInput() || !interpol.process()) {
			System.out.println("Error: " + interpol.getErrorMessage());
		}

		ImageJFunctions.displayAsVirtualStack(interpol.getResult()).show();
		
		System.out.println("Done!");
	}
}
