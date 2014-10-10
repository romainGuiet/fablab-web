package net.collaud.fablab.ctrl.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import net.collaud.fablab.dao.itf.MachineDAO;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.exceptions.FablabException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gaetan
 */
@FacesConverter(forClass = MachineEO.class)
@Component
public class MachineConverter implements Converter {

	@Autowired
	private MachineDAO machineDao;

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				int number = Integer.parseInt(submittedValue);
				return machineDao.getById(number);
			} catch (FablabException | NumberFormatException ex) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid player"), ex);
			}
		}
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((MachineEO) value).getMachineId());
		}
	}

}
