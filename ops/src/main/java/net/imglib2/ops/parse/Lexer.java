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
import java.util.List;
import java.util.Map;

import net.imglib2.ops.operation.UnaryOperation;
import net.imglib2.ops.operation.real.unary.RealAbs;
import net.imglib2.ops.operation.real.unary.RealArccos;
import net.imglib2.ops.operation.real.unary.RealArccosh;
import net.imglib2.ops.operation.real.unary.RealArccot;
import net.imglib2.ops.operation.real.unary.RealArccoth;
import net.imglib2.ops.operation.real.unary.RealArccsc;
import net.imglib2.ops.operation.real.unary.RealArccsch;
import net.imglib2.ops.operation.real.unary.RealArcsec;
import net.imglib2.ops.operation.real.unary.RealArcsech;
import net.imglib2.ops.operation.real.unary.RealArcsin;
import net.imglib2.ops.operation.real.unary.RealArcsinh;
import net.imglib2.ops.operation.real.unary.RealArctan;
import net.imglib2.ops.operation.real.unary.RealArctanh;
import net.imglib2.ops.operation.real.unary.RealCeil;
import net.imglib2.ops.operation.real.unary.RealCos;
import net.imglib2.ops.operation.real.unary.RealCosh;
import net.imglib2.ops.operation.real.unary.RealCot;
import net.imglib2.ops.operation.real.unary.RealCoth;
import net.imglib2.ops.operation.real.unary.RealCsc;
import net.imglib2.ops.operation.real.unary.RealCsch;
import net.imglib2.ops.operation.real.unary.RealCubeRoot;
import net.imglib2.ops.operation.real.unary.RealExp;
import net.imglib2.ops.operation.real.unary.RealExpMinusOne;
import net.imglib2.ops.operation.real.unary.RealFloor;
import net.imglib2.ops.operation.real.unary.RealGaussianRandom;
import net.imglib2.ops.operation.real.unary.RealLog;
import net.imglib2.ops.operation.real.unary.RealLog10;
import net.imglib2.ops.operation.real.unary.RealLog2;
import net.imglib2.ops.operation.real.unary.RealLogOnePlusX;
import net.imglib2.ops.operation.real.unary.RealNearestInt;
import net.imglib2.ops.operation.real.unary.RealRound;
import net.imglib2.ops.operation.real.unary.RealSec;
import net.imglib2.ops.operation.real.unary.RealSech;
import net.imglib2.ops.operation.real.unary.RealSignum;
import net.imglib2.ops.operation.real.unary.RealSin;
import net.imglib2.ops.operation.real.unary.RealSinc;
import net.imglib2.ops.operation.real.unary.RealSincPi;
import net.imglib2.ops.operation.real.unary.RealSinh;
import net.imglib2.ops.operation.real.unary.RealSqr;
import net.imglib2.ops.operation.real.unary.RealSqrt;
import net.imglib2.ops.operation.real.unary.RealStep;
import net.imglib2.ops.operation.real.unary.RealTan;
import net.imglib2.ops.operation.real.unary.RealTanh;
import net.imglib2.ops.operation.real.unary.RealUlp;
import net.imglib2.ops.operation.real.unary.RealUniformRandom;
import net.imglib2.ops.parse.token.And;
import net.imglib2.ops.parse.token.AngleReference;
import net.imglib2.ops.parse.token.Assign;
import net.imglib2.ops.parse.token.CloseParen;
import net.imglib2.ops.parse.token.CloseRange;
import net.imglib2.ops.parse.token.Comma;
import net.imglib2.ops.parse.token.DimensionReference;
import net.imglib2.ops.parse.token.DistanceFromCenterReference;
import net.imglib2.ops.parse.token.Divide;
import net.imglib2.ops.parse.token.DotDot;
import net.imglib2.ops.parse.token.Equal;
import net.imglib2.ops.parse.token.Exponent;
import net.imglib2.ops.parse.token.FunctionCall;
import net.imglib2.ops.parse.token.Greater;
import net.imglib2.ops.parse.token.GreaterEqual;
import net.imglib2.ops.parse.token.ImgReference;
import net.imglib2.ops.parse.token.Int;
import net.imglib2.ops.parse.token.Less;
import net.imglib2.ops.parse.token.LessEqual;
import net.imglib2.ops.parse.token.Max;
import net.imglib2.ops.parse.token.Min;
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
import net.imglib2.ops.parse.token.TypeBoundReference;
import net.imglib2.ops.parse.token.Variable;
import net.imglib2.ops.parse.token.Xor;
import net.imglib2.type.numeric.real.DoubleType;

/**
* Responsible for turning a input String in the equation language into a set
* of tokens for use later by a parser.
* 
* @author Barry DeZonia
*
*/
public class Lexer {

	// -- constructor --
	
	public Lexer() {}
	
	// -- Lexer methods --
	
