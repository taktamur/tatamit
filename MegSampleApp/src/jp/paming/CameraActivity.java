package jp.paming;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jp.co.olympus.meg40.*;
import jp.co.olympus.megsampleapp.R;
import jp.co.olympus.megsampleapp.R.id;
import jp.co.olympus.megsampleapp.R.layout;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.*;
import android.widget.*;

public class CameraActivity extends Activity implements MegListener {
//	private Button transButton ;
	private TextView message;
	private Handler guiThread;
	private Meg mMeg; //MEGへのコマンド送信を行うインスタンス
	private MegGraphics mMegGraphics; // グラフィック描画用
	private MegListener originalListener;
	private String transMessage;

	final int INTERVAL_PERIOD = 1000;
	private Timer timer = new Timer();
	
    private Preview mPreview;
    Camera mCamera;
    int numberOfCameras;
    int cameraCurrentlyLocked;
    // The first rear facing camera
    int defaultCameraId;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);
//        transButton = (Button)this.findViewById(R.id.trans);
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

        guiThread = new Handler();
        // ボタンのトリガー設定(後で1秒おきに変更) 
//        
//        timer.scheduleAtFixedRate(new TimerTask(){
//            @Override
//            public void run() {
//				updateMegMessage();
//            }      
//          }, 0, INTERVAL_PERIOD);

        
        // Create a RelativeLayout container that will hold a SurfaceView,
        // and set it as the content of our activity.
        mPreview = new Preview(this);
        setContentView(mPreview);

        // Find the total number of cameras available
        numberOfCameras = Camera.getNumberOfCameras();

        // Find the ID of the default camera
        CameraInfo cameraInfo = new CameraInfo();
            for (int i = 0; i < numberOfCameras; i++) {
                Camera.getCameraInfo(i, cameraInfo);
                if (cameraInfo.facing == CameraInfo.CAMERA_FACING_BACK) {
                    defaultCameraId = i;
                }
            }

