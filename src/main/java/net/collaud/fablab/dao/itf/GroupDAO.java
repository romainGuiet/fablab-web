package net.collaud.fablab.dao.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
public interface GroupDAO {

	GroupEO getById(Integer id) throws FablabException;
	
	List<GroupEO> getAllGroups() throws FablabException;
	
}
