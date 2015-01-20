package models;

import javax.persistence.*;

import com.avaje.ebean.annotation.EnumValue;
import com.feth.play.module.pa.providers.password.UsernamePasswordAuthUser;
import play.data.validation.Constraints;
import play.db.ebean.Model;
import be.objectify.deadbolt.core.models.Permission;

import java.util.Date;
import java.util.List;

/**
 * Initial version based on work by Steve Chaloner (steve@objectify.be) for
 * Deadbolt2
 */
@Entity
@Table(name = "counter_product")
public class CounterProduct extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

    @Constraints.MaxLength(15)
    @Column(length = 15)
    public String eanCode;

    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String description;

    public Long amount;
    public Long vatAmount;




    public void transferAmountToPurchase(UserCounterPurchaseItem purchase) {
        purchase.actualAmount = this.amount;
        purchase.actualVatAmount = this.vatAmount;
    }


    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

}


