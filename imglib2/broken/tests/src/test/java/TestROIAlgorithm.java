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

import net.imglib2.cursor.LocalizableByDimCursor;
import net.imglib2.cursor.LocalizableCursor;
import net.imglib2.cursor.special.RegionOfInterestCursor;
import net.imglib2.img.Image;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.LOCI;
import net.imglib2.type.numeric.RealType;
import ij.IJ;
import ij.ImagePlus;

import ij.io.OpenDialog;

import net.imglib2.algorithm.ROIAlgorithm;

/**
 * TODO
 *
 */
public class TestROIAlgorithm <T extends RealType<T>> extends ROIAlgorithm<T, T> {

	
	private LocalizableByDimCursor<T> outputCursor;
	
	public TestROIAlgorithm(Image<T> imageIn) {
		super(imageIn.createType(), imageIn, new int[]{1, 1});
		outputCursor = super.getOutputImage().createLocalizableByDimCursor();
	}

	@Override
	protected boolean patchOperation(int[] position,
			RegionOfInterestCursor<T> cursor) {
		outputCursor.setPosition(position);
		int i = 0;
		while (cursor.hasNext())
		{			
			cursor.fwd();
			outputCursor.getType().set(cursor.getType());
		}
		return true;
	}

	public static <R extends RealType<R>> void main(String args[])
	{
		OpenDialog od = new OpenDialog("Select an Image File", "");
		
		ImagePlus implus = IJ.openImage(od.getDirectory() + od.getFileName());
		Image<R> im = ImagePlusAdapter.wrap(implus);		
		Image<R> imout;	
                Image<R> imloci = LOCI.openLOCI(od.getDirectory() + od.getFileName(), new ArrayImgFactory());
		TestROIAlgorithm<R> tra = new TestROIAlgorithm<R>(imloci);
		
		int[] pos = new int[2];

		tra.process();
		imout = tra.getResult();

		/*
		LocalizableCursor<R> checkCursor = imout.createLocalizableCursor();		
		while (checkCursor.hasNext())
		{			
			checkCursor.fwd();
			checkCursor.getPosition(pos);
			IJ.log("" + pos[0] + "," + pos[1] + "," + checkCursor.getType().getRealFloat());
		}
		checkCursor.close();
		*/
		
		imloci.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack(imloci).show();
		
		im.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack(im).show();

		imout.getDisplay().setMinMax();		
		ImageJFunctions.displayAsVirtualStack(imout).show();
		
		
		
	}
	
	
	
}
