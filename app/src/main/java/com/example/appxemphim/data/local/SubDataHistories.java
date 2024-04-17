package com.example.appxemphim.data.local;

import com.example.appxemphim.model.History;
import com.example.appxemphim.model.InformationMovie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubDataHistories{
    private List<History> dataHistory = new ArrayList<>();

    public List<History> initData(){
        return dataHistory;
    }

}
