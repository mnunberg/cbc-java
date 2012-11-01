package com.couchbase.cbc;

import com.beust.jcommander.JCommander;
import com.couchbase.cbc.commands.GetHandler;
import com.couchbase.cbc.commands.ObserveHandler;
import com.couchbase.cbc.commands.SetHandler;
import com.couchbase.client.CouchbaseClient;
import com.couchbase.client.CouchbaseConnectionFactory;
import java.net.URI;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Hello world!
 *
 */
public class App 
{
    
    private static final Map<Command.CommandType,Class> commandMap =
            new EnumMap<Command.CommandType, Class>(Command.CommandType.class);
    static {
        commandMap.put(Command.CommandType.SET, SetHandler.class);
        commandMap.put(Command.CommandType.ADD, SetHandler.class);
        commandMap.put(Command.CommandType.APPEND, SetHandler.class);
        commandMap.put(Command.CommandType.PREPEND, SetHandler.class);
        commandMap.put(Command.CommandType.REPLACE, SetHandler.class);
        
        commandMap.put(Command.CommandType.OBSERVE, ObserveHandler.class);
        
        commandMap.put(Command.CommandType.GET, GetHandler.class);
    }
    
    static private void printCommands() {
        System.err.println("The following commands are recognized");
        for (Command.CommandType ei : Command.CommandType.values()) {
            System.err.println(ei.toString().toLowerCase());
        }
    }
    
    public static void main( String[] args )
    {
        if (args.length < 2) {
            System.err.println("Usage <command> <args...>");
            printCommands();
            System.exit(-1);
        }
        
        String[] realArgs = Arrays.copyOfRange(args, 1, args.length);
        Command.CommandType cmd = null;
        String cmdStr = args[0].toUpperCase();
        
        try {
            cmd = Command.CommandType.valueOf(cmdStr.toUpperCase());
        } catch (IllegalArgumentException ex) {
            System.err.printf("No such command %s\n", args[0]);
            printCommands();
            System.exit(-1);
        }
        
        CLIOptions options = new CLIOptions();
        JCommander jct = new JCommander(options, realArgs);
        
        if (options.help) {
            jct.usage();
            System.exit(0);
        }
        
        CouchbaseClient cli = null;
        LinkedList<URI> uriList = new LinkedList<URI>();
        
        
        try {
            uriList.add(new URI("http://" + options.hostname + "/pools"));
            CouchbaseConnectionFactory factory = new ConnFactory(options, uriList);
            cli = new CouchbaseClient(factory);
            
        } catch (Exception ex) {
            ex.printStackTrace();
            System.exit(-1);
        }
        try {
            boolean res;
            Class cls = commandMap.get(cmd);
            if (cls == null) {
                throw new Exception("not implemented..");
            }
            
            Command handler = 
                    (Command)cls.getConstructor(CouchbaseClient.class,
                    Command.CommandType.class,
                    CLIOptions.class)
                    
                    .newInstance(cli, cmd, options);
            
            res = handler.execute();
            
        } catch (ExecutionException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            cli.shutdown();
        }
    }
}
