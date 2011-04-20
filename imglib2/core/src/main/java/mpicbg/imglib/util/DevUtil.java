package mpicbg.imglib.util;

import mpicbg.imglib.img.Img;
import mpicbg.imglib.img.array.ArrayImg;
import mpicbg.imglib.img.basictypeaccess.ByteAccess;
import mpicbg.imglib.img.basictypeaccess.array.ByteArray;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

final public class DevUtil
{	
	private DevUtil() {}
	
	final public static Img<UnsignedByteType> createImageFromArray( final byte[] data, final long[] dim )
	{
		final ByteAccess byteAccess = new ByteArray( data );
		final ArrayImg<UnsignedByteType, ByteAccess> array = 
			new ArrayImg<UnsignedByteType, ByteAccess>( byteAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return array;
	}
}