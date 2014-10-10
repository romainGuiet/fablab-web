package net.collaud.fablab.ctrl.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import net.collaud.fablab.dao.itf.GroupDAO;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.exceptions.FablabException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gaetan
 */
@FacesConverter(forClass = GroupEO.class, value = "groupConverter")
@Component
public class GroupConverter implements Converter {

	@Autowired
	private GroupDAO groupDao;

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				int number = Integer.parseInt(submittedValue);
				return groupDao.getById(number);
			} catch (FablabException | NumberFormatException exception) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"));
			}
		}
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((GroupEO) value).getGroupId());
		}
	}

}
