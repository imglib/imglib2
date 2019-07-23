/*-
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2019 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MultiThreadSettingTest
{

	@Test
	public void testSingleThread()
	{
		MultiThreadSetting setting = MultiThreadSetting.SINGLE;
		assertEquals( 1, setting.suggestNumberOfTasks() );
	}

	@Test
	public void testSingeThreadExecuteAndWait()
	{
		MultiThreadSetting setting = MultiThreadSetting.SINGLE;
		assertEquals( 5050, sum( 1, 100, setting ) );
	}

	@Test
	public void testDefaultMultiThread()
	{
		MultiThreadSetting setting = MultiThreadSetting.MULTI;
		assertEquals( 5050, sum( 1, 100, setting ) );
	}

	@Test
	public void testNestedMultiThreading()
	{
		testNestedMultiThreading( MultiThreadSetting.SINGLE, MultiThreadSetting.SINGLE );
		testNestedMultiThreading( MultiThreadSetting.MULTI, MultiThreadSetting.SINGLE );
		testNestedMultiThreading( MultiThreadSetting.SINGLE, MultiThreadSetting.MULTI );
		testNestedMultiThreading( MultiThreadSetting.MULTI, MultiThreadSetting.MULTI );
	}

	private void testNestedMultiThreading( MultiThreadSetting setting, MultiThreadSetting subSetting )
	{
		AtomicLong sum = new AtomicLong();
		List< Integer > hundreds = IntStream.range( 0, 1000 ).mapToObj( x -> x * 100 ).collect( Collectors.toList() );
		setting.forEach( hundreds, offset -> sum.addAndGet( sum( offset + 1, offset + 100, subSetting ) ) );
		assertEquals( analyticalSum( 1, 1000 * 100 ), sum.get() );
	}

	private long analyticalSum( long from, long to )
	{
		return ( from + to ) * ( to - from + 1 ) / 2;
	}

	private long sum( int from, int to, MultiThreadSetting setting )
	{
		AtomicLong sum = new AtomicLong();
		List< Integer > numbers = IntStream.rangeClosed( from, to ).boxed().collect( Collectors.toList() );
		setting.forEach( numbers, value -> sum.addAndGet( value ) );
		return sum.get();
	}
}
