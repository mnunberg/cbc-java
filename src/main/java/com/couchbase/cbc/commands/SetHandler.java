/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.cbc.commands;
import com.couchbase.cbc.CLIOptions;
import com.couchbase.cbc.Command;
import com.couchbase.client.CouchbaseClient;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import net.spy.memcached.*;
import net.spy.memcached.internal.OperationFuture;
/**
 *
 * @author mnunberg
 */
public class SetHandler extends Command {
    
    private PersistTo persist = null;
    private ReplicateTo replicate = null;
    private boolean canExpire = true;
    private boolean canCas = true;
    private boolean canPersist = true;
    private boolean canReplicate = true;
    long cas;

    
    static <T extends Enum> T getObserveConstant(Class T, int value)
            throws ExecutionException{
        
        Object[] list = T.getEnumConstants();
        for (Object o : list ) {
            try {
                
                Method m = T.getMethod("getValue");
                if (value == (Integer)m.invoke(o)) {
                    return (T)o;
                }
            } catch (Exception ex) {
                throw new ExecutionException(ex);
            }
        }
        
        throw new IllegalArgumentException("Couldn't find " + value);
    }
    
    private OperationFuture 
            executeGenericMutation(String methname)
            throws Exception 
    {
        if (persist != null) {
            canPersist = true;
            canReplicate = true;
            System.err.printf("Using Persist=%s, Replicate=%s\n",
                    persist, replicate);
            
            return (OperationFuture)CouchbaseClient.class.getMethod(methname,
                    String.class,
                    int.class,
                    String.class,
                    PersistTo.class,
                    ReplicateTo.class)
                    
                    .invoke(
                    cli,
                    options.key, options.expiry, options.value,
                    persist, replicate);
            
        } else {
            canPersist = false;
            canReplicate = false;
            
            return (OperationFuture)CouchbaseClient.class.getMethod(methname,
                    String.class, int.class, Object.class)
                    
                    .invoke(cli, options.key, options.expiry, options.value);
        }
    }
    
    public SetHandler(CouchbaseClient cli,
            Command.CommandType type,
            CLIOptions options) throws ExecutionException {
        
        super(cli, type, options);
        
        if (options.value == null) {
            throw new IllegalArgumentException("Set command must have value");
        }
        if (options.persist != 0) {
            persist = getObserveConstant(PersistTo.class, options.persist);
        }
        
        replicate = getObserveConstant(ReplicateTo.class, options.replicate);
    }
    
    
    private boolean runCommand() throws Exception {
        OperationFuture ft = null;
        CASResponse cr = null;
        
        switch (command) {
            case SET:
                if (options.cas != 0) {
                    if (persist != null) {
                        cr = cli.cas(options.key,
                                options.cas,
                                options.value,
                                persist,
                                replicate);
                        
                    } else {
                        System.err.println("Not setting expirty with cas..");
                        canPersist = false;
                        canReplicate = false;
                    }
                    canExpire = false;
                    
                } else {
                    ft = executeGenericMutation("set");
                }
                
                break;
                
            case ADD:
            case REPLACE:
                String methName = command.toString().toLowerCase();
                ft = executeGenericMutation(methName);
                canCas = false;
                break;
                                
            case APPEND:
            case PREPEND:
                // java blows
                if (command == CommandType.APPEND) {
                    ft = cli.append(options.cas, options.key, options.value);
                } else {
                    ft = cli.prepend(options.cas, options.key, options.value);
                }
                
                canCas = false;
                canExpire = false;
                canPersist = false;
                canReplicate = false;
                
                break;
                
            default:
                throw new IllegalArgumentException("This class does not support " 
                        + command);
        }
        
        try {
            if (ft != null) {
                ft.get();
                if (ft.getStatus().isSuccess()) {
                    System.err.println("Success!");
                    try {
                        cas = ft.getCas();
                        System.out.printf("Got CAS: %d\n", cas);
                    } catch (UnsupportedOperationException exc) {
                        System.err.printf("Apparently CAS isn't " + 
                                "supported for this operation\n");
                    }
                    
                } else {
                    System.err.println("Grrrr...");
                    System.err.println(ft.getStatus().getMessage());
                }
            } else {
                if (cr == CASResponse.OK) {
                    System.err.println("CAS OK!");
                } else {
                    System.err.printf("Couldn't execute: %s\n", cr);
                }
            }
            
            if (cas != 0 && (persist != null || replicate != ReplicateTo.ZERO)) {
                Map<MemcachedNode,ObserveResponse> observeRes = 
                        cli.observe(options.key, cas);
                
                System.out.println(observeRes);
            }
            
        } catch (ExecutionException ex) {
            System.err.println("Couldn't execute command:");
            ex.getCause().printStackTrace();
        } catch (InterruptedException ex) {
            System.err.println("interrupted?");
        }
        
        return true;

    }
    
    @Override
    public boolean execute() throws ExecutionException {
        try {
            return runCommand();
        } catch (ExecutionException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new ExecutionException(ex);
        }
    }
}
