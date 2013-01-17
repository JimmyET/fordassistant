
package com.zzgo.util.http.parse.xml;

import org.xmlpull.v1.XmlPullParser;

import com.zzgo.util.http.exception.XmlParserError;
import com.zzgo.util.http.exception.XmlParserParseException;
import com.zzgo.util.http.parse.BaseObject;

/**
 * 预留接口
 * TODO 连接下载完成后,用来解析
 * @author fuliqiang
 * 2010-9-18
 * @param <T>
 */
public interface IXmlParser<T extends BaseObject> {

    public abstract T parse(XmlPullParser parser) throws XmlParserError, XmlParserParseException;

}
