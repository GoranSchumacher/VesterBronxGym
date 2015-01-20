package models;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import play.db.ebean.Model;
import be.objectify.deadbolt.core.models.Permission;

import java.util.Date;
import java.util.List;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "user_counter_purchase")
public class UserCounterPurchase extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

    @ManyToOne
    public User user;

    public enum PaymentType {
        @EnumValue("CounterCash")
        COUNTER_CASH,
        @EnumValue("CounterCard")
        COUNTER_CARD,
        @EnumValue("RecurringPurchase")
        RECURRING_PURCHASE
    }

    public PaymentType paymentType;
    public Date purchaseDate;

    public Date paymentDate;
    // Only used when paymentType = RECURRING_PURCHASE
    public UserRecurringPurchaseItem paidOnRecurringProductItem;


    @OneToMany(cascade = CascadeType.ALL)
    public List<UserCounterPurchaseItem> purchaseItems;

    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

}


