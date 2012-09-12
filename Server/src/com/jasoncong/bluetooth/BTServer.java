package com.jasoncong.bluetooth;

import java.awt.AWTException;

import java.io.IOException;
import java.io.InputStream;

import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import javax.microedition.io.StreamConnectionNotifier;

public class BTServer implements Runnable {
	
	// 流连接通知 用于创建流连接
	private StreamConnectionNotifier myPCConnNotifier = null;
	// 流连接
	private StreamConnection streamConn = null;
	// 接受数据字节流
	private byte[] acceptedByteArray = new byte[12];
	// 读取（输入）流
	private InputStream inputStream = null;

	/**
	 * 主线程
	 *  
	 * @param args
	 */
	public static void main(String[] args) {
		new BTServer();
	}

	/**
	 * 构造方法
	 */
	public BTServer() {
		try {
			// 得到流连接通知，下面的UUID必须和手机客户端的UUID相一致。
			myPCConnNotifier = (StreamConnectionNotifier) Connector
					.open("btspp://localhost:0000110100001000800000805F9B34FB");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// 打开连接通道并读取流线程
		new Thread(this).start();
	}

	@Override
	public void run() {
		try {
			KeySimulate keySimulate = new KeySimulate();
			
			String inSTR = null;
			// 持续保持着监听客户端的连接请求
			while (true) {
				// 获取流连接
				streamConn = myPCConnNotifier.acceptAndOpen();
				// 获取流通道
				inputStream = streamConn.openInputStream();
				// 读取字节流
				int num;
				while ((num = inputStream.read(acceptedByteArray)) != -1) {
					inSTR = new String(acceptedByteArray);
					
					if (inSTR.contains("QUIT")) {
						// 手机客户端退出则关闭连接通道。
						inputStream.close();
						if (streamConn != null) {
							streamConn.close();
						} 
						break;
					}  
					String[] tmp = inSTR.substring(0,num).split("#");
					//System.out.println(tmp);
					
					for(int i=0; i< tmp.length; i++){
						System.out.print(tmp[i]+" ");
					}
					System.out.println();
					
//					System.out.println(num);
//					System.out.println("The first ch is "+inSTR.charAt(0));
					
					if(tmp.length > 1){
						int[] array = new int[tmp.length];
						for(int i=0; i< tmp.length; i++)
							array[i] = Integer.valueOf(tmp[i]);
						keySimulate.Simulate(array, array.length);
					}else{
						int key = Integer.valueOf(tmp[0]);
						keySimulate.Simulate(key);
					}			
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
