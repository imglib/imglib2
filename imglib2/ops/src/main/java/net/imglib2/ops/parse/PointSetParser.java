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


package net.imglib2.ops.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.imglib2.ops.BinaryOperation;
import net.imglib2.ops.Condition;
import net.imglib2.ops.PointSet;
import net.imglib2.ops.Tuple2;
import net.imglib2.ops.condition.AndCondition;
import net.imglib2.ops.condition.DimensionEqualCondition;
import net.imglib2.ops.condition.NotCondition;
import net.imglib2.ops.condition.OrCondition;
import net.imglib2.ops.condition.RangeCondition;
import net.imglib2.ops.condition.RelationalCondition;
import net.imglib2.ops.condition.UnionCondition;
import net.imglib2.ops.condition.XorCondition;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.ConstantRealFunction;
import net.imglib2.ops.function.real.RealIndexFunction;
import net.imglib2.ops.operation.binary.real.RealAdd;
import net.imglib2.ops.operation.binary.real.RealDivide;
import net.imglib2.ops.operation.binary.real.RealMultiply;
import net.imglib2.ops.operation.binary.real.RealPower;
import net.imglib2.ops.operation.binary.real.RealSubtract;
import net.imglib2.ops.parse.token.And;
import net.imglib2.ops.parse.token.Assign;
import net.imglib2.ops.parse.token.CloseParen;
import net.imglib2.ops.parse.token.CloseRange;
import net.imglib2.ops.parse.token.Comma;
import net.imglib2.ops.parse.token.Divide;
import net.imglib2.ops.parse.token.DotDot;
import net.imglib2.ops.parse.token.Equal;
import net.imglib2.ops.parse.token.Exponent;
import net.imglib2.ops.parse.token.FunctionCall;
import net.imglib2.ops.parse.token.Greater;
import net.imglib2.ops.parse.token.GreaterEqual;
import net.imglib2.ops.parse.token.Int;
import net.imglib2.ops.parse.token.Less;
import net.imglib2.ops.parse.token.LessEqual;
import net.imglib2.ops.parse.token.Minus;
import net.imglib2.ops.parse.token.Mod;
import net.imglib2.ops.parse.token.Not;
import net.imglib2.ops.parse.token.NotEqual;
import net.imglib2.ops.parse.token.OpenParen;
import net.imglib2.ops.parse.token.OpenRange;
import net.imglib2.ops.parse.token.Or;
import net.imglib2.ops.parse.token.Plus;
import net.imglib2.ops.parse.token.Real;
import net.imglib2.ops.parse.token.Times;
import net.imglib2.ops.parse.token.Token;
import net.imglib2.ops.parse.token.Variable;
import net.imglib2.ops.parse.token.Xor;
import net.imglib2.ops.pointset.ConditionalPointSet;
import net.imglib2.ops.pointset.EmptyPointSet;
import net.imglib2.ops.pointset.HyperVolumePointSet;
import net.imglib2.ops.relation.RealEquals;
import net.imglib2.ops.relation.RealGreaterThan;
import net.imglib2.ops.relation.RealGreaterThanOrEqual;
import net.imglib2.ops.relation.RealLessThan;
import net.imglib2.ops.relation.RealLessThanOrEqual;
import net.imglib2.ops.relation.RealNotEquals;
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

equation =
 term |
 term “+” term |
 term “-” term

term =
 factor |
 factor “*” factor |
 factor “\” factor |
 factor “%” factor

factor =
 signedAtom |
 signedAtom “^” signedAtom

signedAtom
  atom |
  sign atom

atom =
 identifier |
 function “(“ equation “)” |
 num |
 “(“ equation “)”

function =
 “log” | “exp” | “abs” | “ceil” | “floor” | “round” | “signum” | “sqrt” | “sqr” | ???

num = real | int | “E” | “PI”
 (actually LEXER detects E and PI constants and creates appropriate Reals)
 
 */

/**
* 
* @author Barry DeZonia
*
*/
public class PointSetParser {

	private Map<String, Integer> varMap;
	private List<Long> minDims;
	private List<Long> maxDims;
	private List<Condition<long[]>> conditions;
	
