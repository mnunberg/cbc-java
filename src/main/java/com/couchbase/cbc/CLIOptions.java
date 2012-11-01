/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.couchbase.cbc;

import com.beust.jcommander.Parameter;
/**
 *
 * @author mnunberg
 */
public class CLIOptions {
    @Parameter(names = {"-H", "-hostname"}, description = "Hostname:port to connect to")
    public String hostname = "127.0.0.1:8091";
    
    @Parameter(names = {"-b", "-bucket"})
    public String bucket = "default";
    
    @Parameter(names = {"-u", "-username"})
    public String username = null;
    
    @Parameter(names = {"-p", "-password"})
    public String password = null;
    
    @Parameter(names = {"-k", "-key"}, description = "Key to operate on", required = true)
    public String key;
    
    @Parameter(names = {"-V", "-value"}, description = "Value (if applicable)")
    public String value;
    
    @Parameter(names = {"-C", "-cas"}, description = "CAS to use (if applicable)")
    public long cas = 0;
    
    @Parameter(names = {"-E", "-expiry"}, description = "Expiry (for set or lock)")
    public int expiry = 0;
    
    @Parameter(names = { "-L", "-lock"}, description = "Lock for this many seconds")
    public int lockTimeout = -1;
    
    @Parameter(names = {"-P", "-persist"}, description = "Persist to these many nodes")
    public int persist = 0;
    
    @Parameter(names = {"-R", "-replicate"}, description = "Replicate to these many nodes")
    public int replicate = 0;
    
    
    @Parameter(names = {"-h", "--help", "-?"}, help = true, description = "This message")
    public boolean help = false;
    
}