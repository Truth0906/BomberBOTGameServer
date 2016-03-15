package Tool;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.TimeZone;

import com.google.gson.Gson;

import ObjectStructure.Message;

public class ST {//Server Tool
	
	
	
	public static String getTime(){
    	SimpleDateFormat nowdate = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    	nowdate.setTimeZone(TimeZone.getTimeZone("GMT+8"));
    	String result ="["+nowdate.format(new java.util.Date())+"]";
    	return result;
    }
	public static void showOnScreen(String inputLogName, String inputMsg){
		//String style = System.lineSeparator();
		String style = " ";
		
		System.out.println(ST.getTime() + "[" + inputLogName + "]" + style + inputMsg);
	}
	public static String B64Encode(String input){
		Base64.Encoder encoder = Base64.getEncoder();
		
		String result = null;
		try {
			result = encoder.encodeToString(input.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	public static String B64decode(String input){
		if(input == null) return null;
		
		Base64.Decoder decoder = Base64.getDecoder();
		
		String result = null;
		try {
			result =  new String(decoder.decode(input), "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
	}
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	private static String ByteToHex(byte[] input){
		
		char[] hexChars = new char[input.length * 2];
		for ( int j = 0; j < input.length; j++ ) {
		    int v = input[j] & 0xFF;
		    hexChars[j * 2] = hexArray[v >>> 4];
		    hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		
		return new String(hexChars);
		
	}
	public static String SHA256(String inputData){
		
		MessageDigest sha = null;
		
		try{
			sha = MessageDigest.getInstance("SHA-256");  
			sha.update(inputData.getBytes("UTF-8"));  
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
		String result = ByteToHex(sha.digest()); 
		
		return result;
		
	}
	public static Message StringToMessage(String inputMsgString){
		Gson gson = new Gson();
		
		Message result = gson.fromJson("{\"MsgMap\":" + inputMsgString + "}", Message.class);
		
		return result;	
	}
	
	public static String MessageToString(Message inputMsg){
		Gson gson = new Gson();
		
		String result = gson.toJson(inputMsg);
		//result = result.replace("{\"MsgMap\":", "");
		result = result.substring(10, result.length() -1);
		return result;		
	}
}
