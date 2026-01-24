package com.example.flowmoney.ui.analytics;

import com.example.flowmoney.data.entity.OperationEntity;

public class AnalyticsOperationItem implements AnalyticsListItem {
    public final OperationEntity operation;
    public AnalyticsOperationItem(OperationEntity op) { this.operation = op; }
}
