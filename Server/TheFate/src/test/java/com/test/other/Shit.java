package com.test.other;

public class Shit extends Base{
	
	private int value;
	
	public Shit() {
		
	}
	
	public Shit(Integer value) {
		this.value = value;
	}
	
	@Override
	public void print() {
		System.out.println(String.format("%s Shit!!!", value));
	}

	@Override
	public String toString() {
		return String.format("Shit [value=%s]", value);
	}
	
}
