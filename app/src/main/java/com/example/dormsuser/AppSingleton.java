package com.example.dormsuser;

import com.example.dormsuser.Model.Complaint;
import com.example.dormsuser.Model.MenuSelection;

public class AppSingleton {

    public static AppSingleton INSTANCE = null;

    public static AppSingleton getINSTANCE() {
        if(INSTANCE == null) INSTANCE = new AppSingleton();
        return INSTANCE;
    }

    Complaint selectedComplaint;

    public Complaint getSelectedComplaint() {
        return selectedComplaint;
    }

    public void setSelectedComplaint(Complaint selectedComplaint) {
        this.selectedComplaint = selectedComplaint;
    }

    MenuSelection selectedMenu;

    public MenuSelection getSelectedMenu() {
        return selectedMenu;
    }

    public void setSelectedMenu(MenuSelection selectedMenu) {
        this.selectedMenu = selectedMenu;
    }
}
