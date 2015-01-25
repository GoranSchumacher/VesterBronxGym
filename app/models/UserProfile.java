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
@Table(name = "user_profile")
public class UserProfile extends Model{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	public Long id;

    @Constraints.Required
    @Column(length = 10)
    public String firstName;

    @Column(length = 10)
    public String middleName;

    @Constraints.Required
    @Column(length = 20)
    public String lastName;

    @Column(length = 1)
	public String sex;
    @Constraints.MaxLength(10)
    @Column(length = 10)
    public String phone;

    public Date birthDate;

    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String street;
    @Constraints.MaxLength(5)
    @Column(length = 5)
    public String streetNo;
    @Constraints.MaxLength(30)
    @Column(length = 30)
    public String line2;
    @Constraints.MaxLength(5)
    @Column(length = 5)
    public String zip;
    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String city;
    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String country;
    @Column(length = 1)
    public String acceptedTerms;    // A - Accepted, F - Not accepted
    @Column(length = 1)
    public String contactPermission;    // A - Can contact, F - No contact
    @Constraints.MaxLength(20)
    @Column(length = 20)
    public String payexAgreementId;


    public S3File userImage() {
        User user = User.findByUserProfile(this);
        if(user==null) {
            return null;
        }
        return S3File.findByUser(user);
    }

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
            return "Angiv fødselsdag!";
        }
        return null;
    }
}


