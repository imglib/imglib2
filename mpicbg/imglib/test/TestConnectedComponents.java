package mpicbg.imglib.test;
import ij.IJ;

import mpicbg.imglib.algorithm.roi.ConnectedComponents;
import mpicbg.imglib.algorithm.roi.StructuringElement;

import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.cursor.LocalizableByDimCursor;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImagePlusAdapter;

import mpicbg.imglib.image.display.imagej.ImageJFunctions;

import mpicbg.imglib.type.logic.BitType;

import mpicbg.imglib.type.numeric.RealType;

import mpicbg.imglib.type.numeric.integer.IntType;

public class TestConnectedComponents
{
	public static <T extends RealType<T>> void  main(String args[])
	{
		Image<T> im = ImagePlusAdapter.wrap(IJ.openImage());
		Cursor<T> c = im.createCursor();
	
		T val = im.createType();

		while (c.hasNext())
		{
			c.fwd();
			if (c.getType().compareTo(val) > 0)
			{
				val = c.getType();
			}
		}
		val = val.clone();
		
		StructuringElement strel = new StructuringElement(new int[]{3, 3}, "4 Connected");
		LocalizableByDimCursor<BitType> cursor = strel.createLocalizableByDimCursor();

		cursor.setPosition(new int[]{1, 0});
		cursor.getType().setOne();

		cursor.setPosition(new int[]{0, 1});
		cursor.getType().setOne();

		strel.getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack(strel).show();

		ConnectedComponents<T, IntType> cc = new ConnectedComponents<T, IntType>(new IntType(), im, strel, val);

		cc.process();

		ImageJFunctions.displayAsVirtualStack(im).show();
		cc.getResult().getDisplay().setMinMax();
		ImageJFunctions.displayAsVirtualStack(cc.getResult()).show();
	}
}