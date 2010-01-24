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
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.type.label;

import mpicbg.imglib.container.Container;
import mpicbg.imglib.container.ContainerFactory;
import mpicbg.imglib.cursor.Cursor;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.type.TypeImpl;

public class FakeType extends TypeImpl<FakeType>
{	
	@Override
	public Container<FakeType> createSuitableContainer( final ContainerFactory storageFactory, final int dim[] ) { return null; }

	@Override
	public void updateDataArray( Cursor<?> c ) {}
	
	@Override
	public Display<FakeType> getDefaultDisplay( Image<FakeType> image ) { return null; }

	@Override
	public void set( final FakeType c ) {}
	
	@Override
	public FakeType[] createArray1D(int size1){ return new FakeType[ size1 ]; }

	@Override
	public FakeType[][] createArray2D(int size1, int size2){ return new FakeType[ size1 ][ size2 ]; }

	@Override
	public FakeType[][][] createArray3D(int size1, int size2, int size3) { return new FakeType[ size1 ][ size2 ][ size3 ]; }

	//@Override
	//public FakeType getType() { return this; }

	@Override
	public FakeType createType( Container<FakeType> container ){ return new FakeType(); }
	
	@Override
	public FakeType createVariable(){ return new FakeType(); }

	@Override
	public FakeType copyVariable(){ return createVariable(); }

	@Override
	public String toString() { return ""; }
}
