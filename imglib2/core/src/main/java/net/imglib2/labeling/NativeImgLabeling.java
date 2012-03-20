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
 * @author Lee Kamentsky
 * @modified Christian Dietz, Martin Horn
 *
 */

package net.imglib2.labeling;

import java.util.Iterator;

import net.imglib2.Cursor;
import net.imglib2.Interval;
import net.imglib2.IterableRealInterval;
import net.imglib2.RandomAccess;
import net.imglib2.Sampler;
import net.imglib2.converter.ConvertedCursor;
import net.imglib2.converter.ConvertedRandomAccess;
import net.imglib2.converter.sampler.SamplerConverter;
import net.imglib2.img.Img;
import net.imglib2.type.numeric.IntegerType;

/**
 * A labeling backed by a native image that takes a labeling type backed by an
 * int array.
 *
 * @author leek
 *
 * @param <T>
 *            the type of labels assigned to pixels
 */
public class NativeImgLabeling< T extends Comparable< T >, I extends IntegerType< I >> extends AbstractNativeLabeling< T >
{

	protected final long[] generation;

	protected final Img< I > img;

	public NativeImgLabeling( final Img< I > img )
	{
		super( dimensions( img ), new DefaultROIStrategyFactory< T >(), new LabelingMapping< T >( img.firstElement().createVariable() ) );
		this.img = img;
		this.generation = new long[ 1 ];
	}

	private static long[] dimensions( final Interval i )
	{
		final long[] dims = new long[ i.numDimensions() ];
		i.dimensions( dims );
		return dims;
	}

	/**
	 * Create a labeling backed by a native image with custom strategy and image
	 * factory
	 *
	 * @param dim
	 *            - dimensions of the labeling
	 * @param strategyFactory
	 *            - the strategy factory that drives iteration and statistics
	 * @param imgFactory
	 *            - the image factory to generate the native image
	 */
	public NativeImgLabeling( final LabelingROIStrategyFactory< T > strategyFactory, final Img< I > img )
	{
		super( dimensions( img ), strategyFactory, new LabelingMapping< T >( img.firstElement().createVariable() ) );
		this.img = img;
		this.generation = new long[ 1 ];
	}

	@Override
	public RandomAccess< LabelingType< T >> randomAccess()
	{
		final RandomAccess< I > rndAccess = img.randomAccess();

		return new ConvertedRandomAccess< I, LabelingType< T >>( new LabelingTypeSamplerConverter( rndAccess ), rndAccess );
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * net.imglib2.labeling.AbstractNativeLabeling#setLinkedType(net.imglib2
	 * .labeling.LabelingType)
	 */
	@Override
	public Cursor< LabelingType< T >> cursor()
	{

		final Cursor< I > cursor = img.cursor();
		return new ConvertedCursor< I, LabelingType< T >>( new LabelingTypeSamplerConverter( cursor ), cursor );
	}

	@Override
	public Cursor< LabelingType< T >> localizingCursor()
	{
		final Cursor< I > cursor = img.localizingCursor();
		return new ConvertedCursor< I, LabelingType< T >>( new LabelingTypeSamplerConverter( cursor ), cursor );
	}

	public Img< I > getStorageImg()
	{
		return img;
	}

	@Override
	public Labeling< T > copy()
	{
		final NativeImgLabeling< T, I > result = new NativeImgLabeling< T, I >( img.factory().create( img, img.firstElement().createVariable() ) );
		final Cursor< LabelingType< T >> srcCursor = cursor();
		final Cursor< LabelingType< T >> resCursor = result.cursor();

		while ( srcCursor.hasNext() )
		{
			srcCursor.fwd();
			resCursor.fwd();

			resCursor.get().set( srcCursor.get() );
		}

		return result;

	}

	class LabelingTypeSamplerConverter implements SamplerConverter< I, LabelingType< T >>
	{

		private final LabelingType< T > type;

		public LabelingTypeSamplerConverter( final Sampler< I > source )
		{
			this.type = new LabelingType< T >( source.get(), mapping, generation );
		}

		@Override
		public LabelingType< T > convert( final Sampler< I > sampler )
		{
			return type;
		}

	}

	@Override
	public boolean equalIterationOrder( final IterableRealInterval< ? > f )
	{
		return false;
	}

	@Override
	public LabelingType< T > firstElement()
	{
		return cursor().next();
	}

	@Override
	public Iterator< LabelingType< T >> iterator()
	{
		return cursor();
	}

	@Override
	public RandomAccess< LabelingType< T >> randomAccess( final Interval interval )
	{
		return randomAccess();
	}

	@Override
	public < LL extends Comparable< LL >> LabelingFactory< LL > factory()
	{
		return new LabelingFactory< LL >()
		{

			@Override
			public Labeling< LL > create( final long[] dim )
			{
				return new NativeImgLabeling< LL, I >( img.factory().create( dim, img.firstElement().createVariable() ) );
			}

		};
	}

}
