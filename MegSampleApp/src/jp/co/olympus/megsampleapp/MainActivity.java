package jp.co.olympus.megsampleapp;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegStatus;
import jp.co.olympus.meg40.MegListener;
import jp.co.olympus.meg40.MegGraphics;
import jp.co.olympus.meg40.BluetoothNotFoundException;
import jp.co.olympus.meg40.BluetoothNotEnabledException;

public class MainActivity extends Activity implements OnItemClickListener, MegListener {

	private Meg mMeg; //MEGへのコマンド送信を行うインスタンス
	private MegGraphics mMegGraphics; // グラフィック描画用
	
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1; // MEGへの接続要求
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int SENSOR_STOP = 3; // センサー値表示のアクティビティが閉じられた
    
    private int mScrollIndex = 0; // テキストスクロール用
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ListViewを作る
        String[] items = new String[]
        		{
        		"0. 接続", 
        		"1. 切断", 
        		"2. ステータス取得", 
        		"3. クリアカラー設定", 
        		"4. 画面クリア", 
        		"5. フォントサイズ指定", 
        		"6. フォント表示色設定", 
        		"7. 文字列描画1", 
        		"8. 文字列描画2", 
        		"9. 画像登録1(11.で描画)", 
        		"10. 画像登録2(13.で描画)", 
        		"11. 画像描画1", 
        		"12. 画像描画2", 
        		"13. 画像描画3", 
        		"14. 加速度センサー値取得",
        		"15. 方位センサー値取得",
        		"16. 自動スリープ設定",
        		"17. テキストスクロール",
        		"18. スクロール終了",
        		};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter);
        // リストビューのアイテムがクリックされた時に呼び出されるコールバックリスナーを登録します
        listview.setOnItemClickListener(this); // onItemClickで処理
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }    

    /**
     *  ListViewのアイテムをクリックした
     */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{
    	if (position == 0) // 接続
    	{
            //Bluetooth接続できるかどうかチェックする
        	//接続できなければ、アプリを終了
    		if (mMeg == null)
    		{
	        	try {
	        		// MEGはシングルトンパターン
	        		// 最初のgetInstance呼び出しではインスタンス生成時に例外が投げられることがある
	    			mMeg = Meg.getInstance();
	        		// MEGのイベント監視のハンドラを登録
	            	mMeg.registerMegListener(this);

	                // MEGのグラフィックス機能を使うクラスの生成
	                mMegGraphics = new MegGraphics(mMeg);
	    		} catch (BluetoothNotFoundException e) {
	    			Toast.makeText(this, "Bluetoothアダプターが見つかりません", Toast.LENGTH_LONG).show();
	    			finish();
	    			return;
	    		} catch (BluetoothNotEnabledException e) {
	    			Toast.makeText(this, "Bluetoothが無効になっています\n有効にしてください", Toast.LENGTH_LONG).show();
	    			finish();
	    			return;
	    		}
    		}
    		// mMegは非null
    		if (mMeg.isConnected())
    		{
    			// 接続済み
    		}
    		else // 未接続
    		{
	            //Bluetooth接続できるペアリング済みデバイスのリストを表示するアクティビティ（ダイアログ）を開始する。
	        	//アクティビティが終了したら、onActivityResult() に終了コードとして、REQUEST_CONNECT_DEVICEを返す。
	            //MEGへの接続はonActivityResult()で実行される。
	            Intent serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    		}
    		return;
    	}
		if (mMeg == null || !mMeg.isConnected())
		{
			Toast.makeText(this, "接続してください", Toast.LENGTH_LONG).show();
			return;
		}

		// mMegは非null、かつ、接続済み
    	if (position == 1) // 切断
    	{
    		mMeg.disconnect();
    		// MegListenerのonMegDisconnectedが呼び出される
    	}
    	else if (position == 2) // ステータス取得
    	{
    		mMeg.updateStatus(); // ステータス取得
    		// MegListenerのonMegStatusChangedが呼び出される
    	}
    	else if (position == 3) // クリアカラー設定
    	{
    		// クリアカラーを青に設定する
    		mMegGraphics.begin();
    		mMegGraphics.setClearColor(0xff0000ff); // 0xffRRGGBB, 青
    		mMegGraphics.end();
    	}
    	else if (position == 4) // クリア
    	{
    		// 設定されているクリアカラーで画面を消去する
    		mMegGraphics.begin();
    		mMegGraphics.clearScreen();
    		mMegGraphics.end();
    	}
    	else if (position == 5) // フォントサイズ指定
    	{
    		mMegGraphics.begin();
    		mMegGraphics.setFontSize(80); // 80ポイント
    		mMegGraphics.end();
    	}
    	else if (position == 6) // フォント表示色設定
    	{
    		mMegGraphics.begin();
    		mMegGraphics.setFontColor(0xff00ff00); // 0xffRRGGBB, 緑
    		mMegGraphics.end();
    	}
    	else if (position == 7) // 文字列描画1
    	{
    		mMegGraphics.begin();
    		mMegGraphics.drawString(100, 50, new String("abc かな漢字")); // (100, 50)の位置に描画
    		mMegGraphics.end();
    	}
    	else if (position == 8) // 文字列描画2
    	{
    		mMegGraphics.begin();
    		mMegGraphics.setFontSize(40);
    		mMegGraphics.setFontColor(0xff00ff00); // 0xffRRGGBB, 緑
    		mMegGraphics.drawString(-20, 20, new String("今日の天気 40pt")); // 負の値も可能
    		mMegGraphics.setFontSize(30);
    		mMegGraphics.setFontColor(0xffff0000); // 0xffRRGGBB, 赤
    		mMegGraphics.drawString(10, 60, new String("東京：晴れ 30pt"));
    		mMegGraphics.setFontSize(20);
    		mMegGraphics.setFontColor(0xffffff00); // 0xffRRGGBB, 黄色
    		mMegGraphics.drawString(250, 90, new String("20pt 気温10℃")); // 画面からはみ出すケース
    		mMegGraphics.end();
    	}
    	else if (position == 9) // 画像登録1
    	{
    		try
    		{
    			InputStream is = getResources().getAssets().open("landscape.jpg");
    			Bitmap bm = BitmapFactory.decodeStream(is);
    			
        		mMegGraphics.begin();
        		mMegGraphics.registerImage(1000, bm); // ID=1000に登録
        		mMegGraphics.end();
        		// 登録した画像は「画像描画1」で表示
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
    		// ---------------------------------------------------------------------------
    		// もう一つの画像登録、registerImageFromBytesの例
    		// registerImageFromBytesを使いJPEG画像を登録する場合、画像の幅高さによって、MEGが落ちる。
    		// registerImageFromBytesのJavadoc参照。
    		/*
    		try
    		{
    			// assetから全部読み出してOutputStreamに入れる
    			InputStream is = getResources().getAssets().open("catQVGA.jpg"); // QVGAは幅、高さとも16の倍数なのでok
				Bitmap bm = BitmapFactory.decodeStream(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
    			int format = 0; // 0はJPEG、1はPNG
				if (format == 0)
				{
					// 再圧縮
					bm.compress(Bitmap.CompressFormat.JPEG, 50, os);
				}
				else if (format == 1)
				{
					// PNGに変換
					bm.compress(Bitmap.CompressFormat.PNG, 50, os);
				}

        		mMegGraphics.begin();
        		mMegGraphics.registerImageFromBytes(2000, os.toByteArray(), format); // ID=2000に登録
        		mMegGraphics.drawImage(2000, 0, 0, new Rect(0, 0, bm.getWidth(), bm.getHeight())); // 全体に表示
        		mMegGraphics.end();
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
			*/
    	}
    	else if (position == 10) // 画像登録2
    	{
    		try
    		{
    			// 2枚を登録
        		// 登録した画像は「画像描画3」で表示
    			InputStream is = getResources().getAssets().open("cat.jpg");
    			Bitmap bm = BitmapFactory.decodeStream(is);
    			InputStream is2 = getResources().getAssets().open("flower.jpg");
    			Bitmap bm2 = BitmapFactory.decodeStream(is2);

    			mMegGraphics.begin();
        		mMegGraphics.registerImage(1001, bm); // すべて送信するまで返らない
        		mMegGraphics.registerImage(1002, bm2);
        		mMegGraphics.end();
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
    	}
    	else if (position == 11) // 画像描画1
    	{
    		// 事前に画像登録1を実行しておく。
    		mMegGraphics.begin();
    		mMegGraphics.drawImage(1000, 0, 0, new Rect(10, 30, 330, 270)); // 画像の(10, 30)-(330, 270)のQVGAサイズを切り出して描画
    		mMegGraphics.end();    			
    	}
    	else if (position == 12) // 画像描画2
    	{
    		// 既存の画面に何か描画してある状態で、そこに画像と文字を追加する場合の振る舞い
    		mMegGraphics.begin();
    		mMegGraphics.setClearColor(0xff00ffff); // 0xffRRGGBB, 水色
    		mMegGraphics.clearScreen();
    		mMegGraphics.setFontSize(40);
    		mMegGraphics.setFontColor(0xff000000); // 0xffRRGGBB, 黒
    		mMegGraphics.drawString(20, 40, new String("今日の天気"));    		
    		mMegGraphics.end();

    		// 「今日の天気」が描いてある画面に晴れの画像と文字列を追加する
    		try
    		{
	    		InputStream is = getResources().getAssets().open("hare.png");
				Bitmap bm = BitmapFactory.decodeStream(is);
    		
	    		mMegGraphics.begin();
	    		mMegGraphics.drawString(20, 80, new String("東京"));
        		mMegGraphics.registerImage(1003, bm);
	    		mMegGraphics.drawImage(1003, 100, 100, new Rect(0, 0, 64, 64));
	    		mMegGraphics.end();
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
    	}
    	else if (position == 13) // 画像描画3
    	{
    		// 事前に画像登録2を実行しておく。
    		mMegGraphics.begin();
    		mMegGraphics.clearScreen();
    		mMegGraphics.setFontColor(0xff00ff00); // 0xffRRGGBB, 緑
    		mMegGraphics.setFontSize(40);
    		mMegGraphics.drawString(20, 40, new String("文字の上に画像を描画")); // 画像より先に描いた文字は下のdrawImageで隠される
    		mMegGraphics.drawImage(1001, 50, 10, new Rect(110, 90, 240, 200));
    		mMegGraphics.drawImage(1002, 150, 110,new Rect(160, 170, 280, 300));
    		mMegGraphics.drawString(100, 150, new String("画像の上に文字を描画")); // 画像より後に描いた文字は画像に重畳する
    		mMegGraphics.end();
    	}
    	else if (position == 14 || position == 15) // 加速度 or 方位センサー
    	{
    		// センサー開始、センサー値表示、停止はSensorActivity.javaに実装
			Intent i = new Intent(getApplicationContext(), SensorActivity.class);
			Bundle bundle = new Bundle();
			if (position == 14) // 加速度
			{
				bundle.putString("exec", "acc");
			}
			else // 方位
			{
				bundle.putString("exec", "dir");
			}
			i.putExtras(bundle);
			startActivityForResult(i, SENSOR_STOP);
    	}
    	else if (position == 16) // 自動スリープ設定
    	{
    		mMeg.setAutoSleep(30); // 30秒にする
    		// スリープに入ったらMegListenerのonMegSleepが呼び出される。
    		// 解除するにはMegのupdateStatusを呼び出す。
    		// 復帰するとMegListenerのonMegStatusChangedが呼び出される。
    	}
    	else if (position == 17) // テキストスクロール
    	{
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
    		
    		//グラフィックスコマンド送信開始
    		mMegGraphics.begin();
    		
    		if (mScrollIndex == 0)
    		{
        		int[] sizes0 	= { FONT_MIDDLE, FONT_SMALL, FONT_MIDDLE, };
        		int[] colors0 	= { RED, GREEN, YELLOW, }; 
        		String[] texts0 	= { "8888888888 ", "888888888888888", "欲しいぞm(. .)m"};
        		final int startX0 = 320;
        		final int startY0 = 0;

        		//文字列登録
        		mMegGraphics.registerText(mScrollIndex, true, sizes0, colors0, texts0);
        		//スクロール設定、スクロール開始
        		mMegGraphics.registerScroll(mScrollIndex, startX0, startY0, SPEED_FAST, 1500, 0, 5);
    		}
    		else if (mScrollIndex == 1)
    		{
        		int[] sizes1 	= { FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, };
        		int[] colors1 	= { WHITE, YELLOW, WHITE, GRAY, WHITE, BLUE, WHITE, BLUE, }; 
        		String[] texts1	= { "Thank you  ", "ありがとう  ", "謝謝  ", "ありがとう  ", "Danke  ", "ありがとう  ", "Merci  ", "ありがとう  ", };
        		final int startX1 = 10;
        		final int startY1 = FONT_MIDDLE;

        		//文字列登録
        		mMegGraphics.registerText(mScrollIndex, true, sizes1, colors1, texts1);
        		//スクロール設定、スクロール開始
        		mMegGraphics.registerScroll(mScrollIndex, startX1, startY1, SPEED_SLOW, 5000, 1000, 2);
    		}
    		else if (mScrollIndex == 2)
    		{
        		int[] sizes2 	= { FONT_LARGE, FONT_MIDDLE, FONT_SMALL, };
        		int[] colors2 	= { RED, GREEN, YELLOW, }; 
        		String[] texts2 	= { "こんな風に", "文字単位で色を変えて", "スクロールできます", };
        		final int startX2 = 320;
        		final int startY2 = 90;

        		//文字列登録
        		mMegGraphics.registerText(mScrollIndex, true, sizes2, colors2, texts2);
        		//スクロール設定、スクロール開始
        		mMegGraphics.registerScroll(mScrollIndex, startX2, startY2, SPEED_FAST, 1500, 0, 1);	
    		}
    		else if (mScrollIndex == 3)
    		{
        		int[] sizes3 	= { FONT_TINY, FONT_SMALL, FONT_MIDDLE, FONT_LARGE, FONT_HUGE, FONT_HUGE, };
        		int[] colors3 	= { RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA, }; 
        		String[] texts3 	= { "wwwww", "wwwww", "wwwwww", "wwwwwwww", "wwwwwwwwww", "wwwwwww"};
        		final int startX3 = 320;
        		final int startY3 = 30;

        		//文字列登録
        		mMegGraphics.registerText(mScrollIndex, true, sizes3, colors3, texts3);
        		//スクロール設定、スクロール開始
        		mMegGraphics.registerScroll(mScrollIndex, startX3, startY3, SPEED_FAST, 1500, 0, 1);	
    		}
    		else if (mScrollIndex == 4)
    		{
        		int[] sizes4 	= { FONT_LARGE, };
        		int[] colors4 	= { WHITE, }; 
        		String[] texts4 	= { "ニコ動の真似では決してございません。関係者にお詫び申し上げます_(＊ ＊)_", };
        		final int startX4 = 0;
        		final int startY4 = 150;

        		//文字列登録
        		mMegGraphics.registerText(mScrollIndex, true, sizes4, colors4, texts4);
        		//スクロール設定、スクロール開始
        		mMegGraphics.registerScroll(mScrollIndex, startX4, startY4, SPEED_MIDDLE, 50, 2000, 1);
    		}
    		if (++mScrollIndex == 5) // 最初に戻す
    		{
    			mScrollIndex = 0;
    		}
    		
    		//グラフィックスコマンド送信終了
    		mMegGraphics.end();
    	}
    	else if (position == 18) // スクロール終了
    	{
    		int[] textIDs = { 0xffff, }; // 0xffffで全削除
    		
    		mMegGraphics.begin();
    		mMegGraphics.scrollStartStop(1);
    		mMegGraphics.removeText(textIDs); // 登録したテキストを全削除、registerScrollで登録したものも削除される
    		mMegGraphics.end();
    		
    		mScrollIndex = 0;
    	}
	}

    //他のアクティビティから結果を受信したときのコールバック
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        
        //Bluetooth接続できるペアリング済みデバイスのリストを表示するアクティビティ（ダイアログ）が終了した場合
        //ユーザーが指定したBluetoothの接続アドレスの取得に成功したら、そのアドレスのMEGに接続を開始する
	    case REQUEST_CONNECT_DEVICE:
	        if (resultCode == Activity.RESULT_OK) {
	            String address = data.getExtras()
	                                 .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				Toast.makeText(this, "Connect to " + address, Toast.LENGTH_SHORT  ).show();

				// addressは"XX:XX:XX:XX:XX:XX"の形式
				mMeg.connect(address);
				// このメソッドからはすぐに返ってくる。接続に成功するとonMegConnectedが、
				// 失敗するとonMegConnectionFailedが呼び出される
	        }
	        break;

	    case REQUEST_ENABLE_BT:
	    	Toast.makeText(this, getString(R.string.bt_disabled), Toast.LENGTH_LONG).show();
	    	if (resultCode == Activity.RESULT_OK) {
	    		Toast.makeText(this, R.string.bt_enabled, Toast.LENGTH_SHORT).show();
	        } else {
	            Toast.makeText(this, R.string.bt_disabled, Toast.LENGTH_SHORT).show();
	        }
	    	break;
	    case SENSOR_STOP: // SensorActivityが閉じられた
            Toast.makeText(this, "Sensor stop", Toast.LENGTH_SHORT).show();
	    	break;
	    }
	}
    
	// 以下、MEGのコールバック(MegListenerのメソッド)
	/** Bluetooth接続完了時のコールバック */
    @Override
	public void onMegConnected()
    {
		String deviceName = mMeg.getDeviceName();
    	Toast.makeText(this, "Connected to " + deviceName + ".", Toast.LENGTH_SHORT).show();
    }

	/** Bluetooth接続失敗時のコールバック */
    @Override
	public void onMegConnectionFailed()
    {
    	Toast.makeText(this, "Meg connection failed.", Toast.LENGTH_LONG).show();
    }

	/** Bluetooth切断時のコールバック */
    @Override
	public void onMegDisconnected()
    {
    	Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
    }

	/** スリープ開始受信時のコールバック */
    @Override
	public void onMegSleep()
    {
    	Toast.makeText(this, "スリープ状態に入ります", Toast.LENGTH_LONG).show();    	
    }

	/** ステータス受信時のコールバック */
    @Override
	public void onMegStatusChanged(MegStatus status)
    {
    	String LCD = new String(status.getLcdStatus() ? "LCD ON\n" : "LCD OFF\n");
    	String LorR = new String(status.getLRmode() ? "左装着\n" : "右装着\n");
    	String isVoltageLow = new String(status.getVoltageStatus() ? "電圧低下\n" : "電圧正常\n");
    	String AutoSleepValue = new String("自動スリープ:") + String.valueOf(status.getAutoSleepValue()) + new String("秒\n");
    	String BrightnessValue = new String("LCD輝度:") + String.valueOf(status.getBrightnessValue());
    	Toast.makeText(this, "Meg Status\n" +
    	LCD +
    	LorR + 
    	isVoltageLow + 
    	AutoSleepValue + 
    	BrightnessValue,
    	Toast.LENGTH_SHORT).show();
    }

	/** 電源電圧低下受信時のコールバック */
    @Override
	public void onMegVoltageLow()
    {
		Toast.makeText(this, "電圧が低下しています", Toast.LENGTH_LONG).show();
    }

	/** アプリボタン押下受信時のコールバック */
    @Override
	public void onMegKeyPush(int push, int release)
    {
    	if (push != 0)
    	{
    		Toast.makeText(this, "アプリボタンが押されました", Toast.LENGTH_SHORT).show();
    	}
    	else if (release != 0)
    	{
    		Toast.makeText(this, "アプリボタンが離されました", Toast.LENGTH_SHORT).show();    		
    	}
    }

	/** 方位・仰角データ受信時のコールバック
	 * @param dir 方位データ (0-3599)
	 * @param angle 仰角データ (0-255)
	 * */
    @Override
	public void onMegDirectionChanged(int dir, int angle)
	{
		
	}
	
	/** 加速度データ受信時のコールバック */
    @Override
	public void onMegAccelChanged(int x, int y, int z)
    {
    	
    }

	/** GraphicsCommand開始受信時のコールバック */
    @Override
	public void onMegGraphicsCommandStart(int ret)
    {
    	
    }

	/** GraphicsCommand終了受信時のコールバック */
    @Override
	public void onMegGraphicsCommandEnd(int ret)
    {
    	
    }

	/** コンテキスト設定受信時のコールバック */
    @Override
	public void onMegSetContext(int ret)
    {
    	// クリアカラー設定、フォントサイズ指定、フォント表示色設定をすると、MEGからの応答として
    	// このメソッドが呼ばれる。
    	// ここでは結果を表示しているが、特に何かをする必要はない
		Toast.makeText(this, ret == 1 ? "OK" : "NG", Toast.LENGTH_SHORT).show();
    }

	/** Image削除受信時のコールバック */
    @Override
	public void onMegDeleteImage(int ret)
    {
    	
    }

}
