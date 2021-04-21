/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2021 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import static junit.framework.TestCase.assertNotSame;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Tests {@link ClassCopier}.
 *
 * @author Matthias Arzt
 */
public class ClassCopierTest
{

	@Test
	public void testExecuteCopy()
			throws IllegalAccessException, InstantiationException
	{
		final ClassCopier< MyInterface > copier = new ClassCopier<>( MyClass.class, MyInterface.class );
		final MyInterface copy = copier.copy().newInstance();
		assertEquals( "Hello World!", copy.toString() );
	}

	@Test
	public void testCopy() throws IllegalAccessException, InstantiationException
	{
		final Class< ? extends MyInterface > originalClass = MyClass.class;
		final ClassCopier< MyInterface > copier = new ClassCopier<>( originalClass, MyInterface.class );
		final Class< ? extends MyInterface > copiedClass = copier.copy();
		assertNotSame( originalClass, copiedClass );
	}

	@Test( expected = IllegalArgumentException.class )
	public void testCopyRequiresInterface()
			throws IllegalAccessException, InstantiationException
	{
		// NB: An instance of a copy of MyClass, cannot be assigned to MyClass.
		final MyClass copy = new ClassCopier<>( MyClass.class, MyClass.class ).copy().newInstance();
	}

	public static class MyClass implements MyInterface
	{

		@Override
		public String toString()
		{
			return "Hello World!";
		}
	}

	public interface MyInterface
	{

	}
}
