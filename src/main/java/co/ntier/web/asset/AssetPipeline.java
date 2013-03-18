package co.ntier.web.asset;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.SourceFile;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.WarningLevel;

public class AssetPipeline {

	/**
	 * @param code
	 *            JavaScript source code to compile.
	 * @return The compiled version of the code.
	 */
	public static String compile(String code) {
		com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();

		CompilerOptions options = new CompilerOptions();
		// Advanced mode is used here, but additional options could be set, too.
		// CompilationLevel.ADVANCED_OPTIMIZATIONS.setOptionsForCompilationLevel(options);

		SourceFile extern = new SourceFile("/Users/dave/Desktop/tmp/extern.js");
		/*
		// To get the complete set of externs, the logic in
		// CompilerRunner.getDefaultExterns() should be used here.
		JSSourceFile extern = JSSourceFile.fromCode("externs.js",
				"function alert(x) {}");

		// The dummy input name "input.js" is used here so that any warnings or
		// errors will cite line numbers in terms of input.js.
		JSSourceFile input = JSSourceFile.fromCode("input.js", code);

		*/
		SourceFile input = new SourceFile("/Users/dave/Desktop/tmp/input.js");
		
		// compile() returns a Result, but it is not needed here.
		compiler.compile(extern, input, options);

		// The compiler is responsible for generating the compiled code; it is
		// not
		// accessible via the Result.
		String source = compiler.toSource();
		return source;
	}
	
	public static void main(String[] args) throws Exception
	  {
	    // These are external JavaScript files you reference but don't want changed
	    String externalJavascriptResources[] = {
	        // "jquery.js",
	        // "jqueryui.js"
	    };
	    
	    // These are the files you want optimized
	    String primaryJavascriptToCompile[] = { 
	        "/Users/dave/Desktop/tmp/input.js",
	        "/Users/dave/Desktop/tmp/extern.js",
	    };
	    // This is where the optimized code will end up
	    String outputFilename = "combined.min.js";
	 
	    com.google.javascript.jscomp.Compiler.setLoggingLevel(Level.INFO);
	    com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
	 
	    CompilerOptions options = new CompilerOptions();
	    CompilationLevel.SIMPLE_OPTIMIZATIONS.setOptionsForCompilationLevel(options);
//	    options.setAliasKeywords(true);
//	    options.setAliasAllStrings(true);
	    
	    options.setCollapseAnonymousFunctions(true);
	    options.setCollapseObjectLiterals(true);
	    options.setCollapseProperties(true);
	    options.setCollapsePropertiesOnExternTypes(true);
	    options.setCollapseVariableDeclarations(true);
	    options.setOptimizeArgumentsArray(true);
	    options.setOptimizeCalls(true);
	    options.setOptimizeParameters(true);
	    options.setOptimizeReturns(true);
	    
	    VariableRenamingPolicy newVariablePolicy = VariableRenamingPolicy.ALL;
		PropertyRenamingPolicy newPropertyPolicy = PropertyRenamingPolicy.ALL_UNQUOTED;
		options.setRenamingPolicy(newVariablePolicy, newPropertyPolicy);
	 
		
		options.setAliasExternals(true);
		options.setAliasableGlobals("alert");
		
	    WarningLevel.VERBOSE.setOptionsForWarningLevel(options);
	 
	    List<JSSourceFile> externalJavascriptFiles = new ArrayList<JSSourceFile>();
	    for (String filename : externalJavascriptResources){
	      externalJavascriptFiles.add(JSSourceFile.fromFile(filename));
	    }
	 
	    List<JSSourceFile> primaryJavascriptFiles = new ArrayList<JSSourceFile>();
	    for (String filename : primaryJavascriptToCompile)
	    {
	      primaryJavascriptFiles.add(JSSourceFile.fromFile(filename));
	    }
	 
	    compiler.compile(externalJavascriptFiles, primaryJavascriptFiles, options);
	    compiler.optimize();
	    
	 
	    for (JSError message : compiler.getWarnings())
	    {
	      System.err.println("Warning message: " + message.toString());
	    }
	 
	    for (JSError message : compiler.getErrors())
	    {
	      System.err.println("Error message: " + message.toString());
	    }
	 
	    System.out.println(compiler.toSource());
	  }
}
