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

package net.imglib2.ops.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imglib2.ops.condition.AndCondition;
import net.imglib2.ops.condition.BinaryFunctionalCondition;
import net.imglib2.ops.condition.Condition;
import net.imglib2.ops.condition.DimensionEqualCondition;
import net.imglib2.ops.condition.NotCondition;
import net.imglib2.ops.condition.OrCondition;
import net.imglib2.ops.condition.RangeCondition;
import net.imglib2.ops.condition.UnionCondition;
import net.imglib2.ops.condition.XorCondition;
import net.imglib2.ops.parse.token.And;
import net.imglib2.ops.parse.token.Assign;
import net.imglib2.ops.parse.token.CloseRange;
import net.imglib2.ops.parse.token.Comma;
import net.imglib2.ops.parse.token.DotDot;
import net.imglib2.ops.parse.token.Equal;
import net.imglib2.ops.parse.token.Greater;
import net.imglib2.ops.parse.token.GreaterEqual;
import net.imglib2.ops.parse.token.Int;
import net.imglib2.ops.parse.token.Less;
import net.imglib2.ops.parse.token.LessEqual;
import net.imglib2.ops.parse.token.Minus;
import net.imglib2.ops.parse.token.Not;
import net.imglib2.ops.parse.token.NotEqual;
import net.imglib2.ops.parse.token.OpenRange;
import net.imglib2.ops.parse.token.Or;
import net.imglib2.ops.parse.token.Plus;
import net.imglib2.ops.parse.token.Token;
import net.imglib2.ops.parse.token.Variable;
import net.imglib2.ops.parse.token.Xor;
import net.imglib2.ops.pointset.ConditionalPointSet;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.pointset.PointSet;
import net.imglib2.ops.relation.real.binary.RealEquals;
import net.imglib2.ops.relation.real.binary.RealGreaterThan;
import net.imglib2.ops.relation.real.binary.RealGreaterThanOrEqual;
import net.imglib2.ops.relation.real.binary.RealLessThan;
import net.imglib2.ops.relation.real.binary.RealLessThanOrEqual;
import net.imglib2.ops.relation.real.binary.RealNotEquals;
import net.imglib2.ops.util.Tuple2;
import net.imglib2.type.numeric.real.DoubleType;

/*
Grammar for list comprehension like string input

statement =
 dimensions |
 dimensions, restrictions

dimensions =
 dimension |
 dimension, dimensions
   (the decision to terminate is based on if 1st token after comma is not a variable)

dimension =
 identifier “=” “[“ values “]”

identifier =
 [a-zA-Z]+

values =
 range |
 intList (if all just ints and commas)

range =
 int “..” int |
 int “,” int “..” int

intList =
 int |
 int “,” intList

int =
 [0-9]+ |
 sign [0-9]+

sign =
 “+” | “-”

restrictions =
 compoundBoolExpression |
 compoundBoolExpression , restrictions

compoundBoolExpression =
 boolClause |
 boolClause “and” compoundBoolExpression
 boolClause “or” compoundBoolExpression
 boolClause “xor” compoundBoolExpression

boolClause =
 expression |
 “not” expression

expression =
 equation relop equation

relop = “<” | “<=” | “>” | “>=” | “==” | “!=”
 (in java the last two have higher precedence than first four. maybe can ignore)

equation = (see EquationParser)
 
*/

/**
 * Parses a point set language string and attempts to build a PointSet that
 * matches the string specification. The language is documented at:
 * http://wiki.imagej.net/ImageJ2/Documentation/PointSetDemo
 * 
 * @author Barry DeZonia
 *
 */
public class PointSetParser {

	private Map<String, Integer> varMap;
	private List<Long> minDims;
	private List<Long> maxDims;
	private List<Condition<long[]>> conditions;
	private EquationParser eqnParser;
	
