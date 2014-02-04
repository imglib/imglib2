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

import java.util.List;
import java.util.Map;

import net.imglib2.img.Img;
import net.imglib2.ops.function.general.GeneralBinaryFunction;
import net.imglib2.ops.function.general.GeneralUnaryFunction;
import net.imglib2.ops.function.real.RealAngleFromOriginFunction;
import net.imglib2.ops.function.real.RealConstantFunction;
import net.imglib2.ops.function.real.RealDistanceFromPointFunction;
import net.imglib2.ops.function.real.RealImageFunction;
import net.imglib2.ops.function.real.RealIndexFunction;
import net.imglib2.ops.operation.real.binary.RealAdd;
import net.imglib2.ops.operation.real.binary.RealBinaryOperation;
import net.imglib2.ops.operation.real.binary.RealDivide;
import net.imglib2.ops.operation.real.binary.RealMax;
import net.imglib2.ops.operation.real.binary.RealMin;
import net.imglib2.ops.operation.real.binary.RealMod;
import net.imglib2.ops.operation.real.binary.RealMultiply;
import net.imglib2.ops.operation.real.binary.RealPower;
import net.imglib2.ops.operation.real.binary.RealSubtract;
import net.imglib2.ops.parse.token.AngleReference;
import net.imglib2.ops.parse.token.CloseParen;
import net.imglib2.ops.parse.token.Comma;
import net.imglib2.ops.parse.token.DimensionReference;
import net.imglib2.ops.parse.token.DistanceFromCenterReference;
import net.imglib2.ops.parse.token.Divide;
import net.imglib2.ops.parse.token.Exponent;
import net.imglib2.ops.parse.token.FunctionCall;
import net.imglib2.ops.parse.token.ImgReference;
import net.imglib2.ops.parse.token.Int;
import net.imglib2.ops.parse.token.Max;
import net.imglib2.ops.parse.token.Min;
import net.imglib2.ops.parse.token.Minus;
import net.imglib2.ops.parse.token.Mod;
import net.imglib2.ops.parse.token.OpenParen;
import net.imglib2.ops.parse.token.Plus;
import net.imglib2.ops.parse.token.Real;
import net.imglib2.ops.parse.token.Times;
import net.imglib2.ops.parse.token.Token;
import net.imglib2.ops.parse.token.TypeBoundReference;
import net.imglib2.ops.parse.token.Variable;
import net.imglib2.type.numeric.RealType;
import net.imglib2.type.numeric.real.DoubleType;

/* Grammar

equation =
term |
term “+” equation |
term “-” equation

term =
factor |
factor “*” term |
factor “\” term |
factor “%” term

factor =
signedAtom |
signedAtom “^” factor

signedAtom
 atom |
 sign atom

atom =
identifier |
"img" |
function “(“ equation “)” |
num |
“(“ equation “)”

function =
“log” | “exp” | “abs” | “ceil” | “floor” | “round” | “signum” | “sqrt” | “sqr” | ???

num = real | int | “E” | “PI”
(actually LEXER detects E and PI constants and creates appropriate Reals)

*/

/**
 * Used by other classes that need to parse equations. See
 * {@link PointSetParser} and {@link RealEquationFunctionParser} 
 * 
 * @author Barry DeZonia
 *
 */
public class EquationParser<T extends RealType<T>> {
	
	private Map<String,Integer> varMap;
	private Img<T> img;
	
	public EquationParser(Map<String,Integer> varMap, Img<T> img) {
		this.varMap = varMap;
		this.img = img;
	}
	
