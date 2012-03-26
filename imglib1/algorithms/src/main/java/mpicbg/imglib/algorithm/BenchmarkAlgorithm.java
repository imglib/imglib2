/*
 * #%L
 * ImgLib: a general-purpose, multidimensional image processing library.
 * %%
 * Copyright (C) 2009 - 2012 Stephan Preibisch, Stephan Saalfeld, Tobias
 * Pietzsch, Albert Cardona, Barry DeZonia, Curtis Rueden, Lee Kamentsky, Larry
 * Lindsey, Johannes Schindelin, Christian Dietz, Grant Harris, Jean-Yves
 * Tinevez, Steffen Jaensch, Mark Longair, Nick Perry, and Jan Funke.
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as
 * published by the Free Software Foundation, either version 2 of the 
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public 
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/gpl-2.0.html>.
 * #L%
 */

package mpicbg.imglib.algorithm;

/**
 * This is a convenience implementation of an algorithm that implements {@link Benchmark} 
 * and {@link Algorithm} so that less code has to be re-implemented.
 * 
 * IMPORTANT: It is not meant to be used for any other purpose than that, it should not be 
 * demanded by any other method or generic construct, use the interfaces instead.
 *   
 * @author Stephan Preibisch
 */
public abstract class BenchmarkAlgorithm implements Benchmark, Algorithm
{
	protected long processingTime = -1;
	protected String errorMessage = "";
	
	@Override
	public long getProcessingTime() { return processingTime; }
	
	@Override
	public String getErrorMessage() { return errorMessage; }		
}
