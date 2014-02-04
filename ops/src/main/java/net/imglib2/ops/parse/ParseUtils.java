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

import net.imglib2.ops.parse.token.Token;
import net.imglib2.ops.pointset.EmptyPointSet;

/**
 * Static methods used by various parsers.
 * 
 * @author Barry DeZonia
 *
 */
public class ParseUtils {

	private ParseUtils() {}
	
	public static boolean match(Class<?> expectedClass, List<Token> tokens, int pos) {
		if (pos >= tokens.size()) return false;
		return tokens.get(pos).getClass() == expectedClass;
	}

	public static ParseStatus syntaxError(Integer tokenNumber, List<Token> tokens, String err) {
		ParseStatus status = new ParseStatus();
		status.columnNumber = -1;
		status.tokenNumber = tokenNumber;
		status.pointSet = new EmptyPointSet();
		if (tokenNumber < tokens.size()) {
			Token token = tokens.get(tokenNumber);
			status.errMsg = "Syntax error with token ("+token.getText()+") near column "+token.getStart()+": "+err;
		}
		else {
			Token token = tokens.get(tokenNumber-1);
			status.errMsg = "Unexpected end of input after token ("+token.getText()+") near column "+token.getStart()+": context - "+err;
		}
		return status;
	}

	public static ParseStatus nextPosition(int pos) {
		ParseStatus status = new ParseStatus();
		status.tokenNumber = pos;
		return status;
	}
	
}
