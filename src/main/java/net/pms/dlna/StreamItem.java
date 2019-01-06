package net.pms.dlna;

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

}
