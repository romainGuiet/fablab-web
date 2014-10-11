package net.collaud.fablab.service.itf;

import java.util.List;
import javax.ejb.Local;
import net.collaud.fablab.data.GroupEO;
import net.collaud.fablab.data.MachineTypeEO;
import net.collaud.fablab.data.MembershipTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.virtual.LDAPSyncResult;
import net.collaud.fablab.exceptions.FablabException;

/**
 *
 * @author gaetan
 */
@Local
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
	 * Sync all users with LDAP.
	 *
	 * If a new user is found in the LDAP server, it will be added to the database.
	 *
	 * If a user is not found in the LDAP server, it will be disabled.
	 *
	 * If a user was disabled but is now present, it will be reactivated.
	 *
	 * @return
	 * @throws FablabException
	 */
	LDAPSyncResult syncWithLDAP() throws FablabException;

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
