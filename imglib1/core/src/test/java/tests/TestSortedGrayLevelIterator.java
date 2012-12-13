/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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

package tests;

import static org.junit.Assert.assertTrue;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.cursor.LocalizableByDimCursor;
import mpicbg.imglib.cursor.LocalizableCursor;
import mpicbg.imglib.cursor.special.SortedGrayLevelIteratorFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImageFactory;
import mpicbg.imglib.type.numeric.integer.ShortType;

import org.junit.Test;

/**
 * TODO
 *
 */
public class TestSortedGrayLevelIterator {

	
	Image<ShortType> image;
	public TestSortedGrayLevelIterator()
	{
		short[][] imgArray = new short[][]{
			{110,90,100},
			{ 50,50, 50},
			{ 40,20, 50},
			{ 50,50, 50},
			{120,70, 80}
		};
		image = createImageFromArray( imgArray );
	}
	
	
	public static Image<ShortType> createImageFromArray(short[][] imgArray)
	{
		int h = imgArray.length;
		int w = imgArray[0].length;

		// create a new Image with the same dimensions as the array
		ImageFactory<ShortType> imageFactory = new ImageFactory<ShortType>(new ShortType(), new ArrayContainerFactory());
		Image<ShortType> img = imageFactory.createImage(new int[] { w,h } );
		LocalizableByDimCursor<ShortType> cur =	img.createLocalizableByDimCursor();
		
		for(int x=0;x<w;x++)
		{
			for(int y=0;y<h;y++)
			{
				cur.setPosition(y, 1);
				cur.setPosition(x, 0);
				cur.getType().set(imgArray[y][x]);
			}
		}
		return img;
	}
		
	@Test
	public void testGrayLevelSorted() 
	{
		SortedGrayLevelIteratorFactory<ShortType> factory = new SortedGrayLevelIteratorFactory<ShortType>(image);
		LocalizableCursor<ShortType> cursorSortedPixel = factory.createSortedGrayLevelIterator(image);
		LocalizableByDimCursor<ShortType> cursorLoc = image.createLocalizableByDimCursor();
		
		short x = -1;
		short y = -1;
		int cnt = 0;
		int[] position = cursorSortedPixel.createPositionArray();
		for(ShortType pixel : cursorSortedPixel)
		{
			cnt++;
			y = pixel.get();
			assertTrue(x == -1 || x>=y); // test if sorted
			
			//set localizable cursor to the current position of the sorted pixel cursor
			cursorSortedPixel.getPosition(position);
			cursorLoc.setPosition(position);
			
			//check if identical pixel value in localizable cursor 
			assertTrue(y == cursorLoc.getType().get()); 
			
			x = y;
		}
		assertTrue(cnt == image.getNumPixels()); // test pixel number
	}
}
