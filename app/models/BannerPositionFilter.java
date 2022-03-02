package models;

import java.util.List;

/**
 * Created by hendriksaragih on 2/5/17.
 */
public class BannerPositionFilter {
    private int position_id;
    private String position_name;
    private List<Banner> list;

    public BannerPositionFilter(int positionId, String positionName, List<Banner> list) {
        this.position_id = positionId;
        this.position_name = positionName;
        this.list = list;
    }

    public int getPosition_id() {
        return position_id;
    }

    public void setPosition_id(int position_id) {
        this.position_id = position_id;
    }

    public String getPosition_name() {
        return position_name;
    }

    public void setPosition_name(String position_name) {
        this.position_name = position_name;
    }

    public List<Banner> getList() {
        return list;
    }

    public void setList(List<Banner> list) {
        this.list = list;
    }
}
