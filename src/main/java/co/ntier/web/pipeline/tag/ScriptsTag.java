package co.ntier.web.pipeline.tag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.FileUtils;

import co.ntier.web.pipeline.core.PipelineConstants;
import co.ntier.web.pipeline.core.ResourceCompiler;

@Slf4j
public class ScriptsTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	public static int CACHE_MINUTES = 5;

	private List<String> scripts;

	@Getter @Setter
	private String ref;
	
	public void addScript(ScriptTag tag) {
		scripts.add(tag.getSrc());
	}

	@Override
	public int doStartTag() throws JspException {
		scripts = new ArrayList<String>();
		return EVAL_BODY_INCLUDE;
	}

	@Override
	public int doEndTag() throws JspException {
		Object production = pageContext.getServletContext().getAttribute( PipelineConstants.IS_PRODUCTION_KEY );
		if ( Boolean.TRUE.equals(production) ){
			verifyCompiled();
			writeResourceTag(ref);
		} else {
			write("\n<!-- Start Pipelined Resources for '" + ref + "' -->\n");
			for (String tag : scripts) {
				writeResourceTag(tag);
			}
			write("<!-- End Pipelined Resources for '" + ref + "' -->");
		}
		return super.doEndTag();
	}

	@SneakyThrows
	private void verifyCompiled() {
		ServletContext ctx = pageContext.getServletContext();

		String endFile = ctx.getRealPath(ref);
		File file = new File(endFile);
		if (file.exists()) {
			// TODO change this to store the md5 of each file and only update if changed
			long diff = System.currentTimeMillis() - file.lastModified();
			if( diff > 60 * 1000 * CACHE_MINUTES){
				log.warn("Haven't recompiled in {} minutes. Will recompile {}", CACHE_MINUTES, file.getAbsoluteFile());
			}else{
				log.warn("Minified resource already exists: {} ({})", file.getAbsoluteFile(), diff);
				return;
			}
		}

		// build a list of files
		List<String> files = new ArrayList<String>();
		for (String tag : scripts) {
			// TODO only do if it's relative
			String path = ctx.getRealPath(tag);
			files.add(path);
		}

		try{
			// compile the resources
			String source = compiler().compile(files);
	
			// create the new file
			FileUtils.writeStringToFile(file, source);
			log.info("Created compiled resource at {}", file.getAbsolutePath() );
		}catch(IllegalStateException e){
			log.warn("Couldn't compile resources: {}", e.getMessage());
		}
	}

	private void writeResourceTag(String url) {
		String path = pageContext.getServletContext().getContextPath();
		write("<script src='" + path + url + "' type='text/javascript'></script>\n");
	}

	@SneakyThrows
	private void write(String msg) {
		try {
			pageContext.getOut().write(msg);
		} catch (IOException e) {
			throw new JspException("Failed writing from tag", e);
		}
	}

	private ResourceCompiler compiler() {
		Object compiler = pageContext.getServletContext().getAttribute( PipelineConstants.RESOURCE_COMPILER_KEY);
		if( compiler == null || !(compiler instanceof ResourceCompiler) ){
			throw new IllegalStateException("ServletContext does not have a ResourceCompiler attribute. Found: " + compiler);
		}
		return (ResourceCompiler) compiler;
	}


	
}
