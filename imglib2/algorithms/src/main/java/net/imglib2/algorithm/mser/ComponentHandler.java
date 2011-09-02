package net.imglib2.algorithm.mser;

/**
 * Handle completed components that are output by {@link ComponentTree}.
 * 
 * @author Tobias Pietzsch
 * 
 * @param <C>
 */
public interface ComponentHandler< C >
{
	public void emit( C component );
}
