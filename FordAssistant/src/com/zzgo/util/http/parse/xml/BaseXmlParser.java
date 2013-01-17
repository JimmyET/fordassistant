package com.zzgo.util.http.parse.xml;

import java.io.IOException;
import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.zzgo.util.LogUtil;
import com.zzgo.util.http.exception.XmlParserError;
import com.zzgo.util.http.exception.XmlParserParseException;
import com.zzgo.util.http.parse.BaseObject;

/**
 * 解析逻辑的基础类
 * 
 * @author fuliqiang
 * @param <T>
 */
public abstract class BaseXmlParser<T extends BaseObject> implements IXmlParser<T> {

	private static final boolean DEBUG = LogUtil.LOGGABLE;

	private static XmlPullParserFactory xmlPullParserFactory;

	static {
		try {
			xmlPullParserFactory = XmlPullParserFactory.newInstance();
			xmlPullParserFactory.setNamespaceAware(true);

		} catch (XmlPullParserException e) {
			throw new IllegalStateException("create factory error");
		}
	}

	/**
	 * 判断文档中是否有错误信息,如果发现错误信息直接抛异常
	 * 
	 * @see net.uiiang.android.BiscuitBox.autocode.xml.parsers.Parser#parse(org.xmlpull.v1.XmlPullParser)
	 */
	public final T parse(XmlPullParser parser) throws XmlParserParseException, XmlParserError {
		if (DEBUG) {
			LogUtil.i("in baseparser parse() name = " + parser.getName());
		}
		try {
			if (parser.getEventType() == XmlPullParser.START_DOCUMENT) {
				parser.nextTag();
				LogUtil.i("in baseparser parse() name2 = " + parser.getName());
				if ("error".equals(parser.getName())) {
					throw new XmlParserError(parser.nextText());
				}
			}
			return parseInner(parser);
		} catch (IOException ioException) {
			if (DEBUG) {
				LogUtil.e("IOException", ioException);
			}
			throw new XmlParserParseException(ioException.getMessage());
		} catch (XmlPullParserException xmlPullParserException) {
			if (DEBUG) {
				LogUtil.e("XmlPullParserException", xmlPullParserException);
			}
			throw new XmlParserParseException(xmlPullParserException.getMessage());
		}
	}

	/**
	 * 根据流创建一个XmlPullParser对象
	 * 
	 * @param inputStream
	 * @return
	 */
	public static final XmlPullParser createXmlPullParser(InputStream inputStream, String encodeing) {
		XmlPullParser parser;
		try {
			parser = xmlPullParserFactory.newPullParser();
			// StringBuilder builder = new StringBuilder();
			// BufferedReader in = new BufferedReader(new InputStreamReader(
			// inputStream));
			// String line = "";
			// while ((line = in.readLine()) != null) {
			// builder.append(new String(line.getBytes(code), encodeing));
			// }
			//
			// // while (true) {
			// // final int ch = inputStream.read();
			// // if (ch < 0) {
			// // break;
			// // } else {
			// // builder.append((char) ch);
			// // }
			// // }
			// inputStream.close();//close
			// if (DEBUG) {
			// LogUtil.d(builder.toString());
			// parser.setInput(new StringReader(builder.toString()));
			// } else {
			parser.setInput(inputStream, encodeing);
			// }
		} catch (XmlPullParserException xmlPullParserException) {
			LogUtil.d("SB了吧 报错了吧 文件又不能通过了吧");
			throw new IllegalArgumentException();
		}
		// catch (IOException ioException) {
		// LogUtil.d("SB了吧 报错了吧 程序又他妈不运行了吧");
		// throw new IllegalArgumentException();
		// }
		return parser;
	}

	// public static void missSubTree(XmlPullParser parser)
	/**
	 * @param parser
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	public static void skipSubTree(XmlPullParser parser) throws XmlPullParserException, IOException {
		parser.require(XmlPullParser.START_TAG, null, null);
		int level = 1;
		while (level > 0) {
			int eventType = parser.next();
			if (eventType == XmlPullParser.END_TAG) {
				// level -=1;
				--level;
			} else if (eventType == XmlPullParser.START_TAG) {
				// level +=1;
				// level ++;
				++level;
			}
		}
	}

	// /**
	// * 将xml元素中的属性值名称及对应值装载到map中,此方法要在实现类中调用
	// * @param parser
	// * @return 如果元素中无属性值,则返回null,使用时需要判断
	// */
	// public static HashMap<String, String> attributeToMap(XmlPullParser
	// parser) {
	// HashMap<String, String> attributeMap = null;
	// if (parser.getAttributeCount() > 0) {
	// attributeMap = new HashMap<String, String>();
	// for (int i = 0; i < parser.getAttributeCount(); i++) {
	// attributeMap.put(parser.getAttributeName(i),
	// parser.getAttributeValue(i));
	// }
	// }
	// return attributeMap;
	// }

	abstract protected T parseInner(final XmlPullParser parser) throws IOException, XmlPullParserException,
			XmlParserError, XmlParserParseException;
}
