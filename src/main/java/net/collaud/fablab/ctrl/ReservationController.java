package net.collaud.fablab.ctrl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import net.collaud.fablab.Constants;
import net.collaud.fablab.data.MachineEO;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.MachineService;
import net.collaud.fablab.service.itf.ReservationService;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@ManagedBean(name = "reservationCtrl")
@ViewScoped
@Controller
public class ReservationController extends AbstractController implements Serializable, Constants {

	private static final Logger LOG = Logger.getLogger(ReservationController.class);

	@Autowired
	private ReservationService reservationService;

	@Autowired
	private MachineService machineService;

	private List<Integer> selectedMachines;
	private List<MachineEO> listMachines;

	public ReservationController() {

	}

	@PostConstruct
	private void init() {
		//FIXME
//		try {
//			listMachines = machineService.getAllMachines();
//			selectedMachines = new ArrayList<>(listMachines.size());
//			for (MachineEO m : listMachines) {
//				selectedMachines.add(m.getId());
//			}
//		} catch (FablabException ex) {
//			addErrorAndLog("Cannot get all machines", ex);
//		}
	}

	public List<MachineEO> getListMachines() {
		return listMachines;
	}

	public List<Integer> getSelectedMachines() {
		return selectedMachines;
	}
	public String getSelectedMachinesAsString() {
		return selectedMachines.toString();
	}

	public void setSelectedMachines(List<Integer> selectedMachines) {
		this.selectedMachines = selectedMachines;
	}

}
