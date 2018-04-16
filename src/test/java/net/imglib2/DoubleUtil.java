/*
 * #%L
 * ImgLib2: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2018 Tobias Pietzsch, Stephan Preibisch, Stephan Saalfeld,
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

package net.imglib2;

/**
 * Utility class for dealing with doubles.
 * 
 * @author Aivar Grislis
 */
public class DoubleUtil
{
	private static final double ZERO = 0x0.0p0;

	private static final double SMALLEST_POSITIVE = 0x0.0000000000001P-1022;

	private static final double SMALLEST_NEGATIVE = -0x0.0000000000001P-1022;

	private static final int MAX_PLACES = 13;

	private static final String MAX_DIGITS = "fffffffffffff";

	private static final String MIN_DIGITS = "0000000000000";

	private static final int LAST_HEX_DIGIT_INDEX = 12;

	/**
	 * Given a double value returns the next representable double.
	 */
	public static double nextDouble( final double value, final boolean inc )
	{
		double nextValue;

		// hardcode values around zero
		if ( ZERO == value )
		{
			if ( inc )
			{
				nextValue = SMALLEST_POSITIVE;
			}
			else
			{
				nextValue = SMALLEST_NEGATIVE;
			}
		}
		else if ( SMALLEST_NEGATIVE == value && inc )
		{
			nextValue = ZERO;
		}
		else if ( SMALLEST_POSITIVE == value && !inc )
		{
			nextValue = ZERO;
		}
		else
		{
			// increment/decrement hex string value
			String hexString;
			final boolean actualInc = ( ( value > 0 && inc ) || ( value < 0 && !inc ) );
			hexString = nextDoubleHexString( Double.toHexString( value ), actualInc );

			// convert hex string value to double
			try
			{
				nextValue = Double.parseDouble( hexString );
			}
			catch ( final Exception e )
			{
				System.out.println( "Error parsing " + hexString );
				nextValue = 0.0;
			}
		}
		return nextValue;
	}

	/**
	 * Parses double as hex string and generates next double hex string.
	 */
	private static String nextDoubleHexString( String hex, final boolean inc )
	{

		// save & restore negative sign
		boolean negative = false;
		if ( '-' == hex.charAt( 0 ) )
		{
			negative = true;
			hex = hex.substring( 1 );
		}

		// get leading digit, 0 or 1
		int leadingDigit = 0;
		try
		{
			leadingDigit = Integer.parseInt( hex.substring( 2, 3 ) );
		}
		catch ( final Exception e )
		{
			System.out.println( "Error parsing " + hex.substring( 2, 3 ) + " " + e );
		}

		// get fractional value in hexadecimal
		final int pIndex = hex.indexOf( 'p' );
		String hexDigits = hex.substring( 4, pIndex );
		hexDigits = padWithZeros( hexDigits );

		// get exponent
		int power = 0;
		try
		{
			power = Integer.parseInt( hex.substring( pIndex + 1 ) );
		}
		catch ( final Exception e )
		{
			System.out.println( "Error parsing " + hex.substring( pIndex + 1 ) + " " + e );
		}

		if ( inc )
		{
			// increment hexadecimal numeric value
			if ( MAX_DIGITS.equals( hexDigits ) )
			{
				if ( 1 == leadingDigit )
				{
					// next after 0x1.fffffffffffffp1 is 0x1.0p2
					++power;
				}
				else
				{
					// next after 0x0.fffffffffffffp-1022 is 0x1.0p-1022
					++leadingDigit;
				}
				hexDigits = MIN_DIGITS;
			}
			else
			{
				final char[] hexChars = hexDigits.toCharArray();

				int i = LAST_HEX_DIGIT_INDEX;
				boolean carry;
				do
				{
					++hexChars[ i ];

					if ( hexChars[ i ] == 'f' + 1 )
					{
						hexChars[ i ] = '0';

						// carry to preceding digit
						carry = true;
						--i;

					}
					else
					{

						// no carry
						carry = false;

						// patch hexadecimal
						if ( hexChars[ i ] == '9' + 1 )
						{
							hexChars[ i ] = 'a';
						}
					}
				}
				while ( i >= 0 && carry );

				hexDigits = new String( hexChars );
			}
		}
		else
		{
			// decrement hexadecimal numeric value
			if ( MIN_DIGITS.equals( hexDigits ) )
			{
				// previous before 0x1.0000000000000p1 is 0x1.fffffffffffffp0
				--power;
				hexDigits = MAX_DIGITS;
			}
			else
			{
				final char[] hexChars = hexDigits.toCharArray();

				int i = LAST_HEX_DIGIT_INDEX;
				boolean borrow;
				do
				{
					--hexChars[ i ];

					if ( hexChars[ i ] == '0' - 1 )
					{
						hexChars[ i ] = 'f';

						// borrow from preceding digit
						borrow = true;
						--i;
					}
					else
					{
						// no borrow
						borrow = false;

						// patch hexadecimal
						if ( hexChars[ i ] == 'a' - 1 )
						{
							hexChars[ i ] = '9';
						}
					}
				}
				while ( i >= 0 && borrow );

				hexDigits = new String( hexChars );
			}
		}
		return ( negative ? "-" : "" ) + "0x" + leadingDigit + "." + hexDigits + "p" + power;
	}

	/**
	 * Cheap way to pad with zeros.
	 */
	private static String padWithZeros( String hexDigits )
	{
		while ( hexDigits.length() < MAX_PLACES )
		{
			hexDigits += "0";
		}
		return hexDigits;
	}
}
