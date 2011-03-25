package mpicbg.imglib.transform.integer;

/**
 * Map the components of the source vector to obtain the target vector, for
 * instance transform (x,y,z) to (x,z,y).
 * 
 * <p>
 * Although it's intended use is a dimension permutation, bijectivity of the
 * mapping is not enforced. The mapping is implemented as a inverse lookup,
 * i.e., every component of the target is read from a source component. The same
 * source component can be mapped to several target components.
 * </p>
 * 
 * @author Tobias Pietzsch
 */
public interface ComponentMapping extends Mixed
{
}
