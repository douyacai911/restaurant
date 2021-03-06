package com.restaurant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.restaurant.util.HttpUtil;

public class MyMenuActivity extends Activity {
	public static MyMenuActivity instance = null;
	private int restid = 0;
	private TextView textview;
	private Handler mainHandler;
	private SimpleAdapter listItemAdapter;
	private ListView list;
	private TheApplication app;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_menu);
		app = (TheApplication) getApplication(); 
		instance = this; //指定关闭用
//		Intent intent = this.getIntent();
//		Bundle bundle = intent.getExtras();
//		restid = bundle.getInt("restid");
		restid = app.getRestid();
		list = (ListView) findViewById(R.id.ListView01);
		setTitle("我的菜单");
		new Thread(progressThread).start();

		// 添加点击
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				
				HashMap<String, Object> thisfood = (HashMap<String, Object>) arg0.getItemAtPosition(arg2);
				int thisfoodid = (Integer) thisfood.get("foodid");
				Intent intent = new Intent().setClass(MyMenuActivity.this, EditDishActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("foodid", thisfoodid);
				bundle.putString("dishname", (String)thisfood.get("dishname"));
				bundle.putDouble("price", (Double)thisfood.get("price"));
				bundle.putInt("categoryid", (Integer)thisfood.get("categoryid"));
				bundle.putString("description", (String)thisfood.get("description"));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});

		findViewById(R.id.Button1).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent().setClass(
								MyMenuActivity.this, AddDishActivity.class);
						Bundle bundle = new Bundle();
						bundle.putInt("restid", restid);
						intent.putExtras(bundle);
						startActivity(intent);

					}
				});
		findViewById(R.id.button2).setOnClickListener(
				new View.OnClickListener() {
					@Override
					public void onClick(View view) {
						Intent intent = new Intent().setClass(
								MyMenuActivity.this, OrderListActivity.class);
//						Bundle bundle = new Bundle();
//						bundle.putInt("restid", restid);
//						intent.putExtras(bundle);
						startActivity(intent);

					}
				});
		this.mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// findViewById(R.id.progressBar1).setVisibility(View.GONE);
				if(msg.what==-123){
					Toast.makeText(MyMenuActivity.this, "现无菜品",Toast.LENGTH_SHORT).show();
				}else{
				listItemAdapter = new SimpleAdapter(MyMenuActivity.this,
						(ArrayList<HashMap<String, Object>>) msg.obj,// 数据源
						R.layout.menu_list_layout,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "dishname", "price" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.DishName, R.id.DishPrice }

				);

				// 添加并且显示
				list.setAdapter(listItemAdapter);
				}

				super.handleMessage(msg);
			}

		};
	}

	Runnable progressThread = new Runnable() {
		@Override
		public void run() {
			Message msg = new Message();
			new Thread();
			Object flag = jsonToMenu(restid);
			if(flag==null){
				msg.what = -123;
			}else{
				msg.obj = flag;
			}
			mainHandler.sendMessage(msg);
		}
	};

	private String goRegister(int id) {
		// 查询参数

		String registerString = "restid=" + id;
		// URL
		String url = HttpUtil.BASE_URL + "MyMenuServlet?" + registerString;
		// 查询返回结果
		return HttpUtil.queryStringForPost(url);
	}

	private ArrayList<HashMap<String, Object>> jsonToMenu(int id) {
		String jsonString = goRegister(id);
		if(jsonString.equals("-1")){
			return null;
		}
		ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
		try {
			JSONObject json = new JSONObject(jsonString);
			JSONArray jsonArray = json.getJSONArray("menu");

			for (int i = 0; i < jsonArray.length(); i++) {
				HashMap<String, Object> map = new HashMap<String, Object>();
				// map.put("ItemTitle", "Level "+i);
				// map.put("ItemText", "Finished in 1 Min 54 Secs, 70 Moves! ");
				// listItem.add(map);
				JSONObject dish = (JSONObject) jsonArray.get(i);
				int foodid = dish.getInt("foodid");
				String dishname = dish.getString("dishname");
				double price = dish.getDouble("price");
				int categoryid = dish.getInt("categoryid");
				String description = dish.getString("description");
				map.put("foodid", foodid);
				map.put("dishname", dishname);
				map.put("price", price);
				map.put("categoryid", categoryid);
				map.put("description", description);
				map.put("index", i);
				listItem.add(map);
			}
			return listItem;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.my_menu, menu);
		return true;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        switch(item.getItemId())//得到被点击的item的itemId
        {
        case R.id.action_settings://这里的Id就是布局文件中定义的Id，在用R.id.XXX的方法获取出来
        	Intent intent = new Intent().setClass(MyMenuActivity.this, EditInfoActivity.class);
			startActivity(intent);
            break;
        case R.id.item1:
        	Intent intent2 = new Intent().setClass(MyMenuActivity.this, BeginActivity.class);
//        	Bundle bundle = new Bundle();
//			bundle.putBoolean("isEdit", true);
//			intent2.putExtras(bundle);
			startActivity(intent2);
			break;
        }
        return true;
    }
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        
        if(keyCode==KeyEvent.KEYCODE_BACK){
        	final AlertDialog.Builder builder = new Builder(
					MyMenuActivity.this);
			builder.setTitle("确认退出")
					.setMessage("确定要退出程序吗？")
					.setPositiveButton(
							"确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface,
										int i) {
									// 按钮事件
									  Intent exit = new Intent(Intent.ACTION_MAIN);
					                    exit.addCategory(Intent.CATEGORY_HOME);
					                    exit.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					                    startActivity(exit);
					                    System.exit(0);

								}
							})
					.setNegativeButton(
							"取消",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(
										DialogInterface dialog,
										int which) {
									dialog.dismiss();
								}
							}).show(); //弹出确定退出对话框
          
            //不需要执行父类的点击事件，直接return
            return true;
        }  //继续执行父类的其他点击事件
        return super.onKeyDown(keyCode, event);
    }
}
