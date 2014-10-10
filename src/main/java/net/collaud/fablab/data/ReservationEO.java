package net.collaud.fablab.data;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author gaetan
 */
@Entity
@Table(name = "t_reservation")
@XmlRootElement
@NamedQueries({
	@NamedQuery(name = ReservationEO.SELECT_BY_TIME, query = ""
			+ "SELECT r "
			+ "FROM ReservationEO r "
			+ "JOIN FETCH r.user "
			+ "JOIN FETCH r.machine "
			+ "WHERE r.dateStart >= :"+ReservationEO.PARAM_DATE_START+" "
			+ "AND r.dateEnd <= :"+ReservationEO.PARAM_DATE_END),
//	@NamedQuery(name = "ReservationEO.findAll", query = "SELECT r FROM ReservationEO r"),
//	@NamedQuery(name = "ReservationEO.findByReservationId", query = "SELECT r FROM ReservationEO r WHERE r.reservationId = :reservationId"),
//	@NamedQuery(name = "ReservationEO.findByDateStart", query = "SELECT r FROM ReservationEO r WHERE r.dateStart = :dateStart"),
//	@NamedQuery(name = "ReservationEO.findByDateEnd", query = "SELECT r FROM ReservationEO r WHERE r.dateEnd = :dateEnd")
})
public class ReservationEO extends AbstractDataEO implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public static final String SELECT_BY_TIME = "ReservationEO.selectByTime";
	public static final String PARAM_DATE_START = "dateStart";
	public static final String PARAM_DATE_END = "dateEnd";
	
	
	@Id
    @Basic(optional = false)
    @Column(name = "reservation_id")
	private Integer reservationId;
	
	@Basic(optional = false)
    @Column(name = "date_start")
    @Temporal(TemporalType.TIMESTAMP)
	private Date dateStart;
	
	@Basic(optional = false)
    @Column(name = "date_end")
    @Temporal(TemporalType.TIMESTAMP)
	private Date dateEnd;
	
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
	private UserEO user;
	
	@JoinColumn(name = "machine_id", referencedColumnName = "machine_id")
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
	private MachineEO machine;

	public ReservationEO() {
	}

	public ReservationEO(Integer reservationId) {
		this.reservationId = reservationId;
	}

	public ReservationEO(Integer reservationId, Date dateStart, Date dateEnd) {
		this.reservationId = reservationId;
		this.dateStart = dateStart;
		this.dateEnd = dateEnd;
	}

	@Override
	public Integer getId() {
		return getReservationId();
	}

	public Integer getReservationId() {
		return reservationId;
	}

	public void setReservationId(Integer reservationId) {
		this.reservationId = reservationId;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateEnd() {
		return dateEnd;
	}

	public void setDateEnd(Date dateEnd) {
		this.dateEnd = dateEnd;
	}

	public UserEO getUser() {
		return user;
	}

	public void setUser(UserEO user) {
		this.user = user;
	}

	public MachineEO getMachine() {
		return machine;
	}

	public void setMachine(MachineEO machine) {
		this.machine = machine;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (reservationId != null ? reservationId.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ReservationEO)) {
			return false;
		}
		ReservationEO other = (ReservationEO) object;
		if ((this.reservationId == null && other.reservationId != null) || (this.reservationId != null && !this.reservationId.equals(other.reservationId))) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "net.collaud.fablab.data.ReservationEO[ reservationId=" + reservationId + " ]";
	}
	
}
