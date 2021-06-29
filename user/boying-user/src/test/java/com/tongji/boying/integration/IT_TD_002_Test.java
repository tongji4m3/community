package com.tongji.boying.integration;

import base.InitHttpCase;
import com.tongji.boying.model.BoyingSeatModel;
import com.tongji.boying.model.BoyingStock;
import com.tongji.boying.service.BoyingSeatService;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class IT_TD_002_Test extends InitHttpCase {
    @Autowired
    private BoyingSeatService boyingSeatService;

    @ParameterizedTest
    @CsvFileSource(resources = "/integration/IT_TD_002_Test.csv", numLinesToSkip = 1)
    public void getShowSeat(Integer id, String expected) {
        BoyingSeatModel boyingSeatModel = boyingSeatService.getShowSeat(id);
        if (boyingSeatModel == null) assertEquals(expected, "null");
        else assertEquals(expected, String.valueOf(boyingSeatModel.getId()));
    }
}