	public Tuple2<PointSet,String> parse(String specification) {
		minDims = new ArrayList<Long>();
		maxDims = new ArrayList<Long>();
		conditions = new ArrayList<Condition<long[]>>();
		varMap = new HashMap<String,Integer>();
		eqnParser = new EquationParser(varMap,null);
		Lexer lexer = new Lexer();
		ParseStatus lexResult = lexer.tokenize(specification, varMap);
		if (lexResult.errMsg != null) {
			return new Tuple2<PointSet, String>(lexResult.pointSet,lexResult.errMsg);
		}
		ParseStatus parseResult = constructPointSet(lexResult.tokens);
		return new Tuple2<PointSet,String>(parseResult.pointSet, parseResult.errMsg);
	}
	
	private ParseStatus constructPointSet(List<Token> tokens) {
		return statement(tokens);
	}
	
	/*
	statement =
			 dimensions |
			 dimensions, restrictions
	*/
	private ParseStatus statement(List<Token> tokens) {
		ParseStatus status = dimensions(tokens, 0);
		if (status.errMsg != null) return status;
		long[] minPt = new long[minDims.size()];
		long[] maxPt = new long[maxDims.size()];
		for (int i = 0; i < minDims.size(); i++) {
			minPt[i] = minDims.get(i);
			maxPt[i] = maxDims.get(i);
		}
		PointSet ps = new HyperVolumePointSet(minPt, maxPt);
		if (ParseUtils.match(Comma.class, tokens, status.tokenNumber)) {
			status = restrictions(tokens, status.tokenNumber+1);
			if (status.errMsg != null) return status;
		}
		for (Condition<long[]> condition : conditions) {
			ps = new ConditionalPointSet(ps, condition);
		}
		status.pointSet = ps;
		return status;
	}

	/*
	dimensions =
			 dimension |
			 dimension, dimensions
			   (the decision to terminate is based on if 1st token after comma is not a variable)
	*/
	private ParseStatus dimensions(List<Token> tokens, int currPos) {
		ParseStatus status = dimension(tokens, currPos);
		if (status.errMsg != null) return status;
		if (ParseUtils.match(Comma.class, tokens, status.tokenNumber)) {
			if (ParseUtils.match(Variable.class, tokens, status.tokenNumber+1))
				if (ParseUtils.match(Assign.class, tokens, status.tokenNumber+2))
					return dimensions(tokens, status.tokenNumber+1);
		}
		return status;
	}

	/*
	dimension =
			 identifier “=” “[“ values “]”
	*/
	private ParseStatus dimension(List<Token> tokens, int pos) {
		if (!ParseUtils.match(Variable.class, tokens, pos))
			return ParseUtils.syntaxError(pos, tokens,
					"Expected a variable name.");
		if (!ParseUtils.match(Assign.class, tokens, pos+1))
			return ParseUtils.syntaxError(pos+1, tokens,
					"Expected an assignment symbol '='.");
		if (!ParseUtils.match(OpenRange.class, tokens, pos+2))
			return ParseUtils.syntaxError(pos+2, tokens,
					"Expected an open bracket symbol '['.");
		ParseStatus valsStatus = values(tokens, pos+3);
		if (valsStatus.errMsg != null) return valsStatus;
		if (!ParseUtils.match(CloseRange.class, tokens, valsStatus.tokenNumber))
			return ParseUtils.syntaxError(valsStatus.tokenNumber, tokens,
					"Expected a close bracket symbol ']'.");

		Variable var = (Variable) tokens.get(pos);
		int dimIndex = varMap.get(var.getText());
		if (dimIndex >= 0)
			return ParseUtils.syntaxError(pos, tokens,
				"Cannot declare dimension ("+var.getText()+") more than once");
		varMap.put(var.getText(), (-dimIndex)-1);  // mark variable as bound
		
		minDims.add(valsStatus.minDim);
		maxDims.add(valsStatus.maxDim);
		
		return ParseUtils.nextPosition(valsStatus.tokenNumber+1);
	}

	/*
	values =
			 range |
			 intList (if all just ints and commas)
	*/
	private ParseStatus values(List<Token> tokens, int pos) {
		if (intsAndCommas(tokens, pos)) {
			ArrayList<Long> dims = new ArrayList<Long>();
			ParseStatus status = intList(tokens, pos, dims);
			if (status.errMsg != null) return status;
			conditions.add(dimListRestriction(minDims.size(), dims));
			return status;
		}
		return range(tokens, pos);
	}

