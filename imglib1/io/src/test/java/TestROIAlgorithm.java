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

import ij.IJ;
import ij.ImagePlus;
import ij.io.OpenDialog;
import mpicbg.imglib.algorithm.ROIAlgorithm;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.special.RegionOfInterestCursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImagePlusAdapter;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;
import mpicbg.imglib.io.LOCI;
import mpicbg.imglib.type.numeric.RealType;

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
                Image<R> imloci = LOCI.openLOCI(od.getDirectory() + od.getFileName(), new ArrayContainerFactory());
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
