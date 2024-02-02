package models;

import lombok.Getter;
import lombok.Setter;


import javax.persistence.*;

@Table(name = "banner_bazar")
@Getter
@Setter
@Entity
public class BannerBazaar extends BaseModel {

    public String title;
    public String url;
	
    @Column(name = "image_url")
    public String imageUrl;
}
