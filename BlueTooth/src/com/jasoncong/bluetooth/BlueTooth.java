package com.jasoncong.bluetooth;



import com.jasoncong.utils.*;

import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

/**
 * BlueTooth & Sensor
 * 
 * 
 */

public class BlueTooth extends Activity {

	private static final int REQUEST_DISCOVERY = 0x1;
	// 建立蓝牙通信的UUID 
	private static final UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
	// 自带的蓝牙适配器
	private BluetoothAdapter bluetoothAdapter = null;
	// 扫描得到的蓝牙设备
	private BluetoothDevice device = null;
	// 蓝牙通信socket
	private BluetoothSocket btSocket = null;
	// 手机输出流
	private OutputStream outStream = null;
	private byte[] msgBuffer = null;
	// 传感器管理
	private SensorManager sensorMgr = null;
	// 传感器感应
	private Sensor sensor = null;
	// 手机x、y、z轴方向数据
	private int x, y, z;
	
	//按钮
//	Button devBtn = null;
	Button leftBtn = null;
	Button rightBtn = null;
	Button quitBtn = null;
	Button playBtn = null;
	Button left_clickBtn = null;
	Button right_clickBtn = null;
	
	private MenuInflater myMenu;
	
	/**
	 * 当这个activity第一次被创建的时候呼叫该方法
	 **/
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		/* 使程序窗口全屏 */
		// 创建一个没有title的全屏主题
		this.setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		// 窗口全屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏标志
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 按bluetooth.xml文件布局风格
		setContentView(R.layout.bluetooth);

		// Gravity sensing 获取传感器
		sensorMgr = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sensor = sensorMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

		// 获取手机默认上的蓝牙适配器
		bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

		// 开启手机蓝牙设备
		bluetoothAction();

		
		
//		devBtn = (Button)findViewById(R.id.devBtn);
		leftBtn = (Button)findViewById(R.id.left);
		rightBtn = (Button)findViewById(R.id.right);
		playBtn = (Button)findViewById(R.id.play);
		quitBtn = (Button)findViewById(R.id.quit);		
		left_clickBtn = (Button)findViewById(R.id.left_click);
		right_clickBtn = (Button)findViewById(R.id.right_click);
		
//		devBtn.setOnClickListener(new button_listener());
		leftBtn.setOnClickListener(new button_listener());
		rightBtn.setOnClickListener(new button_listener());
		quitBtn.setOnClickListener(new button_listener());
		playBtn.setOnClickListener(new button_listener());
		right_clickBtn.setOnClickListener(new button_listener());
		left_clickBtn.setOnClickListener(new button_listener());
		
