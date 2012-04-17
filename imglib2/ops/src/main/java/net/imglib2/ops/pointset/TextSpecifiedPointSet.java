/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
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
 * 
 * The views and conclusions contained in the software and documentation are
 * those of the authors and should not be interpreted as representing official
 * policies, either expressed or implied, of any organization.
 * #L%
 */


package net.imglib2.ops.pointset;

// TODO
//   support equational restrictions

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imglib2.ops.Condition;
import net.imglib2.ops.PointSet;
import net.imglib2.ops.PointSetIterator;
import net.imglib2.ops.condition.AndCondition;
import net.imglib2.ops.condition.OrCondition;

/**
 * Class for defining complex PointSets from a text string. Syntax is similar
 * to list comprehension syntax in Haskell.
 * 
 * @author Barry DeZonia
 *
 */
public class TextSpecifiedPointSet implements PointSet {

	private PointSet set;
	private String origInput;
	
	public TextSpecifiedPointSet(String specification) {
		origInput = specification;
		set = construct(specification);
	}
	
	@Override
	public long[] getAnchor() {
		return set.getAnchor();
	}

	@Override
	public void setAnchor(long[] anchor) {
		set.setAnchor(anchor);
	}

	@Override
	public PointSetIterator createIterator() {
		return set.createIterator();
	}

	@Override
	public int numDimensions() {
		return set.numDimensions();
	}

	@Override
	public long[] findBoundMin() {
		return set.findBoundMin();
	}

	@Override
	public long[] findBoundMax() {
		return set.findBoundMax();
	}

	@Override
	public boolean includes(long[] point) {
		return set.includes(point);
	}

	@Override
	public long calcSize() {
		return set.calcSize();
	}

	@Override
	public TextSpecifiedPointSet copy() {
		return new TextSpecifiedPointSet(origInput);
	}

	private PointSet construct(String spec) {
		HashMap<String,Integer> varMap = new HashMap<String, Integer>();
		List<SpecToken> tokens = tokenize(spec, varMap);
		if (tokens.size() != 0) {
			List<PointSetSpecification> specs = parse(tokens, varMap);
			if (specs.size() != 0)
				return build(specs);
		}
		return new EmptyPointSet();
	}
	
