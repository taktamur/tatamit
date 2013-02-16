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
        		"2. �X�e�[�^�X�擾", 
        		"3. �N���A�J���[�ݒ�", 
        		"4. ��ʃN���A", 
        		"5. �t�H���g�T�C�Y�w��", 
        		"6. �t�H���g�\���F�ݒ�", 
        		"7. ������`��1", 
        		"8. ������`��2", 
        		"9. �摜�o�^1(11.�ŕ`��)", 
        		"10. �摜�o�^2(13.�ŕ`��)", 
        		"11. �摜�`��1", 
        		"12. �摜�`��2", 
        		"13. �摜�`��3", 
        		"14. �����x�Z���T�[�l�擾",
        		"15. ���ʃZ���T�[�l�擾",
        		"16. �����X���[�v�ݒ�",
        		"17. �e�L�X�g�X�N���[��",
        		"18. �X�N���[���I��",
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
    	if (position == 1) // �ؒf
    	{
    		mMeg.disconnect();
    		// MegListener��onMegDisconnected���Ăяo�����
    	}
    	else if (position == 2) // �X�e�[�^�X�擾
    	{
    		mMeg.updateStatus(); // �X�e�[�^�X�擾
    		// MegListener��onMegStatusChanged���Ăяo�����
    	}
    	else if (position == 3) // �N���A�J���[�ݒ�
    	{
    		// �N���A�J���[��ɐݒ肷��
    		mMegGraphics.begin();
    		mMegGraphics.setClearColor(0xff0000ff); // 0xffRRGGBB, ��
    		mMegGraphics.end();
    	}
    	else if (position == 4) // �N���A
    	{
    		// �ݒ肳��Ă���N���A�J���[�ŉ�ʂ���������
    		mMegGraphics.begin();
    		mMegGraphics.clearScreen();
    		mMegGraphics.end();
    	}
    	else if (position == 5) // �t�H���g�T�C�Y�w��
    	{
    		mMegGraphics.begin();
    		mMegGraphics.setFontSize(80); // 80�|�C���g
    		mMegGraphics.end();
    	}
    	else if (position == 6) // �t�H���g�\���F�ݒ�
    	{
    		mMegGraphics.begin();
    		mMegGraphics.setFontColor(0xff00ff00); // 0xffRRGGBB, ��
    		mMegGraphics.end();
    	}
    	else if (position == 7) // ������`��1
    	{
    		mMegGraphics.begin();
    		mMegGraphics.drawString(100, 50, new String("abc ���Ȋ���")); // (100, 50)�̈ʒu�ɕ`��
    		mMegGraphics.end();
    	}
    	else if (position == 8) // ������`��2
    	{
    		mMegGraphics.begin();
    		mMegGraphics.setFontSize(40);
    		mMegGraphics.setFontColor(0xff00ff00); // 0xffRRGGBB, ��
    		mMegGraphics.drawString(-20, 20, new String("�����̓V�C 40pt")); // ���̒l���\
    		mMegGraphics.setFontSize(30);
    		mMegGraphics.setFontColor(0xffff0000); // 0xffRRGGBB, ��
    		mMegGraphics.drawString(10, 60, new String("�����F���� 30pt"));
    		mMegGraphics.setFontSize(20);
    		mMegGraphics.setFontColor(0xffffff00); // 0xffRRGGBB, ���F
    		mMegGraphics.drawString(250, 90, new String("20pt �C��10��")); // ��ʂ���͂ݏo���P�[�X
    		mMegGraphics.end();
    	}
    	else if (position == 9) // �摜�o�^1
    	{
    		try
    		{
    			InputStream is = getResources().getAssets().open("landscape.jpg");
    			Bitmap bm = BitmapFactory.decodeStream(is);
    			
        		mMegGraphics.begin();
        		mMegGraphics.registerImage(1000, bm); // ID=1000�ɓo�^
        		mMegGraphics.end();
        		// �o�^�����摜�́u�摜�`��1�v�ŕ\��
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
    		// ---------------------------------------------------------------------------
    		// ������̉摜�o�^�AregisterImageFromBytes�̗�
    		// registerImageFromBytes���g��JPEG�摜��o�^����ꍇ�A�摜�̕������ɂ���āAMEG��������B
    		// registerImageFromBytes��Javadoc�Q�ƁB
    		/*
    		try
    		{
    			// asset����S���ǂݏo����OutputStream�ɓ����
    			InputStream is = getResources().getAssets().open("catQVGA.jpg"); // QVGA�͕��A�����Ƃ�16�̔{���Ȃ̂�ok
				Bitmap bm = BitmapFactory.decodeStream(is);
				ByteArrayOutputStream os = new ByteArrayOutputStream();
    			int format = 0; // 0��JPEG�A1��PNG
				if (format == 0)
				{
					// �Ĉ��k
					bm.compress(Bitmap.CompressFormat.JPEG, 50, os);
				}
				else if (format == 1)
				{
					// PNG�ɕϊ�
					bm.compress(Bitmap.CompressFormat.PNG, 50, os);
				}

        		mMegGraphics.begin();
        		mMegGraphics.registerImageFromBytes(2000, os.toByteArray(), format); // ID=2000�ɓo�^
        		mMegGraphics.drawImage(2000, 0, 0, new Rect(0, 0, bm.getWidth(), bm.getHeight())); // �S�̂ɕ\��
        		mMegGraphics.end();
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
			*/
    	}
    	else if (position == 10) // �摜�o�^2
    	{
    		try
    		{
    			// 2����o�^
        		// �o�^�����摜�́u�摜�`��3�v�ŕ\��
    			InputStream is = getResources().getAssets().open("cat.jpg");
    			Bitmap bm = BitmapFactory.decodeStream(is);
    			InputStream is2 = getResources().getAssets().open("flower.jpg");
    			Bitmap bm2 = BitmapFactory.decodeStream(is2);

    			mMegGraphics.begin();
        		mMegGraphics.registerImage(1001, bm); // ���ׂđ��M����܂ŕԂ�Ȃ�
        		mMegGraphics.registerImage(1002, bm2);
        		mMegGraphics.end();
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
    	}
    	else if (position == 11) // �摜�`��1
    	{
    		// ���O�ɉ摜�o�^1�����s���Ă����B
    		mMegGraphics.begin();
    		mMegGraphics.drawImage(1000, 0, 0, new Rect(10, 30, 330, 270)); // �摜��(10, 30)-(330, 270)��QVGA�T�C�Y��؂�o���ĕ`��
    		mMegGraphics.end();    			
    	}
    	else if (position == 12) // �摜�`��2
    	{
    		// �����̉�ʂɉ����`�悵�Ă����ԂŁA�����ɉ摜�ƕ�����ǉ�����ꍇ�̐U�镑��
    		mMegGraphics.begin();
    		mMegGraphics.setClearColor(0xff00ffff); // 0xffRRGGBB, ���F
    		mMegGraphics.clearScreen();
    		mMegGraphics.setFontSize(40);
    		mMegGraphics.setFontColor(0xff000000); // 0xffRRGGBB, ��
    		mMegGraphics.drawString(20, 40, new String("�����̓V�C"));    		
    		mMegGraphics.end();

    		// �u�����̓V�C�v���`���Ă����ʂɐ���̉摜�ƕ������ǉ�����
    		try
    		{
	    		InputStream is = getResources().getAssets().open("hare.png");
				Bitmap bm = BitmapFactory.decodeStream(is);
    		
	    		mMegGraphics.begin();
	    		mMegGraphics.drawString(20, 80, new String("����"));
        		mMegGraphics.registerImage(1003, bm);
	    		mMegGraphics.drawImage(1003, 100, 100, new Rect(0, 0, 64, 64));
	    		mMegGraphics.end();
    		}
    		catch (Exception e)
    		{
				Toast.makeText(this, "open asset failed", Toast.LENGTH_SHORT).show();
    		}
    	}
    	else if (position == 13) // �摜�`��3
    	{
    		// ���O�ɉ摜�o�^2�����s���Ă����B
    		mMegGraphics.begin();
    		mMegGraphics.clearScreen();
    		mMegGraphics.setFontColor(0xff00ff00); // 0xffRRGGBB, ��
    		mMegGraphics.setFontSize(40);
    		mMegGraphics.drawString(20, 40, new String("�����̏�ɉ摜��`��")); // �摜����ɕ`���������͉���drawImage�ŉB�����
    		mMegGraphics.drawImage(1001, 50, 10, new Rect(110, 90, 240, 200));
    		mMegGraphics.drawImage(1002, 150, 110,new Rect(160, 170, 280, 300));
    		mMegGraphics.drawString(100, 150, new String("�摜�̏�ɕ�����`��")); // �摜����ɕ`���������͉摜�ɏd�􂷂�
    		mMegGraphics.end();
    	}
    	else if (position == 14 || position == 15) // �����x or ���ʃZ���T�[
    	{
    		// �Z���T�[�J�n�A�Z���T�[�l�\���A��~��SensorActivity.java�Ɏ���
			Intent i = new Intent(getApplicationContext(), SensorActivity.class);
			Bundle bundle = new Bundle();
			if (position == 14) // �����x
			{
				bundle.putString("exec", "acc");
			}
			else // ����
			{
				bundle.putString("exec", "dir");
			}
			i.putExtras(bundle);
			startActivityForResult(i, SENSOR_STOP);
    	}
    	else if (position == 16) // �����X���[�v�ݒ�
    	{
    		mMeg.setAutoSleep(30); // 30�b�ɂ���
    		// �X���[�v�ɓ�������MegListener��onMegSleep���Ăяo�����B
    		// ��������ɂ�Meg��updateStatus���Ăяo���B
    		// ���A�����MegListener��onMegStatusChanged���Ăяo�����B
    	}
    	else if (position == 17) // �e�L�X�g�X�N���[��
    	{
    		//�F��`
    		final int RED 		= 0xffff0000;
    		final int GREEN 	= 0xff00ff00;
    		final int BLUE 		= 0xff0000ff;
    		final int YELLOW 	= 0xffffff00;
    		final int MAGENTA 	= 0xffff00ff;
    		final int CYAN 		= 0xff00ffff;
    		final int WHITE 	= 0xffffffff;
    		final int GRAY      = 0xffcccccc;

    		//�t�H���g�T�C�Y��`
    		final int FONT_HUGE 	= 100;
    		final int FONT_LARGE 	= 80;
    		final int FONT_MIDDLE 	= 60;
    		final int FONT_SMALL 	= 40;
    		final int FONT_TINY 	= 20;
    		
    		//�ړ��ʁi�s�N�Z���j
    		final int SPEED_SLOW = 2;
    		final int SPEED_MIDDLE = 6;
    		final int SPEED_FAST = 10;
    		
    		//�O���t�B�b�N�X�R�}���h���M�J�n
    		mMegGraphics.begin();
    		
    		if (mScrollIndex == 0)
    		{
        		int[] sizes0 	= { FONT_MIDDLE, FONT_SMALL, FONT_MIDDLE, };
        		int[] colors0 	= { RED, GREEN, YELLOW, }; 
        		String[] texts0 	= { "8888888888 ", "888888888888888", "�~������m(. .)m"};
        		final int startX0 = 320;
        		final int startY0 = 0;

        		//������o�^
        		mMegGraphics.registerText(mScrollIndex, true, sizes0, colors0, texts0);
        		//�X�N���[���ݒ�A�X�N���[���J�n
        		mMegGraphics.registerScroll(mScrollIndex, startX0, startY0, SPEED_FAST, 1500, 0, 5);
    		}
    		else if (mScrollIndex == 1)
    		{
        		int[] sizes1 	= { FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, FONT_MIDDLE, };
        		int[] colors1 	= { WHITE, YELLOW, WHITE, GRAY, WHITE, BLUE, WHITE, BLUE, }; 
        		String[] texts1	= { "Thank you  ", "���肪�Ƃ�  ", "�ӎ�  ", "���肪�Ƃ�  ", "Danke  ", "���肪�Ƃ�  ", "Merci  ", "���肪�Ƃ�  ", };
        		final int startX1 = 10;
        		final int startY1 = FONT_MIDDLE;

        		//������o�^
        		mMegGraphics.registerText(mScrollIndex, true, sizes1, colors1, texts1);
        		//�X�N���[���ݒ�A�X�N���[���J�n
        		mMegGraphics.registerScroll(mScrollIndex, startX1, startY1, SPEED_SLOW, 5000, 1000, 2);
    		}
    		else if (mScrollIndex == 2)
    		{
        		int[] sizes2 	= { FONT_LARGE, FONT_MIDDLE, FONT_SMALL, };
        		int[] colors2 	= { RED, GREEN, YELLOW, }; 
        		String[] texts2 	= { "����ȕ���", "�����P�ʂŐF��ς���", "�X�N���[���ł��܂�", };
        		final int startX2 = 320;
        		final int startY2 = 90;

        		//������o�^
        		mMegGraphics.registerText(mScrollIndex, true, sizes2, colors2, texts2);
        		//�X�N���[���ݒ�A�X�N���[���J�n
        		mMegGraphics.registerScroll(mScrollIndex, startX2, startY2, SPEED_FAST, 1500, 0, 1);	
    		}
    		else if (mScrollIndex == 3)
    		{
        		int[] sizes3 	= { FONT_TINY, FONT_SMALL, FONT_MIDDLE, FONT_LARGE, FONT_HUGE, FONT_HUGE, };
        		int[] colors3 	= { RED, GREEN, BLUE, YELLOW, CYAN, MAGENTA, }; 
        		String[] texts3 	= { "wwwww", "wwwww", "wwwwww", "wwwwwwww", "wwwwwwwwww", "wwwwwww"};
        		final int startX3 = 320;
        		final int startY3 = 30;

        		//������o�^
        		mMegGraphics.registerText(mScrollIndex, true, sizes3, colors3, texts3);
        		//�X�N���[���ݒ�A�X�N���[���J�n
        		mMegGraphics.registerScroll(mScrollIndex, startX3, startY3, SPEED_FAST, 1500, 0, 1);	
    		}
    		else if (mScrollIndex == 4)
    		{
        		int[] sizes4 	= { FONT_LARGE, };
        		int[] colors4 	= { WHITE, }; 
        		String[] texts4 	= { "�j�R���̐^���ł͌����Ă������܂���B�֌W�҂ɂ��l�ѐ\���グ�܂�_(�� ��)_", };
        		final int startX4 = 0;
        		final int startY4 = 150;

        		//������o�^
        		mMegGraphics.registerText(mScrollIndex, true, sizes4, colors4, texts4);
        		//�X�N���[���ݒ�A�X�N���[���J�n
        		mMegGraphics.registerScroll(mScrollIndex, startX4, startY4, SPEED_MIDDLE, 50, 2000, 1);
    		}
    		if (++mScrollIndex == 5) // �ŏ��ɖ߂�
    		{
    			mScrollIndex = 0;
    		}
    		
    		//�O���t�B�b�N�X�R�}���h���M�I��
    		mMegGraphics.end();
    	}
    	else if (position == 18) // �X�N���[���I��
    	{
    		int[] textIDs = { 0xffff, }; // 0xffff�őS�폜
    		
    		mMegGraphics.begin();
    		mMegGraphics.scrollStartStop(1);
    		mMegGraphics.removeText(textIDs); // �o�^�����e�L�X�g��S�폜�AregisterScroll�œo�^�������̂��폜�����
    		mMegGraphics.end();
    		
    		mScrollIndex = 0;
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
