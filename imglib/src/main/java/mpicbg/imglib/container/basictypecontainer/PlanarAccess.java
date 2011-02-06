package mpicbg.imglib.container.basictypecontainer;

/**
 * Interface allowing access to data on a plane-by-plane basis.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 *
 * @param <A> primitive array type
 */
public interface PlanarAccess<A>
{
	A getPlane( int no );
	void setPlane( int no, A plane );
}