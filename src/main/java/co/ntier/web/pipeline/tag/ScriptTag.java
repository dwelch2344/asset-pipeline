package co.ntier.web.pipeline.tag;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.Tag;
import javax.servlet.jsp.tagext.TagSupport;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * A simple tag class that adds itself to the containing parent.  
 */
@ToString(of={"src"})
public class ScriptTag extends TagSupport{

	private static final long serialVersionUID = 1L;
	
	@Getter @Setter
	private String src;
	
	@Override
    public int doStartTag() throws JspException {
		Tag parent = getParent();
		if( parent instanceof ScriptsTag){
	        ScriptsTag parentTag = (ScriptsTag) getParent();
	        parentTag.addScript(this);
		}
        return SKIP_BODY;
    }

}
