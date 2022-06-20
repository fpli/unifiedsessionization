/**
 * Autogenerated by Avro
 *
 * DO NOT EDIT DIRECTLY
 */
package com.ebay.epic.common.model.avro;

@SuppressWarnings("all")
@org.apache.avro.specific.AvroGenerated
public interface GlobalEventTypes {
  public static final org.apache.avro.Protocol PROTOCOL = org.apache.avro.Protocol.parse("{\"protocol\":\"GlobalEventTypes\",\"namespace\":\"com.ebay.epic.sojourner.common.model.avro\",\"types\":[{\"type\":\"record\",\"name\":\"RheosHeader\",\"fields\":[{\"name\":\"eventCreateTimestamp\",\"type\":\"long\"},{\"name\":\"eventSentTimestamp\",\"type\":\"long\"},{\"name\":\"schemaId\",\"type\":\"int\"},{\"name\":\"eventId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"default\":null},{\"name\":\"producerId\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}}]},{\"type\":\"record\",\"name\":\"SojEvent\",\"doc\":\"tags not included but in UbiEvent: current_impr_id, source_impr_id, staticPageType, reservedForFuture, eventAttr, oldSessionSkey, seqNum, sessionStartDt, sojDataDt, version\",\"fields\":[{\"name\":\"rheosHeader\",\"type\":\"RheosHeader\",\"doc\":\"Rheos header\"},{\"name\":\"guid\",\"type\":{\"type\":\"string\",\"avro.java.string\":\"String\"}},{\"name\":\"eventTimestamp\",\"type\":\"long\",\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from timestamp, publish time\"},{\"name\":\"eventCaptureTime\",\"type\":[\"null\",\"long\"],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=mobile specific, map from mtsts, used for mobile case\"},{\"name\":\"requestCorrelationId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=request correlation id, map from tag rq\"},{\"name\":\"cguid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=correlation guid, map from tag n\"},{\"name\":\"sid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=Sojourner key for source id\"},{\"name\":\"pageId\",\"type\":[\"null\",\"int\"],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag p\"},{\"name\":\"pageName\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"pageFamily\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag pgf\"},{\"name\":\"eventFamily\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from efam of pulsar event, event family\"},{\"name\":\"eventAction\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from eactn of pulsar event, event action\"},{\"name\":\"userId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag u/bu\"},{\"name\":\"clickId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=Click Id, map from tag c\"},{\"name\":\"siteId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=Site ID, map from tag t\"},{\"name\":\"sessionId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag snid\"},{\"name\":\"seqNum\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag snsq\"},{\"name\":\"ciid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"siid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"rdt\",\"type\":[\"null\",\"int\"],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=1 indicates that the command redirected to another URL\"},{\"name\":\"regu\",\"type\":[\"null\",\"int\"],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=indicating that this is a registered user, map from tag regU\"},{\"name\":\"iframe\",\"type\":[\"null\",\"boolean\"],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag _ifrm\"},{\"name\":\"refererHash\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag r\"},{\"name\":\"sqr\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=search keyword, map from tag sQr\"},{\"name\":\"itemId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag itm/item\"},{\"name\":\"flags\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag flgs\"},{\"name\":\"urlQueryString\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag Referer\"},{\"name\":\"webServer\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=web server, map from Server\"},{\"name\":\"cookies\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag ck/C\"},{\"name\":\"bot\",\"type\":[\"null\",\"int\"],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag bott\"},{\"name\":\"clientIP\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"remoteIP\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag RemoteIP\"},{\"name\":\"agentInfo\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from tag Agent\"},{\"name\":\"appId\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=application ID, map from tag app\"},{\"name\":\"appVersion\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=application version, map from mav\"},{\"name\":\"osVersion\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=operation system version, map from osv\"},{\"name\":\"trafficSource\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from trffc_src\"},{\"name\":\"cobrand\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from cbrnd\"},{\"name\":\"deviceFamily\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from dd_d\"},{\"name\":\"deviceType\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from dd_dc\"},{\"name\":\"browserVersion\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from dd_bv\"},{\"name\":\"browserFamily\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from dd_bf\"},{\"name\":\"osFamily\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from dd_os\"},{\"name\":\"enrichedOsVersion\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"dataClassification=Internal|isEncrypted=false|description=map from dd_osv\"},{\"name\":\"applicationPayload\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"avro.java.string\":\"String\"}},{\"name\":\"rlogid\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}],\"doc\":\"CAL request log id\",\"default\":null},{\"name\":\"clientData\",\"type\":{\"type\":\"map\",\"values\":{\"type\":\"string\",\"avro.java.string\":\"String\"},\"avro.java.string\":\"String\"},\"doc\":\"Includes ForwardFor, ContentLength, Script, Server, Encoding, TMachine, TStamp, TName, TStatus, TDuration, TPayload\"},{\"name\":\"ingestTime\",\"type\":[\"null\",\"long\"]},{\"name\":\"sessionSkey\",\"type\":[\"null\",\"long\"]},{\"name\":\"sessionStartDt\",\"type\":[\"null\",\"long\"]},{\"name\":\"sojDataDt\",\"type\":[\"null\",\"long\"]},{\"name\":\"version","\",\"type\":[\"null\",\"int\"]},{\"name\":\"staticPageType\",\"type\":[\"null\",\"int\"]},{\"name\":\"reservedForFuture\",\"type\":[\"null\",\"int\"]},{\"name\":\"eventAttr\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"currentImprId\",\"type\":[\"null\",\"long\"]},{\"name\":\"sourceImprId\",\"type\":[\"null\",\"long\"]},{\"name\":\"oldSessionSkey\",\"type\":[\"null\",\"long\"]},{\"name\":\"partialValidPage\",\"type\":[\"null\",\"boolean\"]},{\"name\":\"botFlags\",\"type\":[\"null\",{\"type\":\"array\",\"items\":\"int\"}],\"default\":null},{\"name\":\"icfBinary\",\"type\":[\"null\",\"long\"]},{\"name\":\"eventCnt\",\"type\":[\"null\",\"long\"]},{\"name\":\"referrer\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"forwardedFor\",\"type\":[\"null\",{\"type\":\"string\",\"avro.java.string\":\"String\"}]},{\"name\":\"rv\",\"type\":[\"null\",\"boolean\"]},{\"name\":\"sojHeader\",\"type\":[\"null\",{\"type\":\"map\",\"values\":\"bytes\",\"avro.java.string\":\"String\"}],\"default\":null}]}],\"messages\":{}}");

  @SuppressWarnings("all")
  public interface Callback extends GlobalEventTypes {
    public static final org.apache.avro.Protocol PROTOCOL = GlobalEventTypes.PROTOCOL;
  }
}