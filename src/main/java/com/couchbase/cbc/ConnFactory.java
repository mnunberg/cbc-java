/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.cbc;

import com.couchbase.client.CouchbaseConnectionFactory;
import com.couchbase.client.ViewConnection;
import com.couchbase.client.vbucket.config.Config;
import com.couchbase.client.vbucket.config.ConfigType;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.util.List;

/**
 *
 * @author mnunberg
 */
public class ConnFactory extends CouchbaseConnectionFactory {
    protected CLIOptions options;
    protected Config vbConfig = null;

    public ConnFactory(CLIOptions options, List<URI> uriList) throws IOException {
        super(uriList, options.bucket, options.password);
        this.options = options;
    }
    
    
    
    @Override
    public ViewConnection createViewConnection(List<InetSocketAddress> addrs)
            throws IOException {
        if (getVBucketConfig().getConfigType() == ConfigType.MEMCACHE) {
            System.err.println("Not allowing view connection for memcached bucket");
            return null;
        }
        return super.createViewConnection(addrs);
    }
    
}
