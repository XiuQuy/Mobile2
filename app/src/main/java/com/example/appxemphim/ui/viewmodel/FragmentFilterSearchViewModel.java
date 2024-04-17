package com.example.appxemphim.ui.viewmodel;

import androidx.lifecycle.ViewModel;

import com.google.android.material.chip.Chip;

import java.util.List;

public class FragmentFilterSearchViewModel extends ViewModel {
    private boolean btnFilterEnable;
    private String year = "";
    private boolean adult;
    private List<Chip> chipList;

    public boolean isBtnFilterEnable() {
        return btnFilterEnable;
    }

    public void setBtnFilterEnable(boolean btnFilterEnable) {
        this.btnFilterEnable = btnFilterEnable;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public boolean isAdult() {
        return adult;
    }

    public void setAdult(boolean adult) {
        this.adult = adult;
    }

    public List<Chip> getChipList() {
        return chipList;
    }

    public void setChipList(List<Chip> chipList) {
        this.chipList = chipList;
    }
}
