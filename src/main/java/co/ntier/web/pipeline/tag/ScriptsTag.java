package co.ntier.web.pipeline.tag;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import org.apache.commons.io.IOUtils;

import com.google.common.collect.Lists;

@Slf4j
public class ScriptsTag extends TagSupport{

	private static final long serialVersionUID = 1L;
	
	private boolean production;
	
	private List<String> scripts;
	
	@Getter @Setter
	private String ref;
	
	public void addScript(ScriptTag tag){
		scripts.add( tag.getSrc() );
	}
	
	@Override
	public int doStartTag() throws JspException {
		scripts = Lists.newArrayList();
		return EVAL_BODY_INCLUDE;
	}
	
	@Override
	public int doEndTag() throws JspException {
		// WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(pageContext.getServletContext());
		if( production ){
			// TODO if we haven't printed it yet on this request...
			writeResourceTag(ref);
		} else {
			write("\n<!-- Start Pipelined Resources for '" + ref + "' -->\n");
			for(String tag : scripts){
				foobar( tag ); 
				writeResourceTag( tag );
			}
			write("<!-- End Pipelined Resources for '" + ref + "' -->");
		}
		return super.doEndTag();
	}
	
	@SneakyThrows
	private void foobar(String resource) {
		ServletContext ctx = pageContext.getServletContext();
		InputStream stream = ctx.getResourceAsStream(resource);
		if( stream == null ){
			log.warn("Failed loading resource: " + resource);
		}else{
			String data = IOUtils.toString(stream);
			System.out.println(data);
		}
		
	}

	private void writeResourceTag(String url){
		write("<script src='" + url + "' type='text/javascript'></script>\n");
	}
	
	@SneakyThrows
	private void write(String msg){
		try {
			pageContext.getOut().write(msg);
		} catch (IOException e) {
			throw new JspException("Failed writing from tag", e);
		}
	}

}
