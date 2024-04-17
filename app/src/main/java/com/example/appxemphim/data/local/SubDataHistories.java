package com.example.appxemphim.data.local;

import com.example.appxemphim.model.History;
import com.example.appxemphim.model.InformationMovie;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SubDataHistories{
    private List<History> dataHistory = new ArrayList<>();

    public List<History> initData(){
        InformationMovie informationMovie =  new InformationMovie(1,"vTJdVE_gjI0","Đen x JustaTee - Đi Về Nhà (M/V)","YOUTUBE","https://i.ytimg.com/vi/vTJdVE_gjI0/default.jpg", 20);
        History history = new History(1,1, LocalDateTime.of(2024, 3, 25, 15, 20, 50), 200, informationMovie);
        InformationMovie informationMovie1 =  new InformationMovie(1,"HXkh7EOqcQ4","JUSTATEE x PHUONG LY - CRAZY MAN | OFFICIAL MV","YOUTUBE","https://i.ytimg.com/vi/HXkh7EOqcQ4/default.jpg", 20);
        History history1 = new History(1,1, LocalDateTime.of(2024, 3, 27, 15, 20, 50), 200, informationMovie1);

        dataHistory.add(history1);
        dataHistory.add(history);

        return dataHistory;
    }

}