	List<SpecToken> tokenize(String spec, Map<String,Integer> varMap) {
		// maybe the tokenizer could recognize sub equations and create
		// the equational functions via string constructors
		
		List<SpecToken> tokens = new ArrayList<SpecToken>();
		char[] chars = spec.toCharArray();
		int i = 0;
		while (i < chars.length) {
			Character ch = chars[i];
			if (Character.isLetter(ch)) {
				String name = "";
				while (i < chars.length && Character.isLetter(chars[i])) {
					name += chars[i];
					i++;
				}
				SpecToken token = reservedWordLookup(name);
				if (token != null)
					tokens.add(token);
				else
					tokens.add(new VariableName(name, varMap));
			}
			else if (ch == '<') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new LessEquals());
				}
				else
					tokens.add(new Less());
			}
			else if (ch == '>') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new GreaterEquals());
				}
				else
					tokens.add(new Greater());
			}
			else if (ch == '!') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new NotEquals());
				}
				else
					invalidLexResult(spec, i-1, ch);
			}
			else if (ch == '=') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new Equals());
				}
				else
					tokens.add(new Assign());
			}
			else if (ch == '.') {
				i++;
				if (i < chars.length && chars[i] == '.') {
					i++;
					tokens.add(new DotDot());
				}
				else
					invalidLexResult(spec, i-1, ch);
			}
			else if (ch == ',') {
				i++;
				tokens.add(new Comma());
			}
			else if (ch == '[') {
				i++;
				tokens.add(new OpenRange());
			}
			else if (ch == ']') {
				i++;
				tokens.add(new CloseRange());
			}
			else if (ch == '+') {
				i++;
				tokens.add(new Plus());
			}
			else if (ch == '*') {
				i++;
				tokens.add(new Times());
			}
			else if (ch == '/') {
				i++;
				tokens.add(new Divide());
			}
			else if (ch == '-') {
				i++;
				if (i < chars.length && Character.isDigit(chars[i])) {
					String numStr = "-";
					boolean isReal = false;
					while (i < chars.length && (Character.isDigit(chars[i]))) {
						numStr += chars[i];
						i++;
						char next = (i < chars.length) ? chars[i] : 0;
						char next2 = (i < chars.length-1) ? chars[i+1] : 0;
						if ((next == '.') && (next2 == '.')) break;
						if (next == '.') {
							if (isReal) // already seen a decimal
								invalidLexResult(spec, i, chars[i]);
							// else valid decimal
							isReal = true;
							numStr += ".";
							i++;
						}
					}
					if (isReal)
						tokens.add(new Real(numStr));
					else
						tokens.add(new Int(numStr));
				}
				else
					tokens.add(new Minus());
			}
			else if (Character.isDigit(ch)) {
				String numStr = "";
				boolean isReal = false;
				while (i < chars.length && (Character.isDigit(chars[i]))) {
					numStr += chars[i];
					i++;
					char next = (i < chars.length) ? chars[i] : 0;
					char next2 = (i < chars.length-1) ? chars[i+1] : 0;
					if ((next == '.') && (next2 == '.')) break;
					if (next == '.') {
						if (isReal) // already seen a decimal
							invalidLexResult(spec, i, chars[i]);
						// else valid decimal
						isReal = true;
						numStr += ".";
						i++;
					}
				}
				if (isReal)
					tokens.add(new Real(numStr));
				else
					tokens.add(new Int(numStr));
			}
			else if (ch == '^') {
				i++;
				tokens.add(new Exponent());
			}
			else if (ch == '(') {
				i++;
				tokens.add(new LeftParen());
			}
			else if (ch == ')') {
				i++;
				tokens.add(new RightParen());
			}
			else if (ch == '|') {
				if ((i < chars.length-1) && (chars[i+1] == '|')) {
					i += 2;
					tokens.add(new Or());
				}
				else {
					invalidLexResult(spec, i, chars[i]);
				}
			}
			else if (Character.isWhitespace(ch))
				i++;
			else { // invalid char
				invalidLexResult(spec, i, ch);
			}
		}
		return tokens;
	}

	private SpecToken reservedWordLookup(String name) {
		if (name.equals("E")) return new Real(Math.E);
		if (name.equals("PI")) return new Real(Math.PI);
		if (name.equals("mod")) return new Mod();
		if (name.equals("of")) return new Of();
		if (name.equals("within")) return new Within();
		return null;
	}
	
	private void invalidLexResult(String input, int pos, Character ch) {
		throw new IllegalArgumentException(
			"Invalid char ("+ch+") at position ("+pos+") of string ("+input+")");
	}
	
	private interface SpecToken {
	}
	
	private class VariableName implements SpecToken {
		final String name;
		VariableName(String name, Map<String,Integer> varMap) {
			this.name = name;
			Integer i = varMap.get(name);
			if (i == null) {
				// note will range from -1 to -numVars
				//   negative means it is an unbound variable
				//   axis assignment parsing binds variables to axes 0..n-1
				//   later lookup will determine if the variable is bound or not.
				//   if it is bound then axis num = looked up int
				varMap.put(name, -(varMap.size()+1));
			}
		}
	}
	
	private class Assign implements SpecToken {
	}

	private class OpenRange implements SpecToken {
	}
	
	private class CloseRange implements SpecToken {
	}

	private class Comma implements SpecToken {
	}

	private class DotDot implements SpecToken {
	}

	private class Int implements SpecToken {
		final long value;
		Int(String num) {
			value = Long.parseLong(num);
		}
		Int(long val) {
			value = val;
		}
	}
	
	private class Real implements SpecToken {
		final double value;
		Real(String num) {
			value = Double.parseDouble(num);
		}
		Real(double num) {
			value = num;
		}
	}
	
	private class Plus implements SpecToken {
	}

	private class Minus implements SpecToken {
	}

	private class Times implements SpecToken {
	}

	private class Divide implements SpecToken {
	}

	private class Mod implements SpecToken {
	}

	private class Equals implements SpecToken {
	}

	private class NotEquals implements SpecToken {
	}

	private class Greater implements SpecToken {
	}

	private class GreaterEquals implements SpecToken {
	}

	private class Less implements SpecToken {
	}

	private class LessEquals implements SpecToken {
	}

	private class Within implements SpecToken {
	}
	
	private class Of implements SpecToken {
	}
	
	private class LeftParen implements SpecToken {
	}
	
	private class RightParen implements SpecToken {
	}
	
	private class Exponent implements SpecToken {
	}
	
	private class Or implements SpecToken {
	}
	
	private List<PointSetSpecification> parse(
		List<SpecToken> tokens, Map<String,Integer> varMap)
	{
		ArrayList<Long> dims = new ArrayList<Long>();
		ArrayList<PointSetSpecification> restrictions =
				new ArrayList<PointSetSpecification>();
		int pos = dimension(tokens, 0, varMap, dims, restrictions);
		pos = moreDimensions(tokens, pos, varMap, dims, restrictions);
		pos = restrictions(tokens, pos, restrictions);
		ArrayList<PointSetSpecification> list = 
				new ArrayList<PointSetSpecification>();
		long[] minPt = new long[dims.size()/2];
		long[] maxPt = new long[dims.size()/2];
		for (int i = 0; i < dims.size(); i += 2) {
			minPt[i/2] = dims.get(i);
			maxPt[i/2] = dims.get(i+1);
		}
		list.add(new HyperVolumeSpecification(minPt, maxPt));
		list.addAll(restrictions);
		return list;
	}

	private boolean match(List<SpecToken> tokens, int pos, Class<?> clazz) {
		if (pos >= tokens.size()) return false;
		return tokens.get(pos).getClass() == clazz;
	}
	
	private int dimension(
		List<SpecToken> tokens, int pos, Map<String,Integer> varMap,
		List<Long> dims, List<PointSetSpecification> restrictions)
	{
		// x = [
		if (match(tokens, pos, VariableName.class) &&
			match(tokens, pos+1, Assign.class) &&
			match(tokens, pos+2, OpenRange.class)) {
			
			 // Int ]
			if (match(tokens, pos+3, Int.class) &&
				match(tokens, pos+4, CloseRange.class))
			{
				Int i = (Int) tokens.get(pos+3);
				dims .add(i.value);	
				dims .add(i.value);
				VariableName var = (VariableName) tokens.get(pos);
				int dimIndex = varMap.get(var.name);
				if (dimIndex >= 0)
					throw new IllegalArgumentException(
						"cannot declare dimension ("+var.name+") more than once");
				varMap.put(var.name, (-dimIndex)-1);  // mark bound
				//System.out.println("parsed a [x] case");
				return pos+5;
			}
			
			// Int .. Int ]
			if (match(tokens, pos+3, Int.class) &&
					match(tokens, pos+4, DotDot.class) &&
					match(tokens, pos+5, Int.class) &&
					match(tokens, pos+6, CloseRange.class))
			{
				Int min = (Int) tokens.get(pos+3);
				Int max = (Int) tokens.get(pos+5);
				if (min.value > max.value)
					throw new IllegalArgumentException(
						"dimension range error: first dim ("+min+
						") must be less than or equal to last dim ("+max+")");
				dims.add(min.value);	
				dims.add(max.value);
				VariableName var = (VariableName) tokens.get(pos);
				int dimIndex = varMap.get(var.name);
				if (dimIndex >= 0)
					throw new IllegalArgumentException(
						"cannot declare dimension ("+var.name+") more than once");
				varMap.put(var.name, (-dimIndex)-1);  // mark bound
				//System.out.println("parsed a [x..y] case");
				return pos + 7;
			}
			
			// Int , Int .. Int]
			if (match(tokens, pos+3, Int.class) &&
					match(tokens, pos+4, Comma.class) &&
					match(tokens, pos+5, Int.class) &&
					match(tokens, pos+6, DotDot.class) &&
					match(tokens, pos+7, Int.class) &&
					match(tokens, pos+8, CloseRange.class))
			{
				Int min = (Int) tokens.get(pos+3);
				Int next = (Int) tokens.get(pos+5);
				Int max = (Int) tokens.get(pos+7);
				if (min.value >= next.value)
					throw new IllegalArgumentException(
						"dimension range error: first number ("+min+
						") must be less than second number ("+next+")");
				if (min.value > max.value)
					throw new IllegalArgumentException(
						"dimension range error: first number ("+min+
						") must be less than or equal to last number ("+max+")");
				long first = min.value;
				long by = next.value - min.value;
				long prev = min.value;
				long last = min.value;
				while (last < max.value) {
					prev = last;
					last += by;
				}
				if (last > max.value)
					last = prev;
				restrictions.add(
					new DimensionStepRestrictionSpecification(
						dims.size()/2, first, last, by));
				dims.add(first);	
				dims.add(last);
				VariableName var = (VariableName) tokens.get(pos);
				int dimIndex = varMap.get(var.name);
				if (dimIndex >= 0)
					throw new IllegalArgumentException(
						"cannot declare dimension ("+var.name+") more than once");
				varMap.put(var.name, (-dimIndex)-1);  // mark bound
				//System.out.println("parsed a [x,y..z] case");
				return pos + 9;
			}
			
			if (match(tokens, pos+3, Int.class) &&
					match(tokens, pos+4, Comma.class) &&
					match(tokens, pos+5, Int.class))
			{
				List<Long> values = new ArrayList<Long>();
				int p = pos + 3;
				while (match(tokens, p, Int.class)) {
					Int i = (Int) tokens.get(p);
					values.add(i.value);
					p++;
					if (match(tokens, p, CloseRange.class)) break;
					if (match(tokens, p, Comma.class)) {
						p++;
					}
					else
						throw new IllegalArgumentException(
							"parse error: dimension list specification" +
							" expected , or ] but got "+tokens.get(p).getClass());
				}
				if (match(tokens, p, CloseRange.class)) {
					long minDim = Long.MAX_VALUE;
					long maxDim = Long.MIN_VALUE;
					for (long dim : values) {
						minDim = Math.min(dim, minDim);
						maxDim = Math.max(dim, maxDim);
					}
					restrictions.add(
						new DimensionListRestrictionSpecification(dims.size()/2, values));
					dims.add(minDim);
					dims.add(maxDim);
					VariableName var = (VariableName) tokens.get(pos);
					int dimIndex = varMap.get(var.name);
					if (dimIndex >= 0)
						throw new IllegalArgumentException("cannot declare dimension ("+
								var.name+") more than once");
					varMap.put(var.name, (-dimIndex)-1);  // mark bound
					//System.out.println("parsed a [x, y, ...] case");
					return p+1;
				}
			}
			throw new IllegalArgumentException(
				"parse error: dimension list specification should end in ]");
		}
		return pos;
	}
	
	private int moreDimensions(
		List<SpecToken> tokens, int pos, Map<String,Integer> varMap,
		List<Long> dims, List<PointSetSpecification> restrictions)
	{
		if (pos >= tokens.size()) return pos;
		if (match(tokens, pos, Comma.class) &&
				match(tokens, pos+1, VariableName.class) &&
				match(tokens, pos+2, Assign.class))
		{
			pos = dimension(tokens, pos+1, varMap, dims, restrictions);
			return moreDimensions(tokens, pos, varMap, dims, restrictions);
		}
		return pos;
	}
	
	private int restrictions(
		List<SpecToken> tokens, int pos, List<PointSetSpecification> restrictions)
	{
		if (pos >= tokens.size()) return pos;
		// TODO
		return tokens.size();
	}
	
	private interface PointSetSpecification {
	}

	private class HyperVolumeSpecification implements PointSetSpecification {
		private final long[] minPt, maxPt;
		
		public HyperVolumeSpecification(long[] min, long[] max) {
			minPt = min;
			maxPt = max;
		}
		
		public long[] getMinPt() { return minPt; }
		
		public long[] getMaxPt() { return maxPt; }
	}
	
	private class DimensionStepRestrictionSpecification
		implements PointSetSpecification
	{
		final int dimIndex;
		final long first;
		final long last;
		final long step;
		
		public DimensionStepRestrictionSpecification(
			int dimIndex, long first, long last, long step)
		{
			this.dimIndex = dimIndex;
			this.first = first;
			this.last = last;
			this.step = step;
		}
	}
	
	private class DimensionListRestrictionSpecification
		implements PointSetSpecification
	{
		final int dimIndex;
		final List<Long> values;
		
		public DimensionListRestrictionSpecification(
			int dimIndex, List<Long> values)
		{
			this.dimIndex = dimIndex;
			this.values = values;
		}
	}
	
	// if here then we have a list of specs with enclosing hyper volume
	// first and restrictions following
	
	private PointSet build(List<PointSetSpecification> specs) {
		final HyperVolumeSpecification spec = (HyperVolumeSpecification) specs.get(0);
		PointSet ps = new HyperVolumePointSet(spec.getMinPt(), spec.getMaxPt());
		for (int i = 1; i < specs.size(); i++) {
			final Condition<long[]> condition = makeCondition(specs.get(i));
			ps = new ConditionalPointSet(ps, condition);
		}
		return ps;
	}
	
	private class RangeCondition implements Condition<long[]> {
		final int dimIndex;
		final long first;
		final long last;
		final long step;
		
		RangeCondition(int dimIndex, long first, long last, long step) {
			this.dimIndex = dimIndex;
			this.first = first;
			this.last = last;
			this.step = step;
		}

		@Override
		public boolean isTrue(long[] val) {
			long value = val[dimIndex];
			if (value < first) return false;
			if (value > last) return false;
			return (value - first) % step == 0;
		}

		@Override
		public RangeCondition copy() {
			return new RangeCondition(dimIndex, first, last, step);
		}
	}
	
	private Condition<long[]> makeCondition(PointSetSpecification spec) {
		if (spec instanceof HyperVolumeSpecification)
			throw new IllegalArgumentException(
				"more than one hyper volume spec present");
		if (spec instanceof DimensionStepRestrictionSpecification) {
			DimensionStepRestrictionSpecification s =
					(DimensionStepRestrictionSpecification) spec;
			return new RangeCondition(s.dimIndex, s.first, s.last, s.step);
		}
		if (spec instanceof DimensionListRestrictionSpecification) {
			DimensionListRestrictionSpecification dSpec =
				(DimensionListRestrictionSpecification) spec;
			return dimListRestriction(dSpec.dimIndex, dSpec.values);
		}
		throw new IllegalArgumentException(
			"unknown restriction specification ("+spec.getClass()+")");
	}
	
	private class UnionCondition<T> implements Condition<T> {
		private final Condition<T> condition;
		
		public UnionCondition(List<Condition<T>> conditions) {
			if (conditions.size() == 0)
				throw new IllegalArgumentException("no conditions provided");
			else if (conditions.size() == 1)
				condition = conditions.get(0);
			else {
				OrCondition<T> or =
						new OrCondition<T>(conditions.get(0), conditions.get(1));
				for (int i = 2; i < conditions.size(); i++)
					or = new OrCondition<T>(or, conditions.get(i));
				condition = or;
			}
		}

		@Override
		public boolean isTrue(T val) {
			return condition.isTrue(val);
		}

		@Override
		public UnionCondition<T> copy() {
			final List<Condition<T>> conditions = new ArrayList<Condition<T>>();
			conditions.add(condition.copy());
			return new UnionCondition<T>(conditions);
		}
	}
	
	private class IntersectionCondition<T> implements Condition<T> {
		private final Condition<T> condition;
		
		public IntersectionCondition(List<Condition<T>> conditions) {
			if (conditions.size() == 0)
				throw new IllegalArgumentException("no conditions provided");
			else if (conditions.size() == 1)
				condition = conditions.get(0);
			else {
				AndCondition<T> and =
						new AndCondition<T>(conditions.get(0), conditions.get(1));
				for (int i = 2; i < conditions.size(); i++)
					and = new AndCondition<T>(and, conditions.get(i));
				condition = and;
			}
		}

		@Override
		public boolean isTrue(T val) {
			return condition.isTrue(val);
		}

		@Override
		public IntersectionCondition<T> copy() {
			final List<Condition<T>> conditions = new ArrayList<Condition<T>>();
			conditions.add(condition.copy());
			return new IntersectionCondition<T>(conditions);
		}
	}
	
	private UnionCondition<long[]> dimListRestriction(int dimIndex, List<Long> values) {
		final List<Condition<long[]>> dimensionConditions =
				new ArrayList<Condition<long[]>>();
		for (long value : values) {
			final Condition<long[]> cond = new DimensionEqualCondition(dimIndex, value);
			dimensionConditions.add(cond);
		}
		return new UnionCondition<long[]>(dimensionConditions);
	}
	
	private class DimensionEqualCondition implements Condition<long[]> {
		final int dimIndex;
		final long value;
		
		public DimensionEqualCondition(int dimIndex, long value) {
			this.dimIndex = dimIndex;
			this.value = value;
		}

		@Override
		public boolean isTrue(long[] index) {
			return index[dimIndex] == value;
		}

		@Override
		public DimensionEqualCondition copy() {
			return new DimensionEqualCondition(dimIndex, value);
		}
	}
	
	public static void main(String[] args) {
		TextSpecifiedPointSet ps;
		PointSetIterator iter;

		// test all tokens
		String allTokens =
				" var = [ ] 01234 , 56789.12 .. mod < > <= >= == != of + - / * " +
						" within ( ) ^ E PI ||";
		HashMap<String, Integer> varMap = new HashMap<String, Integer>();
		ps = new TextSpecifiedPointSet("");
		List<SpecToken> tokens = ps.tokenize(allTokens, varMap);
		System.out.println("last token should be OR and is "+
				tokens.get(tokens.size()-1).getClass());
		
		// test some definitions
		new TextSpecifiedPointSet("");
		new TextSpecifiedPointSet("x = [5]");
		new TextSpecifiedPointSet("x = [5], y=[1..10]");
		new TextSpecifiedPointSet("x= [1..2], y=[ 1, 3 .. 10 ]");
		new TextSpecifiedPointSet("xx =[1,4..12] ,yy = [1..5] , xx + yy <= 12");
		new TextSpecifiedPointSet("x = [1,7,9], y=[1,4,5]");
		
		// iterate some definitions
		System.out.println("Iterate x = [5]");
		ps = new TextSpecifiedPointSet("x = [5]");
		iter = ps.createIterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}
		
		System.out.println("Iterate x = [1..3], y = [3..5]");
		ps = new TextSpecifiedPointSet("x = [1..3], y = [3..5]");
		iter = ps.createIterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x = [1,3..7], y = [5,8..14]");
		ps = new TextSpecifiedPointSet("x = [1,3..7], y = [5,8..14]");
		iter = ps.createIterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x = [1,4,9], y = [2,3,5,8]");
		ps = new TextSpecifiedPointSet("x = [1,4,9], y = [2,3,5,8]");
		iter = ps.createIterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}

		System.out.println("Iterate x = [-2..2], y = [-1,1..5]");
		ps = new TextSpecifiedPointSet("x = [-2..2], y = [-1,1..5]");
		iter = ps.createIterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}
		System.out.println("Iterate x=[1..5],y=[1,5..50]");
		ps = new TextSpecifiedPointSet("x=[1..5],y=[1,5..50]");
		iter = ps.createIterator();
		while (iter.hasNext()) {
			long[] next = iter.next();
			System.out.println(" " + Arrays.toString(next));
		}
	}
}
