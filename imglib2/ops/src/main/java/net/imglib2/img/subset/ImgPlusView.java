package net.imglib2.img.subset;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.ImgPlus;
import net.imglib2.meta.Metadata;
import net.imglib2.type.Type;

/**
 * Helper class to create a sub image.
 * 
 * @author dietzc, hornm University of Konstanz
 */
public class ImgPlusView< T extends Type< T >> extends ImgPlus< T >
{

	/**
	 * @see ImgView
	 * 
	 */
	public ImgPlusView( RandomAccessibleInterval< T > src, ImgFactory< T > fac, Metadata metadata )
	{
		super( new ImgView< T >( src, fac ), metadata );
	}

	/**
	 * @see ImgView
	 * 
	 */
	public ImgPlusView( RandomAccessibleInterval< T > src, ImgFactory< T > fac )
	{
		super( new ImgView< T >( src, fac ) );
	}
}
