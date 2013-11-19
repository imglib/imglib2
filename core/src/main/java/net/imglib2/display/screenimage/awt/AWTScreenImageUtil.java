package net.imglib2.display.screenimage.awt;

import net.imglib2.RandomAccessibleInterval;
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
	public static < T extends NativeType< T >> AWTScreenImage emptyScreenImage( T type, long[] dims )
	{

		if ( BitType.class.isAssignableFrom( type.getClass() ) ) { return new ByteAWTScreenImage( ByteType.class.cast( type ), new ByteArray( numElements( dims ) ), dims ); }

		if ( ByteType.class.isAssignableFrom( type.getClass() ) ) { return new ByteAWTScreenImage( ByteType.class.cast( type ), new ByteArray( numElements( dims ) ), dims ); }

		if ( UnsignedByteType.class.isAssignableFrom( type.getClass() ) ) { return new UnsignedByteAWTScreenImage( UnsignedByteType.class.cast( type ), new ByteArray( numElements( dims ) ), dims ); }

		if ( ShortType.class.isAssignableFrom( type.getClass() ) ) { return new ShortAWTScreenImage( ShortType.class.cast( type ), new ShortArray( numElements( dims ) ), dims ); }

		if ( UnsignedShortType.class.isAssignableFrom( type.getClass() ) ) { return new UnsignedShortAWTScreenImage( UnsignedShortType.class.cast( type ), new ShortArray( numElements( dims ) ), dims ); }

		if ( IntType.class.isAssignableFrom( type.getClass() ) ) { return new IntAWTScreenImage( IntType.class.cast( type ), new IntArray( numElements( dims ) ), dims ); }

		if ( UnsignedIntType.class.isAssignableFrom( type.getClass() ) ) { return new UnsignedIntAWTScreenImage( UnsignedIntType.class.cast( type ), new IntArray( numElements( dims ) ), dims ); }

		if ( FloatType.class.isAssignableFrom( type.getClass() ) ) { return new FloatAWTScreenImage( FloatType.class.cast( type ), new FloatArray( numElements( dims ) ), dims ); }

		if ( DoubleType.class.isAssignableFrom( type.getClass() ) ) { return new DoubleAWTScreenImage( DoubleType.class.cast( type ), new DoubleArray( numElements( dims ) ), dims ); }

		throw new IllegalArgumentException( "Can't find AWTScreenImage for type " + type.toString() + "!" );
	}

	// only the first two dimensions are considered
	private static int numElements( long[] dims )
	{
		return ( int ) ( dims[ 0 ] * dims[ 1 ] );
	}

}
