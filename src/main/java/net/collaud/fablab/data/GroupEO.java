package net.collaud.fablab.data;

import java.io.Serializable;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.Size;

/**
 *
 * @author gaetan
 */
@Entity
@Table(name = "t_group")
public class GroupEO extends AbstractDataEO implements Serializable {

	private static final long serialVersionUID = 1L;
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "group_id", nullable = false)
	private Integer groupId;

	@Size(min = 1, max = 45)
	@Column(name = "technicalname", nullable = false)
	private String technicalname;

	@Size(min = 1, max = 45)
	@Column(name = "name", nullable = false)
	private String name;

	@ManyToMany(mappedBy = "groupsList", fetch = FetchType.LAZY)
	private List<UserEO> usersList;

	public GroupEO() {
	}

	public GroupEO(Integer groupId) {
		this.groupId = groupId;
	}

	public GroupEO(Integer groupId, String technicalname, String name) {
		this.groupId = groupId;
		this.technicalname = technicalname;
		this.name = name;
	}

	@Override
	public Integer getId() {
		return getGroupId();
	}

	public Integer getGroupId() {
		return groupId;
	}

	public void setGroupId(Integer groupId) {
		this.groupId = groupId;
	}

	public String getTechnicalname() {
		return technicalname;
	}

	public void setTechnicalname(String technicalname) {
		this.technicalname = technicalname;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<UserEO> getUsersList() {
		return usersList;
	}

	public void setUsersList(List<UserEO> usersList) {
		this.usersList = usersList;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (groupId != null ? groupId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GroupEO)) {
			return false;
		}
		GroupEO other = (GroupEO) object;
		if ((this.groupId == null && other.groupId != null) || (this.groupId != null && !this.groupId.equals(other.groupId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return name;
	}

}
