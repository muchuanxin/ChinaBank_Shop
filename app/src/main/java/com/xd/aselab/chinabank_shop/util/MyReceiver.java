package com.xd.aselab.chinabank_shop.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;


import com.xd.aselab.chinabank_shop.activity.CardDiv.CardDiv_My_Recommend_List;
import com.xd.aselab.chinabank_shop.activity.CardDiv.MainActivity;
import com.xd.aselab.chinabank_shop.chat.ui.ChatActivity;
import com.xd.aselab.chinabank_shop.chat.ui.GroupChatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;

import cn.jpush.android.api.JPushInterface;

/**
 * 自定义接收器
 * 
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
	private static final String TAG = "www";
    private String myExtra_str;
	private String type,groupID;
	private SharePreferenceUtil spu;
	private Handler handler;
	private Set<String> groupSet_add = new HashSet<String>() ;
	private Set<String> groupSet_exit = new HashSet<String>() ;

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(TAG,"MyReceiver----onReceive");
		try {
			Bundle bundle = intent.getExtras();
			Log.d(TAG, "[MyReceiver] onReceive - " + intent.getAction() + ", extras: " + printBundle(bundle));

			if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
				String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
				Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
				//send the Registration Id to your server...

			} else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));
				processCustomMessage(context, bundle);

			} else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
				int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

				String s = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的EXTRA_EXTRA: " + s);
				JSONObject obj = new JSONObject(s);
				String info = obj.getString("extra");
				JSONObject json_extra = new JSONObject(info);
				try {
					type = json_extra.getString("message_type");
					groupID = json_extra.getString("group_id");
					Log.d(TAG, "NOTIFICATION_type---"+type);
				}catch(JSONException e){

				}
				if(Constants.CreateChatGroup.equals(type)||Constants.InviteMemberToGroup.equals(type)){
					Log.d(TAG, "新加进去的群---"+groupID);
					groupSet_add.add(groupID);
					JPushInterface.addTags(context,new Random().nextInt(),groupSet_add);
				}else if(Constants.DissolveChatGroup.equals(type)||Constants.ExpelMemberFromGroup.equals(type)){
					Log.d(TAG, "后退的群---"+groupID);
					groupSet_exit.add(groupID);
					JPushInterface.deleteTags(context,new Random().nextInt(),groupSet_exit);
				}


			} else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 用户点击打开了通知");
				String s = bundle.getString(JPushInterface.EXTRA_EXTRA);
				Log.d(TAG, "JPushInterface.EXTRA_EXTRA---"+s);
				JSONObject obj = new JSONObject(s);
				String info = obj.getString("extra");
				JSONObject json_extra = new JSONObject(info);
				try {
					type = json_extra.getString("message_type");
					Log.d(TAG, "message_type---"+type);
				}catch(JSONException e){

				}
				// 在这里可以自己写代码去定义用户点击后的行为
				if(Constants.ConfirmInstallmentRecommend.equals(type)){
					Intent i = new Intent(context, CardDiv_My_Recommend_List.class);  //自定义打开推荐分期界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("scope","one_week");
					context.startActivity(i);
				}else if(Constants.CHAT.equals(type)){
					Log.e(TAG,"Constants.CHAT");
					Intent i = new Intent(context, ChatActivity.class);  //自定义打开单聊界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("receiver",json_extra.getString("sender"));
					i.putExtra("receiver_name",json_extra.getString("sender_name"));
					i.putExtra("receiver_head",json_extra.getString("sender_head"));
					//i.putExtras(bundle2);
					context.startActivity(i);
				}else if(Constants.GROUPCHAT.equals(type)){
					Log.e(TAG,"Constants.GROUPCHAT");
					Intent i = new Intent(context, GroupChatActivity.class);  //自定义打开群聊界面
					i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TOP);
					i.putExtra("group_id",json_extra.getString("group_id"));
					i.putExtra("group_name",json_extra.getString("group_name"));
					i.putExtra("group_head",json_extra.getString("group_head"));
					Log.e("www","group_name:---"+json_extra.getString("group_name"));
					//i.putExtras(bundle2);
					context.startActivity(i);
				}


			} else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
				Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
				//在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

			} else if(JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
				boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
				Log.d(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
			} else {
				Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
			}
		} catch (Exception e){

		}

	}

	// 打印所有的 intent extra 数据
	private static String printBundle(Bundle bundle) {
		StringBuilder sb = new StringBuilder();
		for (String key : bundle.keySet()) {
			if (key.equals(JPushInterface.EXTRA_NOTIFICATION_ID)) {
				sb.append("\nkey:" + key + ", value:" + bundle.getInt(key));
			}else if(key.equals(JPushInterface.EXTRA_CONNECTION_CHANGE)){
				sb.append("\nkey:" + key + ", value:" + bundle.getBoolean(key));
			} else if (key.equals(JPushInterface.EXTRA_EXTRA)) {
				if (TextUtils.isEmpty(bundle.getString(JPushInterface.EXTRA_EXTRA))) {
					Log.i(TAG, "This message has no Extra data");
					continue;
				}

				try {
					JSONObject json = new JSONObject(bundle.getString(JPushInterface.EXTRA_EXTRA));
					Iterator<String> it =  json.keys();

					while (it.hasNext()) {
						String myKey = it.next();
						sb.append("\nkey:" + key + ", value: [" +
								myKey + " - " +json.optString(myKey) + "]");
					}
				} catch (JSONException e) {
					Log.d(TAG, "Get message extra JSON error!");
				}

			} else {
				sb.append("\nkey:" + key + ", value:" + bundle.getString(key));
			}
		}
		return sb.toString();
	}
	
	//send msg to MainActivity、NewNotificationDetailActivity
	private void processCustomMessage(Context context, Bundle bundle) {
		String message = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		String extras = bundle.getString(JPushInterface.EXTRA_EXTRA);

		if (MainActivity.isForeground) {
			Intent msgIntent = new Intent(MainActivity.MESSAGE_RECEIVED_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			if (extras!=null) {
				try {
					JSONObject extraJson = new JSONObject(extras);
					if (extraJson.length() > 0) {
						Log.d(TAG,"-----------send msg to NewNotificationDetailActivity-----------");
						//将附加消息发送到首页，首页存至arrow_down_hui.PNG,在新通知界面从arrow_down_hui.PNG里取出
						msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
						Log.d(TAG,"-------130------"+extras.replace("\\",""));
					}
				} catch (JSONException e) {

				}

			}
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}
		else if(ChatActivity.isForeground){
			Log.e("www", "商铺版-聊天页面在前台-单聊");
			Intent msgIntent = new Intent(MainActivity.MESSAGE_UPDATE_MESSAGCHAT_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
			context.sendBroadcast(msgIntent);
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}else if(GroupChatActivity.isForeground){
			Log.e("www", "商铺版-聊天页面在前台-群聊");
			Intent msgIntent = new Intent(MainActivity.GROUP_MESSAGE_UPDATE_MESSAGCHAT_ACTION);
			msgIntent.putExtra(MainActivity.KEY_MESSAGE, message);
			msgIntent.putExtra(MainActivity.KEY_EXTRAS, extras);
			context.sendBroadcast(msgIntent);
			LocalBroadcastManager.getInstance(context).sendBroadcast(msgIntent);
		}

		else{
			Log.e("www", "没有需要通知的界面在前台");
		}

	}
}
