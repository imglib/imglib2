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
 *
 */
package mpicbg.imglib.labeling;

import mpicbg.imglib.img.basictypeaccess.IntAccess;

/**
 * A labeling represents the assignment of zero or more labels to the
 * pixels in a space.
 * 
 * @author Lee Kamentsky
 *
 * @param <T> - the type used to label the pixels, for instance string
 * names for user-assigned object labels or integers for machine-labeled
 * images.
 */
public abstract class AbstractNativeLabeling<T extends Comparable<T>, A extends IntAccess> 
	extends AbstractLabeling<T> implements NativeLabeling<T, A> {

	protected LabelingType<T> linkedType;
	protected LabelingMapping<T, Integer> mapping = new LabelingMapping<T, Integer>(0);
	
	protected AbstractNativeLabeling(long [] dim, LabelingROIStrategyFactory<T> factory) {
		super(dim, factory);
	}
	/* (non-Javadoc)
	 * @see mpicbg.imglib.labeling.NativeLabeling#getMapping()
	 */
	@Override
	public LabelingMapping<T, Integer> getMapping() {
		return mapping;
	}

	@Override
	public void setLinkedType( final LabelingType<T> type ) { 
		this.linkedType = type;
	}
	
	@Override
	public LabelingType<T> createLinkedType()
	{
		try{ return linkedType.duplicateTypeOnSameNativeImg(); }
		catch ( NullPointerException e ){ return null; }
	}
}
