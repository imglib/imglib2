/**
 * Copyright (c) 2009--2010, Stephan Preibisch & Stephan Saalfeld
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.  Redistributions in binary
 * form must reproduce the above copyright notice, this list of conditions and
 * the following disclaimer in the documentation and/or other materials
 * provided with the distribution.  Neither the name of the Fiji project nor
 * the names of its contributors may be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * @author Stephan Preibisch & Stephan Saalfeld
 */
package mpicbg.imglib.type.label;

import mpicbg.imglib.container.DirectAccessContainer;
import mpicbg.imglib.container.DirectAccessContainerFactory;
import mpicbg.imglib.image.Image;
import mpicbg.imglib.image.display.Display;
import mpicbg.imglib.sampler.RasterSampler;
import mpicbg.imglib.type.AbstractType;

public class FakeType extends AbstractType<FakeType>
{	
	@Override
	public DirectAccessContainer<FakeType,?> createSuitableDirectAccessContainer( final DirectAccessContainerFactory storageFactory, final int dim[] ) { return null; }

	@Override
	public void updateContainer( RasterSampler<?> c ) {}
	
	@Override
	public FakeType duplicateTypeOnSameDirectAccessContainer() { return new FakeType(); }

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
	
	@Override
	public FakeType createVariable(){ return new FakeType(); }

	@Override
	public FakeType clone(){ return createVariable(); }

	@Override
	public String toString() { return ""; }
}
