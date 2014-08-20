package net.collaud.fablab;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.bean.ViewScoped;
import org.apache.log4j.Logger;

@ManagedBean(name = "customContentCtrl")
@RequestScoped
public class CustomContentController implements Serializable {

	private static final Logger LOG = Logger.getLogger(CustomContentController.class);

	public static final String FILE_LOGIN_HEADER = "login_header.html";

	public static String loginContent;

	public CustomContentController() {
	}

	public String getLoginHeaderContent() {
		if (loginContent == null) {
			BufferedReader br = null;
			try {
				br = new BufferedReader(new FileReader(FILE_LOGIN_HEADER));
				StringBuilder sb = new StringBuilder();
				String line = br.readLine();

				while (line != null) {
					sb.append(line);
					sb.append(System.lineSeparator());
					line = br.readLine();
				}
				loginContent = sb.toString();
			} catch (FileNotFoundException ex) {
				LOG.warn("File " + FILE_LOGIN_HEADER + " not found");
			} catch (IOException ex) {
				LOG.warn("Cannot read file " + FILE_LOGIN_HEADER);
			} finally {
				try {
					if (br != null) {
						br.close();
					}
				} catch (IOException ex) {
				}
			}
		}
		return loginContent;
	}

}
