package net.collaud.fablab.dao.itf;

import java.util.List;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface MembershipTypeDAO {

	public List<MembershipTypeEO> findAll() throws FablabException;
	
	public MembershipTypeEO getById(Integer id) throws FablabException;
	
	public MembershipTypeEO getByName(String name) throws FablabException;
	
}
