/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
 * Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
 * Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
 * Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
 * Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
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

package net.imglib2.ops.pointset;

import java.util.Arrays;

import net.imglib2.ops.parse.PointSetParser;
import net.imglib2.ops.util.Tuple2;

/**
 * Class for defining complex PointSets from a text string. Syntax is similar
 * to list comprehension syntax in Haskell.
 * 
 * Some examples:
 * <p>
 * "x=[1..100], y=[200..400]"
 * <p>
 * "x=[1..100], y=[200..400], x + y > 250, x + y < 400"
 * <p>
 * "x=[1,4..20]" (this one results in values 1,4,7,10,13,16,19)
 * <p>
 * "x=[0..499], y=[0..399], (x-200)^2 + (y-200)^2 < (80)^2"
 * <p>
 * "x=[0..499], y=[0..399], angle(x,y) < (PI/6)"
 * 
 * @author Barry DeZonia
 *
 */
public class TextSpecifiedPointSet extends AbstractPointSet {

	// -- instance variables --
	
	private PointSet set;
	private String origInput;
	private String error;
	
	// -- constructor --
	
	public TextSpecifiedPointSet(String specification) {
		origInput = specification;
		error = null;
		set = construct(specification);
	}
	
	// -- PointSet methods --
	
	@Override
	public long[] getOrigin() {
		return set.getOrigin();
	}

	@Override
	public void translate(long[] deltas) {
		set.translate(deltas); // TODO - is this valid for a text pointset?
		invalidateBounds();
	}

	@Override
	public PointSetIterator iterator() {
		return set.iterator();
	}

	@Override
	public int numDimensions() {
		return set.numDimensions();
	}

	@Override
	protected long[] findBoundMin() {
		long[] min = new long[set.numDimensions()];
		set.min(min);
		return min;
	}

	@Override
	protected long[] findBoundMax() {
		long[] max = new long[set.numDimensions()];
		set.max(max);
		return max;
	}

	@Override
	public boolean includes(long[] point) {
		return set.includes(point);
	}

	@Override
	public long size() {
		return set.size();
	}

	@Override
	public TextSpecifiedPointSet copy() {
		return new TextSpecifiedPointSet(origInput);
	}

	public String getErrorString() {
		return error;
	}

	// -- private helpers --
	
	private PointSet construct(String spec) {
		PointSetParser parser = new PointSetParser();
		Tuple2<PointSet,String> results = parser.parse(spec);
		if (results.get2() != null) {
			error = results.get2();
			return new EmptyPointSet();
		}
		return results.get1();
	}

	// -- test stub code --
	
	public static void main(String[] args) {
		TextSpecifiedPointSet ps;
		PointSetIterator iter;

		/*
		// test all tokens
		String allTokens =
				" var = [ ] 01234 , 56789.12 .. mod < > <= >= == != of + - / * " +
						" within ( ) ^ E PI ||";
		HashMap<String, Integer> varMap = new HashMap<String, Integer>();
		ps = new TextSpecifiedPointSet("");
		List<SpecToken> tokens = ps.tokenize(allTokens, varMap);
		System.out.println("last token should be OR and is "+
				tokens.get(tokens.size()-1).getClass());
		*/
		
		// test some definitions
		//new TextSpecifiedPointSet("");
		ps = new TextSpecifiedPointSet("x = [5]");
		System.out.println("result = " +
				(ps.getErrorString() == null ? "OK" : ps.getErrorString()));
		ps = new TextSpecifiedPointSet("x = [5], y=[1..10]");
		System.out.println("result = " +
				(ps.getErrorString() == null ? "OK" : ps.getErrorString()));
		ps = new TextSpecifiedPointSet("x= [1..2], y=[ 1, 3 .. 10 ]");
		System.out.println("result = " +
				(ps.getErrorString() == null ? "OK" : ps.getErrorString()));
		ps = new TextSpecifiedPointSet("xx =[1,4..12] ,yy = [1..5] , xx + yy <= 12");
		System.out.println("result = " +
				(ps.getErrorString() == null ? "OK" : ps.getErrorString()));
		ps = new TextSpecifiedPointSet("x = [1,7,9], y=[1,4,5]");
		System.out.println("result = " +
				(ps.getErrorString() == null ? "OK" : ps.getErrorString()));
		
		// iterate some definitions
		System.out.println("Iterate x = [5]");
		ps = new TextSpecifiedPointSet("x = [5]");
		iter = ps.iterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}
		
		System.out.println("Iterate x = [1..3], y = [3..5]");
		ps = new TextSpecifiedPointSet("x = [1..3], y = [3..5]");
		iter = ps.iterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x = [1,3..7], y = [5,8..14]");
		ps = new TextSpecifiedPointSet("x = [1,3..7], y = [5,8..14]");
		iter = ps.iterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x = [1,4,9], y = [2,3,5,8]");
		ps = new TextSpecifiedPointSet("x = [1,4,9], y = [2,3,5,8]");
		iter = ps.iterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x = [-2..2], y = [-1,1..5]");
		ps = new TextSpecifiedPointSet("x = [-2..2], y = [-1,1..5]");
		iter = ps.iterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}
		
		System.out.println("Iterate x=[1..5],y=[1,5..50]");
		ps = new TextSpecifiedPointSet("x=[1..5],y=[1,5..50]");
		iter = ps.iterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x=[1..20],y=[1..20],x+y < 7");
		ps = new TextSpecifiedPointSet("x=[1..20],y=[1..20],x+y < 7");
		if (ps.getErrorString() != null)
			System.out.println(ps.getErrorString());
		else {
			iter = ps.iterator();
			while (iter.hasNext()) {
				long[] next = iter.next();
				System.out.println(" " + Arrays.toString(next));
			}
		}

		System.out.println("Iterate x=[-5..5],y=[-5..5],x^2+y^2 <= 3");
		ps = new TextSpecifiedPointSet("x=[-5..5],y=[-5..5],x^2+y^2 <= 3");
		if (ps.getErrorString() != null)
			System.out.println(ps.getErrorString());
		else {
			iter = ps.iterator();
			while (iter.hasNext()) {
				long[] next = iter.next();
				System.out.println(" " + Arrays.toString(next));
			}
		}
	}
}
