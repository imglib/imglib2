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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.imglib2.type.Type;
import net.imglib2.type.numeric.IntegerType;
import net.imglib2.type.numeric.integer.IntType;

/**
 * The LabelingType represents a labeling of a pixel with zero or more labelings
 * of type T. Each labeling names a distinct object in the image space.
 *
 * @param <T>
 *            the desired type of the pixel labels, for instance Integer to
 *            number objects or String for user-assigned label names
 */
public class LabelingType< T extends Comparable< T >> implements Type< LabelingType< T >>
{
	final protected long[] generation;

	protected final LabelingMapping< T > mapping;

	protected final IntegerType< ? > type;

	/**
	 * Constructor for mirroring state with another labeling
	 *
	 * @param storage
	 * @param mapping
	 * @param generation
	 */
	protected LabelingType( final IntegerType< ? > type, final LabelingMapping< T > mapping, final long[] generation )
	{
		this.type = type;
		this.mapping = mapping;
		this.generation = generation;
	}

	// this is the constructor if you want it to read from an array
	public LabelingType( final IntegerType< ? > type, final LabelingMapping< T > mapping )
	{
		this.type = type;
		this.mapping = mapping;
		generation = new long[ 1 ];
	}

	// this is the constructor if you want it to be a variable
	public LabelingType( final List< T > value )
	{

		mapping = new LabelingMapping< T >( new IntType() );
		generation = new long[ 1 ];

		this.type = new IntType();
		setLabeling( value );
	}

	@SuppressWarnings( "unchecked" )
	public LabelingType( final T value )
	{
		this( Arrays.asList( value ) );
	}

	// this is the constructor if you want it to be a variable
	public LabelingType()
	{
		this( new ArrayList< T >() );
	}

	/**
	 * Get the labels defined at the type's current pixel or
	 *
	 * @return a list of the labelings at the current location.
	 */
	public final List< T > getLabeling()
	{
		return mapping.listAtIndex( type.getInteger() );
	}

	/**
	 * Set the labeling at the current pixel
	 *
	 * @param labeling
	 */
	public void setLabeling( final List< T > labeling )
	{
		this.type.setInteger( mapping.indexOf( labeling ) );
		synchronized ( generation )
		{
			generation[ 0 ]++;
		}
	}

	public void setLabeling( final T[] labeling )
	{
		setLabeling( Arrays.asList( labeling ) );
	}

	/**
	 * Assign a pixel a single label
	 *
	 * @param label
	 *            - the label to assign
	 */
	public void setLabel( final T label )
	{
		final List< T > labeling = new ArrayList< T >( 1 );
		labeling.add( label );
		setLabeling( labeling );
	}

	/**
	 * This method returns the canonical object for the given labeling.
	 * SetLabeling will work faster if you pass it the interned object instead
	 * of one created by you.
	 *
	 * @param labeling
	 * @return
	 */
	public List< T > intern( final List< T > labeling )
	{
		return mapping.intern( labeling );
	}

	/**
	 * Return the canonical labeling object representing the single labeling.
	 * SetLabeling will work faster if you use this object.
	 *
	 * @param label
	 *            - a label for a pixel.
	 * @return - the canonical labeling with the single label.
	 */
	public List< T > intern( final T label )
	{
		final List< T > labeling = new ArrayList< T >( 1 );
		labeling.add( label );
		return intern( labeling );
	}

	// @Override
	// public void updateContainer(Object o) {
	// b = storage.update(o);
	// }

	@Override
	public LabelingType< T > createVariable()
	{
		return new LabelingType< T >();
	}

	@Override
	public LabelingType< T > copy()
	{
		return new LabelingType< T >( getLabeling() );
	}

	@Override
	public void set( final LabelingType< T > c )
	{
		setLabeling( c.getLabeling() );
	}

	@Override
	public String toString()
	{
		return getLabeling().toString();
	}

	/**
	 * Get the labels known by the type
	 *
	 * @return a list of all labels in the type's associated storage
	 */
	List< T > getLabels()
	{
		return mapping.getLabels();
	}

	/**
	 * The underlying storage has an associated generation which is incremented
	 * every time the storage is modified. For cacheing, it's often convenient
	 * or necessary to know whether the storage has changed to know when the
	 * cache is invalid. The strategy is to save the generation number at the
	 * time of cacheing and invalidate the cache if the number doesn't match.
	 *
	 * @return the generation of the underlying storage
	 */
	long getGeneration()
	{
		return generation[ 0 ];
	}

	public LabelingMapping< T > getMapping()
	{
		return mapping;
	}

}
