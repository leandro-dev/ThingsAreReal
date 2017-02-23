package com.leandrodev.thingsarereal.screen.home;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.KeyEvent;

import com.google.android.things.contrib.driver.button.Button;
import com.google.android.things.contrib.driver.button.ButtonInputDriver;
import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;
import com.leandrodev.thingsarereal.R;
import com.leandrodev.thingsarereal.databinding.HomeScreenBinding;

import java.io.IOException;

/**
 * Created by Leandro on 21/02/2017.
 */

public class HomeActivity extends Activity {
	private static final String TAG = "ButtonActivity";
	private static final String GPIO_INPUT_PIN_NAME = "BCM21";
	private static final String GPIO_OUTPUT_PIN_NAME = "BCM6";

	private ButtonInputDriver mButtonInputDriver;
	private Display display;
	private HomeScreenBinding binding;
	private int clickCounter;


	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DataBindingUtil.setContentView(this, R.layout.home_screen);
		PeripheralManagerService service = new PeripheralManagerService();
		try {
			mButtonInputDriver = new ButtonInputDriver(GPIO_INPUT_PIN_NAME,
					Button.LogicState.PRESSED_WHEN_LOW, KeyEvent.KEYCODE_SPACE);
		} catch (IOException e) {
			Log.e(TAG, "Error on PeripheralIO API", e);
		}



		try {
			final String[] pinNames = new String[]{"BCM16", "BCM26", "BCM20", "BCM19"};
			display = new Display(pinNames);
		} catch (IOException e) {
			Log.e(TAG, "Error on PeripheralIO API", e);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		mButtonInputDriver.register();
	}

	@Override
	protected void onStop() {
		super.onStop();
		mButtonInputDriver.unregister();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

		// Step 6. Close the resource
		if (mButtonInputDriver != null) {
			try {
				mButtonInputDriver.close();
			} catch (IOException e) {
				Log.e(TAG, "Error on PeripheralIO API", e);
			}
		}

		if (display != null) {
			display.dispose();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_SPACE:
				return handleButtonClickEvent();
			default:
				return super.onKeyDown(keyCode, event);
		}
	}

	private boolean handleButtonClickEvent() {
		Log.i(TAG, "GPIO changed, button pressed");

		clickCounter++;
		binding.text.setText(String.format("The button was clicked %d times", clickCounter));

		try {
			display.showNumber(clickCounter);
		} catch (IOException e) {
			Log.e(TAG, "Error on PeripheralIO API", e);
		}
		// Step 5. Return true to keep callback active.
		return true;
	}
}
