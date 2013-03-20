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
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import co.ntier.web.pipeline.core.ResourceCompiler;

@Slf4j
public class ScriptsTag extends TagSupport {

	private static final long serialVersionUID = 1L;
	
	public static int CACHE_MINUTES = 5;

	private List<String> scripts;

	@Getter @Setter
	private String ref;
	
	private boolean production;

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
		if (!production) {
			createMinified();
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
	private void createMinified() {
		ServletContext ctx = pageContext.getServletContext();

		String endFile = ctx.getRealPath(ref);
		File file = new File(endFile);
		if (file.exists()) {
			long diff = System.currentTimeMillis() - file.lastModified();
			if( diff > 60 * 1000 * CACHE_MINUTES){
				log.warn("Haven't recompiled in {} minutes. Will recompile {}", CACHE_MINUTES, file.getAbsoluteFile());
			}else{
				log.warn("Minified resource already exists: {} ({})",
						file.getAbsoluteFile(), diff);
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

		// compile the resources
		String source = compiler().compile(files);

		// create the new file
		FileUtils.writeStringToFile(file, source);
		log.info("Created compiled resource at", file.getAbsolutePath(),
				file.getAbsoluteFile());
	}

	private void writeResourceTag(String url) {
		write("<script src='" + url + "' type='text/javascript'></script>\n");
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
		return getContext().getBean(ResourceCompiler.class);
	}

	private WebApplicationContext getContext() {
		ServletContext sc = pageContext.getServletContext();
		WebApplicationContext wac = WebApplicationContextUtils
				.getRequiredWebApplicationContext(sc);
		return wac;
	}

	
}
