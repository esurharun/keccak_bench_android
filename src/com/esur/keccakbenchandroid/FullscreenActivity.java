package com.esur.keccakbenchandroid;

import java.security.InvalidAlgorithmParameterException;
import java.util.Formatter;
import java.util.concurrent.TimeUnit;

import com.esur.keccakbenchandroid.util.SystemUiHider;
import com.uminho.Keccak;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 * 
 * @see SystemUiHider
 */
public class FullscreenActivity extends Activity {
	
	
	public static String KEY_TO_HASH = "brown.little.fox.jumped.over.the.bitch";
	
	static {
		System.loadLibrary("KHash");
	}
	
	public native String nativeSha3(String input);
	
	/**
	 * Whether or not the system UI should be auto-hidden after
	 * {@link #AUTO_HIDE_DELAY_MILLIS} milliseconds.
	 */
	private static final boolean AUTO_HIDE = true;

	/**
	 * If {@link #AUTO_HIDE} is set, the number of milliseconds to wait after
	 * user interaction before hiding the system UI.
	 */
	private static final int AUTO_HIDE_DELAY_MILLIS = 3000;

	/**
	 * If set, will toggle the system UI visibility upon interaction. Otherwise,
	 * will show the system UI visibility upon interaction.
	 */
	private static final boolean TOGGLE_ON_CLICK = true;

	/**
	 * The flags to pass to {@link SystemUiHider#getInstance}.
	 */
	private static final int HIDER_FLAGS = SystemUiHider.FLAG_HIDE_NAVIGATION;

	/**
	 * The instance of the {@link SystemUiHider} for this activity.
	 */
	private SystemUiHider mSystemUiHider;
	
	
	private class NativeKeccakTask extends AsyncTask<Void, Void, String> {

		
		
		@Override
		protected void onPreExecute() {
			EditText t = (EditText) findViewById(R.id.editText1);
			
			t.append("Running native benchmarks\n");
		}

		@Override
		protected void onPostExecute(String result) {

			EditText t = (EditText) findViewById(R.id.editText1);
			
			t.append(result+"\nFinished native benchmarks");
			//super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Void... params) {

			
			String res = "";
			
			long st = System.nanoTime();
			
			String it = nativeSha3(KEY_TO_HASH);
			
			for (int i = 0; i < 100; i++) {
				it = nativeSha3(it);
			}
				
			long en = System.nanoTime()-st;
				
			res = TimeUnit.MILLISECONDS.convert(en, TimeUnit.NANOSECONDS) + " ms : "+it;

			return res;
		}
	}

	private class JavaKeccakTask extends AsyncTask<Void, Void, String> {

		
		
		@Override
		protected void onPreExecute() {
			EditText t = (EditText) findViewById(R.id.editText1);
			
			t.append("Running java benchmarks\n");
		}

		@Override
		protected void onPostExecute(String result) {

			EditText t = (EditText) findViewById(R.id.editText1);
			
			t.append(result+"\nFinished java benchmarks\n");
			
			NativeKeccakTask nt = new NativeKeccakTask();
			nt.execute();
			//super.onPostExecute(result);
		}

		@Override
		protected String doInBackground(Void... params) {

			Keccak k;
			
			String res = "";
			
			long st = System.nanoTime();
			
			try {
				k = new Keccak(224);

				byte[] m = getByteArray(KEY_TO_HASH);

				k.update(m, 0, m.length);

				String it = getHexStringByByteArray(k.digest());

				//System.out.println("it: " + it);

				for (int i = 0; i < 100; i++) {
					k.reset();

					m = getByteArray(it);

					k.update(m, 0, m.length);

					it = getHexStringByByteArray(k.digest());

					// System.out.println("it: "+it);
				}
				
				long en = System.nanoTime()-st;
				
				res = TimeUnit.MILLISECONDS.convert(en, TimeUnit.NANOSECONDS) + " ms : "+it;

			} catch (InvalidAlgorithmParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return res;
		}

		public byte[] getByteArray(String s) {

			if (s == null)
				return null;

			return s.getBytes();

		}

		public String getHexStringByByteArray(byte[] array) {

			if (array == null)
				return null;

			StringBuilder stringBuilder = new StringBuilder(array.length * 2);
			@SuppressWarnings("resource")
			Formatter formatter = new Formatter(stringBuilder);

			for (byte tempByte : array)
				formatter.format("%02x", tempByte);

			return stringBuilder.toString();
		}

	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		// Trigger the initial hide() shortly after the activity has been
		// created, to briefly hint to the user that UI controls
		// are available.
		// delayedHide(100);

		// findViewById(R.id.editText1)

		EditText t = (EditText) findViewById(R.id.editText1);
		t.setFocusable(false);

		JavaKeccakTask jct = new JavaKeccakTask();
		jct.execute();
		
		
		//t.setText("Deneme\nabc\ndef\n");
	}

	/**
	 * Touch listener to use for in-layout UI controls to delay hiding the
	 * system UI. This is to prevent the jarring behavior of controls going away
	 * while interacting with activity UI.
	 */
	View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
		@Override
		public boolean onTouch(View view, MotionEvent motionEvent) {
			if (AUTO_HIDE) {
				delayedHide(AUTO_HIDE_DELAY_MILLIS);
			}
			return false;
		}
	};

	Handler mHideHandler = new Handler();
	Runnable mHideRunnable = new Runnable() {
		@Override
		public void run() {
			mSystemUiHider.hide();
		}
	};

	/**
	 * Schedules a call to hide() in [delay] milliseconds, canceling any
	 * previously scheduled calls.
	 */
	private void delayedHide(int delayMillis) {
		mHideHandler.removeCallbacks(mHideRunnable);
		mHideHandler.postDelayed(mHideRunnable, delayMillis);
	}
}
