package ObjectStructure;

public class State {
	public static String InPlayerList = "InPlayerList";
	public static String InPairPool = "InPairlPool";
	public static String InMap = "InMap";
	private String Status;
	
	public String getStatus() {
		return Status;
	}
	public State(){
		
		Status = State.InPlayerList;
		
	}
	@Override
	public String toString() {
		return Status;
	}
	public void setStatus(String inputState) {
		Status = inputState;
	}
	
}
