package com.example.envelopebudgetapp.model;

public class IncomeModel
{
    private int id;
    private float income;
    private String incomeDate;
    private String incomeDetail;

    //constructors --------------------------------------------------------------------
    public IncomeModel(int id,float income, String incomeDate,String incomeDetail)
    {
        this.id = id;
        this.income = income;
        this.incomeDate = incomeDate;
        this.incomeDetail = incomeDetail;
    }

    public IncomeModel()
    {

    }
//toString is necessary for printing contents of a class object


    @Override
    public String toString() {
        return "IncomeModel{" +
                "id=" + id +
                ", income=" + income +
                ", incomeDate='" + incomeDate + '\'' +
                ", incomeDetail='" + incomeDetail + '\'' +
                '}';
    }

//getters and setters-----------------------------------------------------
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getIncome() {
        return income;
    }

    public void setIncome(float income) {
        this.income = income;
    }

    public String getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(String incomeDate) {
        this.incomeDate = incomeDate;
    }

    public String getIncomeDetail() {
        return incomeDetail;
    }

    public void setIncomeDetail(String incomeDetail) {
        this.incomeDetail = incomeDetail;
    }
}
