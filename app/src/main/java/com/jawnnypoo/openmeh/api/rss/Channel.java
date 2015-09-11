package com.jawnnypoo.openmeh.api.rss;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Namespace;
import org.simpleframework.xml.NamespaceList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import java.util.List;

/**
 * Channel for an RSS XML feed
 */
@NamespaceList({
        @Namespace(reference = "http://www.w3.org/2005/Atom", prefix = "atom")
})
@Root(strict = false)
public class Channel {
    // Tricky part in Simple XML because the link is named twice
    @ElementList(entry = "link", inline = true, required = false)
    private List<Link> mLinks;
    @ElementList(name = "item", required = true, inline = true)
    private List<Item> mItemList;
    @Element(name = "title")
    private String mTitle;
    @Element(name = "language", required = false)
    private String mLanguage;
    @Element(name = "ttl", required = false)
    private int mTtl;
    @Element(name = "pubDate", required = false)
    private String mPubDate;

    public List<Link> getLinks() {
        return mLinks;
    }

    public List<Item> getItemList() {
        return mItemList;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public int getTtl() {
        return mTtl;
    }

    public String getPubDate() {
        return mPubDate;
    }

    @Override
    public String toString() {
        return "Channel{" +
                "links=" + mLinks +
                ", itemList=" + mItemList +
                ", title='" + mTitle + '\'' +
                ", language='" + mLanguage + '\'' +
                ", ttl=" + mTtl +
                ", pubDate='" + mPubDate + '\'' +
                '}';
    }

    public static class Link {
        @Attribute(name = "href", required = false)
        private String href;
        @Attribute(name = "rel", required = false)
        private String rel;
        @Attribute(name = "type", required = false)
        private String contentType;
        @Text(required = false)
        private String link;

        public String getHref() {
            return href;
        }

        public String getRel() {
            return rel;
        }

        public String getContentType() {
            return contentType;
        }

        public String getLink() {
            return link;
        }
    }

    @Root(name = "item", strict = false)
    public static class Item {

        @Element(name = "title", required = true)
        private String title;//The title of the item.	Venice Film Festival Tries to Quit Sinking
        @Element(name = "link", required = true)
        private String link;//The URL of the item.	http://www.nytimes.com/2002/09/07/movies/07FEST.html
        @Element(name = "description", required = true)
        private String description;//The item synopsis.	Some of the most heated chatter at the Venice Film Festival this week was about the way that the arrival of the stars at the Palazzo del Cinema was being staged.
        @Element(name = "author", required = false)
        private String author;//Email address of the author of the item. More.	oprah@oxygen.net
        @Element(name = "category", required = false)
        private String category;//Includes the item in one or more categories. More.	Simpsons Characters
        @Element(name = "comments", required = false)
        private String comments;//URL of a page for comments relating to the item. More.	http://www.myblog.org/cgi-local/mt/mt-comments.cgi?entry_id=290
        @Element(name = "enclosure", required = false)
        private String enclosure;//	Describes a media object that is attached to the item. More.	<enclosure url="http://live.curry.com/mp3/celebritySCms.mp3" length="1069871" type="audio/mpeg"/>
        @Element(name = "guid", required = false)
        private String guid;//A string that uniquely identifies the item. More.	<guid isPermaLink="true">http://inessential.com/2002/09/01.php#a2</guid>
        @Element(name = "pubDate", required = false)
        private String pubDate;//	Indicates when the item was published. More.	Sun, 19 May 2002 15:21:36 GMT
        @Element(name = "source", required = false)
        private String source;//	The RSS channel that the item came from. More.

        public String getTitle() {
            return title;
        }

        public String getLink() {
            return link;
        }

        public String getDescription() {
            return description;
        }

        public String getAuthor() {
            return author;
        }

        public String getCategory() {
            return category;
        }

        public String getComments() {
            return comments;
        }

        public String getEnclosure() {
            return enclosure;
        }

        public String getGuid() {
            return guid;
        }

        public String getPubDate() {
            return pubDate;
        }

        public String getSource() {
            return source;
        }

        @Override
        public String toString() {
            return "Item{" +
                    "title='" + title + '\'' +
                    ", link='" + link + '\'' +
                    ", description='" + description + '\'' +
                    ", author='" + author + '\'' +
                    ", category='" + category + '\'' +
                    ", comments='" + comments + '\'' +
                    ", enclosure='" + enclosure + '\'' +
                    ", guid='" + guid + '\'' +
                    ", pubDate='" + pubDate + '\'' +
                    ", source='" + source + '\'' +
                    '}';
        }
    }
}