package net.pms.dlna;

import java.io.IOException;
import java.io.InputStream;

/**
 * Base class for media which are actually streams. e.g. desktop capture, web streams, TV feeds etc.
 * If not specified explicitly, the default mime type is video/mp4.
 * 
 * @author atamariya@gmail.com
 *
 */
public abstract class StreamItem extends DLNAResource {

    public StreamItem(int specificType) {
        super(specificType);
    }

    @Override
    public long length() {
        return DLNAMediaInfo.TRANS_SIZE;
    }

    @Override
    public InputStream getInputStream() throws IOException {
    	return null;
    }

}
