package com.jasoncong.bluetooth;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import android.app.ListActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.SimpleAdapter;

/**
 * 该类集成ListActivity,主要是扫描并显示出附近所有的蓝牙设备 结果返回给BlueTooth
 * 
 */
public class DiscoveryActivity extends ListActivity {

	// 获取手机默认上的蓝牙适配器
	private BluetoothAdapter blueToothAdapter = BluetoothAdapter
			.getDefaultAdapter();

	// 把每一个HashMap键值对的蓝牙设备信息存放到list数组中并按文件布局风格的方式呈现出来
	private ArrayList<HashMap<String, String>> list = null;
	// 用于真正存放所有扫描到的蓝牙设备的list
	private List<BluetoothDevice> _devices = new ArrayList<BluetoothDevice>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		/* 使程序窗口全屏 */
		// 创建一个没有title的全屏主题
		this.setTheme(android.R.style.Theme_NoTitleBar_Fullscreen);
		// 窗口全屏
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// 设置全屏标志
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// 按discovery.xml文件布局风格
		setContentView(R.layout.discovery);

		list = new ArrayList<HashMap<String, String>>();

		// 把扫描都的每一个蓝牙设备放到list中，并呈现给客户端
		showDevices();
	}

	/**
	 * 把扫描都的每一个蓝牙设备放到list中，并呈现给客户端。
	 */
	public void showDevices() {
		// 获取所有已配对的蓝牙设备
		Set<BluetoothDevice> devices = blueToothAdapter.getBondedDevices();

		if (devices.size() > 0) {
			Iterator<BluetoothDevice> it = devices.iterator();
			BluetoothDevice bluetoothDevice = null;
			HashMap<String, String> map = new HashMap<String, String>();
			while (it.hasNext()) {
				bluetoothDevice = it.next();
				// 把每一个获取到的蓝牙设备的名称和地址存放到HashMap数组中，比如：xx:xx:xx:xx:xx: royal
				map.put("address", bluetoothDevice.getAddress());
				map.put("name", bluetoothDevice.getName());
				// 该list用于存放呈现的蓝牙设备，存放的是每个设备的map
				list.add(map);
				// 该list用于存放的是真正的每一个蓝牙设备对象
				_devices.add(bluetoothDevice);
			}

			// 构造一个简单的自定义布局风格，各个参数都有明确的相对应。具体给google一下SimpleAdapter和参考一些文献
			SimpleAdapter listAdapter = new SimpleAdapter(this, list,
					R.layout.device, new String[] { "address", "name" },
					new int[] { R.id.address, R.id.name });
			this.setListAdapter(listAdapter);
		}
	}

	/**
	 * list点击项触发事件 当设备扫描显示完成后，可选择点击相应的设备进行连接。
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		Intent result = new Intent();
		String addressStr = _devices.get(position).getAddress();
		//地址只取到17位，虽然addressStr和address都一样 xx:xx:xx:xx:xx:xx
		String address = addressStr.substring(addressStr.length() - 17);
		
		result.putExtra("address", address);
		// 这个就是回传数据了，将地址传回给BlueTooth---activity
		// 这里的resultCode是RESULT_OK，BlueTooth---activity方法onActivityResult里对应的resultCode也应该是RESULT_OK
		//只有resultCode值相匹配，才能确保result数据回调不出错。
		setResult(RESULT_OK, result);
		// 一定要finish，只有finish后才能将数据传给BlueTooth---activity
		// 并在onActivityResult做处理
		finish();
	}

}

