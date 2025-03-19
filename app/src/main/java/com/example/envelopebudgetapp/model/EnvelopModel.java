package com.example.envelopebudgetapp.model;

import java.io.Serializable;

public class EnvelopModel implements Serializable
{
    private int env_id;
    private String env_name;
    private String date_added;

    //constructors

    public EnvelopModel(int env_id, String env_name, String date_added)
    {
        this.env_id = env_id;
        this.env_name = env_name;
        this.date_added = date_added;
    }

    public EnvelopModel()
    {
    }

    @Override
    public String toString() {
        return "EnvelopModel{" +
                "env_id=" + env_id +
                ", env_name='" + env_name + '\'' +
                ", date_added='" + date_added + '\'' +
                '}';
    }

    public int getEnv_id() {
        return env_id;
    }

    public void setEnv_id(int env_id) {
        this.env_id = env_id;
    }

    public String getEnv_name() {
        return env_name;
    }

    public void setEnv_name(String env_name) {
        this.env_name = env_name;
    }

    public String getDate_added() {
        return date_added;
    }

    public void setDate_added(String date_added) {
        this.date_added = date_added;
    }
}
