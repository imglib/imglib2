/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2023 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
 * John Bogovic, Albert Cardona, Barry DeZonia, Christian Dietz, Jan Funke,
 * Aivar Grislis, Jonathan Hale, Grant Harris, Stefan Helfrich, Mark Hiner,
 * Martin Horn, Steffen Jaensch, Lee Kamentsky, Larry Lindsey, Melissa Linkert,
 * Mark Longair, Brian Northan, Nick Perry, Curtis Rueden, Johannes Schindelin,
 * Jean-Yves Tinevez and Michael Zinsmaier.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */
package net.imglib2.blocks;

import java.nio.IntBuffer;
import java.util.Arrays;
import net.imglib2.img.basictypeaccess.array.ArrayDataAccess;
import net.imglib2.img.basictypeaccess.nio.BufferAccess;
import net.imglib2.type.PrimitiveType;

// TODO javadoc
// low-level copying methods
// implementations for all primitive types
// T is a primitive array type
interface MemCopy< S, T >
{
	/**
	 * Copy {@code length} components from the {@code src} array to the {@code
	 * dest} array. The components at positions {@code srcPos} through {@code
	 * srcPos+length-1} in the source array are copied into positions {@code
	 * destPos} through {@code destPos+length-1}, respectively, of the
	 * destination array.
	 */
	void copyForward( S src, int srcPos, T dest, int destPos, int length );

	/**
	 * Copy {@code length} components from the {@code src} array to the {@code
	 * dest} array, in reverse order. The components at positions {@code srcPos}
	 * through {@code srcPos-length-1} in the source array are copied into
	 * positions {@code destPos} through {@code destPos+length-1}, respectively,
	 * of the destination array.
	 */
	void copyReverse( S src, int srcPos, T dest, int destPos, int length );

	/**
	 * Copy component at position {@code srcPos} in the {@code src} array
	 * ({@code length} times) into positions {@code destPos} through {@code
	 * destPos+length-1} of the destination array.
	 */
	void copyValue( S src, int srcPos, T dest, int destPos, int length );

	/**
	 * Copy {@code length} components from the {@code src} array to the {@code
	 * dest} array. The components at positions {@code srcPos} through {@code
	 * srcPos+length-1} in the source array are copied into positions {@code
	 * destPos}, {@code destPos+destStride}, {@code destPos + 2*destStride},
	 * etc., through {@code destPos+(length-1)*destStride} of the destination
	 * array.
	 */
	void copyStrided( S src, int srcPos, T dest, int destPos, int destStride, int length );

	/**
	 * Copy {@code numLines} stretches of {@code lineLength} elements.
	 *
	 * @param lineDir {@code 1}, {@code -1}, or {@code 0}. This corresponds (for every line being copied) to the source position moving forward, backward, or not at all, as the dest position is moving forward.
	 * @param lineLength how many elements to copy per line
	 * @param numLines how many lines to copy
	 * @param src source array
	 * @param srcPos starting position in source array
	 * @param srcStep offset to next line in src
	 * @param dest dest array
	 * @param destPos starting position in dest array
	 * @param destStep offset to next line in dest
	 */
	// Note that this default implementation is overridden in each
	// implementation (with identical code) to soften the performance hit from
	// polymorphism. The default implementation is left here, to make additional
	// implementations easier.
	default void copyLines(
			final int lineDir,
			final int lineLength,
			final int numLines,
			final S src,
			final int srcPos,
			final int srcStep,
			final T dest,
			final int destPos,
			final int destStep )
	{
		if ( lineDir == 1 )
			for ( int i = 0; i < numLines; ++i )
				copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		else if ( lineDir == -1 )
			for ( int i = 0; i < numLines; ++i )
				copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		else // cstep0 == 0
			for ( int i = 0; i < numLines; ++i )
				copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
	}


