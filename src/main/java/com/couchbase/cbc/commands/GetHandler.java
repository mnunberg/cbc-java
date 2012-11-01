/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.cbc.commands;

import com.couchbase.cbc.CLIOptions;
import com.couchbase.cbc.Command;
import com.couchbase.client.CouchbaseClient;
import java.util.concurrent.ExecutionException;
import net.spy.memcached.CASValue;
import net.spy.memcached.internal.OperationFuture;
import net.spy.memcached.ops.OperationStatus;

/**
 *
 * @author mnunberg
 */
public class GetHandler extends Command {
    public GetHandler(CouchbaseClient cli,
            Command.CommandType type,
            CLIOptions options) {
        super(cli, type, options);
    }
    
    @Override
    public boolean execute() throws ExecutionException {
        
        OperationFuture<CASValue<Object>> ftCas;
        
        
        if (options.lockTimeout > 0) {
            ftCas = cli.asyncGetAndLock(options.key, options.lockTimeout);
        } else if (options.expiry > 0) {
            ftCas = cli.asyncGetAndTouch(options.key, options.expiry);
        } else {
            ftCas = cli.asyncGets(options.key);
        }
        
        OperationStatus st;
        CASValue<Object> casRet = null;
        
        try {
            casRet = ftCas.get();
        } catch (Exception ex) {
            throw new ExecutionException(ex);
        }
        
        st = ftCas.getStatus();
        if (st.isSuccess()) {
            System.err.println("Got value " + casRet.getValue());
            System.err.println("Got CAS " + casRet.getCas());
        } else {
            System.err.println("Got error: ");
            System.err.println(st.toString());
        }
        
        return true;
    }
}
