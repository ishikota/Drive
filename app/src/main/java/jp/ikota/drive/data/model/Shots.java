package jp.ikota.drive.data.model;


import java.util.List;

public class Shots {
    public List<Item> items;
    public static class Item {
        public static class Images {
            public String hidpi;
            public String normal;
            public String teaser;
        }
        public static class User {
            public String id;
            public String name;
            public String username;
            public String avatar_url;
            public String bio;
            public String location;
            // omit nelow buckets count
        }
        public String id;
        public String title;
        public String description;
        public int width;
        public int height;
        public Images images;
        public int view_count;
        public int likes_count;
        public int comments_count;
        public int attachments_count;
        public int rebounds_count;
        public int buckets_count;
        public String created_at;
        public String updated_at;
        public User user;
    }
}
