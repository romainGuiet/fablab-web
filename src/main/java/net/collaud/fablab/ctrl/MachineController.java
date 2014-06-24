package net.collaud.fablab.ctrl;

import java.io.Serializable;
import java.util.List;
import java.util.ResourceBundle;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import net.collaud.fablab.ctrl.util.JsfUtil;
import net.collaud.fablab.ctrl.util.JsfUtil.PersistAction;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.MachineService;
import org.apache.log4j.Logger;

@ManagedBean(name = "machineController")
@ViewScoped
public class MachineController extends AbstractController implements Serializable {

	private static final Logger LOG = Logger.getLogger(MachineController.class);

	@EJB
	private MachineService machineService;

	private List<MachineEO> items = null;
	private MachineEO selected;

	public MachineController() {
	}

	public MachineEO getSelected() {
		return selected;
	}

	public void setSelected(MachineEO selected) {
		this.selected = selected;
	}

	protected void setEmbeddableKeys() {
	}

	protected void initializeEmbeddableKey() {
	}

	public MachineEO prepareCreate() {
		selected = new MachineEO();
		initializeEmbeddableKey();
		return selected;
	}

	public void create() {
		persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MachineEOCreated"));
		if (!JsfUtil.isValidationFailed()) {
			items = null;    // Invalidate list of items to trigger re-query.
		}
	}

	public void update() {
		persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MachineEOUpdated"));
	}

	public void destroy() {
		persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MachineEODeleted"));
		if (!JsfUtil.isValidationFailed()) {
			selected = null; // Remove selection
			items = null;    // Invalidate list of items to trigger re-query.
		}
	}

	public List<MachineEO> getItems() {
		if (items == null) {
			try {
				items = machineService.getAllMachines();
			} catch (FablabException ex) {
				LOG.error("Cannot retrieve machines lists", ex);
			}
		}
		return items;
	}

	private void persist(PersistAction persistAction, String successMessage) {
		if (selected != null) {
			setEmbeddableKeys();
			try {
				machineService.save(selected);
				JsfUtil.addSuccessMessage(successMessage);
			} catch (EJBException ex) {
				String msg = "";
				Throwable cause = ex.getCause();
				if (cause != null) {
					msg = cause.getLocalizedMessage();
				}
				if (msg.length() > 0) {
					JsfUtil.addErrorMessage(msg);
				} else {
					JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
				}
			} catch (Exception ex) {
				LOG.error("Cannot save machine " + selected, ex);
				JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
			}
		}
	}

	public List<MachineEO> getItemsAvailableSelectMany() {
		try {
			return machineService.getAllMachines();
		} catch (FablabException ex) {
			LOG.error("Cannot retrieve machine lists", ex);
		}
		return null;
	}

	public List<MachineEO> getItemsAvailableSelectOne() {
		try {
			return machineService.getAllMachines();
		} catch (FablabException ex) {
			LOG.error("Cannot retrieve machine lists", ex);
		}
		return null;
	}
}
