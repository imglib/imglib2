package net.imglib2.display.screenimage.awt;

import net.imglib2.RandomAccessibleInterval;
import net.imglib2.img.basictypeaccess.array.BitArray;
import net.imglib2.img.basictypeaccess.array.ByteArray;
import net.imglib2.img.basictypeaccess.array.DoubleArray;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.basictypeaccess.array.IntArray;
import net.imglib2.img.basictypeaccess.array.ShortArray;
import net.imglib2.type.NativeType;
import net.imglib2.type.logic.BitType;
import net.imglib2.type.numeric.integer.ByteType;
import net.imglib2.type.numeric.integer.IntType;
import net.imglib2.type.numeric.integer.ShortType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.integer.UnsignedIntType;
import net.imglib2.type.numeric.integer.UnsignedShortType;
import net.imglib2.type.numeric.real.DoubleType;
import net.imglib2.type.numeric.real.FloatType;

/**
 * Utility class to create {@link AWTScreenImage}s.
 * 
 * TODO: Add convenience methods to render {@link RandomAccessibleInterval}s.
 * 
 * @author Christian Dietz
 * 
 */
public class AWTScreenImageUtil
{

	/**
	 * Get an appropriate {@link AWTScreenImage} given a type and the
	 * dimensionality of the incoming image.
	 * 
	 * <p>
	 * Only the first two dimensions of the long[] dims are considered.
	 * </p>
	 * 
	 * TODO: review if this is really the only solution to get it running with
	 * jenkins javac.
	 * 
	 * @param type
	 *            type used to create empty {@link AWTScreenImage}
	 * @param dims
	 *            dimensions of the resulting img
	 * @return
	 */
	@SuppressWarnings( "unchecked" )
	public static < T extends NativeType< T >> ArrayImgAWTScreenImage< T, ? > emptyScreenImage( T type, long[] dims )
	{

		if ( BitType.class.isAssignableFrom( type.getClass() ) )
		{
			BitArray array = new BitArray( numElements( dims ) );
			ArrayImgAWTScreenImage< BitType, BitArray > container = new BitAWTScreenImage( new BitType( array ), array, dims );
			container.setLinkedType( new BitType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( ByteType.class.isAssignableFrom( type.getClass() ) )
		{
			ByteArray array = new ByteArray( numElements( dims ) );
			ArrayImgAWTScreenImage< ByteType, ByteArray > container = new ByteAWTScreenImage( new ByteType( array ), array, dims );
			container.setLinkedType( new ByteType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( UnsignedByteType.class.isAssignableFrom( type.getClass() ) )
		{
			ByteArray array = new ByteArray( numElements( dims ) );
			ArrayImgAWTScreenImage< UnsignedByteType, ByteArray > container = new UnsignedByteAWTScreenImage( new UnsignedByteType( array ), array, dims );
			container.setLinkedType( new UnsignedByteType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( ShortType.class.isAssignableFrom( type.getClass() ) )
		{
			ShortArray array = new ShortArray( numElements( dims ) );
			ArrayImgAWTScreenImage< ShortType, ShortArray > container = new ShortAWTScreenImage( new ShortType( array ), array, dims );
			container.setLinkedType( new ShortType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( UnsignedShortType.class.isAssignableFrom( type.getClass() ) )
		{
			ShortArray array = new ShortArray( numElements( dims ) );
			ArrayImgAWTScreenImage< UnsignedShortType, ShortArray > container = new UnsignedShortAWTScreenImage( new UnsignedShortType( array ), array, dims );
			container.setLinkedType( new UnsignedShortType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( IntType.class.isAssignableFrom( type.getClass() ) )
		{
			IntArray array = new IntArray( numElements( dims ) );
			ArrayImgAWTScreenImage< IntType, IntArray > container = new IntAWTScreenImage( new IntType( array ), array, dims );
			container.setLinkedType( new IntType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( UnsignedIntType.class.isAssignableFrom( type.getClass() ) )
		{
			IntArray array = new IntArray( numElements( dims ) );
			ArrayImgAWTScreenImage< UnsignedIntType, IntArray > container = new UnsignedIntAWTScreenImage( new UnsignedIntType( array ), array, dims );
			container.setLinkedType( new UnsignedIntType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( FloatType.class.isAssignableFrom( type.getClass() ) )
		{
			FloatArray array = new FloatArray( numElements( dims ) );
			ArrayImgAWTScreenImage< FloatType, FloatArray > container = new FloatAWTScreenImage( new FloatType( array ), array, dims );
			container.setLinkedType( new FloatType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		if ( DoubleType.class.isAssignableFrom( type.getClass() ) )
		{
			DoubleArray array = new DoubleArray( numElements( dims ) );
			ArrayImgAWTScreenImage< DoubleType, DoubleArray > container = new DoubleAWTScreenImage( new DoubleType( array ), array, dims );
			container.setLinkedType( new DoubleType( container ) );
			return ( ArrayImgAWTScreenImage< T, ? > ) container;
		}

		throw new IllegalArgumentException( "Can't find AWTScreenImage for type " + type.toString() + "!" );
	}

	// only the first two dimensions are considered
	private static int numElements( long[] dims )
	{
		return ( int ) ( dims[ 0 ] * dims[ 1 ] );
	}

}
