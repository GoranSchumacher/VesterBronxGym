package controllers;

import play.db.ebean.Model;
import play.mvc.*;
import be.objectify.deadbolt.java.actions.Group;
import be.objectify.deadbolt.java.actions.Restrict;
import models.*;
import play.data.Form;
import play.libs.Json;
import play.mvc.Controller;
import providers.MyUsernamePasswordAuthProvider;
import scala.App;
import views.html.index;
import views.html.signup;
import views.html.userProfile;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import views.html.*;

import static play.data.Form.form;
import play.mvc.Controller;

/**
 * @author Gøran Schumacher (GS) / Schumacher Consulting Aps
 * @version $Revision$ 22/01/15
 */
public class S3FileController extends Controller {

    public static Result viewImages() {
        List<S3File> uploads = new Model.Finder(UUID.class, S3File.class).all();
        return ok(viewImages.render(uploads));
    }

    public static Result upload() {
        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
        if (uploadFilePart != null) {
            S3File s3File = new S3File();
            s3File.name = uploadFilePart.getFilename();
            s3File.file = uploadFilePart.getFile();
            s3File.save();
            return redirect(routes.S3FileController.viewImages());
        }
        else {
            return badRequest("File upload error");
        }
    }


    @Restrict(@Group(Application.USER_ROLE))
    public static Result uploadUserImage() {

        UserProfile userProfile = UserProfileController.getUserProfileFromLoggedInUser();
        if(userProfile == null) {
            return notFound("User not logged in");
        }

        S3File file = S3File.findByUserProfile(userProfile);
        if(file!=null) {
            file.delete();
        }

        Http.MultipartFormData body = request().body().asMultipartFormData();
        Http.MultipartFormData.FilePart uploadFilePart = body.getFile("upload");
        if (uploadFilePart != null) {
            S3File s3File = new S3File();
            s3File.userProfile= userProfile;
            s3File.name = uploadFilePart.getFilename();
            s3File.file = uploadFilePart.getFile();
            s3File.save();
            return redirect(routes.S3FileController.viewImages());
        }
        else {
            return badRequest("File upload error");
        }
    }
}
