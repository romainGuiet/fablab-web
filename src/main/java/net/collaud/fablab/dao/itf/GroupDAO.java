package net.collaud.fablab.dao.itf;

import java.util.List;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface GroupDAO {

	GroupEO getById(Integer id) throws FablabException;
	
	List<GroupEO> getAllGroups() throws FablabException;
	
}
