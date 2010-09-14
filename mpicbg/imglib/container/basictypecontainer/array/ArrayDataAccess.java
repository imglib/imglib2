package mpicbg.imglib.container.basictypecontainer.array;

import mpicbg.imglib.container.basictypecontainer.DataAccess;

public interface ArrayDataAccess< A > extends DataAccess
{
	A createArray( int numEntities );
}
