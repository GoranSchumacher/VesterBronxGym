package models;

import javax.persistence.*;

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
@Table(name = "user_recurring_purchase")
public class UserRecurringPurchase extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

    @ManyToOne
    public User user;

    @ManyToOne
    public RecurringProduct product;

    public Date purchaseDate;

    public UserRecurringPurchaseItem createNewItem() {
        UserRecurringPurchaseItem item = new UserRecurringPurchaseItem();
        item.purchase=this;
        product.transferAmountToPurchase(item);
        return item;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<UserRecurringPurchaseItem> purchaseItems;

    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

}


