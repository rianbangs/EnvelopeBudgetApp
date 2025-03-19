package com.example.envelopebudgetapp.model;

public class EnvelopDetailModel
{
    private int env_det_id;
    private String env_detail_description;
    private String detail_type;
    private float env_amount;
    private String evn_date;
    private int  env_id;
    //constructor

    public EnvelopDetailModel(int env_det_id, String env_detail_description, String detail_type, float env_amount,String evn_date, int env_id)
    {
        this.env_det_id = env_det_id;
        this.env_detail_description = env_detail_description;
        this.detail_type = detail_type;
        this.env_amount = env_amount;
        this.evn_date = evn_date;
        this.env_id = env_id;
    }

    public EnvelopDetailModel() {
    }

    //getter and setter

    public int getEnv_det_id() {
        return env_det_id;
    }

    public void setEnv_det_id(int env_det_id) {
        this.env_det_id = env_det_id;
    }

    public String getEnv_detail_description() {
        return env_detail_description;
    }

    public void setEnv_detail_description(String env_detail_description) {
        this.env_detail_description = env_detail_description;
    }

    public String getDetail_type() {
        return detail_type;
    }

    public void setDetail_type(String detail_type) {
        this.detail_type = detail_type;
    }

    public float getEnv_amount() {
        return env_amount;
    }

    public String getEvn_date() {
        return evn_date;
    }

    public void setEvn_date(String evn_date) {
        this.evn_date = evn_date;
    }

    public void setEnv_amount(float env_amount) {
        this.env_amount = env_amount;
    }

    public int getEnv_id() {
        return env_id;
    }

    public void setEnv_id(int env_id) {
        this.env_id = env_id;
    }
}
