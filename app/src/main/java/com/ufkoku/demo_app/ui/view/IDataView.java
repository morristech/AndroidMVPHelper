package com.ufkoku.demo_app.ui.view;

import com.ufkoku.demo_app.entity.AwesomeEntity;

import java.util.List;

public interface IDataView {

    void populateData(List<AwesomeEntity> entities);

    void setWaitViewVisible(boolean visible);

}
