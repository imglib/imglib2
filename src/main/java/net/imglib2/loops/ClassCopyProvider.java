/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2024 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Helper to create multiple copies of a class.
 * <p>
 * The copies of the class have individual copies of the byte code.
 * The JIT compiler optimizes the class copies individually,
 * which can increase performance.
 *
 * @author Matthias Arzt
 */
public class ClassCopyProvider< T >
{

	private final Map< Object, Class< ? extends T > > map = new ConcurrentHashMap<>();

	private final ClassCopier< T > copier;

	private final Class< ? >[] signature;

	/**
	 * Create a ClassCopyProvider to make copy a a given class.
	 *
	 * @param clazz                Class to be copied.
	 * @param interfaceOfClazz     Interface that's implemented by the clazz. A class copies can only be used
	 *                             through this interface.
	 * @param constructorSignature Constructor signature to be used
	 *                             when creating a new instance with {@link #newInstanceForKey}
	 */
	public ClassCopyProvider( final Class< ? extends T > clazz, final Class< T > interfaceOfClazz, final Class< ? >... constructorSignature )
	{
		this.copier = new ClassCopier<>( clazz, interfaceOfClazz );
		this.signature = ( constructorSignature == null || constructorSignature.length == 0 ) ? assumeConstructorSignature( clazz ) : constructorSignature;
	}

	private static Class< ? >[] assumeConstructorSignature( final Class< ? > clazz )
	{
		if ( hasDefaultConstructor( clazz ) )
			return new Class[ 0 ];
		final Constructor< ? >[] constructors = clazz.getConstructors();
		if ( constructors.length == 1 )
			return constructors[ 0 ].getParameterTypes();
		if ( constructors.length == 0 )
			throw new IllegalArgumentException( "ClassCopyProvider: Class and it's constructor need to be public (" + clazz.getName() + ")." );
		throw new IllegalArgumentException( "ClassCopyProvider: Please specify constructor signature." );
	}

	private static boolean hasDefaultConstructor( final Class< ? > clazz )
	{
		return ListUtils.anyMatch( constructor -> constructor.getParameterCount() == 0, clazz.getConstructors() );
	}

	private Class< ? extends T > classForKey( final Object key )
	{
		return map.computeIfAbsent( key, k -> copier.copy() );
	}

	/**
	 * Returns true if the given list of objects,
	 * matches the constructor signature provided to {@link #ClassCopyProvider(Class, Class, Class[])}.
	 */
	public boolean matches( final Object... parameters )
	{
		if ( parameters.length != signature.length )
			return false;
		for ( int i = 0; i < signature.length; i++ )
			if ( !signature[ i ].isInstance( parameters[ i ] ) )
				return false;
		return true;
	}

	/**
	 * Returns an instance of a copy of the original class, that has been provided to the constructor.
	 * <p>
	 * Note: The returned object isn't an instance of the original class.
	 * {@link Class#isInstance(Object)} and "instanceof" will return false.
	 * And ClassCastException might occur if this is ignored.
	 * <p>
	 * The returned instance has a copy of the byte code of the original class.
	 * This byte code is independently optimised by the JIT compiler.
	 * The JIT compiler will optimise the byte code, if it's only used in a specific use case.
	 * If used wisely a performance increase is the consequence.
	 *
	 * @param key        Instances created with the same key, share their byte code.
	 * @param parameters Parameters that are passed to the constructor.
	 */
	public T newInstanceForKey( final Object key, final Object... parameters )
	{
		try
		{
			return classForKey( key ).getConstructor( signature ).newInstance( parameters );
		}
		catch ( final Exception e )
		{
			throw new RuntimeException( e );
		}
	}
}
