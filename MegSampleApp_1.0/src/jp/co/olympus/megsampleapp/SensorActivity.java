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

        // �Z���T�[�l�̕\���p
        list = new ArrayList<String>();
        list.add("X�������x=");
        list.add("---"); // onMegAccelChanged�Œl������
        list.add("Y�������x=");
        list.add("---"); // onMegAccelChanged�Œl������
        list.add("Z�������x=");
        list.add("---"); // onMegAccelChanged�Œl������
        list.add("����=");
        list.add("---"); // onMegDirectionChanged�Œl������
        list.add("�p=");
        list.add("---"); // onMegDirectionChanged�Œl������
        
        adapter = new ArrayAdapter<String>(
        		getApplicationContext(), android.R.layout.simple_list_item_1, list);
        GridView gridView = (GridView) findViewById(R.id.sensorView);
        gridView.setAdapter(adapter);

        // Stop�{�^�����������Ƃ��̓���
        Button button = (Button)findViewById(R.id.button_sensor_stop);
        button.setOnClickListener(new OnClickListener() {
            @Override
		    public void onClick(View v) {
            	// Activity�����
	            finish();
		    }        	
        });
        
        try
        {
            Bundle bundle = getIntent().getExtras();
            String which = bundle.getString("exec");

        	// MEG��listener�����̃N���X�ɍ����ւ�
        	oldListener = Meg.getInstance().registerMegListener(this);
        	if (which.equals("acc"))
        	{
	        	// �����x�Z���T�[�J�n
	        	// �Z���T�[�l���͂����т�onMegAccelChanged���Ăяo�����
        		// ������SENSOR_FAST, SENSOR_MIDDLE, SENSOR_SLOW����I��
	        	Meg.getInstance().startAccelerometer(Meg.SENSOR_MIDDLE);
        	}
        	else
        	{
        		// ���ʃZ���T�[�J�n
	        	// �Z���T�[�l���͂����т�onMegDirectionChanged���Ăяo�����
        		// ������SENSOR_FAST, SENSOR_MIDDLE, SENSOR_SLOW����I��
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

        	// �Z���T�[���~�߂�
        	if (which.equals("acc"))
            {
            	Meg.getInstance().stopAccelerometer();
            }
            else
            {
            	Meg.getInstance().stopDirectionSensor();            	
            }
        	// listener�����ɖ߂�
        	Meg.getInstance().registerMegListener(oldListener);
        }
        catch (Exception e)
        {
        }
    }

	// �ȉ��AMEG�̃R�[���o�b�N
    // �����ł̓Z���T�[�l�����擾���Ȃ��̂ŁAonMegAccelChanged��onMegDirectionChanged�����������Ȃ�
	/** ���ʁE�p�f�[�^��M���̃R�[���o�b�N
	 * @param dir ���ʃf�[�^ (0-3599)
	 * @param angle �p�f�[�^ (0-255)
	 * */
    @Override
	public void onMegDirectionChanged(int dir, int angle)
	{
    	list.set(1, "---");
    	list.set(3, "---");
    	list.set(5, "---");
    	list.set(7, String.valueOf(dir)); // [7]������
    	list.set(9, String.valueOf(angle)); // [9]���p
    	adapter.notifyDataSetChanged(); // �\���X�V
    	// �����Ŏ��Ԃ̂����鏈���͂��Ȃ������悢
	}
	
	/** �����x�f�[�^��M���̃R�[���o�b�N */
    @Override
	public void onMegAccelChanged(int x, int y, int z)
    {
    	list.set(1, String.valueOf(x)); // [1]��X�������x
    	list.set(3, String.valueOf(y)); // [3]��Y�������x
    	list.set(5, String.valueOf(z)); // [5]��Z�������x
    	list.set(7, "---");
    	list.set(9, "---");
    	adapter.notifyDataSetChanged(); // �\���X�V
    	// �����Ŏ��Ԃ̂����鏈���͂��Ȃ������悢
    }

	/** Bluetooth�ڑ��������̃R�[���o�b�N */
    @Override
	public void onMegConnected()
    {
    }

	/** Bluetooth�ڑ����s���̃R�[���o�b�N */
    @Override
	public void onMegConnectionFailed()
    {
    }

	/** Bluetooth�ؒf���̃R�[���o�b�N */
    @Override
	public void onMegDisconnected()
    {
    }

	/** �X���[�v�J�n��M���̃R�[���o�b�N */
    @Override
	public void onMegSleep()
    {
    	
    }

	/** �X�e�[�^�X��M���̃R�[���o�b�N */
    @Override
	public void onMegStatusChanged(MegStatus status)
    {
    }

	/** �d���d���ቺ��M���̃R�[���o�b�N */
    @Override
	public void onMegVoltageLow()
    {
    	
    }

	/** �L�[������M���̃R�[���o�b�N */
    @Override
	public void onMegKeyPush(int push, int release)
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
    }

	/** Image�폜��M���̃R�[���o�b�N */
    @Override
	public void onMegDeleteImage(int ret)
    {
    }
}
