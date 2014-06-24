package net.collaud.fablab.ctrl.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import net.collaud.fablab.data.type.AuditObject;

/**
 *
 * @author gaetan
 */
@FacesConverter(forClass = AuditObject.class, value = "auditObjectConverter")
public class AuditObjectConverter implements Converter {

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				int number = Integer.parseInt(submittedValue);
				return AuditObject.values()[number];
			} catch (NumberFormatException ex) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", ""), ex);
			}
		}
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((AuditObject) value).ordinal());
		}
	}

}