	public Tuple2<PointSet,String> parse(String specification) {
		minDims = new ArrayList<Long>();
		maxDims = new ArrayList<Long>();
		conditions = new ArrayList<Condition<long[]>>();
		varMap = new HashMap<String,Integer>();
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
	
	private boolean match(Class<?> expectedClass, List<Token> tokens, int pos) {
		if (pos >= tokens.size()) return false;
		return tokens.get(pos).getClass() == expectedClass;
	}

	private ParseStatus syntaxError(Integer tokenNumber, Token token, String err) {
		ParseStatus status = new ParseStatus();
		status.columnNumber = -1;
		status.tokenNumber = tokenNumber;
		status.pointSet = new EmptyPointSet();
		status.errMsg = "Syntax error with token ("+token.getText()+") near column "+token.getStart()+": "+err;
		return status;
	}

	private ParseStatus nextPosition(int pos) {
		ParseStatus status = new ParseStatus();
		status.tokenNumber = pos;
		return status;
	}
	
	/*
	statement =
			 dimensions |
			 dimensions, restrictions
	*/
	private ParseStatus statement(List<Token> tokens) {
		ParseStatus status = dimensions(tokens, 0, 0);
		if (status.errMsg != null) return status;
		long[] minPt = new long[minDims.size()];
		long[] maxPt = new long[maxDims.size()];
		for (int i = 0; i < minDims.size(); i++) {
			minPt[i] = minDims.get(i);
			maxPt[i] = maxDims.get(i);
		}
		PointSet ps = new HyperVolumePointSet(minPt, maxPt);
		if (match(Comma.class, tokens, status.tokenNumber)) {
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
	private ParseStatus dimensions(List<Token> tokens, int currPos, int dimNum) {
		ParseStatus status = dimension(tokens, currPos, dimNum);
		if (status.errMsg != null) return status;
		if (match(Comma.class, tokens, status.tokenNumber)) {
			if (match(Variable.class, tokens, status.tokenNumber+1))
				if (match(Assign.class, tokens, status.tokenNumber+2))
					return dimensions(tokens, status.tokenNumber+1, dimNum+1);
		}
		return status;
	}

	/*
	dimension =
			 identifier “=” “[“ values “]”
	*/
	private ParseStatus dimension(List<Token> tokens, int pos, int dimNum) {
		if (!match(Variable.class, tokens, pos))
			return syntaxError(pos, tokens.get(pos),
					"Expected a variable name.");
		if (!match(Assign.class, tokens, pos+1))
			return syntaxError(pos, tokens.get(pos+1),
					"Expected an assignment symbol '='.");
		if (!match(OpenRange.class, tokens, pos+2))
			return syntaxError(pos, tokens.get(pos+2),
					"Expected an open bracket symbol '['.");
		ParseStatus valsStatus = values(tokens, pos+3);
		if (valsStatus.errMsg != null) return valsStatus;
		if (!match(CloseRange.class, tokens, valsStatus.tokenNumber))
			return syntaxError(valsStatus.tokenNumber,
					tokens.get(valsStatus.tokenNumber),
					"Expected a close bracket symbol ']'.");

		Variable var = (Variable) tokens.get(pos);
		int dimIndex = varMap.get(var.getText());
		if (dimIndex >= 0)
			throw new IllegalArgumentException(
				"cannot declare dimension ("+var.getText()+") more than once");
		varMap.put(var.getText(), (-dimIndex)-1);  // mark bound
		
		minDims.add(valsStatus.minDim);
		maxDims.add(valsStatus.maxDim);
		
		return nextPosition(valsStatus.tokenNumber+1);
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
		if (match(DotDot.class, tokens, status1.tokenNumber)) {
			ParseStatus status2 = integer(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status1;
			long first = status1.minDim;
			long second = status2.minDim;
			if (first > second)
				return syntaxError(
						status2.tokenNumber,
						tokens.get(status2.tokenNumber),
						"In a range the first value must be <= last value.");
			ParseStatus status = new ParseStatus();
			conditions.add(
					new RangeCondition(minDims.size(), first, second, 1));
			status.minDim = first;
			status.maxDim = second;
			status.tokenNumber = status2.tokenNumber;
			return status;
		}
		else if (match(Comma.class, tokens, status1.tokenNumber)) {
			ParseStatus status2 = integer(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			if (!match(DotDot.class, tokens, status2.tokenNumber))
				return syntaxError(
						status2.tokenNumber,
						tokens.get(status2.tokenNumber),
						"Expected '..' after integer in range definition.");
			ParseStatus status3 = integer(tokens, status2.tokenNumber+1);
			if (status3.errMsg != null) return status3;
			long first = status1.minDim;
			long second = status2.minDim;
			long third = status3.minDim;
			long by = second - first;
			if (by <= 0)
				return syntaxError(
						status2.tokenNumber,
						tokens.get(status2.tokenNumber),
						"In a range the difference between the 1st and "+
						"2nd number must be >= 0.");
			if (first > third)
				return syntaxError(
						status3.tokenNumber,
						tokens.get(status3.tokenNumber),
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
			return syntaxError(pos+1, tokens.get(pos+1),
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
		if (match(Comma.class, tokens, status1.tokenNumber)) {
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
		if (match(Plus.class, tokens, p)) {
			p++;
			multiplier = 1;
		}
		else if (match(Minus.class, tokens, p)) {
			p++;
			multiplier = -1;
		}
		if (!match(Int.class, tokens, p))
			return syntaxError(p, tokens.get(p), "Expected an integer.");
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
		if (match(Comma.class, tokens, status.tokenNumber)) {
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
		if (match(And.class, tokens, status1.tokenNumber)) {
			status2 = compoundBoolExpression(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.condition = new AndCondition<long[]>(status1.condition, status2.condition);
		}
		else if (match(Or.class, tokens, status1.tokenNumber)) {
			status2 = compoundBoolExpression(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.condition = new OrCondition<long[]>(status1.condition, status2.condition);
		}
		else if (match(Xor.class, tokens, status1.tokenNumber)) {
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
		if (match(Not.class, tokens, pos)) {
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
		ParseStatus status1 = equation(tokens, pos);
		if (status1.errMsg != null) return status1;
		ParseStatus status2 = relop(tokens, status1.tokenNumber);
		if (status2.errMsg != null) return status2;
		ParseStatus status3 = equation(tokens, status2.tokenNumber);
		if (status3.errMsg != null) return status3;
		status3.condition = new RelationalCondition(
				status2.relop, status1.function, status3.function);
		return status3;
	}

	/*
	relop = “<” | “<=” | “>” | “>=” | “==” | “!=”
	 (in java the last two have higher precedence than first four. maybe can ignore)
	*/
	private ParseStatus relop(List<Token> tokens, int pos) {
		ParseStatus status = new ParseStatus();
		if (match(Less.class, tokens, pos)) {
			status.relop = new RealLessThan<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (match(LessEqual.class, tokens, pos)) {
			status.relop = new RealLessThanOrEqual<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (match(Greater.class, tokens, pos)) {
			status.relop = new RealGreaterThan<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (match(GreaterEqual.class, tokens, pos)) {
			status.relop = new RealGreaterThanOrEqual<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (match(Equal.class, tokens, pos)) {
			status.relop = new RealEquals<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else if (match(NotEqual.class, tokens, pos)) {
			status.relop = new RealNotEquals<DoubleType,DoubleType>();
			status.tokenNumber = pos + 1;
		}
		else
			return syntaxError(pos, tokens.get(pos), "Expected a relational operator.");
		
		return status;
	}
	
	/*
	equation =
	 term |
	 term “+” term |
	 term “-” term
	*/
	private ParseStatus equation(List<Token> tokens, int pos) {
		ParseStatus status1 = term(tokens, pos);
		ParseStatus status2 = status1;
		if (match(Plus.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealAdd<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (match(Minus.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealSubtract<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		return status2;
	}
	
	/*
	term =
	 factor |
	 factor “*” factor |
	 factor “\” factor |
	 factor “%” factor
	*/
	private ParseStatus term(List<Token> tokens, int pos) {
		ParseStatus status1 = factor(tokens, pos);
		ParseStatus status2 = status1;
		if (match(Times.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealMultiply<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (match(Divide.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealDivide<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (match(Mod.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealMod(), new DoubleType());
		}
		return status2;
	}
	
	/*
	factor =
	 signedAtom |
	 signedAtom “^” signedAtom
	*/
	private ParseStatus factor(List<Token> tokens, int pos) {
		ParseStatus status1 = signedAtom(tokens, pos);
		ParseStatus status2 = status1;
		if (match(Exponent.class, tokens, status1.tokenNumber)) {
			status2 = signedAtom(tokens, status1.tokenNumber+1);
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealPower<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		return status2;
	}
	
	/*
	signedAtom
	  atom |
	  "+" atom |
	  "-" atom
	*/
	private ParseStatus signedAtom(List<Token> tokens, int pos) {
		if (match(Plus.class, tokens, pos)) {
			return atom(tokens, pos+1);
		}
		else if (match(Minus.class, tokens, pos)) {
			ParseStatus status = atom(tokens, pos+1);
			if (status.errMsg != null) return status;
			ConstantRealFunction<long[], DoubleType> constant =
				new ConstantRealFunction<long[], DoubleType>(new DoubleType(), -1);
			status.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					constant, status.function,
					new RealMultiply<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
			return status;
		}
		else
			return atom(tokens, pos);
	}
	
	/*
	atom =
	 identifier |
	 function “(“ equation “)” |
	 num |
	 “(“ equation “)” 
	*/
	private ParseStatus atom(List<Token> tokens, int pos) {
		if (match(Variable.class, tokens, pos)) {
			Variable var = (Variable) tokens.get(pos);
			int index = varMap.get(var.getText());
			if (index< 0)
				return syntaxError(pos, tokens.get(pos),
						"Undeclared variable " + var.getText());
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos + 1;
			status.function = new RealIndexFunction(index);
			return status;
		}
		else if (match(FunctionCall.class, tokens, pos)) {
			FunctionCall funcCall = (FunctionCall) tokens.get(pos);
			if (!match(OpenParen.class, tokens, pos))
				syntaxError(pos, tokens.get(pos),
							"Function call definition expected a '('");
			ParseStatus status = equation(tokens, pos+1);
			if (status.errMsg != null) return status;
			if (!match(CloseParen.class, tokens, status.tokenNumber))
				return syntaxError(
						status.tokenNumber,
						tokens.get(status.tokenNumber),
						"Function call definition expected a ')'");
			status.function =
				new GeneralUnaryFunction<long[], DoubleType, DoubleType>(
					status.function, funcCall.getOp(), new DoubleType());	
			status.tokenNumber++;
			return status;
		}
		else if (match(OpenParen.class, tokens, pos)) {
			ParseStatus status = equation(tokens, pos+1);
			if (status.errMsg != null) return status;
			if (!match(CloseParen.class, tokens, status.tokenNumber))
				return syntaxError(status.tokenNumber, tokens.get(status.tokenNumber), "Expected a ')'");
			status.tokenNumber++;
			return status;
		}
		else
			return num(tokens, pos);
	}
	
	/*
	num = real | int
	*/
	private ParseStatus num(List<Token> tokens, int pos) {
		if (match(Real.class, tokens, pos)) {
			Real r = (Real) tokens.get(pos);
			ParseStatus status = new ParseStatus();
			status.function = new ConstantRealFunction<long[],DoubleType>(new DoubleType(),r.getValue());
			status.tokenNumber = pos + 1;
			return status;
		}
		else if (match(Int.class, tokens, pos)) {
			Int i = (Int) tokens.get(pos);
			ParseStatus status = new ParseStatus();
			status.function = new ConstantRealFunction<long[],DoubleType>(new DoubleType(),i.getValue());
			status.tokenNumber = pos + 1;
			return status;
		}
		else
			return syntaxError(pos, tokens.get(pos), "Expected a number.");
	}
	
	// -- helpers --
	
	private boolean intsAndCommas(List<Token> tokens, int pos) {
		if (!match(Int.class, tokens, pos)) return false;
		int p = pos;
		while (match(Int.class, tokens, p) || match(Comma.class, tokens, p)) p++;
		return match(CloseRange.class, tokens, p);
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
	
	// not a great function. will not make public.
	
	private class RealMod implements BinaryOperation<DoubleType, DoubleType, DoubleType> {

		@Override
		public DoubleType compute(DoubleType input1, DoubleType input2, DoubleType output) {
			long value = ((long) input1.get()) % ((long) input2.get());
			output.set(value);
			return output;
		}

		@Override
		public RealMod copy() {
			return new RealMod();
		}
		
	}
	
	/*
	public static void main(String[] args) {
		String spec;
		
		spec = "x=[1..100],y=[1..50]";
		System.out.println(spec+ " results in:\n" + new PointSetParser().parse(spec).get2());
	
		spec = "x=[1,100],y=[1..50]";
		System.out.println(spec+ " results in:\n" + new PointSetParser().parse(spec).get2());
	}
	*/
}
