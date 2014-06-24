package net.collaud.fablab.data.virtual;

import java.util.Date;
import net.collaud.fablab.data.PaymentEO;
import net.collaud.fablab.data.SubscriptionEO;
import net.collaud.fablab.data.UsageDetailEO;
import org.apache.log4j.Logger;

/**
 *
 * @author gaetan
 */
public class HistoryEntry implements Comparable<HistoryEntry> {

	private static final Logger LOG = Logger.getLogger(HistoryEntry.class);

	public enum HistoryEntryType {

		PAYMENT("payment"),
		USAGE("usage"),
		SUBSCRIPTION("subscription");
		private final String css;

		private HistoryEntryType(String css) {
			this.css = css;
		}

		public String getCss() {
			return css;
		}

	}
	private final int id;
	private final HistoryEntryType type;
	private final String comment;
	private final Date date;
	private final float amount;
	private final String detail;

	public HistoryEntry(PaymentEO payment) {
		type = HistoryEntryType.PAYMENT;
		id = payment.getPaymentId();
		date = payment.getDatePayement();
		comment = payment.getComment();
		detail = "cashier=" + payment.getCashier().getFirstLastName();
		amount = payment.getTotal();
	}

	public HistoryEntry(UsageDetailEO usage){
		type = HistoryEntryType.USAGE;
		id = usage.getUsageId();
		date = usage.getDateStart();
		comment = usage.getComment();
		detail = usage.getMachine().getName() + " | " + usage.getMinutes() + "min" + " | " + usage.getAdditionalCost() + " CHF additional";
		amount = -((usage.getPrice() * usage.getMinutes()) / 60 + usage.getAdditionalCost());
	}

	public HistoryEntry(SubscriptionEO subscription) {
		type = HistoryEntryType.SUBSCRIPTION;
		id = subscription.getId();
		date = subscription.getDateSubscription();
		comment = subscription.getComment();
		detail = "Subscription type : " + subscription.getPriceCotisation().getMembershipType().getName();
		amount = -subscription.getPriceCotisation().getPrice();
	}

	public String getComment() {
		return comment;
	}

	public Date getDate() {
		return date;
	}

	public float getAmount() {
		return amount;
	}

	public String getDetail() {
		return detail;
	}

	public HistoryEntryType getType() {
		return type;
	}

	@Override
	public int compareTo(HistoryEntry o) {
		int res = date.compareTo(o.date);
		if (res != 0) {
			return -res;//inverse
		}
		res = type.compareTo(o.type);
		if (res != 0) {
			return -res;
		}
		return -Integer.valueOf(id).compareTo(Integer.valueOf(o.id));
	}
}
