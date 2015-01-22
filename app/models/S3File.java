package models;

import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.avaje.ebean.ExpressionList;
import play.Logger;
import play.db.ebean.Model;
import plugins.S3Plugin;

import javax.persistence.*;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.UUID;

/**
 * Example from link: https://devcenter.heroku.com/articles/using-amazon-s3-for-file-uploads-with-java-and-play-2
 */
@Entity
@Table(name = "s3file", uniqueConstraints=@UniqueConstraint(columnNames={"user_profile"}))
public class S3File extends Model {

    public static final Finder<Long, S3File> find = new Finder<Long, S3File>(
            Long.class, S3File.class);

    @Id
    public UUID id;

    @OneToOne
    public UserProfile userProfile;

    private String bucket;

    public String name;

    @Transient
    public File file;

    public URL getUrl() throws MalformedURLException {
        return new URL("https://s3.amazonaws.com/" + bucket + "/" + getActualFileName());
    }

    private String getActualFileName() {
        return id + "/" + name;
    }

    public static S3File findByUserProfile(final UserProfile userProfile) {
        return getUserProfileFind(userProfile).findUnique();
    }

    private static ExpressionList<S3File> getUserProfileFind(final UserProfile userProfile) {
        return find.where().eq("user_profile_id", userProfile.id);
    }

    @Override
    public void save() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not save because amazonS3 was null");
            throw new RuntimeException("Could not save");
        }
        else {
            this.bucket = S3Plugin.s3Bucket;

            super.save(); // assigns an id

            PutObjectRequest putObjectRequest = new PutObjectRequest(bucket, getActualFileName(), file);
            putObjectRequest.withCannedAcl(CannedAccessControlList.PublicRead); // public for all
            S3Plugin.amazonS3.putObject(putObjectRequest); // upload file
        }
    }

    @Override
    public void delete() {
        if (S3Plugin.amazonS3 == null) {
            Logger.error("Could not delete because amazonS3 was null");
            throw new RuntimeException("Could not delete");
        }
        else {
            S3Plugin.amazonS3.deleteObject(bucket, getActualFileName());
            super.delete();
        }
    }

}