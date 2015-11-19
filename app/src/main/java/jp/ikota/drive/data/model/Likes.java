package jp.ikota.drive.data.model;


import java.util.List;

public class Likes {

    public List<Like> items;

    public static class Like {
        public String id;
        public String created_at;
        public Shot shot;
    }

}
