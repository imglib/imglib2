package mpicbg.imglib.test;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ij.IJ;

import mpicbg.imglib.algorithm.roi.ConnectedComponents;
import mpicbg.imglib.algorithm.roi.StructuringElement;

import mpicbg.imglib.cursor.Cursor;

import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.ImagePlusAdapter;
import mpicbg.imglib.image.display.imagej.ImageJFunctions;

import mpicbg.imglib.type.numeric.RealType;
import mpicbg.imglib.type.numeric.integer.IntType;

public class TestConnectedComponents
{
	public static <T extends RealType<T>> void  main(String args[])
	{
		Image<T> im = ImagePlusAdapter.wrap(IJ.openImage());
		Cursor<T> c = im.createCursor();
		ArrayList<ConnectedComponents<T, IntType>> connectedComponentList = new ArrayList<ConnectedComponents<T, IntType>>();
		List<StructuringElement> strels;
	
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
		
		StructuringElement strel4 = ConnectedComponents.halfConnectedRaster(im.getNumDimensions());
		StructuringElement strel8 = ConnectedComponents.fullConnectedRaster(im.getNumDimensions());
		StructuringElement strel4all = ConnectedComponents.halfConnectedRandom(im.getNumDimensions());
		StructuringElement strel8all = ConnectedComponents.fullConnectedRandom(im.getNumDimensions());
		
		strels = Arrays.asList(strel4, strel8, strel4all, strel8all);
		
		for (StructuringElement s : strels)
		{
			s.getDisplay().setMinMax();
			ImageJFunctions.displayAsVirtualStack(s).show();
			connectedComponentList.add(new ConnectedComponents<T, IntType>(new IntType(), im, s, val));			
		}
		
		ImageJFunctions.displayAsVirtualStack(im).show();

		for (ConnectedComponents<T, IntType> cc : connectedComponentList)
		{
			cc.process();
			cc.getResult().getDisplay().setMinMax();
			ImageJFunctions.displayAsVirtualStack(cc.getResult()).show();
		}
		
	}
}