	public ParseStatus tokenize(String spec, Map<String,Integer> varMap)
	{
		List<Token> tokens = new ArrayList<Token>();
		char[] chars = spec.toCharArray();
		int i = 0;
		while (i < chars.length) {
			Character ch = chars[i];
			if (Character.isLetter(ch)) {
				StringBuilder builder = new StringBuilder();
				while (i < chars.length &&
						(Character.isLetter(chars[i]) ||
							Character.isDigit(chars[i])))
				{
					builder.append(chars[i]);
					i++;
				}
				String name = builder.toString();
				Token token = reservedWordLookup(name, i);
				if (token != null)
					tokens.add(token);
				else
					tokens.add(new Variable(i, name, varMap));
			}
			else if (Character.isDigit(ch)) {
				int start = i;
				StringBuilder builder = new StringBuilder();
				boolean isReal = false;
				while (i < chars.length && (Character.isDigit(chars[i]))) {
					builder.append(chars[i]);
					i++;
					char next = (i < chars.length) ? chars[i] : 0;
					char next2 = (i < chars.length-1) ? chars[i+1] : 0;
					if ((next == '.') && (next2 == '.')) break;
					if (next == '.') {
						if (isReal) // already seen a decimal
							return lexicalError(spec, i, chars[i]);
						// else valid decimal
						isReal = true;
						builder.append(".");
						i++;
					}
				}
				String numStr = builder.toString();
				if (isReal)
					tokens.add(new Real(start, numStr));
				else
					tokens.add(new Int(start, numStr));
			}
			else if (ch == '<') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new LessEqual(i-2, "<="));
				}
				else
					tokens.add(new Less(i-1, "<"));
			}
			else if (ch == '>') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new GreaterEqual(i-2, ">="));
				}
				else
					tokens.add(new Greater(i-1, ">"));
			}
			else if (ch == '!') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new NotEqual(i-2, "!="));
				}
				else
					tokens.add(new Not(i-1, "!"));
			}
			else if (ch == '=') {
				i++;
				if (i < chars.length && chars[i] == '=') {
					i++;
					tokens.add(new Equal(i-2, "=="));
				}
				else
					tokens.add(new Assign(i-1, "="));
			}
			else if (ch == '.') {
				i++;
				if (i < chars.length && chars[i] == '.') {
					i++;
					tokens.add(new DotDot(i-2, ".."));
				}
				else
					return lexicalError(spec, i-1, ch);
			}
			else if (ch == '&') {
				i++;
				if (i < chars.length && chars[i] == '&') {
					i++;
					tokens.add(new And(i-2, "&&"));
				}
				else
					return lexicalError(spec, i-1, ch);
			}
			else if (ch == '|') {
				i++;
				if (i < chars.length && chars[i] == '|') {
					i++;
					tokens.add(new Or(i-2, "||"));
				}
				else
					return lexicalError(spec, i-1, ch);
			}
			else if (ch == '^') {
				i++;
				if (i < chars.length && chars[i] == '^') {
					i++;
					tokens.add(new Xor(i-2, "^^"));
				}
				else
					tokens.add(new Exponent(i-1, "^"));
			}
			else if (ch == ',') {
				i++;
				tokens.add(new Comma(i-1, ","));
			}
			else if (ch == '[') {
				i++;
				tokens.add(new OpenRange(i-1, "["));
			}
			else if (ch == ']') {
				i++;
				tokens.add(new CloseRange(i-1, "["));
			}
			else if (ch == '+') {
				i++;
				tokens.add(new Plus(i-1, "+"));
			}
			else if (ch == '*') {
				i++;
				if (i < chars.length && chars[i] == '*') {
					i++;
					tokens.add(new Exponent(i - 2, "**"));
				}
				tokens.add(new Times(i-1, "*"));
			}
			else if (ch == '/') {
				i++;
				tokens.add(new Divide(i-1, "/"));
			}
			else if (ch == '-') {
				i++;
				tokens.add(new Minus(i-1, "-"));
			}
			else if (ch == '%') {
				i++;
				tokens.add(new Mod(i-1, "%"));
			}
			else if (ch == '(') {
				i++;
				tokens.add(new OpenParen(i-1, "("));
			}
			else if (ch == ')') {
				i++;
				tokens.add(new CloseParen(i-1, ")"));
			}
			else if (Character.isWhitespace(ch))
				i++;
			else { // invalid char
				return lexicalError(spec, i, ch);
			}
		}
		ParseStatus status = new ParseStatus();
		status.tokens = tokens;
		return status;
	}

	// -- private helpers --
	
	private Token reservedWordLookup(String name, int pos) {
		// constants
		if (name.equals("E")) return new Real(pos, name, Math.E);
		if (name.equals("PI")) return new Real(pos, name, Math.PI);

		// image reference
		if (name.equals("img")) return new ImgReference(pos, name);
		
		// dimension reference
		if (name.equals("dim")) return new DimensionReference(pos, name);
		
		// type bound reference
		if (name.equals("tmin")) return new TypeBoundReference(pos, name, true);
		if (name.equals("tmax")) return new TypeBoundReference(pos, name, false);
		
		// distance from center reference
		if (name.equals("dctr")) return new DistanceFromCenterReference(pos, name);

		// angle reference
		if (name.equals("angle")) return new AngleReference(pos, name);
		
		// min/max call
		if (name.equals("min")) return new Min(pos, name);
		if (name.equals("max")) return new Max(pos, name);
		
		// logical operations
		if (name.equals("and")) return new And(pos, name);
		if (name.equals("or")) return new Or(pos, name);
		if (name.equals("xor")) return new Xor(pos, name);
		if (name.equals("not")) return new Not(pos, name);

		// predefined functions
		UnaryOperation<DoubleType, DoubleType> op = null;

		if (name.equals("abs")) op = new RealAbs<DoubleType,DoubleType>();
		if (name.equals("acos")) op = new RealArccos<DoubleType,DoubleType>();
		if (name.equals("acosh")) op = new RealArccosh<DoubleType,DoubleType>();
		if (name.equals("acot")) op = new RealArccot<DoubleType,DoubleType>();
		if (name.equals("acoth")) op = new RealArccoth<DoubleType,DoubleType>();
		if (name.equals("acsc")) op = new RealArccsc<DoubleType,DoubleType>();
		if (name.equals("acsch")) op = new RealArccsch<DoubleType,DoubleType>();
		if (name.equals("asec")) op = new RealArcsec<DoubleType,DoubleType>();
		if (name.equals("asech")) op = new RealArcsech<DoubleType,DoubleType>();
		if (name.equals("asin")) op = new RealArcsin<DoubleType,DoubleType>();
		if (name.equals("asinh")) op = new RealArcsinh<DoubleType,DoubleType>();
		if (name.equals("atan")) op = new RealArctan<DoubleType,DoubleType>();
		if (name.equals("atanh")) op = new RealArctanh<DoubleType,DoubleType>();
		if (name.equals("cbrt")) op = new RealCubeRoot<DoubleType,DoubleType>();
		if (name.equals("ceil")) op = new RealCeil<DoubleType,DoubleType>();
		if (name.equals("cos")) op = new RealCos<DoubleType,DoubleType>();
		if (name.equals("cosh")) op = new RealCosh<DoubleType,DoubleType>();
		if (name.equals("cot")) op = new RealCot<DoubleType,DoubleType>();
		if (name.equals("coth")) op = new RealCoth<DoubleType,DoubleType>();
		if (name.equals("csc")) op = new RealCsc<DoubleType,DoubleType>();
		if (name.equals("csch")) op = new RealCsch<DoubleType,DoubleType>();
		if (name.equals("exp")) op = new RealExp<DoubleType,DoubleType>();
		if (name.equals("expm1")) op = new RealExpMinusOne<DoubleType,DoubleType>();
		if (name.equals("floor")) op = new RealFloor<DoubleType,DoubleType>();
		if (name.equals("gauss")) op = new RealGaussianRandom<DoubleType,DoubleType>();
		if (name.equals("log")) op = new RealLog<DoubleType,DoubleType>();
		if (name.equals("log1p")) op = new RealLogOnePlusX<DoubleType,DoubleType>();
		if (name.equals("log10")) op = new RealLog10<DoubleType,DoubleType>();
		if (name.equals("log2")) op = new RealLog2<DoubleType,DoubleType>();
		if (name.equals("rand")) op = new RealUniformRandom<DoubleType,DoubleType>();
		if (name.equals("rint")) op = new RealNearestInt<DoubleType,DoubleType>();
		if (name.equals("round")) op = new RealRound<DoubleType,DoubleType>();
		if (name.equals("sec")) op = new RealSec<DoubleType,DoubleType>();
		if (name.equals("sech")) op = new RealSech<DoubleType,DoubleType>();
		if (name.equals("signum")) op = new RealSignum<DoubleType,DoubleType>();
		if (name.equals("sin")) op = new RealSin<DoubleType,DoubleType>();
		if (name.equals("sinc")) op = new RealSinc<DoubleType,DoubleType>();
		if (name.equals("sincpi")) op = new RealSincPi<DoubleType,DoubleType>();
		if (name.equals("sinh")) op = new RealSinh<DoubleType,DoubleType>();
		if (name.equals("sqr")) op = new RealSqr<DoubleType,DoubleType>();
		if (name.equals("sqrt")) op = new RealSqrt<DoubleType,DoubleType>();
		if (name.equals("step")) op = new RealStep<DoubleType,DoubleType>();
		if (name.equals("tan")) op = new RealTan<DoubleType,DoubleType>();
		if (name.equals("tanh")) op = new RealTanh<DoubleType,DoubleType>();
		if (name.equals("ulp")) op = new RealUlp<DoubleType,DoubleType>();
		
		if (op != null) return new FunctionCall(pos, name, op);
		
		return null;
	}
	
	private ParseStatus lexicalError(String input, int pos, Character ch) {
		List<Token> emptyTokenList = new ArrayList<Token>();
		String errorMessage = "Invalid char ("+ch+") at position ("+pos+") of input string ("+input+")";
		ParseStatus status = new ParseStatus();
		status.tokens = emptyTokenList;
		status.columnNumber = pos;
		status.errMsg = errorMessage;
		return status;
	}
}
