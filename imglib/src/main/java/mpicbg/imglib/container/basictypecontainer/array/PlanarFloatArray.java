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
package mpicbg.imglib.container.basictypecontainer.array;

import mpicbg.imglib.container.basictypecontainer.FloatAccess;

/**
 * A 2D float array dimensioned planeCount x elementsPerPlane.
 * Allows for efficient access to individual image planes.
 *
 * @author Curtis Rueden ctrueden at wisc.edu
 */
public class PlanarFloatArray implements FloatAccess,
  ArrayDataAccess<PlanarFloatArray>, PlanarAccess<float[]>
{
  protected int elementsPerPlane;
  protected float[][] data;

  public PlanarFloatArray(final int elementsPerPlane, final int numEntities) {
    if (numEntities % elementsPerPlane != 0) {
      throw new IllegalArgumentException(
        "Elements per plane must divide total number of entities");
    }
    this.elementsPerPlane = elementsPerPlane;
    final int planeCount = numEntities / elementsPerPlane;
    this.data = new float[planeCount][];
  }

  public PlanarFloatArray(final float[][] data) {
    this.data = data;
  }

  // -- PlanarFloatArray methods --

  public float[][] getCurrentStorageArray() { return data; }

  // -- FloatAccess methods --

  @Override
  public float getValue(final int index) {
    final int no = index / elementsPerPlane;
    if (data[no] == null) return 0;
    return data[no][index % elementsPerPlane];
  }

  @Override
  public void setValue(final int index, final float value) {
    final int no = index / elementsPerPlane;
    if (data[no] == null) data[no] = new float[elementsPerPlane];
    data[no][index % elementsPerPlane] = value;
  }

  // -- DataAccess methods --

  @Override
  public void close() { data = null; }

  // -- ArrayDataAccess methods --

  @Override
  public PlanarFloatArray createArray(final int numEntities) {
    throw new RuntimeException("Unsupported operation");
  }

  @Override
  public Object getCurrentStorageArrayAsObject() {
    return getCurrentStorageArray();
  }

  // -- PlanarAccess methods --

  @Override
  public float[] getPlane(int no) {
  	if (data[no] == null) {
  		// allocate plane with empty data
  		data[no] = new float[elementsPerPlane];
  	}
    return data[no];
  }

  @Override
  public void setPlane(int no, float[] plane) {
    data[no] = plane;
  }

}
