package ObjectStructure;

import java.util.HashMap;
import java.util.Map;

import Tool.ST;

public class Message {
	
	public static final String ID 					= "ID";
	public static final String Password				= "Password";
	public static final String FunctionName			= "FunctionName";
	public static final String Map 					= "Map";
	public static final String Message 				= "Message";
	public static final String ErrorCode 			= "ErrorCode";
	public static final String PlayerMark 			= "PlayerMark";
	public static final String Move					= "Move";
	public static final String BombFlag				= "BombFlag";
	public static final String End					= "End";
	public static final String GameResult			= "GameResult";
	
	private HashMap<String, String> MsgMap;
	public Message(){
		
		MsgMap = new HashMap<String, String>();
	}
	
	public String getMsg(String inputKey){
		
		String result = MsgMap.get(inputKey.toLowerCase());
		result = ST.HexToString(result);
		
		return result;
		
	}
	public void setMsg(String inputKey, boolean inputValue){
		
		setMsg(inputKey, (inputValue + ""));
		
	}
	public void setMsg(String inputKey, int inputValue){
		
		setMsg(inputKey, (inputValue + ""));
		
	}
	public void setMsg(String inputKey, String inputValue){
		
		MsgMap.put(inputKey.toLowerCase(), ST.StringToHex(inputValue));
		
	}
	public Map<String, String> getMsgMap() {
		return MsgMap;
	}
	public void setMsgMap(HashMap<String, String> msgMap) {
		MsgMap = msgMap;
	}
}
