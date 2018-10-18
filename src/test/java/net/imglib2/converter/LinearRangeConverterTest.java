package net.imglib2.converter;

import org.junit.Assert;
import org.junit.Test;

import net.imglib2.Cursor;
import net.imglib2.IterableInterval;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.type.numeric.real.DoubleType;

public class LinearRangeConverterTest
{

	@Test
	public void testRealType()
	{
		final IterableInterval< UnsignedByteType > img = ArrayImgs.unsignedBytes( new long[]{ 2, 2 } );
		final Cursor< UnsignedByteType > c = img.cursor();
		c.next().setInteger(  50 );
		c.next().setInteger( 100 );
		c.next().setInteger( 125 );
		c.next().setInteger( 150 );
		
		print4( img );
		
		final UnsignedByteType min = new UnsignedByteType( 100 );
		final UnsignedByteType max = new UnsignedByteType( 150 );
		
		System.out.println( "min: " + min.get() + ", max: " + max.get() );
		
		final LinearRangeConverter< UnsignedByteType > converter = new LinearRangeConverter<UnsignedByteType>( min, max );
		
		final IterableInterval< UnsignedByteType > ii = Converters.convert( img, converter, new UnsignedByteType() );
				
		final int[] p = print4( ii );
		
		Assert.assertArrayEquals( new int[]{ 0,  0, 128, 255 }, p );
	}
	
	private int[] print4( final IterableInterval< UnsignedByteType > img )
	{
		final Cursor< UnsignedByteType > c = img.cursor();
		
		final int[] p = new int[ (int) img.size() ];
		
		for (int i=0; i< p.length; ++i) p[i] = c.next().get();

		System.out.println( p[0] + ", " + p[1] + ", " + p[2] + ", " + p[3] );
		
		return p;
	}
	
	@Test
	public void testTyped()
	{
		final IterableInterval< UnsignedByteType > img = ArrayImgs.unsignedBytes( new long[]{ 2, 2 } );
		final Cursor< UnsignedByteType > c = img.cursor();
		c.next().setInteger(  50 );
		c.next().setInteger( 100 );
		c.next().setInteger( 125 );
		c.next().setInteger( 150 );
		
		print4( img );
		
		final UnsignedByteType min = new UnsignedByteType( 100 );
		final UnsignedByteType max = new UnsignedByteType( 150 );
		
		System.out.println( "min: " + min.get() + ", max: " + max.get() );
		
		final LinearRangeTypedOpConverter< UnsignedByteType, DoubleType > converter = LinearRangeTypedOpConverter.create( min, max );
		
		final IterableInterval< UnsignedByteType > ii = Converters.convert( img, converter, new UnsignedByteType() );
				
		final int[] p = print4( ii );
		
		Assert.assertArrayEquals( new int[]{ 0,  0, 128, 255 }, p );
	}
}