	/*
	equation =
	 term |
	 term “+” equation |
	 term “-” equation
	*/
	public ParseStatus equation(List<Token> tokens, int pos) {
		ParseStatus status1 = term(tokens, pos);
		if (status1.errMsg != null) return status1;
		ParseStatus status2 = status1;
		if (ParseUtils.match(Plus.class, tokens, status1.tokenNumber)) {
			status2 = equation(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealAdd<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (ParseUtils.match(Minus.class, tokens, status1.tokenNumber)) {
			status2 = equation(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
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
	 factor “*” term |
	 factor “\” term |
	 factor “%” term
	*/
	private ParseStatus term(List<Token> tokens, int pos) {
		ParseStatus status1 = factor(tokens, pos);
		if (status1.errMsg != null) return status1;
		ParseStatus status2 = status1;
		if (ParseUtils.match(Times.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealMultiply<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (ParseUtils.match(Divide.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealDivide<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		else if (ParseUtils.match(Mod.class, tokens, status1.tokenNumber)) {
			status2 = term(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			status2.function = new
				GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
					status1.function, status2.function,
					new RealMod<DoubleType,DoubleType,DoubleType>(),
					new DoubleType());
		}
		return status2;
	}
	
	/*
	factor =
	 signedAtom |
	 signedAtom “^” factor
	*/
	private ParseStatus factor(List<Token> tokens, int pos) {
		ParseStatus status1 = signedAtom(tokens, pos);
		if (status1.errMsg != null) return status1;
		ParseStatus status2 = status1;
		if (ParseUtils.match(Exponent.class, tokens, status1.tokenNumber)) {
			status2 = factor(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
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
		if (ParseUtils.match(Plus.class, tokens, pos)) {
			return atom(tokens, pos+1);
		}
		else if (ParseUtils.match(Minus.class, tokens, pos)) {
			ParseStatus status = atom(tokens, pos+1);
			if (status.errMsg != null) return status;
			RealConstantFunction<long[], DoubleType> constant =
				new RealConstantFunction<long[], DoubleType>(new DoubleType(-1));
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
		if (ParseUtils.match(Variable.class, tokens, pos)) {
			Variable var = (Variable) tokens.get(pos);
			int index = varMap.get(var.getText());
			if (index< 0)
				return ParseUtils.syntaxError(pos, tokens,
						"Undeclared variable " + var.getText());
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos + 1;
			status.function = new RealIndexFunction(index);
			return status;
		}
		else if (ParseUtils.match(FunctionCall.class, tokens, pos)) {
			FunctionCall funcCall = (FunctionCall) tokens.get(pos);
			if (!ParseUtils.match(OpenParen.class, tokens, pos+1))
				return ParseUtils.syntaxError(pos+1, tokens,
							"Function call definition expected a '('");
			ParseStatus status = equation(tokens, pos+2);
			if (status.errMsg != null) return status;
			if (!ParseUtils.match(CloseParen.class, tokens, status.tokenNumber))
				return ParseUtils.syntaxError(
						status.tokenNumber,
						tokens,
						"Function call definition expected a ')'");
			status.function =
				new GeneralUnaryFunction<long[], DoubleType, DoubleType>(
					status.function, funcCall.getOp(), new DoubleType());	
			status.tokenNumber++;
			return status;
		}
		else if (ParseUtils.match(ImgReference.class, tokens, pos)) {
			if (img == null)
				return ParseUtils.syntaxError(
						pos, tokens, "IMG reference not allowed in this context");
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos+1;
			status.function =
				new RealImageFunction<T, DoubleType>(img, new DoubleType());
			return status;
		}
		else if (ParseUtils.match(TypeBoundReference.class, tokens, pos)) {
			TypeBoundReference bound = (TypeBoundReference) tokens.get(pos);
			if (img == null)
				return ParseUtils.syntaxError(
						pos, tokens,
						"Type bounds only work in equations that are associated with an Img");
			T type = img.cursor().get();
			double constant = (bound.isMin() ? type.getMinValue() : type.getMaxValue());
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos+1;
			status.function =
				new RealConstantFunction<long[], DoubleType>(new DoubleType(constant));
			return status;
		}
		else if (ParseUtils.match(DimensionReference.class, tokens, pos)) {
			if (!ParseUtils.match(OpenParen.class, tokens, pos+1))
				return ParseUtils.syntaxError(pos+1, tokens, "Expected a '('.");
			if (!ParseUtils.match(Variable.class, tokens, pos+2))
				return ParseUtils.syntaxError(pos+2, tokens, "Expected a dimension variable reference.");
			Variable var = (Variable) tokens.get(pos+2);
			if (!ParseUtils.match(CloseParen.class, tokens, pos+3))
				return ParseUtils.syntaxError(pos+3, tokens, "Expected a ')'.");
			// if here then structured okay
			Integer reference = varMap.get(var.getText());
			if (reference == null)
				return ParseUtils.syntaxError(pos+2, tokens, "Unknown variable.");
			if (reference < 0)
				return ParseUtils.syntaxError(pos+2, tokens, "Undeclared variable.");
			if (img == null)
				return ParseUtils.syntaxError(
						pos, tokens,
						"Dimension bounds only work in equations that are associated with an Img");
			double constant = img.dimension(reference);
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos+4;
			status.function =
				new RealConstantFunction<long[], DoubleType>(new DoubleType(constant));
			return status;
		}
		else if (ParseUtils.match(DistanceFromCenterReference.class, tokens, pos)) {
			if (img == null)
				return ParseUtils.syntaxError(
					pos, tokens,
					"Center distance references only work in equations that are associated with an Img");
			long[] dims = new long[img.numDimensions()];
			img.dimensions(dims);
			// calc center of image
			// TODO - is this off by a little (such as a half)? Depends where we
			// define pixel locations (left or center).
			double[] ctr = new double[dims.length];
			for (int i = 0; i < dims.length; i++) {
				ctr[i] = dims[i] / 2.0;
			}
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos+1;
			status.function =	new RealDistanceFromPointFunction<DoubleType>(ctr, new DoubleType());
			return status;
		}
		else if (ParseUtils.match(AngleReference.class, tokens, pos)) {
			if (!ParseUtils.match(OpenParen.class, tokens, pos+1))
				return ParseUtils.syntaxError(pos+1, tokens, "Expected a '('.");
			if (!ParseUtils.match(Variable.class, tokens, pos+2))
				return ParseUtils.syntaxError(pos+2, tokens, "Expected a variable reference.");
			if (!ParseUtils.match(Comma.class, tokens, pos+3))
				return ParseUtils.syntaxError(pos+3, tokens, "Expected a ','.");
			if (!ParseUtils.match(Variable.class, tokens, pos+4))
				return ParseUtils.syntaxError(pos+4, tokens, "Expected a variable reference.");
			if (!ParseUtils.match(CloseParen.class, tokens, pos+5))
				return ParseUtils.syntaxError(pos+5, tokens, "Expected a ')'.");
			Variable var1 = (Variable) tokens.get(pos+2);
			Variable var2 = (Variable) tokens.get(pos+4);
			int axis1 = varMap.get(var1.getText());
			int axis2 = varMap.get(var2.getText());
			if (axis1 < 0)
				return ParseUtils.syntaxError(pos+2, tokens, "Undeclared variable.");
			if (axis2 < 0)
				return ParseUtils.syntaxError(pos+4, tokens, "Undeclared variable.");
			if (axis1 == axis2)
				return ParseUtils.syntaxError(pos, tokens, "Cannot reference same axis variable twice.");
			ParseStatus status = new ParseStatus();
			status.tokenNumber = pos+6;
			status.function =	new RealAngleFromOriginFunction<DoubleType>(axis1, axis2, new DoubleType());
			return status;
		}
		else if (ParseUtils.match(Min.class, tokens, pos) ||
							ParseUtils.match(Max.class, tokens, pos))
		{
			if (!ParseUtils.match(OpenParen.class, tokens, pos+1))
				return ParseUtils.syntaxError(pos+1, tokens, "Expected a '('.");
			ParseStatus status1 = equation(tokens, pos+2);
			if (status1.errMsg != null) return status1;
			if (!ParseUtils.match(Comma.class, tokens, status1.tokenNumber))
				return ParseUtils.syntaxError(status1.tokenNumber, tokens, "Expected a ','.");
			ParseStatus status2 = equation(tokens, status1.tokenNumber+1);
			if (status2.errMsg != null) return status2;
			if (!ParseUtils.match(CloseParen.class, tokens, status2.tokenNumber))
				return ParseUtils.syntaxError(status2.tokenNumber, tokens, "Expected a ')'.");
			ParseStatus status = new ParseStatus();
			status.tokenNumber = status2.tokenNumber+1;
			RealBinaryOperation<DoubleType,DoubleType,DoubleType> op =
					ParseUtils.match(Min.class, tokens, pos) ?
						new RealMin<DoubleType,DoubleType,DoubleType>() :
						new RealMax<DoubleType,DoubleType,DoubleType>();
			status.function =
					new GeneralBinaryFunction<long[],DoubleType,DoubleType,DoubleType>(
							status1.function, status2.function, op, new DoubleType());
			return status;
		}
		else if (ParseUtils.match(OpenParen.class, tokens, pos)) {
			ParseStatus status = equation(tokens, pos+1);
			if (status.errMsg != null) return status;
			if (!ParseUtils.match(CloseParen.class, tokens, status.tokenNumber))
				return ParseUtils.syntaxError(
						status.tokenNumber, tokens, "Expected a ')'");
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
		if (ParseUtils.match(Real.class, tokens, pos)) {
			Real r = (Real) tokens.get(pos);
			ParseStatus status = new ParseStatus();
			status.function =
				new RealConstantFunction<long[], DoubleType>(new DoubleType(r
					.getValue()));
			status.tokenNumber = pos + 1;
			return status;
		}
		else if (ParseUtils.match(Int.class, tokens, pos)) {
			Int i = (Int) tokens.get(pos);
			ParseStatus status = new ParseStatus();
			status.function =
				new RealConstantFunction<long[], DoubleType>(new DoubleType(i
					.getValue()));
			status.tokenNumber = pos + 1;
			return status;
		}
		else
			return ParseUtils.syntaxError(pos, tokens, "Expected a number.");
	}
	
}
