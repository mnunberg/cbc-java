/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.cbc.commands;

import com.couchbase.cbc.CLIOptions;
import com.couchbase.cbc.Command;
import com.couchbase.client.CouchbaseClient;
import java.util.Map;
import net.spy.memcached.MemcachedNode;
import net.spy.memcached.ObserveResponse;

/**
 *
 * @author mnunberg
 */
public class ObserveHandler extends Command{
    public
            ObserveHandler(CouchbaseClient cli,
            Command.CommandType type,
            CLIOptions opts)
    {
        super(cli, type, opts);
    }
    
    @Override
    public boolean execute() {
        System.err.println(cli.observe(options.key, options.cas));
        return true;
    }
}
