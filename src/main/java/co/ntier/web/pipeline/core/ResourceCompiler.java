package co.ntier.web.pipeline.core;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.google.javascript.jscomp.CheckLevel;
import com.google.javascript.jscomp.CompilationLevel;
import com.google.javascript.jscomp.CompilerOptions;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.JSSourceFile;
import com.google.javascript.jscomp.PropertyRenamingPolicy;
import com.google.javascript.jscomp.VariableRenamingPolicy;
import com.google.javascript.jscomp.WarningLevel;

/**
 * TODO rename this
 *
 */
public class ResourceCompiler {
	
	public String compile(List<String> externalJavascriptResources, List<String>primaryJavascriptToCompile){
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
	    
	    options.setAggressiveVarCheck(CheckLevel.WARNING);
	    
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
	 
	    return compiler.toSource();
	}

	public String compile(List<String> files) {
		return compile(new ArrayList<String>(), files);
	}
}