	MemCopyBoolean BOOLEAN = new MemCopyBoolean();
	MemCopyByte BYTE = new MemCopyByte();
	MemCopyChar CHAR = new MemCopyChar();
	MemCopyShort SHORT = new MemCopyShort();
	MemCopyInt INT = new MemCopyInt();
	MemCopyLong LONG = new MemCopyLong();
	MemCopyFloat FLOAT = new MemCopyFloat();
	MemCopyDouble DOUBLE = new MemCopyDouble();

	static MemCopy< ?, ? > forPrimitiveType( final PrimitiveType primitiveType )
	{
		switch ( primitiveType )
		{
		case BOOLEAN:
			return BOOLEAN;
		case BYTE:
			return BYTE;
		case CHAR:
			return CHAR;
		case SHORT:
			return SHORT;
		case INT:
			return INT;
		case LONG:
			return LONG;
		case FLOAT:
			return FLOAT;
		case DOUBLE:
			return DOUBLE;
		default:
		case UNDEFINED:
			throw new IllegalArgumentException();
		}
	}

	class MemCopyBoolean implements MemCopy< boolean[], boolean[] >
	{
		@Override
		public void copyForward( final boolean[] src, final int srcPos, final boolean[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final boolean[] src, final int srcPos, final boolean[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final boolean[] src, final int srcPos, final boolean[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final boolean[] src, final int srcPos, final boolean[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final boolean[] src, final int srcPos, final int srcStep, final boolean[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyByte implements MemCopy< byte[], byte[] >
	{
		@Override
		public void copyForward( final byte[] src, final int srcPos, final byte[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final byte[] src, final int srcPos, final byte[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final byte[] src, final int srcPos, final byte[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final byte[] src, final int srcPos, final byte[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final byte[] src, final int srcPos, final int srcStep, final byte[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyShort implements MemCopy< short[], short[] >
	{
		@Override
		public void copyForward( final short[] src, final int srcPos, final short[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final short[] src, final int srcPos, final short[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final short[] src, final int srcPos, final short[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}


		@Override
		public void copyStrided( final short[] src, final int srcPos, final short[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final short[] src, final int srcPos, final int srcStep, final short[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyChar implements MemCopy< char[], char[] >
	{
		@Override
		public void copyForward( final char[] src, final int srcPos, final char[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final char[] src, final int srcPos, final char[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final char[] src, final int srcPos, final char[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final char[] src, final int srcPos, final char[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final char[] src, final int srcPos, final int srcStep, final char[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyInt implements MemCopy< int[], int[] >
	{
		@Override
		public void copyForward( final int[] src, final int srcPos, final int[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final int[] src, final int srcPos, final int[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final int[] src, final int srcPos, final int[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final int[] src, final int srcPos, final int[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final int[] src, final int srcPos, final int srcStep, final int[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyLong implements MemCopy< long[], long[] >
	{
		@Override
		public void copyForward( final long[] src, final int srcPos, final long[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final long[] src, final int srcPos, final long[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final long[] src, final int srcPos, final long[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final long[] src, final int srcPos, final long[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final long[] src, final int srcPos, final int srcStep, final long[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyFloat implements MemCopy< float[], float[] >
	{
		@Override
		public void copyForward( final float[] src, final int srcPos, final float[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final float[] src, final int srcPos, final float[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final float[] src, final int srcPos, final float[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final float[] src, final int srcPos, final float[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final float[] src, final int srcPos, final int srcStep, final float[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

	class MemCopyDouble implements MemCopy< double[], double[] >
	{
		@Override
		public void copyForward( final double[] src, final int srcPos, final double[] dest, final int destPos, final int length )
		{
			System.arraycopy( src, srcPos, dest, destPos, length );
		}

		@Override
		public void copyReverse( final double[] src, final int srcPos, final double[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src[ srcPos - i ];
		}

		@Override
		public void copyValue( final double[] src, final int srcPos, final double[] dest, final int destPos, final int length )
		{
			Arrays.fill( dest, destPos, destPos + length, src[ srcPos ] );
		}

		@Override
		public void copyStrided( final double[] src, final int srcPos, final double[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src[ srcPos + i ];
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final double[] src, final int srcPos, final int srcStep, final double[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}


	/*
	 * -----------------------------------------------------------------------
	 *
	 * EXPERIMENTAL: Copying from Buffers into primitive arrays
	 *
	 * -----------------------------------------------------------------------
	 */

	MemCopy BUFFER_TO_ARRAY_BOOLEAN = null;
	MemCopy BUFFER_TO_ARRAY_BYTE = null;
	MemCopy BUFFER_TO_ARRAY_CHAR = null;
	MemCopy BUFFER_TO_ARRAY_SHORT = null;
	MemCopyBufferInt BUFFER_TO_ARRAY_INT = new MemCopyBufferInt();
	MemCopy BUFFER_TO_ARRAY_LONG = null;
	MemCopy BUFFER_TO_ARRAY_FLOAT = null;
	MemCopy BUFFER_TO_ARRAY_DOUBLE = null;

	static MemCopy< ?, ? > forPrimitiveType( final PrimitiveType primitiveType, final ArrayDataAccess< ? > sourceAccessType )
	{
		boolean fromBuffer = sourceAccessType instanceof BufferAccess;
		switch ( primitiveType )
		{
		case BOOLEAN:
			return fromBuffer ? BUFFER_TO_ARRAY_BOOLEAN : BOOLEAN;
		case BYTE:
			return fromBuffer ? BUFFER_TO_ARRAY_BYTE : BYTE;
		case CHAR:
			return fromBuffer ? BUFFER_TO_ARRAY_CHAR : CHAR;
		case SHORT:
			return fromBuffer ? BUFFER_TO_ARRAY_SHORT : SHORT;
		case INT:
			return fromBuffer ? BUFFER_TO_ARRAY_INT : INT;
		case LONG:
			return fromBuffer ? BUFFER_TO_ARRAY_LONG : LONG;
		case FLOAT:
			return fromBuffer ? BUFFER_TO_ARRAY_FLOAT : FLOAT;
		case DOUBLE:
			return fromBuffer ? BUFFER_TO_ARRAY_DOUBLE : DOUBLE;
		default:
		case UNDEFINED:
			throw new IllegalArgumentException();
		}
	}


	class MemCopyBufferInt implements MemCopy< IntBuffer, int[] >
	{
		@Override
		public void copyForward( final IntBuffer src, final int srcPos, final int[] dest, final int destPos, final int length )
		{
			src.position(srcPos);
			src.get( dest, destPos, length );
		}

		@Override
		public void copyReverse( final IntBuffer src, final int srcPos, final int[] dest, final int destPos, final int length )
		{
			for ( int i = 0; i < length; ++i )
				dest[ destPos + i ] = src.get( srcPos - i );
		}

		@Override
		public void copyValue( final IntBuffer src, final int srcPos, final int[] dest, final int destPos, final int length )
		{
			final int val = src.get( srcPos );
			Arrays.fill( dest, destPos, destPos + length, val );
		}

		@Override
		public void copyStrided( final IntBuffer src, final int srcPos, final int[] dest, final int destPos, final int destStride, final int length )
		{
			if ( destStride == 1 )
				copyForward( src, srcPos, dest, destPos, length );
			else
				for ( int i = 0; i < length; ++i )
					dest[ destPos + i * destStride ] = src.get( srcPos + i );
		}

		@Override
		public void copyLines( final int lineDir, final int lineLength, final int numLines, final IntBuffer src, final int srcPos, final int srcStep, final int[] dest, final int destPos, final int destStep )
		{
			if ( lineDir == 1 )
				for ( int i = 0; i < numLines; ++i )
					copyForward( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else if ( lineDir == -1 )
				for ( int i = 0; i < numLines; ++i )
					copyReverse( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
			else // cstep0 == 0
				for ( int i = 0; i < numLines; ++i )
					copyValue( src, srcPos + i * srcStep, dest, destPos + i * destStep, lineLength );
		}
	}

}
