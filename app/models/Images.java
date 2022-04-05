package models;

import com.hokeba.util.Encryption;
import com.hokeba.util.Helper;
import lombok.Getter;
import lombok.Setter;
import play.db.ebean.Model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.security.NoSuchAlgorithmException;
import java.util.Date;

/**
 * Created by hendriksaragih on 11/19/16.
 */
@Entity
@Table(name = "images")
@Getter
@Setter
public class Images extends Model {
    public static final long serialVersionUID = 1L;

    @Id
    public String id;

    @Column(columnDefinition = "TEXT")
    public String images;

    @Column(name = "module")
    public String module;

    @Column(name = "image_key")
    public String imageKey;

    public static Finder<String, Images> find = new Finder<>(String.class, Images.class);

    public static String saveImage(String image){
        if (image != null && !image.isEmpty()){
            Images img = new Images();
            try {
                img.id = Encryption.MD5(Helper.getRandomString(5) + new Date().toString());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            img.images = image;
            img.save();
            return img.id;
        }
        return "";
    }

    public static String saveImage(String image, String id){
        if (image != null && !image.isEmpty()){
            if (id != null && !id.isEmpty()){
                Images img = find.where().eq("id", id).findUnique();
                if (img != null){
                    img.images = image;
                    img.update();
                    return id;
                }
            }
            return saveImage(image);
        }
        return "";
    }

    public static String deleteImage(String id){
        if (id != null && !id.isEmpty()){
            Images img = find.where().eq("id", id).findUnique();
            if (img != null){
                img.delete();
            }
        }
        return "";
    }

    public static String getImage(String id) {
        String imageLink = "";
        if(id != null && !id.equals("")){
            Images image = Images.find.where().eq("id", id).findUnique();
            if(image != null){
                imageLink = "data:image/jpeg;base64,"+image.images;
            }
        }

        return imageLink;
    }
}
