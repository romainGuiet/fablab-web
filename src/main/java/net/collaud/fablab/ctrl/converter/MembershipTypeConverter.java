package net.collaud.fablab.ctrl.converter;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import javax.faces.convert.FacesConverter;
import net.collaud.fablab.dao.itf.MembershipTypeDAO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.exceptions.FablabException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 *
 * @author gaetan
 */
@FacesConverter(forClass = MembershipTypeEO.class)
@Component
public class MembershipTypeConverter implements Converter {

	@Autowired
	private MembershipTypeDAO membershipTypeDao;

	public MembershipTypeConverter() {
	}

	@Override
	public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
		if (submittedValue.trim().equals("")) {
			return null;
		} else {
			try {
				int number = Integer.parseInt(submittedValue);
				
				
				
				return membershipTypeDao.getById(number);

//				for (MembershipTypeEO p : listUser) {
//					if (p.getMembershipTypeId() == number) {
//						return p;
//					}
//				}
			} catch (FablabException | NumberFormatException exception) {
				throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Not a valid membershiptype"));
			}
		}
	}

	@Override
	public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
		if (value == null || value.equals("")) {
			return "";
		} else {
			return String.valueOf(((MembershipTypeEO) value).getMembershipTypeId());
		}
	}

}
