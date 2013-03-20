package co.ntier.web.pipeline.core;

import java.util.List;

/**
 * Provides a simple interface for compiling resources.
 */
public interface ResourceCompiler {

	String compile(List<String> files);

}
