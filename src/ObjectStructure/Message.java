package ObjectStructure;

import java.util.HashMap;
import java.util.Map;

import Tool.ST;

public class Message {
	private Map<String, String> MsgMap;
	public Message(){
		
		MsgMap = new HashMap<String, String>();
	}
	
	public String getMsg(String inputKey){
		
		String result = MsgMap.get(inputKey.toLowerCase());
		result = ST.B64decode(result);
		
		return result;
		
	}
	public void setMsg(String inputKey, int inputValue){
		
		MsgMap.put(inputKey, inputValue + "");
		
	}
	public void setMsg(String inputKey, String inputValue){
		
		MsgMap.put(inputKey.toLowerCase(), ST.B64Encode(inputValue));
		
	}
	public Map<String, String> getMsgMap() {
		return MsgMap;
	}
	public void setMsgMap(Map<String, String> msgMap) {
		MsgMap = msgMap;
	}
}
