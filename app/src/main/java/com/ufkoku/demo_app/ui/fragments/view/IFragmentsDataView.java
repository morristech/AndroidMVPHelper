package com.ufkoku.demo_app.ui.fragments.view;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import java.util.List;

public interface IFragmentsDataView {

    void populateData(List<AwesomeEntity> entities);

    void setWaitViewVisible(boolean visible);

}
