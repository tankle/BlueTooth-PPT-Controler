package com.jasoncong.bluetooth;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.InputEvent;

import com.jasoncong.util.Key_Events;

public class KeySimulate {
	private Robot robot = null;
	
	/**构造函数*/
	public KeySimulate() {
		try {
			robot = new Robot();
		} catch (AWTException e) {
			// TODO Auto-generated catch block
			System.out.println("Robot create failed");
			e.printStackTrace();
		}
	}
	/** 模拟按键 */
	public void Simulate(int[] key , int len) throws InterruptedException{
		for(int i=0; i<len; i++){
			robot.keyPress(key[i]);
		}
		Thread.sleep(5);
		for(int i=0; i<len; i++){
			robot.keyRelease(key[i]);
		}
	}
	
	/** 模拟按键 */
	public void Simulate(int key) throws InterruptedException{
		if(key >= Key_Events.BUTTON1_MASK)
		{
			switch(key){
			case Key_Events.BUTTON1_MASK:
				robot.mousePress(InputEvent.BUTTON1_MASK);
				Thread.sleep(10);
				robot.mouseRelease(InputEvent.BUTTON1_MASK);
				break;
			case Key_Events.BUTTON2_MASK:
				robot.mousePress(InputEvent.BUTTON2_MASK);
				Thread.sleep(10);
				robot.mouseRelease(InputEvent.BUTTON2_MASK);
				break;
			case Key_Events.BUTTON3_MASK:
				robot.mousePress(InputEvent.BUTTON3_MASK);
				Thread.sleep(10);
				robot.mouseRelease(InputEvent.BUTTON3_MASK);
				break;

			default:
				break;
			}
		}
		else{
			robot.keyPress(key);
			Thread.sleep(5);
			robot.keyRelease(key);
			Thread.sleep(5);
		}
			
		
	}
}
