package net.imglib2.io;

import net.imglib2.exception.IncompatibleTypeException;
import net.imglib2.img.ImgFactory;
import net.imglib2.type.numeric.RealType;

/**
 * A class that encloses a Type that is a RealType and a NativeType, as well as an {@link ImgFactory} with the same generic type T
 *
 * @author stephan.preibisch@gmx.de
 *
 * @param <T>
 */
public class ImgCreator < T extends RealType< T > >
{
	public ImgFactory< T > factory;
	public T type;

	public ImgCreator( final T type )
	{
		this.type = type;
	}

	public void setImageFactory( final ImgFactory< ? > imgFactory ) throws IncompatibleTypeException
	{
		this.factory = imgFactory.imgFactory( type );
	}
}
