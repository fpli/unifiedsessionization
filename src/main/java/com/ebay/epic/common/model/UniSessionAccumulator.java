package com.ebay.epic.common.model;

import com.ebay.epic.common.model.raw.RawUniSession;
import lombok.Data;

@Data
public class UniSessionAccumulator {
    private RawUniSession uniSession;

    public UniSessionAccumulator() {
        this.uniSession = new RawUniSession();
    }
}
