/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.cbc;

import com.couchbase.client.CouchbaseClient;
import java.util.concurrent.ExecutionException;

/**
 *
 * @author mnunberg
 */
public abstract class Command {

    public enum CommandType {
        SET,
        APPEND,
        PREPEND,
        REPLACE,
        ADD,
        DELETE,
        GET,
        TOUCH,
        OBSERVE
    }
    
    protected CouchbaseClient cli;
    protected CLIOptions options;
    protected CommandType command;
    
    public Command(CouchbaseClient cli, CommandType type, CLIOptions options) {
        this.cli = cli;
        this.options = options;
        this.command = type;
    }

    public abstract boolean execute() throws ExecutionException ;
}
