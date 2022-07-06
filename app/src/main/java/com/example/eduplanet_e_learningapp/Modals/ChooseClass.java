package com.example.eduplanet_e_learningapp.Modals;

public class ChooseClass {
    String className;
    private boolean isSelected;

    public ChooseClass() {
    }

    public ChooseClass(String className) {
        this.className = className;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setSelected(boolean selection){
        this.isSelected = selection;
    }

    public boolean isSelected(){
        return isSelected;
    }
}
