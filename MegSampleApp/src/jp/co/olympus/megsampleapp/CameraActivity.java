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
	private Meg mMeg; //MEG�ւ̃R�}���h���M���s���C���X�^���X
	private MegGraphics mMegGraphics; // �O���t�B�b�N�`��p
	private MegListener originalListener;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
        transButton = (Button)this.findViewById(R.id.trans);
        message = (TextView)this.findViewById(R.id.message);
        
        // �ŏ���getInstance�Ăяo���ł̓C���X�^���X�������ɗ�O���������邱�Ƃ�����
        try {
			mMeg = Meg.getInstance();
		} catch (BluetoothNotFoundException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (BluetoothNotEnabledException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		// MEG�̃C�x���g�Ď��̃n���h����o�^
        // ����Activity���I���Ƃ��AoriginalListener�ɖ߂�
        originalListener = mMeg.registerMegListener(this);
        // MEG�̃O���t�B�b�N�X�@�\���g���N���X�̐���
        mMegGraphics = new MegGraphics(mMeg);

        //        guiThread = new Handler();
        // �{�^���̃g���K�[�ݒ�(���1�b�����ɕύX) 
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
		    		mMegGraphics.drawString(100, 50,transMessage); // (100, 50)�̈ʒu�ɕ`��
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
		// �I�����ɂ̓I���W�i���ɖ߂�
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
				// Meg�ւ̒ʐM�͂����ł��
				// TODO
				view.stText(text);
			}			
		});
	}
*/
}
