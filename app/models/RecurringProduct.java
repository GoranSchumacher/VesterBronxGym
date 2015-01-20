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
@Table(name = "recurring_product")
public class RecurringProduct extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    public Long id;

    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String description;
    @Constraints.MaxLength(255)
    @Column(length = 255)
    public String longDescription;

    public enum Period {
        @EnumValue("Month")
        MONTH,

        @EnumValue("Day")
        DAY,

        @EnumValue("Year")
        YEAR
    }

    // For instance;
    // If it is a month subscription and we by it th 15th.
    // Is the remainder of the first month free, or
    // Should we recalculate the price accordingly.
    public enum Type {      // Make descriptions as comments
        @EnumValue("M")
        MONTH
    }

    public Type type;

    public Period period;
    public Integer numberOfPeriods;

    public Long amount;
    public Long vatAmount;



    public void transferAmountToPurchase(UserRecurringPurchaseItem purchase) {
        purchase.actualAmount = this.amount;
        purchase.actualVatAmount = this.vatAmount;
    }




    public static final Finder<Long, RecurringProduct> find = new Finder<Long, RecurringProduct>(
            Long.class, RecurringProduct.class);

    public static RecurringProduct findById(final Long id) {
        return find.where().eq("id", id).findUnique();
    }

}


