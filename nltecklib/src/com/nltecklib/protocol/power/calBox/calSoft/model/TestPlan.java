package com.nltecklib.protocol.power.calBox.calSoft.model;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nltecklib.protocol.power.calBox.calSoft.CalSoftEnvironment.TestType;

public class TestPlan {
    public TestType testType; // 工序类型

    public List<TestDot> testDotLst = new LinkedList<>();
    public Map<Byte, ToleranceLimit> toleranceLimitMap = new HashMap<>(); // 各电流档位, 数值偏差上限
    public boolean backup1Enabled = true;
    public boolean backup2Enabled = true;
    public boolean validateValueWhenCalibrate = true; // 校准时检查数值是否有效

    public interface ToleranceLimit {
    }

}
