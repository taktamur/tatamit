package jp.co.olympus.megsampleapp;

import java.util.Date;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import jp.co.olympus.meg40.*;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class CameraActivity extends Activity implements MegListener {
	private Button transButton ;
	private TextView message;
//	private Handler guiThread;
	private Meg mMeg; //MEGへのコマンド送信を行うインスタンス
	private MegGraphics mMegGraphics; // グラフィック描画用
	private MegListener originalListener;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        transButton = (Button)this.findViewById(R.id.trans);
        message = (TextView)this.findViewById(R.id.message);
        
        // 最初のgetInstance呼び出しではインスタンス生成時に例外が投げられることがある
        try {
			mMeg = Meg.getInstance();
		} catch (BluetoothNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BluetoothNotEnabledException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// MEGのイベント監視のハンドラを登録
        // このActivityが終わるとき、originalListenerに戻す
        originalListener = mMeg.registerMegListener(this);
        // MEGのグラフィックス機能を使うクラスの生成
        mMegGraphics = new MegGraphics(mMeg);

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
		    		mMegGraphics.begin();
		    		mMegGraphics.drawString(100, 50,transMessage); // (100, 50)の位置に描画
		    		mMegGraphics.end();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
    }

	@Override
	public void onStop(){
		// 終了時にはオリジナルに戻す
		mMeg.registerMegListener(originalListener);
	}
	
	@Override
	public void onMegAccelChanged(int arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegConnectionFailed() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegDeleteImage(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegDirectionChanged(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegGraphicsCommandEnd(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegGraphicsCommandStart(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegKeyPush(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegSetContext(int arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegSleep() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegStatusChanged(MegStatus arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onMegVoltageLow() {
		// TODO Auto-generated method stub
		
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
