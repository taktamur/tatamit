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
import jp.paming.CameraActivity;

public class MainActivity extends Activity implements OnItemClickListener, MegListener {

	private Meg mMeg; //MEG�ւ̃R�}���h���M���s���C���X�^���X
	private MegGraphics mMegGraphics; // �O���t�B�b�N�`��p
	
	// Intent request codes
	private static final int REQUEST_CONNECT_DEVICE = 1; // MEG�ւ̐ڑ��v��
    private static final int REQUEST_ENABLE_BT = 2;
    private static final int SENSOR_STOP = 3; // �Z���T�[�l�\���̃A�N�e�B�r�e�B������ꂽ
    
    private int mScrollIndex = 0; // �e�L�X�g�X�N���[���p
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // ListView�����
        String[] items = new String[]
        		{
        		"0. �ڑ�", 
        		"1. �ؒf", 
        		"2. �����݂��ƃf��"
        		};
        
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);
        ListView listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(adapter);
        // ���X�g�r���[�̃A�C�e�����N���b�N���ꂽ���ɌĂяo�����R�[���o�b�N���X�i�[��o�^���܂�
        listview.setOnItemClickListener(this); // onItemClick�ŏ���
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }    

    /**
     *  ListView�̃A�C�e�����N���b�N����
     */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id)
	{

    	if (position == 0) // �ڑ�
    	{
            //Bluetooth�ڑ��ł��邩�ǂ����`�F�b�N����
        	//�ڑ��ł��Ȃ���΁A�A�v�����I��
    		if (mMeg == null)
    		{
	        	try {
	        		// MEG�̓V���O���g���p�^�[��
	        		// �ŏ���getInstance�Ăяo���ł̓C���X�^���X�������ɗ�O���������邱�Ƃ�����
	    			mMeg = Meg.getInstance();
	        		// MEG�̃C�x���g�Ď��̃n���h����o�^
	            	mMeg.registerMegListener(this);

	                // MEG�̃O���t�B�b�N�X�@�\���g���N���X�̐���
	                mMegGraphics = new MegGraphics(mMeg);
	    		} catch (BluetoothNotFoundException e) {
	    			Toast.makeText(this, "Bluetooth�A�_�v�^�[��������܂���", Toast.LENGTH_LONG).show();
	    			finish();
	    			return;
	    		} catch (BluetoothNotEnabledException e) {
	    			Toast.makeText(this, "Bluetooth�������ɂȂ��Ă��܂�\n�L���ɂ��Ă�������", Toast.LENGTH_LONG).show();
	    			finish();
	    			return;
	    		}
    		}
    		// mMeg�͔�null
    		if (mMeg.isConnected())
    		{
    			// �ڑ��ς�
    		}
    		else // ���ڑ�
    		{
	            //Bluetooth�ڑ��ł���y�A�����O�ς݃f�o�C�X�̃��X�g��\������A�N�e�B�r�e�B�i�_�C�A���O�j���J�n����B
	        	//�A�N�e�B�r�e�B���I��������AonActivityResult() �ɏI���R�[�h�Ƃ��āAREQUEST_CONNECT_DEVICE��Ԃ��B
	            //MEG�ւ̐ڑ���onActivityResult()�Ŏ��s�����B
	            Intent serverIntent = new Intent(this, DeviceListActivity.class);
	            startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
    		}
    		return;
    	}
		if (mMeg == null || !mMeg.isConnected())
		{
			Toast.makeText(this, "�ڑ����Ă�������", Toast.LENGTH_LONG).show();
			return;
		}

		// mMeg�͔�null�A���A�ڑ��ς�
		else if (position == 1) // �ؒf
    	{
    		mMeg.disconnect();
    		// MegListener��onMegDisconnected���Ăяo�����
    	}
    	else if( position == 2 ){ // �����݂��ƃf��
    		// Activity�@��
            Intent serverIntent = new Intent(this, CameraActivity.class);
            startActivity(serverIntent);
    	}
	}

    //���̃A�N�e�B�r�e�B���猋�ʂ���M�����Ƃ��̃R�[���o�b�N
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
        
        //Bluetooth�ڑ��ł���y�A�����O�ς݃f�o�C�X�̃��X�g��\������A�N�e�B�r�e�B�i�_�C�A���O�j���I�������ꍇ
        //���[�U�[���w�肵��Bluetooth�̐ڑ��A�h���X�̎擾�ɐ���������A���̃A�h���X��MEG�ɐڑ����J�n����
	    case REQUEST_CONNECT_DEVICE:
	        if (resultCode == Activity.RESULT_OK) {
	            String address = data.getExtras()
	                                 .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
				Toast.makeText(this, "Connect to " + address, Toast.LENGTH_SHORT  ).show();

				// address��"XX:XX:XX:XX:XX:XX"�̌`��
				mMeg.connect(address);
				// ���̃��\�b�h����͂����ɕԂ��Ă���B�ڑ��ɐ��������onMegConnected���A
				// ���s�����onMegConnectionFailed���Ăяo�����
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
	    case SENSOR_STOP: // SensorActivity������ꂽ
            Toast.makeText(this, "Sensor stop", Toast.LENGTH_SHORT).show();
	    	break;
	    }
	}
    
	// �ȉ��AMEG�̃R�[���o�b�N(MegListener�̃��\�b�h)
	/** Bluetooth�ڑ��������̃R�[���o�b�N */
    @Override
	public void onMegConnected()
    {
		String deviceName = mMeg.getDeviceName();
    	Toast.makeText(this, "Connected to " + deviceName + ".", Toast.LENGTH_SHORT).show();
    }

	/** Bluetooth�ڑ����s���̃R�[���o�b�N */
    @Override
	public void onMegConnectionFailed()
    {
    	Toast.makeText(this, "Meg connection failed.", Toast.LENGTH_LONG).show();
    }

	/** Bluetooth�ؒf���̃R�[���o�b�N */
    @Override
	public void onMegDisconnected()
    {
    	Toast.makeText(this, "Disconnected", Toast.LENGTH_LONG).show();
    }

	/** �X���[�v�J�n��M���̃R�[���o�b�N */
    @Override
	public void onMegSleep()
    {
    	Toast.makeText(this, "�X���[�v��Ԃɓ���܂�", Toast.LENGTH_LONG).show();    	
    }

	/** �X�e�[�^�X��M���̃R�[���o�b�N */
    @Override
	public void onMegStatusChanged(MegStatus status)
    {
    	String LCD = new String(status.getLcdStatus() ? "LCD ON\n" : "LCD OFF\n");
    	String LorR = new String(status.getLRmode() ? "������\n" : "�E����\n");
    	String isVoltageLow = new String(status.getVoltageStatus() ? "�d���ቺ\n" : "�d������\n");
    	String AutoSleepValue = new String("�����X���[�v:") + String.valueOf(status.getAutoSleepValue()) + new String("�b\n");
    	String BrightnessValue = new String("LCD�P�x:") + String.valueOf(status.getBrightnessValue());
    	Toast.makeText(this, "Meg Status\n" +
    	LCD +
    	LorR + 
    	isVoltageLow + 
    	AutoSleepValue + 
    	BrightnessValue,
    	Toast.LENGTH_SHORT).show();
    }

	/** �d���d���ቺ��M���̃R�[���o�b�N */
    @Override
	public void onMegVoltageLow()
    {
		Toast.makeText(this, "�d�����ቺ���Ă��܂�", Toast.LENGTH_LONG).show();
    }

	/** �A�v���{�^��������M���̃R�[���o�b�N */
    @Override
	public void onMegKeyPush(int push, int release)
    {
    	if (push != 0)
    	{
    		Toast.makeText(this, "�A�v���{�^����������܂���", Toast.LENGTH_SHORT).show();
    	}
    	else if (release != 0)
    	{
    		Toast.makeText(this, "�A�v���{�^����������܂���", Toast.LENGTH_SHORT).show();    		
    	}
    }

	/** ���ʁE�p�f�[�^��M���̃R�[���o�b�N
	 * @param dir ���ʃf�[�^ (0-3599)
	 * @param angle �p�f�[�^ (0-255)
	 * */
    @Override
	public void onMegDirectionChanged(int dir, int angle)
	{
		
	}
	
	/** �����x�f�[�^��M���̃R�[���o�b�N */
    @Override
	public void onMegAccelChanged(int x, int y, int z)
    {
    	
    }

	/** GraphicsCommand�J�n��M���̃R�[���o�b�N */
    @Override
	public void onMegGraphicsCommandStart(int ret)
    {
    	
    }

	/** GraphicsCommand�I����M���̃R�[���o�b�N */
    @Override
	public void onMegGraphicsCommandEnd(int ret)
    {
    	
    }

	/** �R���e�L�X�g�ݒ��M���̃R�[���o�b�N */
    @Override
	public void onMegSetContext(int ret)
    {
    	// �N���A�J���[�ݒ�A�t�H���g�T�C�Y�w��A�t�H���g�\���F�ݒ������ƁAMEG����̉����Ƃ���
    	// ���̃��\�b�h���Ă΂��B
    	// �����ł͌��ʂ�\�����Ă��邪�A���ɉ���������K�v�͂Ȃ�
		Toast.makeText(this, ret == 1 ? "OK" : "NG", Toast.LENGTH_SHORT).show();
    }

	/** Image�폜��M���̃R�[���o�b�N */
    @Override
	public void onMegDeleteImage(int ret)
    {
    	
    }

}
