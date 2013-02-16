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
	private String transMessage;

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
				updateMegMessage();
			}
		});
    }

	@Override
	public void onDestroy(){
		super.onDestroy();
		// 終了時にはオリジナルに戻す
    	try {
			Meg.getInstance().registerMegListener(originalListener);
		} catch (BluetoothNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (BluetoothNotEnabledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		updateMegMessage();

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
		
	}

	private void updateMegMessage() {
		TransTask task = new TransTask();
		Future<String> future = Executors.newSingleThreadExecutor().submit(task);
		try {
			transMessage = future.get();
			message.setText(transMessage);
			
			
			//色定義
			final int RED 		= 0xffff0000;
			final int GREEN 	= 0xff00ff00;
			final int BLUE 		= 0xff0000ff;
			final int YELLOW 	= 0xffffff00;
			final int MAGENTA 	= 0xffff00ff;
			final int CYAN 		= 0xff00ffff;
			final int WHITE 	= 0xffffffff;
			final int GRAY      = 0xffcccccc;

			//フォントサイズ定義
			final int FONT_HUGE 	= 100;
			final int FONT_LARGE 	= 80;
			final int FONT_MIDDLE 	= 60;
			final int FONT_SMALL 	= 40;
			final int FONT_TINY 	= 20;
			
			//移動量（ピクセル）
			final int SPEED_SLOW = 2;
			final int SPEED_MIDDLE = 6;
			final int SPEED_FAST = 10;

			mMegGraphics.begin();
			
			int[] textIDs = { 0xffff, }; // 0xffffで全削除
			mMegGraphics.scrollStartStop(1);
			mMegGraphics.removeText(textIDs); // 登録したテキストを全削除、registerScrollで登録したものも削除される
					    		
			int[] sizes4 	= { FONT_LARGE, };
			int[] colors4 	= { RED, }; 
			String[] texts4 	= { transMessage , };
			final int startX4 = 0;
			final int startY4 = 150;

			mMegGraphics.clearScreen();
			//文字列登録
			mMegGraphics.registerText(0, true, sizes4, colors4, texts4);
			//スクロール設定、スクロール開始
			mMegGraphics.registerScroll(0, startX4, startY4, SPEED_MIDDLE, 50, 2000, 10);
			
			mMegGraphics.end();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
