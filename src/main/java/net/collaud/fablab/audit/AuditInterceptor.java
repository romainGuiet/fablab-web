package net.collaud.fablab.audit;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.interceptor.AroundInvoke;
import javax.interceptor.Interceptor;
import javax.interceptor.InvocationContext;
import net.collaud.fablab.data.AbstractDataEO;
import net.collaud.fablab.data.AuditEO;
import net.collaud.fablab.data.PaymentEO;
import net.collaud.fablab.data.UsageEO;
import net.collaud.fablab.data.UserAuthorizedMachineTypeEO;
import net.collaud.fablab.data.UserEO;
import net.collaud.fablab.data.type.AuditAction;
import net.collaud.fablab.data.type.AuditObject;
import net.collaud.fablab.exceptions.FablabException;
import net.collaud.fablab.service.itf.AuditService;
import net.collaud.fablab.service.itf.SecurityService;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
@Interceptor
@Audit
@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
public class AuditInterceptor {

	private static final Logger LOG = Logger.getLogger(AuditInterceptor.class);

	@EJB
	private AuditService auditService;

	@EJB
	private SecurityService securityService;

	@AroundInvoke
	public Object audit(InvocationContext context) throws Exception {
		LOG.info("Intercepted method " + context.getMethod().getName());
		AuditDetail ann = context.getMethod().getAnnotation(AuditDetail.class);
		try {
			Object result = context.proceed();
			Object object = getObjectOutOfResultAndParameters(ann, result, context.getParameters());
			Integer id = getIdOfObject(object);
			addEntry(ann.action(), ann.object(), id, true, getReadableMessage(ann.object(), ann.action(), object), null);
			return result;
		} catch (Exception ex) {
			Object entity = getObjectOutOfResultAndParameters(ann, null, context.getParameters());
			Integer id = getIdOfObject(entity);
			addEntry(ann.action(), ann.object(), id, true, "Error while " + ann.action() + " " + ann.object() + " with id  " + id, ex.getMessage());
			throw ex;
		}
	}

	private void addEntry(AuditAction action, AuditObject object, Integer objectId, boolean success, String content, String detail) throws FablabException {
		if (detail != null && detail.isEmpty()) {
			detail = null;
		}
		auditService.addEntry(new AuditEO(securityService.getCurrentUser(), action, object, objectId, new Date(), success, content, detail));
	}

	private Object getObjectOutOfResultAndParameters(AuditDetail ann, Object o, Object[] parameters) {
		if (ann.action().equals(AuditAction.INSERT)
				|| ann.action().equals(AuditAction.UPDATE)) {
			if (o instanceof AbstractDataEO) {
				return (AbstractDataEO) o;
			}
		} else if (ann.action().equals(AuditAction.DELETE)) {
			//take the first parameter
			if (parameters.length > 0 && parameters[0] instanceof AbstractDataEO) {
				return (AbstractDataEO) parameters[0];
			}
		}
		if (o != null) {
			return o;
		}
		return null;
	}

	private Integer getIdOfObject(Object entity) {
		if (entity instanceof AbstractDataEO) {
			return ((AbstractDataEO) entity).getId();
		}
		return null;
	}

	private String getReadableMessage(AuditObject obj, AuditAction action, Object res) {
		switch (obj) {
			case USAGE:
				return getReadableMessage(action, (UsageEO) res);
			case PAYMENT:
				return getReadableMessage(action, (PaymentEO) res);
			case USER:
				return getReadableMessage(action, (UserEO) res);
//			case ACCESS_DOOR:
//				return getReadableMessage(action, (AccessDoorResponse) res);
			case SUBSCRIPTION:
				return getReadableMessageSubscription(action, (UserEO) res);
			default:
				return "ERROR object " + obj + " not implemented yet";
		}
	}

	//FIXME take care of action
	private String getReadableMessage(AuditAction action, UsageEO usage) {
		StringBuilder sb = new StringBuilder();
		sb.append(usage.getUser().getFirstLastName());
		sb.append(" used the machine ");
		sb.append(usage.getMachine().getName());
		sb.append(" for ");
		sb.append(usage.getMinutes()).append("min");
		sb.append(" with ");
		if (usage.getAdditionalCost() == 0) {
			sb.append("no additional cost");
		} else {
			sb.append(usage.getAdditionalCost()).append("CHF of additional cost");
		}
		return sb.toString();
	}

	//FIXME take care of action
	private String getReadableMessage(AuditAction action, PaymentEO payment) {
		StringBuilder sb = new StringBuilder();
		if (action == AuditAction.INSERT) {
			sb.append(payment.getUser().getFirstLastName());
			sb.append(" paid ");
			sb.append(payment.getTotal());
			sb.append("CHF ");
		} else if (action == AuditAction.DELETE) {
			sb.append("Payment of ");
			sb.append(payment.getTotal());
			sb.append(" by ");
			sb.append(payment.getUser().getFirstLastName());
			sb.append(" was removed");
		}
		return sb.toString();
	}

	private String getReadableMessage(AuditAction action, UserEO user) {
		StringBuilder sb = new StringBuilder();
		sb.append("User ");
		sb.append(user.getLogin());
		switch (action) {
			case INSERT:
				sb.append(" inserted : ");
				break;
			case UPDATE:
				sb.append(" edited : ");
				break;
			case DELETE:
				sb.append(" deleted : ");
				break;
		}
		sb.append("firstname=");
		sb.append(user.getFirstname());
		sb.append(" lastname=");
		sb.append(user.getLastname());
		sb.append(" rfid=");
		sb.append(user.getRfid());
		sb.append(" authMachine=[");
		for(UserAuthorizedMachineTypeEO auth : user.getMachineTypeAuthorizedList()){
			sb.append(auth.getMachineType().getName());
			sb.append(",");
		}
		sb.deleteCharAt(sb.length()-1);//remove last coma
		sb.append("]");
		return sb.toString();
	}

//	private String getReadableMessage(AuditAction action, AccessDoorResponse res) {
//		StringBuilder sb = new StringBuilder();
//		if (res.getUser() != null) {
//			sb.append("User ");
//			sb.append(res.getUser().getFirstLastName());
//		} else {
//			sb.append("Unknown ");
//			sb.append(res.getRfid());
//		}
//		if (res.isGranted()) {
//			sb.append(" oppened the door");
//		} else {
//			sb.append(" could not open the door");
//		}
//		return sb.toString();
//	}

	private String getReadableMessageSubscription(AuditAction action, UserEO userEO) {
		StringBuilder sb = new StringBuilder();
		sb.append("User ");
		sb.append(userEO.getLogin());
		sb.append(" confirmed his subscription");
		return sb.toString();
	}

}
