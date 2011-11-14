package mpicbg.imglib.util;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.array.ArrayContainerFactory;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.DoubleAccess;
import mpicbg.imglib.container.basictypecontainer.ShortAccess;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.container.basictypecontainer.array.DoubleArray;
import mpicbg.imglib.container.basictypecontainer.array.ShortArray;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;
import mpicbg.imglib.type.numeric.integer.UnsignedShortType;
import mpicbg.imglib.type.numeric.real.DoubleType;

final public class DevUtil
{	
	private DevUtil() {}
	
	final public static Image<UnsignedByteType> createImageFromArray( final byte[] data, final int[] dim )
	{
		final ByteAccess byteAccess = new ByteArray( data );
		final Array<UnsignedByteType, ByteAccess> array = 
			new Array<UnsignedByteType, ByteAccess>(new ArrayContainerFactory(), byteAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<UnsignedByteType>(array, new UnsignedByteType());
	}
	
	final public static Image<UnsignedShortType> createImageFromArray( final short[] data, final int[] dim )
	{
		final ShortAccess access = new ShortArray( data );
		final Array<UnsignedShortType, ShortAccess> array = 
			new Array<UnsignedShortType, ShortAccess>(new ArrayContainerFactory(), access, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedShortType linkedType = new UnsignedShortType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<UnsignedShortType>(array, new UnsignedShortType());
	}
	

	final public static Image<DoubleType> createImageFromArray( final double[] data, final int[] dim )
	{
		final DoubleAccess access = new DoubleArray( data );
		final Array<DoubleType, DoubleAccess> array = 
			new Array<DoubleType, DoubleAccess>(new ArrayContainerFactory(), access, dim, 1 );
			
		// create a Type that is linked to the container
		final DoubleType linkedType = new DoubleType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<DoubleType>(array, new DoubleType());
	}
}
