package net.imglib2.img.sparse;

import net.imglib2.img.basictypeaccess.DataAccess;

public interface NtreeAccess< L extends Comparable< L >, A extends NtreeAccess< L, A >> extends DataAccess
{

	Ntree< L > getCurrentStorageNtree();

	A createInstance( long[] pos );
}
