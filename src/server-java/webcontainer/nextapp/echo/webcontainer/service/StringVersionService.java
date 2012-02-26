package nextapp.echo.webcontainer.service;

import nextapp.echo.webcontainer.Service;

/**
 * A service with a string version attribute.  This may be used in the case
 * that a service wishes to produce a uid representing its content to allow
 * long term caching, for example a MD5 hash.  It is not possible to represent
 * that using the {@link Service#getVersion()} method that returns an integer.
 * @author developer
 *
 */
public interface StringVersionService extends Service {
    
    /**
     * Returns the service version expressed as a string, or
     * <code>null</code> if the service has no version set.
     * @return the service version expressed as a string, or
     * <code>null</code> if the service has no version set.
     */
    public String getVersionAsString();
    
}