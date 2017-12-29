package com.example.mymaptest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * 监听短信广播，数据交互采用借口回调的方式传递
 */
public class SmsReceive extends BroadcastReceiver
{

	private BRInteraction brInteraction;
	private String address;
	private String messageBody;
	private String[] con;
	@Override
	public void onReceive(Context context, Intent intent)//短信到来监听
	{

		Object[] objects = (Object[]) intent.getExtras().get("pdus");//固定写法
		for (Object obj : objects)
		{
			SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) obj);
			messageBody = smsMessage.getMessageBody();
			//con = s.spl(messageBody);
			address = smsMessage.getOriginatingAddress();
			Log.i("短信", "onReceive:"+ messageBody + "-----" + address);
			System.out.println(messageBody + "-----" + address);
		}
			brInteraction.setText(messageBody);
		
	}
//	接口回调
	public interface BRInteraction
	{
		public void setText(String content);
	}

	public void setBRInteractionListener(BRInteraction brInteraction)
	{
		this.brInteraction = brInteraction;
	}
}