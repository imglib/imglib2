package net.imglib2.util;

import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.basictypeaccess.ByteAccess;
import net.imglib2.img.basictypeaccess.FloatAccess;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Utility methods for developers
 * 
 * Stephan Preibisch, Curtis Rueden
 *
 */
final public class DevUtil
{	
	private DevUtil() {}
	
	/**
	 * Creates an {@link ArrayImg} of UnsignedByteType from a java byte array by wrapping it
	 * 
	 * @param data - the array
	 * @param dim - the dimensionality
	 * 
	 * @return the instance of {@link ArrayImg} using the given byte array
	 */
	final public static ArrayImg<UnsignedByteType, ByteAccess> createImageFromArray( final byte[] data, final long[] dim )
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

	/**
	 * Creates an {@link ArrayImg} of FloatType from a java float array by wrapping it
	 * 
	 * @param data - the array
	 * @param dim - the dimensionality
	 * 
	 * @return the instance of {@link ArrayImg} using the given float array
	 */
	final public static ArrayImg<FloatType,FloatAccess> createImageFromArray( final float[] data, final long[] dim )
	{
		final FloatAccess floatAccess = new FloatArray( data );
		final ArrayImg<FloatType, FloatAccess> array = 
			new ArrayImg<FloatType, FloatAccess>( floatAccess, dim, 1 );
			
		// create a Type that is linked to the container
		final FloatType linkedType = new FloatType( array );
		
		// pass it to the DirectAccessContainer
		array.setLinkedType( linkedType );
		
		return array;
	}
}
