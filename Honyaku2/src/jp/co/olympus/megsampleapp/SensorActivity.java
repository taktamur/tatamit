package jp.co.olympus.megsampleapp;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
//import android.widget.Toast;

import jp.co.olympus.meg40.Meg;
import jp.co.olympus.meg40.MegListener;
import jp.co.olympus.meg40.MegStatus;

public class SensorActivity extends Activity implements MegListener {
	private ArrayList<String> list;
	private ArrayAdapter<String> adapter;
	MegListener oldListener;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sensor);

        // センサー値の表示用
        list = new ArrayList<String>();
        list.add("X軸加速度=");
        list.add("---"); // onMegAccelChangedで値を入れる
        list.add("Y軸加速度=");
        list.add("---"); // onMegAccelChangedで値を入れる
        list.add("Z軸加速度=");
        list.add("---"); // onMegAccelChangedで値を入れる
        list.add("方位=");
        list.add("---"); // onMegDirectionChangedで値を入れる
        list.add("仰角=");
        list.add("---"); // onMegDirectionChangedで値を入れる
        
        adapter = new ArrayAdapter<String>(
        		getApplicationContext(), android.R.layout.simple_list_item_1, list);
        GridView gridView = (GridView) findViewById(R.id.sensorView);
        gridView.setAdapter(adapter);

        // Stopボタンを押したときの動作
        Button button = (Button)findViewById(R.id.button_sensor_stop);
        button.setOnClickListener(new OnClickListener() {
            @Override
		    public void onClick(View v) {
            	// Activityを閉じる
	            finish();
		    }        	
        });
        
        try
        {
            Bundle bundle = getIntent().getExtras();
            String which = bundle.getString("exec");

        	// MEGのlistenerをこのクラスに差し替え
        	oldListener = Meg.getInstance().registerMegListener(this);
        	if (which.equals("acc"))
        	{
	        	// 加速度センサー開始
	        	// センサー値が届くたびにonMegAccelChangedが呼び出される
        		// 引数はSENSOR_FAST, SENSOR_MIDDLE, SENSOR_SLOWから選択
	        	Meg.getInstance().startAccelerometer(Meg.SENSOR_MIDDLE);
        	}
        	else
        	{
        		// 方位センサー開始
	        	// センサー値が届くたびにonMegDirectionChangedが呼び出される
        		// 引数はSENSOR_FAST, SENSOR_MIDDLE, SENSOR_SLOWから選択
        		Meg.getInstance().startDirectionSensor(Meg.SENSOR_MIDDLE);        		
        	}
        }
        catch (Exception e)
        {
        	
        }
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        try
        {
            Bundle bundle = getIntent().getExtras();
            String which = bundle.getString("exec");

        	// センサーを止めて
        	if (which.equals("acc"))
            {
            	Meg.getInstance().stopAccelerometer();
            }
            else
            {
            	Meg.getInstance().stopDirectionSensor();            	
            }
        	// listenerを元に戻す
        	Meg.getInstance().registerMegListener(oldListener);
        }
        catch (Exception e)
        {
        }
    }

	// 以下、MEGのコールバック
    // ここではセンサー値しか取得しないので、onMegAccelChangedとonMegDirectionChangedしか実装しない
	/** 方位・仰角データ受信時のコールバック
	 * @param dir 方位データ (0-3599)
	 * @param angle 仰角データ (0-255)
	 * */
    @Override
	public void onMegDirectionChanged(int dir, int angle)
	{
    	list.set(1, "---");
    	list.set(3, "---");
    	list.set(5, "---");
    	list.set(7, String.valueOf(dir)); // [7]が方位
    	list.set(9, String.valueOf(angle)); // [9]が仰角
    	adapter.notifyDataSetChanged(); // 表示更新
    	// ここで時間のかかる処理はしない方がよい
	}
	
	/** 加速度データ受信時のコールバック */
    @Override
	public void onMegAccelChanged(int x, int y, int z)
    {
    	list.set(1, String.valueOf(x)); // [1]がX軸加速度
    	list.set(3, String.valueOf(y)); // [3]がY軸加速度
    	list.set(5, String.valueOf(z)); // [5]がZ軸加速度
    	list.set(7, "---");
    	list.set(9, "---");
    	adapter.notifyDataSetChanged(); // 表示更新
    	// ここで時間のかかる処理はしない方がよい
    }

	/** Bluetooth接続完了時のコールバック */
    @Override
	public void onMegConnected()
    {
    }

	/** Bluetooth接続失敗時のコールバック */
    @Override
	public void onMegConnectionFailed()
    {
    }

	/** Bluetooth切断時のコールバック */
    @Override
	public void onMegDisconnected()
    {
    }

	/** スリープ開始受信時のコールバック */
    @Override
	public void onMegSleep()
    {
    	
    }

	/** ステータス受信時のコールバック */
    @Override
	public void onMegStatusChanged(MegStatus status)
    {
    }

	/** 電源電圧低下受信時のコールバック */
    @Override
	public void onMegVoltageLow()
    {
    	
    }

	/** キー押下受信時のコールバック */
    @Override
	public void onMegKeyPush(int push, int release)
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
    }

	/** Image削除受信時のコールバック */
    @Override
	public void onMegDeleteImage(int ret)
    {
    }
}
