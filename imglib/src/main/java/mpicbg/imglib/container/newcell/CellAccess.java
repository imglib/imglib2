package mpicbg.imglib.container.newcell;

import mpicbg.imglib.container.basictypecontainer.DataAccess;
import mpicbg.imglib.type.NativeType;

public interface CellAccess< T extends NativeType< T >, A extends DataAccess >
{
	public Cell< T, A > getCell();
}
