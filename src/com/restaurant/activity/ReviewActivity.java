package com.restaurant.activity;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.restaurant.util.HttpUtil;
import com.restaurant.util.RatingbarAdapter;

public class ReviewActivity extends Activity {
	private int foodid = 0;
	private TheApplication app;
	private ListView list;
	private TextView tdishname;
	private String dishname = "";
	private Handler mainHandler;

	private SimpleAdapter listItemAdapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_review);
		app = (TheApplication) getApplication();
		Intent intent = this.getIntent();
		Bundle bundle = intent.getExtras();
		foodid = bundle.getInt("foodid");
		dishname = bundle.getString("dishname");
		list = (ListView) findViewById(R.id.listView1);
		tdishname = (TextView) findViewById(R.id.textView1);
		tdishname.setText(dishname);
		

		new Thread(progressThread).start();
		
		this.mainHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// findViewById(R.id.progressBar1).setVisibility(View.GONE);
				if (msg.what == -321) {
					listItemAdapter = new RatingbarAdapter(
							ReviewActivity.this,
							(ArrayList<HashMap<String, Object>>) msg.obj,// 数据源
							R.layout.comment_list_layout,// ListItem的XML实现
							// 动态数组与ImageItem对应的子项
							new String[] { "customername", "time", "detail",
									"stars" },
							// ImageItem的XML文件里面的一个ImageView,两个TextView ID
							new int[] { R.id.textView1, R.id.time,
									R.id.textView4, R.id.rating }

					);

					// 添加并且显示
					list.setAdapter(listItemAdapter);
				}else{
					Toast.makeText(ReviewActivity.this, "此菜品暂无评价信息",Toast.LENGTH_SHORT).show();
					
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
			String result = getResult(foodid);
			
			Object flag = null;
			try {
				JSONObject json = new JSONObject(result);
				
				msg.what = -321;
				
				ArrayList<HashMap<String, Object>> listItem = new ArrayList<HashMap<String, Object>>();
				JSONArray jsonArray = json.getJSONArray("comment");
				for (int i = 0; i < jsonArray.length(); i++) {
					HashMap<String, Object> map = new HashMap<String, Object>();
					JSONObject comment = (JSONObject) jsonArray.get(i);
					String customername = comment.getString("customername");
					String time = comment.getString("time");
					String detail = comment.getString("detail");
					double stars = comment.getDouble("stars");
					float star = (float) stars;
					map.put("customername", customername);
					map.put("detail", detail);
					map.put("stars", star);
					map.put("time", time);
					map.put("index", i);
					listItem.add(map);
				}
				flag = listItem;
			

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.what=-123;
			}
			if (msg.what == -321) {

				msg.obj = flag;
			}
			mainHandler.sendMessage(msg);
		}
	};

	private String getResult(int id) {
		// 查询参数

		String registerString = "foodid=" + id;
		// URL
		String url = HttpUtil.BASE_URL + "GetReviewServlet?" + registerString;
		// 查询返回结果
		String result = HttpUtil.queryStringForPost(url);

		return result;
	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.review, menu);
		return true;
	}

}
