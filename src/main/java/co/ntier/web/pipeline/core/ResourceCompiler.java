package co.ntier.web.pipeline.core;

import java.util.List;

/**
 * Provides a simple interface for compiling resources.
 */
public interface ResourceCompiler {

	/**
	 * Compiles a list of resources
	 * @param files A list of absolute path references
	 * @return the compiled source
	 */
	String compile(List<String> files);

}
