package mpicbg.imglib.container.newcell;

import mpicbg.imglib.container.basictypecontainer.DataAccess;

public interface ArrayOfCells< A extends DataAccess >
{
	public A getDataAccessForCell( int index );
}
