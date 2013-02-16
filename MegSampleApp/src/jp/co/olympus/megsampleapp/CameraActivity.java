package jp.co.olympus.megsampleapp;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class CameraActivity extends Activity {
	private Button transButton ;
	private TextView message;
//	private Handler guiThread;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        transButton = (Button)this.findViewById(R.id.trans);
        message = (TextView)this.findViewById(R.id.message);
//        guiThread = new Handler();
        // ボタンのトリガー設定(後で1秒おきに変更) 
        // TODO
        transButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TransTask task = new TransTask();
				Future<String> future = Executors.newSingleThreadExecutor().submit(task);
				try {
					String transMessage;
					transMessage = future.get();
					message.setText(transMessage);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    };
/*	
	private void guiSetText(final TextView view, final String text){
		guiThread.post(new Runnable(){
			public void run() {
				// Megへの通信はここでやる
				// TODO
				view.stText(text);
			}			
		});
	}
*/
}
