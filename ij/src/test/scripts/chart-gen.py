###
# #%L
# ImgLib2: a general-purpose, multidimensional image processing library.
# %%
# Copyright (C) 2009 - 2014 Stephan Preibisch, Tobias Pietzsch, Barry DeZonia,
# Stephan Saalfeld, Albert Cardona, Curtis Rueden, Christian Dietz, Jean-Yves
# Tinevez, Johannes Schindelin, Lee Kamentsky, Larry Lindsey, Grant Harris,
# Mark Hiner, Aivar Grislis, Martin Horn, Nick Perry, Michael Zinsmaier,
# Steffen Jaensch, Jan Funke, Mark Longair, and Dimiter Prodanov.
# %%
# Redistribution and use in source and binary forms, with or without
# modification, are permitted provided that the following conditions are met:
# 
# 1. Redistributions of source code must retain the above copyright notice,
#    this list of conditions and the following disclaimer.
# 2. Redistributions in binary form must reproduce the above copyright notice,
#    this list of conditions and the following disclaimer in the documentation
#    and/or other materials provided with the distribution.
# 
# THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
# AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
# IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
# ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
# LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
# CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
# SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
# INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
# CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
# ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
# POSSIBILITY OF SUCH DAMAGE.
# #L%
###

# Transforms CSV data into HTML that uses Flot Javascript charts.

# Charts produced:
#   - Iteration x Time for minimum resolution image, by method
#   - Iteration x Time for maximum resolution image, by method
#   - Resolution x Time at first iteration, by method
#   - Resolution x Time at last iteration, by method

# Total charts: 8 (4 for "cheap" and 4 for "expensive")

import math, os, string

# NB: Presumably there is a slick way to avoid having two nearly identical
# methods (generateIterationChart and generateResolutionChart) iterating over
# different list indices, but my Python knowledge is weak.

# Iteration x Time at a given resolution, by method
def generateIterationChart(name, data, methods, resolution_index):
  print '    // Iteration x Time at resolution #' + str(resolution_index) + ':'
  resolution_count = len(data)
  iteration_count = len(data[0])
  method_count = len(methods)
  print '    var ' + name + ' = {'
  for m in range(0, method_count):
    print '        "method' + str(m + 1) + '": {'
    print '            label: "' + methods[m] + '",'
    print '            data: [',
    for i in range(0, iteration_count):
      if i < iteration_count - 1:
        suffix = '],'
      else:
        suffix = ']'
      print '[' + str(i) + ', ' + str(data[resolution_index][i][m]) + suffix,
    print '],'
    checked = methods[m] == 'ImageJ' \
      or methods[m] == 'Imglib (Array)' \
      or methods[m] == 'Imglib (Planar)' \
      or methods[m] == 'Raw';
    print '            checked: ' + string.lower(str(checked))
    print '        },'
  print '    };'

# Resolution x Time at a given iteration, by method
def generateResolutionChart(name, data, methods, iteration_index):
  print '    // Resolution x Time at iteration #' + str(iteration_index) + ':'
  resolutions = range(1, 26, 3)
  resolution_count = len(data)
  iteration_count = len(data[0])
  method_count = len(methods)
  print '    var ' + name + ' = {'
  for m in range(0, method_count):
    print '        "method' + str(m + 1) + '": {'
    print '            label: "' + methods[m] + '",'
    print '            data: [',
    for r in range(0, resolution_count):
      if r < resolution_count - 1:
        suffix = '],'
      else:
        suffix = ']'
      print '[' + str(resolutions[r]) + ', ' + \
        str(data[r][iteration_index][m]) + suffix,
    print ']'
    print '        },'
  print '    };'

# reads data from CSV files into 3D array dimensioned:
# [resolution_count][iteration_count][method_count]
def process(prefix):
  methods = []

  # loop over image resolutions
  data = []
  for p in range(1, 26, 3):
    # compute filename
    res = round(math.sqrt(1000000 * p))
    s_res = str(int(res))
    path_prefix = 'results-' + prefix + '-' + s_res + 'x' + s_res
    in_path = path_prefix + '.csv'

    # read data file
    with open(in_path, 'r') as f:
      lines = f.readlines()

    # loop over iterations
    header = True
    data0 = []
    for line in lines:
      items = line.rstrip().split('\t')
      items.pop(0)
      if header:
        header = False
        methods = items
      else:
        # loop over methods
        data1 = []
        for item in items:
          data1.append(int(item))
        data0.append(data1)
    data.append(data0)

  resolution_count = len(data)
  iteration_count = len(data[0])
  method_count = len(methods)

  # Iteration x Time for minimum resolution image, by method
  generateIterationChart('res_' + prefix + '_min', data, methods, 0)
  # Iteration x Time for maximum resolution image, by method
  generateIterationChart('res_' + prefix + '_max', \
    data, methods, resolution_count - 1)
  # Resolution x Time at first iteration, by method
  generateResolutionChart('iter_' + prefix + '_first', data, methods, 0)
  # Resolution x Time at last iteration, by method
  generateResolutionChart('iter_' + prefix + '_last', \
    data, methods, iteration_count - 1)

process('cheap')
process('expensive')
