/**
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License 2
 * as published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 *
 * @author Stephan Preibisch & Johannes Schindelin
 */
package mpicbg.imglib.type;

import mpicbg.imglib.type.numeric.ByteType;
import mpicbg.imglib.type.numeric.FloatType;
import mpicbg.imglib.type.numeric.IntType;
import mpicbg.imglib.type.numeric.LongType;
import mpicbg.imglib.type.numeric.ShortType;

public abstract class TypeConverter
{
	public abstract void convert();
	
	public static <A extends Type< A >, B extends Type< B > > TypeConverter getTypeConverter( final A input, final B output ) 
	{		
		if ( input instanceof ByteType )
			return getTypeConverter((ByteType)input, output);

		if ( input instanceof ShortType )
			return getTypeConverter((ShortType)input, output);
		
		System.out.println("mpi.imglib.type.TypeConverter(): Do not know how to convert Type " + input.getClass() );		
		return null;		
	}
	
	public static <A extends Type< A > > TypeConverter getTypeConverter( final ByteType in, final A output ) 
	{
		
		if ( ByteType.class.isInstance( output ) ) 
		{
			final ByteType out = (ByteType)output;
			
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
			final ShortType out = (ShortType)output;
			
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
			final IntType out = (IntType)output;
			
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
			final IntType out = (IntType)output;
			
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
			final FloatType out = (FloatType)output;
			
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
			final ShortType out = (ShortType)output;
			
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
			final FloatType out = (FloatType)output;
			
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
