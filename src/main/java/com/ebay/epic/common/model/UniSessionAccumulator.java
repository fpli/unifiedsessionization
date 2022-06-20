package com.ebay.epic.common.model;

import lombok.Data;

@Data
public class UniSessionAccumulator {
    private UniSession uniSession;

    public UniSessionAccumulator() {
        this.uniSession = new UniSession();
    }
}
