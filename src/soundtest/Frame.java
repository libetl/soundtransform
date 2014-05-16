package soundtest;

public class Frame {

	private byte [] data;
	
	public Frame (byte [] data){
		this.data = data;
	}

	public byte [] getData () {
		return data;
	}

	public void setData (byte [] data) {
		this.data = data;
	}
}
