package com.ebay.epic.business.normalizer.ubi;

import com.ebay.epic.business.normalizer.FieldNormalizer;
import lombok.val;

/**
 * Accept condition is by default false.
 */
public abstract class AcceptorNormalizer<Source, Target> extends FieldNormalizer<Source, Target> {

    protected final static int CODE_REJECT = -1;
    protected final static int CODE_DEFAULT_ACCEPT = 0;

    @Override
    public final void normalize(Source src, Target tar) throws Exception {
        val code = accept(src, tar);
        if (code >= 0) {
            update(code, src, tar);
        }
    }

    /**
     * Accept condition is by default CODE_REJECT.
     */
    public int accept(Source input, Target tar) {
        return CODE_REJECT;
    }

    public abstract void update(int code, Source src, Target tar);

    protected static int acceptBoolean(boolean condition) {
        if (condition) return CODE_DEFAULT_ACCEPT;
        else return CODE_REJECT;
    }
}
