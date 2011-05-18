package net.imglib2.shape;

import ij.ImageJ;

import java.awt.Rectangle;

import mpicbg.util.Timer;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class ShowShapeImageSeries {

	final static private < T extends Type< T > > long draw(
			final IterableInterval< T > target,
			final ShapeListSeries< T > sl )
	{
		final Timer timer = new Timer();
		timer.start();
		final Cursor< T > c = target.localizingCursor();
		final RandomAccess<T> r = sl.randomAccess();
		while ( c.hasNext() )
		{
			c.fwd();
			r.setPosition(c);
			c.get().set( r.get() );
		}
		return timer.stop();
	}
	
	static public final void main(String[] args) {
		final ImagePlusImgFactory< UnsignedShortType > factory = new ImagePlusImgFactory< UnsignedShortType >();
		long[] size = new long[]{400, 400, 3};
		final ImagePlusImg< UnsignedShortType, ? > img = factory.create( new long[]{ size[ 0 ], size[ 1 ], size[ 2 ] }, new UnsignedShortType() );
		
		new ImageJ();
		
		ShapeListSeries<UnsignedShortType> sl = new ShapeListSeries<UnsignedShortType>(size, new UnsignedShortType(0));
		sl.add(new Rectangle(40, 40, 100, 100), new UnsignedShortType(255), new long[]{0, 0, 0});
		sl.add(new Rectangle(60, 70, 100, 100), new UnsignedShortType(127), new long[]{0, 0, 0});
		sl.add(new Rectangle(50, 50, 100, 100), new UnsignedShortType(255), new long[]{0, 0, 1});
		sl.add(new Rectangle(60, 60, 100, 100), new UnsignedShortType(255), new long[]{0, 0, 2});
		
		long time = draw(img, sl);
		System.out.println("took: " + time);
		
		try
		{
			img.getImagePlus().show();
		}
		catch ( ImgLibException e )
		{
			e.printStackTrace();
		}
	}
}
