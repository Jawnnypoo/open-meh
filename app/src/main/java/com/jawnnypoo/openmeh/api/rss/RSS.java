package com.jawnnypoo.openmeh.api.rss;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

/**
 * Basic model for pulling an RSS XML feed
 */
@Root
public class RSS {

    @Attribute(name = "version")
    String mVersion;

    @Element(name = "channel")
    Channel mChannel;

    public Channel getChannel() {
        return mChannel;
    }

    @Override
    public String toString() {
        return "RSS{" +
                "version='" + mVersion + '\'' +
                ", channel=" + mChannel +
                '}';
    }
}