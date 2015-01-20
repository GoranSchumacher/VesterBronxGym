package models;

import javax.persistence.*;

import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import play.data.validation.Constraints;
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


    @Constraints.MaxLength(30)
    @Column(length = 30)
	public String orderRef;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String sessionRef;

    @Constraints.MaxLength(60)
    @Column(length = 60)
    public String initializeRedirectUrl;
    @Constraints.MaxLength(10)
    @Column(length = 10)
    public String initializeErrorCode;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String initializeDescription;

    @Constraints.MaxLength(10)
    @Column(length = 10)
    public String completeErrorCode;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String completeDescription;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String completeParamName;
    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String completeTransactionNumber;
    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String completeTransactionTime;


    @Constraints.MaxLength(10)
    @Column(length = 10)
    public String autoPayErrorCode;
    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String autoPayErrorCodeSimple;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String autoPayDescription;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String autoPayParamName;






    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

}


