package models;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import play.db.ebean.Model;
import be.objectify.deadbolt.core.models.Permission;

import java.util.Date;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "user_recurring_purchase_item")
public class UserRecurringPurchaseItem extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

    public Date startPeriod;
    public Date endPeriod;

    public Long actualAmount;
    public Long actualVatAmount;

    @ManyToOne
    public UserRecurringPurchase purchase;

	public String orderRef;
    public String sessionRef;

    public String initializeRedirectUrl;
    public String initializeErrorCode;
    public String initializeDescription;

    public String completeErrorCode;
    public String completeDescription;
    public String completeParamName;
    public String completeTransactionNumber;
    public String completeTransactionTime;


    public String autoPayErrorCode;
    public String autoPayErrorCodeSimple;
    public String autoPayDescription;
    public String autoPayParamName;






    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

}


