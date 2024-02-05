package repository;

import java.util.List;
import models.BannerBazaar;
import play.db.ebean.Model;


public class BannerBazaarRepository {
  public static Model.Finder<Long, BannerBazaar> find = new Model.Finder<>(Long.class, BannerBazaar.class);

    public static List<BannerBazaar> findAll() {
        return find.where()
                .eq("t0.is_deleted", Boolean.FALSE).query().findList();
    }

    public  static int countAll()  {

      int count = find.where().eq("isDeleted", false).findRowCount();

      return count;

  }
}
