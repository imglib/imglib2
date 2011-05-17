package net.imglib2.shape;

import java.awt.Rectangle;

import mpicbg.util.Timer;
import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.RealRandomAccess;
import net.imglib2.exception.ImgLibException;
import net.imglib2.img.imageplus.ImagePlusImg;
import net.imglib2.img.imageplus.ImagePlusImgFactory;
import net.imglib2.type.Type;
import net.imglib2.type.numeric.integer.UnsignedShortType;

public class ShowShapeImage {

	final static private < T extends Type< T > > long draw(
			final IterableInterval< T > target,
			final ShapeList< T > sl )
	{
		final Timer timer = new Timer();
		timer.start();
		final Cursor< T > c = target.localizingCursor();
		final RealRandomAccess<T> r = sl.realRandomAccess();
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
		long[] size = new long[]{400, 400};
		final ImagePlusImg< UnsignedShortType, ? > img = factory.create( new long[]{ size[ 0 ], size[ 1 ], 1 }, new UnsignedShortType() );
		
		ShapeList<UnsignedShortType> sl = new ShapeList<UnsignedShortType>(new UnsignedShortType(0));
		sl.add(new Rectangle(40, 40, 100, 100), new UnsignedShortType(255));
		
		draw(img, sl);
		
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
