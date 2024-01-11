#!/usr/bin/env groovy

/*
 * #%L
 * SciJava Operations: a framework for reusable algorithms.
 * %%
 * Copyright (C) 2018 SciJava developers.
 * %%
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDERS OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * #L%
 */

debug = System.getenv('DEBUG')
def debug(msg) {
	if (debug) System.err.println("[DEBUG] $msg")
}

@Grab('org.apache.velocity:velocity:1.7')
import org.apache.velocity.app.VelocityEngine

// TODO: Get path to Groovy script and make these dirs relative to that.
templateDirectory = 'templates'
outputDirectory = 'src'

knownFiles = new java.util.HashSet();

/* Gets the last modified timestamp for the given file. */
def timestamp(dir, file) {
	if (file == null) return Long.MAX_VALUE;
	file = new java.io.File(dir, file);
	knownFiles.add(file);
	return file.lastModified();
}

/* Processes a template using Apache Velocity. */
def processTemplate(engine, context, templateFile, outFilename) {
	debug("processTemplate('$engine', '$context', '$templateFile', '$outFilename')")

	if (outFilename == null) return; // nothing to do

	// create output directory if it does not already exist
	outFile = new java.io.File(outputDirectory, outFilename);
	knownFiles.add(outFile);
	if (outFile.getParentFile() != null) outFile.getParentFile().mkdirs();

	// apply the template and write out the result
	t = engine.getTemplate(templateFile);
	writer = new StringWriter();
	t.merge(context, writer);
	out = new PrintWriter(outFile, "UTF-8");
	out.print(writer.toString());
	out.close();
}

/* Evaluates a string using Groovy. */
def parseValue(sh, translationsFile, key, expression) {
	try {
		result = sh.evaluate(expression)
		sh.setVariable(key, result)
		return result
	}
	catch (groovy.lang.GroovyRuntimeException e) {
		print("[WARNING] $translationsFile: " +
			"key '$key' has unparseable value: " + e.getMessage());
	}
}

/* Reads a translations File */
def readTranslation(engine, globalContext, reader, templateSubdirectory, templateFile, translationsFile, isInclude){
	sh = new groovy.lang.GroovyShell();
	for (;;) {
		// read the line
		line = reader.readLine();

		if (line == null) break;
		// check if the line starts a new section
		if (line.startsWith("[") && line.endsWith("]")) {
			// if we are parsing a .include file, return when we hit any sections
			if(isInclude){
				println("[WARNING] $translationsFile: Section definition in .include file. Ending processing of $translationsFile");
				return context; 
			}
			// write out the previous file
			processTemplate(engine, context, templateFile, outputFilename);

			// start a new file
			outputFilename = line.substring(1, line.length() - 1);
			if (!templateDirectory.equals(templateSubdirectory)) {
				subPath = templateSubdirectory.substring(templateDirectory.length() + 1);
				outputFilename = "$subPath/$outputFilename";
			}
			context = new org.apache.velocity.VelocityContext(globalContext);
			continue;
		}

		// ignore blank lines
		trimmedLine = line.trim();
		if (trimmedLine.isEmpty()) continue;

		// ignore comments
		if (trimmedLine.startsWith("#")) continue;

		// include any global files
		if (trimmedLine.startsWith(".include")){
			includeFile = line.substring(9);
			if(includeFile.startsWith("templates")){
				includeSubdirectory = includeFile.substring(0, includeFile.lastIndexOf("/"))
				includeFile = includeFile.substring(includeFile.lastIndexOf("/"))
			}
			else{
			includeSubdirectory = templateSubdirectory
			}
			globalReader = new java.io.BufferedReader(new java.io.FileReader("$includeSubdirectory/$includeFile"));
			encapsulatedContext = new org.apache.velocity.VelocityContext(context)
			context = readTranslation(engine, encapsulatedContext, globalReader, templateSubdirectory, templateFile, includeFile, true) 
			continue;
		}
		
		if (!line.contains('=')) {
			print("[WARNING] $translationsFile: Ignoring spurious line: $line");
			continue;
		}

		int idx = line.indexOf('=');
		key = line.substring(0, idx).trim();
		value = line.substring(idx + 1);

		if (value.trim().equals('```')) {
			// multi-line value
			builder = new StringBuilder();
			for (;;) {
				line = reader.readLine();
				if (line == null) {
					throw new RuntimeException("Unfinished value: " + builder.toString());
				}
				if (line.equals('```')) {
					break;
				}
				if (builder.length() > 0) {
					builder.append("\n");
				}
				builder.append(line);
			}
			value = builder.toString();
		}

		context.put(key, parseValue(sh, translationsFile, key, value));
	}
	
	return context;
}

/*
 * Translates a template into many files in the outputDirectory,
 * given a translations file in INI style; e.g.:
 *
 * [filename1]
 * variable1 = value1
 * variable2 = value2
 * ...
 * [filename2]
 * variable1 = value3
 * variable2 = value4
 * ...
 */
def translate(templateSubdirectory, templateFile, translationsFile) {
	debug("translate('$templateSubdirectory', '$templateFile', '$translationsFile')")

	// initialize the Velocity engine
	engine = new org.apache.velocity.app.VelocityEngine();
	p = new java.util.Properties();
	// fail if template uses an invalid expression; e.g., an undefined variable
	p.setProperty("runtime.references.strict", "true");
	// tell Velocity where the templates are located
	p.setProperty("file.resource.loader.path", "$templateSubdirectory");
	// tell Velocity to log to stderr rather than to a velocity.log file
	p.setProperty(org.apache.velocity.runtime.RuntimeConstants.RUNTIME_LOG_LOGSYSTEM_CLASS,
			"org.apache.velocity.runtime.log.SystemLogChute");
	engine.init(p);

	// read translation lines
	outputFilename = null;
	context = globalContext = new org.apache.velocity.VelocityContext();
	reader = new java.io.BufferedReader(new java.io.FileReader("$templateSubdirectory/$translationsFile"));

	readTranslation(engine, context, reader, templateSubdirectory, templateFile, translationsFile, false);

	reader.close();

	// process the template
	processTemplate(engine, context, templateFile, outputFilename);
}

/* Recursively translates all templates in the given directory. */
def translateDirectory(templateSubdirectory) {
	debug("translateDirectory('$templateSubdirectory')")

	for (file in new java.io.File(templateSubdirectory).listFiles()) {
		if (file.isDirectory()) {
			// process subdirectories recursively
			translateDirectory(file.getPath());
		}
		else {
			// process Velocity template files only
			name = file.getName();
			if (!name.endsWith('.vm')) continue;
			prefix = name.substring(0, name.lastIndexOf('.'));
			translate(templateSubdirectory, name, prefix + '.list');
		}
	}
}

try {
	translateDirectory(templateDirectory);
}
catch (Throwable t) {
	t.printStackTrace(System.err);
	throw t;
}
