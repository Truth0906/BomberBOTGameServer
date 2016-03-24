package ObjectStructure;

public class State {
	public static String InPlayerList = "In Player List";
	public static String InPairPool = "In Pairl Pool";
	public static String InMap = "In Map";
	
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
		// TODO Auto-generated method stub
		
	}
	
}
