/**
 * Copyright (c) 2009--2012, ImgLib2 developers
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Tobias Pietzsch
 */
package net.imglib2.img.sparse;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @author Tobias Pietzsch
 *
 */
public class NtreeTest
{
	@Test
	public void test_getNode()
	{
		final int v = 10;
		final Ntree< Integer > t = new Ntree< Integer >( new long[]{ 256, 257, 100 }, v );
		final long[] pos = new long[] {27, 38, 99};
		final Ntree.NtreeNode< Integer > n = t.getNode( pos );

		assertTrue( n != null );
		assertTrue( n.getValue() == v );
	}

	@Test
	public void test_createNote()
	{
		final int v = 10;
		final Ntree< Integer > t = new Ntree< Integer >( new long[]{ 256, 257, 100 }, v );
		final long[] pos = new long[] {27, 38, 99};
		final Ntree.NtreeNode< Integer > n = t.createNode( pos );

		assertTrue( n != null );
		assertTrue( n.getValue() == v );

		n.setValue( v + 1 );
		final long[] pos2 = new long[] {28, 38, 99};
		final Ntree.NtreeNode< Integer > n2 = t.getNode( pos2 );

		assertTrue( n2 != null );
		assertTrue( n2.getValue() == v );
	}

	@Test
	public void test_mergeUpwards()
	{
		final int v = 10;
		final Ntree< Integer > t = new Ntree< Integer >( new long[]{ 256, 257, 100 }, v );
		final long[] pos = new long[] {27, 38, 99};
		t.createNode( pos ).setValue( v + 1 );

		final Ntree.NtreeNode< Integer > n = t.getNode( pos );
		assertTrue( n != null );
		assertTrue( n.getValue() == v + 1 );
		assertTrue( t.root.hasChildren() );

		n.setValue( v );
		t.mergeUpwards( n );

		assertFalse( t.root.hasChildren() );
	}
}
