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
@Table(name = "user_counter_purchase_item")
public class UserCounterPurchaseItem extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

    @ManyToOne
    public UserCounterPurchase purchase;

    public CounterProduct counterProduct;
    public Integer pieces;

    public Long actualAmount;           // Moved from counterProduct when created. Procuct price might change
    public Long actualVatAmount;        // Moved from counterProduct when created. Procuct price might change







    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

}


