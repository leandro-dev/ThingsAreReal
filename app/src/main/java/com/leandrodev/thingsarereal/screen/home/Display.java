package com.leandrodev.thingsarereal.screen.home;

import android.support.annotation.NonNull;

import com.google.android.things.pio.Gpio;
import com.google.android.things.pio.PeripheralManagerService;

import java.io.IOException;

/**
 * Created by Leandro on 23/02/2017.
 */

public class Display {
	private final Gpio pins[];

	public Display(@NonNull String[] pinNames) throws IOException {
		if (pinNames.length != 4) {
			throw new IllegalStateException("Error");
		}
		PeripheralManagerService service = new PeripheralManagerService();
		pins = new Gpio[4];
		for (int i = 0; i < 4; i++) {
			pins[i] = service.openGpio(pinNames[i]);
			pins[i].setDirection(Gpio.DIRECTION_OUT_INITIALLY_LOW);
		}
	}

	public void showNumber(int number) throws IOException {
		number = ((number%10)+10)%10;
		int[] directions = new int[4];
		for (int i = 0; i < 4; i++) {
			if ((number & (Math.round(Math.pow(2, i)))) != 0) {
				directions[i] = Gpio.DIRECTION_OUT_INITIALLY_HIGH;
			} else {
				directions[i] = Gpio.DIRECTION_OUT_INITIALLY_LOW;
			}
		}
		for (int i = 0; i < 4; i++) {
			pins[i].setDirection(directions[i]);
		}
	}

	public void dispose() {
		for (Gpio pin : pins) {
			if (pin != null) {
				try {
					pin.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
