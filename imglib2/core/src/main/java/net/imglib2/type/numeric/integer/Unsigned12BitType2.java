package net.imglib2.type.numeric.integer;

import net.imglib2.img.NativeImg;
import net.imglib2.img.NativeImgFactory;
import net.imglib2.img.basictypeaccess.LongAccess;
import net.imglib2.img.basictypeaccess.array.LongArray;
import net.imglib2.util.Fraction;

public class Unsigned12BitType2 extends AbstractBitType<Unsigned12BitType2>
{
	// A mask for bit and, containing nBits of 1
	private final long mask;

	// this is the constructor if you want it to read from an array
	public Unsigned12BitType2(
			final NativeImg<Unsigned12BitType2,
			? extends LongAccess> bitStorage)
	{
		super( bitStorage );
		this.mask = 4095; // 111111111111 in binary
	}

	// this is the constructor if you want it to be a variable
	public Unsigned12BitType2( final long value )
	{
		this( (NativeImg<Unsigned12BitType2, ? extends LongAccess>)null );
		dataAccess = new LongArray( 1 );
		set( value );
	}

	// this is the constructor if you want to specify the dataAccess
	public Unsigned12BitType2( final LongAccess access )
	{
		this( (NativeImg<Unsigned12BitType2, ? extends LongAccess>)null );
		dataAccess = access;
	}

	// this is the constructor if you want it to be a variable
	public Unsigned12BitType2() { this( 0 ); }

	@Override
	public NativeImg<Unsigned12BitType2, ? extends LongAccess> createSuitableNativeImg( final NativeImgFactory<Unsigned12BitType2> storageFactory, final long dim[] )
	{
		// create the container
		final NativeImg<Unsigned12BitType2, ? extends LongAccess> container = storageFactory.createLongInstance( dim, new Fraction( getBitsPerPixel(), 64 ) );

		// create a Type that is linked to the container
		final Unsigned12BitType2 linkedType = new Unsigned12BitType2( container );

		// pass it to the NativeContainer
		container.setLinkedType( linkedType );

		return container;
	}

	@Override
	public Unsigned12BitType2 duplicateTypeOnSameNativeImg() { return new Unsigned12BitType2( img ); }

	@Override
	public long get() {
		final long k = i * 12;
		final int i1 = (int)(k >>> 6); // k / 64;
		final long shift = k & 63; // k % 64;
		// equivalent: seems like less operations but it is a tiny bit slower
		//final long k = (i << 1) + i;
		//final int i1 = (int)(k >>> 4);
		//final long shift = (k & 15) << 2;
		
		final long v = dataAccess.getValue(i1);
		if (0 == shift) {
			// Number contained in a single long, ending exactly at the first bit
			return v & mask;
		} else {
			final long antiShift = 64 - shift;
			if (antiShift < 12) {
				// Number split between two adjacent long
				final long v1 = (v >>> shift) & (mask >>> (12 - antiShift)); // lower part, stored at the upper end
				final long v2 = (dataAccess.getValue(i1 + 1) & (mask >>> antiShift)) << antiShift; // upper part, stored at the lower end
				return v1 | v2;
			} else {
				// Number contained inside a single long
				return (v >>> shift) & mask;
			}
		}
	}

	// Crops value to within mask
	@Override
	public void set( final long value ) {
		final long k = i * 12;
		final int i1 = (int)(k >>> 6); // k / 64;
		final long shift = k & 63; // k % 64;
		final long safeValue = value & mask;

		final long antiShift = 64 - shift;
		final long v = dataAccess.getValue(i1);
		if (antiShift < 12) {
			// Number split between two adjacent longs
			// 1. Store the lower bits of safeValue at the upper bits of v1
			final long v1 = (v & (0xffffffffffffffffL >>> antiShift)) // clear upper bits, keep other values
					| ((safeValue & (mask >>> (12 - antiShift))) << shift); // the lower part of safeValue, stored at the upper end
			dataAccess.setValue(i1, v1);
			// 2. Store the upper bits of safeValue at the lower bits of v2
			final long v2 = (dataAccess.getValue(i1 + 1) & (0xffffffffffffffffL << (12 - antiShift))) // other
					| (safeValue >>> antiShift); // upper part of safeValue, stored at the lower end
			dataAccess.setValue(i1 + 1, v2);
		} else {
			// Number contained inside a single long
			if (0 == v) {
				// Trivial case
				dataAccess.setValue(i1, safeValue << shift);
			} else {
				// Clear the bits first
				dataAccess.setValue(i1, (v & ~(mask << shift)) | (safeValue << shift));
			}
		}
	}

	@Override
	public Unsigned12BitType2 createVariable(){ return new Unsigned12BitType2( 0 ); }

	@Override
	public Unsigned12BitType2 copy(){ return new Unsigned12BitType2( get() ); }

	@Override
	public int getBitsPerPixel() { return 12; }
}
