package net.collaud.fablab.util;

import java.util.ArrayList;
import java.util.List;
import net.collaud.fablab.data.UserEO;

/**
 *
 * @author Gaétan
 */
abstract public class Filters {
	public static List<UserEO> filterUsers(List<UserEO> listUsers, String query){
		List<UserEO> results = new ArrayList<>();

		for (UserEO u : listUsers) {
			if (u.getFirstLastName().toLowerCase().contains(query.toLowerCase())) {
				results.add(u);
			}
		}
		return results;
	}
}
