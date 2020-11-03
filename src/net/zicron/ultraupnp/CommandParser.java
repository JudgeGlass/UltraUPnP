package net.zicron.ultraupnp;

public class CommandParser {

    public static CommandParser currentParser = new CommandParser();

    public String host;
    public int internalPort;
    public int externalPort;
    public String protocol;

    public void add(String args[]){
        for(int i = 1; i < args.length; i+=2){
            String value = args[i + 1];
            switch (args[i]){
                case "-externalPort":
                    externalPort = Integer.parseInt(value);
                    break;
                case "-internalPort":
                    internalPort = Integer.parseInt(value);
                    break;
                case "-host":
                    host = value;
                    break;
                case "-proto":
                    protocol = value.toUpperCase();
                    break;
                default:
                    Log.error("[PARSER] Unknown Argument: " + args[i]);
            }
        }
    }

    public void remove(String args[]){
        for(int i = 1; i < args.length; i+=2){
            String value = args[i + 1];
            switch (args[i]){
                case "-externalPort":
                    externalPort = Integer.parseInt(value);
                    break;
                case "-host":
                    host = value;
                    break;
                case "-proto":
                    protocol = value.toUpperCase();
                    break;
                default:
                    Log.error("[PARSER] Unknown Argument: " + args[i]);
            }
        }
    }
}
