package net.pms.dlna;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import net.pms.configuration.RendererConfiguration;
import net.pms.util.FileUtil;
import net.pms.util.PlayerUtil;

/**
 * Base class representing web feed items.
 * @author anand
 *
 */
public abstract class WebStreamItem extends DLNAResource {

    protected String title;
    protected String url;
    protected String thumbURL;

    public WebStreamItem(int type) {
        super(type);
    }

    @Override
    public boolean isValid() {
    	resolveFormat();
    	return getFormat() != null;
    }

    /**
     * @return the url
     * @since 1.50
     */
    protected String getUrl() {
    	return url;
    }

    /**
     * @param url the url to set
     * @since 1.50
     */
    protected void setUrl(String url) {
    	this.url = url;
    }
    
    @Override
    public String getName() {
        return title;
    }

    @Override
    public boolean isFolder() {
        return false;
    }
    
    @Override
    public String getSystemName() {
        return getUrl();
    }

    @Override
    public InputStream getThumbnailInputStream() throws IOException {
        if (thumbURL != null) {
            return FileUtil.isUrl(thumbURL) ? downloadAndSend(thumbURL, true) : new FileInputStream(thumbURL);
        } else {
            return super.getThumbnailInputStream();
        }
    }
    
    @Override
    public InputStream getInputStream() {
        return null;
    }

    @Override
    public long length() {
        return DLNAMediaInfo.TRANS_SIZE;
    }
    
    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append(getClass().getSimpleName());
        result.append(" [id=");
        result.append(id);
        result.append(", name=");
        result.append(getName());
        result.append(", full path=");
        result.append(getResourceId());
        result.append(", ext=");
        result.append(getFormat());
        result.append(", url=");
        result.append(getUrl());
        result.append(", discovered=");
        result.append(isDiscovered());
        result.append(']');
        return result.toString();
    }

}
