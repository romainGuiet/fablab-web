package net.collaud.fablab.service.itf;

import java.util.List;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
public interface UserService {

	UserEO save(UserEO selectedUser) throws FablabException;

	UserEO saveMachineAuthorized(UserEO user, List<MachineTypeEO> listTypes) throws FablabException;

	List<UserEO> getAllUsers() throws FablabException;

	/**
	 * @param rfid
	 * @return null if no one found, user otherwise
	 * @throws FablabException 
	 */
	UserEO findByRFID(String rfid) throws FablabException;

	UserEO findByLogin(String username) throws FablabException;

	UserEO getById(int userid) throws FablabException;

	List<MembershipTypeEO> getListMembershipTypes() throws FablabException;

	/**
	 * get the number of days remaining for the user subscription. A negativ number mean that the
	 * subscription is expired. Integer.MIN_VALUE means that the user has never confirm a
	 * subscription (but he sould).
	 *
	 * If the membership type has no subscription fee, Integer.MAX_VALUE will be returned.
	 *
	 * @param user
	 * @return
	 * @throws FablabException
	 */
	Integer daysToEndOfSubscription(UserEO user) throws FablabException;

	/**
	 * Same as daysToEndOfSubscription but for the current user connected.
	 *
	 * @see UserService.daysToEndOfSubscription
	 * @return
	 * @throws FablabException
	 */
	Integer daysToEndOfSubscriptionForCurrentUser() throws FablabException;

	void remove(UserEO user) throws FablabException;

	public List<GroupEO> getListGroups() throws FablabException;

}
