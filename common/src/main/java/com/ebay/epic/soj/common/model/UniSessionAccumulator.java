package com.ebay.epic.soj.common.model;

import com.ebay.epic.soj.common.model.raw.RawUniSession;
import lombok.Data;

@Data
public class UniSessionAccumulator {
    private RawUniSession uniSession;

    public UniSessionAccumulator() {
        this.uniSession = new RawUniSession();
    }
}
