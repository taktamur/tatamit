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
        		"2. たたみっとデモ"
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
		else if (position == 1) // 切断
    	{
    		mMeg.disconnect();
    		// MegListenerのonMegDisconnectedが呼び出される
    	}
    	else if( position == 2 ){ // たたみっとデモ
    		// Activity機動
            Intent serverIntent = new Intent(this, CameraActivity.class);
            startActivity(serverIntent);
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
