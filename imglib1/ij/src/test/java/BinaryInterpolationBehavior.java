/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
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

import ij.ImageJ;

import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;

import mpicbg.imglib.algorithm.labeling.BinaryInterpolation2D;
import mpicbg.imglib.container.shapelist.ShapeList;
import mpicbg.imglib.container.shapelist.ShapeListCached;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.type.logic.BitType;

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
