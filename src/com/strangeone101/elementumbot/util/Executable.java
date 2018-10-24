package com.strangeone101.elementumbot.util;

public class Executable {

	public String cmd;
	public String[] execute;
	public int possibleArgs;
	
	public Executable(String cmd, String[] args) {
		this.cmd = cmd;
		this.execute = args;
		this.possibleArgs = args.length;
		
		for (String arg : args) {
			if (!(arg.equals("*"))) {
				this.possibleArgs--;
			}
		}
	}
	
	public String getCommand() {
		return cmd;
	}
	
	public String[] getArray() {
		return execute;
	}
	
	public int getNumberOfPossibleArgs() {
		return possibleArgs;
	}
	
	public String getExecuteStatement(String[] userInput) {
		if (userInput == null || userInput.length == 0) {
			userInput = new String[execute.length];
		}
		
		StringBuilder builder = new StringBuilder("!execute " + cmd);
		
		int counter = 0;
		for (int i = 0; i < execute.length; i++) {
			String arg = execute[i];
			
			if (arg.equals("*")) {
				arg = userInput[counter++];
			}
			
			if (arg == null || arg.equals("null")) {
				arg = "";
			}
			
			if (!arg.equals("")) {
				builder.append(" " + arg);
			}
		}
		
		return builder.toString();
	}
	
	public String getArgsString() {
		return StringUtil.combine(execute).trim();
	}
	
	public String getOptionalArgsString() {
		String[] optional = new String[execute.length];
		for (int i = 0; i < execute.length; i++) {
			String arg = execute[i];
			
			if (arg.contains("*")) {
				optional[i] = arg;
			} else {
				optional[i] = "";
			}
		}
		
		return StringUtil.combine(optional).trim();
	}
}
