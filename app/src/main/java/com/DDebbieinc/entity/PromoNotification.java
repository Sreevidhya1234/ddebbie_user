package com.DDebbieinc.entity;

/**
 * Created by appsplanet on 17/8/16.
 *
 * {
 "action":"PROMO_PUSH",
 "promo_code":"DDPC20",
 "promo_valid_from":"17 Aug, 2016",
 "promo_valid_to":"18 Aug, 2016",
 "title":"Promo codeDDPC20",
 "body":"Use promo code DDPC20 to get discount of 20",
 "link":"",
 "image":""
 }
 */
public class PromoNotification {

    private int _id;
    private String action, promo_code, promo_valid_from, promo_valid_to, title, body, link, image;



    public PromoNotification(int _id, String action, String promo_code, String promo_valid_from,
                             String promo_valid_to, String title, String body, String link, String image) {
        this._id = _id;
        this.action = action;
        this.promo_code = promo_code;
        this.promo_valid_from = promo_valid_from;
        this.promo_valid_to = promo_valid_to;
        this.title = title;
        this.body = body;
        this.link = link;
        this.image = image;
    }

    public PromoNotification(String action, String promo_code, String promo_valid_from,
                             String promo_valid_to, String title, String body, String link, String image) {
        this.action = action;
        this.promo_code = promo_code;
        this.promo_valid_from = promo_valid_from;
        this.promo_valid_to = promo_valid_to;
        this.title = title;
        this.body = body;
        this.link = link;
        this.image = image;
    }

    public PromoNotification() {

    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getPromo_code() {
        return promo_code;
    }

    public void setPromo_code(String promo_code) {
        this.promo_code = promo_code;
    }

    public String getPromo_valid_from() {
        return promo_valid_from;
    }

    public void setPromo_valid_from(String promo_valid_from) {
        this.promo_valid_from = promo_valid_from;
    }

    public String getPromo_valid_to() {
        return promo_valid_to;
    }

    public void setPromo_valid_to(String promo_valid_to) {
        this.promo_valid_to = promo_valid_to;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public int get_id() {
        return _id;
    }

    public void set_id(int _id) {
        this._id = _id;
    }
}
