package jp.co.olympus.megsampleapp;

import java.util.Date;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.*;
import android.widget.*;

public class CameraActivity extends Activity {
	private Button transButton ;
	private TextView message;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        transButton = (Button)this.findViewById(R.id.trans);
        message = (TextView)this.findViewById(R.id.message);
        
        // ボタンのトリガー設定(後で1秒おきに変更) 
        // TODO
        transButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				message.setText((new Date()).toString());
			}
		});
    }
}
