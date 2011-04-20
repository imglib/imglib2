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
