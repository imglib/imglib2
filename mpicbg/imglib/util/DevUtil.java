package mpicbg.imglib.util;

import mpicbg.imglib.container.array.Array;
import mpicbg.imglib.container.basictypecontainer.ByteAccess;
import mpicbg.imglib.container.basictypecontainer.array.ByteArray;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.type.numeric.integer.UnsignedByteType;

final public class DevUtil
{	
	private DevUtil() {}
	
	final public static Image<UnsignedByteType> createImageFromArray( final byte[] data, final long[] dim )
	{
		final ByteAccess byteAccess = new ByteArray( data );
		final Array<UnsignedByteType, ByteAccess> array = 
			new Array<UnsignedByteType, ByteAccess>( new UnsignedByteType(), byteAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final UnsignedByteType linkedType = new UnsignedByteType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return new Image<UnsignedByteType>(array, new UnsignedByteType());
	}
}
