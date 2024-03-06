/*-
 * #%L
 * Mastodon Collections
 * %%
 * Copyright (C) 2015 - 2022 Tobias Pietzsch, Jean-Yves Tinevez
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
package net.imglib2.stream;

import java.io.PrintStream;

public class DebugHelpers
{
	public static void printStackTrace( String ... unlessContains )
	{
		printStackTrace( System.out, 3, -1, unlessContains );
	}

	public static void printStackTrace( int maxDepth, String ... unlessContains )
	{
		printStackTrace( System.out, 3, maxDepth, unlessContains );
	}

	public static void printStackTrace( PrintStream out, int maxDepth, String ... unlessContains )
	{
		printStackTrace( out, 3, maxDepth, unlessContains );
	}

	public static void printStackTrace( PrintStream out, int startDepth, int maxDepth, String ... unlessContains )
	{
		final StackTraceElement[] trace = Thread.currentThread().getStackTrace();

		for ( StackTraceElement element : trace )
		{
			final String traceLine = element.toString();
			for ( String template : unlessContains )
				if ( traceLine.contains( template ) )
					return;
		}

		final int len = ( maxDepth < 0 )
				? trace.length
				: Math.min( startDepth + maxDepth, trace.length );
		for ( int i = startDepth; i < len; ++i )
		{
			final String prefix = ( i == startDepth ) ? "" : "    at ";
			out.println( prefix + trace[ i ].toString() );
		}

		out.println();
	}
}
