package controllers;

import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.SecurityRole;
import models.User;
import models.UserProfile;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import views.html.signup;
import views.html.userProfile;

import java.util.Collections;
import java.util.Date;

import static play.data.Form.form;

/**
 * @author Gøran Schumacher (GS) / Schumacher Consulting Aps
 * @version $Revision$ 18/01/15
 */
public class UserProfileController extends Controller {

    public static class UserProfile {

        public Long id;

        //@Constraints.Required
        //@Constraints.MinLength(5)
        public String sex;
        public Date birthDate;
        public String street;
        public String streetNo;
        public String line2;
        public String zip;
        public String city;
        public String country = "Denmark";
        public String acceptedTerms;
        public String contactPermission;
        public String payExAgreementId;


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
            return null;
        }
    }

    public static final Form<models.UserProfile> USERPROFILE_FORM = form(models.UserProfile.class);

    @Restrict(@Group(Application.USER_ROLE))
    public static Result userProfile() {
        final User localUser = Application.getLocalUser(session());
        models.UserProfile userProfile = models.UserProfile.findByUser(localUser);
        Form<models.UserProfile> filledForm = USERPROFILE_FORM;;
        if(userProfile != null) {
            filledForm = USERPROFILE_FORM.fill(userProfile);
        }
        return ok(views.html.userProfile.render(filledForm));
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result doUserProfile() {
        final User localUser = Application.getLocalUser(session());
        com.feth.play.module.pa.controllers.Authenticate.noCache(response());
        final Form<models.UserProfile> filledForm = USERPROFILE_FORM.bindFromRequest();
        if (filledForm.hasErrors()) {
            // User did not fill everything properly
            return badRequest(userProfile.render(filledForm));
        } else {
            models.UserProfile userProfile = models.UserProfile.findByUser(localUser);
            if(userProfile == null) {
                userProfile = filledForm.get();
                //moveFromUserProfileFormToEntity(filledForm, userProfile);
                userProfile.id = localUser.id;
                userProfile.save();

                // Add USERPROFILE_ROLE to user
                localUser.roles.add(SecurityRole.findByRoleName(Application.USERPROFILE_ROLE));
                localUser.save();
            } else {
                filledForm.get().update();
                //moveFromUserProfileFormToEntity(filledForm, userProfile);
                //userProfile.update();
            }
            return redirect(routes.Application.index());
        }
    }

    private static void moveFromUserProfileFormToEntity(Form<models.UserProfile> filledForm, models.UserProfile userProfile) {
        userProfile.sex = filledForm.get().sex;
        userProfile.birthDate = filledForm.get().birthDate;
        userProfile.street = filledForm.get().street;
        userProfile.streetNo = filledForm.get().streetNo;
        userProfile.line2 = filledForm.get().line2;
        userProfile.zip = filledForm.get().zip;
        userProfile.city = filledForm.get().city;
        userProfile.country = filledForm.get().country;
    }
}
