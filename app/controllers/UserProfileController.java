package controllers;

import be.objectify.deadbolt.core.models.Role;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.S3File;
import models.SecurityRole;
import models.User;
import models.UserProfile;
import play.Configuration;
import play.Play;
import play.data.Form;
import play.db.ebean.Model;
import play.mvc.Controller;
import play.mvc.Result;
import providers.MyUsernamePasswordAuthProvider;
import views.html.signup;
import views.html.userProfile;
import views.html.*;

import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static play.data.Form.form;

/**
 * Example from link: https://devcenter.heroku.com/articles/using-amazon-s3-for-file-uploads-with-java-and-play-2
 * @author Gøran Schumacher (GS) / Schumacher Consulting Aps
 * @version $Revision$ 18/01/15
 */
public class UserProfileController extends Controller {


    private static Configuration SMTP_CONFIGURATION = Play.application().configuration().getConfig("smtp");
    private static String SMTP_USER = SMTP_CONFIGURATION.getString("user");

    /*
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
    }*/

    public static final Form<models.UserProfile> USERPROFILE_FORM = form(models.UserProfile.class);

    @Restrict(@Group(Application.USER_ROLE))
    public static Result userProfile() {
        final User localUser = Application.getLocalUser(session());
        models.UserProfile userProfile = null;
        try {
            userProfile = models.UserProfile.findByUser(localUser);
        } catch(Exception e) {}
        Form<models.UserProfile> filledForm = USERPROFILE_FORM;
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
            models.UserProfile userProfile = null;
            try {
                userProfile = models.UserProfile.findByUser(localUser);
            } catch(Exception e) {}
            if(userProfile == null) {
                userProfile = filledForm.get();
                //moveFromUserProfileFormToEntity(filledForm, userProfile);
                userProfile.id = localUser.id;

                // We removed the field on the form, so set it to default for now.
                userProfile.country = "Denmark";

                userProfile.save();

                // Add USERPROFILE_ROLE to user
                try {
                    localUser.roles.add(SecurityRole.findByRoleName(Application.USERPROFILE_ROLE));
                    localUser.save();} catch (Exception e){
                    // Do nothing - Probably already added role.
                }

                // If email ends with smtp.user => Set role Admin
                // Then the user is an employee of the Gym
                String mailDomain = localUser.email.split("@")[1];
                String gymDomain = SMTP_USER.split("@")[1];
                if(gymDomain.equalsIgnoreCase(mailDomain)) {
                    try {
                        localUser.roles.add(SecurityRole.findByRoleName(Application.ADMIN_ROLE));
                        localUser.save();
                    } catch (Exception e){
                        // Do nothing - Probably already added role.
                    }
                }

                // Redirect to payment page
                return redirect(routes.PayEx.membership());
            } else {
                filledForm.get().update();
                //moveFromUserProfileFormToEntity(filledForm, userProfile);
                //userProfile.update();
            }
            return redirect(routes.Application.index());
        }
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static Result userProfilePicture() {
        final User localUser = Application.getLocalUser(session());
        models.UserProfile userProfile = null;
        try {
            userProfile = models.UserProfile.findByUser(localUser);
        } catch(Exception e) {}
        /*Form<models.UserProfile> filledForm = USERPROFILE_FORM;
        if(userProfile != null) {
            filledForm = USERPROFILE_FORM.fill(userProfile);
        }*/
        return ok(views.html.userProfilePicture.render());
    }

    public static boolean hasProfile() {
        UserProfile userProfile = getUserProfileFromLoggedInUser();
        if(userProfile == null) {
            return false;
        }
        return true;
    }

    public static boolean hasProfileAndAcceptedTerms() {
        UserProfile userProfile = getUserProfileFromLoggedInUser();
        if(userProfile == null) {
            return false;
        }
        return userProfile.acceptedTerms.equalsIgnoreCase("A");
    }

    public static boolean hasRole(String roleName) {
        final User localUser = Application.getLocalUser(session());
        if(localUser == null) {
            return false;
        }
        for (Role role : localUser.getRoles()) {
            if(role.getName().equalsIgnoreCase(roleName)) {
                return true;
            }
        }
        return false;
    }

    public static UserProfile getUserProfileFromLoggedInUser() {
        final User localUser = Application.getLocalUser(session());
        if(localUser == null) {
            return null;
        }
        UserProfile userProfile = null;
        try {
            userProfile = UserProfile.findByUser(localUser);
        } catch(Exception e) {}
        return userProfile;
    }

    @Restrict(@Group(Application.USER_ROLE))
    public static String userProfileImageUrl() {
        UserProfile userProfile = getUserProfileFromLoggedInUser();
        if((userProfile == null) || (userProfile.userImage()==null)) {
            return null;
        }
        try {
            return userProfile.userImage().getUrl().toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Result viewUsers() {
        List<UserProfile> users = new Model.Finder(Long.class, UserProfile.class).all();
        return ok(viewUsers.render(users));
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
