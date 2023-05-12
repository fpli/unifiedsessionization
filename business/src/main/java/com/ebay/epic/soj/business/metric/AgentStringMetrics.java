package com.ebay.epic.soj.business.metric;

import com.ebay.epic.soj.common.model.UniSessionAccumulator;
import com.ebay.epic.soj.common.model.raw.RawUniSession;
import com.ebay.epic.soj.common.model.raw.UniEvent;
import com.ebay.sojourner.common.model.SessionAccumulator;
import com.ebay.sojourner.common.util.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;

@Slf4j
public class AgentStringMetrics implements FieldMetrics<UniEvent, UniSessionAccumulator> {

  public static final String SHOCKWAVE_FLASH_AGENT = "Shockwave Flash";
  public static final int AGENT_MAX_LENGTH = 2000;
  private static Set<Integer> agentExcludeSet;
  @Override
  public void init() throws Exception {
    agentExcludeSet =
            PropertyUtils.getIntegerSet(
                    UBIConfig.getString(Property.AGENT_EXCLUDE_PAGES), Property.PROPERTY_DELIMITER);
    log.info(
            "UBIConfig.getString(Property.AGENT_EXCLUDE_PAGES): {}",
            UBIConfig.getString(Property.AGENT_EXCLUDE_PAGES));
  }

  @Override
  public void start(UniSessionAccumulator sessionAccumulator) throws Exception {
    // do clear first as end method may not been invoked.
    sessionAccumulator.getUniSession().setUserAgent(null);
  }

  @Override
  public void process(UniEvent event, UniSessionAccumulator uniSessionAccumulator) throws Exception {
    String agentInfo = event.getUserAgent();
    boolean isEarlyValidEvent = com.ebay.epic.soj.common.utils.SojEventTimeUtil
            .isEarlyEvent(event.getEventTs(),
                    uniSessionAccumulator.getUniSession().getStartTimestamp());
    if (!event.getIframe()
            && (event.getRdt()==0)
            && !agentExcludeSet.contains(event.getPageId())
            && agentInfo != null
            && !agentInfo.equals(SHOCKWAVE_FLASH_AGENT)
            && !IsValidIPv4.isValidIP(agentInfo)) {
      if (agentInfo.length() > AGENT_MAX_LENGTH) {
        agentInfo = agentInfo.substring(0, AGENT_MAX_LENGTH);
      }
      if (isEarlyValidEvent) {
        uniSessionAccumulator.getUniSession().setUserAgent(agentInfo);
      } else {
        if (uniSessionAccumulator.getUniSession().getUserAgent() == null) {
          uniSessionAccumulator.getUniSession().setUserAgent(agentInfo);
        }
      }
    }

  }

}
