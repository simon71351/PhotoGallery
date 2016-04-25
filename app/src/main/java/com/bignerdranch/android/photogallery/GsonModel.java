package com.bignerdranch.android.photogallery;

/**
 * Created by simon on 4/25/16.
 */
public class GsonModel {
    private Photos photos;

    public Photos getPhotos() {
        return photos;
    }

    public void setPhotos(Photos photos) {
        this.photos = photos;
    }

    public static class Photos{
        private Photo[] photo;

        public Photo[] getPhoto() {
            return photo;
        }

        public void setPhoto(Photo[] photo) {
            this.photo = photo;
        }
    }

    public static class Photo{
        private String id;
        private String title;
        private String url_s;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getUrl_s() {
            return url_s;
        }

        public void setUrl_s(String url_s) {
            this.url_s = url_s;
        }
    }
}
