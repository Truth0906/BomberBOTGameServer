package ObjectStructure;

import java.util.HashMap;
import java.util.Map;

import Tool.ST;

public class Message {
	private HashMap<String, String> MsgMap;
	public Message(){
		
		MsgMap = new HashMap<String, String>();
	}
	
	public String getMsg(String inputKey){
		
		String result = MsgMap.get(inputKey.toLowerCase());
		result = ST.HexToString(result);
		
		return result;
		
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