		myMenu = new MenuInflater(this);
	}

	/**button click listener */
	class button_listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			vibrator();
			
			int id = v.getId();
			switch(id){
//			case R.id.devBtn:
//				connectToDevice();
//				break;
			case R.id.left:
				sendData(Key_Events.VK_LEFT);
				break;
			case R.id.right:
				sendData(Key_Events.VK_RIGHT);
				break;
			case R.id.play:
				int[] array = {Key_Events.VK_SHIFT,Key_Events.VK_F5};
				sendData(array,array.length);
				break;
			case R.id.quit:
				sendData(Key_Events.VK_ESCAPE);
				break;
			case R.id.left_click:
				sendData(Key_Events.BUTTON1_MASK);
				break;
			case R.id.right_click:
				sendData(Key_Events.BUTTON3_MASK);
				break;
			default:
				break;
			}	
		}
		
	}
	
	
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		//显示关于对话框
		case R.id.select_dev:
			connectToDevice();
			break;
		}
		return true;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		myMenu.inflate(R.xml.menu, menu);
		return true;
	}
	
	/**
	 * 蓝牙开始 查询手机是否支持蓝牙，如果支持的话，进行下一步。 查看蓝牙设备是否已打开，如果否则打开。
	 */
	public void bluetoothAction() {
		// 查看手机是否有蓝牙设备功能
		if (hasAdapter(bluetoothAdapter)) {
			if (!bluetoothAdapter.isEnabled()) {
				// 开启蓝牙功能
				bluetoothAdapter.enable();
			}
		} else {
			// 程序终止
			this.finish();
		}
	}

	/**
	 * 查看手机是否有蓝牙设备功能
	 * 
	 * @param ba
	 *            蓝牙设备适配器
	 * @return boolean
	 */
	public boolean hasAdapter(BluetoothAdapter ba) {
		if (ba != null) {
			return true;
		}
		displayLongToast("该手机没有蓝牙功能！");
		return false;
	}

	/**
	 * 创建一个长时间弹出的提示窗口toast
	 * 
	 * @param str
	 *            提示字符串
	 */
	public void displayLongToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_LONG);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}
	
	/**
	 * 创建一个短时间弹出的提示窗口toast
	 * 
	 * @param str
	 *            提示字符串
	 */
	public void displayShortToast(String str) {
		Toast toast = Toast.makeText(this, str, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.TOP, 0, 220);
		toast.show();
	}

	/**
	 * 蓝牙若启动，则查询附近的所有蓝牙设备进行选择连接
	 */
	public void connectToDevice() {
		if (bluetoothAdapter.isEnabled()) {
			// 跳到另一个activity---DiscoveryActivity，该类用于查询附近所有的蓝牙设备。
			Intent intent = new Intent(this, DiscoveryActivity.class);
			// 弹出窗口提示
			displayLongToast("请选择一个蓝牙设备进行连接！");

			// 手机此时跳进DiscoveryActivity程序界面。
			// 注意：利用startActivityForResult回调数据返回当前的程序。
			// 详细参考：http://snmoney.blog.163.com/blog/static/440058201073025132670/
			this.startActivityForResult(intent, REQUEST_DISCOVERY);
		} else {
			this.finish();
		}
	}

	/**
	 * startActivityForResult触发调用DiscoveryActivity后进行处理
	 * 获取到相应的蓝牙地址数据后，开始我们核心的数据交互
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		// super.onActivityResult(requestCode, resultCode, data);

		// 这里确保相互回调时数据的准确传输
		if (requestCode != REQUEST_DISCOVERY) {
			return;
		}
		if (resultCode != RESULT_OK) {
			return;
		}
//		devBtn.setVisibility(View.INVISIBLE);
		
		// 获取到DiscoveryActivity点击项后传过来的蓝牙设备地址
		String addressStr = data.getStringExtra("address");
		// 根据蓝牙设备地址得到该蓝牙设备对象（这是扫描到的蓝牙设备哦，不是自己的）
		device = bluetoothAdapter.getRemoteDevice(addressStr);
		try {
			//根据UUID创建通信套接字
			btSocket = device.createRfcommSocketToServiceRecord(uuid);
		} catch (Exception e) {
			displayLongToast("通信通道创建失败！");
		}

		if (btSocket != null) {
			try {
				//这一步一定要确保连接上，不然的话程序就卡死在这里了。
				btSocket.connect();
				displayLongToast("通信通道连接成功！");
			} catch (IOException ioe) {
				displayLongToast("通信通道连接失败！");
				try {
					btSocket.close();
					displayLongToast("通信通道已关闭！");
				} catch (IOException ioe2) {
					displayLongToast("通信通道尚未连接，无法关闭！");
				}
			} 
			try {
				// 获取输出流
				outStream = btSocket.getOutputStream();
				// 手机发出数据
			//	sendSensorData();
			} catch (IOException e) {
				displayLongToast("数据流创建失败！");
			} 
		}
	}

	/** send the single key data */
	public void sendData(int key){
		if(btSocket == null || outStream == null)
			return ;
		try {
			outStream.write((key+"#").getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayShortToast("数据发送失败！");
		}
	}
	
	/******* send the compose keys data ****/
	public void sendData(int key[] , int len){
		String tmp="";
		for(int i=0; i<len; i++)
			tmp+=key[i]+"#";
		
		if(btSocket == null || outStream == null)
			return ;
		try {
			outStream.write(tmp.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			displayShortToast("数据发送失败！");
		}	
	}

	
	/**
	 * 发送数据 发出从手机通过重力感应器获取到的数据
	 */
	public void sendSensorData() {
		
		// 重力感应监听
		SensorEventListener lsn = new SensorEventListener() {
			// 重写内部方法，当精确度发生变化是触发该方法。
			@Override
			public void onAccuracyChanged(Sensor s, int accuracy) {
				// TODO Auto-generated method stub
			}

			// 重写内部方法，当数据发生变化的时候触发该方法。
			@Override
			public void onSensorChanged(SensorEvent se) {
				// TODO Auto-generated method stub
				/**
				 * 当手机横向头部朝左屏幕正对自己时 x=10,y=0,z=0; 当手机竖向屏幕正对自己时 x=0,y=10,z=0;
				 * 当手机平放屏幕朝上时 x=0,y=0,z=10; 由此可知：当手握手机且竖向屏幕正对自己时，有： 水平就是X轴
				 * 垂直就是Y轴 屏幕所对方向便是Z轴 具体可参考简单例子---SensorDemo
				 */
				x = (int)se.values[SensorManager.DATA_X];
				y = (int)se.values[SensorManager.DATA_Y];
				z = (int)se.values[SensorManager.DATA_Z];
				if ((y > 5 || y < -5 ) && z > 0) {
//					String str = String.valueOf(x).concat(String.valueOf(y)).concat(String.valueOf(z));
					String str = "x" + String.valueOf(x) + "y" + String.valueOf(y) + "z" + String.valueOf(z) + "/";
					//String str = "hello";
					msgBuffer = str.getBytes();
					try {
						//System.out.println("x=" + x + " y =" + y + " z =" + z);
						outStream.write(msgBuffer);
					} catch (IOException e) {
						displayShortToast("数据发送失败！");
					}
				}
				
//				if (y > 5 || y < -5) {
//					DataModel dataModel=new DataModel(x,y,z);
//					try {
//						System.out.println("x=" + x + " y =" + y + " z =" + z);
//						msgBuffer = dataModel.convertSelfToByteArray();
//						System.out.println("--------"+msgBuffer.length);
//						outStream.write(msgBuffer);
//					} catch (IOException e) {
//						Log.e("BlueTooth",e.getMessage());
//						e.printStackTrace();
//						displayShortToast("数据发送失败！");
//					}
//				}
			}
		};
		

		// 别忘了注册重力感应器，由于android的一些东东是又很大一部分都要这么干的。
		// 所有要注意。比如蓝牙这块，在对它打开的时候其实你也要注册权限的。看AndroidManifest.xml文件。
		sensorMgr
				.registerListener(lsn, sensor, SensorManager.SENSOR_DELAY_GAME);
	}

	
    /*
     * 手机震动
     */
    public void vibrator(){
    	Vibrator vibrator = (Vibrator)getSystemService(VIBRATOR_SERVICE);
    	vibrator.vibrate(100);
    }
	
	
	
	// ////////////////////以下是退出程序的一些操作，不关核心功能的事/////////////////////////////////
	/**
	 * 重写方法：点击返回键，确认是否退出程序。
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Builder alertDialog = new AlertDialog.Builder(this);
			// 设置弹出框的图标
			alertDialog.setIcon(R.drawable.quit);
			// 设置弹出框的title
			alertDialog.setTitle(R.string.prompt);
			// 设置弹出框的提示信息
			alertDialog.setMessage(R.string.quit_msg);
			// 设置弹出框确认键触发事件
			alertDialog.setPositiveButton(R.string.confirm,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog,
								int whichButton) {
							// TODO Auto-generated method stub
							try {
								outStream.write("QUIT".getBytes());
								btSocket.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							finish(); 
						}
					});
			// 设置弹出框取消键触发事件（不做任何操作）
			alertDialog.setNegativeButton(R.string.cancel,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
						}
					});
			// 显示弹出框
			alertDialog.show();
			return true;
		} else {
			// 如果点击的不是返回键按钮，那么该做什么操作就做什么操作。
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 重写方法：销毁线程，退出系统。
	 */
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		System.exit(0);
	}





}