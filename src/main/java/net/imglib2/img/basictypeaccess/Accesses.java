/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2.img.basictypeaccess;

/**
 * Utility and helper methods for accesses ({@link ByteAccess} etc).
 *
 * @author Philipp Hanslovsky
 */
public interface Accesses
{

	public static void copy( final ByteAccess source, final ByteAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final ByteAccess source, final ByteAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

	public static void copy( final CharAccess source, final CharAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final CharAccess source, final CharAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

	public static void copy( final DoubleAccess source, final DoubleAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final DoubleAccess source, final DoubleAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

	public static void copy( final FloatAccess source, final FloatAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final FloatAccess source, final FloatAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

	public static void copy( final IntAccess source, final IntAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final IntAccess source, final IntAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

	public static void copy( final LongAccess source, final LongAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final LongAccess source, final LongAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

	public static void copy( final ShortAccess source, final ShortAccess target, final int size )
	{
		copy( source, target, 0, size );
	}

	public static void copy( final ShortAccess source, final ShortAccess target, final int start, final int stop )
	{
		for ( int index = start; index < stop; ++index )
		{
			target.setValue( index, source.getValue( index ) );
		}
	}

}
