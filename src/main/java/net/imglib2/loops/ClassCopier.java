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
package net.imglib2.loops;

import java.io.DataInputStream;
import java.io.IOException;

/**
 * {@link ClassCopier} helps copying a class. The resulting class works as the
 * original.
 * <p>
 * Why would you want to copy a class?
 * <p>
 * The problem is described in <a href=
 * "https://github.com/tpietzsch/none/">https://github.com/tpietzsch/none/</a>.
 * The solution suggested there is to copy a specific method of a class, but
 * copying a whole class is easier.
 *
 * @author Matthias Arzt
 */
class ClassCopier< T >
{

	private final Class< ? extends T > original;

	private final byte[] bytes;

	public ClassCopier( final Class< ? extends T > original, final Class< T > interfaceOfOriginal )
	{
		if ( !interfaceOfOriginal.isAssignableFrom( original ) || interfaceOfOriginal.equals( original ) )
			throw new IllegalArgumentException( "\"original\" must be a implementation of interface \"interfaceOfOriginal\"" );
		this.original = original;
		this.bytes = classToBytes( original );
	}

	public Class< ? extends T > copy()
	{
		@SuppressWarnings( "unchecked" )
		final Class< ? extends T > copy = ( Class< ? extends T > ) new ClassCopyLoader( original.getClassLoader() ).bytesToClass( original.getName(), bytes );
		return copy;
	}

	private byte[] classToBytes( final Class< ? > aClass )
	{
		final String className = aClass.getName();
		final String classAsPath = className.replace( '.', '/' ) + ".class";
		try (DataInputStream stream = new DataInputStream( aClass.getClassLoader().getResourceAsStream( classAsPath ) ))
		{
			final byte[] bytes = new byte[ stream.available() ];
			stream.readFully( bytes );
			return bytes;
		}
		catch ( final IOException e )
		{
			throw new RuntimeException( e );
		}
	}

	private static class ClassCopyLoader extends ClassLoader
	{

		public ClassCopyLoader( ClassLoader parent )
		{
			super( parent );
		}

		private Class< ? > bytesToClass( final String className, final byte[] bytes )
		{
			final Class< ? > copiedClass = super.defineClass( className, bytes, 0, bytes.length );
			super.resolveClass( copiedClass );
			return copiedClass;
		}
	}
}
