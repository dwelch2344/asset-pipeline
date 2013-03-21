package co.ntier.web.pipeline.core;

import javax.servlet.ServletContext;

public final class PipelineConstants {

	/**
	 * Used as a {@link ServletContext} attribute key; should be set to {@code true} if resources should be optimized. 
	 */
	public static final String IS_PRODUCTION_KEY = "co.ntier.web.pipeline.production";
	
	/**
	 * Used as a {@link ServletContext} attribute key; should be set to {@code ResourceCompiler} if resources should be optimized. 
	 */
	public static final String RESOURCE_COMPILER_KEY = "co.ntier.web.pipeline.compiler";
	
	// No instantiation for you
	private PipelineConstants() {}
}
