/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Johannes Schindelin
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
 * @author Stephan Preibisch & Johannes Schindelin
 */
package mpicbg.imglib.type;

import mpicbg.imglib.type.numeric.integer.ByteType;
import mpicbg.imglib.type.numeric.integer.IntType;
import mpicbg.imglib.type.numeric.integer.LongType;
import mpicbg.imglib.type.numeric.integer.ShortType;
import mpicbg.imglib.type.numeric.real.FloatType;

/**
 * @deprecated TODO This construct is usable only for situations where
 * the input Type<?> is constant.  Since this is not guaranteed for any of the
 * accessors, this construct is fragile and will, most likely, break. 
 */
@Deprecated
public abstract class TypeConverter
{
	public abstract void convert();
	
	public static <A extends Type< A >, B extends Type< B > > TypeConverter getTypeConverter( final A input, final B output ) 
	{		
		/* inconvertible types due to javac bug 6548436: if ( input instanceof ByteType )
			return getTypeConverter((ByteType)input, output); */
		if ( (Object)input instanceof ByteType )
			return getTypeConverter((ByteType)(Object)input, output);

		/* inconvertible types due to javac bug 6548436: if ( input instanceof ShortType )
			return getTypeConverter((ShortType)input, output); */
		if ( (Object)input instanceof ShortType )
			return getTypeConverter((ShortType)(Object)input, output);
		
		System.out.println("mpi.imglib.type.TypeConverter(): Do not know how to convert Type " + input.getClass() );		
		return null;		
	}
	
	public static <A extends Type< A > > TypeConverter getTypeConverter( final ByteType in, final A output ) 
	{
		
		if ( ByteType.class.isInstance( output ) ) 
		{
			/* inconvertible types due to javac bug 6548436: final ByteType out = (ByteType)output; */
			final ByteType out = (ByteType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( in );
				}
			};
		}

		if ( ShortType.class.isInstance( output ) ) 
		{
			/* inconvertible types due to javac bug 6548436: final ShortType out = (ShortType)output; */
			final ShortType out = (ShortType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( (short)( in.get() & 0xff ) );
				}
			};
		}

		if ( IntType.class.isInstance( output ) )
		{
			/* inconvertible types due to javac bug 6548436: final IntType out = (IntType)output; */
			final IntType out = (IntType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( in.get() & 0xff );
				}
			};
		}

		if ( LongType.class.isInstance( output ) ) 
		{
			/* inconvertible types due to javac bug 6548436: final IntType out = (IntType)output; */
			final IntType out = (IntType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( in.get() & 0xff );
				}
			};
		}
		
		if ( FloatType.class.isInstance( output ) ) 
		{
			/* inconvertible types due to javac bug 6548436: final FloatType out = (FloatType)output; */
			final FloatType out = (FloatType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( in.get() & 0xff );
				}
			};
		}
		
		System.out.println("mpi.imglib.type.TypeConverter(): Do not know how to convert Type ByteType to Type " + output.getClass() );		
		return null;		
	}

	public static <A extends Type< A > > TypeConverter getTypeConverter( final ShortType in, final A output ) 
	{
		
		if ( ShortType.class.isInstance( output ) ) 
		{
			/* inconvertible types due to javac bug 6548436: final ShortType out = (ShortType)output; */
			final ShortType out = (ShortType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( in );
				}
			};
		}

		if ( FloatType.class.isInstance( output ) ) 
		{
			/* inconvertible types due to javac bug 6548436: final FloatType out = (FloatType)output; */
			final FloatType out = (FloatType)(Object)output;
			
			return new TypeConverter() 
			{
				final public void convert() 
				{
					out.set( in.get() & 0xff );
				}
			};
		}
		
		System.out.println("mpi.imglib.type.TypeConverter(): Do not know how to convert Type ShortType to Type " + output.getClass() );		
		return null;		
	}
	
}
