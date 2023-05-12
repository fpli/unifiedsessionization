package com.ebay.epic.soj.common.model;

import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import lombok.Data;

@Data
public class UniSessionAccumulator {
    private RawUniSession uniSession;
    public UniSessionAccumulator() {
        this.uniSession = new RawUniSession();
    }
}
