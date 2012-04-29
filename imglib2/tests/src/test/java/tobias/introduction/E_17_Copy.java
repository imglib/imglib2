package tobias.introduction;

import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.io.ImgIOException;
import net.imglib2.io.ImgOpener;
import net.imglib2.type.NativeType;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Copy using (localizing) Cursor and RandomAccess.
 *
 * @author Tobias Pietzsch <tobias.pietzsch@gmail.com>
 */
public class E_17_Copy
{
	public static void main( final String[] args ) throws ImgIOException
	{
		final Img< FloatType > img = open( "src/test/java/resources/leaf-400x300.tif", new FloatType() );
		final Img< FloatType > copy = create( new FloatType() );

		final Cursor< FloatType > d = copy.cursor();
//		final Cursor< FloatType > d = copy.localizingCursor();
		final RandomAccess< FloatType > s = img.randomAccess();
		while ( d.hasNext() )
		{
			d.fwd();
			s.setPosition( d );
			d.get().set( s.get() );
		}

		ImageJFunctions.show( copy );
	}

	public static < T extends NativeType< T > > Img< T > create( final T type )
	{
		final ImgFactory< T > factory = new CellImgFactory< T >( 100 );
		final long[] dimensions = new long[] { 400, 300 };
		final Img< T > img = factory.create( dimensions, type );
		return img;
	}

	public static < T extends RealType< T > & NativeType< T > >
			Img< T > open( final String filename, final T type ) throws ImgIOException
	{
		final ImgOpener opener = new ImgOpener();
		final ImgFactory< T > factory = new ArrayImgFactory< T >();
		final Img< T > img = opener.openImg( filename, factory, type );
		return img;
	}
}