/*        transButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
			}
		});*/
    }
    @Override
    protected void onResume() {
        super.onResume();

        // Open the default i.e. the first rear facing camera.
        // Nexus7の場合、背面カメラが無いから、cameraIdを指定しないとnilになる。
        mCamera = Camera.open(defaultCameraId);
        cameraCurrentlyLocked = defaultCameraId;
        mPreview.setCamera(mCamera);

    }
    @Override
    protected void onPause() {
        super.onPause();

        // Because the Camera object is a shared resource, it's very
        // important to release it when the activity is paused.
        if (mCamera != null) {
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
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
		// カメラ画像を取得
		mCamera.takePicture(null, null,new Camera.PictureCallback() {  
			   public void onPictureTaken(byte[] data,Camera camera) {  
				   updateMegMessage("通信中..");
					// CallableSWATPostでXML取得
					CallableSWATPost task = new CallableSWATPost(data);
					Future<InputStream> future = Executors.newSingleThreadExecutor().submit(task);
					InputStream xml = null;
						try {
							xml = future.get();
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				   
					// XMLパース
					String name = null;
					try {
						DocumentBuilderFactory document_builder_factory = DocumentBuilderFactory.newInstance();
						DocumentBuilder document_builder = document_builder_factory.newDocumentBuilder();
						Document document = document_builder.parse(xml);
						Element root = document.getDocumentElement();
						NodeList nodeList = root.getElementsByTagName("name");
						if( nodeList.getLength()>=1){
							name = nodeList.item(0).getNodeValue();
						}
					} catch (SAXException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (ParserConfigurationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 文字列をMEGに飛ばす
					if( name != null ){
						updateMegMessage(name);
					}else{
						updateMegMessage("");
					}
			   }  
		});  
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

	private void updateMegMessage(String message){
		if( message == null ){return;};
		//色定義
		final int RED 		= 0xffff0000;
		final int GREEN 	= 0xff00ff00;
		final int BLUE 		= 0xff0000ff;
		final int YELLOW 	= 0xffffff00;
		final int MAGENTA 	= 0xffff00ff;
		final int CYAN 		= 0xff00ffff;
		final int WHITE 	= 0xffffffff;
		final int GRAY      = 0xffcccccc;
		final int BLACK     = 0xff000000;

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
		int[] colors4 	= { WHITE, }; 
		String[] texts4 	= { message , };
		final int startX4 = 0;
		final int startY4 = 150;

		mMegGraphics.setClearColor(BLACK);
		mMegGraphics.clearScreen();
		//文字列登録
		mMegGraphics.registerText(0, true, sizes4, colors4, texts4);
		//スクロール設定、スクロール開始
		mMegGraphics.registerScroll(0, startX4, startY4, SPEED_MIDDLE, 50, 2000, 10);
		
		mMegGraphics.end();

	}
	
	private void updateMegMessage() {
		TransTask task = new TransTask();
		Future<String> future = Executors.newSingleThreadExecutor().submit(task);
			String tmpMessage=null;
			try {
				tmpMessage = future.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if(tmpMessage == null ){
				return;
			}
			if( tmpMessage.equalsIgnoreCase(transMessage)){
				return;
			}
			transMessage = tmpMessage;
				
			guiThread.post(new Runnable() {
				
				@Override
				public void run() {
					message.setText(transMessage);
				}
			});
			updateMegMessage(transMessage);
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



/**
 * A simple wrapper around a Camera and a SurfaceView that renders a centered preview of the Camera
 * to the surface. We need to center the SurfaceView because not all devices have cameras that
 * support preview sizes at the same aspect ratio as the device's display.
 */
class Preview extends ViewGroup implements SurfaceHolder.Callback {
    private final String TAG = "Preview";

    SurfaceView mSurfaceView;
    SurfaceHolder mHolder;
    Size mPreviewSize;
    List<Size> mSupportedPreviewSizes;
    Camera mCamera;

    Preview(Context context) {
        super(context);

        mSurfaceView = new SurfaceView(context);
        addView(mSurfaceView);

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = mSurfaceView.getHolder();
        mHolder.addCallback(this);
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mSupportedPreviewSizes = mCamera.getParameters().getSupportedPreviewSizes();
            requestLayout();
        }
    }

    public void switchCamera(Camera camera) {
       setCamera(camera);
       try {
           camera.setPreviewDisplay(mHolder);
       } catch (IOException exception) {
           Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
       }
       Camera.Parameters parameters = camera.getParameters();
       parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
       requestLayout();

       camera.setParameters(parameters);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // We purposely disregard child measurements because act as a
        // wrapper to a SurfaceView that centers the camera preview instead
        // of stretching it.
        final int width = resolveSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        final int height = resolveSize(getSuggestedMinimumHeight(), heightMeasureSpec);
        setMeasuredDimension(width, height);

        if (mSupportedPreviewSizes != null) {
            mPreviewSize = getOptimalPreviewSize(mSupportedPreviewSizes, width, height);
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (changed && getChildCount() > 0) {
            final View child = getChildAt(0);

            final int width = r - l;
            final int height = b - t;

            int previewWidth = width;
            int previewHeight = height;
            if (mPreviewSize != null) {
                previewWidth = mPreviewSize.width;
                previewHeight = mPreviewSize.height;
            }

            // Center the child SurfaceView within the parent.
            if (width * previewHeight > height * previewWidth) {
                final int scaledChildWidth = previewWidth * height / previewHeight;
                child.layout((width - scaledChildWidth) / 2, 0,
                        (width + scaledChildWidth) / 2, height);
            } else {
                final int scaledChildHeight = previewHeight * width / previewWidth;
                child.layout(0, (height - scaledChildHeight) / 2,
                        width, (height + scaledChildHeight) / 2);
            }
        }
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, acquire the camera and tell it where
        // to draw.
        try {
            if (mCamera != null) {
                mCamera.setPreviewDisplay(holder);
            }
        } catch (IOException exception) {
            Log.e(TAG, "IOException caused by setPreviewDisplay()", exception);
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        // Surface will be destroyed when we return, so stop the preview.
        if (mCamera != null) {
            mCamera.stopPreview();
        }
    }


    private Size getOptimalPreviewSize(List<Size> sizes, int w, int h) {
        final double ASPECT_TOLERANCE = 0.1;
        double targetRatio = (double) w / h;
        if (sizes == null) return null;

        Size optimalSize = null;
        double minDiff = Double.MAX_VALUE;

        int targetHeight = h;

        // Try to find an size match aspect ratio and size
        for (Size size : sizes) {
            double ratio = (double) size.width / size.height;
            if (Math.abs(ratio - targetRatio) > ASPECT_TOLERANCE) continue;
            if (Math.abs(size.height - targetHeight) < minDiff) {
                optimalSize = size;
                minDiff = Math.abs(size.height - targetHeight);
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE;
            for (Size size : sizes) {
                if (Math.abs(size.height - targetHeight) < minDiff) {
                    optimalSize = size;
                    minDiff = Math.abs(size.height - targetHeight);
                }
            }
        }
        return optimalSize;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // Now that the size is known, set up the camera parameters and begin
        // the preview.
        Camera.Parameters parameters = mCamera.getParameters();
        parameters.setPreviewSize(mPreviewSize.width, mPreviewSize.height);
        requestLayout();

        mCamera.setParameters(parameters);
        mCamera.startPreview();
    }

}

