package com.example.planeapplication.ui.home;

import java.util.Calendar;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel
{

    private MutableLiveData<Calendar> mFromCalendarLive;
    private MutableLiveData<Calendar> mToCalendarLive;

    public HomeViewModel()
    {
      mFromCalendarLive = new MutableLiveData<>();
      mFromCalendarLive.setValue(Calendar.getInstance());
      mToCalendarLive = new MutableLiveData<>();
      mToCalendarLive.setValue(Calendar.getInstance());
    }

    public MutableLiveData<Calendar> getFromCalendarLive()
    {
        return mFromCalendarLive;
    }

    public MutableLiveData<Calendar> getToCalendarLive()
    {
        return mToCalendarLive;
    }
}