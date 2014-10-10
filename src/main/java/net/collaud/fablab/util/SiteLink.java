package net.collaud.fablab.util;

import java.io.IOException;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import net.collaud.fablab.file.ConfigFileHelper;
import net.collaud.fablab.file.FileHelperFactory;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;

/**
 *
 * @author gaetan
 */
@ManagedBean(name = "link")
@Controller
public class SiteLink {
	private static final Logger LOG = Logger.getLogger(SiteLink.class);

	public static final String PAGE_HOME = "home.xhtml";
	public static final String PAGE_LOGIN = "login.xhtml";
	public static final String PAGE_ACCESS_DENIED = "accessDenied.xhtml";

	public void goTo(String url) {
		try {
			FacesContext context = FacesContext.getCurrentInstance();
			String app = context.getExternalContext().getRequestContextPath();
			context.getExternalContext().redirect(app + "/" + url);
		} catch (IOException e) {
			LOG.error("Cannort redirect", e);
		}
	}
		
	public String menuActive(String path){
		if(FacesContext.getCurrentInstance().getViewRoot().getViewId().contains(path)){
			return " class=\"active\" ";
		}
		return "";
	}
	
	public boolean isActive(String path){
		return FacesContext.getCurrentInstance().getViewRoot().getViewId().contains(path);
	}
	
	public boolean getShowReservation(){
		return FileHelperFactory.getConfig().getAsInt(ConfigFileHelper.SHOW_RESERVATION, 1)==1;
	}
}
