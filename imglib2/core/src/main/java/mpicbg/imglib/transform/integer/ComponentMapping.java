package mpicbg.imglib.transform.integer;

/**
 * Map the components of the source vector to obtain the target vector, for
 * instance transform (x,y,z) to (x,z,y).
 * 
 * <p>
 * The intended use of ComponentMapping is as a dimension permutation. The
 * mapping is implemented as a inverse lookup, i.e., every component of the
 * target is read from a source component.
 * <em>Note, that it is not allowed to set this array such that a source component
 * is mapped to several target components!</em>
 * </p>
 * 
 * @author Tobias Pietzsch
 */
public interface ComponentMapping extends Mixed
{}