	/*
	range =
			 int “..” int |
			 int “,” int “..” int
	*/
	private ParseStatus range(List<Token> tokens, int pos) {
		ParseStatus status1 = integer(tokens, pos);
		if (status1.errMsg != null) return status1;
		if (ParseUtils.match(DotDot.class, tokens, status1.tokenNumber)) {
			ParseStatus status2 = integer(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status1;
			long first = status1.minDim;
			long second = status2.minDim;
			if (first > second)
				return ParseUtils.syntaxError(
						status2.tokenNumber, tokens,
						"In a range the first value must be <= last value.");
			ParseStatus status = new ParseStatus();
			conditions.add(
					new RangeCondition(minDims.size(), first, second, 1));
			status.minDim = first;
			status.maxDim = second;
			status.tokenNumber = status2.tokenNumber;
			return status;
		}
		else if (ParseUtils.match(Comma.class, tokens, status1.tokenNumber)) {
			ParseStatus status2 = integer(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			if (!ParseUtils.match(DotDot.class, tokens, status2.tokenNumber))
				return ParseUtils.syntaxError(
						status2.tokenNumber, tokens,
						"Expected '..' after integer in range definition.");
			ParseStatus status3 = integer(tokens, status2.tokenNumber+1);
			if (status3.errMsg != null) return status3;
			long first = status1.minDim;
			long second = status2.minDim;
			long third = status3.minDim;
			long by = second - first;
			if (by <= 0)
				return ParseUtils.syntaxError(
						status2.tokenNumber, tokens,
						"In a range the difference between the 1st and "+
						"2nd number must be >= 0.");
			if (first > third)
				return ParseUtils.syntaxError(
						status3.tokenNumber, tokens,
						"In a range the 1st integer must be <= 3rd integer.");
			// do some minor dimension optimization - find real last value
			long last = first;
			for (long i = first; i <= third; i += by) {
				last = i;
			}
			conditions.add(
					new RangeCondition(minDims.size(), first, last, by));
			status3.minDim = first;
			status3.maxDim = last;
			return status3;
		}
		else
			return ParseUtils.syntaxError(pos+1, tokens,
					"Unexpected token in range definition.");
	}

	/*
	intList =
	 integer |
	 integer “,” intList
	*/
	private ParseStatus intList(List<Token> tokens, int pos, List<Long> values) {
		ParseStatus status1 = integer(tokens, pos);
		if (status1.errMsg != null) return status1;
		values.add(status1.minDim);
		if (ParseUtils.match(Comma.class, tokens, status1.tokenNumber)) {
			ParseStatus status2 = intList(tokens, status1.tokenNumber+1, values);
			if (status2.errMsg != null) return status2;
			status2.maxDim = Math.max(status1.maxDim, status2.maxDim);
			status2.minDim = Math.min(status1.minDim, status2.minDim);
			return status2;
		}
		return status1;
	}

	/*
	integer =
	 int |
	 "+" int |
	 "-" int
	*/
	private ParseStatus integer(List<Token> tokens, int pos) {
		int p = pos;
		long multiplier = 1;
		if (ParseUtils.match(Plus.class, tokens, p)) {
			p++;
			multiplier = 1;
		}
		else if (ParseUtils.match(Minus.class, tokens, p)) {
			p++;
			multiplier = -1;
		}
		if (!ParseUtils.match(Int.class, tokens, p))
			return ParseUtils.syntaxError(p, tokens, "Expected an integer.");
		Int i = (Int) tokens.get(p);
		long value = multiplier * i.getValue();
		ParseStatus status = new ParseStatus();
		status.minDim = value;
		status.maxDim = value;
		status.tokenNumber = p + 1;
		return status;
	}
	
	/*
	restrictions =
	 compoundBoolExpression |
	 compoundBoolExpression , restrictions
	*/
	private ParseStatus restrictions(List<Token> tokens, int pos) {
		ParseStatus status = compoundBoolExpression(tokens, pos);
		if (status.errMsg != null) return status;
		conditions.add(status.condition);
		if (ParseUtils.match(Comma.class, tokens, status.tokenNumber)) {
			return restrictions(tokens, status.tokenNumber+1);
		}
		return status;
	}
	
	/*
	compoundBoolExpression =
	 boolClause |
	 boolClause “and” compoundBoolExpression
	 boolClause “or” compundBoolExpression
	 boolClause “xor” compundBoolExpression
	*/
	private ParseStatus compoundBoolExpression(List<Token> tokens, int pos) {
		ParseStatus status1 = boolClause(tokens, pos);
		ParseStatus status2 = status1;
		if (status1.errMsg != null) return status1;
		if (ParseUtils.match(And.class, tokens, status1.tokenNumber)) {
			status2 = compoundBoolExpression(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.condition = new AndCondition<long[]>(status1.condition, status2.condition);
		}
		else if (ParseUtils.match(Or.class, tokens, status1.tokenNumber)) {
			status2 = compoundBoolExpression(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.condition = new OrCondition<long[]>(status1.condition, status2.condition);
		}
		else if (ParseUtils.match(Xor.class, tokens, status1.tokenNumber)) {
			status2 = compoundBoolExpression(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.condition = new XorCondition<long[]>(status1.condition, status2.condition);
		}
		return status2;
	}
	
	/*
	boolClause =
	 expression |
	 “not” expression
	*/
	private ParseStatus boolClause(List<Token> tokens, int pos) {
		if (ParseUtils.match(Not.class, tokens, pos)) {
			ParseStatus status = expression(tokens, pos+1);
			if (status.errMsg != null) return status;
			status.condition = new NotCondition<long[]>(status.condition);
			return status;
		}
		return expression(tokens, pos);
	}
	
	/*
	expression =
	 equation relop equation
	*/
	
	private ParseStatus expression(List<Token> tokens, int pos) {
		ParseStatus status1 = eqnParser.equation(tokens, pos);
		if (status1.errMsg != null) return status1;
		ParseStatus status2 = relop(tokens, status1.tokenNumber);
		if (status2.errMsg != null) return status2;
		ParseStatus status3 = eqnParser.equation(tokens, status2.tokenNumber);
		if (status3.errMsg != null) return status3;
		status3.condition = new BinaryFunctionalCondition<long[],DoubleType,DoubleType>(
				status1.function, status3.function, status2.relop);
		return status3;
	}

	/*
	relop = “<” | “<=” | “>” | “>=” | “==” | “!=”
	 (in java the last two have higher precedence than first four. maybe can ignore)
	*/
	private ParseStatus relop(List<Token> tokens, int pos) {
		ParseStatus status = new ParseStatus();
		if (ParseUtils.match(Less.class, tokens, pos)) {
			status.relop = new RealLessThan<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (ParseUtils.match(LessEqual.class, tokens, pos)) {
			status.relop = new RealLessThanOrEqual<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (ParseUtils.match(Greater.class, tokens, pos)) {
			status.relop = new RealGreaterThan<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (ParseUtils.match(GreaterEqual.class, tokens, pos)) {
			status.relop = new RealGreaterThanOrEqual<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (ParseUtils.match(Equal.class, tokens, pos)) {
			status.relop = new RealEquals<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (ParseUtils.match(NotEqual.class, tokens, pos)) {
			status.relop = new RealNotEquals<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else
			return ParseUtils.syntaxError(pos, tokens, "Expected a relational operator.");
		
		return status;
	}

	// -- helpers --
	
	private boolean intsAndCommas(List<Token> tokens, int pos) {
		if (!ParseUtils.match(Int.class, tokens, pos)) return false;
		int p = pos;
		while (ParseUtils.match(Int.class, tokens, p) || ParseUtils.match(Comma.class, tokens, p)) p++;
		return ParseUtils.match(CloseRange.class, tokens, p);
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
}
