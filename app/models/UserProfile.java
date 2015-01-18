package models;

import javax.persistence.Entity;
import javax.persistence.Id;
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
@Table(name = "userProfile")
public class UserProfile extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

	public String sex;

    public Date birthDate = null;

    public String street;
    public String streetNo;
    public String line2;
    public String zip;
    public String city;
    public String country;
    public String acceptedTerms;
    public String contactPermission;
    public String payExAgreementId;

    public static final Finder<Long, UserProfile> find = new Finder<Long, UserProfile>(
            Long.class, UserProfile.class);

    public static UserProfile findByUser(final User user) {
        return find.where().eq("id", user.id).findUnique();
    }

    public String validate() {
        if (sex == null ) {
            return "Køn skal agives!";
        }
        if (acceptedTerms == null ) {
            return "Acceptere betingelserne!";
        }
        if (contactPermission == null ) {
            return "Angiv hvis vi må kontakte dej!";
        }
        if (birthDate == null ) {
            birthDate = null;
        }
        return null;
    }
}


