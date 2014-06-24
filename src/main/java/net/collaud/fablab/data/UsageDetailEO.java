package net.collaud.fablab.data;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author gaetan
 */
@Entity
@Table(name = "v_usage_detail")
@NamedQueries({
	@NamedQuery(name = UsageDetailEO.SELECT_FROM_USER,
			query = "SELECT u FROM UsageDetailEO u WHERE u.user=:" + UsageDetailEO.PARAM_USER),
	@NamedQuery(name = UsageDetailEO.SELECT_FROM_IDS,
			query = "SELECT u FROM UsageDetailEO u WHERE u.usageId IN :" + UsageDetailEO.PARAM_IDS),})
public class UsageDetailEO extends UsageEO implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String SELECT_FROM_USER = "UsageEO.selectFromUser";
	public static final String SELECT_FROM_IDS = "UsageEO.findByIds";
	public static final String PARAM_IDS = "ids";
	public static final String PARAM_USER = "user";

	@Column(name = "price", nullable = false)
	private float price;

	public UsageDetailEO() {
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

}
